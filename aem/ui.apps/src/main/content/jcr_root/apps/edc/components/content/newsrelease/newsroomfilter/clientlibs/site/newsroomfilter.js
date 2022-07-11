var newsroomFilterJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private Variables
        var select = element.querySelector('select'),
            news = element.querySelectorAll('li.news');

        // Data Layer
        function _trackEvent(e, data) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName,
                    eventAction: element.dataset.eventAction,
                    eventText: data.text,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: element.dataset.eventEngagement
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _changeValues(e) {
            var val = select.value;

            news.forEach(function (item) {
                item.classList.remove('hide');

                if (item.getAttribute('data-year') !== val) {
                    item.classList.add('hide');
                }
            });

            _trackEvent(e, { text: val });
        }

        // Attach events
        function _attachEvents() {
            if (select) {
                EDC.utils.attachEvents(select, 'change', _changeValues);
            }
        }

        _attachEvents();
        _changeValues();
    }

    // Public methods
    function init() {
        var newsroomFilter = document.querySelectorAll('.c-newsroom-filter:not([data-component-state="initialized"])');

        if (newsroomFilter) {
            newsroomFilter.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', newsroomFilterJS.init);