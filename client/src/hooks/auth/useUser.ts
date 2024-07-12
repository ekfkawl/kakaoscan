import { useSelector } from 'react-redux';
import { RootState } from '../../redux/store';
import {User, UserWrapper} from '../../types/authTypes';

const useUser = (): UserWrapper | null => {
    const user = useSelector((state: RootState) => state.auth.user);

    if (user) {
        const hasSnapshotPreservation = user.items?.some(item => item.productType === 'SNAPSHOT_PRESERVATION') || false;
        return { ...user, hasSnapshotPreservation };
    }

    return null;
}
export default useUser;
