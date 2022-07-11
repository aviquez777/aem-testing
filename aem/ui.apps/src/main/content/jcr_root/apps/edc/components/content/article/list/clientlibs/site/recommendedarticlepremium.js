var recommendedArticlePremiumJS = (function () {
    'use strict';

    function _initialize(element) {
        var pParent = element.querySelector('.description-text'),
            anchor = pParent.querySelector('a.ra-premium'),
            btn = element.querySelector('a.edc-primary-btn'),
            pElement = pParent.querySelector('.description .small'),
            pHeight;

        // Tracking purposes
        function _trackEvent(e) {
            var clickedEl = e.currentTarget,
                parentEl = clickedEl.closest('.recommended-article-premium'),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: clickedEl.dataset.eventType,
                        eventName: clickedEl.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventText: clickedEl.dataset.eventText ? clickedEl.dataset.eventText : clickedEl.querySelector('span').innerHTML,
                        eventValue: parentEl.dataset.eventValue,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: anchor.getAttribute('href').toLowerCase(),
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: clickedEl.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _removeTextFade() {
            pHeight = parseInt((window.getComputedStyle(pElement).height).replace('px', ''), 10);
            if (pHeight <= 120) {
                pParent.classList.add('no-fade');
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(anchor, 'click', _trackEvent);
            EDC.utils.attachEvents(btn, 'click', _trackEvent);
        }

        _attachEvents();
        setTimeout(function () {
            _removeTextFade();
        }, 750);
    }

    // Public methods
    function init() {
        var rap = document.querySelectorAll('.recommended-articles-premium-wrapper:not([data-component-state="initialized"])');

        if (rap) {
            rap.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', recommendedArticlePremiumJS.init);