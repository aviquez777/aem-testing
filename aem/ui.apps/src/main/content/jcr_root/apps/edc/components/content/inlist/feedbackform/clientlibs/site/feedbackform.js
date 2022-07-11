var inListFeedbackFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var form = element.querySelector('form'),
            formElements = form ? form.elements : '',
            userEmail = form ? form.querySelector('input[name="emailAddress"]') : '';

        // Data Layer
        function _trackEvent() {
            var obj = {
                eventInfo: {
                    eventComponent: form.dataset.eventComponent.toLowerCase(),
                    eventType: form.dataset.eventType,
                    eventName: (form.dataset.eventName + ' - ' + form.getAttribute('name')).toLowerCase(),
                    eventAction: form.dataset.eventAction,
                    eventText: form.querySelector('button[type="submit"]').textContent.toLowerCase().trim(),
                    eventValue: form.querySelector('input[name="docID"]').value.toLowerCase(),
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName().toLowerCase(),
                    destinationPage: '',
                    engagementType: form.dataset.eventEngagement,
                    eventLevel: form.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _validateSubmit(e) {
            var submitURL = '',
                submitBtn,
                submitFailedMessage = form.querySelector('.submit-failed-message');

            e.preventDefault();

            EDC.forms.validateForm(formElements);
            userEmail.value = EDC.props.userData.email ? EDC.props.userData.email : '';

            if (!form.querySelectorAll('.form-control.error').length) {
                submitBtn = form.querySelector('button[type="submit"]');
                submitURL = form.getAttribute('action');
                EDC.forms.disableSubmit(submitBtn);
                EDC.forms.submitFormData('POST', submitURL, EDC.forms.getFormData(form), function () {
                    form.querySelector('.content').classList.add('hide');
                    element.querySelector('.subtitle').classList.add('hide');
                    form.querySelector('.success').classList.remove('hide');
                    // Tracking
                    _trackEvent(e);
                }, submitFailedMessage);
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(form, 'submit', _validateSubmit);
        }

        _attachEvents();

        if (form) {
            EDC.forms.validateChange(formElements);
        }
    }

    // Public methods
    function init() {
        var inListFeedbackForm = document.querySelectorAll('.c-inlist-feedback-form:not([data-component-state="initialized"])');

        if (inListFeedbackForm) {
            inListFeedbackForm.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', inListFeedbackFormJS.init);