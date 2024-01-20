import React, { useCallback, useEffect, useRef, useState } from 'react';
import LightGallery from 'lightgallery/react';

import './Gallery.css';
import lgThumbnail from 'lightgallery/plugins/thumbnail';
import lgZoom from 'lightgallery/plugins/zoom';
import lgFullscreen from 'lightgallery/plugins/fullscreen';
import lgVideo from 'lightgallery/plugins/video';
import lgAutoplay from 'lightgallery/plugins/autoplay';
import lgPager from 'lightgallery/plugins/pager';
import lgRotate from 'lightgallery/plugins/rotate';
import { GalleryProps } from '../../types/galleryItem';
import { v4 as uuidv4 } from 'uuid';

const Gallery: React.FC<GalleryProps> = React.memo(({ items }) => {
    const lightGallery = useRef<any>(null);
    const [key, setKey] = useState<string>('');

    const onInit = useCallback((detail: any) => {
        if (detail) {
            lightGallery.current = detail.instance;
        }
    }, []);

    useEffect(() => {
        if (lightGallery.current) {
            setKey(uuidv4());
            lightGallery.current.refresh();
        }
    }, [items]);

    const getItem = useCallback(() => {
        return items.map((item) => {
            const isMp4 = item.src?.endsWith('.mp4');
            return (
                <a
                    key={item.id}
                    className="gallery-item"
                    data-src={item.src}
                    data-sub-html={item.subHtml}
                    {...(isMp4 ? { 'data-iframe': true, 'data-poster': item.thumb } : {})}
                >
                    <img src={item.thumb} alt="" />
                </a>
            );
        });
    }, [items]);

    return (
        <LightGallery
            key={key}
            licenseKey={process.env.REACT_APP_LIGHTGALLERY_LICENSE_KEY}
            mode="lg-fade"
            speed={0}
            plugins={[lgThumbnail, lgZoom, lgVideo, lgAutoplay, lgFullscreen, lgPager, lgRotate]}
            mobileSettings={{
                controls: false,
                showCloseIcon: true,
                download: false,
                rotate: false,
            }}
            onInit={onInit}
        >
            {getItem()}
        </LightGallery>
    );
});

export default Gallery;
