dbs_crudForm = function(pId) {
	var xH = 0;
    xH = $(pId + "> .-caption").outerHeight();
    xH = xH + $(pId + "> .-toolbar").outerHeight();
    
    $(pId).css("padding-bottom", xH + "px");

}

dbsfaces.crudForm = {
	closeEsc: function(e){
		var tecla_pressionada;
		if( !e ) {
			if( window.event ) {
				//Internet Explorer
				e = window.event;
			} else {
				return;
			}
		}
		if ( typeof( e.keyCode ) == 'number'  ){
			//DOM
		   tecla_pressionada = e.keyCode;
		} else if( typeof( e.which ) == 'number' ){
		  //NS 4 compatible
		   tecla_pressionada = e.which;
		} else if( typeof( e.charCode ) == 'number'  ){
		  //NS 6+, Mozilla 0.9+
		   tecla_pressionada = e.charCode;
		} else {
		  return;
		}
		//Verifica se a tecla ESC foi pressionada
		if(tecla_pressionada==27){
			var xCancel = $(".dbs_crudForm [id$='dialog:cancel']").last();
			if (xCancel.length==0){
				xCancel = $(".dbs_crudForm [id$='dialog:close']").last();
			}
			if (xCancel.length > 0){
				xCancel.click();
			}
			//Recupera a lista de elementos fechar
//			var xLista = document.getElementsByClassName("fechar");
//		  	xLista[xLista.length -1].click(); //Clica no último botão da lista, o mais interno.
		}
	}
}