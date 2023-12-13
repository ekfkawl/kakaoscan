import './index.css';
import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import Layout from './components/Layout';
import { Provider } from 'react-redux';
import store from './redux/store';
import { createRoot } from 'react-dom/client';

const container = document.getElementById('root');
if (container) {
    const root = createRoot(container);

    root.render(
        <React.StrictMode>
            <Provider store={store}>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<Layout />}>
                            <Route
                                index
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
            </Provider>
        </React.StrictMode>,
    );
}
