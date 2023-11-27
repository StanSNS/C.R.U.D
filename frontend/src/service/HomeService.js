import axios from "axios";


// Function to fetch all users from the server using the provided email and password.
export const getAllUsers = (email, password) => {
    const url = `http://localhost:8000/home?email=${email}&password=${password}`;
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