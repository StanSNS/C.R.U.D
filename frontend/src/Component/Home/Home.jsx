import React, {useEffect, useState} from "react";
import "./Home.css"
import {useNavigate} from "react-router-dom";
import {deleteUser, getAllUsers, logoutUser} from "../../service/HomeService";
import {isAdministrator, loggedUserEmail, loggedUserFirstName, loggedUserPassword} from "../../service/AuthService";
import {FaTrashAlt} from "react-icons/fa";
import {Button, Modal} from "react-bootstrap";

export default function Home() {

    const navigator = useNavigate(); // Import the useNavigate hook from the 'react-router-dom' library.
    const [users, setUsers] = useState([]); // State hook to manage the 'users' state variable and 'setUsers' function to update it.
    const [showModal, setShowModal] = useState(false); // State variable to control the visibility of a modal
    const [userToDelete, setUserToDelete] = useState(null); // State variable to store information about the user to be deleted
    const [showSearchModal, setShowSearchModal] = useState(false); // State variable to control the visibility of the search modal
    const [searchTerm, setSearchTerm] = useState(""); // New state for the search term
    const [searchError, setSearchError] = useState(""); // New state for search error
    const [showEditModal, setShowEditModal] = useState(false); // State variable to control the visibility of the edit user modal
    const [editedUser, setEditedUser] = useState(null); // State variable to store information about the user being edited
    const [newPhoneNumber, setNewPhoneNumber] = useState(""); // State variable to store the new phone number input in the edit user modal
    const [phoneNumberError, setPhoneNumberError] = useState(""); // New state for phone number error


    // Function that handles the logout button click event.
    const handleLogoutButton = () => {
        logoutUser(loggedUserEmail(), loggedUserPassword())
            .then(() => {
                navigator("/");
                localStorage.clear()
                sessionStorage.clear()
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

    // Handle the click event for default sorting
    const handleDefaultSort = () => {
        // Add logic for default sorting here
        console.log("Default Sort Clicked");
    };

    // Handle the click event for sorting by last name and date of birth
    const handleSortByLastName = () => {
        console.log("Sort By Last Name and Date of Birth Clicked");
    };

    // Show the search modal when the "Search by Last Name" button is clicked
    const handleSearchByLastName = () => {
        setShowSearchModal(true);
    };

    // Close the search modal and reset the search term
    const handleSearchModalClose = () => {
        setSearchTerm("")
        setShowSearchModal(false);
    };

    // Handle change in the search input field
    const handleSearchInputChange = (event) => {
        setSearchTerm(event.target.value);
    };

    // Handle confirmation for search by last name
    const handleSearchModalConfirm = () => {
        if (searchTerm.trim() === "") {
            setSearchError("Please enter a last name!");
        } else {
            setSearchError("");

            console.log("Search by Last Name Confirmed:", searchTerm);
            setShowSearchModal(false);
        }
    };

    // Close the edit modal and reset related state variables
    const closeEditModal = () => {
        setShowEditModal(false);
        setEditedUser(null);
        setNewPhoneNumber("");
        setPhoneNumberError("")
    };

    // Handle the click event for editing a user
    const handleEditUser = (user) => {
        setEditedUser(user);
        setNewPhoneNumber(user.phoneNumber);
        setShowEditModal(true);
    };

    // Confirm the edit of a user and close the edit modal
    const confirmEditUser = () => {
        if (newPhoneNumber.trim() === "") {
            setPhoneNumberError("Please enter a phone number!");
        } else {
            setEditedUser((prevUser) => ({
                ...prevUser,
                phoneNumber: newPhoneNumber,
            }));
            closeEditModal();
        }
    };

    // Handle the click event for getting a random user
    const handleGetRandomUser = () => {
        console.log("Get Random User Clicked");
    };

    return (
        <>
            <div className="d-flex justify-content-between">
                <div className="text-left font-weight-bolder ml-3 mt-1 ">
                    <h2>Hello, {loggedUserFirstName()}</h2>
                </div>

                <div className="mt-2">
                    <button className="sortingButton"
                            onClick={handleDefaultSort}>

                        Default Sort
                    </button>

                    <button className="sortingButton ml-3 mr-3 "
                            onClick={handleSortByLastName}>

                        Sort By Last Name and Date of Birth
                    </button>

                    <button className="sortingButton"
                            onClick={handleSearchByLastName}>
                        Search by Last Name
                    </button>

                    <button className="sortingButton ml-3"
                            onClick={handleGetRandomUser}>
                        Random User
                    </button>
                </div>

                <div className="text-right">
                    <button className="customLogoutButton mr-2" onClick={handleLogoutButton}><span
                        className="customTextSize">Logout</span></button>
                </div>
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
                                                className="pinkWordColor">phoneNumber</span>;

                                                <button
                                                    className="customEditButton ml-1 font-weight-bolder"
                                                    onClick={() => handleEditUser(user)}>
                                                    {'{EDIT}'}
                                                </button>
                                            </p>

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

                                                {isAdministrator() && !user.roles.some(role => role.name === "ADMIN") &&
                                                    <button className="customBin"
                                                            onClick={() => handleDeleteUser(user)}><FaTrashAlt/>
                                                    </button>
                                                }
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

            <Modal show={showSearchModal} onHide={handleSearchModalClose}>
                <Modal.Header>
                    <Modal.Title>Search by Last Name</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <label htmlFor="lastNameInput">Enter Last Name:</label>
                    <input
                        type="text"
                        id="lastNameInput"
                        className="form-control"
                        value={searchTerm}
                        onChange={handleSearchInputChange}
                    />
                </Modal.Body>
                {searchError && <p className="text-danger text-center font-weight-bolder">{searchError}</p>}

                <Modal.Footer className="justify-content-center">

                    <Button variant="secondary" onClick={handleSearchModalClose}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={handleSearchModalConfirm}>
                        Confirm
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showEditModal} onHide={closeEditModal}>
                <Modal.Header>
                    <Modal.Title>Edit Phone Number</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <label htmlFor="phoneNumberInput">Enter New Phone Number:</label>
                    <input
                        type="text"
                        id="phoneNumberInput"
                        className="form-control"
                        value={newPhoneNumber}
                        onChange={(e) => setNewPhoneNumber(e.target.value)}
                    />
                </Modal.Body>
                {phoneNumberError && (
                    <p className="text-danger text-center font-weight-bolder">{phoneNumberError}</p>
                )}
                <Modal.Footer className="justify-content-center">
                    <Button variant="secondary" onClick={closeEditModal}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={confirmEditUser}>
                        Confirm
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}