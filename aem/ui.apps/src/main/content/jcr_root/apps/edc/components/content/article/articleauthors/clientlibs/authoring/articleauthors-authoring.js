(function ($, $document) {
    "use strict";
    $document.onDialogReady('edc/components/content/article/articleauthors', function () {

        var loc = "" + window.location.href,
            primaryCheckbox = $('input[type="checkbox"][name="./primaryshowbio"]'),
            primaryBiography = $('input[name="./primaryshortbio"]'),
            secondaryCheckbox = $('input[type="checkbox"][name="./secondaryshowbio"]'),
            secondaryBiography = $('input[name="./secondaryshortbio"]');

        if (loc.indexOf("/fr/") > -1) {  // remap the picker root path to French bios
            var primaryOverlay = $("foundation-autocomplete[name='./primaryauthorpath']"),
                secondaryOverlay = $("foundation-autocomplete[name='./secondaryauthorpath']"),
                pickerSource = primaryOverlay.attr("pickersrc");

            pickerSource = pickerSource ? pickerSource.replace("%2fen%2f", "%2ffr%2f") : pickerSource;
            primaryOverlay.attr("pickersrc", pickerSource);

            pickerSource = secondaryOverlay.attr("pickersrc");
            pickerSource = pickerSource ? pickerSource.replace("%2fen%2f", "%2ffr%2f") : pickerSource;
            secondaryOverlay.attr("pickersrc", pickerSource);
        }

        /**
         * Show/hide RTEs based on checkbox selection
         */
        function toggleBiographyRTE(checkboxChecked, rteWrapper) {
            if (checkboxChecked) {
                rteWrapper.removeClass('hide');
            } else {
                rteWrapper.addClass('hide');
            }
        }

        /**
         * Events
         */
        function handleEl(e, type) {
            var element = $(e.currentTarget),
                bioWrapper = document.querySelector('input[name="./' + type + 'shortbio"]').closest('.coral-Form-fieldwrapper');

            toggleBiographyRTE(element.is(':checked'), $(bioWrapper));
        }

        primaryCheckbox.click(function (e) {
            handleEl(e, 'primary');
        });

        secondaryCheckbox.click(function (e) {
            handleEl(e, 'secondary');
        });

        toggleBiographyRTE(primaryCheckbox.is(':checked'), primaryBiography.closest('.coral-Form-fieldwrapper'));
        toggleBiographyRTE(secondaryCheckbox.is(':checked'), secondaryBiography.closest('.coral-Form-fieldwrapper'));
    });
})($, $(document));