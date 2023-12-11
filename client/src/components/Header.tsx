import React from 'react';
import {DarkThemeToggle} from "flowbite-react";
import {FaGithub} from "react-icons/fa";

const Header = () => {
    return (
        <div className="absolute top-1 right-1">
            <DarkThemeToggle
                iconDark={FaGithub}
                iconLight={FaGithub}
                onClick={(e) => {
                    e.preventDefault();
                    window.open('https://github.com/ekfkawl/kakaoscan', '_blank');
                }}
            />
            <DarkThemeToggle />
        </div>
    );
};

export default Header;