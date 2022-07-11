var multipleVideoLineupJS = (function () {
    'use strict';

    function _initialize(element) {
        // Tracking purposes
        function _trackEvent(el) {
            var content = el.target.closest('.content-video'),
                anchor = el.target.closest('a'),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: anchor.dataset.eventName,
                        eventAction: content.dataset.eventAction, // order of link as it appears in the cluster
                        eventText: content.dataset.eventText,
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: anchor.getAttribute('href').toLowerCase(),
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _attachEvents() {
            var videos = element.querySelectorAll('.content-video a');

            EDC.utils.attachEvents(videos, 'click', _trackEvent);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var mvl = document.querySelectorAll('.multiple-video-lineup:not([data-component-state="initialized"])');

        if (mvl) {
            mvl.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', multipleVideoLineupJS.init);