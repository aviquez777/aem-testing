var headerNavInit = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var body = document.querySelector('body'),
            d = document,
            megaNav = element.querySelector('.mega-nav'),
            bottomNav = element.querySelector('.bottom-nav'),
            navToggle = element.querySelector('.header-nav .nav-toggle'),
            closeMenuBtns = element.querySelectorAll('.mega-nav .close'),
            goBackBtns = element.querySelectorAll('.mega-nav .back'),
            skipBtn = document.querySelector('#skip'),
            mainContent,
            submenuAnchors = [];

        // Private methods
        function _isMenuOpen() {
            return EDC.utils.elContainsCl(megaNav, 'show');
        }

        function _isTablet() {
            return window.innerWidth > EDC.props.media.lg && navigator.userAgent.match(/Tablet|iPad/i);
        }

        function _isMobile() {
            return window.innerWidth < EDC.props.media.md;
        }

        function _goBack(e) {
            e.target.parentNode.parentNode.parentNode.classList.remove('show');

            if (megaNav) {
                megaNav.classList.remove('fixed');
            }
        }

        function _closeParent(e) {
            var eTarget = e.currentTarget,
                parent = eTarget ? eTarget.closest('.level-0') : null;

            if (parent) {
                parent.classList.remove('hover');
            }
        }

        function _goBackKeyPressed(e) {
            var keyCode = e.keyCode || e.which,
                isSpaceOrEnter = keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode;

            if (isSpaceOrEnter) {
                _goBack(e);
                _closeParent(e);
                EDC.utils.focusingOnFirstElementForAccessibility(e, submenuAnchors);
            }
        }

        function _openMenu(e) {
            var eTarget = e.target,
                eType = e.type;

            e.preventDefault();
            // if LoggedIn Nav is present and open, close it
            if (eType === 'click' && document.querySelector('.loggedin-nav') && typeof (EDC.components.loggedInNavMenu) !== 'undefined') {
                EDC.components.loggedInNavMenu.closeMenu(e);
            }

            body.classList.add('open-nav');

            if (megaNav) {
                megaNav.classList.add('show');
                megaNav.classList.remove('hidden-for-accessibility');
                body.style.height = megaNav.offsetHeight + 'px';
            }

            if (bottomNav) {
                bottomNav.classList.add('show');
            }

            if (eTarget) {
                eTarget.setAttribute('aria-expanded', 'true');
            }

            if (submenuAnchors.length === 0) {
                megaNav.querySelectorAll('a.sub-activate, a.bottom-items, a.language-toggle, button.trigger-modal-search, .level-0.mobile-nav > button.close, .mobile-title > button.close').forEach(
                    function (thisAnchor) {
                        if (thisAnchor.offsetWidth > 0 && thisAnchor.offsetHeight > 0) {
                            submenuAnchors.push(thisAnchor);
                        }
                    });
            }

            EDC.utils.focusingOnFirstElementForAccessibility(e, submenuAnchors);
        }

        function _handleKeyPressed(e) {
            var keyCode = e.keyCode || e.which,
                isCloseBtn = e.currentTarget.classList.contains('close'),
                isSpaceOrEnter = keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode;

            if (isSpaceOrEnter || keyCode === EDC.props.arrowDownKeyCode) {
                e.preventDefault();

                if (isCloseBtn && isSpaceOrEnter) {
                    EDC.components.headerNav.closeMenu();
                } else {
                    _openMenu(e);
                }
            }
        }

        function _closeBtnActivated(e) {
            var keyCode = e.keyCode || e.which;

            if ((keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode) && (_isTablet() || _isMobile())) {
                _handleKeyPressed(e);
                _closeParent(e);
            }
        }

        function _initComponent() {
            EDC.utils.attachEvents(navToggle, 'click', _openMenu);
            EDC.utils.attachEvents(navToggle, 'keydown', _handleKeyPressed);

            closeMenuBtns.forEach(function (closeMenuBtn) {
                EDC.utils.attachEvents(closeMenuBtn, 'click', EDC.components.headerNav.closeMenu);
                EDC.utils.attachEvents(closeMenuBtn, 'keydown', _closeBtnActivated);
            });

            if (typeof (goBackBtns) !== 'undefined' && goBackBtns !== null && goBackBtns.length > 0) {
                goBackBtns.forEach(function (goBackBtn) {
                    EDC.utils.attachEvents(goBackBtn, 'click', _goBack);
                    EDC.utils.attachEvents(goBackBtn, 'keydown', _goBackKeyPressed);
                });
            }

            if (typeof (skipBtn) !== 'undefined') {
                EDC.utils.attachEvents(skipBtn, 'click', function (e) {
                    e.preventDefault();
                    mainContent = document.querySelector('#maincontent');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(mainContent).y);

                    //     // when focus leaves this element,
                    //     // remove the tabindex attribute
                    EDC.utils.attachEvents(mainContent, 'blur', function () {
                        mainContent.removeAttribute('tabindex');
                    });

                    // // Setting 'tabindex' to -1 takes an element out of normal
                    // // tab flow but allows it to be focused via javascript
                    mainContent.setAttribute('tabindex', -1);
                    mainContent.focus();
                });
            }
        }

        function _prepareModal() {
            var searchModal = d.querySelector('.c-modal-search'),
                modalInput = searchModal.querySelector('.cmp-search__input');

            if (modalInput) {
                setTimeout(function () {
                    modalInput.focus();
                    PubSub.publish('search-modal', true);
                }, 100);
            }
        }

        function _attachEvents() {
            var searchBtn = element.querySelectorAll('.trigger-modal-search');

            EDC.utils.attachEvents(searchBtn, 'click', _prepareModal);

            EDC.utils.attachEvents(window, 'keydown', function (e) {
                if (_isTablet() || _isMobile()) {
                    EDC.utils.preventingKeydownForAccessibility(e, _isMenuOpen());
                }
            });

            EDC.utils.attachEvents(megaNav, 'keydown', function (e) {
                if (_isTablet() || _isMobile()) {
                    EDC.utils.enableFocusTrap(e, submenuAnchors, navToggle, EDC.components.headerNav.closeMenu);
                }
            });
        }

        _initComponent();
        _attachEvents();
    }

    // Public methods
    function init() {
        var mainHeader = document.querySelectorAll('.main-header:not([data-component-state="initialized"])');

        if (mainHeader) {
            mainHeader.forEach(function (element) {
                _initialize(element);
                element.setAttribute('data-component-state', 'initialized');
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
    function closeMenu(e) {
        var body = document.querySelector('body'),
            megaNav = document.querySelector('.mega-nav'),
            navToggle = document.querySelector('.header-nav .nav-toggle'),
            bottomNav = document.querySelector('.bottom-nav'),
            openSubMenu = document.querySelector('.submenu.show'),
            isTablet = window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg,
            isMobile = window.innerWidth < window.EDC.props.media.md;

        if (e) {
            e.preventDefault();
        }

        body.classList.remove('show');

        if (megaNav) {
            megaNav.classList.remove('fixed');
            megaNav.classList.remove('show');

            setTimeout(function () {
                if (!EDC.utils.elContainsCl(megaNav, 'show')) {
                    megaNav.classList.add('hidden-for-accessibility');
                }
            }, isTablet || isMobile ? 350 : 0);
        }

        if (bottomNav) {
            bottomNav.classList.remove('show');
        }

        if (typeof (openSubMenu) !== 'undefined' && openSubMenu !== null) {
            openSubMenu.classList.remove('show');
        }

        if (navToggle) {
            navToggle.setAttribute('aria-expanded', 'false');
            navToggle.focus();
        }

        body.classList.remove('open-nav');
        body.style.height = 'auto';
    }

    return {
        closeMenu: closeMenu
    };
})();