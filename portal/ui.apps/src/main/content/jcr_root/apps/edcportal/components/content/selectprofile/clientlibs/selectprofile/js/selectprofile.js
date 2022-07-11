var selectProfileForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var radios = element.querySelectorAll('input[type="radio"]'),
            page1Error = element.querySelector('.submit-failed-message');

        function _hideElement(elem) {
          if (typeof (elem) !== 'undefined' && !elem.classList.contains('hide')) {
              elem.classList.add('hide');
          }
        }

        function _showElement(elem) {
          if (typeof (elem) !== 'undefined' && elem.classList.contains('hide')) {
              elem.classList.remove('hide');
          }
        }

        function _trackEvent(e, num) {
            var el = e.target,
                userSegment,
                selectedRadio,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: element.dataset.eventType.toLowerCase(),
                        eventName: element.querySelector('.page-' + num).dataset.eventName.toLowerCase(),
                        eventAction: element.dataset.eventAction.toLowerCase(),
                        eventValue: element.dataset.eventValue.toLowerCase(),
                        eventPage: EDC.props.pageNameURL.toLowerCase(),
                        eventPageName: EDC.utils.getPageName().toLowerCase(),
                        engagementType: element.dataset.eventEngagement.toLowerCase()
                    }
                };
            radios.forEach(function (radio) {
                if (radio.checked) {
                    if (radio.value.toLowerCase() == "other") {
                        selectedRadio = "other-" + el.querySelector('select[name="otherType"]').value;
                    } else {
                        selectedRadio = radio.value.toLowerCase();
                    }
                }
            });
            function _getMultiDropdownData(id) {
                //var elements = el.querySelector('[id^=' + id+"]") ? el.querySelector('[id^=' + id+"]").closest('.dropdown').querySelectorAll('.visible') : [],elementsValue = '';
                var elements = el.querySelector('[data-form-field="'+id+'"]') ? el.querySelector('[data-form-field="'+id+'"]').querySelectorAll('.menu .selected-item') : [],elementsValue = '';

                if (elements.length > 0) {
                    elements.forEach(function (item, index) {
                        elementsValue += item.textContent;
                        if ((index + 1) < elements.length) {
                            elementsValue += '|';
                        }
                    });
                }
                return elementsValue.toLowerCase();
            }

            if (num === 1) {
                obj.eventInfo.eventText = el.textContent.toLowerCase();

                userSegment = {
                    profileType: selectedRadio
                };

            } else if (num === 2) {
                obj.eventInfo.eventText = el.querySelector('button[type="submit"]').textContent.toLowerCase();
                obj.eventInfo.destinationPage = element.getAttribute('action').toLowerCase();
                userSegment = {
                    annualSales: el.querySelector('select[id^=annualSales]') ? el.querySelector('select[id^=annualSales]').value.toLowerCase() : '',
                    exportStatus: el.querySelector('select[id^=tradeStatus]') ? el.querySelector('select[id^=tradeStatus]').value.toLowerCase() : '',
                    exportChallenges: _getMultiDropdownData('painPoint'),
                    industrySector: el.querySelector('select[id^=industry]') ? el.querySelector('select[id^=industry]').value.toLowerCase() : '',
                    sellCountries: _getMultiDropdownData('marketsInt'),
                    buyCountries: _getMultiDropdownData('countriesToBuyFrom'),
                    marketsInterest: _getMultiDropdownData('marketsInt'),
                    profileType: selectedRadio
                };
            }
            EDC.utils.userSegmentTracking(userSegment);
            EDC.utils.dataLayerTracking(obj);
        }

        function _trackDropdown(e) {
            var obj,userSegment,
                selectedRadio,
                el = e.currentTarget;

            if (el.classList.contains('active')) {
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: 'drop-down',
                        eventName: 'drop-down activation - other',
                        eventAction: element.dataset.eventAction.toLowerCase(),
                        eventPage: EDC.props.pageNameURL.toLowerCase(),
                        eventPageName: EDC.utils.getPageName().toLowerCase(),
                        engagementType: '1'
                    }
                };

                EDC.utils.dataLayerTracking(obj);
                el.removeEventListener('click', _trackDropdown);
            }
        }

        function _validateSubmit(e) {
            var oneChecked = false;
          
            for (var i = 0, length = radios.length; i < length; i++) {
              if (radios[i].checked) {
                oneChecked = true;
                break;
              }
            }

            if (!oneChecked) {
              _showElement(page1Error);
            }

            if (page1Error && !page1Error.classList.contains('hide')) {
                e.preventDefault();
            } else {
                _trackEvent(e, 1);
            }
        }

        function _toggleSelect() {
            _hideElement(page1Error);
        }

        function _attachEvents() {
            EDC.utils.attachEvents(radios, 'click', _toggleSelect);
            EDC.utils.attachEvents(element, 'submit', _validateSubmit);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var selectProfileForms = document.querySelectorAll('form.c-select-profile:not([data-component-state="initialized"])');

        if (selectProfileForms) {
          selectProfileForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

// This should be uncommented to load component on AEM
document.addEventListener('DOMContentLoaded', selectProfileForm.init);
