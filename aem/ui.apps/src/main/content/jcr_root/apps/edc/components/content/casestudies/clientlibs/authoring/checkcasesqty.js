(function ($, $document) {
    'use strict';
    //Change qty and messages as necessary
    $document
        .onDialogReady(
            'edc/components/content/casestudies',
            function (e, form) {

                var qty = 3,
                    multifield = form.find('coral-multifield[data-granite-coral-multifield-name^="./slides"]'),
                    slidesQty = multifield.find('> coral-multifield-item').length;

                if (slidesQty > 0) {

                    if (slidesQty >= qty) {
                        multifield.find('button:last').hide();
                    }

                    if (slidesQty > qty) {
                        var remove = slidesQty - qty;

                        e.preventDefault();
                        $(window).adaptTo('foundation-ui').alert('Error',
                            'There must be 3 Case Studies, please remove ' + remove + ' item (s).',
                            'error');
                    }

                    if (slidesQty < qty) {
                        var add = qty - slidesQty;

                        e.preventDefault();
                        $(window).adaptTo('foundation-ui').alert('Error',
                            'There must be 3 Case Studies, please add ' + add + ' more.', 'error');
                    }

                }
            });
})($, $(document));
