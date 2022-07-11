var cimFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var servletURL = element.getAttribute('data-url'),
            form = element.querySelector('form'),
            formRows = form ? form.querySelectorAll('.form-row') : null,
            formElements = form ? form.elements : null,
            submitBtn = form ? form.querySelector('button[type="submit"]') : null,
            serviceURL = form ? form.getAttribute('action') : null,
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            formSuccess = element.querySelector('.form-success'),
            successMessage = element.querySelector('.success-message'),
            userName = form ? form.querySelector('input[name="userName"]') : null,
            userEmail = form ? form.querySelector('input[name="userEmail"]') : null,
            processingScreen = element.querySelector('.c-processing-screen'),
            codeContainer = element.querySelector('.isv-code'),
            isvCode = codeContainer ? codeContainer.querySelector('.code') : null;

        // Private methods

        // Helper function to submit the form
        function _formSubmit(e) {
            var errorField,
                formErrors;

            e.preventDefault();

            if (formElements) {
                EDC.forms.validateForm(formElements);
            }

            if (form) {
                formErrors = form.querySelectorAll('.form-control.error, input.error');
            }

            // Gets values from myEDC
            if (userName && userEmail) {
                userName.value = EDC.props.userData.firstName ? EDC.props.userData.firstName : '';
                userEmail.value = EDC.props.userData.email ? EDC.props.userData.email : '';
            }

            if (formErrors && !formErrors.length && serviceURL) {
                if (submitBtn) {
                    EDC.forms.disableSubmit(submitBtn);
                }

                // Shows processing screen
                if (processingScreen && form) {
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y);
                    form.classList.add('hide');
                    element.classList.add('processing');
                }

                if (serviceURL && form) {
                    EDC.utils.fetchJSON('POST', serviceURL, EDC.forms.getFormData(form), function (data) {
                        // Hides processing screen
                        element.classList.remove('processing');

                        if (formSuccess && successMessage) {
                            successMessage.classList.remove('hide');
                            formSuccess.classList.remove('hide');
                        }

                        if (data && data.isv && isvCode && codeContainer) {
                            isvCode.innerText = data.isv;
                            codeContainer.classList.remove('hide');
                        }
                    }, function () {
                        // Error on server communication, delay or related issues
                        if (submitFailedMessage && form && codeContainer) {
                            codeContainer.classList.add('hide');
                            element.classList.remove('processing');
                            form.classList.remove('hide');
                            submitFailedMessage.classList.remove('hide');
                        }

                        if (submitFailedMessage && submitBtn) {
                            EDC.forms.enableSubmit(submitBtn);
                        }
                    });
                }

            } else {
                errorField = element.querySelector('.form-control.error');

                if (errorField) {
                    errorField.focus();
                }
            }
        }

        // Helper function to set the content of the success CTA for Can or non Can companies
        function _setCTAContent(isCan) {
            var guideTitle = element.querySelector('.insight-guide .title'),
                guideDescription = element.querySelector('.insight-guide .description'),
                guideLinkContainer = element.querySelector('.insight-guide .link-container'),
                linkText = guideLinkContainer ? guideLinkContainer.querySelector('span') : null,
                linkURL = guideLinkContainer ? guideLinkContainer.querySelector('.button') : null;

            if (guideTitle && guideDescription && linkURL && linkText && guideLinkContainer) {
                if (isCan) {
                    guideTitle.innerText = guideTitle.getAttribute('data-can-text');
                    guideDescription.querySelector('.can-text').classList.remove('hide');
                    linkText.innerText = guideLinkContainer.getAttribute('data-can-text');
                    linkURL.setAttribute('href', guideLinkContainer.getAttribute('data-can-url'));
                    linkURL.setAttribute('target', guideLinkContainer.getAttribute('data-can-target'));
                } else {
                    guideTitle.innerText = guideTitle.getAttribute('data-alternate-text');
                    guideDescription.querySelector('.alternate-text').classList.remove('hide');
                    linkText.innerText = guideLinkContainer.getAttribute('data-alternate-text');
                    linkURL.setAttribute('href', guideLinkContainer.getAttribute('data-alternate-url'));
                    linkURL.setAttribute('target', guideLinkContainer.getAttribute('data-alternate-target'));
                }
            }
        }

        // Attach events
        function _attachEvents() {
            var country = form ? form.querySelector('.country select') : null,
                provinceSelect = form ? form.querySelector('.province .can select') : null,
                stateSelect = form ? form.querySelector('.province .usa select') : null,
                provinceInput = form ? form.querySelector('.province input') : null,
                provinceLabel = form ? form.querySelector('.province > label') : null;

            if (formElements) {
                EDC.forms.validateChange(formElements);
            }

            if (form) {
                EDC.utils.attachEvents(form, 'submit', _formSubmit);
            }

            if (country && provinceSelect && stateSelect && provinceInput && provinceLabel) {
                EDC.utils.attachEvents(country, 'change', function () {
                    EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
                });
            }

            EDC.utils.attachEvents(window, 'resize', function () {
                if (formRows) {
                    EDC.forms.setHeightLabels(formRows);
                }
            });
        }

        if (servletURL && form) {
            EDC.forms.getUserProfileType(servletURL, function () {
                form.classList.remove('hide');
                _attachEvents();

                if (formRows) {
                    EDC.forms.setHeightLabelsOnLoad(formRows);
                }

                _setCTAContent(true);
            }, function () {
                if (formSuccess) {
                    formSuccess.classList.remove('hide');
                    _setCTAContent(false);
                }
            });
        }
    }

    // Public methods
    function init() {
        var cimForm = document.querySelectorAll('.c-cim-form:not([data-component-state="initialized"])');

        if (cimForm) {
            cimForm.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', cimFormJS.init);