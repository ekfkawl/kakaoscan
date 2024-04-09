import React from 'react';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import AppLayout from './components/AppLayout';
import SearchPage from './pages/SearchPage';
import ProtectedRoute from './components/ProtectedRoute';
import SearchHistoryPage from './pages/SearchHistoryPage';
import SearchHistoryDetailPage from './pages/SearchHistoryDetailPage';
import ShopPage from './pages/ShopPage';
import E404Page from './pages/E404Page';
import PolicyPage from './pages/PolicyPage';
import PaymentHistory from './pages/PaymentHistory';
import ProtectedAdminRoute from './components/ProtectedAdminRoute';
import PaymentManagementPage from "./pages/admin/PaymentManagementPage";

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
                                <SearchHistoryPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="search-history/detail"
                        element={
                            <ProtectedRoute>
                                <SearchHistoryDetailPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="shop"
                        element={
                            <ProtectedRoute>
                                <ShopPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="policy"
                        element={
                            <ProtectedRoute>
                                <PolicyPage />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/payment-history"
                        element={
                            <ProtectedRoute>
                                <PaymentHistory />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/admin/payment"
                        element={
                            <ProtectedRoute>
                                <ProtectedAdminRoute>
                                    <PaymentManagementPage />
                                </ProtectedAdminRoute>
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
                    <Route path="*" element={<E404Page />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
};

export default App;
