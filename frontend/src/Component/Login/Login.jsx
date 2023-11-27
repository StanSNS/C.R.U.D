import React, {useState} from "react";
import "./Login.css";
import {Link, useNavigate} from "react-router-dom";
import {loginUser, saveLoggedUser} from "../../service/AuthService";

export default function Login() {

    // Import hooks and set initial state variables.
    const navigator = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    // Home button click handler navigates to the root route.
    const handleHomeButton = () => {
        navigator("/");
    };

    // Form submission handler with email and password validation.
    const handleSubmit = (event) => {
        event.preventDefault();

        // Validation logic
        if (!email.includes("@")) {
            setError("Please enter a valid email address.");
        } else if (!password) {
            setError("Please enter your password.");
        } else {
            setError("");

            try {
                // Attempt to log in
                loginUser(email, password)
                    .then((response) => {
                        if (response.status === 200) {
                            localStorage.clear()
                            sessionStorage.clear()
                            const email = response.data.email;
                            const password = response.data.password;
                            const roles = response.data.roles;
                            const firstName = response.data.firstName;
                            saveLoggedUser(email,password,roles,firstName)
                            navigator("/home")
                        }
                    })
                    .catch(error => {
                        // Handle invalid login.
                        setError("Invalid username or password.");
                        console.error(error);
                    })
            } catch (error) {
                // Handle unexpected errors during login.
                setError("Invalid username or password.");
                console.error(error);
            }
        }
    }


    return (
        <div>
            <button className="customHomeButton pulses" onClick={handleHomeButton}>C.R.U.D.</button>

            <div className="container">
                <form className="form" onSubmit={handleSubmit}>
                    <p className="title">Login </p>
                    <p className="message">Login to get full access to our app.</p>

                    <label>
                        <input
                            className="input"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                        <span>Email</span>
                    </label>

                    <label>
                        <input
                            className="input"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <span>Password</span>
                    </label>

                    <button className="submit" type="submit">
                        Submit
                    </button>
                    <p className="signin">
                        Don't have an account? <Link to="/auth/register">Register</Link>
                    </p>

                    {error && <p className="errorMsg text-center">{error}</p>}
                </form>
            </div>
        </div>
    );
}
