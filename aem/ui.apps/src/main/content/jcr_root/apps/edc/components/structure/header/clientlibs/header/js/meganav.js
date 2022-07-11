// Mega Nav Main Functionalinity for Desktop, Tablet, Mobile, and touch Devices
var megaNavInit = (function () {
    'use strict';

    function _initialize(elem) {
        var i,
            megaNav = elem,
            subMenus = megaNav.querySelectorAll('.submenu'),
            currentAnchors = [],
            megaNavUL = elem.querySelector('.mega-nav ul'),
            menuMainLinks = elem.querySelectorAll('.sub-activate'),
            pointerMoved = false,
            pointerInitPos = [0, 0],
            pointerNewPos = [0, 0];

        function _isMenuOpen() {
            return elem.querySelector('.level-0.hover');
        }

        function _closeAllSubMenus(e) {
            document.querySelectorAll('.mega-nav .hover').forEach(function (thisSubmenu) {
                thisSubmenu.classList.remove('hover');
            });

            document.querySelectorAll('.mega-nav .touched').forEach(function (thisSubmenu) {
                if (e.target.parentElement !== thisSubmenu) {
                    thisSubmenu.classList.remove('touched');
                }
            });
        }

        function _toggleTabletMenu(e) {
            var w = window.innerWidth,
                subMenu = e.currentTarget.parentNode.children[1],
                subMenuShown = document.getElementsByClassName('submenu show');

            e.preventDefault();
            // Tablet & Mobile
            if (subMenu.classList.contains('show')) {
                // Hide this SubMenu
                subMenu.classList.toggle('show');
            } else {
                // Hide previous opened submenu and show this one
                for (i = 0; i < subMenuShown.length; i++) {
                    subMenuShown[i].classList.toggle('show');
                }
                subMenu.classList.toggle('show');
            }

            // only applies for Mobile
            if (w <= EDC.props.media.md) {
                megaNav.classList.add('fixed');
            }
        }

        function _handleMenuTouched(e) {
            e.preventDefault();
            // if first touch, open this menu, close other menus
            // if second touch, follow link
            if (megaNav.classList.contains('show')) {
                if (!pointerMoved) {
                    _toggleTabletMenu(e);
                } else {
                    pointerMoved = false;
                }
            } else if (e.target.parentNode.classList.contains('touched')) {
                // 'second touch'
                window.location = e.target.href;
            } else {
                // 'first touch'
                _closeAllSubMenus(e);
                megaNavUL.classList.add('touched');
                e.target.parentNode.classList.add('touched');
                e.target.parentNode.classList.add('hover');
            }
        }

        function _handleMenuMouseover(e) {
            e.preventDefault();
            _closeAllSubMenus(e);
            e.target.parentNode.classList.add('hover');
        }

        function _handleMenuMouseout(e) {
            e.preventDefault();
            if (!e.target.parentNode.classList.contains('touched')) {
                megaNavUL.classList.remove('touched');
                e.target.parentNode.classList.remove('hover');
            }
        }

        function _handleMenuClicked(e) {
            e.preventDefault();

            // follow link
            if (megaNav.classList.contains('show')) {
                // IF MEGA NAV IS OPEN USER IS ON TABLET VIEW
                _toggleTabletMenu(e);
            } else {
                // USER IS ON DESKTOP VIEW // GO TO URL
                window.location = e.currentTarget.href;
            }
        }

        function _handlePointerDown(e) {
            e.preventDefault();
            pointerMoved = false;
            pointerInitPos = [e.screenX, e.screenY];
        }

        function _handlePointerMove(e) {
            pointerMoved = true;
            pointerNewPos = [e.screenX, e.screenY];
        }

        function _handlePointerUp(e) {
            e.preventDefault();
            if (pointerMoved) {
                // pointer movement not significant enough
                if ((Math.abs(pointerNewPos[0] - pointerInitPos[0]) < 5) || (Math.abs(pointerNewPos[1] - pointerInitPos[1]) < 5)) {
                    pointerMoved = false;
                }
            }

            if (e.pointerType === 'mouse') {
                // "mouse up"
                _handleMenuClicked(e);
            } else if (e.pointerType === 'touch') {
                // "touch up"
                _handleMenuTouched(e);
            }
        }

        function _doNothing(e) {
            e.preventDefault();
            return false;
        }

        function toggleAriaExpanded(el) {
            if (el) {
                if (el.getAttribute('aria-expanded') === 'false') {
                    el.setAttribute('aria-expanded', 'true');
                } else if (el.getAttribute('aria-expanded') === 'true') {
                    el.setAttribute('aria-expanded', 'false');
                }
            }
        }

        function _handleKeyPressed(e) {
            var eTarget = e.currentTarget,
                currentListElement = eTarget ? eTarget.parentNode : null,
                currentSubmenu = currentListElement ? currentListElement.querySelector('.submenu') : null,
                keyCode = e.keyCode || e.which,
                isMobileOrTablet = (window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg) || window.innerWidth < window.EDC.props.media.md;

            if (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode || (keyCode === EDC.props.arrowDownKeyCode && !isMobileOrTablet)) {
                e.preventDefault();

                // go to link if menu is submenu open (second time enter key is pressed)
                if ((currentListElement && currentListElement.classList.contains('hover') && keyCode === EDC.props.enterKeyCode) ||
                (!currentSubmenu && (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode))) {
                    window.location = e.currentTarget.href;
                } else if (currentSubmenu) {
                    // open submenu
                    _closeAllSubMenus(e);
                    currentListElement.classList.add('hover');
                    currentAnchors = [];

                    currentSubmenu.querySelectorAll('a, button.close, button.back').forEach(function (thisAnchor) {
                        if (thisAnchor.offsetWidth > 0 && thisAnchor.offsetHeight > 0) {
                            currentAnchors.push(thisAnchor);
                        }
                    });

                    EDC.utils.focusingOnFirstElementForAccessibility(e, currentAnchors);
                    toggleAriaExpanded(eTarget);
                }
            }
        }

        function _handleTouchStart() {
            pointerMoved = false;
        }

        function _handleTouchMoved() {
            pointerMoved = true;
        }

        function _attachEvents() {
            menuMainLinks.forEach(function (link) {
                if (window.PointerEvent) {
                    EDC.utils.attachEvents(link, 'pointerdown', _handlePointerDown);
                    EDC.utils.attachEvents(link, 'pointermove', _handlePointerMove);
                    EDC.utils.attachEvents(link, 'pointerup', _handlePointerUp);
                    EDC.utils.attachEvents(link, 'pointerover', _handleMenuMouseover);
                    EDC.utils.attachEvents(link, 'pointerout', _handleMenuMouseout);
                    EDC.utils.attachEvents(link, 'click', _doNothing);
                } else if (window.TouchEvent) {
                    EDC.utils.attachEvents(link, 'touchstart', _handleTouchStart);
                    EDC.utils.attachEvents(link, 'touchmove', _handleTouchMoved);
                    EDC.utils.attachEvents(link, 'touchend', _handleMenuTouched);
                } else {
                    EDC.utils.attachEvents(link, 'mouseover', _handleMenuMouseover);
                    EDC.utils.attachEvents(link, 'mouseout', _handleMenuMouseout);
                    EDC.utils.attachEvents(link, 'click', _handleMenuClicked);
                }

                EDC.utils.attachEvents(link, 'keydown', _handleKeyPressed);
            });

            EDC.utils.attachEvents(window, 'keydown', function (e) {
                EDC.utils.preventingKeydownForAccessibility(e, _isMenuOpen());
            });

            subMenus.forEach(function (submenu) {
                var mobileNav = submenu.querySelector('.mobile-nav > a');

                EDC.utils.attachEvents(submenu, 'transitionend', function (e) {
                    var eTarget = e.currentTarget;

                    if (eTarget && eTarget.classList.contains('show')) {
                        eTarget.classList.add('transitioned');
                    } else {
                        eTarget.classList.remove('transitioned');
                    }
                });

                EDC.utils.attachEvents(submenu, 'animationstart', function (e) {
                    var eTarget = e.currentTarget;

                    if (eTarget) {
                        eTarget.classList.remove('transitioned');
                    }
                });

                if (mobileNav) {
                    EDC.utils.attachEvents(mobileNav, 'click', function (e) {
                        var eTarget = e.currentTarget;

                        if (!eTarget.closest('.transitioned')) {
                            e.preventDefault();
                            eTarget.blur();
                        }
                    });
                }

                EDC.utils.attachEvents(submenu, 'keydown', function (e) {
                    var eTarget = e.currentTarget,
                        triggerer = eTarget.closest('.level-0').querySelector('.sub-activate');

                    EDC.utils.enableFocusTrap(e, currentAnchors, triggerer);
                });

            });
        }

        _attachEvents();
    }

    function init() {
        var megaNavs = document.querySelectorAll('.mega-nav:not([data-component-state="initialized"])');

        if (megaNavs) {
            megaNavs.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

megaNavInit.init();