package winter.util;

import jakarta.servlet.http.HttpServletResponse;
import winter.exception.MappingNotFoundException;
import winter.util.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler extends Utility {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    public static void handleException(Exception e, Level level, HttpServletResponse resp) {
        logger.log(level, e.getMessage(), e);

        try {
            if (!resp.isCommitted()) {
                if (e instanceof MappingNotFoundException) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                } else {
                    sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Error sending error response to client", ioException);
        }
    }

    public static void handleInitException(HttpServletResponse resp, Exception initException) {
        if (initException != null) {
            handleException(initException, Level.SEVERE, resp);
        }
    }

    private static void sendError(HttpServletResponse resp, int status, String message) throws IOException {
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

    private static String getErrorTitle(int status) {
        return switch (status) {
            case 404 -> "404 - Page Not Found";
            case 500 -> "500 - Internal Server Error";
            default -> status + " - Error";
        };
    }

    private static String getErrorDetails(int status) {
        return switch (status) {
            case 404 ->
                "<p class=\"text-gray-600\">The page you're looking for might have been removed or is temporarily unavailable.</p>";
            case 500 ->
                "<p class=\"text-gray-600\">An unexpected error occurred on our server. We're working on it!</p>";
            default -> "";
        };
    }
}
