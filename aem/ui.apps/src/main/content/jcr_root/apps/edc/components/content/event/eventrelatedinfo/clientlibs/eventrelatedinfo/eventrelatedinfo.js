var eventRelatedInfoJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods

        function _showHideAudience() {
            var eventAudience = element.querySelector('.event-audience');

            if (eventAudience) {
                if (window.EDC.utils.getDeviceViewPort() === 'mobile') {
                    eventAudience.classList.add('hide');
                } else {
                    eventAudience.classList.remove('hide');
                }
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(window, 'resize', _showHideAudience);
        }

        _attachEvents();
        _showHideAudience();
    }

    // Public methods
    function init() {
        var eventRelatedInfo = document.querySelectorAll('.c-event-related-info:not([data-component-state="initialized"])');

        if (eventRelatedInfo) {
            eventRelatedInfo.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

// This should be uncommented to load component on AEM
document.addEventListener('DOMContentLoaded', eventRelatedInfoJS.init);