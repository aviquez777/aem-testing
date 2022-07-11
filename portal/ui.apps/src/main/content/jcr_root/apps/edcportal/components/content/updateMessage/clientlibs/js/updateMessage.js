var successMessageJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var closeBtn = element.querySelector('.close-button'),
            param = element.dataset.eventtype;

        // Data Layer
        function _trackEvent() {
            var eventName = (function (eventValue) {
                    switch (eventValue) {
                        case 'basic':
                            return element.dataset.eventNameBasicInfo;
                        case 'password':
                            return element.dataset.eventNamePassword;
                        default:
                            return '';
                    }
                })(param),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventType + ' ' + eventName,
                        eventAction: element.dataset.eventAction,
                        eventText: '',
                        eventValue: param,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _showClass() {
            element.classList.remove('hide');
            _trackEvent();
        }

        function _closeMessage() {
            element.classList.add('hide');
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(closeBtn, 'click', _closeMessage);
        }

        if (param === 'password' || param === 'basic') {
            _showClass();
        }
        _attachEvents();
    }

    // Public methods
    function init() {
        var successMessages = document.querySelectorAll('.c-success-message:not([data-component-state="initialized"])');

        if (successMessages) {
            successMessages.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

successMessageJS.init();