var consentRequestGatingJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var jsonUrl = element.getAttribute('data-json-url'),
            pagePath = element.getAttribute('data-page-path'),
            title = element.querySelector('.title'),
            text = element.querySelector('.description'),
            errorMessageSection = element.querySelector('.error-message'),
            errorMessageP = errorMessageSection.querySelector('p'),
            defaultErrorMessage = errorMessageSection.getAttribute('data-default-message'),
            consentProvidedSection = element.querySelector('.consent-provided'),
            consentRequiredSection = element.querySelector('.consent-required'),
            consentRequestModal = element.querySelector('.consent-request-modal'),
            consentRequestModalForm = consentRequestModal.querySelector('form[name="consent-request-modal-form"]'),
            consentRequestModalError = consentRequestModalForm ? consentRequestModalForm.querySelector('.modal-error-message') : null,
            consentRequestModalErrorP = consentRequestModalError ? consentRequestModalError.querySelector('p') : null,
            consentRequestModalCaslSection = consentRequestModal ? consentRequestModal.querySelector('.casl-section') : null,
            consentRequestModalCheckboxes = consentRequestModalCaslSection ? consentRequestModalCaslSection.querySelectorAll('input[type="checkbox"]') : null,
            consentRequestModalText = consentRequestModal ? consentRequestModal.querySelector('.modal-text') : null,
            consentRequestModalAnchor = consentRequestModal ? consentRequestModal.querySelector('.anchor-container') : null,
            consentRequestModalCloseBtn = consentRequestModal ? consentRequestModal.querySelector('.modal-close') : null,
            consentRequestModalAgreeBtn = consentRequestModal ? consentRequestModal.querySelector('.c-interaction-button') : null,
            consentRequestModalCancelBtn = consentRequestModal ? consentRequestModal.querySelector('button.unstyled') : null,
            containerHeight,
            fullScrolled = false,
            firstLoad = true,
            contentReady = false,
            editMode = element.getAttribute('data-edit-mode') === 'true',
            siblingProgressiveProfiling = element.parentNode.querySelector('.c-progressive-profiling'),
            hasConsent = EDC.utils.getURLParams().consent !== 'no',
            consentRequestAnchor,
            consentRequestPosition,
            consentScrollDiff = 0;

        // Private methods
        // Tracking
        function _trackEvent(e) {
            var el = e.currentTarget,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: element.dataset.destinationPage,
                        engagementType: element.dataset.engagementType,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            if (el.tagName.toLowerCase() === 'button') {
                obj.eventInfo.eventText = el.getAttribute('data-english-text');
                obj.eventInfo.eventValue = el.querySelector('span') ? el.querySelector('span').textContent : el.textContent;
            } else if (el.tagName.toLowerCase() === 'input' && el.type === 'checkbox') {
                obj.eventInfo.eventText = el.parentNode.querySelector('.input-description label p').textContent;
                obj.eventInfo.eventValue = el.checked ? 'checked' : 'unchecked';
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _consentRequestAnchorScroll() {
            if (!hasConsent) {
                // Remove query from url and scroll
                history.replaceState && history.replaceState(null, '', location.pathname + location.search.replace(/[\?&]consent=[^&]+/, '').replace(/^&/, '?'));
                consentRequestAnchor = element.parentNode.querySelector('a.anchor[data-target="consent"]');
                consentRequestPosition = EDC.utils.getPosition(consentRequestAnchor).y;
                if (EDC.utils.getDeviceViewPort() !== 'desktop') {
                    consentScrollDiff = 65;
                }
                setTimeout(function () {
                    EDC.utils.scrollTo('top', consentRequestPosition - consentScrollDiff);
                }, 1000);
            }
        }

        function _showPageLoadErrorMsg(errorMessage) {
            if (errorMessage) {
                errorMessageP.textContent = errorMessage;
            } else {
                errorMessageP.textContent = defaultErrorMessage;
            }
            element.querySelector('.title').classList.add('hide');
            element.querySelector('.content-section').classList.add('hide');
            errorMessageSection.classList.remove('hide');
        }

        function _loadJson() {
            if (siblingProgressiveProfiling) {
                siblingProgressiveProfiling.classList.add('hide');
            }
            element.classList.add('hide');

            // When user status is retrieved proceed with the status service call
            if (jsonUrl) {
                if (pagePath) {
                    jsonUrl = jsonUrl + '?pagePath=' + window.btoa(pagePath);
                }
                EDC.utils.fetchJSON('GET', jsonUrl, '', function (data) {
                    if (data) {
                        if (data.errorMessage) {
                            _showPageLoadErrorMsg(data.errorMessage);
                        } else if (data.url) {
                            consentRequiredSection.classList.add('hide');
                            consentProvidedSection.classList.remove('hide');
                            consentProvidedSection.querySelector('a').href = data.url;
                            title.textContent = title.getAttribute('data-consent-provided-title');
                            text.innerHTML = text.getAttribute('data-consent-provided-text');
                        } else {
                            consentRequiredSection.classList.remove('hide');
                            consentProvidedSection.classList.add('hide');
                        }
                    } else {
                        _showPageLoadErrorMsg();
                    }
                    contentReady = true;
                }, function () {
                    _showPageLoadErrorMsg();
                    contentReady = true;
                });
            }
        }

        function _innerScrollToBottom(el) {
            var start = el.scrollTop ? el.scrollTop : 0,
                change = el.querySelector('.full-section') ? parseInt(window.getComputedStyle(el.querySelector('.full-section')).getPropertyValue('height'), 10) - start : 0,
                currentTime = 0,
                increment = 20,
                duration = 600;

            function _animateScroll() {
                var val;

                currentTime += increment;
                val = Math.easeInOutQuad(currentTime, start, change, duration);
                el.scrollTop = val;

                if (currentTime < duration) {
                    setTimeout(_animateScroll, increment);
                }
            }

            Math.easeInOutQuad = function (t, b, c, g) {
                t /= g / 2;
                if (t < 1) {
                    return c / 2 * t * t + b;
                }

                t--;
                return -c / 2 * (t * (t - 2) - 1) + b;
            };

            _animateScroll();
        }

        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        function _checkCheckboxes() {
            var totalCheckboxes = consentRequestModalCheckboxes.length,
                counter = 0;

            consentRequestModalCheckboxes.forEach(function (checkbox) {
                if (checkbox.checked) {
                    counter++;
                }
            });

            if (fullScrolled && counter === totalCheckboxes) {
                consentRequestModalAgreeBtn.classList.remove('disabled');
                consentRequestModalAgreeBtn.removeAttribute('disabled');
            } else {
                consentRequestModalAgreeBtn.classList.add('disabled');
                consentRequestModalAgreeBtn.setAttribute('disabled', 'disabled');
            }
        }

        function _consentRequestModalScrolled(e) {
            var target = e.target,
                scrollSize = target.scrollHeight - target.offsetHeight,
                newScrollTop = Math.round(target.scrollTop);

            if (newScrollTop + 1 < scrollSize) {
                consentRequestModalAnchor.classList.remove('hide');
            } else {
                consentRequestModalAnchor.classList.add('hide');
                fullScrolled = true;
                _checkCheckboxes();
            }

            if (newScrollTop > 0 && newScrollTop < scrollSize) {
                consentRequestModalText.classList.add('shadow-after');
            } else if (newScrollTop === 0) {
                consentRequestModalText.classList.add('shadow-after');
            } else if (newScrollTop === scrollSize) {
                consentRequestModalText.classList.remove('shadow-after');
            }
        }

        function _windowResized() {
            var modalCtas = consentRequestModal.querySelector('.modal-ctas'),
                textSection = consentRequestModalText.querySelector('.text-section'),
                modalTitle = consentRequestModal.querySelector('.modal-title'),
                ctasHeight = parseInt(window.getComputedStyle(modalCtas).getPropertyValue('height'), 10),
                titleHeight = parseInt(window.getComputedStyle(modalTitle).getPropertyValue('height'), 10),
                screenHeight = window.outerHeight - 120,
                isIE = _isIE(),
                maxHeight = screenHeight - ctasHeight - titleHeight - (isIE ? 200 : 0);

            if (containerHeight >= screenHeight) {
                consentRequestModalAnchor.classList.remove('hide');
                consentRequestModalText.classList.add('section-fixed');
                textSection.scrollTop = 0;
                EDC.utils.attachEvents(textSection, 'scroll', _consentRequestModalScrolled);
                EDC.utils.attachEvents(consentRequestModalAnchor.querySelector('.circle-button'), 'click', function () {
                    _innerScrollToBottom(consentRequestModalText.querySelector('.text-section'));
                });
                textSection.style.maxHeight = maxHeight + 'px';
                textSection.style.overflowY = 'auto';
                consentRequestModalAgreeBtn.classList.add('disabled');
                consentRequestModalAgreeBtn.setAttribute('disabled', 'disabled');
                fullScrolled = false;
            } else {
                textSection.style.maxHeight = '';
                textSection.style.overflowY = 'none';
                consentRequestModalAnchor.classList.add('hide');
                consentRequestModalText.classList.remove('section-fixed');
                EDC.utils.unAttachEvents(textSection, 'scroll', _consentRequestModalScrolled);
                EDC.utils.unAttachEvents(consentRequestModalAnchor.querySelector('.circle-button'), 'click', function () {
                    textSection.scrollTop = textSection.scrollHeight;
                });
                fullScrolled = true;
            }
        }

        function _showModal() {
            consentRequestModal.classList.remove('hide');
            containerHeight = parseInt(window.getComputedStyle(consentRequestModal.querySelector('.modal-container')).getPropertyValue('height'), 10);
            if (firstLoad) {
                _windowResized();
                firstLoad = false;
                EDC.utils.attachEvents(window, 'resize', _windowResized);
            }
        }

        function _handleClick(e) {
            if (e.target.closest('.consent-btn').querySelector('button')) {
                _showModal();
            }
        }

        function _closeConsentRequestModal(e) {
            consentRequestModal.classList.add('hide');
            document.querySelector('body').classList.remove('no-scroll');
            _trackEvent(e);
        }

        function _checkboxClicked(e) {
            _checkCheckboxes();
            _trackEvent(e);
        }

        function _checkUserStatus() {
            PubSub.subscribe('user-status', function (name, data) {
                var interval = setInterval(function () {
                    if (contentReady) {
                        siblingProgressiveProfiling.classList[data ? 'add' : 'remove']('hide');
                        element.classList[data ? 'remove' : 'add']('hide');
                        clearInterval(interval);
                    }
                }, 100);
            });
        }

        function _showModalErrorMsg(errorMessage) {
            if (errorMessage) {
                consentRequestModalErrorP.textContent = errorMessage;
            } else {
                consentRequestModalErrorP.textContent = defaultErrorMessage;
            }
            consentRequestModalError.classList.remove('hide');
        }

        function _consentRequestModalFormSubmit(e) {
            var action = consentRequestModalForm.action;

            e.preventDefault();
            consentRequestModalError.classList.add('hide');
            EDC.utils.fetchJSON('POST', action, '', function (data) {
                if (data) {
                    if (data.errorMessage) {
                        _showModalErrorMsg(data.errorMessage);
                    } else if (data.url) {
                        location.href = data.url;
                    } else {
                        _showModalErrorMsg();
                    }
                } else {
                    _showModalErrorMsg();
                }
            }, function () {
                _showModalErrorMsg();
            });
        }

        // Attach events
        function _attachEvents() {
            var actionLinks = element.querySelectorAll('.consent-btn');

            EDC.utils.attachEvents(actionLinks, 'click', _handleClick);
            EDC.utils.attachEvents(consentRequestModalCloseBtn, 'click', _closeConsentRequestModal);
            EDC.utils.attachEvents(consentRequestModalCancelBtn, 'click', _closeConsentRequestModal);
            EDC.utils.attachEvents(consentRequestModalAgreeBtn, 'click', _trackEvent);
            EDC.utils.attachEvents(consentRequestModalCheckboxes, 'click', _checkboxClicked);
            EDC.utils.attachEvents(window, 'resize', _windowResized);
            EDC.utils.attachEvents(consentRequestModalForm, 'submit', _consentRequestModalFormSubmit);
        }

        if (!editMode) {
            _loadJson();
            _consentRequestAnchorScroll();
        }

        if (siblingProgressiveProfiling) {
            _checkUserStatus();
        }

        _windowResized();
        _attachEvents();
    }

    // Public methods
    function init() {
        var ConsentRequestGatings = document.querySelectorAll('.c-consent-request-gating:not([data-component-state="initialized"])');

        if (ConsentRequestGatings) {
            ConsentRequestGatings.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', consentRequestGatingJS.init);