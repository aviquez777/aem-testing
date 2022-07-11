var productCardClick = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var button = element.querySelector('footer .button');

        // Data Layer
        function _trackEvent(e, data) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: data.name,
                    eventAction: element.dataset.eventAction,
                    eventText: data.text,
                    eventValue: '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: element.dataset.eventEngagement,
                    eventStage: '',
                    destinationPage: data.destinationPage,
                    eventLevel: element.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _handleClick(e) {
            var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth,
                linkElement = e.currentTarget,
                linkName = linkElement.querySelector('a.edc-primary-btn'),
                productName = element.querySelector('.title');

            _trackEvent(e, {
                name: linkName.dataset.eventName,
                text: linkName.textContent + ' : ' + productName.textContent,
                destinationPage: linkName.getAttribute('href')
            });

            if (w <= EDC.props.media.md) {
                button.click();
            }
        }

        function _handleKeyboard(e) {
            switch (e.keyCode) {
                case EDC.props.enterKeyCode:
                    button.click();
                    break;

                case EDC.props.spaceKeyCode:
                    button.click();
                    break;

                default:
                    return;
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(element, 'click', _handleClick);
            EDC.utils.attachEvents(element, 'keyup', _handleKeyboard);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var productCards = document.querySelectorAll('.c-product-card-banker.banker:not([data-component-state="initialized"])');

        if (productCards) {
            productCards.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', productCardClick.init);