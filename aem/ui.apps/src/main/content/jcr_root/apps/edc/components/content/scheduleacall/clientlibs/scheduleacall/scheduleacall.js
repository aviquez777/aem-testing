var scheduleCallForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var formElements = element.elements,
            formRows = element.querySelectorAll('.form-row');

        // Data Layer
        function _trackEvent(e) {
            var componentContainer = e.target.closest('.schedule-call'),
                successContainer,
                obj = {
                    eventInfo: {
                        eventComponent: componentContainer.dataset.eventComponent,
                        eventType: componentContainer.dataset.eventType,
                        eventName: componentContainer.dataset.eventName + ' - ' + componentContainer.querySelector('button[type="submit"]').textContent,
                        eventAction: componentContainer.dataset.eventAction,
                        eventText: componentContainer.querySelector('button[type="submit"]').textContent,
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: componentContainer.dataset.eventEngagement,
                        eventStage: '',
                        eventLevel: componentContainer.dataset.eventLevel
                    }
                },
                userSegment = {
                    painPoint: EDC.forms.getOptionsSelected(componentContainer.querySelector('.dropdown [name="painPoint"]'))
                };

            if (e.type === 'click') {
                successContainer = e.target.closest('.success');
                obj.eventInfo.eventType = successContainer.dataset.eventType;
                obj.eventInfo.eventName = successContainer.dataset.eventName + ' - ' + e.target.text;
                obj.eventInfo.eventText = (e.target.getAttribute('href')).replace(/^tel:/, '');
                obj.eventInfo.engagementType = successContainer.dataset.eventEngagement;
            }

            EDC.utils.userSegmentTracking(userSegment, true);
            EDC.utils.dataLayerTracking(obj);
        }

        function _duplicateExportPain(params) {
            var painPoint = params.match(/painPoint(=([^&#]*)|&|#|$)/);

            return painPoint ? params.replace(/exportPain(=([^&#]*)|&|#|$)/, 'exportPain=' + painPoint[painPoint.length - 1]) : params;
        }

        // Private methods
        function _validateSubmit(e) {
            var submitURL = '',
                submitBtn,
                success,
                submitFailedMessage = element.querySelector('.submit-failed-message'),
                params;

            e.preventDefault();

            EDC.forms.validateForm(formElements);

            if (!element.querySelectorAll('input.error, select.error, checkbox.error').length) {
                submitBtn = element.querySelector('button[type="submit"]');
                submitURL = element.getAttribute('action');
                params = _duplicateExportPain(EDC.forms.getFormData(element));

                EDC.forms.disableSubmit(submitBtn);

                EDC.forms.submitFormData('POST', submitURL, params, function () {
                    success = element.querySelector('.success');
                    success.querySelector('.time').innerHTML = formElements.time.querySelector('option[value="' + formElements.time.value + '"]').text;
                    success.querySelector('.date').innerHTML = formElements.date.value;
                    element.querySelector('.content').classList.add('hide');
                    success.classList.add('show');
                    success.setAttribute('data-dtm-tracking', 'success');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);

                    // Tracking
                    _trackEvent(e);
                }, submitFailedMessage);
            }
        }

        function _compareDate() {
            var el = element.querySelector('.preferredDate input[name="date"]'),
                preferredTime = element.querySelector('.preferredTime'),
                timeSelect = preferredTime.querySelector('select'),
                timeMenu = preferredTime.querySelector('.menu'),
                timeLabel = preferredTime.querySelector('.text'),
                currentDate = new Date(),
                selectedDate = new Date(el.value),
                currentHours,
                etOffset = (EDC.forms.isDLS(currentDate) ? -4 : -5) * 60,
                utcOffset = currentDate.getTimezoneOffset(),
                optionHour,
                isToday;

            // Convert local timezone to UTC time zone
            currentDate.setMinutes(currentDate.getMinutes() + etOffset);
            // Convert UTC time zone to Eastern time zone
            currentDate.setMinutes(currentDate.getMinutes() + utcOffset);
            // Get earlier hour to be attended
            currentHours = currentDate.getHours() + (currentDate.getMinutes() / 60) + 4;
            isToday = currentDate.getFullYear() === selectedDate.getFullYear() &&
                currentDate.getMonth() === selectedDate.getMonth() &&
                currentDate.getDate() === selectedDate.getDate();

            // Shows Available Hours
            timeSelect.querySelectorAll('option').forEach(function (option) {
                var menuOption;

                optionHour = option.dataset.begin;
                option.removeAttribute('selected');

                if (optionHour && option.value) {
                    menuOption = timeMenu.querySelector('.item[data-value="' + option.value + '"]');
                }

                if (menuOption) {
                    if (isToday && optionHour < currentHours) {
                        menuOption.classList.add('hide');
                    } else {
                        menuOption.classList.remove('hide');
                    }
                }
            });

            // Resets the dropdown
            timeLabel.innerHTML = timeSelect.getAttribute('data-default-value');
            timeLabel.classList.add('default');
            timeMenu.querySelectorAll('.item').forEach(function (item) {
                item.classList.remove('active');
                item.classList.remove('selected');
            });

            timeSelect.value = '';
        }

        function _initializeLibraries() {
            var datePickerCreated = [],
                currentLanguage = element.getAttribute('data-lang');

            element.querySelectorAll('.preferredDate input[name="date"]').forEach(function (date, index) {
                datePickerCreated[index] = new datepicker('dp' + index, date, true, currentLanguage, _compareDate);

                EDC.utils.attachEvents(date, ['click', 'focus'], function (e) {
                    e.stopPropagation();
                    e.preventDefault();

                    datePickerCreated[index].showDlg();
                });

                element.querySelectorAll('table td').forEach(function (el) {
                    EDC.utils.attachEvents(el, 'click', function () {
                        setTimeout(function () {
                            EDC.forms.validateField(date);
                        }, 100);
                    });
                });
            });
        }

        function _attachEvents() {
            EDC.forms.validateChange(formElements);
            EDC.utils.attachEvents(element, 'submit', _validateSubmit);
            EDC.utils.attachEvents(element.querySelectorAll('.success a[href^="tel:"]'), 'click', _trackEvent);

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
            });
        }

        _initializeLibraries();
        _attachEvents();
        EDC.forms.fillHiddenFields(element);
        EDC.forms.setHeightLabelsOnLoad(formRows);

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var scheduleCallForms = document.querySelectorAll('form.schedule-call:not([data-component-state="initialized"])');

        if (scheduleCallForms) {
            scheduleCallForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', scheduleCallForm.init);