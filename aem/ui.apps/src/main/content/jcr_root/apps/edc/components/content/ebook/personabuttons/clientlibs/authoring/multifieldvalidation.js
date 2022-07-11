(function ($, $document) {
    'use strict';

    //---------------------------------------------------------
    // Modify rootpath to current ebook.
    //---------------------------------------------------------
    function adjustMultifieldPath(multifields, lang, teaserName) {
        if (multifields.length) {
            var items = multifields.find('foundation-autocomplete'),
                pickersrc;

            if (items.length) {
                pickersrc = items.first().attr('pickersrc').replace(/%7b0%7d/ig, lang).replace(/%7b1%7d/ig, teaserName);
                items.attr('pickersrc', pickersrc);
            }
        }
    }

    $document.onDialogReady('edc/components/content/ebook/personabuttons', function (e, form) {
        var personaButtonMultifields = form.find('coral-multifield[data-granite-coral-multifield-name^="./chapterbtn"]'),
            href = form.data('cq-dialog-pageeditor') || '',
            langIndex = href.search(/\/([a-z]){2}(\/|\.)/g),
            lang = langIndex >= 0 ? href.substring(langIndex + 1, langIndex + 3) : 'en',
            teaserName = href.substring(href.lastIndexOf('/') + 1, href.lastIndexOf('.html'));

        //---------------------------------------------------------
        // Set correct root path for each pathfield.
        //---------------------------------------------------------
        adjustMultifieldPath(personaButtonMultifields, lang, teaserName);

        //=====================================================================
        // When the "Add" button is clicked...
        //=====================================================================
        form.on('click', 'coral-multifield[data-granite-coral-multifield-name^="./chapterbtn"] > button[coral-multifield-add]', function (e) {
            adjustMultifieldPath($(e.target).closest('coral-multifield[data-granite-coral-multifield-name^="./chapterbtn"]'), lang, teaserName);
        });
    });
})($, $(document));
