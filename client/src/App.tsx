import React, { useEffect } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import Layout from './components/Layout';
import { refreshToken } from './utils/refreshToken';
import Main from './pages/Main';
import ProtectedRoute from './components/auth/ProtectedRoute';
import { useDispatch } from 'react-redux';
import { setInitialized } from './redux/slices/authSlice';

const App = () => {
    const dispatch = useDispatch();

    useEffect(() => {
        refreshToken().finally(() => {
            dispatch(setInitialized());
        });
    }, [dispatch]);

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Layout />}>
                    <Route
                        index
                        element={
                            <ProtectedRoute>
                                <Main />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="login"
                        element={
                            <AuthPage>
                                <LoginForm />
                            </AuthPage>
                        }
                    />
                    <Route
                        path="register"
                        element={
                            <AuthPage>
                                <RegisterForm />
                            </AuthPage>
                        }
                    />
                </Route>
            </Routes>
        </BrowserRouter>
    );
};

export default App;
