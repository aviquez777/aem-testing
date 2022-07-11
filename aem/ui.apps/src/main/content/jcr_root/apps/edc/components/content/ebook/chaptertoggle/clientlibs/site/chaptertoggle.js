var ebookSlider = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var navBtns,
            slides = element.querySelectorAll('.content-ebook'),
            arrowsClickCounter = 0,
            startIndex = element.dataset.startIndex;

        // Tracking function
        function _trackEvent(data) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: 'button click - ' + data.eventName,
                    eventAction: element.dataset.eventAction,
                    eventPageName: EDC.utils.getPageName(),
                    eventValue: data.eventValue,
                    eventPage: EDC.props.pageNameURL,
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: data.eventLevel
                }
            };

            if (data.eventValue2) {
                obj.eventInfo.eventValue2 = data.eventValue2;
            } else {
                obj.eventInfo.destinationPage = element.dataset.destinationPage;
                obj.eventInfo.eventText = data.eventText;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _btnTabIndex() {
            var btn;

            slides.forEach(function (item) {
                btn = item.querySelector('a.button');
                btn.setAttribute('tabIndex', '-1');
                if (item.classList.contains('tns-slide-active')) {
                    btn.setAttribute('tabIndex', '0');
                }
            });
        }

        function _btnClicked(e) {
            var btnText = e.currentTarget.querySelector('span').innerHTML;

            _trackEvent({
                eventName: btnText,
                eventValue: arrowsClickCounter,
                eventLevel: 'primary',
                eventText: btnText
            });
        }

        function _arrowsClicked(e) {
            var currentSlide = parseInt(element.querySelector('.content-ebook.tns-slide-active').dataset.eventValue, 10),
                arrow = e.currentTarget.getAttribute('data-controls') === 'prev' ? 'previous' : 'next',
                value2 = arrow === 'next' ? (currentSlide + 1) : (currentSlide - 1);

            _trackEvent({
                eventName: arrow + ' chapter',
                eventValue: 'chapter ' + currentSlide,
                eventValue2: 'chapter ' + value2,
                eventLevel: 'secondary'
            });
            arrowsClickCounter++;
        }

        function _attachEvents() {
            var btns = document.querySelectorAll('.content-ebook .edc-primary-btn');

            navBtns = element.querySelectorAll('button[data-controls]');

            EDC.utils.attachEvents(navBtns, 'click', _btnTabIndex);
            EDC.utils.attachEvents(element, 'keydown', _btnTabIndex);
            EDC.utils.attachEvents(navBtns, 'click', _arrowsClicked);
            EDC.utils.attachEvents(btns, 'click', _btnClicked);
        }

        function _orderNode() {
            var slidesList = element.querySelector('.tns-outer'),
                node1 = element.querySelector('.tns-inner'),
                node2 = element.querySelector('.tns-nav'),
                node3 = element.querySelector('.tns-controls');

            slidesList.insertBefore(node1, node3);
            slidesList.insertBefore(node2, node3);
        }

        function _sliderInit() {
            _attachEvents();
            _orderNode();
        }

        function _initSlider() {
            var sliderContainer = element.querySelector('.ebook-slider');

            if (sliderContainer) {
                tns({
                    container: sliderContainer,
                    startIndex: startIndex,
                    items: 1,
                    slideBy: 'page',
                    loop: false,
                    animateIn: 'tns-fadeIn',
                    speed: 1200,
                    sliderContainer: 1,
                    onInit: _sliderInit
                });
            }
        }

        _initSlider();
    }

    // Public methods
    function init() {
        var ebook = document.querySelectorAll('.c-ebook-chapter:not([data-component-state="initialized"])');

        if (ebook) {
            ebook.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ebookSlider.init);