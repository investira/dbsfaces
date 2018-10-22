dbs_combobox = function(pId) {
	$(pId).focusin(function(){
		$(pId + " > .-container > .-th_input-data").addClass("-th_input-data-FOCUS");
	});	

	$(pId).focusout(function(){
		$(pId + " > .-container > .-th_input-data").removeClass("-th_input-data-FOCUS");
	});
	
	$(pId + " > .-container > .-th_input-data > select").keydown(function(e){
		dbsfaces.combobox.copyText(pId, this);
	});
	
	$(pId + " > .-container > .-th_input-data > select").keyup(function(e){
		dbsfaces.combobox.copyText(pId, this);
	});	
	
	$(pId + " > .-container > .-th_input-data > select").change(function(e){
		dbsfaces.combobox.copyText(pId, this);
	});	
}

dbsfaces.combobox = {
	copyText: function(pId, e){
		var xText = e.options[e.selectedIndex].text;
		$(pId + " > .-container > .-th_input-data > .-data").get(0).firstChild.textContent = xText;
		return;
	},
	
	triggerChange: function(pId){
		$(pId + "-data").trigger("change");
	}		

}


