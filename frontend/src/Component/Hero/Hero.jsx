import React from "react";
import "./Hero.css"
import {useNavigate} from 'react-router-dom'

export default function Hero() {

    const navigator = useNavigate(); // Import the useNavigate hook from the 'react-router-dom' library.

    // Function that handles the login button click event.
    const handleLoginButton = () => {
        navigator('/auth/login');
    };

    // Function that handles the register button click event.
    const handleRegisterButton = () => {
        navigator('/auth/register');
    };


    return (
        <div className="container d-flex flex-column align-items-center justify-content-center vh-100">
            <h1 className="text-center mb-4 pulses">C.R.U.D.</h1>
            <div className="text-center loginRegisterButtons">

                <button className="loginButton mr-2" onClick={handleLoginButton}>
                    <span className="loginButtonContent">Login</span>
                </button>

                <button className="registerButton ml-2" onClick={handleRegisterButton}>
                    <span className="registerButtonContent">Register</span>
                </button>

            </div>
        </div>
    );
}