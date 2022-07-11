(function ($, $document, $window) {
    "use strict";

    $document.on("dialog-ready", function () {
        var dialogProductFormCards = $('coral-dialog[open]').find('.cq-dialog-content[data-dialog-id="dialog-productformcards"]'),
            currentTimeMillis,
            countValidatorId,
            $multifield,
            minimumNumberOfItems,
            maximumNumberOfItems,
            $countValidatorField;

        if (dialogProductFormCards.length) {
            currentTimeMillis = new Date().getTime();
            countValidatorId = "multifield-validator-" + parseInt(currentTimeMillis);
            $countValidatorField = $("#" + countValidatorId);
            $multifield = $("coral-multifield.coral-Multifield");
            //=====================================================================
            // Change this value to change minimum & maximum number of items
            // allowed
            //=====================================================================
            minimumNumberOfItems = 1;
            maximumNumberOfItems = 9;
            //=====================================================================
            if (maximumNumberOfItems < minimumNumberOfItems) {
                console.log("Product Form Cards: configured maximum number of items (" + maximumNumberOfItems +
                            ") is less than the minimum (" + minimumNumberOfItems + ").");
            }
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
                    j,
                    $addButtons = $multifield.find('button[coral-multifield-add]'),
                    $removeButtons = $multifield.find('.coral3-Multifield-remove'),
                    $addButton,
                    $removeButton,
                    $multifieldItems;

                $countValidatorField.checkValidity();
                $countValidatorField.updateErrorUI();

                for (i = 0; i < $addButtons.length; i++) {
                    $addButton = $addButtons[i];
                    $multifieldItems = $addButton.parentNode.getElementsByTagName("coral-multifield-item")
                    //---------------------------------------------------------
                    // If there are not the minimum number of multifields now,
                    // keep adding multifields until we have the minimum.
                    //---------------------------------------------------------
                    for (j = $multifieldItems.length; j < minimumNumberOfItems; j++) {
                        $addButton.click();
                    }
                    //---------------------------------------------------------
                    // If there are only minimumNumberOfItems of delete buttons,
                    // disable them. If there are more than that number of
                    // buttons enabled all buttons so the user can delete any
                    // multifield item they may want to.
                    //---------------------------------------------------------
                    for (j = 0; j < $removeButtons.length; j++) {
                        $removeButton = $removeButtons[j];
                        $removeButton.disabled = ($removeButtons.length == minimumNumberOfItems);
                    }
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
    });
})($, $(document), $(window));