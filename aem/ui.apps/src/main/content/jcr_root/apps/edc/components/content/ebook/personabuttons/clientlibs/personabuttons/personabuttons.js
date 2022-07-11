var accordionPersonaButton = (function () {
    'use strict';


    function _initialize(element) {
        var btnPersona = element.querySelectorAll('.list-persona a'),
            personaIsSet = false;

        // Data Layer
        function _trackEvent(personaButton) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventType + ' ' + element.dataset.eventName + ' - ' + personaButton.textContent,
                    eventAction: element.dataset.eventAction,
                    eventText: personaButton.textContent,
                    eventValue: personaButton.parentNode.dataset.persona,
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: '',
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: '',
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _cleanButtons() {
            btnPersona.forEach(function (item) {
                item.closest('li').classList.remove('active');
            });
        }

        // for myEDC we need to tell from FE the persona and set the first chapter's url
        function _setPersonaLink(elem, path, persona) {
            var personaStr = '_!_' + persona,
                currentURL = elem.getAttribute('href'),
                newURL;

            if (elem.classList.contains('in')) {
                // link for a logged in user
                newURL = path.replace('/content/edc', '') + '.html?persona=' + persona;
            } else {
                // other links
                newURL = currentURL.substring(0, currentURL.lastIndexOf('_!_')) + '_!_';

                if (!personaIsSet) {
                    newURL = newURL + path + '.html';
                }
                newURL += personaStr;
            }

            elem.setAttribute('href', newURL);
        }

        function _showContentPersona(e) {
            var that = this,
                validate,
                contentPersonas = document.querySelectorAll('.c-persona-content'),
                clickedBtn = that.getAttribute('data-tab-content'),
                btnContainer = that.closest('li'),
                profilingForm = document.querySelector('.c-progressive-profiling form[data-mode="mode_ebook_persona"]'),
                profilingElem,
                myedcContainer,
                myedcLinks,
                ppLinks;


            e.preventDefault();

            if (profilingForm) {
                profilingElem = profilingForm.parentElement;
                myedcContainer = profilingElem.querySelector('.myedc-container');
            }

            if (contentPersonas.length) {
                if (btnContainer.className.indexOf('active') === -1) {
                    _cleanButtons();
                    contentPersonas.forEach(function (item, index) {
                        item.classList.remove('show');
                        validate = contentPersonas[index].getAttribute('data-tab-content');

                        if (validate === clickedBtn) {
                            contentPersonas[index].classList.add('show');

                            if (profilingElem) {
                                ppLinks = profilingForm.querySelectorAll('.form-ctas a:not(.cancel)');

                                profilingForm.classList.remove('hide');
                                profilingElem.classList.remove('email-closed');

                                if (ppLinks) {
                                    ppLinks.forEach(function (elem) {
                                        _setPersonaLink(elem, btnContainer.getAttribute('data-path'), btnContainer.getAttribute('data-persona'));
                                    });
                                }

                                if (myedcContainer) {
                                    myedcLinks = myedcContainer.querySelectorAll('a');

                                    myedcContainer.classList.remove('hide');

                                    if (myedcLinks) {
                                        myedcLinks.forEach(function (elem) {
                                            _setPersonaLink(elem, btnContainer.getAttribute('data-path'), btnContainer.getAttribute('data-persona'));
                                        });
                                    }
                                }

                                personaIsSet = true;
                            }

                            _trackEvent(that);
                        }
                    });
                    btnContainer.classList.add('active');
                } else {
                    btnContainer.classList.remove('active');
                    contentPersonas.forEach(function (item, index) {
                        validate = contentPersonas[index].getAttribute('data-tab-content');

                        if (validate === clickedBtn) {
                            contentPersonas[index].classList.remove('show');

                            if (profilingElem) {
                                profilingForm.classList.add('hide');
                                profilingElem.classList.add('email-closed');
                            }

                            if (myedcContainer) {
                                myedcContainer.classList.add('hide');
                            }
                        }
                    });
                }
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(btnPersona, 'click', _showContentPersona);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var personaButton = document.querySelectorAll('.c-persona-button:not([data-component-state="initialized"])');

        if (personaButton) {
            personaButton.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', accordionPersonaButton.init);