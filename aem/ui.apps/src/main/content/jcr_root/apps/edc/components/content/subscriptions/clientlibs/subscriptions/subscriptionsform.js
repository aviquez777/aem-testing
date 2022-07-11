var subscriptionsForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var formElements = element.elements ? EDC.forms.cleanFormElements(element.elements) : null,
            checkboxes = element.querySelectorAll('input[type="checkbox"]'),
            descriptionEl = element.querySelector('#checkboxes-error'),
            descriptionTxt = descriptionEl ? descriptionEl.id : '';

        // Private methods
        // Data Layer
        function _trackEvent() {
            var values = element.querySelectorAll('.check-subscription'),
                buttonText = element.querySelector('button[type="submit"]').textContent,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventName + ' - ' + buttonText,
                        eventAction: element.dataset.eventAction,
                        eventText: buttonText,
                        eventValue: values[0].checked ? values[0].dataset.value : '',
                        eventValue2: values[1].checked ? values[1].dataset.value : '',
                        eventValue3: values[2].checked ? values[2].dataset.value : '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        eventPageName: EDC.utils.getPageName(),
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _validateSubscriptionOptions() {
            var subscription = element.querySelectorAll('.group-validation .check-subscription:checked').length,
                error = element.querySelector('.group-validation > .error');

            if (!subscription) {
                error.classList.add('show');
            } else {
                error.classList.remove('show');
            }

            return subscription;
        }

        function _validateSubmit(e) {
            var submitURL = '',
                submitBtn,
                validateSubscriptionOptions,
                submitFailedMessage = element.querySelector('.submit-failed-message');

            e.preventDefault();

            if (formElements) {
                EDC.forms.validateForm(formElements);
            }

            validateSubscriptionOptions = _validateSubscriptionOptions();

            if (!element.querySelectorAll('input.error, select.error, textarea.error, checkbox.error').length && validateSubscriptionOptions) {
                submitBtn = element.querySelector('button[type="submit"]');
                submitURL = element.getAttribute('action');

                EDC.forms.disableSubmit(submitBtn);

                EDC.forms.submitFormData('POST', submitURL, EDC.forms.getFormData(element), function () {
                    element.querySelector('.content').classList.add('hide');
                    element.querySelector('.success').classList.add('show');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);

                    // Call tracking here
                    _trackEvent();
                }, submitFailedMessage);
            }
        }

        function _validateCheckboxes() {
            var valid = false;

            if (checkboxes && descriptionTxt) {
                Array.from(checkboxes).some(function (checkbox) {
                    if (checkbox.checked) {
                        valid = true;
                    }

                    return valid;
                });

                checkboxes.forEach(function (checkbox) {
                    var parent = checkbox.closest('.checkbox-item');

                    if (parent) {
                        if (valid) {
                            checkbox.setAttribute('aria-invalid', 'false');
                            parent.removeAttribute('aria-describedby');
                        } else {
                            checkbox.setAttribute('aria-invalid', 'true');
                            parent.setAttribute('aria-describedby', descriptionTxt);
                        }
                    }
                });
            }
        }

        function _attachEvents() {
            if (formElements) {
                EDC.forms.validateChange(formElements);
            }

            EDC.utils.attachEvents(element, 'submit', _validateSubmit);
            EDC.utils.attachEvents(checkboxes, 'click', _validateCheckboxes);
        }

        _attachEvents();
        EDC.forms.fillHiddenFields(element);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var subscriptionsForms = document.querySelectorAll('form.c-subscriptions-form:not([data-component-state="initialized"])');

        if (subscriptionsForms) {
            subscriptionsForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', subscriptionsForm.init);