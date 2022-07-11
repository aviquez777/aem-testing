(function ($, $document, $window) {
    "use strict";

    $document.on("dialog-ready", function () {
        var dialogProductFormCards = $('coral-dialog[open]').find('[data-dialog-id="dialog-stickynav"]'),
            currentTimeMillis,
            countValidatorId,
            $multifield,
            maximumNumberOfItems,
            $countValidatorField;

        if (dialogProductFormCards.length) {
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
            $multifield.on("click", "button[coral-multifield-add]", function (e) {
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
                    $addButtons = $multifield.find("button[coral-multifield-add]"),
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

            function show($el, message) {
                this.clear($countValidatorField);
            }

            function clear() {
            }
        }
    });
})($, $(document), $(window));