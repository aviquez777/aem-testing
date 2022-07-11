var inListSupplierProfileJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var services = element.querySelector('.section.services ul'),
            isDevice;

        // Private methods
        function _isDevice() {
            return window.innerWidth < EDC.props.media.md;
        }

        function _checkDevice() {
            isDevice = _isDevice();

            if (!isDevice && services) {
                services.classList.add('split');
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(window, 'resize', function () {
                if (isDevice !== _isDevice() && services) {
                    isDevice = _isDevice();
                    if (isDevice) {
                        services.classList.remove('split');
                    } else {
                        services.classList.add('split');
                    }
                }
            });
        }

        _checkDevice();
        _attachEvents();
    }

    // Public methods
    function init() {
        var supplierProfile = document.querySelectorAll('.c-inlist-supplier-profile:not([data-component-state="initialized"])');

        if (supplierProfile) {
            supplierProfile.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', inListSupplierProfileJS.init);