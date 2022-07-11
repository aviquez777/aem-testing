function logoHeader() {
    'use strict';

    var skipBtn = document.querySelector('#skip'),
        mainContent = document.querySelector('#maincontent');

    function init() {
        if (typeof (skipBtn) !== 'undefined') {
            skipBtn.addEventListener('click', function (e) {
                e.preventDefault();
                EDC.utils.scrollTo('top', EDC.utils.getPosition(mainContent).y);

                // when focus leaves this element,
                // remove the tabindex attribute
                mainContent.addEventListener('blur', function () {
                    mainContent.removeAttribute('tabindex');
                });

                // Setting 'tabindex' to -1 takes an element out of normal
                // tab flow but allows it to be focused via javascript
                mainContent.setAttribute('tabindex', -1);
                mainContent.focus();
            });
        }
    }

    return {
        init: init
    };
}

// logoHeader().init();