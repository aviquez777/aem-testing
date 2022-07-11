var headerNavInit = (function () {
    'use strict';

    // Initialize the component
    function _initialize(elem) {
        var body = document.querySelector('body'),
            megaNav = elem.querySelector('.mega-nav'),
            bottomNav = elem.querySelector('.bottom-nav'),
            navToggle = elem.getElementsByClassName('nav-toggle'),
            closeMenuBtns = elem.querySelectorAll('.mega-nav .close'),
            goBackBtns = elem.querySelectorAll('.mega-nav .back'),
            skipBtn = document.querySelector('#skip'),
            j,
            mainContent;

        // Private methods
        function _goBack(e) {
            e.target.parentNode.parentNode.parentNode.classList.remove('show');

            if (megaNav) {
                megaNav.classList.remove('fixed');
            }
        }

        function _openMenu(e) {
            // if LoggedIn Nav is present and open, close it
            if (document.querySelector('.loggedin-nav') && typeof (EDC.components.loggedInNavMenu) !== 'undefined') {
                EDC.components.loggedInNavMenu.closeMenu(e);
            }

            body.classList.add('open-nav');

            if (megaNav) {
                megaNav.classList.add('show');
                body.style.height = megaNav.offsetHeight + 'px';
            }

            if (bottomNav) {
                bottomNav.classList.add('show');
            }
        }

        function _initComponent() {
            if (typeof (navToggle) !== 'undefined' && navToggle !== null && navToggle.length > 0) {
                navToggle[0].addEventListener('click', function () {
                    _openMenu();
                });
            }

            if (typeof (closeMenuBtns) !== 'undefined' && closeMenuBtns !== null && closeMenuBtns.length > 0) {
                for (j = 0; j < closeMenuBtns.length; j++) {
                    closeMenuBtns[j].addEventListener('click', function () {
                        EDC.components.headerNav.closeMenu();
                    });
                }
            }

            if (typeof (goBackBtns) !== 'undefined' && goBackBtns !== null && goBackBtns.length > 0) {
                for (j = 0; j < goBackBtns.length; j++) {
                    goBackBtns[j].addEventListener('click', function (e) {
                        _goBack(e);
                    });
                }
            }

            if (typeof (skipBtn) !== 'undefined') {
                skipBtn.addEventListener('click', function (e) {
                    e.preventDefault();
                    mainContent = document.querySelector('#maincontent');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(mainContent).y);

                    //     // when focus leaves this element,
                    //     // remove the tabindex attribute
                    mainContent.addEventListener('blur', function () {
                        mainContent.removeAttribute('tabindex');
                    });

                    // // Setting 'tabindex' to -1 takes an element out of normal
                    // // tab flow but allows it to be focused via javascript
                    mainContent.setAttribute('tabindex', -1);
                    mainContent.focus();
                });
            }
        }

        _initComponent();
    }

    // Public methods
    function init() {
        var mainHeader = document.querySelectorAll('.main-header:not([data-component-state="initialized"])');

        if (mainHeader) {
            mainHeader.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', headerNavInit.init);

// Exposing a Global Function to be used by Logged In Nav
EDC.components.headerNav = (function () {
    'use strict';

    // Public methods
    function closeMenu() {
        var body = document.querySelector('body'),
            megaNav = document.querySelector('.mega-nav'),
            bottomNav = document.querySelector('.bottom-nav'),
            openSubMenu = document.querySelector('.submenu.show');

        body.classList.remove('show');
        megaNav.classList.remove('fixed');
        megaNav.classList.remove('show');
        bottomNav.classList.remove('show');

        if (typeof (openSubMenu) !== 'undefined' && openSubMenu !== null) {
            openSubMenu.classList.remove('show');
        }

        body.classList.remove('open-nav');
        body.style.height = 'auto';
    }

    return {
        closeMenu: closeMenu
    };
})();