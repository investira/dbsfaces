dbs_checkbox = function(pId) {
	dbsfaces.checkbox.setValue($(pId));
	$(pId).off('click.checkbox')
		  .on('click.checkbox', function(e){
			  dbsfaces.checkbox.setValue($(pId));
			  $(e.target).focus();
	});
}

dbsfaces.checkbox = {

	setValue: function(pCheckbox, pValue){
		var xE = pCheckbox;
		if (!(xE instanceof jQuery)){
			xE = $(pCheckbox);
		}
		var xInputData = $(dbsfaces.util.jsid(xE[0].id + "-data"));
		if (typeof pValue != "undefined"){
			xInputData.val(pValue);
		}
		xE.val(xInputData[0].checked);
	}

}


