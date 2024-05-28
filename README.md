## Spring Clone - Winter Project

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
}
```

**Current Functionality:**

* The `FrontController` class (located in `src/winter/controllers/FrontController.java`) captures the user's requested URL and displayes it in the browser. It also shows the details of the associated controller for that URL.
* The `@Controller` annotation is used to annotate all classes that the developer wants to be scanned as a controller.
* The `@GetMapping` annotation is used to map methods to specific URLs.

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
