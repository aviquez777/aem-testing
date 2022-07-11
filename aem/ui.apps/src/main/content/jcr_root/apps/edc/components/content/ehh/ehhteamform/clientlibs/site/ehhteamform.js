var ehhTeamFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var servletURL = element.getAttribute('data-url'),
            isDefault = element.classList.contains('default'),
            form = element.querySelector('.cp-content'),
            fiContent = element.querySelector('.fi-content'),
            ehhFilter = document.querySelector('.c-ehh-filter'),
            isForm = false,
            formElements,
            submitBtn,
            resourceBtn,
            serviceURL,
            submitFailedMessage,
            successMessage,
            userName,
            userEmail,
            externalId,
            formURL,
            selectedValue,
            showModal = element.getAttribute('show-modal') === 'true',
            modal = element.querySelector('.form-modal'),
            formModalCloseBtn = modal ? modal.querySelector('.modal-close') : null,
            formModalCTA1 = modal ? modal.querySelector('.share-question') : null,
            formModalCTA2 = modal ? modal.querySelector('.already-shared') : null;

        // Data layer tracking
        function _trackEvent(buttonText, engagementType, eventValue) {
            var obj = {
                eventInfo: {
                    eventComponent: isForm ? element.dataset.eventForm : element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName + ' - ' + buttonText.toLowerCase(),
                    eventAction: element.dataset.eventAction,
                    eventText: buttonText.toLowerCase(),
                    eventValue: eventValue ? eventValue.toLowerCase() : '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: '',
                    engagementType: engagementType,
                    eventLevel: element.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }
        // Helper function to submit the form
        function _formSubmit(e) {
            var formInfo = form.querySelector('.form-info'),
                formFields = form.querySelector('.form-fields'),
                errorField,
                buttonText,
                engagementType;

            e.preventDefault();

            EDC.forms.validateForm(formElements);

            // Gets values from myEDC
            userName.value = EDC.props.userData.firstName ? EDC.props.userData.firstName : '';
            userEmail.value = EDC.props.userData.email ? EDC.props.userData.email : '';
            externalId.value = EDC.props.userData.externalID ? EDC.props.userData.externalID : '';
            formURL.value = window.location.href;

            if (!form.querySelectorAll('.form-control.error, input.error').length && serviceURL) {
                EDC.forms.disableSubmit(submitBtn);

                // Gets values for Data Layer
                buttonText = submitBtn.getAttribute('data-english-text') ? submitBtn.getAttribute('data-english-text') : submitBtn.text;
                engagementType = submitBtn.getAttribute('data-engagement-type') ? submitBtn.getAttribute('data-engagement-type') : '';
                selectedValue = form.querySelector('select').value;

                // Shows processing screen
                EDC.utils.scrollTo('top', EDC.utils.getPosition(form).y);
                if (!isDefault) {
                    formInfo.classList.add('hide');
                }
                formFields.classList.add('hide');
                form.classList.add('processing');

                EDC.utils.fetchJSON('POST', serviceURL, EDC.forms.getFormData(form), function () {
                    // Hides processing screen
                    form.classList.remove('processing');
                    successMessage.classList.remove('hide');
                    _trackEvent(buttonText, engagementType, selectedValue);

                    if (ehhFilter) {
                        ehhFilter.setAttribute('data-question-submitted', true);
                    }
                }, function () {
                    // Error on server communication, delay or related issues
                    if (submitFailedMessage) {
                        form.classList.remove('processing');
                        formInfo.classList.remove('hide');
                        form.classList.remove('hide');
                        submitFailedMessage.classList.remove('hide');
                        formFields.classList.remove('hide');
                        EDC.forms.enableSubmit(submitBtn);

                        if (ehhFilter) {
                            ehhFilter.removeAttribute('data-question-submitted');
                        }
                    }
                });

            } else {
                errorField = element.querySelector('.form-control.error');

                if (errorField) {
                    errorField.focus();
                }
            }
        }

        // Helper function to initialize the form
        function _initializeForm() {
            formElements = form.elements;
            submitBtn = form.querySelector('button[type="submit"]');
            serviceURL = form.getAttribute('action');
            submitFailedMessage = form.querySelector('.submit-failed-message');
            successMessage = form.querySelector('.form-success');
            userName = form.querySelector('input[name="userName"]');
            userEmail = form.querySelector('input[name="userEmail"]');
            externalId = form.querySelector('input[name="externalId"]');
            formURL = form.querySelector('input[name="formURL"]');

            EDC.forms.validateChange(formElements);
        }

        function _trackResource(e) {
            var target = e.target.classList.contains('resource-btn') ? e.target : e.target.parentElement,
                buttonText = target.getAttribute('data-english-text') ? target.getAttribute('data-english-text') : target.text,
                engagementType = target.getAttribute('data-engagement-type') ? target.getAttribute('data-engagement-type') : '';
console.warn("buttonText", buttonText)
            _trackEvent(buttonText, engagementType);
        }

        function _closeModal() {
            modal.classList.add('hide');
        }

        function _handleModal() {
            if (showModal) {
                modal.classList.remove('hide');
                EDC.utils.attachEvents(window, 'click', function (e) {
                    if (e.target === modal.querySelector('.modal-shade')) {
                        _closeModal();
                    }
                });
            }
        }

        function _alreadyShared() {
            var commentField = form.querySelector('textarea[name="comment"]'),
                cta = successMessage.querySelector('a.c-interaction-button'),
                title = successMessage.querySelector('h3.success-title'),
                text = successMessage.querySelector('.success-description');

            _closeModal();
            commentField.removeAttribute('required');
            if (cta) {
                cta.innerHTML = cta.getAttribute('data-special-cta');
            }

            if (title) {
                title.innerHTML = title.getAttribute('data-special-title');
            }

            if (text) {
                text.innerHTML = text.getAttribute('data-special-text');
            }

            submitBtn.click();
        }

        function _attachEvents() {
            if (isForm) {
                EDC.utils.attachEvents(form, 'submit', _formSubmit);
            } else {
                EDC.utils.attachEvents(resourceBtn, 'click', _trackResource);
            }

            if (formModalCloseBtn) {
                EDC.utils.attachEvents(formModalCloseBtn, 'click', _closeModal);
            }

            if (formModalCTA1) {
                EDC.utils.attachEvents(formModalCTA1, 'click', _closeModal);
            }

            if (formModalCTA2) {
                EDC.utils.attachEvents(formModalCTA2, 'click', _alreadyShared);
            }
        }

        EDC.forms.getUserProfileType(servletURL, function () {
            element.classList.add('form');
            form.classList.remove('hide');
            isForm = true;
            _initializeForm();
            _attachEvents();
            _handleModal();
        }, function () {
            fiContent.classList.remove('hide');
            resourceBtn = element.querySelector('.resource-btn');
            _attachEvents();
        });
    }

    // Public methods
    function init() {
        var ehhTeamForm = document.querySelectorAll('.c-ehh-team-form:not([data-component-state="initialized"])');

        if (ehhTeamForm) {
            ehhTeamForm.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ehhTeamFormJS.init);