var sciInterstitialJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var body = document.querySelector('body'),
            d = document,
            modalDialog = element.querySelector('.modal-section'),
            closeBtn = modalDialog.querySelector('.modal-close'),
            device = window.EDC.utils.getDeviceViewPort(),
            btn = element.querySelector('.modal-actions a.button');

        // Data Layer
        function _trackEvent(e) {
            var el = e.currentTarget,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: 'button',
                        eventName: 'button click - ' + (el === btn ? 'get a quote' : 'close popup'),
                        eventAction: element.dataset.eventAction,
                        eventText: el === btn ? btn.querySelector('span').textContent.toLowerCase() : 'close',
                        eventValue: element.dataset.eventValue,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        engagementType: el === btn ? '2' : element.dataset.engagementType,
                        destinationPage: el === btn ? btn.href : element.dataset.destinationPage
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _positionModal() {
            var viewportHeight,
                modalContainerHeight,
                modalOffset = 0,
                heightDiff,
                modalContainer,
                shareBarBottom,
                hasBottomShare,
                shareHeight,
                modalTextContainer;

            if (modalDialog) {
                modalContainer = element.querySelector('.modal-container');
                modalTextContainer = modalContainer.querySelector('.modal-text');
                shareBarBottom = document.querySelector('.at-share-dock');
                hasBottomShare = device === 'mobile' && shareBarBottom;
                shareHeight = hasBottomShare ? parseInt(window.getComputedStyle(shareBarBottom).getPropertyValue('height'), 10) : 0;
                viewportHeight = window.innerHeight;
                modalTextContainer.removeAttribute('style');
                modalContainerHeight = modalContainer.clientHeight;

                if (viewportHeight > modalContainerHeight) {
                    modalOffset = (viewportHeight - modalContainerHeight) / 2;
                } else if (viewportHeight < modalContainerHeight) {
                    heightDiff = modalContainerHeight + 65 - viewportHeight;
                    modalTextContainer.style.maxHeight = modalTextContainer.clientHeight - heightDiff - shareHeight + 'px';
                    modalTextContainer.style.overflow = 'auto';
                }
                if (device === 'mobile') {
                    modalDialog.style.top = '0px';
                } else {
                    modalDialog.style.top = modalOffset + 'px';
                }
            }
        }

        function _hide(el) {
            el.classList.remove('show');
            el.classList.add('hide');
        }

        function _resetBody() {
            EDC.utils.unAttachEvents(window, 'resize', _positionModal);
            body.classList.remove('modal-is-open');
            bodyScrollLock.enableBodyScroll(modalDialog);
        }

        function _closeModalShade(e) {
            var el = e.currentTarget,
                parent = el ? el.closest('.c-sci-interstitial') : null;

            if (parent) {
                parent.classList.add('hide');
            }

            _resetBody();
        }

        function _closeModal(e) {
            _hide(element);
            _resetBody();
            _trackEvent(e);
        }

        function _detectEsc(e) {
            if (e.keyCode === EDC.props.escapeKeyCode) {
                _closeModal(e);
            }
        }

        // Attach events
        function _attachEvents() {
            var modalShade = element.querySelector('.modal-shade');

            EDC.utils.attachEvents(btn, 'click', _trackEvent);
            EDC.utils.attachEvents(closeBtn, 'click', _closeModal);
            EDC.utils.attachEvents(window, 'resize', _positionModal);
            EDC.utils.attachEvents(d, 'keyup', _detectEsc);
            EDC.utils.attachEvents(modalShade, 'click', _closeModalShade);
        }

        _attachEvents();
        _positionModal();
    }

    // Public methods
    function init() {
        var sciInterstitials = document.querySelectorAll('.c-sci-interstitial:not([data-component-state="initialized"])');

        if (sciInterstitials) {
            sciInterstitials.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', sciInterstitialJS.init);