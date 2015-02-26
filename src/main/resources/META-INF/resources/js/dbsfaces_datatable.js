dbs_dataTable = function(pId) {
	var xH = 0;
	var xW = 15;
	var xTable = pId + " > .-container > .-content > table ";
    var xRow = $(xTable + " > tbody > tr:first");
    var xSelectedRow;

	xW = xW + $(pId + " > .-container > .-header > .-filter > .-input").outerWidth();
	xW = xW + $(pId + " > .-container > .-header > .-filter > .-button").outerWidth();

	$(pId).css("min-width", xW + "px");

	xW = xRow.outerWidth() + 2;
	xW = xW - xRow.children(".-CX").outerWidth();
//	$(xTable + " > tbody").css("min-width", xW);
//	$(xTable + " > thead").css("min-width", xW);
//	$(xTable + " > thead > tr").css("min-width", xW);

	xH = $(pId + " > .-container > .-header").outerHeight();
	$(pId + " > .-container > .-content").css("margin-top", "-" + xH + "px")
	                                     .css("padding-top", xH + "px");

	dbsfaces.dataTable.showOverflowShadow($(xTable));
	dbsfaces.dataTable.positionOnSelectRow(pId);
	
	$(xTable).scroll(function(){
		dbsfaces.dataTable.showOverflowShadow($(this));
	});
	
	var xMouseover = function(e) {
		dbsfaces.dataTable.rowFocusAdd(pId, this);
	}

	var xMousemove = function(e){
		var xTable = pId + " > .-container > .-content > table ";
		$(this).off("mousemove");
		$(xTable + " > tbody > tr").off('mouseover.datatable') 
								   .on('mouseover.datatable', xMouseover); 
	}

	/*hover*/
	$(xTable + " > tbody > tr").off('mouseover.datatable') 
							   .on('mouseover.datatable', xMouseover);

	$(pId).mouseout(function(e){
		dbsfaces.dataTable.rowFocusRemove(pId);
	})

	$(xTable + " > tbody > tr").focusin(function(e){
		dbsfaces.dataTable.rowSelect(pId, this);
	});
	
	/*Força o foco no click*/
	$(xTable + " > tbody > tr").click(function(e){
		//Se click foi em campo de input, não precisa forçar o foco no input-foo.
		if ($(e.target).hasClass("dbs_input-data")
		 || $(e.target).is("input")){
			dbsfaces.dataTable.headFocusAdd(pId);
		}else{
			//Somente seleciona se for diferente do selecionado anteriormente
			if (this != xSelectedRow){
				xSelectedRow = this;
				dbsfaces.dataTable.rowSelect(pId, this);
			}
		}
//		e.preventDefault();
	});
	
	/*Executa o click na linha */
	$(xTable + " > tbody > tr").dblclick(function (e){
		//ignora execução padrão do dblclick e marca a linha
		if (!$(pId).hasClass(".noDialogEdit")){
			var xB = $(this).find("td > .-selectOne");
	    	if (xB.length > 0){
	    		e.stopImmediatePropagation();
				e.preventDefault();
	    		xB.click();
	    		dbsfaces.dataTable.headFocusRemove(pId);
	       		dbsfaces.dataTable.rowFocusRemove(pId);
	       		dbsfaces.dataTable.rowDeselect(pId);
	    	}
	    	return false;
		}
    });

	/*Evitar a propagação do evento click do campo e dispara o click para o datagrid*/
	$(pId + " > .-container > input.-foo").click(function(e){
		e.stopImmediatePropagation();
		$(pId).trigger('click');
		return false;
	});
	
	$(pId + " > .-container > input.-foo").on("select", function(e){
		return false;
	});
	
	$(pId + " > .-container > input.-foo").keydown(function(e){
		if(e.keyCode==40 || //DOWN
		   e.keyCode==38){  //UP
			var xTable = pId + " > .-container > .-content > table ";
			dbsfaces.dataTable.moveToNextOrPreviousRow(pId, e.keyCode);
			var xRow = $(xTable + " > tbody > tr");
			var xTBody = xRow.parent();
			xRow.off("mouseover.datatable");
			xTBody.off('mousemove.datatable');
			xTBody.on('mousemove.datatable', xMousemove);
			return false;
		}
	});  
	
	$(pId + " > .-container > input.-foo").focusin(function(e){
		var xTable = pId + " > .-container > .-content > table ";
		var xE = $(xTable + " > tbody > tr.-selected");
		//Seleciona linha caso não exista alguma já selecionada
		if (xE.length == 0){
			xE = $(xTable + " > tbody > tr.-focus");
			if (xE.length == 0){
				xE = $(xTable + " > tbody > tr:first");
				dbsfaces.dataTable.rowFocusAdd(pId, xE);
			}
		}
		dbsfaces.dataTable.headFocusAdd(pId);
		e.stopPropagation();
		//Comentado em 28/08/2013 - no IE, retirava o foco do input.-foo, deixando de funcionar a nagevação por seta
		//$(pId).trigger('focus'); 
	}); 
	
	$(pId + " > .-container > input.-foo").focusout(function(e){
		dbsfaces.dataTable.headFocusRemove(pId);
		e.stopPropagation();
		$(pId).trigger('blur');
	}); 
	//Captura evento ajax dbsoft
	dbsfaces.ui.ajaxShowLoading(pId + ".dbs_dataTable");
}


