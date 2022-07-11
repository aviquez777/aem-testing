var newsletterSubscriptionJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.querySelector('form'),
            modalParent = element.closest('.modal-container'),
            formElements = form ? form.elements : null,
            sideBarSelected,
            timeOut = 5,
            modalCampaignBtns = element.querySelector('.actions');

        // Data Layer
        function _trackEvent(e) {
            var eTarget = e ? e.target : null,
                componentContainer = eTarget ? eTarget.closest('.newsletter-form') : null,
                submitBtn = componentContainer ? componentContainer.querySelector('button[type="submit"]') : null,
                obj = {
                    eventInfo: {
                        eventComponent: componentContainer ? componentContainer.dataset.eventComponent : '',
                        eventType: componentContainer ? componentContainer.dataset.eventType : '',
                        eventName: componentContainer ? componentContainer.dataset.eventName : '',
                        eventText: submitBtn ? submitBtn.textContent : '',
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: componentContainer ? componentContainer.dataset.eventEngagement : '',
                        eventStage: '',
                        eventLevel: componentContainer ? componentContainer.dataset.eventLevel : ''
                    }
                };

            if (element.classList.contains('newsletter-banner')) {
                obj.eventInfo.eventAction = 'banner';
            } else if (componentContainer && componentContainer.closest('aside')) {
                obj.eventInfo.eventAction = 'right rail';
            } else {
                obj.eventInfo.eventAction = 'main body';
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _validateSubmit(e) {
            var submitURL = '',
                submitBtn,
                submitFailedMessage = element.querySelector('.submit-failed-message'),
                formErrors;

            e.preventDefault();

            if (formElements) {
                EDC.forms.validateForm(formElements);
            }

            formErrors = form ? form.querySelectorAll('input.error') : null;

            if (!formErrors.length) {
                submitBtn = element.querySelector('button[type="submit"]');
                submitURL = form.getAttribute('action');

                if (submitBtn) {
                    EDC.forms.disableSubmit(submitBtn);
                }

                if (form) {
                    EDC.forms.submitFormData('POST', submitURL, EDC.forms.getFormData(form), function () {
                        var formContent = form.querySelector('.form-content'),
                            modalSuccess,
                            formSuccess = form.querySelector('.success');

                        if (formContent) {
                            formContent.classList.add('hide');
                        }

                        if (modalParent) {
                            modalSuccess = modalParent.querySelector('.success');

                            if (modalCampaignBtns) {
                                modalCampaignBtns.classList.add('hide');
                            }

                            if (modalSuccess) {
                                modalSuccess.classList.add('show');
                            }
                        } else if (formSuccess) {
                            formSuccess.classList.add('show');
                        }

                        _trackEvent(e);

                        PubSub.publish('newsletter-subscription-submitted', true);
                    }, submitFailedMessage);
                }
            }
        }

        function _closeBanner() {
            var banner = this.closest('.newsletter-banner');

            if (banner) {
                banner.classList.remove('show');
                banner.classList.add('banner-closed');
            }

            if (sideBarSelected) {
                sideBarSelected.forEach(function (elem) {
                    elem.style.display = 'block';
                });
            }
        }

        function _showBanner() {
            var bodyHeight = d.body.scrollHeight * 0.5,
                windowHeight = window.innerHeight;

            if (!element.classList.contains('banner-closed')) {
                if (windowHeight >= bodyHeight || window.pageYOffset >= bodyHeight) {
                    if (EDC.utils.getDeviceViewPort() !== 'desktop' && sideBarSelected) {
                        sideBarSelected.forEach(function (elem) {
                            elem.style.display = 'none';
                        });
                    }

                    element.classList.add('show');
                }
            }
        }

        function _displayBanner() {
            var closeBtn,
                sideBar = d.querySelectorAll('.addthis-smartlayers');

            if (timeOut !== 0) {
                if (sideBar && sideBar.length > 0) {
                    sideBarSelected = sideBar;
                    _showBanner();
                } else {
                    setTimeout(_displayBanner, 1000);
                    timeOut--;
                }

                closeBtn = element.querySelector('.close-button');

                if (closeBtn) {
                    EDC.utils.attachEvents(closeBtn, 'click', _closeBanner);
                }

                EDC.utils.attachEvents(window, 'scroll', _showBanner);
            } else {
                _showBanner();
            }
        }

        function _prepopulateEmail() {
            var emailAddress = element.querySelector('input[name="emailAddress"]'),
                timer,
                counter = 0;

            timer = setInterval(function () {
                if (EDC.props.userData.email) {
                    emailAddress.value = EDC.props.userData.email;
                    clearInterval(timer);
                }

                if (counter === 20) {
                    clearInterval(timer);
                }

                counter++;
            }, 100);
        }

        function _initialValidations() {
            var dismissBtn = modalCampaignBtns ? modalCampaignBtns.querySelector('.dismiss-btn') : null;

            if (modalCampaignBtns) {
                if (modalParent) {
                    modalCampaignBtns.classList.add('inside-campaign-modal');
                } else if (dismissBtn) {
                    dismissBtn.parentNode.remove(dismissBtn);
                }
            }
        }

        function _attachEvents() {
            if (formElements) {
                EDC.forms.validateChange(formElements);
            }

            if (form) {
                EDC.utils.attachEvents(form, 'submit', _validateSubmit);
            }

            if (element.classList.contains('newsletter-banner')) {
                _displayBanner();
            }
        }

        _initialValidations();
        _attachEvents();
        EDC.forms.fillHiddenFields(form);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
        _prepopulateEmail();
    }

    // Public methods
    function init() {
        var formNewsletters = document.querySelectorAll('.c-newsletter-subscription:not([data-component-state="initialized"])');

        if (formNewsletters) {
            formNewsletters.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', newsletterSubscriptionJS.init);