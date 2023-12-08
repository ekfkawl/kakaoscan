import "./index.css";
import React from "react";
import ReactDOM from 'react-dom';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AuthPage from "./pages/AuthPage";
import LoginForm from "./components/auth/LoginForm";
import RegisterForm from "./components/auth/RegisterForm";
import Layout from "./components/Layout";

ReactDOM.render(
    <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={<AuthPage>
                        <LoginForm />
                    </AuthPage>} />

                    <Route path="register" element={<AuthPage>
                        <RegisterForm />
                    </AuthPage>} />
                </Route>
            </Routes>
        </BrowserRouter>
    </React.StrictMode>,
    document.getElementById("root")
);