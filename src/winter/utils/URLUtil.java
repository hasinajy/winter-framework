package winter.utils;

import jakarta.servlet.http.HttpServletRequest;

public class UrlUtil extends Utility {
    public static String extractTargetURL(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }

    public static String extractLastURL(HttpServletRequest req) {
        String[] splitRequest = req.getRequestURL().toString().split("/");
        return splitRequest[splitRequest.length - 1];
    }
}
