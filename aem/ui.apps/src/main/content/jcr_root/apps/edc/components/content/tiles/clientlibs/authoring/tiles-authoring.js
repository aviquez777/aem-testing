(function ($, $document) {
	"use strict";
	$document.on("dialog-ready", function () {       
		var currentTimeMillis = new Date().getTime();
		var countValidatorId = "multifield-validator-" + parseInt(currentTimeMillis);
		var $countValidatorField = $("#" + countValidatorId),
		$multifield = $("coral-multifield"),
		//=====================================================================
		// Change this value to change maximum number of bullet items allowed
		//=====================================================================
		fieldLimit = 5;
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
			if($("coral-dialog[open]").find("coral-dialog-header")[0].innerText.trim() == "Two Column Tile")
			{
				$countValidatorField.checkValidity();
				$countValidatorField.updateErrorUI();
				for(var i = 0; i < $multifield.find("button[coral-multifield-add]").length; i++)
				{
					var $addButton = $multifield.find("button[coral-multifield-add]")[i];
					if ($addButton.parentNode.getElementsByTagName("coral-multifield-item").length >= fieldLimit) {
						$addButton.disabled = true;
						$addButton.innerText = "Maximum items reached";
					} 
					else {
						$addButton.disabled = false;
                        if ($addButton.innerText=="Maximum items reached") {
							$addButton.innerText = "Add";
                        }   
					} 					
				}
			}
		}
		function show($el, message) {
			this.clear($countValidatorField);
		}
		function clear() {
		}
	});
})($, $(document));
