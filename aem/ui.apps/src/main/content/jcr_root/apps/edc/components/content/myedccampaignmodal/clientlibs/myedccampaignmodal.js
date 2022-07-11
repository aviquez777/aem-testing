var modalCampaignJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            html = d.querySelector('html'),
            body = d.querySelector('body'),
            selectedModal,
            modalBodyClass = 'modal-is-open',
            submitBtn = element.querySelector('button[type="submit"]');

        // Data Layer
        function _trackEvent(action, e) {
            var eTarget = e ? e.currentTarget : null,
                btnSpan = eTarget ? eTarget.querySelector('span') : null,
                btnTxt = btnSpan ? btnSpan.textContent.toLowerCase() : null,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventAction: element.dataset.eventAction,
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            if (action === 'load') {
                obj.eventInfo.eventType = 'interstitial';
                obj.eventInfo.eventName = 'Interstitial view';
                obj.eventInfo.engagementType = '1';
                obj.eventInfo.eventText = '';
            } else if (action === 'cta-clicked' || (action === 'modal-closed' && eTarget && eTarget.classList.contains('dismiss-btn'))) {
                obj.eventInfo.eventType = 'button';
                obj.eventInfo.eventName = btnTxt ? ('button click - ' + btnTxt) : '';
                obj.eventInfo.eventText = eTarget ? eTarget.getAttribute('data-english-text') : '';
                obj.eventInfo.engagementType = '2';
            } else if (action === 'modal-closed' && eTarget && eTarget.classList.contains('modal-close-campaign')) {
                obj.eventInfo.eventType = 'button';
                obj.eventInfo.eventName = 'button click - close popup';
                obj.eventInfo.eventText = eTarget ? eTarget.getAttribute('data-english-text') : '';
                obj.eventInfo.engagementType = '1';
            } else if (action === 'form-submitted') {
                obj.eventInfo.eventType = 'interstitial';
                obj.eventInfo.eventName = 'interstitial - email submitted';
                obj.eventInfo.eventText = 'thanks for subscribing';
                obj.eventInfo.engagementType = '3';
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _toggleScroll(action) {
            if (action === 'show') {
                body.classList.remove(modalBodyClass);
                html.style.overflow = 'auto';
                body.style.overflow = 'auto';
            } else if (action === 'hide') {
                body.classList.add(modalBodyClass);
                html.style.overflow = 'hidden';
                body.style.overflow = 'hidden';
            }
        }

        function _closeModal(e) {
            e.preventDefault();
            element.querySelector('.modal-campaign.show').classList.remove('show');
            _toggleScroll('show');
            _trackEvent('modal-closed', e);
        }

        // Detects Escape key pressed
        function _detectEsc(e) {
            var modalOpened = d.querySelector('.modal-campaign.show');

            if (e.keyCode === EDC.props.escapeKeyCode && modalOpened) {
                modalOpened.classList.remove('show');
            }
        }

        function _showModal(e) {
            var modalToTrigger = e.currentTarget.getAttribute('data-modal-to-trigger');

            e.preventDefault();
            selectedModal = d.getElementById(modalToTrigger);
            selectedModal.classList.add('show');
            _toggleScroll('hide');
        }

        function _handleSubmit(e) {
            _trackEvent('cta-clicked', e);
        }

        function _pubSubs() {
            PubSub.subscribe('newsletter-subscription-submitted', function (msg, status) {
                if (status) {
                    _trackEvent('form-submitted');
                }
            });
        }

        // Attach events
        function _attachEvents() {
            var modalTriggers = d.querySelectorAll('.modal-trigger'),
                modalClose = element.querySelector('.modal-close-campaign'),
                dismissBtn = element.querySelector('.actions .dismiss-btn');

            EDC.utils.attachEvents(modalTriggers, 'click', _showModal);
            EDC.utils.attachEvents(modalClose, 'click', _closeModal);
            EDC.utils.attachEvents(dismissBtn, 'click', _closeModal);
            EDC.utils.attachEvents(submitBtn, 'click', _handleSubmit);
            EDC.utils.attachEvents(d, 'keyup', _detectEsc);
        }

        _attachEvents();
        _pubSubs();

        setTimeout(function () {
            if (element.querySelector('.modal-campaign.show')) {
                _toggleScroll('hide');
            }
        }, 0);

        _trackEvent('load');
    }

    // Public methods
    function init() {
        var modals = document.querySelectorAll('.c-modal-campaign:not([data-component-state="initialized"])');

        if (modals) {
            modals.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', modalCampaignJS.init);