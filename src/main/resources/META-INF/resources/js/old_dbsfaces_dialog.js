
dbsfaces.dialog = {
	/*Retorna ClientId + subfixo dbsoft utilizado nos componentes*/
	getClientIdFaces : function(pDialogId){
		return pDialogId + "_" + dbsfaces.ID.DIALOG;
	},

	//Exibe Janela
	//O dialog deverá estar em um arquivo xhtml com o nome do exatamente igual ao seu id.
	show : function(pDialogId, pDelay){
		var xFoo = dbsfaces.dialog.getClientIdFaces(pDialogId) + "_foo";
		//Cria elemento na pagina, que será utilizado para injetar o dialogo
		$("body").append("<span id='" + xFoo + "' />");
		//Injeta o dialogo no elemento criado acima
		$("#" + xFoo).load(pDialogId + '.xhtml',{}, function() {	

	        //Exibe m�scara
			$('#' + pDialogId + '_mask').show();

			if (dbsfaces.util.isiOS()) {
				$('#' + pDialogId).show();
			}else{
				$('#' + pDialogId).fadeIn(pDelay);
			}
		});
	},

	//Fecha a janela
	close : function(pDialogId){
    	//Dispara evento antes de fechar
		console.log("BEFORE CLOSE");
        if (dbsfaces.util.trigger(dbsfaces.EVENT.BEFORE_CLOSE, '#' + pDialogId)){
			//Fecha ambas as telas (m�scara e diálogo)
	        $('#' + pDialogId + '_mask, #' + pDialogId).hide();
    	}	        
       dbsfaces.util.trigger(dbsfaces.EVENT.AFTER_CLOSE, '#' + pDialogId);
		console.log("AFTER CLOSE");
	}
}


//'Classe' dbsDialog
function dbsDialog(pDialogId) {
	//Atributo Local
	var wsDialogId = pDialogId;
	var wsDialogIdFaces =  dbsfaces.dialog.getClientIdFaces(pDialogId);
	var wsMousePositionX = 0;
	var wsMousePositionY = 0;
	var wsMouseEvent = 0;
	//Atributo P�blico
	//this.getDialogIdFaces = wsDialogIdFaces; 
	//this.getMouseEvent = function(e){return mouseEvent;} 

	/* METODOS PRIVADOS */
	//Inicia o redimensionamento
	function pvResizeStart(e){
		$('#' + wsDialogId).css('cursor','se-resize');
		wsMouseEvent = 2;
		pvSavePosition(e);
	};

	//Inicia o move da janela
	function pvMoveStart(e){
		$('#' + wsDialogId).css('cursor','move');
		wsMouseEvent = 1;
		pvSavePosition(e);
	};

	//Finaliza o move e resize da janela
	function pvResizeStop(){
		$('#' + wsDialogId).css('cursor','default');
		wsMouseEvent = 0;
	};
	
	//Finaliza o move e resize da janela
	function pvMoveStop(){
		pvResizeStop();
	};
	
	//Ajusta tamanho da m�scara traseira para bloquear acesso aos componentes na retarguarda
	function pvAjustMaskSize(){
        //Seta altura e largura da mascara para ocupar toda a tela interna do browser
		$('#' + dbsfaces.dialog.getClientIdFaces(pDialogId) + '_mask').css({'width':$(window).width(),'height':$(window).height()});
	};
	
	//Reposiciona da janela
	function pvMove(e){
		var xEle = $('#' + wsDialogId);
		var xX = xEle.offset().left - wsMousePositionX + e.pageX;
		var xY = xEle.offset().top - wsMousePositionY + e.pageY;
		//Limita posição minima
		if (xX < 0){
			xX = 0;
			pvMoveStop();
		}
		if (xY < 0){
			xY = 0;
			pvMoveStop();
		}
		//Limita posição m�xima
		if (xEle.width() + xX > $(document).width()){
			xX = xEle.offset().left;
			pvMoveStop();
		}
		if (xEle.height() + xY > $(document).height()){
			xY = xEle.offset().top;
			pvMoveStop();
		}
		//console.log(xEle.offset().top + ":" + wsMousePositionY  + ":" + e.pageY  + ":" + $(document).width());
		//Reposiciona
		xEle.offset({left: xX, top:xY});
		pvSavePosition(e);
	};
	
	//Redimensiona janela
	function pvResize(e){
		var xEle = $('#' + wsDialogId);
		var xX = xEle.width() - wsMousePositionX + e.pageX;
		var xY = xEle.height() - wsMousePositionY + e.pageY;
		//console.log(xEle.css("min-height") + "," + xY);
		//Interrompe o resize caso tenha chegado no limite mínimo
		if (parseInt(xEle.css('min-height')) > xY || 
			parseInt(xEle.css('min-width')) > xX){
			pvMoveStop();
		}else{
			xEle.css('height',xY);
			xEle.css('width', xX);
			pvSavePosition(e);
		}
	};
	
	function pvSavePosition(e){
		wsMousePositionX = e.pageX;
		wsMousePositionY = e.pageY;
	};

	/* EVENTOS */
	//CLOSE
	$('#' + wsDialogId + ' .dbs_dialog_close').mousedown(function (e){
		dbsfaces.dialog.close(wsDialogId);		
	});

	//RESIZE
	$('#' + wsDialogId + ' .dbs_dialog_resize_image').mousedown(function (e){
		if (wsMouseEvent==0){
			//Se foi botão esquerdo...
			if (e.which === 1) {
				pvResizeStart(e);
				return false; //Inibe a propagação do evento
			}
		}
	});

	//MOVE
	$('#' + wsDialogId + ' .dbs_dialog_header').mousedown(function (e){
		if (wsMouseEvent==0){
			//e.preventDefault();
			//Se foi botão esquerdo...
			if (e.which === 1) {
				pvMoveStart(e);
				return false; //Inibe a propagação do evento
			}
		}
	});

	//Evita a propagação do tab teclado para elementos filhos 
	$('#' + wsDialogId).keydown(function(e){
		//if (e.keyCode === 9) {
		//}
		e.stopPropagation();
	});

	$(window).resize(function() {
		pvAjustMaskSize();
	});

	$(window).mousemove(function(e){
		//MOVE
		if (wsMouseEvent == 1){
			pvMove(e);
		//RESIZE
		}else if(wsMouseEvent == 2){
			pvResize(e);
		}
	});
	
	//Cancela eventos mouve ou resize ap�s saltar o botão do mouse
	$(window).mouseup(function(e){
		if (wsMouseEvent != 0){
			pvMoveStop();
		}
	});
	
	//Cancela eventos move ou resize caso o mouse tenha deixado a �rea do documento
	$(document).mouseleave(function(e){
		if (wsMouseEvent != 0){
			pvMoveStop();
		}
	});

	$(document).ready(function() {
		pvAjustMaskSize();
	});
	
	//Posiciona no primeiro campo de input dentro do content
	$(document).keydown(function(e){
		//Se modal estiver visível
		console.log("document.keydown");
		if ($('#' + wsDialogId).is(":visible")){
			$('#' + wsDialogId + ' .dbs_dialog_content').find('input:first').focus();	
            return false;
		}
	});
}

	