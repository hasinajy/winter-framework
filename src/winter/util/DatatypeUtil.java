package winter.util;

import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import winter.annotation.methodlevel.RequestParam;
import winter.data.enumdata.RequestParamType;
import winter.exception.InvalidFormDataException;

public class DatatypeUtil extends Utility {
    public static Object parseObject(Class<?> objType, String value) {
        if (value == null) {
            return null;
        } else if (objType == int.class || objType == Integer.class) {
            return Integer.parseInt(value);
        } else if (objType == double.class || objType == Double.class) {
            return Double.parseDouble(value);
        } else if (objType == float.class || objType == Float.class) {
            return Float.parseFloat(value);
        } else {
            return value;
        }
    }

    /* --------------------------- Validation methods --------------------------- */
    public static void validateRequestParamConstraints(Parameter param, String value) throws InvalidFormDataException {
        boolean isEmail = param.getAnnotation(RequestParam.class).type().equals(RequestParamType.EMAIL);
        boolean isNumeric = param.getAnnotation(RequestParam.class).type().equals(RequestParamType.NUMERIC);

        if (isEmail && !isEmail(value)) {
            throw new InvalidFormDataException("Invalid email format");
        } else if (isNumeric && !isNumeric(value)) {
            throw new InvalidFormDataException("Invalid numeric format");
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class;
    }

    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }

    public static boolean isNumeric(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            new java.math.BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
