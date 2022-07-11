var modalJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            body = document.querySelector('body'),
            device = window.EDC.utils.getDeviceViewPort(),
            modalDialog = element.querySelector('.modal-dialog'),
            selectedModal;

        // Private methods

        // Set focus on first interactive element on the modal.
        function _setFocus(modal) {
            var button = modal.querySelector('.modal-close');

            // Focus on interactive elementon the modal
            if (button) {
                button.focus();
            }
        }

        function _positionModal() {
            var modal = body.querySelector('.modal-dialog.show .modal-container'),
                modalEDC = body.querySelector('.modal-dialog.edc-popup'),
                viewportHeight,
                modalHeight,
                modalOffset = 0;

            if (modal) {
                viewportHeight = window.innerHeight;
                modalHeight = modal.clientHeight;

                if (viewportHeight > modalHeight) {
                    modalOffset = (viewportHeight - modalHeight) / 2;
                }
                if (device === 'mobile' && modalEDC) {
                    modal.style.top = '0px';
                } else {
                    modal.style.top = modalOffset + 'px';
                }
            }

            _setFocus(modal);
        }

        function _resetBody() {
            EDC.utils.unAttachEvents(window, 'resize', _positionModal);
            body.classList.remove('modal-is-open');
            if (selectedModal) {
                bodyScrollLock.enableBodyScroll(selectedModal);
            }
        }

        function _closeModalShade(e) {
            e.preventDefault();
            e.currentTarget.parentNode.classList.remove('show');
            _resetBody();
        }

        function _closeModal(e) {
            e.preventDefault();
            e.currentTarget.parentNode.parentNode.classList.remove('show');
            _resetBody();
        }

        // Detects Escape key pressed
        function _detectEsc(e) {
            var modalOpened = d.querySelector('.modal-dialog.show');

            if (e.keyCode === EDC.props.escapeKeyCode && modalOpened) {
                modalOpened.classList.remove('show');
                EDC.utils.unAttachEvents(d, 'keyup', _detectEsc);
                _resetBody();
            }
        }

        function _showModal(e) {
            var modalToTrigger = e.currentTarget.getAttribute('data-modal-to-trigger');

            e.preventDefault();

            selectedModal = d.getElementById(modalToTrigger);

            // Disables scrolling on body and html elements on all devices
            bodyScrollLock.disableBodyScroll(selectedModal);

            // Adds resize event to fix modal position
            EDC.utils.attachEvents(window, 'resize', _positionModal);

            // Adds keypress event to close modal
            EDC.utils.attachEvents(d, 'keyup', _detectEsc);

            // Show Modal via CSS
            selectedModal.classList.add('show');

            // Let body know that modal is open
            body.classList.add('modal-is-open');

            // Reposition modal on screen
            _positionModal();
        }

        function _addModalToBody() {
            body.appendChild(modalDialog);
        }

        // Attach events
        function _attachEvents() {
            var modalTriggers = d.querySelectorAll('.modal-trigger'),
                modalClose = element.querySelector('.modal-close'),
                modalShade = element.querySelector('.modal-shade');

            EDC.utils.attachEvents(modalTriggers, 'click', _showModal);
            EDC.utils.attachEvents(modalClose, 'click', _closeModal);
            EDC.utils.attachEvents(modalShade, 'click', _closeModalShade);
        }

        _attachEvents();
        _addModalToBody();
    }

    // Public methods
    function init() {
        var modals = document.querySelectorAll('.c-modal:not([data-component-state="initialized"])');

        if (modals) {
            modals.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', modalJS.init);