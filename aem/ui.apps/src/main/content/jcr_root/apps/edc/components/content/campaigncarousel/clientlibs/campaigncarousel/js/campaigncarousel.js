var campaignProductCarouselJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var carousel = element.querySelector('.carousel');

        // Data Layer
        function _trackEvent(event) {
            var el = carousel,
                currentBullet,
                currentPosition,
                currentSlide,
                obj = {
                    eventInfo: {
                        eventComponent: el.dataset.eventComponent,
                        eventType: 'button',
                        eventName: 'button click-Carousel New Results',
                        eventAction: el.dataset.eventAction,
                        eventValue: '',
                        eventText: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: 1,
                        eventLevel: 1
                    }
                };

            if (event.path[2].nodeName.toLowerCase() === 'a') {
                obj.eventInfo.eventText = 'Carousel Result ' + event.path[2].getAttribute('data-slide-position');
                obj.eventInfo.eventValue = event.path[2].getAttribute('data-slide-position');
                obj.eventInfo.destinationPage = event.path[2].getAttribute('href');

            } else if (event.target.nodeName.toLowerCase() === 'button') {
                currentBullet = carousel.querySelector('.carousel-actions .carousel-ballons .ballon-for-slide.active');
                currentPosition = currentBullet.getAttribute('data-slide-position');
                currentSlide = carousel.querySelector('.carousel-slides .campaign-product-card[data-slide-position="' + currentPosition + '"]');
                obj.eventInfo.eventText = 'Carousel Result ' + currentPosition;
                obj.eventInfo.eventValue = currentPosition;
                obj.eventInfo.destinationPage = currentSlide.getAttribute('href');
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _activateCarousel() {
            EDC.utils.initCarousel(carousel, 3, 4);
        }

        function _attachEvents() {
            EDC.utils.attachEvents(carousel.querySelectorAll('.carousel-slides .campaign-product-card'), 'click', _trackEvent);
            EDC.utils.attachEvents(carousel.querySelectorAll('.carousel-actions .carousel-ballons .ballon-for-slide, .carousel-actions .carousel-arrows .carousel-button'), 'click', _trackEvent);
        }

        _attachEvents();
        _activateCarousel();
    }

    // Public methods
    function init() {
        var campaignProdCarousel = document.querySelectorAll('.c-campaign-product-carousel:not([data-component-state="initialized"])');

        if (campaignProdCarousel) {
            campaignProdCarousel.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', campaignProductCarouselJS.init);