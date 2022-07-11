var mstlForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var formElements = element.elements,
            formRows = element.querySelectorAll('.form-row'),
            submitBtn = element.querySelector('button[type="submit"]'),
            jsonUrl = '/bin/mstlFormServlet',
            response = 'no response',
            cdoSubmissionDate = element.querySelector('input[name="cdoSubmissionDate"]');
            eEmailAddressField = element.querySelector('input[name="emailAddress"]');

        // Data Layer
        function _trackEvent(e) {
            var componentContainer = e.target.closest('.c-mstl-form'),
                obj = {
                    eventInfo: {
                        eventComponent: componentContainer.dataset.eventComponent,
                        eventType: componentContainer.dataset.eventType,
                        eventName: componentContainer.dataset.eventName + ' - ' + componentContainer.getAttribute('name'),
                        eventAction: componentContainer.dataset.eventAction,
                        eventText: componentContainer.querySelector('button[type="submit"]').textContent,
                        eventValue: componentContainer.querySelector('input[name="docID"]').value,
                        eventValue2: componentContainer.querySelector('input[name="mstlConsent"]').checked ? 'checked' : 'not',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: componentContainer.dataset.eventEngagement,
                        eventLevel: componentContainer.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        // Pre-populate the email field if the user is Logged in
        function _prePopulateEmailFields() {
            EDC.utils.addLoggedInUserEmailToField(eEmailAddressField);
        }

        // Private methods
        function _getCurrentTimeStamp() {
            if (cdoSubmissionDate) {
                EDC.utils.fetchJSON('GET', jsonUrl, '', function (data) {
                    if (data) {
                        response = data.timeStamp;
                    }

                    if (response) {
                        cdoSubmissionDate.value = response;
                    }
                });
            }
        }

        function _validateSubmit(e) {
            var submitURL = '',
                submitFailedMessage = element.querySelector('.submit-failed-message');

            e.preventDefault();

            EDC.forms.validateForm(formElements);

            if (!element.querySelectorAll('.form-control.error').length) {
                submitURL = element.getAttribute('action');
                EDC.forms.disableSubmit(submitBtn);
                EDC.forms.submitFormData('POST', submitURL, EDC.forms.getFormData(element), function () {
                    element.querySelector('.content').classList.add('hide');
                    element.querySelector('.disclaimer-container').classList.add('hide');
                    element.querySelector('.success').classList.add('show');

                    // Tracking
                    _trackEvent(e);
                }, submitFailedMessage);
            }
        }

        function _attachEvents() {
            EDC.forms.validateChange(formElements);
            EDC.utils.attachEvents(element, 'submit', _validateSubmit);

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
            });
        }

        _prePopulateEmailFields();
        _getCurrentTimeStamp();
        _attachEvents();
        EDC.forms.fillHiddenFields(element);
        EDC.forms.setHeightLabelsOnLoad(formRows);

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var mstlForms = document.querySelectorAll('form.c-mstl-form:not([data-component-state="initialized"])');

        if (mstlForms) {
            mstlForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', mstlForm.init);