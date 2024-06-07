package winter.utils;

import jakarta.servlet.http.HttpServletResponse;
import winter.exceptions.MappingNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler extends Utility {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    public static void handleException(Exception e, Level level, HttpServletResponse resp) {
        logger.log(level, e.getMessage(), e);

        try {
            if (!resp.isCommitted()) {
                if (e instanceof MappingNotFoundException) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Error sending error response to client", ioException);
        }
    }

    public static void handleInitExceptions(HttpServletResponse resp, List<Exception> initExceptions) {
        for (Exception e : initExceptions) {
            handleException(e, Level.SEVERE, resp);
        }
    }
}
