var ProductCardJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var ctaBtns = element.querySelectorAll('.product-card footer a');

        // Data Layer
        function _trackEvent(e, data) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: data.name,
                    eventAction: element.closest('section') ? element.closest('section').className : '',
                    eventText: data.text + ' : ' + data.product,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: data.level
                }
            };

            if (data.destinationPage) {
                obj.eventInfo.destinationPage = data.destinationPage;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _clickCta(e) {
            var linkElement = e.currentTarget,
                productName = linkElement.closest('.product-card').querySelector('header .title');

            _trackEvent(e, {
                name: linkElement.dataset.eventName,
                text: linkElement.textContent,
                level: linkElement.dataset.eventLevel,
                product: productName ? productName.textContent.trim() : '',
                destinationPage: linkElement.getAttribute('href')
            });
        }

        function _attachEvents() {
            EDC.utils.attachEvents(ctaBtns, 'click', _clickCta);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var productCards = document.querySelectorAll('.product-card:not([data-component-state="initialized"])');

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

document.addEventListener('DOMContentLoaded', ProductCardJS.init);