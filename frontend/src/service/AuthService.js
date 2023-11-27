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
export const saveLoggedUser = (email, password, roles) => {
    sessionStorage.setItem("Email", encryptData(email));
    sessionStorage.setItem("Password", encryptData(password));
    sessionStorage.setItem("Roles", encryptData(roles));
}
