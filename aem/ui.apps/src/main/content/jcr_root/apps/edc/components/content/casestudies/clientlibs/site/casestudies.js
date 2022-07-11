var caseStudiesJS = (function () {
    'use strict';

    function _initialize(element) {
        var imgLinks = element.querySelectorAll('a.video-img'),
            titleLinks = element.querySelectorAll('.title a'),
            headlineLinks = element.querySelectorAll('.headline a'),
            casesGrid = element.querySelector('.content-video-grid'),
            caseClose = element.querySelectorAll('.case-close'),
            casesExpanded = element.querySelectorAll('.single-cases .case-study');

        // Tracking purposes
        function _trackEvent(e) {
            var anchor = e.currentTarget,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventName,
                        eventAction: anchor.dataset.eventAction,
                        eventText: anchor.text.trim(),
                        eventPage: EDC.props.pageNameURL,
                        engagementType: element.dataset.eventEngagement,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _expandCase(e) {
            var thisCase = element.querySelector('.case-study[data-case="' + e.currentTarget.getAttribute('data-case') + '"]'),
                btn = thisCase.querySelector('.start-video'),
                caseTouched = thisCase.className.indexOf('case-touched') > -1;

            thisCase.classList.remove('hide');

            if (btn && !caseTouched) {
                btn.click();
                thisCase.classList.add('case-touched');
            }

            setTimeout(function () {
                thisCase.querySelector('.full-width-video').classList.add('show-video');
                if (btn && caseTouched) {
                    setTimeout(function () {
                        btn.click();
                    }, 1000);
                }
            }, 700);

            _trackEvent(e);
        }

        function _closeCase(e) {
            e.preventDefault();
            PubSub.publish('stop-video', true);
            if (casesGrid.className.indexOf('hide') > -1) {
                casesGrid.classList.remove('hide');
                casesExpanded.forEach(function (item) {
                    if (item.className.indexOf('hide') === -1) {
                        item.classList.add('hide');
                        item.querySelector('.full-width-video').classList.remove('show-video');
                    }
                });
            }
        }

        function _detectEsc(e) {
            if (e.which === 27) {
                _closeCase(e);
            }
        }

        function _itemClicked(e) {
            e.preventDefault();
            if (casesGrid.className.indexOf('hide') === -1) {
                casesGrid.classList.add('hide');
                _expandCase(e);
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(imgLinks, 'click', _itemClicked);
            EDC.utils.attachEvents(titleLinks, 'click', _itemClicked);
            EDC.utils.attachEvents(headlineLinks, 'click', _itemClicked);
            EDC.utils.attachEvents(caseClose, 'click', _closeCase);
            EDC.utils.attachEvents(document, 'keyup', _detectEsc);
        }

        EDC.utils.setMobileText(element);
        EDC.utils.changeMobileTextOnResize();
        _attachEvents();
    }

    // Public methods
    function init() {
        var cs = document.querySelectorAll('.c-case-studies:not([data-component-state="initialized"])');

        if (cs) {
            cs.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', caseStudiesJS.init);