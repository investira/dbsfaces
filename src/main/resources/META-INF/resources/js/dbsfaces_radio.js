
dbsfaces.radio = {
	setValue: function(pRadioItem, pValue){
		if (pRadioItem == null || (typeof(pRadioItem) == "undefined")){return;}
		var xE = pElement;
		if (!(xE instanceof jQuery)){
			xE = $(pRadio);
		}
		var xItem = "[id='" + dbsfaces.util.js(pRadioItem[0].id) + "'][value='" + pValue + "']";
		if (pValue){
			xItem.attr("checked", "checked");
			xItem.val("checked", true);
			xItem[0].checked = true;
		}else{
			xItem.attr("checked", "");
			xItem.val("checked", false);
			xItem[0].checked = false;
		}
	}
}
