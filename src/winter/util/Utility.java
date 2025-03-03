package winter.util;

/**
 * A utility class providing static helper methods for the Winter framework.
 * <p>
 * This class is not intended to be instantiated. All functionality is provided
 * through static methods. Attempting to create an instance will result in an
 * {@link IllegalStateException}.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class Utility {

    /**
     * Prevents instantiation of this utility class.
     * <p>
     * This constructor is protected to enforce the non-instantiable nature of
     * the class. Subclasses are also discouraged from instantiation.
     * </p>
     *
     * @throws IllegalStateException if an attempt is made to instantiate this class
     */
    protected Utility() throws IllegalStateException {
        throw new IllegalStateException("Cannot instantiate a utility class.");
    }
}