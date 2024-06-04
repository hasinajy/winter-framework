package winter.utils;

import java.io.PrintWriter;

public class HtmlElementBuilder extends Utility {
    /* ------------------------- Project related methods ------------------------ */
    public static void printRequestInfo(PrintWriter out, String requestURL) {
        HtmlElementBuilder.printList(out, "URL Information", new String[] { "Request URL" }, new String[] { requestURL });
    }

    public static void printTargetControllerInfo(PrintWriter out, String targetURL, String className, String methodName,
            String returnValue) {
        HtmlElementBuilder.printList(out, "Controller Information",
                new String[] { "Target Mapping", "Controller", "Method", "Returned Value" },
                new String[] { targetURL, className, methodName, returnValue });
    }

    /* --------------------------- Generalized methods -------------------------- */
    public static void printList(PrintWriter out, String title, String[] labels, String[] values)
            throws IllegalArgumentException {
        if (out == null || labels == null || values == null || labels.length != values.length) {
            throw new IllegalArgumentException(
                    "Invalid arguments: out, labels, and values must be valid and have the same length");
        }

        out.print(makeHeading(3, title));
        out.println("<ul>");

        for (int i = 0; i < labels.length; i++) {
            out.print("<li>");
            out.print(makeBold(labels[i] + ": "));
            out.println(values[i]);
            out.println("</li>");
        }

        out.println("</ul>"); // End the unordered list
    }

    public static void printError(PrintWriter out, String errMsg) {
        out.print(makeParagraph(makeBold(">>>> ") + errMsg));
    }

    public static void printError(PrintWriter out, Exception e) {
        printError(out, makeBold("EXCEPTION LOG"));

        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = e.getStackTrace();

        stringBuilder.append(e.toString());

        for (StackTraceElement stackTraceElement: stackTraceElements) {
            stringBuilder.append("\n\tat ");
            stringBuilder.append(stackTraceElement.toString());
        }

        out.print(makePre(stringBuilder.toString()));
    }

    private static String makeParagraph(String text) {
        return "<p>" + text + "</p>";
    }

    private static String makeHeading(int level, String text) throws IllegalArgumentException {
        if (level <= 0 || level >= 7 || text == null) {
            throw new IllegalArgumentException("Invalid heading level or text");
        }

        switch (level) {
            case 1:
                return "<h1>" + text + "</h1>";
            case 2:
                return "<h2>" + text + "</h2>";
            case 3:
                return "<h3>" + text + "</h3>";
            case 4:
                return "<h4>" + text + "</h4>";
            case 5:
                return "<h5>" + text + "</h5>";
            case 6:
                return "<h6>" + text + "</h6>";
            default:
                return null;
        }
    }

    private static String makeBold(String text) {
        return "<b>" + text + "</b>";
    }

    private static String makePre(String text) {
        return "<pre>" + text + "</pre>";
    }
}
