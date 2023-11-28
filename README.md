# Project Overview

## Backend

- **Spring Boot**: Version 3.2.2 project based on the `spring-boot-starter-parent` for configuration.
- **Spring Framework**: Includes various Spring dependencies like:
    - `spring-boot-starter-data-jpa`
    - `spring-boot-starter-security`
    - `spring-boot-starter-validation`
    - `spring-boot-starter-web`
- **Database**: MySQL database with the `mysql-connector-j` dependency.
- **Project Lombok**: A library that simplifies the creation of Java classes by reducing boilerplate code.
- **Model Mapping**: Facilitates object-to-object mapping.
- **IP geolocation**:  Provides functionalities to query and retrieve geolocation data based on IP addresses.
- **JUnit and AssertJ**: Used for testing and test assertions.

### Entity table relations

- Below, you will find the table relations between the entities in the database.

![table relations](https://i.imgur.com/HWKg9At.png)


### Testing

- Below, you will find the coverage percentage obtained from the JUnit testing.

![test coverage](https://i.imgur.com/lN14mWw.png)

## Frontend

- **React**: The frontend is built using the `React framework`.
- **React Router**: Utilizes `React Router` for client-side routing.
- **Styling**: `Bootstrap 4.6` and CSS are used for styling.
- **HTTP Requests**: `Axios` is used for making HTTP requests.
- **Icons**: Various icon libraries like Font `Awesome` and `React Icons` are used, with dependencies including
    - @fortawesome/fontawesome-svg-core
    - @fortawesome/free-solid-svg-icons
    - @fortawesome/react-fontawesome
    - @react-icons/all-files
    - and react-icons
- **Encrypt & Decrypt**: `Crypto.js` is added for encrypting and decrypting sessionStorage data.
- **Module System**: Utilizes the esm package for ECMAScript Modules (ESM) support in Node.js.

### Other Dependencies are also part of the project:

- react-scripts
- web-vitals


## Project Setup Instructions

1. Make sure to clone or download the whole project.
2. Separate **[backend]** and **[frontend + frontend.iml]** into two individual folders.

### Backend (Spring Boot):

1. **Install Java:**
    - Make sure you have Java 17 installed on your computer:
      [Java 17 official download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)


2. **Install Maven:**
    - Install Maven on your computer. You can download it
      from [Maven official website](https://maven.apache.org/download.cgi).


3. **Install MySQL Community Server:**
    - Install MySQL Server on your computer. You can download it
      from [MySQL official website](https://dev.mysql.com/downloads/installer/).


4. **Open the backend project**
    - Go to **application.properties** file and change the **spring.datasource.(username & password)** values to your
      MySQL
      credentials


5. **Run the backend server:**
    - Open **BackendApplication.java** class - right click and run the program.

This will build your project and start the Spring Boot application.

5. **Verify:**
    - Open a web browser and go to [localhost:8000](http://localhost:8000) to verify that your backend is running.

### Frontend (React):

1. **Install Node.js:**
    - Make sure you have Node.js installed on your computer. You can download it
      from [Node.js official website](https://nodejs.org/).


2. **Open The frontend project**
    - In order everything to workout smoothly make sure that the **frontend.iml** file is outside the
      **frontend** folder. Then open the **frontend** folder.


3. **Install Dependencies:**
    - Open a terminal in the frontend project directory.
    - Run the following command to install the required dependencies:
      ```bash
      npm install
      ```
4. **Start the React App:**
    - Run the following command in the terminal to start the React development server:
      ```bash
      npm start
      ```
   This will start the React app and open it in your default web browser.


5. **Verify:**
    - Open a web browser and go to [localhost:3000](http://localhost:3000) to verify that your frontend is running.
      Now, both your Spring Boot backend and React frontend should be up and running. Make sure that the
      backend and frontend are successfully communicating with each other. If there are any API endpoints, ensure that the
      React app is making requests to the correct backend URLs.

