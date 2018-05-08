dbs_radio = function(pId) {
	$(pId + "> .-th_input").on("mouseup touchend", function(e){
		var xInput = $(this).find("> .-input > input");
		dbsfaces.radio.setValue(xInput);
	});	
}



dbsfaces.radio = {
	setValue: function(pRadioInput){
		if (pRadioInput == null || (typeof(pRadioInput) == "undefined") || pRadioInput.length == 0){return;}
		var xE = pRadioInput;
		if (!(xE instanceof jQuery)){
			xE = $(pRadioInput);
		}
		//Desmarca todos os itens do do mesmo grupo
		$("[name='" + xE.attr("name") + "']").each(function(){
			var xPrevious = $(this);
			xPrevious[0].checked = false;
			xPrevious.attr("checked", false);
			xPrevious.closest(".-th_input").attr("checked", false);
		});
		xE[0].checked = true;
		xE.attr("checked", true);
		xE.closest(".-th_input").attr("checked", true);
//		xE.closest(".dbs_radio").trigger("change", xE);
		xE.trigger("change");
	}
}


