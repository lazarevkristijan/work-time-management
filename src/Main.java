
import auth.AdminView;
import auth.LoginPage;
import auth.UserView;
import state.AppState;
import auth.SessionManager;

public class Main {

    public static void main(String args[]) {
        AppState state = AppState.getInstance();
        try {
            if (SessionManager.validateSession()) {
                String userRole = state.getRole();
                if (userRole.equals("admin")) {
                    new AdminView();
                } else if (userRole.equals("agent")) {
                    new UserView();
                }
            } else {
                new LoginPage();
            }
        } catch (Exception e) {
            new LoginPage();
        }
    }
}
