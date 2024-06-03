package winter.utils;

public class UrlUtil extends Utility {

    public static String extractTargetURL(String requestURL) {
        String[] splitRequest = requestURL.split("/");
        return splitRequest[splitRequest.length - 1];
    }

}
