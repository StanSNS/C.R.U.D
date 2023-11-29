import axios from "axios";

// Fetch all users ordered by default criteria (unspecified) using email and password for authorization
export const getAllUsersDefault = (email, password) => {
    const url = `http://localhost:8000/home?action=getAllUsersOrderedByDefault&email=${email}&password=${password}`;
    return axios.get(url).then((response) => {
        if (response.status === 200) {
            return response.data;
        } else {
            throw new Error('Failed to get all users!');
        }
    }).catch((error) => {
        throw error;
    });
};

// Fetch all users sorted by last name and date of birth using email and password for authorization
export const getAllUsersSortedByLastNameAndDOB = (email, password) => {
    const url = `http://localhost:8000/home?action=getAllUsersSortedByLastNameAndDateOfBirth&email=${email}&password=${password}`;
    return axios.get(url).then((response) => {
        if (response.status === 200) {
            return response.data;
        } else {
            throw new Error('Failed to get sorted users by last name and DOB!');
        }
    }).catch((error) => {
        throw error;
    })
}

// Fetch all users found by selected option using email and password for authorization, and a search string
export const getAllUsersSearch = (email, password, searchTerm, selectedSearchOption) => {
    const url = `http://localhost:8000/home?action=getAllUsersFoundByParameter&email=${email}&password=${password}&searchTerm=${searchTerm}&selectedSearchOption=${selectedSearchOption}`;
    return axios.get(url).then((response) => {
        if (response.status === 200) {
            return response.data;
        } else {
            throw new Error('Failed to get sorted users by last name!');
        }
    }).catch((error) => {
        throw error;
    })
}

// Fetch a random user using email and password for authorization
export const getRandomUser = (email, password) => {
    const url = `http://localhost:8000/home?action=getRandomUser&email=${email}&password=${password}`;
    return axios.get(url).then((response) => {
        if (response.status === 200) {
            return response.data;
        } else {
            throw new Error('Failed to get random User!');
        }
    }).catch((error) => {
        throw error;
    })
}

// Deletes a user based on the provided email and password.
export const deleteUser = (email, password, userToDeleteEmail) => {
    const url = `http://localhost:8000/home?email=${email}&password=${password}&userToDeleteEmail=${userToDeleteEmail}`;
    return axios.delete(url)
        .then((response) => {
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error('Failed to delete User');
            }
        }).catch((error) => {
            throw error;
        });
};


// Function to change a user's phone number
export const changeUserPhoneNumber = (email, password, emailUserToChange, phoneNumberToChange) => {
    const url = `http://localhost:8000/home?email=${email}&password=${password}&emailUserToChange=${emailUserToChange}&phoneNumberToChange=${phoneNumberToChange}`;
    return axios.patch(url)
        .then((response) => {
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error('Failed to change user phone number');
            }
        }).catch((error) => {
            throw error;
        });
};

// Function to log out a user.
export const logoutUser = (email, password) => {
    const url = `http://localhost:8000/home?email=${email}&password=${password}`
    return axios.post(url)
};