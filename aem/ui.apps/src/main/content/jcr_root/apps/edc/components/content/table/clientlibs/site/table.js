var tableScrolling = (function () {
    'use strict';

    function checkTableScroll(e) {
        var container = e.currentTarget,
            scrollDiff = container.scrollWidth - container.clientWidth;

        e.preventDefault();

        if (container.scrollLeft >= scrollDiff) {
            container.classList.remove('fade-shadow');
        } else {
            container.classList.add('fade-shadow');
        }
    }

    function verifyScroll(elem) {
        var table = elem.querySelector('table');

        if (table.clientWidth > elem.offsetWidth) {
            elem.classList.add('fade-shadow');
        } else {
            elem.classList.remove('fade-shadow');
        }
    }

    function resizeTables() {
        var scroll = document.querySelectorAll('.table .table-container'),
            i;

        if (scroll) {
            for (i = 0; i < scroll.length; i++) {
                verifyScroll(scroll[i]);
            }
        }
    }

    window.addEventListener('resize', resizeTables);

    // Public methods
    function init() {
        var scroll = document.querySelectorAll('.table .table-container'),
            i;

        if (scroll) {
            for (i = 0; i < scroll.length; i++) {
                scroll[i].addEventListener('scroll', checkTableScroll);
                verifyScroll(scroll[i]);
            }
        }
    }

    return {
        init: init
    };
})();

// This should be uncommented to load component on AEM
document.addEventListener('DOMContentLoaded', tableScrolling.init);