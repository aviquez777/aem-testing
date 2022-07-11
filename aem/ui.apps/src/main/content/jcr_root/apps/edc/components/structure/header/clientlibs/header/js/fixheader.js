// SHOW Top Nav on mobile Scroll Up
var fixHeaderInit = (function () {
    'use strict';

    function _initialize(elem) {
        var sticky = document.querySelector('.c-sticky-nav-wrapper'),
            d = document,
            body = d.querySelector('body'),
            timer,
            scrollTreshold = 80,
            oldScroll = 0,
            currentScroll = 0;

        function _fixMenuPosition(pos, y) {
            if (pos > y) {
                // scrolling up
                elem.classList.remove('scrolldown');

                if (sticky) {
                    sticky.classList.add('stickynav-extra-pad');
                    body.classList.add('stickynav-extra-pad');
                }
            } else {
                // scrolling down
                elem.classList.add('scrolldown');

                if (sticky) {
                    sticky.classList.remove('stickynav-extra-pad');
                    body.classList.remove('stickynav-extra-pad');
                }
            }
        }

        function _checkPosition() {
            oldScroll = currentScroll;

            if (timer) {
                clearTimeout(timer);
            }

            if ((window.innerWidth < EDC.props.media.md)) {
                timer = window.setTimeout(function () {
                    currentScroll = window.pageYOffset;

                    if (((currentScroll - oldScroll) > scrollTreshold) || ((oldScroll - currentScroll) > scrollTreshold)) {
                        _fixMenuPosition(oldScroll, currentScroll);
                    }
                }, 50);
            }
        }

        function _startTopNav() {
            elem.classList.remove('scrolldown');

            if (sticky) {
                sticky.classList.remove('stickynav-extra-pad');
                body.classList.remove('stickynav-extra-pad');
            }

            if ((window.innerWidth < EDC.props.media.md)) {
                if (sticky) {
                    sticky.classList.add('stickynav-extra-pad');
                    body.classList.add('stickynav-extra-pad');
                }
            }
        }

        function _addEventHandlers() {
            window.addEventListener('scroll', _checkPosition);
            window.addEventListener('resize', _startTopNav);
        }

        _startTopNav();
        _addEventHandlers();
    }

    function init() {
        var topNavs = document.querySelectorAll('.main-header .top-nav:not([data-component-state="initialized"])');

        if (topNavs) {
            topNavs.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

window.addEventListener('load', fixHeaderInit.init);