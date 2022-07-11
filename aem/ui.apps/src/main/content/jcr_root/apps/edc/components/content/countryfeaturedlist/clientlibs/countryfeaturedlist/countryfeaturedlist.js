var featuredCountriesToggleView = (function () {
    'use strict';

    function setToggleVisibility(e) {
        var mainContainer = e.target.closest('.featured-countries');

        e.preventDefault();

        mainContainer.querySelector('.active-view').classList.toggle('hide');
        mainContainer.querySelector('.default-view').classList.toggle('hide');
    }

    function trackEvent(e) {
        var componentContainer = e.target.closest('.featured-countries'),
            href = e.target.getAttribute('href'),
            destinationPage = href.indexOf('http') === 0 ? e.target.getAttribute('href') : location.protocol + '//' + location.hostname + (href.indexOf('/') === 0 ? href : '/' + href),
            obj = {
                eventInfo: {
                    eventComponent: componentContainer.dataset.eventComponent,
                    eventType: componentContainer.dataset.eventType,
                    eventName: componentContainer.dataset.eventType + ' ' + e.type,
                    eventAction: componentContainer.dataset.eventAction,
                    eventText: e.target.textContent.toLowerCase(),
                    eventValue: '',
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: destinationPage,
                    engagementType: componentContainer.dataset.eventEngagement
                }
            };

        EDC.utils.dataLayerTracking(obj);
    }

    function attachEvents(elem) {
        var closebtns = elem.querySelectorAll('.closetoggle'),
            countries = elem.querySelectorAll('.active-view article ul li a'),
            i;

        for (i = 0; i < closebtns.length; i++) {
            closebtns[i].addEventListener('click', setToggleVisibility, false);
        }

        for (i = 0; i < countries.length; i++) {
            countries[i].addEventListener('click', trackEvent, false);
        }
    }

    // Public methods
    function init() {
        var featuredCountries = document.querySelectorAll('.featured-countries'),
            i;

        if (featuredCountries) {
            for (i = 0; i < featuredCountries.length; i++) {
                attachEvents(featuredCountries[i]);
            }
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', featuredCountriesToggleView.init);