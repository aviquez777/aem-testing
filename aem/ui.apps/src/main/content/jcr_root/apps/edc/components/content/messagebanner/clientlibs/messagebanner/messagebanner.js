var messageBannerJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var mainErrorMsgcloseBtn = element.querySelector('.close-button'),
            errorModal = element.querySelector('.error-modal'),
            errorModalCloseBtn = errorModal ? errorModal.querySelector('.modal-close') : null;

        function _closeErrorMsg() {
            element.classList.add('hide');
        }

        function _showErrorModal() {
            errorModal.classList.remove('hide');
            element.classList.add('bring-front');
        }

        function _closeErrorModal() {
            errorModal.classList.add('hide');
            element.classList.remove('bring-front');
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(element.querySelector('.gdpr-error button'), 'click', _showErrorModal);
            EDC.utils.attachEvents(errorModalCloseBtn, 'click', _closeErrorModal);
            EDC.utils.attachEvents(mainErrorMsgcloseBtn, 'click', _closeErrorMsg);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var messageBanners = document.querySelectorAll('.c-message-banner:not([data-component-state="initialized"])');

        if (messageBanners) {
            messageBanners.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', messageBannerJS.init);