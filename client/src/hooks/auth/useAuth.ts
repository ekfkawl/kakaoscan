import { useSelector } from 'react-redux';
import { RootState } from '../../redux/store';

const useAuth = () => {
    const { token, isInitialized } = useSelector((state: RootState) => state.auth);
    return {
        isAuthenticated: !!token,
        isInitialized,
    };
};

export default useAuth;
