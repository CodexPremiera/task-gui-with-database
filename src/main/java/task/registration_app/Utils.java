package task.registration_app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isValidEmail(String email) {
        // regular expression for validating email addresses
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        // return true if the email matches the pattern, false otherwise
        return matcher.matches();
    }
}
