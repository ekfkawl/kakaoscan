import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
    token: string | null;
    isInitialized: boolean;
}

const initialState: AuthState = {
    token: null,
    isInitialized: false,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setToken(state, action: PayloadAction<string>) {
            state.token = action.payload;
        },
        clearToken(state) {
            state.token = null;
        },
        setInitialized: (state) => {
            state.isInitialized = true;
        },
    },
});

export const { setToken, clearToken, setInitialized } = authSlice.actions;

export default authSlice.reducer;
