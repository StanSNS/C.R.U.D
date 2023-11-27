import React, {useEffect, useState} from "react";
import "./Home.css"
import {useNavigate} from "react-router-dom";
import {getAllUsers} from "../../service/HomeService";
import {loggedUserEmail, loggedUserPassword} from "../../service/AuthService";

export default function Home() {

    // Import the useNavigate hook from the 'react-router-dom' library.
    const navigator = useNavigate();

    // State hook to manage the 'users' state variable and 'setUsers' function to update it.
    const [users, setUsers] = useState([]);

    // Function that handles the home button click event.
    const handleHomeButton = () => {
        navigator("/");
    };

    // Effect hook to fetch all users when the component mounts.
    useEffect(() => {
        getAllUsers(loggedUserEmail(), loggedUserPassword())
            .then((data) => {
                setUsers(data);
            })
            .catch((error) => {
                console.error("Error fetching user data: ", error);
            });
    }, []);


    return (
        <>
            <button className="customHomeButton pulses" onClick={handleHomeButton}>C.R.U.D.</button>
            <div className="container">
                <div className="row">

                    {users.map((user, index) => (

                        <div className="col-md-4 mb-4" key={index}>

                            <div className="flip-card mr-5">
                                <div className="flip-card-inner">
                                    <div className="flip-card-front">

                                        <div className="text-center mt-2">
                                            <span className="ml-2 mr-2">{user.firstName}</span>
                                            <span className="mr-2">{user.lastName}</span>
                                        </div>

                                        <span><span className="yellowWordColor">Date of Birth: </span>{user.dateOfBirth}</span>

                                        <div className="text-left mb-3 ml-3 mt-3">
                                            <h6 className="orangeWordColor">Roles:</h6>
                                            <h6 className="text-left ml-2">
                                                {user.roles.map((role, index) => (
                                                    <span key={index}>{role.name}{index !== user.roles.length - 1 ? ', ' : ''}</span>
                                                ))}
                                            </h6>
                                        </div>

                                        <div className="text-left mb-3 ml-3">
                                            <h6 className="orangeWordColor">User Details:</h6>
                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Email: </span>{user.email}</h6>
                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Phone number: </span>{user.phoneNumber}</h6>
                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Registered on: </span>{user.registerDate}
                                            </h6>
                                        </div>

                                        <div className="text-left mb-3 ml-3">
                                            <h6 className="orangeWordColor">Location Details:</h6>
                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Country: </span>{user.country}</h6>
                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Currency: </span>{user.currency}</h6>
                                            <h6 className="text-left ml-2"><span className="yellowWordColor">City: </span>{user.city}</h6>
                                        </div>

                                    </div>

                                    <div className="flip-card-back">
                                        <div className="text-left ml-4 ">
                                            <p className="reduceFontSizeImports mb-0"><span className="orangeWordColor">import </span>jakarta.validation.constraints.<span className="yellowWordColor">NotNull</span>;</p>
                                            <p className="reduceFontSizeImports mb-0"><span className="orangeWordColor">import </span>lombok.<span className="yellowWordColor">Getter</span>;</p>
                                            <p className="reduceFontSizeImports mb-1"><span className="orangeWordColor">import </span>lombok.<span className="yellowWordColor">Setter</span>;</p>
                                            <p className="reduceFontSizeImports mb-1"><span className="orangeWordColor">import </span>java.util.Set;</p>

                                            <p className="reduceFontSize mb-0"><span className="yellowWordColor">@Getter</span></p>
                                            <p className="reduceFontSize mb-0"><span className="yellowWordColor">@Setter</span></p>
                                            <p className="reduceFontSize mb-1"><span className="orangeWordColor">public class </span>UserDetailsDTO {'{'}</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">firstName</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">lastName</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">dateOfBirth</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> Set{'<RoleDTO>'} <span className="pinkWordColor">roles</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">email</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">phoneNumber</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">registerDate</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">country</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">currency</span>;</p>
                                            <p className="reduceFontSize mb-2 ml-4"><span className="orangeWordColor">private</span> String <span className="pinkWordColor">city</span>;</p>
                                            <p className="reduceFontSize ml-1 mb-2 ">{'}'}</p>

                                        </div>


                                    </div>

                                </div>
                            </div>

                        </div>

                    ))}
                </div>

            </div>

        </>
    );
}