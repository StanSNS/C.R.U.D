import React from "react";
import "./Home.css"
import {useNavigate} from "react-router-dom";

export default function Home() {

    // Import the useNavigate hook from the 'react-router-dom' library.
    const navigator = useNavigate();

    // Function that handles the home button click event.
    const handleHomeButton = () => {
        navigator("/");
    };


    return (
        <>
            <button className="customHomeButton pulses" onClick={handleHomeButton}>C.R.U.D.</button>
            <div className="container">

                <div className="flip-card">
                    <div className="flip-card-inner">

                        <div className="flip-card-front">

                            <div className="text-center mt-2">
                                <span className="ml-2 mr-2">Stanimir</span>
                                <span className="mr-2">Sergev</span>
                            </div>

                            <span>Date of Birth: 12/15/2023</span>

                            <div className="text-left mb-3 ml-2 mt-3">
                                <h6>Roles</h6>
                                <h6 className="text-left ml-2 ">Administrator</h6>
                                <h6 className="text-left ml-2">Normal User</h6>
                            </div>

                            <div className="text-left mb-3 ml-2">
                                <h6>User Details:</h6>
                                <h6 className="text-left ml-2 ">Email: stanimirsergev159@abv.bg</h6>
                                <h6 className="text-left ml-2">Phone number: 0898660224</h6>
                            </div>

                            <div className="text-left mb-3 ml-2">
                                <h6>Location Details:</h6>
                                <h6 className="text-left ml-2 ">Country: Bulgaria</h6>
                                <h6 className="text-left ml-2">Currency: BGN</h6>
                                <h6 className="text-left ml-2">City: Ruse</h6>
                            </div>


                        </div>

                        <div className="flip-card-back">


                        </div>

                    </div>
                </div>
            </div>

        </>
    );
}