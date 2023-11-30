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
- **JUnit and AssertJ**: Used for testing and test assertions.
- **Springdoc OpenAPI**: Generates OpenAPI documentation for backend API endpoints.
- **Flyway Database Migrations**: Handles database migrations.

### Entity table relations

- Below, you will find the table relations between the entities in the database.

![table relations](https://i.imgur.com/hpa3Ohs.png)


### Testing

- Below, you will find the coverage percentage obtained from the JUnit testing.

![test coverage](https://i.imgur.com/Dt1wkSC.png)

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

# Spring Boot Project Documentation

## AuthController

### Class Description

The `AuthController` class is responsible for handling authentication-related endpoints. It uses the Spring `@RestController` annotation to indicate that it is a controller handling RESTful requests. The class is annotated with `@CrossOrigin(FRONTEND_BASE_URL)` to allow cross-origin requests from the specified frontend base URL. The controller manages user registration through the `/auth/register` endpoint.

### Endpoints

#### 1. `/auth/register`

- **Method:** POST
- **Description:** Endpoint for user registration.
- **Parameters:**
    - `registerDto` (Request Body): The data transfer object containing registration information.
- **Returns:**
    - `ResponseEntity<String>`: A ResponseEntity with a string response and appropriate HTTP status.
        - If registration is successful, returns a success message with HTTP status code 201 (CREATED).
        - If the user email already exists, returns an appropriate message with HTTP status code 226 (IM_USED).

    
#### 2. `/auth/login`

- **Method:** POST
- **Description:** Endpoint for user login.
- **Parameters:**
    - `loginDTO` (Request Body): The data transfer object containing login information.
- **Returns:**
    - `ResponseEntity<AuthResponseDTO>`: A ResponseEntity with an authentication response DTO and HTTP status OK.


#### Dependencies

- `AuthService`: An instance of the `AuthService` class is injected through constructor injection, providing the necessary business logic for user registration.

## AuthService

### Class Description

The `AuthService` class is responsible for managing user registration. It encapsulates business logic for validating registration data, checking for existing email addresses, and persisting user entities to the database.

### Methods

#### 1. `register(RegisterDTO registerDto)`

- **Description:** Registers a new user with the provided `RegisterDTO` information.
- **Parameters:**
    - `registerDto`: Registration data for the new user.
- **Returns:**
    - `String`: A success message if registration is successful.
- **Exceptions:**
    - `DataValidationException`: Thrown if the registration data is not valid.
    - `ResourceAlreadyExistsException`: Thrown if the email already exists.

#### 2. `login(LoginDTO loginDto)`

- **Description:** Logs in a user with the provided login credentials.
- **Parameters:**
    - `loginDto`: Login credentials including email and password.
- **Returns:**
    - `AuthResponseDTO`: Contains information and user role(s) on successful login.
- **Exceptions:**
    - `DataValidationException`: Thrown if the login data is not valid.
    - `ResourceNotFoundException`: Thrown if the user with the provided email is not found.

#### Dependencies

- `UserEntityRepository`: Manages the persistence of user entities.
- `RoleEntityRepository`: Manages the persistence of role entities.
- `PasswordEncoder`: Encodes passwords before storing them.
- `AuthenticationManager`: Manages authentication processes.
- `ModelMapper`: Maps data between objects.
- `ValidationUtil`: Provides utility methods for data validation.
- `CustomDateFormatter`: Formats dates according to custom patterns.



## HomeController 

## Class Description

The `HomeController` class is responsible for managing and handling various operations related to user data. It utilizes the Spring `@RestController` annotation to designate it as a controller for RESTful requests. This controller is designed to interact with the `HomeService` to perform operations such as retrieving user details based on different criteria.

### Endpoints

#### 1. `/home`

- **Method:** GET
- **Description:** Endpoint for retrieving user data based on specified parameters.
- **Parameters:**
    - `action` (Query Parameter): Action to be performed, including options like retrieving all users, sorting by last name and date of birth, and more.
    - `email` (Query Parameter): User email for authentication.
    - `password` (Query Parameter): User password for authentication.
    - `selectedUserEmail` (Query Parameter, optional): Email of the selected user (used in specific actions).
    - `searchTerm` (Query Parameter, optional): Search term for filtering users based on specific criteria.
    - `selectedSearchOption` (Query Parameter, optional): Selected option for custom search.
    - `currentPage` (Query Parameter): Current page number for paginated results.
    - `sizeOnPage` (Query Parameter): Number of items per page for paginated results.
- **Returns:**
    - `ResponseEntity<?>`: ResponseEntity containing the result of the requested action and appropriate HTTP status.
        - If the action is successful, returns user data with HTTP status code 200 (OK).
        - If there are missing parameters, throws a `MissingParameterException` with HTTP status code 400 (BAD REQUEST).

#### 2. `/home`

- **Method:** DELETE
- **Description:** Endpoint for deleting a user.
- **Parameters:**
    - `email` (Query Parameter): User email for authentication.
    - `password` (Query Parameter): User password for authentication.
    - `userToDeleteEmail` (Query Parameter): Email of the user to be deleted.
- **Returns:**
    - `ResponseEntity<?>`: ResponseEntity with HTTP status code 200 (OK) if the deletion is successful.

#### 3. `/home`

- **Method:** POST
- **Description:** Endpoint for logging out a user.
- **Parameters:**
    - `email` (Query Parameter): User email for authentication.
    - `password` (Query Parameter): User password for authentication.
- **Returns:**
    - `ResponseEntity<?>`: ResponseEntity with HTTP status code 200 (OK) if the logout is successful.

#### 4. `/home`

- **Method:** PUT
- **Description:** Endpoint for editing user details.
- **Parameters:**
    - `email` (Query Parameter): User email for authentication.
    - `password` (Query Parameter): User password for authentication.
    - `emailUserToChange` (Query Parameter): Email of the user whose details are to be edited.
    - `newUserDataObject` (Request Body): Object containing the new user details.
- **Returns:**
    - `ResponseEntity<AuthResponseDTO>`: ResponseEntity containing the edited user details with HTTP status code 200 (OK).

### Dependencies

- `HomeService`: An instance of the `HomeService` class is injected through constructor injection, providing the necessary business logic for handling user data.

## Home Service

## Class Description

The `HomeService` class is responsible for containing the business logic related to user data retrieval. It performs operations such as retrieving all users by default, sorting users by last name and date of birth, and filtering users based on specific criteria.

### Methods

#### 1. `getAllUsersByDefault(String email, String password, Integer page, Integer size)`

- **Description:** Retrieves all users by default with paginated results.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.
    - `page`: Current page number for paginated results.
    - `size`: Number of items per page for paginated results.
- **Returns:**
    - `Page<UserDetailsDTO>`: Paginated user details.
- **Exceptions:**
    - `DataValidationException`: Thrown if the retrieved user data is not valid.

#### 2. `getAllUsersOrderedByLastNameAndDateOfBirth(String email, String password, Integer page, Integer size)`

- **Description:** Retrieves all users ordered by last name and date of birth with paginated results.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.
    - `page`: Current page number for paginated results.
    - `size`: Number of items per page for paginated results.
- **Returns:**
    - `Page<UserDetailsDTO>`: Paginated user details.
- **Exceptions:**
    - `DataValidationException`: Thrown if the retrieved user data is not valid.


#### 3. `getSelectedUser(String email, String password, String selectedUserEmail)`

- **Description:** Retrieves details of a specific user based on the provided email.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.
    - `selectedUserEmail`: Email of the user to retrieve details.
- **Returns:**
    - `UserDetailsDTO`: Details of the selected user.
- **Exceptions:**
    - `DataValidationException`: Thrown if the retrieved user data is not valid.
    - `ResourceNotFoundException`: Thrown if the specified user is not found.

#### 4. `getAllUsersByParameter(String email, String password, String searchTerm, String selectedSearchOption, Integer page, Integer size)`

- **Description:** Retrieves users based on various search parameters with paginated results.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.
    - `searchTerm`: The term to search for.
    - `selectedSearchOption`: The search option specifying the type of search (e.g., first name, last name, email).
    - `page`: Current page number for paginated results.
    - `size`: Number of items per page for paginated results.
- **Returns:**
    - `Page<UserDetailsDTO>`: Paginated user details based on the search criteria.
- **Exceptions:**
    - `DataValidationException`: Thrown if the retrieved user data is not valid.
    - `MissingParameterException`: Thrown if required parameters are missing.

#### 5. `deleteUser(String email, String password, String userToDeleteEmail)`

- **Description:** Deletes a user based on the provided email.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.
    - `userToDeleteEmail`: Email of the user to be deleted.
- **Exceptions:**
    - `ResourceNotFoundException`: Thrown if the specified user to delete is not found.
    - `AccessDeniedException`: Thrown if the logged-in user does not have the required permissions.

#### 6. `logoutUser(String email, String password)`

- **Description:** Logs out a user based on the provided email.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.

#### 7. `editUserDetails(String email, String password, String emailUserToChange, EditDetailsDTO newUserDataObject)`

- **Description:** Edits user details based on the provided email and new data.
- **Parameters:**
    - `email`: User email for authentication.
    - `password`: User password for authentication.
    - `emailUserToChange`: Email of the user whose details are to be edited.
    - `newUserDataObject`: Object containing the new user details.
- **Returns:**
    - `AuthResponseDTO`: Authentication response containing edited user details.
- **Exceptions:**
    - `ResourceAlreadyExistsException`: Thrown if the new email already exists.
    - `ResourceNotFoundException`: Thrown if the specified user to edit is not found.
    - `AccessDeniedException`: Thrown if the logged-in user does not have the required permissions.


### Dependencies

- `ValidateData`: Provides methods for validating user data.
- `UserEntityRepository`: Manages the persistence of user entities.
- `ModelMapper`: Maps data between objects.
- `ValidationUtil`: Provides utility methods for data validation.
- `PasswordEncoder`: Encodes passwords before storing them.


# Project Setup Instructions

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

