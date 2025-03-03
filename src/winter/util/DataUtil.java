package winter.util;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import winter.data.annotation.http.RequestParam;
import winter.data.annotation.http.UrlMapping;
import winter.data.enumdata.RequestParamType;
import winter.data.exception.client.InvalidFormDataException;

public class DataUtil extends Utility {
    /* ----------------------------- Parsing methods ---------------------------- */
    public static Object parseObject(Class<?> objType, String value) {
        if (isNumeric(objType) && !isNumeric(value)) {
            throw new NumberFormatException("Invalid numeric format");
        }

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
    public static void validateRequestParamConstraints(AnnotatedElement element, Object value)
            throws InvalidFormDataException {

        RequestParam requestParam = element.getAnnotation(RequestParam.class);

        if (requestParam == null) {
            return;
        }

        RequestParamType type = requestParam.type();

        if (type == RequestParamType.EMAIL && !isEmail((String) value)) {
            throw new InvalidFormDataException("Invalid email format");
        } else if (type == RequestParamType.NUMERIC && !isNumeric((String) value)) {
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

    public static boolean isNumeric(Class<?> objType) {
        if (objType == null) {
            return false;
        }

        return objType.isPrimitive() ||
                objType == Boolean.class ||
                objType == Character.class ||
                objType == Byte.class ||
                objType == Short.class ||
                objType == Integer.class ||
                objType == Long.class ||
                objType == Float.class ||
                objType == Double.class;
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

    public static boolean isValidPackageName(String packageName) {
        String packageNameRegex = "^(\\w+)(\\.(\\w+))*$";
        Pattern pattern = Pattern.compile(packageNameRegex);

        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        return pattern.matcher(packageName).matches();
    }

    /* --------------------------- Generation methods --------------------------- */
    public static String generateTimestampFilename(String originalFilename) {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS").withZone(ZoneOffset.UTC);
        String timestamp = formatter.format(now);

        if (originalFilename != null && !originalFilename.isEmpty()) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex != -1) {
                String extension = originalFilename.substring(lastDotIndex);
                return "file_" + timestamp + extension;
            }
        }

        return "file_" + timestamp + ".bin"; // Default to .bin if no extension
    }

    public static String getUrlMapping(Class<?> clazz, Method method) {
        String urlString = "";
        UrlMapping classAnnotation = clazz.getAnnotation(UrlMapping.class);
        UrlMapping methodAnnotation = method.getAnnotation(UrlMapping.class);

        if (classAnnotation != null) {
            urlString += classAnnotation.value();
        }

        if (methodAnnotation != null) {
            urlString += methodAnnotation.value();
        }

        return urlString;
    }

    public static Method getSetterMethod(Class<?> objType, String attrName)
            throws NoSuchFieldException, NoSuchMethodException {
        String setterName = getSetterName(attrName);
        Field field = objType.getDeclaredField(attrName);
        Class<?> attrType = field.getType();
        return objType.getDeclaredMethod(setterName, attrType);
    }

    public static String getSetterName(String attrName) {
        return "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
    }

    /* --------------------------- Extraction methods --------------------------- */
    public static String extractURIMapping(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }
}
