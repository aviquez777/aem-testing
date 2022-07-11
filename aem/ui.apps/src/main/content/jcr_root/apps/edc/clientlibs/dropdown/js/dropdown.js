/**
* Component initialization
* Behaviors:
*       allowReselection: true // Alows to fire on click / on change event on selected item
*       action: activate // Default value for single select
*       action: custom // Override action for multi select dropdown.
*
* Settings:
*       onShow: Custom behavior on dropdown click to open options
*       onHide: Custom behavior on dropdown option click, blur or closed
*       onChange: Custom behavior only for single dropdown to avoid closing the element on blur
*       duration: 0 // Modifies transition duration for user friendly experience on mobile
*/

var dropdownJS = (function () {
    'use strict';

    // Public methods
    function init() {
        var dropdown = document.querySelectorAll('.c-dropdown:not([data-component-state="initialized"])');

        if (dropdown) {
            dropdown.forEach(function (elem) {
                EDC.dropdown.init(elem);
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', dropdownJS.init);