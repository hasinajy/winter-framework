# Winter Framework Technical Documentation

**Version**: 1.0.0  
**Author**: Hasina JY

## Overview

Winter is a work-in-progress clone of the Spring MVC framework written in Java, designed for educational purposes. It implements key MVC features, such as request routing, controller scanning, and view rendering, while remaining lightweight and extensible. This documentation provides instructions for setup, usage, and understanding the framework’s current capabilities.

## Requirements

- **Java**: Compatible with Java 8 or higher.
- **Jakarta EE**: Requires Jakarta EE 10 APIs (formerly javax.ee) for servlet functionality.
- **Dependencies**: 
  - Gson (for JSON serialization in REST responses).
  - No pre-configured build system (manual compilation required).

## Installation

Winter v1.0.0 is available as a pre-built JAR file, `winter.jar`, for easy integration. Alternatively, you can build it manually from source if customization is needed. Choose one of the following options:

### Option 1: Use the Released JAR

1. **Download the JAR**:
   - Obtain `winter.jar` from the [Release v1.0.0](https://github.com/hasinajy/winter-framework/releases/tag/v1.0.0) assets on GitHub.

2. **Add to Your Project**:
   - Copy `winter.jar` to your web application’s library directory (e.g., `WEB-INF/lib`).
   - Ensure Jakarta EE 10 libraries (e.g., `jakarta.servlet-api`) are included in your project’s classpath.

### Option 2: Build Manually from Source

Winter does not yet include a pre-configured build system (e.g., Maven, Gradle). Follow these steps to build and install manually:

1. **Compile the Source Code**:
   - Navigate to the `src/` directory.
   - Compile all `.java` files using a Java compiler (e.g., `javac`).
     - **VS Code Users**: Compilation output defaults to the `bin` directory.
     - **Other IDEs**: Adjust the output directory as needed.
   - Example command (Linux/macOS):
     ```bash
     javac -d bin src/winter/**/*.java
     ```

2. **Package into a JAR**:
   - Use the provided scripts in the `packaging/` directory:
     - **Linux/macOS**: Run `./packaging/package.sh`
     - **Windows**: Double-click `packaging/package.bat`
   - The script generates `winter.jar` in a `lib/` directory.
   - Adjust the script if your compilation directory differs from `bin`.

3. **Add to Your Project**:
   - Copy `winter.jar` to your web application’s library directory (e.g., `WEB-INF/lib`).
   - Ensure Jakarta EE 10 libraries are included in your project’s classpath.

**Note**: For most use cases, Option 1 is recommended for simplicity. Use Option 2 if you need to modify the source code (see [Contributing](#contributing) for details).

## Configuration

Configure Winter in your web application’s `web.xml` file to enable the front controller and specify the controller package:

```xml
<!-- Front controller mapping -->
<servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>winter.FrontController</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

<!-- Map the path to the package to scan for controllers -->
<context-param>
    <param-name>ControllersPackage</param-name>
    <param-value>com.example.controllers</param-value>
</context-param>
```

- **FrontController**: Maps all URLs (`/`) to `winter.FrontController`, which handles request routing.
- **ControllersPackage**: Specifies the package (e.g., `com.example.controllers`) containing classes annotated with `@Controller`.

## Current Functionalities

### Core Components

- **FrontController**: 
  - Located at `src/winter/FrontController.java`.
  - Intercepts all incoming requests, routes them to mapped controller methods, and handles responses (HTML or JSON).
  - Supports multipart requests via `@MultipartConfig`.

- **ControllerScanner**: 
  - Scans the specified package for `@Controller`-annotated classes during initialization.
  - Registers URL mappings in a static `Map<String, Mapping>`.

- **ControllerHandler**: 
  - Invokes controller methods, manages parameter binding (via `@RequestParam`), and injects `Session` objects.

- **ExceptionHandler**: 
  - Logs exceptions and sends styled HTML error responses (e.g., 404, 500) using Tailwind CSS.

### Annotations

- **`@Controller`**:
  - Marks a class as a controller for scanning and routing.
  - Example: `@Controller public class UserController {}`

- **`@UrlMapping`**:
  - Defines URL paths for classes or methods (e.g., `@UrlMapping("/users")`).
  - Can be class-level (prefix) or method-level (endpoint).

- **`@GET`, `@POST`**:
  - Maps methods to HTTP GET or POST verbs.
  - Example: `@GET @UrlMapping("/list") public String listUsers() {}`

- **`@Rest`**:
  - Marks methods as REST endpoints, returning JSON instead of rendering views.
  - Example: `@Rest @GET @UrlMapping("/api/data") public String getData() {}`

- **`@RequestParam`**:
  - Binds request parameters to method arguments or fields.
  - Attributes: `value` (name), `type` (e.g., `TEXT`, `EMAIL`), `required` (true/false).
  - Example: `@RequestParam(value = "email", type = RequestParamType.EMAIL, required = true)`

### Data Structures

- **`Mapping`**:
  - Stores a URL’s associated controller class and methods, keyed by HTTP verbs.

- **`MappingMethod`**:
  - Encapsulates a controller method, its verb, and authentication roles.

- **`ModelView`**:
  - Holds a JSP URL and a data map for view rendering.
  - Example: `return new ModelView("users.jsp").addObject("users", userList);`

- **`JsonString`**:
  - Wraps a string for JSON responses in REST methods.

- **`Session`**:
  - Abstracts `HttpSession` for session management (e.g., `session.add("user", userObj)`).

### Error Handling

- **HTTP 404**: Sent when no mapping matches the requested URL (`MappingNotFoundException`).
- **HTTP 500**: Sent for critical errors, including:
  - Missing package provider (`PackageProviderNotFoundException`).
  - Invalid package name (`InvalidPackageNameException`).
  - Duplicate mappings (`DuplicateMappingException`).
  - Invalid return types (`InvalidReturnTypeException`).
  - Unexpected exceptions.

## Usage Examples

### Basic Controller with JSP Rendering

```java
package com.example.controllers;

import winter.data.annotation.Controller;
import winter.data.annotation.http.requestverb.GET;
import winter.data.annotation.http.UrlMapping;
import winter.data.client.ModelView;

@Controller
@UrlMapping("/users")
public class UserController {

    @GET
    @UrlMapping("/list")
    public ModelView listUsers() {
        ModelView mv = new ModelView("users.jsp");
        mv.addObject("message", "User List");
        return mv;
    }
}
```

- **URL**: `/users/list`
- **Result**: Forwards to `users.jsp` with `"message"` attribute.

### REST Endpoint

```java
package com.example.controllers;

import winter.data.annotation.Controller;
import winter.data.annotation.Rest;
import winter.data.annotation.http.requestverb.GET;
import winter.data.annotation.http.UrlMapping;

@Controller
@UrlMapping("/api")
public class ApiController {

    @Rest
    @GET
    @UrlMapping("/data")
    public String getData() {
        return "Hello there!";
    }
}
```

- **URL**: `/api/data`
- **Result**: Returns `Hello there!` as JSON.

### Form Data with Validation

```java
package com.example.controllers;

import winter.data.annotation.Controller;
import winter.data.annotation.http.RequestParam;
import winter.data.annotation.http.requestverb.POST;
import winter.data.annotation.http.UrlMapping;
import winter.data.client.ModelView;
import winter.data.enumdata.RequestParamType;

@Controller
public class FormController {

    @POST
    @UrlMapping("/submit")
    public ModelView submitForm(@RequestParam(value = "email", type = RequestParamType.EMAIL, required = true) String email) {
        ModelView mv = new ModelView("result.jsp");
        mv.addObject("email", email);
        return mv;
    }
}
```

- **URL**: `/submit` (POST)
- **Result**: Validates `email` parameter; forwards to `result.jsp` or sets error attributes if invalid.

### Session Management

```java
package com.controller.session;

import winter.data.annotation.Controller;
import winter.data.annotation.http.UrlMapping;
import winter.data.servletabstraction.Session;

@Controller
public class SessionSetController {
    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    @UrlMapping("/set-session")
    public String setSessionValue() {
        session.add("session", "Hasina");
        session.add("auth", "manager");
        return "Session has been set";
    }
}
```

- **URL**: `/set-session`
- **Result**: Adds `"session"` and `"auth"` attributes to the session with values `"Hasina"` and `"manager"`, respectively, and returns a confirmation string. The `Session` object is automatically injected by `ControllerHandler`. The setter method needs to be present.

### Form Data with Object Mapping

```java
package com.controller;

import com.models.Person;

import winter.data.annotation.Controller;
import winter.data.annotation.http.RequestParam;
import winter.data.annotation.http.UrlMapping;
import winter.data.annotation.http.requestverb.POST;
import winter.data.client.FormData;
import winter.data.client.ModelView;

@Controller
public class FormController {

    @UrlMapping("/person-form")
    public ModelView displayPersonForm() {
        ModelView modelView = new ModelView("WEB-INF/jsp/person-form.jsp");
        modelView.addObject("formData", new FormData());
        return modelView;
    }

    @POST
    @UrlMapping("/person-form")
    public ModelView processPersonForm(@RequestParam(value = "person") Person person) {
        ModelView modelView = new ModelView("WEB-INF/jsp/person-details.jsp");
        modelView.addObject("errorUrl", "WEB-INF/jsp/person-form.jsp");

        if (person != null) {
            modelView.addObject("name", person.getName());
            modelView.addObject("age", person.getAge());
        }

        return modelView;
    }
}
```

- **URL**: 
  - GET `/person-form`: Displays the form.
  - POST `/person-form`: Processes form submission.
- **Result**: 
  - The GET method renders `person-form.jsp` with an empty `FormData` object for initial form display.
  - The POST method maps form data to a `Person` object using `@RequestParam`, expecting fields like `person.name` and `person.age`. It renders `person-details.jsp` with the extracted data or redirects to `person-form.jsp` on validation errors (via `errorUrl`).

## API Reference

### Key Classes

- **`winter.FrontController`**:
  - Methods: `init()`, `doGet()`, `doPost()`, `getUrlMappings()`.
  - Handles all requests and initializes mappings.

- **`winter.service.ControllerScanner`**:
  - Method: `scanControllers(ServletContext)`.
  - Scans and registers controllers.

- **`winter.data.client.ModelView`**:
  - Methods: `addObject(String, Object)`, `setRequestAttributes(HttpServletRequest)`, `getJsonData()`.
  - Manages view data and JSP forwarding.

### Key Annotations

- **`@Controller`**: Marks controller classes.
- **`@UrlMapping`**: Defines URL paths.
- **`@GET`, `@POST`**: Specifies HTTP verbs.
- **`@Rest`**: Indicates REST endpoints.
- **`@RequestParam`**: Binds request parameters.

## Troubleshooting

- **404 Not Found**: Ensure the URL matches a `@UrlMapping` and the verb is supported.
- **500 Internal Server Error**:
  - Check `web.xml` for correct `ControllersPackage`.
  - Verify package name syntax (e.g., `com.example.controllers`).
  - Ensure unique `@UrlMapping` values.
  - Confirm controller methods return `String` or `ModelView`.

## Future Work

- Add support for additional HTTP verbs (e.g., PUT, DELETE).
- Implement dependency injection.
- Enhance validation for `@RequestParam`.
- Add a build system (e.g., Maven).

## Contributing

Contributions are welcome! To contribute:
1. Fork the repository.
2. Implement features or improvements.
3. Add Javadoc and comments where applicable.
4. Submit a pull request.

Focus areas:
- Replicating more Spring MVC features.
- Code optimization.
- Documentation expansion.

## License

Licensed under the MIT License. See [LICENSE](./LICENSE.md) for details.