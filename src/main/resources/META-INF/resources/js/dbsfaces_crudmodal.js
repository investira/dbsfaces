dbs_crudModal = function(pId) {
	var xH = 0;
    xH = $(pId + "> .-caption").outerHeight();
    xH = xH + $(pId + "> .-toolbar").outerHeight();
    
    $(pId).css("padding-bottom", xH + "px");

}

dbsfaces.crudModal = {
	setBackgroundColor: function(pCrudId, pRadioId){
		dbsfaces.crudModal.setBackgroundColorStyleClass(xCrudId, xRadioId);
		
		$(dbsfaces.util.jsid(xCrudId + ":modal:" + xRadioId) + " input:radio").off("change.cor");
		$(dbsfaces.util.jsid(xCrudId + ":modal:" + xRadioId) + " input:radio").on("change.cor", function(){
			dbsfaces.crudModal.setBackgroundColorStyleClass(xCrudId, xRadioId);
		});
	},
	
	setBackgroundColorStyleClass: function(pCrudId, pRadioId){
		//VENDA OU DÉBITO
		if($(dbsfaces.util.jsid(pCrudId + ":modal:" + pRadioId + "1")).is(":checked")){
			dbsfaces.crudModal.getBackgroundComponent(pCrudId).addClass("dbs_back_gradient_orange")
														 	   .removeClass("dbs_back_gradient_blue")
		            						  		   	 	   .removeClass("dbs_back_gradient_white");
  		//COMPRA OU CRÉDITO   	 
		}else if($(dbsfaces.util.jsid(pCrudId + ":modal:" + pRadioId + "0")).is(":checked")){
			dbsfaces.crudModal.getBackgroundComponent(pCrudId).addClass("dbs_back_gradient_blue")
												  		   	   .removeClass("dbs_back_gradient_orange")
															   .removeClass("dbs_back_gradient_white");
		}
	},
		
	getBackgroundComponent: function(pId){
		return dbsfaces.modal.getBackgroundComponent(pId + ":modal");
	},

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
			var xCancel = $(".dbs_crudModal [id$='modal:btCancel']").last();
			if (xCancel.length==0){
				xCancel = $(".dbs_crudModal [id$='modal:btClose']").last();
			}
			if (xCancel.length > 0){
				xCancel.click();
			}
			//Recupera a lista de elementos fechar
//			var xLista = document.getElementsByClassName("fechar");
//		  	xLista[xLista.length -1].click(); //Clica no último botão da lista, o mais interno.
		}
	}
};