package validate;

import java.util.regex.Matcher;

import consts.Constants;

public class InputValidator {

    public static boolean validatePasword(String password) {
        Matcher pwMatcher = Constants.PW_PATTERN.matcher(password);

        return pwMatcher.matches();
    }

    public static boolean validateFullName(String fullName) {
        Matcher fnMatcher = Constants.FN_PATTERN.matcher(fullName);

        return fnMatcher.matches();
    }

    public static boolean validateUserName(String userName) {
        Matcher unMatcher = Constants.UN_PATTERN.matcher(userName);

        return unMatcher.matches();
    }

}
