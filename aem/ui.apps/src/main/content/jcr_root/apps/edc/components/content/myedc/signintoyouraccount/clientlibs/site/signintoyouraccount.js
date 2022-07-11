var signInJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var tabsEl = element.querySelector('ul.tab-labels'),
            selectEl = element.querySelector('select'),
            tabLabels = element.querySelectorAll('.tab-label'),
            dropdownLabel = element.querySelector('.select-label'),
            selectVal = 1;

        // Private methods
        function _tabClicked(e) {
            EDC.utils.displayTabs(e);

            tabsEl.children.forEach(function (item, index) {
                if (item === e.target.closest('li')) {
                    selectVal = index;
                }
            });

            selectEl.value = selectVal;

            EDC.utils.setHashTag(element.getAttribute('data-component-target'), e.currentTarget.getAttribute('data-link-target'));
        }

        function _dropdownChanged(e) {
            tabsEl.children[e.target.value].querySelector('a.tab-label').click();
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(tabLabels, 'click', _tabClicked);
            EDC.utils.attachEvents(dropdownLabel, 'change', _dropdownChanged);
        }

        _attachEvents();

        setTimeout(function () {
            EDC.utils.scrollToSelectedTab(element, tabLabels);
        }, 1000);
    }

    // Public methods
    function init() {
        var signIn = document.querySelectorAll('.c-sign-in:not([data-component-state="initialized"])');

        if (signIn) {
            signIn.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', signInJS.init);