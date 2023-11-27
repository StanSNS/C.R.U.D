import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Hero from "./Component/Hero/Hero";
import './globalCSS/css//bootstrap.min.css'
import Register from "./Component/Register/Register";
import Login from "./Component/Login/Login";
import Home from "./Component/Home/Home";
import {isUserLoggedIn} from "./service/AuthService";
import Error404 from "./Component/Error404/error404";

function App() {

    // Guarded route for logged-in users: redirects to 404 if not logged in
    function LoggedUserGuardedRoute({element}) {
        if (isUserLoggedIn()) {
            return <Navigate to="/404"/>
        }
        return element;
    }

    // Guarded route for not logged-in users: redirects to 404 if logged in
    function NotLoggedUserGuardedRoute({element}) {
        if (!isUserLoggedIn()) {
            return <Navigate to="/404"/>
        }
        return element;
    }

    return (
        <>
            <BrowserRouter>
                <Routes>
                    {/*<Route path='/' element={<Hero/>}></Route>*/}
                    <Route path='/' element={<LoggedUserGuardedRoute element={<Hero/>}/>}/>
                    <Route path='/home' element={<NotLoggedUserGuardedRoute element={<Home/>}/>}/>


                    <Route path='/auth/login' element={<LoggedUserGuardedRoute element={<Login/>}/>}/>
                    <Route path='/auth/register' element={<LoggedUserGuardedRoute element={<Register/>}/>}/>

                    <Route path="*" element={<Navigate to="/404"/>}/>
                    <Route path='/404' element={<Error404/>}></Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
