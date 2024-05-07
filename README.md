## Spring Clone - Winter Project

This project is a work-in-progress clone of the Spring framework written in Java. It currently implements a basic FrontController that captures and prints the user's requested URL.

**Directory Structure:**

* **src/**: Contains the source code for the project.
* **packaging/**: Houses scripts for converting the project into a JAR file for deployment.
    * **package.sh**: Shell script for Linux/macOS users.
    * **package.bat**: Batch script for Windows users.

**Requirements**

The project uses the Jakarta EE APIs (formerly javax.ee). Make sure to have Jakarta EE 10 (or the required version) installed.

**Building and Running the Project:**

The project currently does not have a pre-configured build system. To manually build and run the project:

1. Compile the source code using a Java compiler. If you are using *VS Code*, the compilation directory will be `bin`. If the compilation directory is elsewhere, please adjust the script accordingly.
2. Use the appropriate script in the `packaging/` directory to create a JAR file:
    * Linux/macOS: Run `./packaging/package.sh`
    * Windows: Double-click `packaging/package.bat`

The script will create a JAR file named `winter.jar`. Add the file to your project's libraries and it will be ready to use for your web application.

**Current Functionality:**

* The `FrontController` class (located in `src/winter/controllers/FrontController.java`) captures the user's requested URL and prints it in the browser.

**Future Work:**

This project is currently under development and only implements a basic feature. More functionalities and functionalities specific to replicating Spring's capabilities will be added in the future.

**Contributing:**

Contributions are welcome! Feel free to contribute to this project by:

* Implementing additional features based on the Spring framework.
* Improving the existing code.
* Adding documentation and comments.

**License:**

This project is licensed under the MIT License. This license grants you permission to freely use, modify, and distribute this software under certain conditions. Please refer to the [LICENSE](./LICENSE.md) file for more details.

**Author:**

Hasina JY
