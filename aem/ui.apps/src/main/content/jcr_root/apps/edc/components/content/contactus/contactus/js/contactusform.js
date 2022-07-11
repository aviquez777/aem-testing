var contactUsForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var formElements = element.elements,
            formRows = element.querySelectorAll('.form-row'),
            submitBtn = element.querySelector('button[type="submit"]');

        // Data Layer
        function _trackEvent(e) {
            var componentContainer = e.target.closest('.c-contact-us'),
                obj = {
                    eventInfo: {
                        eventComponent: componentContainer.dataset.eventComponent,
                        eventType: componentContainer.dataset.eventType,
                        eventName: componentContainer.dataset.eventName + ' - ' + componentContainer.getAttribute('name'),
                        eventAction: componentContainer.dataset.eventAction,
                        eventText: componentContainer.querySelector('button[type="submit"]').textContent,
                        eventValue: componentContainer.querySelector('input[name="docID"]').value,
                        eventValue2: componentContainer.querySelector('input[name="caslConsent"]').checked ? 'checked' : 'not',
                        eventValue3: element.querySelector('[name="inquiry"]').value.toLowerCase(),
                        eventValue4: element.querySelector('[name="tradeStatus"]').value.toLowerCase(),
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: componentContainer.dataset.eventEngagement,
                        eventLevel: componentContainer.dataset.eventLevel
                    }
                },
                userSegment = {
                    inquiry: EDC.forms.getOptionsSelected(componentContainer.querySelector('.dropdown [name="inquiry"]')),
                    tradeStatus: EDC.forms.getOptionsSelected(componentContainer.querySelector('.dropdown [name="tradeStatus"]'))
                };

            EDC.utils.userSegmentTracking(userSegment, true);
            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
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
                    element.querySelector('.success').classList.add('show');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);

                    // Tracking
                    _trackEvent(e);
                }, submitFailedMessage);
            }
        }

        function _attachEvents() {
            EDC.forms.validateChange(formElements);
            EDC.utils.attachEvents(element, 'submit', _validateSubmit);
            EDC.utils.attachEvents(element.querySelector('.resetBtn'), 'click', function () {
                element.querySelector('.content').classList.remove('hide');
                element.querySelector('.content').classList.add('show');
                element.querySelector('.success').classList.remove('show');
                element.querySelector('.success').classList.add('hide');
                element.querySelector('select')[0].selectedIndex = 0;
                EDC.forms.resetForm(element);
                submitBtn.removeAttribute('disabled');
            });

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
            });
        }

        _attachEvents();
        EDC.forms.fillHiddenFields(element);
        EDC.forms.setHeightLabelsOnLoad(formRows);

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var contactUsForms = document.querySelectorAll('form.c-contact-us:not([data-component-state="initialized"])');

        if (contactUsForms) {
            contactUsForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', contactUsForm.init);