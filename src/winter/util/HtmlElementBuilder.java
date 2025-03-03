package winter.util;

import java.io.PrintWriter;

/**
 * Utility class for building HTML elements in the Winter framework.
 * <p>
 * This class extends {@link Utility} and provides static methods to generate
 * HTML output
 * for displaying request information, controller details, and error messages.
 * It is not
 * intended to be instantiated, and all functionality is accessed statically.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class HtmlElementBuilder extends Utility {

    /* ------------------------- Project related methods ------------------------ */

    /**
     * Prints request URL information as an HTML list.
     * <p>
     * Generates an HTML unordered list with the request URL under a heading titled
     * "URL Information".
     * </p>
     *
     * @param out        the PrintWriter to output the HTML
     * @param requestURL the URL of the request to display
     * @throws IllegalArgumentException if out or requestURL is null
     * @see #printList(PrintWriter, String, String[], String[])
     */
    public static void printRequestInfo(PrintWriter out, String requestURL) {
        HtmlElementBuilder.printList(out, "URL Information", new String[] { "Request URL" },
                new String[] { requestURL });
    }

    /**
     * Prints controller information as an HTML list.
     * <p>
     * Generates an HTML unordered list with details about the target mapping,
     * controller
     * class, method, and return value under a heading titled "Controller
     * Information".
     * </p>
     *
     * @param out         the PrintWriter to output the HTML
     * @param targetURL   the target mapping URL
     * @param className   the name of the controller class
     * @param methodName  the name of the method handling the request
     * @param returnValue the value returned by the method
     * @throws IllegalArgumentException if out or any parameter is null
     * @see #printList(PrintWriter, String, String[], String[])
     */
    public static void printTargetControllerInfo(PrintWriter out, String targetURL, String className, String methodName,
            String returnValue) {
        HtmlElementBuilder.printList(out, "Controller Information",
                new String[] { "Target Mapping", "Controller", "Method", "Returned Value" },
                new String[] { targetURL, className, methodName, returnValue });
    }

    /* --------------------------- Generalized methods -------------------------- */

    /**
     * Prints a labeled list as HTML.
     * <p>
     * Generates an HTML unordered list (&lt;ul&gt;) with a heading and paired
     * labels and
     * values. The number of labels must match the number of values.
     * </p>
     *
     * @param out    the PrintWriter to output the HTML
     * @param title  the heading title for the list
     * @param labels the array of labels to display
     * @param values the array of corresponding values
     * @throws IllegalArgumentException if out, labels, or values is null, or if
     *                                  labels and values have different lengths
     */
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

    /**
     * Prints an error message as an HTML paragraph.
     * <p>
     * Outputs the error message in a paragraph with a bold ">>>>" prefix.
     * </p>
     *
     * @param out    the PrintWriter to output the HTML
     * @param errMsg the error message to display
     * @throws IllegalArgumentException if out or errMsg is null
     */
    public static void printError(PrintWriter out, String errMsg) {
        out.print(makeParagraph(makeBold(">>>> ") + errMsg));
    }

    /**
     * Prints an exception's details as HTML.
     * <p>
     * Outputs a bold "EXCEPTION LOG" heading followed by the exception's message
     * and
     * stack trace in a preformatted block.
     * </p>
     *
     * @param out the PrintWriter to output the HTML
     * @param e   the exception to display
     * @throws IllegalArgumentException if out or e is null
     */
    public static void printError(PrintWriter out, Exception e) {
        printError(out, makeBold("EXCEPTION LOG"));

        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = e.getStackTrace();

        stringBuilder.append(e.toString());

        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append("\n\tat ");
            stringBuilder.append(stackTraceElement.toString());
        }

        out.print(makePre(stringBuilder.toString()));
    }

    /**
     * Wraps text in an HTML paragraph tag.
     *
     * @param text the text to wrap
     * @return the text enclosed in &lt;p&gt; tags
     * @throws IllegalArgumentException if text is null
     */
    private static String makeParagraph(String text) {
        return "<p>" + text + "</p>";
    }

    /**
     * Creates an HTML heading of the specified level.
     *
     * @param level the heading level (1 to 6)
     * @param text  the heading text
     * @return the text enclosed in the appropriate heading tag (e.g., &lt;h3&gt;)
     * @throws IllegalArgumentException if level is not between 1 and 6, or if text
     *                                  is null
     */
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

    /**
     * Wraps text in HTML bold tags.
     *
     * @param text the text to bold
     * @return the text enclosed in &lt;b&gt; tags
     * @throws IllegalArgumentException if text is null
     */
    private static String makeBold(String text) {
        return "<b>" + text + "</b>";
    }

    /**
     * Wraps text in HTML preformatted tags.
     *
     * @param text the text to preformat
     * @return the text enclosed in &lt;pre&gt; tags
     * @throws IllegalArgumentException if text is null
     */
    private static String makePre(String text) {
        return "<pre>" + text + "</pre>";
    }
}