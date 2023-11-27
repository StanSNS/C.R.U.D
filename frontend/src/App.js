import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Hero from "./Component/Hero/Hero";
import './globalCSS/css//bootstrap.min.css'
import Register from "./Component/Register/Register";
import Login from "./Component/Login/Login";
import Home from "./Component/Home/Home";

function App() {

    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path='/' element={<Hero/>}></Route>
                    <Route path='/auth/register' element={<Register/>}></Route>
                    <Route path='/home' element={<Home/>}></Route>
                    <Route path='/auth/login' element={<Login/>}></Route>
                    <Route path="*" element={<Navigate to="/404"/>}/>
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
