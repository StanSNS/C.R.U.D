import React, {useEffect, useState} from "react";
import "./Home.css"
import {useNavigate} from "react-router-dom";
import {deleteUser, getAllUsers, logoutUser} from "../../service/HomeService";
import {loggedUserEmail, loggedUserPassword} from "../../service/AuthService";
import {FaTrashAlt} from "react-icons/fa";
import {Button, Modal} from "react-bootstrap";

export default function Home() {

    const navigator = useNavigate(); // Import the useNavigate hook from the 'react-router-dom' library.
    const [users, setUsers] = useState([]); // State hook to manage the 'users' state variable and 'setUsers' function to update it.

    const [showModal, setShowModal] = useState(false);
    const [userToDelete, setUserToDelete] = useState(null);


    // Function that handles the logout button click event.
    const handleLogoutButton = () => {
        logoutUser(loggedUserEmail(),loggedUserPassword())
            .then(() => {
                localStorage.clear()
                sessionStorage.clear()
                navigator("/");
            })
            .catch((error) => {
                console.error("Error fetching user data: ", error);
            });

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

    // Closes the modal and resets the userToDelete state.
    const closeModal = () => {
        setShowModal(false);
        setUserToDelete(null);
    };

    // Prepares to delete a user by setting the userToDelete state and showing the modal.
    const handleDeleteUser = (user) => {
        setUserToDelete(user);
        setShowModal(true);
    };

    // Confirms the deletion of the user, triggers the deleteUser API call, and updates the user list.
    const confirmDeleteUser = () => {
        deleteUser(loggedUserEmail(), loggedUserPassword(), userToDelete.email)
            .then(() => {

                closeModal();

                getAllUsers(loggedUserEmail(), loggedUserPassword())
                    .then((data) => {
                        setUsers(data);
                    })
                    .catch((error) => {
                        console.error("Error fetching user data: ", error);
                    });
            })
            .catch((error) => {
                console.error("Error deleting user: ", error);
                setShowModal(false);
            });
    };


    return (
        <>
            <div className="text-right">
                <button className="customLogoutButton mr-2" onClick={handleLogoutButton}><span className="customTextSize">Logout</span></button>
            </div>

            <div className="container">
                <div className="row">
                    {users.map((user, index) => (
                        <div className={`col-${12 / Math.min(users.length, 3)} mb-4`} key={index}>
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
                                                    <span
                                                        key={index}>{role.name}{index !== user.roles.length - 1 ? ', ' : ''}</span>
                                                ))}
                                            </h6>
                                        </div>

                                        <div className="text-left mb-3 ml-3">
                                            <h6 className="orangeWordColor">User Details:</h6>

                                            <h6 className="text-left ml-2"><span
                                                className="yellowWordColor">Email: </span>{user.email}</h6>

                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Phone number: </span>{user.phoneNumber}
                                            </h6>

                                            <h6 className="text-left ml-2"><span className="yellowWordColor">Registered on: </span>{user.registerDate}
                                            </h6>
                                        </div>

                                        <div className="text-left mb-3 ml-3">
                                            <h6 className="orangeWordColor">Location Details:</h6>

                                            <h6 className="text-left ml-2"><span
                                                className="yellowWordColor">Country: </span>{user.country}</h6>

                                            <h6 className="text-left ml-2"><span
                                                className="yellowWordColor">Currency: </span>{user.currency}</h6>

                                            <h6 className="text-left ml-2"><span
                                                className="yellowWordColor">City: </span>{user.city}</h6>
                                        </div>

                                    </div>

                                    <div className="flip-card-back">
                                        <div className="text-left ml-4 mt-1">
                                            <p className="reduceFontSize mb-0"><span
                                                className="yellowWordColor">@Getter</span></p>

                                            <p className="reduceFontSize mb-0"><span
                                                className="yellowWordColor">@Setter</span></p>

                                            <p className="reduceFontSize mb-1"><span className="orangeWordColor">public class </span>UserDetailsDTO {'{'}
                                            </p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">firstName</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">lastName</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">dateOfBirth</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> Set{'<RoleDTO>'} <span
                                                className="pinkWordColor">roles</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">email</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">phoneNumber</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">registerDate</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">country</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">currency</span>;</p>

                                            <p className="reduceFontSize mb-2 ml-4"><span
                                                className="orangeWordColor">private</span> String <span
                                                className="pinkWordColor">city</span>;

                                                <button className="customBin" onClick={() => handleDeleteUser(user)}>
                                                    <FaTrashAlt/>
                                                </button>
                                            </p>

                                            <p className="reduceFontSize ml-1 mb-2 ">{'}'}</p>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            <Modal show={showModal} onHide={closeModal}>
                <Modal.Header>
                    <Modal.Title>Confirmation</Modal.Title>
                </Modal.Header>
                <Modal.Body className="text-center">
                    Are you sure you want to delete the user?
                </Modal.Body>
                <Modal.Footer className="justify-content-center">
                    <Button variant="secondary" onClick={closeModal}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={confirmDeleteUser}>
                        Delete
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}