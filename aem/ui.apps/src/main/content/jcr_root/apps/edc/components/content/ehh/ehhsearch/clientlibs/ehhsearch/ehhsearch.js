var exportHelpHubSearch = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {

        function _submitEvent(e) {
            var event,
                resultComponent = document.querySelector('.c-ehh-search-results'),
                form = element.querySelector('form'),
                input = form ? form.querySelector('input[type="text"]') : '',
                query = form ? encodeURIComponent(input.value) : '',
                escapedQuery = query
                    .replace(/\(/g, '%28')
                    .replace(/\)/g, '%29')
                    .replace(/\%26/g, '&')
                    .replace(/\%24/g, '$')
                    .replace(/\%22/g, '”'), // encode and replace parentheses and other symbols to avoid the resquest being blocked by the dispatcher
                destinationPage = input ? form.action + '?' + input.name + '=' + escapedQuery : '';

            e.preventDefault();
            if (resultComponent) {
                event = new CustomEvent('searchQuery', {
                    detail: {
                        query: query,
                        event: element.dataset.eventType + ' - ' + element.dataset.eventName
                    }
                });
                resultComponent.dispatchEvent(event);
            } else if (form) {
                window.open(destinationPage, form.target);
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(element, 'submit', _submitEvent);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var ehhSearch = document.querySelectorAll('.c-ehh-search:not([data-component-state="initialized"])');

        if (ehhSearch) {
            ehhSearch.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', exportHelpHubSearch.init);