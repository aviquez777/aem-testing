(function ($window, $document, Granite, $) {
    "use strict";
    $document.on("dialog-ready", function () {

        var dialogValuePropBanner = $('coral-dialog[open]').find('.cq-dialog-content[data-dialog-id="dialog-valuepropositionbanner"]'),
            currentTimeMillis,
            countValidatorId,
            $multifield,
            maximumNumberOfItems,
            $countValidatorField;

        if (dialogValuePropBanner.length) {
            currentTimeMillis = new Date().getTime();
            countValidatorId = "multifield-validator-" + parseInt(currentTimeMillis);
            $countValidatorField = $("#" + countValidatorId);
            $multifield = $("coral-multifield");
            //=====================================================================
            // Change this value to change minimum & maximum number of items
            // allowed
            //=====================================================================
            maximumNumberOfItems = 4;
            //=====================================================================
            adjustMultifieldUI(); // Disable "Add" btns if already fieldLimit items
            //=====================================================================
            //                          F U N C T I O N S
            //=====================================================================
            $.validator.register({
                selector: $countValidatorField,
                show: show,
                clear: clear
            });
            //=====================================================================
            // When the "Add" button is clicked...
            //=====================================================================
            $multifield.on("click", 'button[coral-multifield-add]', function (e) {
                adjustMultifieldUI();
            });
            //=====================================================================
            // When the "Delete" button is clicked...
            //=====================================================================
            $document.on("click", ".coral3-Multifield-remove", function (e) {
                adjustMultifieldUI();
            });
            //=====================================================================
            // Iterate the "Add" buttons, finding the number of items under its
            // parent (this is the number of bullet items). If this number is >=
            // the current fieldLimit, disable the "Add" button and change its
            // inner text. Otherwise, enable that button & ensure its text is "Add"
            //=====================================================================
            function adjustMultifieldUI() {
                var i,
                    $addButtons = $multifield.find('button[coral-multifield-add]'),
                    $addButton;

                $countValidatorField.checkValidity();
                $countValidatorField.updateErrorUI();

                for (i = 0; i < $addButtons.length; i++) {
                    $addButton = $addButtons[i];
                    //---------------------------------------------------------
                    // If we've hit the maximum, disable the "Add" button
                    //---------------------------------------------------------
                    if ($addButton.parentNode.getElementsByTagName("coral-multifield-item").length >= maximumNumberOfItems) {
                        $addButton.disabled = true;
                        $addButton.innerText = "Maximum items reached";
                    } else {
                        $addButton.disabled = false;
                        $addButton.innerText = "Add";
                    }
                }
            }

            function show() {
                this.clear($countValidatorField);
            }

            function clear() {
            }
        }
        /**
         * Show the colorBlock and textalignment fields when the bannertype is 'campaign'
         */
        function showHideOptions() {
            var bannerType = $('coral-select input[name="./bannertype"]').val(),
                colorBlock = $('[data-validation="valuepropositionbanner.field.colorBlock"]'),
                textalignment = $('[data-validation="valuepropositionbanner.field.textalignment"]');

            if(bannerType === 'home') {
                colorBlock.closest('.coral-Form-fieldwrapper').addClass('hide');
                textalignment.closest('.coral-Form-fieldwrapper').addClass('hide');
            } else {
                colorBlock.closest('.coral-Form-fieldwrapper').removeClass('hide');
                textalignment.closest('.coral-Form-fieldwrapper').removeClass('hide');
            }
        }

        /**
         * Events
         */
        $document.on('change', 'coral-select[name="./bannertype"]', showHideOptions);

        setTimeout(showHideOptions, 500) ;
    });
})($(window), $(document), Granite, $);