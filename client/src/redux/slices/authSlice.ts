import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AuthState, User } from '../../types/authTypes';

const initialState: AuthState = {
    isInitialized: false,
    token: null,
    user: null,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setInitialized: (state) => {
            state.isInitialized = true;
        },
        setToken(state, action: PayloadAction<string>) {
            state.token = action.payload;
        },
        clearToken(state) {
            state.token = null;
            state.user = null;
        },
        setUser: (state, action: PayloadAction<User>) => {
            state.user = action.payload;
        },
        clearUser: (state) => {
            state.user = null;
        },
    },
});

export const { setInitialized, setToken, clearToken, setUser, clearUser } = authSlice.actions;

export default authSlice.reducer;
