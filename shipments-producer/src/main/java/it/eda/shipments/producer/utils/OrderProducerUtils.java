package it.eda.shipments.producer.utils;

import java.util.Objects;
import java.util.regex.Pattern;

public class OrderProducerUtils {

    private OrderProducerUtils() {
    }


    public static boolean isFieldMissing(Object field) {
        return field == null || (field instanceof String && ((String) field).isEmpty());
    }

    public static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    public static String stringValue(Object o) {
        return Objects.isNull(o) ? null : o.toString();
    }

    public static boolean checkIfCapIsValid(String cap) {
        return cap != null && cap.matches("^[0-9]{5}$");
    }

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
            + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
}
