import { useState, useCallback } from 'react';
import { GalleryItem } from 'lightgallery/lg-utils';

export const useGalleryItems = () => {
    const [items, setItems] = useState<GalleryItem[]>([]);

    const addGalleryItem = useCallback((newItem: GalleryItem) => {
        setItems((prevItems) => [...prevItems, newItem]);
    }, []);

    const clearGalleryItems = useCallback(() => {
        setItems([]);
    }, []);

    return { items, addGalleryItem, clearGalleryItems };
};
