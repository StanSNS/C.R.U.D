import './error404.css'
import {Link} from "react-router-dom";
import React from "react";
import {isUserLoggedIn} from "../../service/AuthService";

export default function Error404() {
    return (
        <div className="error-container mt-5">
            <div className="error-content">
                <div>
                    <h1 className="text-danger">404 Not Found</h1>
                    <p>The page you are looking for does not exist.</p>

                    {isUserLoggedIn() &&
                        <Link to='/home' className="btn-box-error">Home</Link>
                    }

                    {!isUserLoggedIn() &&
                        <Link to='/' className="btn-box-error">Home</Link>
                    }

                </div>
            </div>
        </div>
    );
}