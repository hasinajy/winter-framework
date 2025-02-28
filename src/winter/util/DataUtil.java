package winter.util;

import java.lang.reflect.AnnotatedElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import winter.data.annotation.http.RequestParam;
import winter.data.enumdata.RequestParamType;
import winter.exception.InvalidFormDataException;

public class DataUtil extends Utility {
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
    public static void validateRequestParamConstraints(AnnotatedElement element, String value)
            throws InvalidFormDataException {

        RequestParam requestParam = element.getAnnotation(RequestParam.class);

        if (requestParam == null) {
            return;
        }

        RequestParamType type = requestParam.type();

        if (type == RequestParamType.EMAIL && !isEmail(value)) {
            throw new InvalidFormDataException("Invalid email format");
        } else if (type == RequestParamType.NUMERIC && !isNumeric(value)) {
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
                clazz == Double.class ||
                clazz == String.class;
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
