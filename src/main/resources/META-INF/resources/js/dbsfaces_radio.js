dbs_radio = function(pId) {
	$(pId + "> .-th_input").on("mouseup touchend", function(e){
		var xInput = $(this).find("> .-input > input");
		dbsfaces.radio.setValue(xInput);
	});	
}



dbsfaces.radio = {
	setValue: function(pRadioItem){
		if (pRadioItem == null || (typeof(pRadioItem) == "undefined")){return;}
		var xE = pRadioItem;
		if (!(xE instanceof jQuery)){
			xE = $(pRadio);
		}

		var xPrevious = xE.closest(".dbs_radio").find(".-th_input-data[checked]");
		if (xPrevious.length > 0){
			xPrevious[0].checked = false;
			xPrevious.attr("checked", false);
			xPrevious.closest(".-th_input").attr("checked", false);
		}
		xE[0].checked = true;
		xE.attr("checked", true);
		xE.closest(".-th_input").attr("checked", true);
		xE.trigger("change");
	}
}
