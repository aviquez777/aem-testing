var loggedInNavMenuJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            anchor = element.querySelector('.menu-btn'),
            submenu = element.querySelector('.submenu'),
            submenuAnchors = [],
            signoutBtn = submenu.querySelector('.signout.button.edc-quaternary-btn'),
            submenuInitialHeight = 0,
            firstLoad = true,
            navColumn = submenu.querySelector('.nav-column'),
            mobileTitle = element.querySelector('.mobile-title'),
            navToggle = element.querySelector('.nav-toggle'),
            topNav = d.querySelector('.top-nav'),
            closeBtn = submenu.querySelector('.mobile-title button.close'),
            pointerMoved = false,
            pointerInitPos = [0, 0],
            pointerNewPos = [0, 0],
            isTablet,
            isIE,
            windowInnerWidth,
            isAndroid = /(android)/i.test(navigator.userAgent);

        function _isMenuOpen() {
            return EDC.utils.elContainsCl(submenu, 'hover');
        }

        function _isMenuTouched() {
            return EDC.utils.elContainsCl(submenu, 'touched');
        }

        function _isTablet() {
            return window.innerWidth > EDC.props.media.lg && navigator.userAgent.match(/Tablet|iPad/i);
        }

        function _isMobile() {
            return window.innerWidth < EDC.props.media.md;
        }

        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        function _openMenu(e) {
            var eTarget = e.target,
                eType = e.type;

            e.preventDefault();
            eTarget.setAttribute('aria-expanded', 'true');

            // Close HeaderNav if open
            if (eType === 'click' && document.querySelector('.main-header') && typeof (EDC.components.headerNav) !== 'undefined') {
                EDC.components.headerNav.closeMenu(e);
            }

            // Open this menu
            submenu.classList.add('hover');
            mobileTitle.classList.add('hover');
            navColumn.scrollTop = 0;
            submenu.classList.remove('hidden-for-accessibility');
            submenuAnchors = [];

            if (submenuAnchors.length === 0) {
                submenu.querySelectorAll('.logged-in a, .mobile-title .close').forEach(function (thisAnchor) {
                    if (thisAnchor.offsetWidth > 0 && thisAnchor.offsetHeight > 0) {
                        submenuAnchors.push(thisAnchor);
                    }
                });
            }

            EDC.utils.focusingOnFirstElementForAccessibility(e, submenuAnchors);
        }

        function _toggleMenu(e) {
            if (!_isMenuOpen()) {
                _openMenu(e);
            } else {
                EDC.components.loggedInNavMenu.closeMenu(e);
            }
        }

        function _handleKeyPressed(e) {
            var keyCode = e.keyCode || e.which;

            if (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode || keyCode === EDC.props.arrowDownKeyCode) {
                e.preventDefault();

                if (submenu.classList.contains('hover') && (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode)) {
                    EDC.components.loggedInNavMenu.closeMenu(e);

                    if (navToggle) {
                        navToggle.focus();
                    }
                } else {
                    _toggleMenu(e);
                }
            }
        }

        function _doNothing(e) {
            e.preventDefault();
            return false;
        }

        function _handleClick(e) {
            if (_isMenuOpen()) {
                // you are on tablet
                _doNothing(e);
            } else {
                _openMenu(e);
            }
        }

        function _handlePointerDown(e) {
            e.preventDefault();
            pointerMoved = false;
            pointerInitPos = [e.screenX, e.screenY];
        }

        function _handlePointerMoved(e) {
            pointerMoved = true;
            pointerNewPos = [e.screenX, e.screenY];
        }

        function _handleMenuTouched(e) {
            // if first touch, open this menu, close other menus
            // if second touch, follow link
            if (_isMenuOpen()) {
                if (!pointerMoved) {
                    _toggleMenu(e);
                } else {
                    pointerMoved = false;
                }
            } else if (_isMenuTouched() && (!isTablet || isIE)) {
                // second touch
                window.location = e.currentTarget.href;
            } else {
                // first touch
                _openMenu(e);
                submenu.classList.add('touched');
            }
        }

        function _handlePointerOver(e) {
            // Only for Desktop

            if (!isTablet || isIE) {
                _openMenu(e);
            }
        }

        function _handlePointerOut() {
            // Only for Desktop
            if (!isAndroid && (!isTablet || isIE)) {
                if (!_isMenuTouched()) {
                    submenu.classList.remove('touched');
                    EDC.components.loggedInNavMenu.closeMenu();
                }
            }
        }

        function _handlePointerUp(e) {
            e.preventDefault();
            if (pointerMoved && (!isTablet || isIE)) {
                // pointer movement not significant enough
                if ((Math.abs(pointerNewPos[0] - pointerInitPos[0]) < 5) || (Math.abs(pointerNewPos[1] - pointerInitPos[1]) < 5)) {
                    pointerMoved = false;
                }
            }

            if (e.pointerType === 'mouse' && e.which === 1) {
                // mouse up
                _handleClick(e);
            } else if (e.pointerType === 'touch') {
                //  touch up
                _handleMenuTouched(e);
            }
        }

        function _handleTouchStart() {
            pointerMoved = false;
        }

        function _handleTouchMove() {
            pointerMoved = true;
        }

        function _fillUserInfo() {
            var userInfoSection = element.querySelector('.user-info'),
                userName = userInfoSection.querySelector('.user-name'),
                userEmail = userInfoSection.querySelector('.user-email');

            userName.textContent = EDC.props.userData.firstName + ' ' + EDC.props.userData.lastName;
            userEmail.textContent = EDC.props.userData.email;
        }

        function _setPropertiesForScroll(topNavHeight, windowHeight) {
            if ((!_isMobile() && !_isTablet()) || isIE) {
                if ((submenuInitialHeight + topNavHeight) > windowHeight) {
                    submenu.classList.add('show-scroll');
                    navColumn.style.height = (windowHeight - topNavHeight - 100) + 'px';
                } else {
                    submenu.classList.remove('show-scroll');
                    navColumn.style.height = 'auto';
                }
            }
        }

        function _activateScrollOnSubmenu() {
            var windowHeight = window.innerHeight,
                topNavHeight = topNav.clientHeight;

            if (firstLoad) {
                if (isIE) {
                    setTimeout(function () {
                        submenuInitialHeight = submenu.clientHeight;
                        _setPropertiesForScroll(topNavHeight, windowHeight);
                    }, 1000);
                } else {
                    submenuInitialHeight = submenu.clientHeight;
                    _setPropertiesForScroll(topNavHeight, windowHeight);
                }
                firstLoad = false;
            } else {
                _setPropertiesForScroll(topNavHeight, windowHeight);
            }
        }

        function _closeBtnActivated(e) {
            var keyCode = e.keyCode || e.which;

            if ((keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode) && (_isTablet() || _isMobile())) {
                _handleKeyPressed(e);
            }
        }

        // Attach events
        function _attachEvents() {
            if (window.PointerEvent) {
                EDC.utils.attachEvents(anchor, 'pointerdown', _handlePointerDown);
                EDC.utils.attachEvents(anchor, 'pointermove', _handlePointerMoved);
                EDC.utils.attachEvents(anchor, 'pointerup', _handlePointerUp);
                EDC.utils.attachEvents(anchor, 'click', _doNothing);
                EDC.utils.attachEvents(anchor, 'pointerover', _handlePointerOver);
                EDC.utils.attachEvents(element, 'pointerleave', _handlePointerOut);
            } else if (window.TouchEvent) {
                EDC.utils.attachEvents(anchor, 'touchstart', _handleTouchStart);
                EDC.utils.attachEvents(anchor, 'touchmove', _handleTouchMove);
                EDC.utils.attachEvents(anchor, 'touchend', _handleMenuTouched);
            } else {
                EDC.utils.attachEvents(anchor, 'mouseover', _openMenu);
                EDC.utils.attachEvents(element, 'mouseleave', EDC.components.loggedInNavMenu.closeMenu);
                EDC.utils.attachEvents(anchor, 'click', _handleClick);
            }

            EDC.utils.attachEvents(closeBtn, 'click', EDC.components.loggedInNavMenu.closeMenu);
            EDC.utils.attachEvents(navToggle, 'click', _openMenu);
            EDC.utils.attachEvents(anchor, 'keydown', _handleKeyPressed);
            EDC.utils.attachEvents(closeBtn, 'keydown', _closeBtnActivated);
            EDC.utils.attachEvents(navToggle, 'keydown', _handleKeyPressed);

            EDC.utils.attachEvents(submenu, 'keydown', function (e) {
                EDC.utils.enableFocusTrap(e, submenuAnchors, [navToggle, anchor], EDC.components.loggedInNavMenu.closeMenu);
            });

            EDC.utils.attachEvents(window, 'keydown', function (e) {
                EDC.utils.preventingKeydownForAccessibility(e, _isMenuOpen());
            });

            if (topNav) {
                EDC.utils.attachEvents(window, 'load', _activateScrollOnSubmenu);
                EDC.utils.attachEvents(window, 'resize', function () {
                    if (windowInnerWidth !== window.innerWidth) {
                        isTablet = _isTablet();

                        if (!isTablet || isIE) {
                            submenu.classList.remove('touched');
                        }

                        _activateScrollOnSubmenu();
                    }

                    windowInnerWidth = window.innerWidth;
                });
            }
        }

        PubSub.subscribe('user-status', function (name, data) {
            if (data) {
                element.classList.add('myedc-logged-in');
                submenu.classList.remove('hide');
                _fillUserInfo();
            } else if (element.classList.contains('myedc-logged-in')) {
                element.classList.remove('myedc-logged-in');
            }
        });

        function _initLogout() {
            var currentPath = window.location.pathname;

            if (currentPath.indexOf('my-account') === -1 && currentPath.indexOf('mon-compte') === -1) {
                signoutBtn.href = signoutBtn.href.slice(0, signoutBtn.href.indexOf('=') + 1) + currentPath + '?logout=true';
            }
        }

        isTablet = _isTablet();
        isIE = _isIE();
        _initLogout();
        _attachEvents();
    }

    // Public methods
    function init() {
        var userMenu = document.querySelectorAll('.c-user-menu:not([data-component-state="initialized"])');

        if (userMenu) {
            userMenu.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
            if (Object.keys(EDC.props.userData).length > 1) {
                PubSub.publish('user-status', true);
            } else {
                EDC.utils.userStatus();
            }
        }
    }

    return {
        init: init
    };
})();


// Exposing a global function to be used while opening/closing other menus
EDC.components.loggedInNavMenu = new function () {
    'use strict';

    this.closeMenu = function () {
        var el = document.querySelector('.c-user-menu'),
            submenu = el ? el.querySelector('.submenu') : null,
            mobileTitle = submenu ? submenu.querySelector('.mobile-title') : null,
            isTablet = window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg,
            isMobile = window.innerWidth < window.EDC.props.media.md;

        if (submenu) {
            submenu.classList.remove('hover');

            setTimeout(function () {
                if (!EDC.utils.elContainsCl(submenu, 'hover')) {
                    submenu.classList.add('hidden-for-accessibility');
                }
            }, isTablet || isMobile ? 800 : 0);
        }

        if (mobileTitle) {
            mobileTitle.classList.remove('hover');
        }
    };
};

loggedInNavMenuJS.init();