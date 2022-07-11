var formFieldInfoJS = (function () {
    'use strict';

    // Public methods
    function init() {
        var formFieldInfos = document.querySelectorAll('.c-form-field-info:not([data-component-state="initialized"])');

        if (formFieldInfos) {
            formFieldInfos.forEach(function (elem) {
                EDC.forms.initializeFormReviewField(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', formFieldInfoJS.init);