dbsfaces.dataTable = {
	focus: function(e){
		var xTBody = $('#' + dbsfaces.util.jsid(e.source.id) + " > .-container > .-content > table > tbody");
		//Monitora evento ajax recebido e dispara evento dbsoft
		dbsfaces.onajax(e);
		
		if (e.status=='complete'){
			jQuery.data(e.source, 'wScrollTop', xTBody.scrollTop());
		}else if (e.status=='success'){
			var wScrollTopValue = jQuery.data(e.source, 'wScrollTop');
			if (typeof(wScrollTopValue) != "undefined"){
				xTBody.scrollTop(jQuery.data(e.source, 'wScrollTop'));
			}
			$('#' + dbsfaces.util.jsid(e.source.id) + " > .-container > input.-foo").focus();
		}
	},
		
	rowSelect: function(pId, pNew){
		dbsfaces.dataTable.rowFocusRemove(pId);
		$(pId + " > .-container > input.-foo").val($(pNew).attr("index"));
		dbsfaces.dataTable.rowDeselect(pId);
		$(pNew).addClass("-selected");
		$(pId).trigger(dbsfaces.EVENT.ON_ROW_SELECTED, pNew);
		$(pId).trigger("select.datatable");
		$(pId + " > .-container > input.-foo").select();
	},

	rowDeselect: function(pId){
		$(pId + " > .-container > .-content > table > tbody > tr.-selected").removeClass("-selected");
	},
	
	rowFocusAdd: function(pId, pNew){
		dbsfaces.dataTable.rowFocusRemove(pId);
		$(pNew).addClass("-focus");
	},
	
	rowFocusRemove: function(pId){
		$(pId + " > .-container > .-content > table > tbody > tr.-focus").removeClass("-focus");
	},
	
	headFocusAdd: function(pId){
		$(pId + " > .-container > .-content > table > thead > tr").addClass("-focus");
	},
	
	headFocusRemove: function(pId){
		$(pId + " > .-container > .-content > table > thead > tr").removeClass("-focus");
	},

	
	moveToNextOrPreviousRow: function(pId, pKeyCode){
		var xNew;
		var xDirection = 0;
		var xRow = $(pId + " > .-container > .-content > table > tbody > tr");
		var xTBody = xRow.parent();

		if (xTBody.find(".-selected").length == 0){
			xNew = xRow.first();
		}else{
			if (pKeyCode==40){ //DOWN
				xNew = xRow.filter(".-selected").next();
				if ($(xNew).length == 0){
					xNew = xRow.last();
				}else{
					xDirection = 1;
				}
			}else if (pKeyCode==38){ //UP
				xNew = xRow.filter(".-selected").prev();
				if ($(xNew).length == 0){
					xNew = xRow.first();
				}else{
					xDirection = -1;
				}
			}
		}
		if ($(xNew).length > 0){ 
			dbsfaces.dataTable.rowSelect(pId, xNew);
			if (xDirection!=0){
				var xNewPos = xTBody.scrollTop() + xNew.position().top + xNew.outerHeight();
				var xLT = xTBody.scrollTop() + xNew.outerHeight();
				var xLB = xTBody.scrollTop() + xTBody.outerHeight();
				if ((xNewPos) > xLB ||
					(xNewPos) < xLT){
					if (xDirection<0){
						xTBody.scrollTop(xNewPos - xNew.outerHeight());
					}else{
						xTBody.scrollTop(xNewPos - xTBody.outerHeight());
					}
				}
			}
		}
	},
	
	showOverflowShadow: function(pE){
		if (pE.length == 0){return;}
		var xScrollLeftMax = pE.get(0).scrollWidth - pE.get(0).clientWidth;
		if (pE.get(0).scrollLeft == 0){
			pE.removeClass("-scrollLeft");
			pE.removeClass("-scrollLeftRight");
			if (xScrollLeftMax > 0){
				pE.addClass("-scrollRight");
			} 
		}else if (pE.get(0).scrollLeft == xScrollLeftMax){
			pE.removeClass("-scrollRight");
			pE.removeClass("-scrollLeftRight");
			if (pE.get(0).scrollLeft > 0){
				pE.addClass("-scrollLeft");
			} 
		}else if (xScrollLeftMax < pE.get(0).scrollWidth){
			pE.removeClass("-scrollLeft");
			pE.removeClass("-scrollRight");
			pE.addClass("-scrollLeftRight");
		}
	},	
	
	positionOnSelectRow: function(pId){
		var xNew;
		var xRow = $(pId + " > .-container > .-content > table > tbody > tr");
		
		xNew = xRow.filter(".-selected");
		if ($(xNew).length != 0){
			xNew.parent().scrollTop($(xNew).position().top - $(xNew).height() - xNew.parent().offset().top);
		}
	}
}

