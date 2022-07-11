(function ($window, $document, Granite, $) {
    "use strict";
    $document.on("dialog-ready", function () {
        // if there is already an inital value make sure the according target element becomes visible
        setTimeout(function(){
            $(".cq-dialog-dropdown-showhide-single").each( function() {
                showHide($(this));
            });
        }, 500)
    });

    $document.on("change", ".cq-dialog-dropdown-showhide-single", function(e) {
        showHide($(this));
    });

    function showHide(el){
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = el.data("cqDialogDropdownShowhideSingleTarget");
        var elementIndex = el.closest('coral-multifield-item').index();

        // get the selected value
        var value = el.val();
        $(target).each(function(index){
            var tarIndex = $(this).closest('coral-multifield-item').index();
            if (elementIndex == tarIndex) {
                $(this).not(".hide").addClass("hide");
                $(this).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
            }
        })
   }

})($(window), $(document), Granite, $);