var businessConnectionsLoginButtonJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var defaultContent = element.querySelector('.default-content'),
            loggedinContent = element.querySelector('.loggedin-content.hide');

        // Private methods
        function _checkUserStatus() {
            PubSub.subscribe('user-status', function (name, isLoggedIn) {
                if (isLoggedIn) {
                    defaultContent.classList.add('hide');

                    if (loggedinContent) {
                        loggedinContent.classList.remove('hide');
                    }
                } else {
                    if (loggedinContent) {
                        loggedinContent.classList.add('hide');
                    }

                    defaultContent.classList.remove('hide');
                }
            });
        }

        _checkUserStatus();
    }

    // Public methods
    function init() {
        var businessConnectionsLoginButtons = document.querySelectorAll('.c-business-connections-login-button:not([data-component-state="initialized"])');

        if (businessConnectionsLoginButtons) {
            businessConnectionsLoginButtons.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', businessConnectionsLoginButtonJS.init);