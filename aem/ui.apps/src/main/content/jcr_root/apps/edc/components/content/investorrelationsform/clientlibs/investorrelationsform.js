var investorRelationsForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var inputs = element.querySelectorAll('input[type="radio"]'),
            page1Btn = element.querySelector('.page-1 button');

        // Data Layer
        function _trackEvent() {
            var inputChecked = element.querySelector('input[type="radio"]:checked'),
                el = element,
                obj = {
                    eventInfo: {
                        eventComponent: el.dataset.eventComponent,
                        eventType: el.dataset.eventType,
                        eventName: el.dataset.eventName,
                        eventAction: el.dataset.eventAction,
                        eventText: page1Btn.textContent.toLowerCase(),
                        eventValue: el.dataset.eventValue,
                        eventValue2: el.querySelector('label[for=' + inputChecked.id + ']').textContent,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: inputChecked.dataset.form,
                        engagementType: el.dataset.engagementType,
                        eventLevel: el.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                },
                userSegment = {
                    restrictionsOnAccess: inputChecked.dataset.restrictionsOnAccess
                };

            EDC.utils.userSegmentTracking(userSegment);
            EDC.utils.dataLayerTracking(obj);
        }

        function _showElement(elem) {
            if (typeof (elem) !== 'undefined' && elem.classList.contains('hide')) {
                elem.classList.remove('hide');
            }
        }

        function _submitForm(e) {
            var inputChecked = element.querySelector('input[type="radio"]:checked'),
                page1Error = element.querySelector('.submit-failed-message');

            e.preventDefault();
            if (typeof (inputChecked) === 'undefined' || inputChecked === null) {
                // Show Error Message
                _showElement(page1Error);
            } else {
                // Data Layer
                _trackEvent();
                // Redirect to expected page, remove /content/edc if there
                window.setTimeout(function () {
                    location.href = EDC.utils.removeContentEdcFromLink(inputChecked.dataset.form);
                }, 1000);
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(page1Btn, 'click', _submitForm);
        }

        // if user hit back deselect the radiobuttons
        function _clearInputs() {
            var arrayLength = inputs.length,
                i = 0;

            for (i; i < arrayLength; i++) {
                inputs[i].checked = false;
            }
        }

        _clearInputs();
        _attachEvents();
    }

    // Public methods
    function init() {
        var investorRelationsForms = document.querySelectorAll('form.c-investor-relations:not([data-component-state="initialized"])');

        if (investorRelationsForms) {
            investorRelationsForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', investorRelationsForm.init);