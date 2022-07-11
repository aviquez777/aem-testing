// Mega Nav Main Functionalinity for Desktop, Tablet, Mobile, and touch Devices
var megaNavInit = (function () {
    'use strict';

    function _initialize(elem) {
        var i,
            megaNav = elem,
            mobileSubMenu = document.querySelectorAll('.mega-nav .submenu'),
            subMenus = megaNav.querySelectorAll('.submenu'),
            megaNavUL = document.querySelector('.mega-nav ul'),
            menuMainLinks = document.querySelectorAll('.sub-activate'),
            pointerMoved = false,
            previousFocus = null,
            pointerInitPos = [0, 0],
            pointerNewPos = [0, 0];

        function _closeAllSubMenus(e) {
            var openedSubMenu;

            openedSubMenu = document.querySelectorAll('.mega-nav .hover');
            if (openedSubMenu.length > 0) {
                for (i = 0; i < openedSubMenu.length; i++) {
                    openedSubMenu[i].classList.remove('hover');
                }
            }

            openedSubMenu = document.querySelectorAll('.mega-nav .touched');
            if (openedSubMenu.length > 0) {
                for (i = 0; i < openedSubMenu.length; i++) {
                    if (e.target.parentElement !== openedSubMenu[i]) {
                        openedSubMenu[i].classList.remove('touched');
                    }
                }
            }
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
                // var scrollPos = document.querySelector('.mega-nav');
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
            return;
        }

        // *****
        // This menu will support navigation using keyboard
        //
        // MENU
        // TAB: select next menu element
        // SHIFT + TAB: select previous menu element
        // ENTER(*), SPACE,  ARROW-DOWN: will open the submenu
        // ESC, ARROW-UP: will close the submenu
        // ARROW-RIGHT: will select next menu element
        // ARROW-LEFT: will select previous menu element.
        // (*) if the user wants to navigate to main page (our solutions), second hit on ENTER will go to URL
        //
        // SUBMENU
        // TAB: select next element
        // SHIFT TAB: select previous element
        // ESC: close submenu
        // ******

        function _handleKeyPressed(event) {
            var handled = false,
                currentListElement = event.currentTarget.parentNode,
                previousMenuItem,
                nextMenuItem;

            event.preventDefault();

            if (event.keyCode !== undefined) {
                // Handle the event with KeyboardEvent.keyCode and set handled true.
                handled = true;

                // If enter, space or arrow down is used
                if (event.keyCode === EDC.props.enterKeyCode ||
                    event.keyCode === EDC.props.spaceKeyCode ||
                    event.keyCode === EDC.props.arrowDownKeyCode) {

                    // go to link if menu is submenu open (second time enter key is pressed)
                    if (currentListElement.classList.contains('hover') && event.keyCode === EDC.props.enterKeyCode) {
                        window.location = event.currentTarget.href;
                    } else {
                        // open submenu
                        _closeAllSubMenus(event);
                        previousFocus = event.currentTarget;
                        currentListElement.classList.add('hover');
                    }
                } else if (event.keyCode === EDC.props.escapeKeyCode || event.keyCode === EDC.props.arrowUpKeyCode) {
                    _closeAllSubMenus(event);
                } else if (event.keyCode === EDC.props.arrowRightCode) {
                    // navigate to next element
                    nextMenuItem = currentListElement.nextElementSibling.querySelector('a');
                    if (typeof (nextMenuItem) !== 'undefined' && nextMenuItem !== null) {
                        nextMenuItem.focus();
                    }
                } else if (event.keyCode === EDC.props.arrowLeftCode) {
                    // navigate to previous element
                    previousMenuItem = currentListElement.previousElementSibling.querySelector('a');
                    if (typeof (previousMenuItem) !== 'undefined' && previousMenuItem !== null) {
                        previousMenuItem.focus();
                    }
                }
            }

            if (handled) {
                // Suppress "double action" if event handled
                event.preventDefault();
            }
        }

        function _handleTouchStart() {
            pointerMoved = false;
        }

        function _handleTouchMoved() {
            pointerMoved = true;
        }

        function handleSubMenuKeyboard(event) {
            if (event.keyCode !== undefined) {

                // If enter, space or arrow down is used
                if (event.keyCode === EDC.props.escapeKeyCode) {
                    event.preventDefault();
                    _closeAllSubMenus(event);
                    if (previousFocus !== null) {
                        previousFocus.focus();
                        previousFocus = null;
                    }
                }
            }
        }

        function _addEventHandlers() {
            for (i = 0; i < menuMainLinks.length; i++) {
                if (window.PointerEvent) {
                    menuMainLinks[i].addEventListener('pointerdown', _handlePointerDown);
                    menuMainLinks[i].addEventListener('pointermove', _handlePointerMove);
                    menuMainLinks[i].addEventListener('pointerup', _handlePointerUp);
                    menuMainLinks[i].addEventListener('pointerover', _handleMenuMouseover);
                    menuMainLinks[i].addEventListener('pointerout', _handleMenuMouseout);
                    menuMainLinks[i].addEventListener('keyup', _handleKeyPressed);
                    menuMainLinks[i].addEventListener('click', _doNothing);
                } else if (window.TouchEvent) {
                    menuMainLinks[i].addEventListener('touchstart', _handleTouchStart);
                    menuMainLinks[i].addEventListener('touchmove', _handleTouchMoved);
                    menuMainLinks[i].addEventListener('touchend', _handleMenuTouched);
                    menuMainLinks[i].addEventListener('keyup', _handleKeyPressed);
                } else {
                    menuMainLinks[i].addEventListener('mouseover', _handleMenuMouseover);
                    menuMainLinks[i].addEventListener('mouseout', _handleMenuMouseout);
                    menuMainLinks[i].addEventListener('click', _handleMenuClicked);
                    menuMainLinks[i].addEventListener('keyup', _handleKeyPressed);
                }
            }

            Array.prototype.forEach.call(subMenus, function (submenu) {
                submenu.addEventListener('keyup', handleSubMenuKeyboard);
            });

            Array.prototype.forEach.call(mobileSubMenu, function (submenu) {
                submenu.addEventListener('transitionend', function (e) {
                    if (e.currentTarget.classList.contains('show')) {
                        e.currentTarget.classList.add('transitioned');
                    } else {
                        e.currentTarget.classList.remove('transitioned');
                    }
                });
                submenu.addEventListener('animationstart', function (e) {
                    e.currentTarget.classList.remove('transitioned');
                });
                submenu.querySelector('.mobile-nav > a').addEventListener('click', function (e) {
                    if (!e.currentTarget.parentNode.parentNode.classList.contains('transitioned')) {
                        e.preventDefault();
                        e.currentTarget.blur();
                    }
                });
            });
        }

        _addEventHandlers();
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