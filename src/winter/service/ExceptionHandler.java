package winter.service;

import jakarta.servlet.http.HttpServletResponse;
import winter.data.exception.client.InvalidRequestVerbException;
import winter.data.exception.client.MappingNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling and responding to exceptions in the Winter
 * framework.
 * <p>
 * This class logs exceptions using a {@link Logger} and sends formatted HTML
 * error responses
 * to the client via {@link HttpServletResponse}. It maps specific exceptions to
 * appropriate
 * HTTP status codes and provides user-friendly error pages styled with Tailwind
 * CSS.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionHandler {

    /** Logger instance for recording exception details. */
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    /**
     * Handles an exception by logging it and sending an error response to the
     * client.
     * <p>
     * Logs the exception at the specified {@link Level} and, if the response is not
     * yet committed,
     * sends an HTML error page with a status code and message based on the
     * exception type.
     * </p>
     *
     * @param e     the exception to handle
     * @param level the logging level (e.g., {@link Level#SEVERE})
     * @param resp  the HTTP response to send the error to
     * @throws IOException if an error occurs while writing the response
     */
    public void handleException(Exception e, Level level, HttpServletResponse resp) {
        logger.log(level, e.getMessage(), e);

        try {
            if (!resp.isCommitted()) {
                if (e instanceof MappingNotFoundException) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                } else if (e instanceof InvalidRequestVerbException) {
                    sendError(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
                } else if (e instanceof IllegalAccessException) {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                } else {
                    sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Error sending error response to client", ioException);
        }
    }

    /**
     * Handles initialization exceptions by delegating to the general exception
     * handler.
     * <p>
     * If an initialization exception is provided, it is logged and processed as a
     * severe error.
     * </p>
     *
     * @param resp          the HTTP response to send the error to
     * @param initException the initialization exception to handle, or null if none
     */
    public void handleInitException(HttpServletResponse resp, Exception initException) {
        if (initException != null) {
            handleException(initException, Level.SEVERE, resp);
        }
    }

    /**
     * Sends an HTML error response with a specific status code and message.
     * <p>
     * Constructs a styled HTML error page using Tailwind CSS, including a title and
     * details
     * based on the status code, and writes it to the response.
     * </p>
     *
     * @param resp    the HTTP response to send the error to
     * @param status  the HTTP status code (e.g., 404, 500)
     * @param message the error message to display
     * @throws IOException if an error occurs while writing the response
     */
    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("text/html");
        resp.setStatus(status);

        try (PrintWriter out = resp.getWriter()) {
            String html = String.format("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Error %d</title>
                        <script src="https://cdn.tailwindcss.com"></script>
                    </head>
                    <body class="bg-gray-100 h-screen flex items-center justify-center">
                        <div class="bg-white p-8 rounded shadow-md text-center">
                            <h1 class="text-4xl font-bold mb-4">
                                %s
                            </h1>
                            <p class="text-gray-600 mb-6">%s</p>
                            %s
                        </div>
                    </body>
                    </html>
                    """, status, getErrorTitle(status), message, getErrorDetails(status));
            out.println(html);
        }
    }

    /**
     * Returns a user-friendly title for the given HTTP status code.
     *
     * @param status the HTTP status code
     * @return a descriptive title (e.g., "404 - Page Not Found")
     */
    private String getErrorTitle(int status) {
        return switch (status) {
            case 404 -> "404 - Page Not Found";
            case 405 -> "405 - Method Not Allowed";
            case 403 -> "403 - Forbidden";
            case 500 -> "500 - Internal Server Error";
            default -> status + " - Error";
        };
    }

    /**
     * Returns additional details for the given HTTP status code.
     *
     * @param status the HTTP status code
     * @return a descriptive message, or an empty string if no details are defined
     */
    private String getErrorDetails(int status) {
        return switch (status) {
            case 404 ->
                "<p class=\"text-gray-600\">The page you're looking for might have been removed or is temporarily unavailable.</p>";
            case 405 ->
                "<p class=\"text-gray-600\">The requested method is not allowed for this resource.</p>";
            case 403 ->
                "<p class=\"text-gray-600\">You don't have permission to access this resource.</p>";
            case 500 ->
                "<p class=\"text-gray-600\">An unexpected error occurred on our server. We're working on it!</p>";
            default -> "";
        };
    }
}