import axios from "axios";
import CryptoJS from 'crypto-js';


// API call to register a new user.
export const registerUser = (registerObj) => axios.post(`http://localhost:8000/auth/register`, registerObj);

// API call to authenticate and log in a user.
export const loginUser = (email, password) => axios.post(`http://localhost:8000/auth/login`, {email, password});


// Secret key for encryption and decryption
const SECRET_KEY = "C.R.U.D."


// Encrypt data using AES encryption.
const encryptData = (data) => {
    let encJson = CryptoJS.AES.encrypt(JSON.stringify(data), SECRET_KEY).toString();
    return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(encJson));
}


// Decrypt data using AES decryption.
const decryptData = (data) => {
    let decData = CryptoJS.enc.Base64.parse(data).toString(CryptoJS.enc.Utf8);
    let bytes = CryptoJS.AES.decrypt(decData, SECRET_KEY).toString(CryptoJS.enc.Utf8);
    if (bytes) {
        return JSON.parse(bytes);
    }
}

// Save user information securely in session storage.
export const saveLoggedUser = (email, password, roles, firstName) => {
    sessionStorage.setItem("Email", encryptData(email));
    sessionStorage.setItem("Password", encryptData(password));
    sessionStorage.setItem("Roles", encryptData(roles));
    sessionStorage.setItem("First Name", encryptData(firstName));
}

// Save user information securely in session storage after user details change.
export const updateLoggedUserDetails = (email, password, roles, firstName) => {

    if (email) {
        sessionStorage.setItem("Email", encryptData(email));
    }

    if (password) {
        sessionStorage.setItem("Password", encryptData(password));
    }

    if (roles) {
        sessionStorage.setItem("Roles", encryptData(roles));
    }

    if (firstName) {
        sessionStorage.setItem("First Name", encryptData(firstName));
    }
}


// Function to retrieve the logged-in user's first name from session storage and decrypt it.
export const loggedUserFirstName = () => {
    const name = sessionStorage.getItem("First Name");
    if (name) {
        return decryptData(name);
    }
}

// Function to retrieve the logged-in user's email from session storage and decrypt it.
export const loggedUserEmail = () => {
    const email = sessionStorage.getItem("Email");
    if (email) {
        return decryptData(email);
    }
}

// Function to retrieve the logged-in user's password from session storage and decrypt it.
export const loggedUserPassword = () => {
    const password = sessionStorage.getItem("Password");
    if (password) {
        return decryptData(password);
    }
}


// Function to check if the logged-in user is an administrator based on their role.
export const isAdministrator = () => {
    let roles = sessionStorage.getItem("Roles");

    if (roles) {
        roles = decryptData(roles);
        if (roles.includes("ADMIN")) {
            return true;
        }
    }
    return false;
}

// Function to check if a user is logged in based on session storage.
export const isUserLoggedIn = () => {
    return sessionStorage.getItem("Email") != null
        && sessionStorage.getItem("Roles")
        && sessionStorage.getItem("First Name")
        && sessionStorage.getItem("Password");
}

