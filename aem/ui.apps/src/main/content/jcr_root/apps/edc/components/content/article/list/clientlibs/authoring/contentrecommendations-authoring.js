(function ($, $document) {
    "use strict";
    $document.on("dialog-ready", function () {
        var currentTimeMillis = new Date().getTime();
        var countValidatorId = "multifield-validator-" + parseInt(currentTimeMillis);
        var $countValidatorField = $("#" + countValidatorId),
            $multifield = $("coral-multifield"),
            $premiumornot = $('.coral-Form-field.premiumornot-tab-showhide'),
        //=====================================================================
        // Change this value to change maximum number of bullet items allowed
        //=====================================================================
            fieldLimit = 5,
        //=====================================================================
        // Get dialog tab
        //=====================================================================
            coralPanel,
            tabId,
            coralTab,
            isTab;
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
        $multifield.on("click", "[coral-multifield-add]", function (e) {
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
            if($("coral-dialog[open]").find("coral-dialog-header")[0].innerText.trim() == "Content Recommendations") {
                var maxItemsReached = "Maximum items reached [tags selected below will not be used]",
                $addButton = $multifield.find("[coral-multifield-add]"),
                items = $multifield.find("coral-multifield-item").length;

                $countValidatorField.checkValidity();
                $countValidatorField.updateErrorUI();

                for(var i = 0; i < items; i++) {
                    if (items >= fieldLimit) {
                        $addButton.prop("disabled", true )
                        $addButton.text(maxItemsReached);
                        enableDisableTagsDropDown(true);
                    } else {
                        $addButton.removeProp("disabled");
                        enableDisableTagsDropDown(false);
                        if ($addButton.text() == maxItemsReached) {
                            $addButton.text("Add");
                        }
                    }
                }
            }
        }

        function enableDisableTagsDropDown(enabled) {
            var dropDown = $("select[name='./tags']");
            if(dropDown.length == 1)
            {
                dropDown[0].disabled = enabled;
            }
        }

        function show($el, message) {
            this.clear($countValidatorField);
        }

        function clear() {
        }

        //=====================================================================
        // Show/Hide Premium Tab
        //=====================================================================
        function showHidePremiunTab() {
            var value = $premiumornot.find('input[name="./premiumornot"]:checked').val(),
                exportJourney = $('.export-journey-settings'),
                psContainer = $('.premium-settings'),
                ctaTextField = $('.coral-Form-field[name="./ctatext"], .coral-Form-field[name="./ctaurl"]'),
                reqFields = $('.coral-Form-field[name="./ctatext"], .coral-Form-field[name="./ctaurl"], .coral-Form-field[name="./externalImageAlt"], .coral-Form-field[name="./externalTitle"], .coral-Form-field[name="./externalSynopsis"], .coral-Form-field[name="./externalUrl"]');

            switch (value) {
                case 'premium' :
                    psContainer.removeClass('hide');
                    reqFields.removeAttr('required aria-required');
                    ctaTextField.attr({required:true,"aria-required":true});
                    coralPanel.not('.hide').addClass('hide');
                    coralTab.not('.hide').addClass('hide');
                    exportJourney.not('.hide').addClass('hide');
                    break;
                case 'premium-external':
                    coralPanel.removeClass('hide');
                    coralTab.removeClass('hide');
                    psContainer.removeClass('hide');
                    exportJourney.not('.hide').addClass('hide');
                    reqFields.attr({required:true,"aria-required":true});
                    ctaTextField.attr({required:true,"aria-required":true});
                    break;
                default:
                    coralPanel.not('.hide').addClass('hide');
                    coralTab.not('.hide').addClass('hide');
                    psContainer.not('.hide').addClass('hide');
                    exportJourney.removeClass('hide');
                    reqFields.removeAttr('required aria-required');
                    break;
            }
        }

        //=====================================================================
        // Get dialog tab
        //=====================================================================
        function getTab() {
            coralPanel = $('.premiumornot-tab-showhide-target').closest('coral-panel');
            tabId = coralPanel.prop('id');
            coralTab = $('coral-tab[aria-controls="' + tabId + '"]');
        }

        //=====================================================================
        // Verify Premium Tab when radio is clicked...
        //=====================================================================
        $premiumornot.on('click', 'input[name="./premiumornot"]', function (e) {
            showHidePremiunTab();
        });
        
        //=====================================================================
        // Initialize Premium Tab
        //=====================================================================
        isTab = setInterval( function () {
            getTab();
            if (coralTab.length) {
                showHidePremiunTab();
                clearInterval(isTab);
            }
        }, 100);
    });
})($, $(document));
