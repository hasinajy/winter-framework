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

/**
 * Utility class providing data-related helper methods for the Winter framework.
 * <p>
 * This class extends {@link Utility} and offers static methods for parsing
 * objects,
 * validating request parameters, generating timestamps, and extracting URI
 * mappings.
 * As a utility class, it cannot be instantiated, and all functionality is
 * accessed statically.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataUtil extends Utility {

    /* ----------------------------- Parsing methods ---------------------------- */

    /**
     * Parses a string value into an object of the specified type.
     * <p>
     * Supports primitive types and their wrappers (e.g., int, Integer, double,
     * Double).
     * Returns the original string if the type is not explicitly handled.
     * </p>
     *
     * @param objType the target class type to parse the value into
     * @param value   the string value to parse
     * @return the parsed object, or null if the value is null
     * @throws NumberFormatException if the value cannot be parsed into a numeric
     *                               type
     */
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

    /**
     * Validates constraints on a request parameter based on its annotation.
     * <p>
     * Checks the {@link RequestParam} annotation on the provided element and
     * enforces
     * its {@link RequestParamType} constraints (e.g., email or numeric format).
     * </p>
     *
     * @param element the annotated element (e.g., method parameter or field)
     * @param value   the value to validate
     * @throws InvalidFormDataException if the value violates the parameter's type
     *                                  constraints
     */
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

    /**
     * Checks if a class represents a primitive type or its wrapper.
     *
     * @param clazz the class to check
     * @return true if the class is a primitive or wrapper type, false otherwise
     */
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

    /**
     * Validates if a string is a well-formed email address.
     *
     * @param email the string to check
     * @return true if the string matches an email pattern, false otherwise
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * Checks if a class represents a numeric type (primitive or wrapper).
     *
     * @param objType the class to check
     * @return true if the class is a numeric type, false otherwise
     */
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

    /**
     * Determines if a string represents a valid numeric value.
     *
     * @param value the string to check
     * @return true if the string can be parsed as a number, false otherwise
     */
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

    /**
     * Validates if a string is a well-formed Java package name.
     *
     * @param packageName the package name to check
     * @return true if the package name is valid, false otherwise
     */
    public static boolean isValidPackageName(String packageName) {
        String packageNameRegex = "^(\\w+)(\\.(\\w++))*+$";
        Pattern pattern = Pattern.compile(packageNameRegex);

        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        return pattern.matcher(packageName).matches();
    }

    /* --------------------------- Generation methods --------------------------- */

    /**
     * Generates a timestamped filename based on the current UTC time.
     * <p>
     * If an original filename is provided, the extension is preserved; otherwise,
     * a default ".bin" extension is used.
     * </p>
     *
     * @param originalFilename the original filename (optional)
     * @return a new filename in the format "file_yyyyMMdd_HHmmssSSS.extension"
     */
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

    /**
     * Constructs a full URL mapping from class and method annotations.
     * <p>
     * Combines the {@link UrlMapping} values from the class and method, if present.
     * </p>
     *
     * @param clazz  the controller class
     * @param method the method within the class
     * @return the concatenated URL mapping string
     */
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

    /**
     * Retrieves the setter method for a given attribute in a class.
     *
     * @param objType  the class containing the attribute
     * @param attrName the name of the attribute
     * @return the setter method for the attribute
     * @throws NoSuchFieldException  if the attribute does not exist
     * @throws NoSuchMethodException if the setter method does not exist
     */
    public static Method getSetterMethod(Class<?> objType, String attrName)
            throws NoSuchFieldException, NoSuchMethodException {
        String setterName = getSetterName(attrName);
        Field field = objType.getDeclaredField(attrName);
        Class<?> attrType = field.getType();
        return objType.getDeclaredMethod(setterName, attrType);
    }

    /**
     * Generates the setter method name for a given attribute name.
     *
     * @param attrName the name of the attribute
     * @return the setter method name (e.g., "setAttrName")
     */
    public static String getSetterName(String attrName) {
        return "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
    }

    /* --------------------------- Extraction methods --------------------------- */

    /**
     * Extracts the URI mapping from an HTTP request.
     * <p>
     * Strips the context path from the request URI to obtain the mapping path.
     * </p>
     *
     * @param req the HTTP servlet request
     * @return the URI mapping relative to the context path
     */
    public static String extractURIMapping(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }
}