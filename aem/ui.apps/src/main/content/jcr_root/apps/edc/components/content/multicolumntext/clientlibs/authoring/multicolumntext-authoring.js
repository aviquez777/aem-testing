(function ($, $document, $window, Granite) {
    "use strict";

    $document.on("dialog-ready", function () {
        var dialogMultiColumnText = $('coral-dialog[open]').find('.cq-dialog-content[data-dialog-id="dialog-multicolumntext"]'),
            currentTimeMillis,
            countValidatorId,
            $countValidatorField,
            $multifield,
            minimumNumberOfItems,
            maximumNumberOfItems,
            hasLink = false,
            oneEmpty = false;

        if (dialogMultiColumnText.length) {
            currentTimeMillis = new Date().getTime();
            countValidatorId = "multifield-validator-" + parseInt(currentTimeMillis);
            $countValidatorField = $("#" + countValidatorId);
            $multifield = $("coral-multifield");
            //=====================================================================
            // Change this value to change minimum & maximum number of multifields
            // allowed
            //=====================================================================
            minimumNumberOfItems = 2;
            maximumNumberOfItems = 4;
            //=====================================================================
            if(maximumNumberOfItems < minimumNumberOfItems) {
                console.log("Multi-Column Text component: configured maximum number of tabs (" + maximumNumberOfItems +
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
                    j,
                    $addButtons = $multifield.find("button[coral-multifield-add]"),
                    $removeButtons = $multifield.find(".coral3-Multifield-remove"),
                    $addButton,
                    $multifieldItems;

                $countValidatorField.checkValidity();
                $countValidatorField.updateErrorUI();

                for (i = 0; i < $addButtons.length; i++) {
                    $addButton = $addButtons[i];
                    $multifieldItems = $addButton.parentNode.getElementsByTagName("coral-multifield-item");
                    //---------------------------------------------------------
                    // If there are not the minimum number of multifields now,
                    // keep adding multifields until we have the minimum.
                    //---------------------------------------------------------
                    for(j = $multifieldItems.length; j < minimumNumberOfItems; j++) {
                        $addButton.click();
                    }
                    //---------------------------------------------------------
                    // If there are only minimumNumberOfItems of delete buttons,
                    // disable them. If there are more than that number of
                    // buttons enabled all buttons so the user can delete any
                    // multifield item they may want to.
                    //---------------------------------------------------------
                    for(j = 0; j < $removeButtons.length; j++) {
                        $removeButtons[j].disabled = ($removeButtons.length == minimumNumberOfItems);
                    }
                    //---------------------------------------------------------
                    // If we've hit the maximum, disable the "Add" button
                    //---------------------------------------------------------
                    if ($multifieldItems.length >= maximumNumberOfItems) {
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


            //=====================================================================
            // Validator for link fields
            //=====================================================================
            $window.adaptTo("foundation-registry").register("foundation.validation.validator", {
                selector: '[data-validation="field.link"],[data-foundation-validation="field.link"]',
                validate: function(el) {

                    if (!hasLink && $(el).val() !== '') {
                        hasLink = true;
                    }

                    if (hasLink && $(el).val() === '') {
                        oneEmpty = true;
                    }

                    if (hasLink && oneEmpty) {
                        //reset the variables for next 
                        hasLink = false;
                        oneEmpty = false;

                        return Granite.I18n.get('Links are optional. <br />However if any Link url is included, all of Links must have an url.');
                    }
                },
                show: function (el, message) {
                    var linkField = $(el),
                        fieldErrorEl = $('<span class="coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS" data-init="quicktip" data-quicktip-type="error" />'),
                        arrow = linkField.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

                    this.clear(el);

                    linkField.addClass('is-invalid');

                    fieldErrorEl.clone()
                        .attr('data-quicktip-arrow', arrow)
                        .attr('data-quicktip-content', message)
                        .insertAfter(linkField);
                },
                clear: function (el) {
                    if ($(el).val() !== '' && $(el).hasClass('is-invalid')) {
                        $(el).prev('.coral-Form-fielderror').tooltip('hide').remove();
                        $(el).nextAll('.coral-Form-fielderror').tooltip('hide').remove();
                        $(el).removeClass('is-invalid');
                    }
                }
            });
        }
    });
})($, $(document), $(window), Granite);