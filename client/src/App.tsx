import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import AppLayout from './components/AppLayout';
import SearchPage from './pages/SearchPage';
import ProtectedRoute from './components/ProtectedRoute';
import SearchHistory from './pages/SearchHistory';

const App = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<AppLayout />}>
                    <Route
                        index
                        element={
                            <ProtectedRoute>
                                <SearchPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="search-history"
                        element={
                            <ProtectedRoute>
                                <SearchHistory />
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
