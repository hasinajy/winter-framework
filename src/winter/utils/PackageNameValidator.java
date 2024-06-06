package winter.utils;

import java.util.regex.Pattern;

public class PackageNameValidator extends Utility {
    private static final String PACKAGE_NAME_REGEX = "^(\\w+)(\\.(\\w+))*$";
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile(PACKAGE_NAME_REGEX);

    public static boolean isValidPackageName(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        return PACKAGE_NAME_PATTERN.matcher(packageName).matches();
    }
}
