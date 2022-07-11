var pciHeaderNavInit = (function () {
    'use strict';

    // Initialize the component
    function _initialize() {
        var skipBtn = document.querySelector('#skip'),
            mainContent;

        // Private methods
        function _initComponent() {
            if (typeof (skipBtn) !== 'undefined') {
                skipBtn.addEventListener('click', function (e) {
                    e.preventDefault();
                    mainContent = document.querySelector('#maincontent');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(mainContent).y);

                    //     // when focus leaves this element,
                    //     // remove the tabindex attribute
                    mainContent.addEventListener('blur', function () {
                        mainContent.removeAttribute('tabindex');
                    });

                    // // Setting 'tabindex' to -1 takes an element out of normal
                    // // tab flow but allows it to be focused via javascript
                    mainContent.setAttribute('tabindex', -1);
                    mainContent.focus();
                });
            }
        }

        _initComponent();
    }

    // Public methods
    function init() {
        var pciHeader = document.querySelectorAll('.pci-main-header:not([data-component-state="initialized"])');

        if (pciHeader) {
            pciHeader.forEach(function (elem) {
                _initialize();
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', pciHeaderNavInit.init);