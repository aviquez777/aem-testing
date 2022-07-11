/**
 * Extension to the standard checkbox component. It enables showing/hiding of
 * other components based on the selection made in the checkbox.
 * 
 * How to use: - add the class cq-dialog-checkbox-showhide to the checkbox
 * element - add the data attribute cq-dialog-checkbox-showhide-target to the
 * checkbox element, value should be the selector, usually a specific class
 * name, to find all possible target elements that can be shown/hidden. - add
 * the target class to each target component that can be shown/hidden - add the
 * class hidden to each target component to make them initially hidden - add the
 * attribute showhidetargetvalue to each target component, the value should
 * equal the value of the select option that will unhide this element. Leave
 * this value as an empty string to toggle the target component on when the
 * checkbox is unchecked.
 * based on script by rjspiker https://gist.github.com/rjspiker/003cf9eac1e853bb109a.js
 */
(function(document, $) {
    "use strict";

    // when dialog gets injected
    $(document).on("foundation-contentloaded", function(e) {
        // if there is already an inital value make sure the according target
        // element becomes visible
        $(".cq-dialog-checkbox-showhide").each(function() {
            showHide($(this));
        });
    });

    $(document).on("change", ".cq-dialog-checkbox-showhide", function(e) {
        showHide($(this));
    });

    function showHide(el) {

        // get the selector to find the target elements. its stored as data-..
        // attribute
        var target = el.data("cqDialogCheckboxShowhideTarget");
        // get the load more msg
        var textfield = $(target).find("input[name='./tier2loadmoremsg']");
        // is checkbox checked?
        var checked = el.prop('checked');
        // get the selected value
        // if checkbox is not checked, we set the value to empty string
        var value = checked ? el.val() : '';

        if (value) {
            $(textfield).attr("aria-required", false);
            $(textfield).val("");
            $(".coral-TabPanel-tab.is-invalid").removeClass("is-invalid");
        } else {
            $(textfield).attr("aria-required", true);
        }

        // make sure all unselected target elements are hidden.
        $(target).not(".hidden").addClass("hidden");

        // unhide the target element that contains the selected value as
        // data-showhidetargetvalue attribute
        $(target).filter("[data-showhidetargetvalue='" + value + "']")
                .removeClass("hidden");

    }

})(document, Granite.$);
