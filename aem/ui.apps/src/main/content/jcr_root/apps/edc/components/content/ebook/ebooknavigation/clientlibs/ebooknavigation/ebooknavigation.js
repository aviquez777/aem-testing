var ebookNavigationJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var modal = document.querySelector('.modal-dialog.ebook-nav-modal-content'),
            modalCloseBtn = modal.querySelector('button'),
            modalTriggerSection = element.querySelector('.modal-section'),
            modalTriggerBtn = modalTriggerSection.querySelector('.modal-example');

        // Private methods
        function _closeModal() {
            modal.classList.remove('show');
            bodyScrollLock.enableBodyScroll(modal);
        }

        function _stickyEbookNav() {
            var btnPosition = modalTriggerSection.offsetTop || 0,
                posY = window.pageYOffset || document.documentElement.scrollTop || 0,
                hasStickyClass = modalTriggerBtn.className.indexOf('sticky') !== -1;

            if (btnPosition < posY && !hasStickyClass) {
                modalTriggerBtn.classList.add('sticky');
                modalTriggerSection.style.height = (modalTriggerBtn.offsetHeight) + 'px';
            } else if (btnPosition >= posY && hasStickyClass) {
                modalTriggerBtn.classList.remove('sticky');
                modalTriggerSection.style.height = 'auto';
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(window, 'scroll', function () {
                if (window.innerWidth < EDC.props.media.lg) {
                    _stickyEbookNav();
                }
            });
            EDC.utils.attachEvents(modalCloseBtn, 'click', _closeModal);
            EDC.utils.attachEvents(window, 'resize', function () {
                if (window.innerWidth > EDC.props.media.lg) {
                    _closeModal();
                }
            });
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var ebookNav = document.querySelectorAll('.c-ebook-navigation:not([data-component-state="initialized"])');

        if (ebookNav) {
            ebookNav.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ebookNavigationJS.init);