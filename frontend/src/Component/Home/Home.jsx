import React, {useEffect, useState} from "react";
import "./Home.css"
import {useNavigate} from "react-router-dom";
import {
    changeUserDetails,
    deleteUser,
    getAllUsersDefault,
    getAllUsersSearch,
    getAllUsersSortedByLastNameAndDOB,
    getSelectedUserInfo,
    logoutUser
} from "../../service/HomeService";
import {
    isAdministrator,
    loggedUserEmail,
    loggedUserFirstName,
    loggedUserPassword,
    updateLoggedUserDetails
} from "../../service/AuthService";
import {Button, Modal} from "react-bootstrap";
import {MdOutlineDangerous} from "react-icons/md";

export default function Home() {

    const navigator = useNavigate(); // Import the useNavigate hook from the 'react-router-dom' library.
    const [users, setUsers] = useState([]); // State hook to manage the 'users' state variable and 'setUsers' function to update it.
    const [showModal, setShowModal] = useState(false); // State variable to control the visibility of a modal
    const [userToDelete, setUserToDelete] = useState(null); // State variable to store information about the user to be deleted
    const [showSearchModal, setShowSearchModal] = useState(false); // State variable to control the visibility of the search modal
    const [searchTerm, setSearchTerm] = useState(""); // New state for the search term
    const [searchError, setSearchError] = useState(""); // New state for search error
    const [showEditModal, setShowEditUserModal] = useState(false); // State variable to control the visibility of the edit user modal
    const [editedUser, setEditedUser] = useState(null); // State variable to store information about the user being edited
    const [userEditError, setUserEditError] = useState(""); // New state for phone number error
    const [selectedSearchOption, setSelectedSearchOption] = useState("lastName"); // New state to store the selected option
    const [selectedUserInfo, setSelectedUserInfo] = useState(null);// New state variable to store the selected user information for displaying in the modal
    const [showUserInfoModal, setShowUserInfoModal] = useState(false);// New state variable to control the visibility of the user info modal
    const [newUserData, setNewUserData] = useState(null); // State to store new user data, initialized as null
    const [editedUserFirstName, setEditedUserFirstName] = useState(""); // State to store the edited first name of a user
    const [editedUserLastName, setEditedUserLastName] = useState("");// State to store the edited last name of a user
    const [editedUserEmail, setEditedUserEmail] = useState("");// State to store the edited email of a user
    const [editedUserPhoneNumber, setEditedUserPhoneNumber] = useState("");// State to store the edited phone number of a user
    const [editedUserPassword, setEditedUserPassword] = useState("");// State to store the edited password of a user
    const [editedUserConfirmPassword, setEditedUserConfirmPassword] = useState("");// State to store the edited confirmed password of a user
    const [userToEditEmail, setUserToEditEmail] = useState("");// State to store the email of the user to be edited


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
        getAllUsersDefault(loggedUserEmail(), loggedUserPassword())
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

                getAllUsersDefault(loggedUserEmail(), loggedUserPassword())
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
        getAllUsersDefault(loggedUserEmail(), loggedUserPassword())
            .then((data) => {
                setUsers(data)
            }).catch((error) => {
            console.error("Error sorting users by default sorting: ", error);
        });
    };

    // Handle the click event for sorting by last name and date of birth
    const handleSortByLastNameAndDOB = () => {
        getAllUsersSortedByLastNameAndDOB(loggedUserEmail(), loggedUserPassword())
            .then((data) => {
                setUsers(data)
            }).catch((error) => {
            console.error("Error sorting users by last name and date of birth: ", error);
        });
    };

    // Show the search modal when the "Search by..." button is clicked
    const handleSearchByParameter = () => {
        setSearchTerm("")
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

    // Handle confirmation for search by parameter
    const handleSearchModalConfirm = () => {
        if (searchTerm.trim() === "" && selectedSearchOption) {
            setSearchError("Search box cannot be empty!");
        } else {
            setSearchError("");
            getAllUsersSearch(loggedUserEmail(), loggedUserPassword(), searchTerm, selectedSearchOption)
                .then((data) => {
                    setUsers(data)
                    setSearchTerm("")
                }).catch((error) => {
                console.error("Error getting users by search term: ", error);
            });
            setShowSearchModal(false);
        }
    };

    // Close the edit modal and reset related state variables
    const closeEditModal = () => {
        setShowEditUserModal(false);
        setEditedUser(null);
        setNewUserData(null);
        setUserEditError("");
        setEditedUserFirstName("");
        setEditedUserLastName("");
        setEditedUserEmail("");
        setEditedUserPhoneNumber("");
        setEditedUserPassword("");
        setEditedUserConfirmPassword("");
        setUserToEditEmail("")
    };

    // Handle the click event for editing a user
    const handleEditUser = (user) => {
        setShowEditUserModal(true);
        setEditedUser(user);
    };

    // Confirm the edit of a user and close the edit modal
    const confirmEditUser = () => {
        setNewUserData(constructEditedUserData());
        setUserToEditEmail(editedUser.email)
    };

    // useEffect to handle user data updates when newUserData changes or any of the dependencies change
    useEffect(() => {
        if (newUserData) {
            if (!editedUserFirstName && !editedUserLastName && !editedUserPhoneNumber && !editedUserPassword && !editedUserEmail) {
                setUserEditError("Please fill at least one field!");
                setNewUserData(null)
            } else if (editedUserEmail && !editedUserEmail.includes("@")) {
                setUserEditError("Please enter a valid email address.");
                setNewUserData(null)
            } else if (editedUserPassword !== editedUserConfirmPassword) {
                setUserEditError("Passwords do not match.");
                setNewUserData(null)
            } else {
                setUserEditError("");
                changeUserDetails(loggedUserEmail(), loggedUserPassword(), userToEditEmail, newUserData)
                    .then((data) => {
                        updateLoggedUserDetails(data.email, data.password, data.roles, data.firstName)
                        handleDefaultSort()
                        closeEditModal();
                    })
                    .catch((error) => {
                        setUserEditError("An error occurred trying to edit the user details!");
                        console.error("Error changing user details: ", error);
                    });
            }
        }
    }, [userToEditEmail, editedUserConfirmPassword, editedUserEmail, editedUserFirstName, editedUserLastName, editedUserPassword, editedUserPhoneNumber, newUserData]);

    // Function to construct an object from edited user fields
    const constructEditedUserData = () => {
        return {
            firstName: editedUserFirstName,
            lastName: editedUserLastName,
            email: editedUserEmail,
            phoneNumber: editedUserPhoneNumber,
            password: editedUserPassword,
        };
    };

    // Function to handle the "Show info" button click event
    const handleShowInfo = (user) => {
        getSelectedUserInfo(loggedUserEmail(), loggedUserPassword(), user.email)
            .then((data) => {
                setSelectedUserInfo(data);
                setShowUserInfoModal(true);
            }).catch((error) => {
            console.error("Error selecting user: ", error);
        });
    };


    return (
        <>
            <div className="d-flex justify-content-between">
                <div className="text-left font-weight-bolder ml-3 mt-1 ">
                    <h2>Hello, {loggedUserFirstName()}</h2>
                </div>

                <div className="mt-2">
                    <button className="sortingButton" onClick={handleDefaultSort}>Default Sort</button>
                    <button className="sortingButton ml-3 mr-3 " onClick={handleSortByLastNameAndDOB}>Sort By Last Name
                        and Date of Birth
                    </button>
                    <button className="sortingButton" onClick={handleSearchByParameter}>Search by...</button>
                </div>

                <div className="text-right">
                    <button className="customLogoutButton mr-2" onClick={handleLogoutButton}>
                        <span className="customTextSize">Logout</span>
                    </button>
                </div>
            </div>

            <div className="container">
                <div className="row">
                    {users.length === 0 && (
                        <div className="text-center">
                            <div className="errorIcon mb-5"><MdOutlineDangerous/></div>
                            <h1 className="font-weight-bolder customStyleErrorMSG">No users were found!</h1>
                        </div>
                    )}
                    {users.map((user, index) => (
                        <div className={`col-${12 / Math.min(users.length, 3)} mb-4`} key={index}>
                            <div className="flip-card mr-5">
                                <div className="flip-card-inner">
                                    <div className="flip-card-front">

                                        <h4 className="mb-4 mt-3">User Details:</h4>

                                        <h6 className="text-left mb-4 ml-4">
                                            <span className="orangeWordColor">First Name: </span> {user.firstName}
                                        </h6>

                                        <h6 className="text-left mb-4 ml-4">
                                            <span className="orangeWordColor">Last Name: </span> {user.lastName}
                                        </h6>

                                        <h6 className="text-left mb-4 ml-4">
                                            <span className="orangeWordColor">Date of Birth: </span> {user.dateOfBirth}
                                        </h6>

                                        <h6 className="text-left mb-4 ml-4">
                                            <span className="orangeWordColor">Email: </span> {user.email}
                                        </h6>

                                        <h6 className="text-left mb-4 ml-4">
                                            <span className="orangeWordColor">Phone number: </span> {user.phoneNumber}
                                        </h6>

                                        <h6 className="text-left mb-4 ml-4">
                                            <span className="orangeWordColor">Roles: </span>
                                            {user.roles.map((role, index) => (
                                                <span key={index}>
                                                    {role.name}{index !== user.roles.length - 1 ? ', ' : ''}
                                                </span>))
                                            }
                                        </h6>
                                    </div>

                                    <div className="flip-card-back">
                                        <div className="customMarginTop">

                                            {user.email === loggedUserEmail() &&
                                                <button className="btn btn-warning customWidth"
                                                        onClick={() => handleEditUser(user)}>Edit User
                                                </button>
                                            }
                                            <button className="btn btn-primary customWidth mt-3"
                                                    onClick={() => handleShowInfo(user)}>
                                                Show info
                                            </button>

                                            {isAdministrator() && !user.roles.some(role => role.name === "ADMIN") &&
                                                <button className="btn btn-danger customWidth mt-3"
                                                        onClick={() => handleDeleteUser(user)}>
                                                    Delete User
                                                </button>
                                            }
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
                    <Modal.Title>Search Users</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <label htmlFor="searchOptionSelect">Select Search Option:</label>
                    <select

                        id="searchOptionSelect"
                        className="form-control mb-3"
                        value={selectedSearchOption}
                        onChange={(e) => setSelectedSearchOption(e.target.value)}
                    >
                        <option value="firstName">First Name</option>
                        <option value="lastName">Last Name</option>
                        <option value="phoneNumber">Phone Number</option>
                        <option value="email">Email</option>
                    </select>

                    <label htmlFor="searchInput">Search: </label>
                    <input
                        type="text"
                        id="searchInput"
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
                    <Modal.Title>Edit User Details:</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <label htmlFor="firstNameInput">Change Your First Name</label>
                    <input
                        type="text"
                        id="firstNameInput"
                        className="form-control"
                        value={editedUserFirstName}
                        placeholder={`Your current first name is: ${editedUser?.firstName || ''}`}
                        onChange={(e) => setEditedUserFirstName(e.target.value)}
                    />

                    <label htmlFor="lastNameInput" className="mt-3">Change Your Last Name</label>
                    <input
                        type="text"
                        id="lastNameInput"
                        className="form-control"
                        value={editedUserLastName}
                        placeholder={`Your current last name is: ${editedUser?.lastName || ''}`}
                        onChange={(e) => setEditedUserLastName(e.target.value)}
                    />

                    <label htmlFor="emailInput" className="mt-3">Change Your Email</label>
                    <input
                        type="text"
                        id="emailInput"
                        className="form-control"
                        value={editedUserEmail}
                        placeholder={`Your current email is: ${editedUser?.email || ''}`}
                        onChange={(e) => setEditedUserEmail(e.target.value)}
                    />

                    <label htmlFor="phoneNumberInput" className="mt-3">Change Your Phone Number</label>
                    <input
                        type="text"
                        id="phoneNumberInput"
                        className="form-control"
                        value={editedUserPhoneNumber}
                        placeholder={`Your current phone number is: ${editedUser?.phoneNumber || ''}`}
                        onChange={(e) => setEditedUserPhoneNumber(e.target.value)}
                    />

                    <label htmlFor="passwordInput" className="mt-3">Change Your Password</label>
                    <input
                        type="password"
                        id="passwordInput"
                        className="form-control"
                        value={editedUserPassword}
                        onChange={(e) => setEditedUserPassword(e.target.value)}
                    />

                    <label htmlFor="passwordInputConfirm" className="mt-3">Please confirm your password!</label>
                    <input
                        type="password"
                        id="passwordInputConfirm"
                        className="form-control"
                        value={editedUserConfirmPassword}
                        onChange={(e) => setEditedUserConfirmPassword(e.target.value)}
                    />

                </Modal.Body>
                {userEditError && (
                    <p className="text-danger text-center font-weight-bolder">{userEditError}</p>
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

            <Modal show={showUserInfoModal} onHide={() => setShowUserInfoModal(false)}>
                <Modal.Header>
                    <Modal.Title>User Information</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedUserInfo && (
                        <>
                            <h6>
                                <span className="font-weight-bolder">First Name: </span>{" "}
                                {selectedUserInfo.firstName}
                            </h6>

                            <h6>
                                <span className="font-weight-bolder">Last Name: </span>{" "}
                                {selectedUserInfo.lastName}
                            </h6>

                            <h6>
                                <span className="font-weight-bolder">Date of birth: </span>{" "}
                                {selectedUserInfo.dateOfBirth}
                            </h6>

                            <h6>
                                <span className="font-weight-bolder">Email: </span>{" "}
                                {selectedUserInfo.email}
                            </h6>

                            <h6>
                                <span className="font-weight-bolder">Phone number: </span>{" "}
                                {selectedUserInfo.phoneNumber}
                            </h6>

                            <h6>
                                <span className="font-weight-bolder">Roles: </span>{" "}
                                {selectedUserInfo.roles.map((role, index) => (
                                    <span key={index}>
                                                    {role.name}{index !== selectedUserInfo.roles.length - 1 ? ', ' : ''}
                                                </span>))
                                }
                            </h6>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer className="justify-content-center">
                    <Button variant="secondary" onClick={() => setShowUserInfoModal(false)}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

        </>
    );
}