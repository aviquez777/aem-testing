(function ($, $document, $window, Granite) {
    "use strict";

    $document.on("dialog-ready", function () {
        var $multifield = $("coral-multifield"),
        //=====================================================================
        // Change this value to change minimum of multifields
        // allowed
        //=====================================================================
            minimumNumberOfTabs = 4;

        //=====================================================================
        adjustMultifieldUI(); // Verify minimum of multifields items
        //=====================================================================

        //=====================================================================
        // When the "Delete" button is clicked...
        //=====================================================================
        $document.on("click", ".coral3-Multifield-remove", function (e) {
            adjustMultifieldUI();
        });

        //=====================================================================
        // Iterate the "Remove" buttons, finding the number of items under its
        // parent (this is the number of bullet items). If this number is >=
        // the current fieldLimit, disable the "Remove" button
        //=====================================================================
        function adjustMultifieldUI() {
            if ($("coral-dialog[open]").find("coral-dialog-header")[0].innerText.trim() == "Feature Grid") {
                var j,
                    i,
                    k,
                    $items,
                    $addButton;

                for (i = 0; i < $multifield.find("> button[coral-multifield-add]").length; i++) {
                    $addButton = $multifield.find("> button[coral-multifield-add]")[i];
                    $items = $addButton.parentNode.getElementsByTagName("coral-multifield-item");

                    //---------------------------------------------------------
                    // If there are not the minimum number of multifields now,
                    // keep adding multifields until we have the minimum.
                    //---------------------------------------------------------
                    for (j = $items.length; j < minimumNumberOfTabs; j++) {
                        $addButton.click();
                    }

                    //---------------------------------------------------------
                    // If there are only minimumNumberOfTabs of delete buttons,
                    // disable them. If there are more than that number of
                    // buttons enabled all buttons so the user can delete any
                    // multifield item they may want to.
                    //---------------------------------------------------------
                    for (k = 0; k < $(".coral3-Multifield-remove").length; k++) {
                        $(".coral3-Multifield-remove")[k].disabled = ($(".coral3-Multifield-remove").length == minimumNumberOfTabs);
                    }
                }
            }
        }
    });
})($, $(document), $(window), Granite);
