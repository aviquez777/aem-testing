var oneTrustPrivacyButton = (function() {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var button = element.querySelectorAll('.button'), 
            link = element.querySelectorAll('.optanon-show-settings');

        function _clickOneTrust() {
            if (button && link) {
                link[0].click();
            }
        }

        EDC.utils.attachEvents(button, 'click', _clickOneTrust);
    }

    // Public methods
    function init() {
        var elements = document.querySelectorAll('.onetrustprivacybutton-container:not([data-component-state="initialized"])');

        if (elements) {
            elements.forEach(function(elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init : init
    };
})();

document.addEventListener('DOMContentLoaded', oneTrustPrivacyButton.init);
