package auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Date;

import components.WTOptionPane;
import consts.Constants;
import state.AppState;
import utils.DatabaseUtils;

public class SessionManager {

    private static String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);

        return Base64.getEncoder().encodeToString(tokenBytes);
    }

    public static String[] createSession(int id) throws IOException {
        String token = generateSessionToken();
        long expirationTime = new Date().getTime() + (Constants.SESSION_VALIDITY_DAYS * 24 * 60 * 60 * 1000L);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(Constants.SESSION_FILE))) {
            writer.write(token + '\n');
            writer.write(Integer.toString(id) + '\n');
            writer.write(Long.toString(expirationTime));
        }

        return new String[] { token, Long.toString(expirationTime) };
    }

    public static boolean validateSession() throws IOException {
        AppState state = AppState.getInstance();

        if (!Files.exists(Paths.get(Constants.SESSION_FILE))) {
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(Constants.SESSION_FILE))) {
            String fileToken = reader.readLine();
            String fileId = reader.readLine();
            long fileExpTime = Long.parseLong(reader.readLine());

            Connection con = DatabaseUtils.getConnection();
            PreparedStatement sessionPSTMT = con.prepareStatement("SELECT * FROM sessions WHERE user_id = ?");
            sessionPSTMT.setString(1, fileId);
            ResultSet sessionData = sessionPSTMT.executeQuery();

            if (!sessionData.isBeforeFirst()) {
                return false;
            }

            sessionData.next();

            int dbUserId = sessionData.getInt(2);
            String dbToken = sessionData.getString(3);
            long dbExpTime = sessionData.getLong(4);

            if (!Integer.toString(dbUserId).equals(fileId) || !dbToken.equals(fileToken) || dbExpTime != fileExpTime) {
                return false;
            }

            if (new Date().getTime() > fileExpTime) {
                return false;
            }

            PreparedStatement userPSTMT = con.prepareStatement("SELECT * FROM users WHERE id = ?");
            userPSTMT.setInt(1, dbUserId);
            ResultSet userSet = userPSTMT.executeQuery();

            userSet.next();

            state.setUserId(userSet.getInt(1));
            state.setUserName(userSet.getString(2));
            state.setFullName(userSet.getString(4));
            state.setRole(userSet.getString(5));

            con.close();
            return true;
        } catch (Exception err) {
            WTOptionPane.showMessageBox("Error in session manager" + err);
            return false;
        }
    }

    public static void invalidateSession() throws IOException {
        Files.deleteIfExists(Paths.get(Constants.SESSION_FILE));
    }
}
