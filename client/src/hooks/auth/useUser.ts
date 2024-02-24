import { useSelector } from 'react-redux';
import { RootState } from '../../redux/store';
import { User } from '../../types/authTypes';

const useUser = (): User | null => {
    return useSelector((state: RootState) => state.auth.user);
}

export default useUser;
