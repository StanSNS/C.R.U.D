import React, {useState} from "react";
import "./Register.css";
import {Link, useNavigate} from "react-router-dom";
import {registerUser} from "../../service/AuthService";

export default function Register() {

    // Import hooks and set initial state variables.
    const navigator = useNavigate();
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [dateOfBirth, setDateOfBirth] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");


    // Home button click handler navigates to the root route.
    const handleHomeButton = () => {
        navigator("/");
    };


    // Date change handler validates and updates date of birth.
    const handleDateChange = (event) => {
        const enteredDate = new Date(event.target.value);
        const currentDate = new Date();

        if (enteredDate > currentDate) {
            setError("Please select a past date.");
        } else {
            setDateOfBirth(event.target.value);
            setError("");
        }
    };


    // Form submission handler with validation and registration logic.
    const handleSubmit = (event) => {
        event.preventDefault();

        if (!firstName || !lastName) {
            setError("First name and last name cannot be empty.");
        } else if (!email.includes("@")) {
            setError("Please enter a valid email address.");
        } else if (password !== confirmPassword) {
            setError("Passwords do not match.");
        } else {
            setError("");

            // Create registration object and make API call.
            const register = {firstName, lastName, email, phoneNumber, dateOfBirth, password};
            registerUser(register)
                .then((response) => {
                    if (response.status === 226) {
                        setError(response.data);
                    } else {
                        navigator("/auth/login");
                    }
                })
                .catch((error) => {
                    console.error(error);
                });
        }
    };

    return (
        <div>
            <button className="customHomeButton pulses" onClick={handleHomeButton}>C.R.U.D.</button>

            <div className="container mt-1">
                <form className="form" onSubmit={handleSubmit}>
                    <p className="title">Register </p>
                    <p className="message">
                        Signup now and get full access to our app.{" "}
                    </p>
                    <div className="flex">
                        <label>
                            <input
                                className="input"
                                type="text"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                            />
                            <span>Firstname</span>
                        </label>

                        <label>
                            <input
                                className="input"
                                type="text"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                            />
                            <span>Lastname</span>
                        </label>
                    </div>

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
                            type="text"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
                        />
                        <span>Phone Number</span>
                    </label>

                    <label>
                        <input
                            className="input"
                            type="date"
                            value={dateOfBirth}
                            onChange={handleDateChange}
                        />
                        <span>Date of Birth (DD/MM/YYYY)</span>
                    </label>

                    <label>
                        <input className="input"
                               type="password"
                               value={password}
                               onChange={(e) => setPassword(e.target.value)}
                        />
                        <span>Password</span>
                    </label>
                    <label>
                        <input
                            className="input"
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                        />
                        <span>Confirm password</span>
                    </label>

                    <button className="submit" type="submit">Submit</button>

                    <p className="signin">Already have an account ?{" "}<Link to="/auth/login">Login</Link></p>

                    {error && <p className="errorMsg text-center">{error}</p>}
                </form>
            </div>
        </div>
    );
}
