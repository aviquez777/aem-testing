var testimonialSlider = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var slider;

        // Private functions
        function _moveDots() {
            var dots = element.querySelector('.tns-nav'),
                currentSlide = element.querySelector('.tns-slide-active .container');

            if (dots && currentSlide) {
                dots.style.top = currentSlide.offsetHeight + 'px';
            }
        }

        function _initSlider() {
            var sliderContainer = element.querySelector('.testimonial-slider');

            if (sliderContainer) {
                slider = tns({
                    container: sliderContainer,
                    items: 1,
                    mode: 'gallery',
                    arrowKeys: true,
                    slideBy: 'page',
                    animateIn: 'tns-fadeIn',
                    autoHeight: true,
                    autoplay: false,
                    mouseDrag: true,
                    speed: 1200,
                    loop: false,
                    rewind: false,
                    onInit: _moveDots
                });

                // bind function to event
                slider.events.on('indexChanged', _moveDots);

                // Re calculate the height of the slider.
                setTimeout(function () {
                    _moveDots();
                }, 100);
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(window, 'resize', _moveDots);
        }

        _initSlider();
        _attachEvents();
    }

    // Public methods
    function init() {
        var testimonial = document.querySelectorAll('.c-testimonial:not([data-component-state="initialized"])');
        console.warn("hello")
        if (testimonial) {
            testimonial.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

// This should be uncommented to load component on AEM
document.addEventListener('DOMContentLoaded', testimonialSlider.init);