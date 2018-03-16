dbs_checkbox = function(pId) {
	$(pId + "> .-th_input").on("mouseup touchend", function(e){
		var xInput = $(this).find("> .-input > input");
		dbsfaces.checkbox.setValue(xInput);
	});	
}

dbsfaces.checkbox = {
	setValue: function(pCheckbox){
		if (pCheckbox == null || (typeof(pCheckbox) == "undefined")){return;}
		var xE = pCheckbox;
		if (!(xE instanceof jQuery)){
			xE = $(pCheckbox);
		}
		var xValue = !xE[0].hasAttribute("checked");
		xE[0].checked = xValue;
		xE.attr("checked", xValue);
		xE.closest(".-th_input").attr("checked", xValue);
		xE.trigger("change");
	}
}



