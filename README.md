## Spring MVC Clone - Winter Project

This project is a work-in-progress clone of the Spring MVC framework written in Java. It's not a full replica, but focuses on implementing key features for educational purposes.

We welcome contributions! If you'd like to add a specific feature, please refer to the `Contributing` section for details.

**Directory Structure:**

* **src/**: Contains the project's source code.
* **packaging/**: Houses scripts for converting the project into a JAR file for deployment.
    * **package.sh**: Shell script for Linux/macOS users.
    * **package.bat**: Batch script for Windows users.

**Requirements**

The project uses the Jakarta EE APIs (formerly javax.ee). Ensure you have Jakarta EE 10 (or the required version) installed.

**Building and Running the Project:**

The project currently does not have a pre-configured build system. To manually build and run the project:

1. Compile the source code using a Java compiler. If you are using *VS Code*, the compilation directory will be `bin`. If the compilation directory is elsewhere, please adjust the script accordingly.
2. Use the appropriate script in the `packaging/` directory to create a JAR file:
    * Linux/macOS: Run `./packaging/package.sh`
    * Windows: Double-click `packaging/package.bat`

The script will create a JAR file named `winter.jar`. Add the file to your project's libraries and it will be ready to use for your web application.

**How to Use**

Add the following XML configuration to your web application's `web.xml` file:
* **FrontController:** This maps to the controller handling every URL.
* **ControllersPackage:** This specifies the path where developer-created controller classes with the `@Controller` annotation reside.
```XML
    <!-- Front controller mapping -->
    <servlet>
        <servlet-name>FrontController</servlet-name>
        <servlet-class>winter.controllers.FrontController</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>FrontController</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- Map the path to the package to scan for controllers -->
    <context-param>
        <param-name>ControllersPackage</param-name>
        <param-value>com.controllers</param-value>
    </context-param>
```
Every class that the developer wants to be scanned as controller should be annotated as `@Controller`
```java
import winter.annotations.Controller;

@Controller
public class ExampleController {
    // Code goes here ...
}
```
Every method that the developer wants to be mapped to a URL should be annotated as `@GetMapping("mapping")`
```java
import winter.annotations.Controller;

@Controller
public class ExampleController {
    @GetMapping("test-mapping")
    public void exampleMethod() {
        // Code goes here ...
    }

    // If the method has arguments
    @GetMapping("test-mapping")
    public void exampleMethod(@RequestParam(name = "name") String name, ...) {
        // Code goes here...
    }
}
```

**Current Functionalities:**

* The `FrontController` class (located in `src/winter/controllers/FrontController.java`) intercepts incoming URLs, identifies the requested one, and displays it in the browser. It also provides details about the associated controller for that specific URL.
* When the invoked method within a controller returns a `ModelView` object, the `FrontController` forwards the request to the corresponding JSP page.
* `Error 500` is sent to the client when one of the following issues occurs:
  * Package provider not found in the configuration file.
  * Provided package name is invalid.
  * Multiple methods have the same `@GetMapping` value.
  * Mapped method return value is neither `String` nor `ModelView`.
  * Critical error.
* `Error 404` is sent to the client when the requested URL doesn't match any `@GetMapping`.
* The `@Controller` annotation is used to annotate all classes that are wanted to be scanned as a controller.
* The `@GetMapping` annotation is used to map controller methods to specific URLs, allowing for handling GET requests.
* The `Mapping` data structure stores information about a URL and its associated controller.
* The `ModelView` data structure holds information about the target JSP and any attributes (data) that need to be passed along to it for rendering.
* The `@RequestParam(name = "value")` annotation is used to annotate parameters to be mapped to a form attribute.
* If the parameter is an object, the form attribute value should match the pattern `objectName.attributeName` where objectName is the value of the `@RequestParam` annotation or the declared name of the parameter if no annotation is provided.

**Future Work:**

This project is currently under development and only implements a basic feature. More functionalities replicating Spring's capabilities will be added in the future.

**Contributing:**

We welcome your contributions! Here's how you can help:

* Implementing additional features based on the Spring framework.
* Improving the existing code.
* Adding documentation and comments.

**License:**

This project is licensed under the MIT License. This license grants you permission to freely use, modify, and distribute this software under certain conditions. Please refer to the [LICENSE](./LICENSE.md) file for more details.

**Author:**

Hasina JY
