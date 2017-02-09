/*Constantes*/
//                            stop   |    prevent     | prevent "same element"
//                          bubbling | default action | event handlers
//
//return false                 Yes           Yes             No
//preventDefault()             No            Yes             No
//stopPropagation()            Yes           No              No
//stopImmediatePropagation()   Yes           No              Yes

//SVG dimension (Fire fox temn problemas com o uso dos atributos abaixo)(verificar compatibilidade(29/set/2016)
//.height.baseVal.value;
//.textLength.baseVal.value;
//.clientHeight


//Exemplo de load assyncrono
//  (function(d, s, id) {
//    var js, fjs = d.getElementsByTagName(s)[0];
//    if (d.getElementById(id)) return;
//    js = d.createElement(s); js.id = id;
//    js.src = "//connect.facebook.net/en_US/sdk.js";
//    fjs.parentNode.insertBefore(js, fjs);
//  }(document, 'script', 'facebook-jssdk'));
//  

//DBSFACES===========================================================

dbsfaces = {
	CSS : {
		MODIFIER : {
			CLOSABLE : " -closable ",
			CLOSED : " -closed ",
			SELECTED : " -selected ",
			SELECTABLE : " -selectable ",
			TITLE : " -title ",
			LABEL : " -label ",
			ICON : " -icon ",
			ICONCLOSE : " -iconclose ",
			DISABLED : " -disabled "
		}
	},
	
	ID: {
		DIALOG: "dialog",
		INPUTTEXT: "inputText"
	},
	
	EVENT: {
		ON_AJAX_BEGIN: "dbs_ON_AJAX_BEGIN",
		ON_AJAX_COMPLETE: "dbs_ON_AJAX_COMPLETE",
		ON_AJAX_SUCCESS: "dbs_ON_AJAX_SUCESS",
		ON_AJAX_ERROR: "dbs_ON_AJAX_ERROR",
		ON_ROW_SELECTED: "select.dataTable",
		ON_TRANSITION_START: "webkitTransitionStart otransitionStart oTransitionStart msTransitionStart transitionstart",
		ON_TRANSITION_END: "webkitTransitionEnd otransitionEnd oTransitionEnd msTransitionEnd transitionend",
		ON_ANIMATION_END: "webkitAnimationEnd oanimationEnd msAnimationEnd animationend",
		ON_ANIMATION_INTERATION: "webkitAnimationIteration animationiteration"
	},
	
	JAVAX: {
		VIEWSTATE: "javax.faces.ViewState",
		SOURCE: "javax.faces.source",
		PARTIAL_EXECUTE: "javax.faces.partial.execute",
		PARTIAL_RENDER: "javax.faces.partial.render",
		PARTIAL_AJAX: "javax.faces.partial.ajax"
	}
}

var wAjaxTimeout;
var wsAnimationTime = 200;   

//var evt = (evt) ? evt : ((event) ? event : null); 
//var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 

//Desabilita TECLAS ESPECÍFICAS para INPUTS ESPECÍFICOS
$(document).on("keydown", function(e){
	if ((e.which == 13) && //ENTER
		(e.target.type=="text"))  {
		return false;
	} 
	if ((e.which == 8 //BACKSPACE
	  || e.which == 46) && //DELETE
		(e.target.type=="select-one" ||
		 e.target.type=="radio" ||
		 e.target.type=="submit" ||
		 e.target.type=="checkbox"))  {
			return false;
	} 
});

//Inicializa tamanho do fonte quando for class for responsivo(-th_responsive)
$(document).ready(function() { 
	dbsfaces.ui.initializeTheme();
//	dbsfaces.ui.initializeParallax();
//	dbsfaces.ui.initializeResponsive();
//	$(window).resize(function() {
//		dbsfaces.ui.initializeResponsive();
//	});
});



String.prototype.replaceAll = function(target, replacement) {
  return this.split(target).join(replacement);
};


dbsfaces.sound = {
	beep : function(){
		$("#dbs_beep").remove();
		$("body").append('<audio id="dbs_beep" src="sound/alert.mp3" preload autoplay/>');
//		$("body").append('<embed id="dbs_beep" src="sound/alert.mp3" showcontrols="false" hidden="true" autostart="true" loop="false" />');
	}
}

dbsfaces.svg = {
	gsvg: function(pComponent, pX, pY, pWidth, pHeight, pStyleClass, pStyle, pAttrs){
		var xG = dbsfaces.svg.g(pComponent, pWidth, pHeight, pStyleClass, pStyle, pAttrs);
		return dbsfaces.svg.svg(xG, null, null, null, null, null, null, null);
	},
	 
	g: function(pComponent, pWidth, pHeight, pStyleClass, pStyle, pAttrs){
		var xG = dbsfaces.svg.createElement('g', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xG, pStyleClass, pStyle);

		xG.svgAttr("width", pWidth)
		  .svgAttr("height", pHeight);

		pComponent.append(xG);
		return xG;
	},

	svg: function(pComponent, pX, pY, pWidth, pHeight, pStyleClass, pStyle, pAttrs){
		var xSVG = dbsfaces.svg.createElement('svg', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xSVG, pStyleClass, pStyle);

		xSVG.svgAttr("x", pX)
			.svgAttr("y", pY)
			.svgAttr("width", pWidth)
			.svgAttr("height", pHeight);

		pComponent.append(xSVG);
		return xSVG;
	},
	
	use: function(pComponent, pHRef, pStyleClass, pStyle, pAttrs){
		var xUse = dbsfaces.svg.createElement('use', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xUse, pStyleClass, pStyle);
		dbsfaces.svg.setAttributeHRef(xUse, pHRef);
//		xUse.get(0).setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + pHRef);
//		xUse.svgAttr("xlink:href", "#" + pHRef);
		pComponent.append(xUse);
		return xUse;
	},

	line: function(pComponent, pX1, pY1, pX2, pY2, pStyleClass, pStyle, pAttrs){
		var xLine = dbsfaces.svg.createElement('line', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xLine, pStyleClass, pStyle);
		xLine.svgAttr("x1", pX1)
			 .svgAttr("y1", pY1)
			 .svgAttr("x2", pX2)
			 .svgAttr("y2", pY2);
		pComponent.append(xLine);
		return xLine;
	},
	
	circle: function(pComponent, pCX, pCY, pR, pStyleClass, pStyle, pAttrs){
		var xCircle = dbsfaces.svg.createElement('circle', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xCircle, pStyleClass, pStyle);

		xCircle.svgAttr("cx", pCX)
			   .svgAttr("cy", pCY)
			   .svgAttr("r", pR);

		pComponent.append(xCircle);
		return xCircle;
	},

	rect: function(pComponent, pX, pY, pWidth, pHeight, pRX, pRY, pStyleClass, pStyle, pAttrs){
		var xRect = dbsfaces.svg.createElement('rect', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xRect, pStyleClass, pStyle);

		xRect.svgAttr("x", pX)
			 .svgAttr("y", pY)
			 .svgAttr("rx", pRX)
			 .svgAttr("ry", pRY)
			 .svgAttr("height", pHeight)
			 .svgAttr("width", pWidth);
		
		pComponent.append(xRect);
		return xRect;
	},
	
	ellipse: function(pComponent, pCX, pCY, pRX, pRY, pStyleClass, pStyle, pAttrs){
		var xEllipse = dbsfaces.svg.createElement('ellipse', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xEllipse, pStyleClass, pStyle);

		xEllipse.svgAttr("cx", pCX)
			    .svgAttr("cy", pCY)
			    .svgAttr("rx", pRX)
			    .svgAttr("ry", pRY);

		pComponent.append(xEllipse);
		return xEllipse;
	},
	
	path: function(pComponent, pData, pStyleClass, pStyle, pAttrs){
		var xPath = dbsfaces.svg.createElement('path', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xPath, pStyleClass, pStyle);
		
		xPath.svgAttr("d", pData);
		
		pComponent.append(xPath);
		return xPath;
	},

	text: function(pComponent, pX, pY, pText, pStyleClass, pStyle, pAttrs){
		var xText = dbsfaces.svg.createElement('text', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xText, pStyleClass, pStyle);

		xText.svgAttr("x", pX)
			 .svgAttr("y", pY);

		xText.text(pText);
		pComponent.append(xText);
		return xText;
	},

	textPath: function(pComponent, pHRef, pText, pStyleClass, pStyle, pAttrs){
		var xTextPath = dbsfaces.svg.createElement('textPath', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xTextPath, pStyleClass, pStyle);

		xTextPath.text(pText);
		dbsfaces.svg.setAttributeHRef(xTextPath, pHRef);
		
		pComponent.append(xTextPath);
		
		return xTextPath;
	},
	
	linearGradient: function(pComponent, pId, pAttrs){
		var xElement = dbsfaces.svg.createElement('linearGradient', pAttrs);
		pComponent.append(xElement);

		xElement.svgAttr("id", pId);
		
		return xElement;
	},
	
	marker: function(pComponent, pId, pRefX, pRefY){
		var xElement = dbsfaces.svg.createElement('marker', null);
		pComponent.append(xElement);

		xElement.svgAttr("id", pid)
				.svgAttr("refx", pRefX)
				.svgAttr("refy", pRefY);

		return xElement;
	},

	stop: function(pComponent, pOffset, pStopColor){
		var xElement = dbsfaces.svg.createElement('stop', null);

		xElement.svgAttr("offset", pOffset)
			    .svgAttr("stop-color", pStopColor);

		pComponent.append(xElement);
		return xElement;
	},
	
	createElement: function(pTag, pAttrs){
		var xElement = $(document.createElementNS('http://www.w3.org/2000/svg', pTag));
		//Seta atributos do componente.
		if (pAttrs != null){
			for (var xAttr in pAttrs){
				xElement.svgAttr(xAttr, pAttrs[xAttr]);
			}
		}
		return xElement;
	},

	setDefaultAttrs: function(pComponent, pStyleClass, pStyle){
		pComponent.svgAttr("class", pStyleClass)
				  .svgAttr("style", pStyle);
	},
	
	setAttributeHRef: function(pComponent, pHRef){
		var xNS = "http://www.w3.org/1999/xlink";
		pComponent.get(0).setAttributeNS(xNS, "xlink:href", "#" + pHRef);
	}
	
}

dbsfaces.url = {
	//Retorna os parametros da Url em um objeto
	getParams : function(){
		var xQueryString = {};
		var xQuery = window.location.search.substring(1);
		var xVars = xQuery.split("&");
		for (var i=0;i<xVars.length;i++) {
			var xPair = xVars[i].split("=");
			var xParam = xPair[0].toLowerCase();
		    // If first entry with this name
			if (typeof xQueryString[xParam] === "undefined") {
			  xQueryString[xParam] = decodeURIComponent(xPair[1]);
			    // If second entry with this name
			} else if (typeof xQueryString[xParam] === "string") {
			  var xArr = [ xQueryString[xParam],decodeURIComponent(xPair[1]) ];
			  xQueryString[xParam] = xArr;
			    // If third or later entry with this name
			} else {
			  xQueryString[xParam].push(decodeURIComponent(xPair[1]));
			}
		}
		return xQueryString;
	},
	
	//Copia os valore dos parametros da URL para os campos quando o id for iqual ao nome do parametro.
	setInputsValuesFromParams:function(){
		var wParams = dbsfaces.url.getParams();
		for (var xProperty in wParams) {
		    if (wParams.hasOwnProperty(xProperty)){
		    	var xInput = document.getElementById(xProperty);
		    	if (xInput != null){
		    		xInput.value = wParams[xProperty];
		    	}
		    }
		}
	}
}


dbsfaces.onView = {
	initialize: function(pElement){
		pElement.addClass("-th_onview");
		if ($("body").data("onview") == null){
			$(window).resize(function(e){
				dbsfaces.onView.event();
			});
			$(window).scroll(function(e){
				dbsfaces.onView.event();
			});
			pElement.scroll(function(e){
				dbsfaces.onView.event();
			});
		}
		$("body").data("onview", $(".-th_onview"));
	},
	
	/*Dispara eventos viewEnter, viewExit, viewScroll, viewTopIn, viewTopOut.*/
	event: function(){
		var xWindow = $(window);
		$("body").data("onview").each(function(){
			var xThis = $(this);
			if (xThis.length > 0){
				var xTotalTop = xThis[0].getBoundingClientRect().top + xThis[0].getBoundingClientRect().height;
				if (xTotalTop < 0 
				 || xThis[0].getBoundingClientRect().top >= xWindow.height()){
					if (xThis.data("onview") != null){
//						console.log("viewExit\t" + this.id);
						clearTimeout(xThis.data("onview"));
						xThis.data("onview", null);
						xThis.data("onviewTopIn", null);
						xThis.data("onviewTopOut", null);
						xThis.removeClass("-enter");
						xThis.trigger("viewExit");
					}
				}else{
					if (xThis.data("onview") == null){
//						console.log("viewEnter\t" + this.id);
						xThis.data("onview", true);
						xThis.addClass("-enter");
						xThis.trigger("viewEnter");
					}else{
//						console.log("viewScroll\t" + this.id);
						/*uso do setTimeout para evitar muitas chamadas consecutivas*/
						clearTimeout(xThis.data("onview"));
						xThis.data("onview", 
							setTimeout(function(e){
								if (xThis.data("onviewTopIn") == null 
								 && xThis[0].getBoundingClientRect().top > 0){
									xThis.data("onviewTopIn" , true);
									xThis.trigger("viewTopIn");
								}else if (xThis.data("onviewTopOut") == null 
								       && xThis[0].getBoundingClientRect().top < 0){
									xThis.data("onviewTopOut" , true);
									xThis.trigger("viewTopOut");
								}
									
								var xFactor = 1 - (xTotalTop / (xWindow.height() + xThis[0].getBoundingClientRect().height));
								var xFactorTop = 1 - (xThis[0].getBoundingClientRect().top / xWindow.height());
								var xEvent = $.Event("viewScroll", {dbs: true, source: xThis, factor:xFactor, factorTop:xFactorTop});
								xThis.trigger(xEvent);
							},10)
						);
						
					}
				}
			}
		});
	}
}

dbsfaces.component = {
	//Utilizado pelos componentes DBSUICommand para setar a existencia de mensagem
	setHasMessage: function(pId){
		var xComponent = $(pId);
		xComponent.data("hasmessage" , true);
		xComponent.attr("hasmessage" , true);
	}
}
dbsfaces.ui = {

	initializeTheme: function(){
		dbsfaces.ui.initializeThemeInput();
		dbsfaces.ui.initializeThemeCard();
	},
	
	initializeThemeInput: function(){
		var xColor = tinycolor($("body").css("color"));
		var xStyles = "";
		/*cor do fundo quando selecionado*/
		xColor.setAlpha(.05);
		xStyles += ".-th_input-data:FOCUS,.-th_input-data-FOCUS {background-color: "; 
		xStyles += xColor.toHslString();
		xStyles += ";} ";
		/*cor da borda*/
		xColor.setAlpha(.08);
		xStyles += ".-th_input-data[type=text],";
		xStyles += ".-th_input-data[type=password], ";
		xStyles += ".-th_input-data[type=email], ";
		xStyles += ".-th_input-data[type=tel], ";
		xStyles += ".-th_input-data[type=number], ";
		xStyles += ".-th_input-data[type=date], ";
		xStyles += ".-th_input-data[type=time], ";
		xStyles += ".-th_input-data[type=url], ";
		xStyles += "select.-th_input-data,  ";
		xStyles += "textarea.-th_input-data, ";
		xStyles += "span.-th_input-data, ";
		xStyles += "div.-th_input-data, ";
		xStyles += "label.-th_input-data, ";
		xStyles += ".-th_input-suggestion{ ";
		xStyles += "border: 1px ";
		xStyles += xColor.toHslString();
		xStyles += " solid;";
		xStyles += "}";
		
		var xStyleTag = document.createElement('style');
		 if (xStyleTag.styleSheet){
		     xStyleTag.styleSheet.cssText= Styles;
		 }else{
		     xStyleTag.appendChild(document.createTextNode(xStyles));
		 }
		 document.getElementsByTagName('head')[0].appendChild(xStyleTag);
	},
	
	initializeThemeCard: function(){
	},
	
	moveToFront: function(pElement, pMoveToElement){
		if (pElement == null || (typeof(pElement) == "undefined")){return;}
		var xE = pElement;
		var xToE;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		if (pMoveToElement == null){
			xToE = xE.parentElement; 
		}else{
			if (pMoveToElement instanceof jQuery){
				xToE = pMoveToElement.get(0);
			}else{
				xToE = pMoveToElement;
			}
		}
		if (xToE != null){
			xToE.appendChild(xE);
		}
	},
	moveToBack: function(pElement){
		var xE = pElement;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		xE.parentElement.insertBefore(xE, xE.parentElement.childNodes[0]);
	},
	
	getRectangle : function(obj) {
		var xE = obj;
		if (!(obj instanceof jQuery)){
			xE = $(obj);
		}
	   var off = xE.offset();
	
	   return {
	          top: off.top,
	          left: off.left,
	          height: xE.outerHeight(),
	          width: xE.outerWidth()
	   };
	},

	isInsideRectangle : function(pX, pY, pRect) {
        if ((pX > pRect.left && pX < (pRect.left + pRect.width))
            && (pY > pRect.top && pY < (pRect.top + pRect.height))){
        	return true;
        }else{
	        return false;
        }
	},	

	isOverlappingRectagle : function(pRectA, pRectB) {
		if (dbsfaces.ui.isInsideRectangle(pRectA.left, pRectA.top, pRectB) ||
			dbsfaces.ui.isInsideRectangle(pRectA.left + pRectA.width, pRectA.top, pRectB) ||
			dbsfaces.ui.isInsideRectangle(pRectA.left, pRectA.top + pRectA.height, pRectB) ||
			dbsfaces.ui.isInsideRectangle(pRectA.left + pRectA.width, pRectA.top + pRectA.height, pRectB)){
			return true;
		}else{
			return false;
		}
	},
	
	//Posiciona do próximo campo dentro do container informado
	focusOnFirstInput: function(pContainer){
		//Seta foco no primeiro campo de input
		var xEle = pContainer.find("input:first, select:first, textarea:first");
		if (xEle.length != 0 ){
			xEle.get(0).focus();
		}
	},
	//Seleciona todo o texto
	selectAll: function(pObj){
		var xE = pObj;
		if (pObj instanceof jQuery){
			xE = pObj[0];
		}
		//timeout para evitar que o click desmarque o item selecionado
		setTimeout( function(){
			dbsfaces.ui.selectRange(xE, 0, xE.value.length);
		}, 1 );
	},
	//Retira a seleção de qualquer texto
	selectNone: function(pObj){
		dbsfaces.ui.selectRange(pObj, 0, 0);
	},
	//Seleciona da posição atual até o final do texto
	selectEnd: function(pObj){
		var xE = pObj;
		if (pObj instanceof jQuery){
			xE = pObj[0];
		}
		if (xE.value != null){
			dbsfaces.ui.selectRange(xE, xE.selectionStart, xE.value.length);
		}
	},
	//Seleciona intervalo
	selectRange: function(pObj, pStart, pEnd){
		var xE = pObj;
		if (pObj instanceof jQuery){
			xE = pObj[0];
		}
		if (xE.hasOwnProperty("selectionStart")){
			xE.setSelectionRange(pStart, pEnd);
		}
	},
	
	disableBackgroundInputs: function(pSourceId){
		var xE = pSourceId;
		if (!(pSourceId instanceof jQuery)){
			xE = $(pSourceId);
		}
		var xId = dbsfaces.util.jsid(xE[0].id);
//		xOP.find(".dbs_dialog").not(xId).attr('disabled', true);
	    $(".-th_action").not(xId + " .-th_action").attr('disabled', true);
		$(".dbs_menu").not(xId + " .dbs_menu").attr('disabled', true);
		$("input").not('[type="hidden"]').not(xId + " input").attr('disabled', true);
		$("button").not(xId + " button").attr('disabled', true);
		$("select").not(xId + " select").attr('disabled', true);
		$(".dbs_nav").not(xId + " nav").attr('disabled', true);
		$("a").not(xId + " a").attr('disabled', true);
		$("textarea").not(xId + " textarea").attr('disabled', true);

		dbsfaces.ui.enableForegroundInputs(xE);
	},
	
	enableForegroundInputs: function(pSourceId){
		var xE = pSourceId;
		if (!(pSourceId instanceof jQuery)){
			xE = $(pSourceId);
		}
		xE.find(".-th_action").not(".-disabled").attr('disabled', null);
		xE.find(".dbs_menu").not(".-disabled").attr('disabled', null);
//		xE.find(".dbs_dialog").not(".-disabled").attr('disabled', true);
		xE.find("input").not(".-disabled").attr('disabled', null);
		xE.find("a").not(".-disabled").attr('disabled', null);
		xE.find("button").not(".-disabled").attr('disabled', null);
		xE.find("select").not(".-disabled").attr('disabled', null);
		xE.find(".dbs_nav").not(".-disabled").attr('disabled', null);
		xE.find("textarea").not(".-disabled").attr('disabled', null);
	},

	cssAllBrowser: function(e, pAtribute, pValue){
		var xE = e;
		if (!(xE instanceof jQuery)){
			xE = $(e);
		}
		pAtribute = pAtribute.trim();
		pValue = pValue.trim();
		xE.css("-webkit-" + pAtribute, pValue)
		  .css("-moz-" + pAtribute, pValue)
		  .css("-ms-" + pAtribute, pValue)
		  .css("-o-" + pAtribute, pValue)
		  .css(pAtribute, pValue);
	},

	cssFilterDropShadow: function(e, pValue){
		var xValue = "drop-shadow(" + pValue + ")";
		dbsfaces.ui.cssFilter(e, xValue);
	},
	
	cssFilterBlur: function(e, pValue){
		var xValue = "blur(" + pValue + "px)";
		dbsfaces.ui.cssFilter(e, xValue);
	},

	cssFilterOpacity: function(e, pValue){
		var xValue = "opacity(" + pValue + ")";
		dbsfaces.ui.cssFilter(e, xValue);
	},

	cssFilter: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "filter", pValue);
	},
	
	cssTransform: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "transform", pValue);
	},
	
	cssTransition: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "transition", pValue);
	},

	cssAnimation: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "animation", pValue);
	},

	//Retorna valores do transform
	getTransform:function(e){
		var xE = e;
		if (xE instanceof jQuery){
			xE = $(e).get(0);
		}
		var xSt = window.getComputedStyle(xE, null);
		var xTr = xSt.getPropertyValue("-webkit-transform") ||
		          xSt.getPropertyValue("-moz-transform") ||
		          xSt.getPropertyValue("-ms-transform") ||
		          xSt.getPropertyValue("-o-transform") ||
		          xSt.getPropertyValue("transform") ||
		          "erro";
		return xTr;
	},
	//Retorna valores do Z definido no transform
	getTransformZ: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null
		 || xTr == ""){
			return 0;
		}else{
			xTr = xTr.slice(4,5);
			if (xTr == ""){
				xTr = 0;
			}
		}
		return parseFloat(xTr);
	},
	//Retorna valores do X definido no transform
	getTransformX: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null
		 || xTr == ""){
			return 0;
		}else{
			xTr = xTr.slice(2,3);
			if (xTr == ""){
				xTr = 0;
			}
		}
		return parseFloat(xTr);
	},
	//Retorna valores do Y definido no transform
	getTransformY: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null
		 || xTr == ""){
			return 0;
		}else{
			xTr = xTr.slice(3,4);
			if (xTr == ""){
				xTr = 0;
			}
		}
		return parseFloat(xTr);
	},

	getTransformMatrix3d: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null){
			return "";
		}else{
			return xTr.slice(2,5);
		}
	},
	getTransformMatrix: function(e){
		var xTr = dbsfaces.ui.getTransform(e);
		xTr = xTr.match(/matrix(?:(3d)\(-{0,1}\d+\.?\d*(?:, -{0,1}\d+\.?\d*)*(?:, (-{0,1}\d+\.?\d*))(?:, (-{0,1}\d+\.?\d*))(?:, (-{0,1}\d+\.?\d*)), -{0,1}\d+\.?\d*\)|\(-{0,1}\d+\.?\d*(?:, -{0,1}\d+\.?\d*)*(?:, (-{0,1}\d+\.?\d*))(?:, (-{0,1}\d+\.?\d*))\))/);
		return xTr;
	},
	
	//	Retorna fator mínimo para ajuste de tamanho conforme largura e altura da tela atual em relação a tela desejada
	aspectRatio: function(pWindow, pBaseWidth, pBaseHeight){
		var xBaseRatio = pBaseWidth / pBaseHeight;
		var xWidth = pWindow.width();
		var xHeight = pWindow.height();
		var xUseWidth = ((xWidth / xHeight) < xBaseRatio); 
		var xRatio;
		if (xUseWidth){
			xRatio = xWidth / pBaseWidth;
		}else{
			xRatio = xHeight / pBaseHeight;
		}
//		console.log(xRatio + "\t" + xWidth + "\t" + xHeight + "\t" +  xBaseRatio + "\t" + pBaseWidth + "\t" + pBaseHeight);
		return xRatio;
	},

	getAllEvents: function(e) {
	    var xResult = [];
	    for (var xKey in e) {
	        if (xKey.indexOf('on') === 0) {
	        	xResult.push(xKey.slice(2));
	        }
	    }
	    return xResult.join(' ');
	},
	
	recreateElement: function(e){
		var xE = e;
		if (!(e instanceof jQuery)){
			xE = $(e);
		}
	    var xNew = xE.clone(true);
	    xE.before(xNew);       
	    xE.remove();
	    return xNew;
	},
	
	getTimeFromTextLength: function(pText){
		var xTime = pText.length;
		xTime = (xTime / 2) * 200;
		return xTime;
	},
	
	//	Verificar se elemento esta contido dentro de algum elemento com position:fixed
	pvIsParentFixed: function(pElement){
		var xE = pElement;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		var xParent = xE.parentElement;
		if (xParent == null){
			return false;
		}
		var xStyle = window.getComputedStyle(xParent);
	    var xPosition = xStyle.getPropertyValue("position");
		if (xPosition == "fixed"){
			return true;
		}else{
			return dbsfaces.ui.pvIsParentFixed(xParent);
		}
	},
	
	//Dispara evento click
	ajaxTriggerClick: function(e){
		if ($(e.source).length == 0){
			return;
		}
		if (e.status == "success"){
			$(e.source).trigger("click");
		}
	},
	
	ajaxTriggerLoaded: function(e){
		if ($(e.source).length == 0){
			return;
		}
		if (e.status == "success"){
			$(e.source).trigger("loaded");
		}
	},	

	//Captura evento ajax dbsoft
	ajaxShowLoading : function(pSelector){
//		console.log("ajaxShowLoading:" + pSelector);
		var xEle = pSelector;
		if (!(xEle instanceof jQuery)){
			xEle = $(pSelector);
		}
		xEle.off(dbsfaces.EVENT.ON_AJAX_BEGIN);
		xEle.on(dbsfaces.EVENT.ON_AJAX_BEGIN, function(e){
			dbsfaces.ui.showLoading("main",true);
		});
		xEle.off(dbsfaces.EVENT.ON_AJAX_COMPLETE);
		xEle.on(dbsfaces.EVENT.ON_AJAX_COMPLETE, function(e){
			//Reinicia a contagem do timeout a cada complete, já que existe respostas ajax em andamento
			window.clearTimeout(wAjaxTimeout);
			wAjaxTimeout = window.setTimeout(function(e){
				dbsfaces.ui.showLoadingError("main");
			}, 1000); //Time de delay para efetuar a chamada acima(showLoadingError). A chamada será cancelada em caso de sucesso. 	
		});

		xEle.off(dbsfaces.EVENT.ON_AJAX_SUCCESS);
		xEle.on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
			window.clearTimeout(wAjaxTimeout); //Cancela o timeout definido no evento COMPLETE, cancelando a respectiva chamada ao showLoadingError.
			dbsfaces.ui.showLoading("main",false);
		});
	},
	
	/*Exibe a imagem de que indica que está aguardando o recebimento dos dados*/
	showLoading : function(pId, pShow){
		var xId = dbsfaces.util.jsid(pId + "_loading");
		//Sempre remove loading se já existir 
		
		//Exibe loading
	    if (pShow){
			if ($(xId).length == 0){
				$('body').append("<span id='" + xId.replace("#","") + "' class='-loading -large'/>");
			}
	    }else{
			if ($(xId).length > 0){
		    	$(xId).remove();
			}
	    }
	},
	
	showLoadingError : function(pId){
		var xId = dbsfaces.util.jsid(pId + "_loading");
		$(xId).fadeOut(2000, function(){ 
			$(xId).remove();
			$(pId).removeClass("-ajaxBegin");
//			$("div .-loading").remove(); Comentado em 08/abr/15 - Ricardo: Excluid código até a certeza que código não é mais necessário.
		});
	}

}

//Exibe janela ajustando a localização de form a não ultrapassar os limites da janela principal


dbsfaces.util = {
	/*Verifica se elemento existe*/
	exists: function(pId){
		if(!dbsfaces.util.isEmpty(pId)){
			if ($(pId).length>0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	},

	//Retorna vazio se for nulo
	getNotNull: function(pValue, pDefaultValue){
		if(pValue == null){
			return pDefaultValue;
		}
		return pValue;
	},

	//Retorna vazio se for nulo
	getNotEmpty: function(pValue, pDefaultValue){
		if(dbsfaces.util.isEmpty(pValue)){
			return pDefaultValue;
		}
		return pValue;
	},
	//Retorna vazio se for nulo
	isUndefined: function(pValue){
		if(typeof(pValue) == "undefined"){
			return true;
		}
		return false;
	},

	//Retorna vazio se for nulo
	getNotUndefined: function(pValue, pDefaultValue){
		if(dbsfaces.util.isUndefined(pValue)){
			return pDefaultValue;
		}
		return pValue;
	},
	//Verifica se é nulo ou vazio
	isEmpty: function(pValue){
		if(pValue == null){
			return true;
		}
		if(pValue == ''){
			return true;
		}
		if(dbsfaces.util.isUndefined(pValue)){
			return true;
		}
		return false;
	},
	
	/*dispara evento para o elemento informado*/
	trigger: function (pEventName, pId){
		if (dbsfaces.util.exists(pId) && 
			!dbsfaces.util.isEmpty(pEventName)){
				//Cria evento com parametro para indicar que é dbsoft
				var xEvent = $.Event(pEventName, {dbs: true});
				//Dispara evento depois de fechar 
				$(pId).trigger(xEvent);
				return xEvent;
		}else{
			alert('Parametros inválidos! pId=' + pId + ' pEventName=' + pEventName + ' dbsFaces.trigger');
			return false;
		}
	},
	
	isiOS: function(){
		var xNav = navigator.userAgent.toLowerCase();
		if(xNav.match(/iphone/i) || 
		   xNav.match(/ipod/i) ||
		   xNav.match(/ipad/i)) {
			return true;
		};
	    return false;
	},
	
	isAndroid: function(){
		var xNav = navigator.userAgent.toLowerCase();
		if(xNav.match(/android/i)) {
			return true;
		};
	    return false;
	},

	isBlackBerry: function(){
		var xNav = navigator.userAgent.toLowerCase();
		if(xNav.match(/blackberry/i)) {
			return true;
		};
	    return false;
	},

	isMobile: function(){
	    if (this.isiOS()
	     || this.isBlackBerry()
	     || this.isAndroid()){
	    	return true;
	    }
	    return false;
	},
	
	supports: function(pProp){
	   var div = document.createElement('div'),
	      vendors = 'Khtml Ms O Moz Webkit'.split(' '),
	      len = vendors.length;
	 
      if ( pProp in div.style ) return true;
 
      pProp = pProp.replace(/^[a-z]/, function(val) {
         return val.toUpperCase();
      });
 
      while(len--) {
         if ( vendors[len] + pProp in div.style ) {
            // browser supports box-shadow. Do what you need.
            // Or use a bang (!) to test if the browser doesn't.
            return true;
         } 
      }
      return false;
	}
}


dbsfaces.util.jsid = function(pClientId){
	if (pClientId == null
	 || typeof(pClientId) == "undefined"){
		return "";
	}
	//Retira dois pontos(:) do inicio
	pClientId = pClientId.trim().replace(/^(:)/,"");
	//Retira dois pontos(:) em escaped se houver
	pClientId = pClientId.replace(/\\:/g,":");
	//Convert os outros dois pontos(:)  em escaped caracter
	pClientId = pClientId.replace(/:/g,"\\:");
	//Incluir o "#" se não houver
	if (pClientId.length > 0
	 && pClientId.charAt(0) != "#"){
		pClientId = "#" + pClientId;
	}
	return pClientId;
}

dbsfaces.date = {
	isDate:function(pYear, pMonth, pDay, pHour, pSeconds, pMilliseconds){
		pMonth = pMonth - 1;
		var xD =  new Date(pYear, pMonth, pDay, pHour, pSeconds, pMilliseconds);
		if (xD.getDate() != pDay){
			return "";
		}
		if (xD.getMonth() != pMonth){
			return "";
		}
		if (xD.getFullYear() != pYear){
			return "";
		}
		return xD;
	}
}

dbsfaces.math = {
	round: function(pValue, pDecimals){
		var xP = Math.pow(10, pDecimals);
		var xValue = pValue * xP;
		xValue = Math.round(pValue * xP);
		return dbsfaces.math.trunc(xValue / xP, pDecimals);
	},
	
	trunc: function(pValue, pDecimals){
		var xP = Math.pow(10, pDecimals);
		var xValue = pValue * xP;
		xValue = Math[xValue < 0 ? 'ceil' : 'floor'](xValue);
		return xValue / xP;
	}
};

dbsfaces.number = {
	isNumber: function(pVal){
		return !isNaN(parseFloat(pVal)) && isFinite(pVal);
	},
	
	sizeInBytes: function(pVal){
		if (typeof(pVal) != 'undefined'){
			var xVal = pVal.trim().toUpperCase();
			var xI = xVal.indexOf("KB");
			var xN = 1024;
			if (xI == -1){
				xI = xVal.indexOf("MB");
				if (xI != -1){
					xN *= xN;
				}else{
					xI = xVal.indexOf("GB");
					if (xI != -1){
						xN *= xN;
					}else{
						xI = xVal.indexOf("TB");
						if (xI != -1){
							xN *= xN;
						}else{
							xI = xVal.indexOf("B");
							if (xI == -1){
								xI = xVal.length;
							}
							xN = 1;
						}
					}
				}
			}
			xVal = xVal.substr(0, xI);
			if (dbsfaces.number.isNumber(xVal)){
				return xVal * xN;
			}
		}
		return 0;
	},
	
	getOnlyNumber: function(pValue){
		return pValue.replace(/[^-\d\.]/g, '');
 	}
};

dbsfaces.format = {
	number: function(pValue, pDecimals){
//		var xLanguage = window.navigator.userLanguage || window.navigator.language;
		pValue = dbsfaces.math.round(pValue, pDecimals);
		var xDS = dbsfaces.format.getDecimalSeparator();
	    if (xDS == ".") { //Ingles
	    	return pValue.toString().split("/(?=(?:\d{3})+(?:\.|$))/g").join( "," );
	    }else{ //Portugues
	        return pValue.toString().split("/(?=(?:\d{3})+(?:,|$))/g").join( "." );
	    }
	},
	
	//Retorna o número simplificado com mil, mi, bi, tri, quatri.
	numberSimplify: function(pVal){
		var xVal = Number(pVal);
		var xLength = dbsfaces.math.round(xVal, 0).toString().length;
		if (xLength == 0){return;}
		var xSimple = (xVal / Math.pow(10, ((xLength -1) - ((xLength -1) % 3))));
		var xSuf = "";
		if (xLength > 15){
			xSuf = "quatri";
		}else if (xLength > 12){
			xSuf = "tri";
		}else if (xLength > 9){
			xSuf = "bi";
		}else if (xLength > 6){
			xSuf = "mi";
		}else if (xLength > 3){
			xSuf = "mil";
		}else{
			xSimple = xVal;
		}
		if (xSuf != ""){
			return dbsfaces.format.number(xSimple, 2) + xSuf;
		}else{
			return dbsfaces.format.number(xVal, 2);
		}		
	},
	
	getDecimalSeparator: function(){
		if ((1.1).toLocaleString().indexOf(".") >= 0){
			return ".";
		}
		return ",";
	}

};

	
dbsfaces.string = {
	fromCharCode: function(pVal){
		//Ajuste para os códigos numéricos retornados pelo teclado estendido.
		return String.fromCharCode((96 <= pVal && pVal <= 105)? pVal-48 : pVal);
	}	
};

dbsfaces.ajax = {
	request: function(pSourceId, pExecuteIds, pRenderIds, pOnEvent, pOnError, pParams){
		if (pSourceId == null){return;}
		pSourceId = dbsfaces.util.jsid(pSourceId);
		var xSource = $(pSourceId);

	    var xForm = xSource.closest("form");
	    if (xForm.length == 0){return;}
	    
	    var xUrl = xForm.attr("action");
	    if (xUrl == null){return;}
	    
		var xData = xForm.serialize();
		xData += "&" + dbsfaces.JAVAX.SOURCE + "=" + xSource[0].id; 
		xData += "&" + dbsfaces.JAVAX.PARTIAL_AJAX + "=true";
		//EXECUTE
		xData += "&" + dbsfaces.JAVAX.PARTIAL_EXECUTE + "=" + xSource[0].id;
		if (pExecuteIds != null){
			pExecuteIds = pExecuteIds.replace("@this", xSource[0].id);
			pExecuteIds = pExecuteIds.replace("@form", xForm[0].id);
			xData += " " + pExecuteIds;
		}
		//RENDER
		if (pRenderIds != null){
			pRenderIds = pRenderIds.replace("@this", xSource[0].id);
			pRenderIds = pRenderIds.replace("@form", xForm[0].id);
			xData += "&" + dbsfaces.JAVAX.PARTIAL_RENDER + "=" + pRenderIds;
		}
		if (pParams != null){
			xData += "&params=" + pParams;
		}
		
		var xEvent = {};  // data payload for function
		xEvent.source = pSourceId;
		
        var xhrOptions = {
	        url : xUrl,
	        async: true,
	        type : "POST",
	        cache : false,
	        timeout: 0,
	        crossDomain: true,
	        data : xData,
	        dataType : "xml",
	        xhr: function(){
				var xXhr = new XMLHttpRequest();
				xXhr.onprogress = this.onprogress;
				xXhr.addEventListener('readystatechange',function(pEvt){
					if (pOnEvent && typeof pOnEvent === 'function') {
						xEvent.type = "event";
						xEvent.status = "statechange";
						xEvent.readyState = pEvt.target.readyState;
						pOnEvent(xEvent);
					}
				}, false);
				return xXhr;
	        },
	        beforeSend: function(xhr) {
	        	xhr.setRequestHeader('Faces-Request', 'partial/ajax');
	        },
	        error: function(pXhr, pStatus, pErrorThrown) {
        		console.log("erro");
                if (pOnError && typeof pOnError === 'function') {
            		xEvent.type = "error";
            		xEvent.status = pStatus;
            		pOnError(xEvent);
	        	}
	        },
	        complete : function(pXhr, pStatus) {
               if (pOnEvent && typeof pOnEvent === 'function') {
               		xEvent.type = "event";
            		xEvent.status = pStatus;
            		pOnEvent(xEvent);
	        	}
	        },
	        success : function(pData, pStatus, pXhr) {
	        	dbsfaces.ajax.response(pData);
	        	if (pOnEvent && typeof pOnEvent === 'function') {
               		xEvent.type = "event";
            		xEvent.status = pStatus;
            		pOnEvent(xEvent);
	        	}
	        }
	    };
        xhrOptions.global = false;
        $.ajax(xhrOptions);
	},
	
	response: function(pData){
	    xUpdates = $(pData).find('update');
	    for(var i=0; i < xUpdates.length; i++) {
	        var xUpdate = xUpdates.eq(i);
	        var xId = xUpdate.attr('id');
	        var xContent = xUpdate.text();
	        //Atualiza conteúdo do componente
	        var xTarget = $(dbsfaces.util.jsid(xId));
	        xTarget.replaceWith(xContent);
	        xTarget.trigger("loaded");
	        //Busca componente novamente já que foi substituido integralmente
	        xTarget = $(dbsfaces.util.jsid(xId));
	        //Se há controle para exibição gradativa posUpdate(DBSDiv)
	        if (xTarget.hasClass("-posUpdate")){
		        xTarget.css("opacity", "1");
		        xTarget.on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		        	$(e.target).removeClass("-posUpdate");
		        });
	        }
	    }
	},
	
	onprogress: function(pData){
		
	}
	
}
		
//Monitora evento ajax recebido e dispara evento dbsoft
dbsfaces.onajax = function(e, pData){
	xEle = $(e.source);
	if (xEle.length == 0){
		return;
	}
	if (e.status == "begin"){
		xEle.addClass("-ajaxBegin");
		xEle.trigger(dbsfaces.EVENT.ON_AJAX_BEGIN, pData);
	}else if (e.status == "complete"){
		xEle.trigger(dbsfaces.EVENT.ON_AJAX_COMPLETE, pData);
	}else if (e.status == "success"){
		xEle.removeClass("-ajaxBegin");
		xEle.trigger(dbsfaces.EVENT.ON_AJAX_SUCCESS, pData);
	}
};

dbsfaces.onajaxerror = function(e){
	xEle = $(e.source);
	xEle.removeClass("-ajaxBegin");
	xEle.trigger(dbsfaces.EVENT.ON_AJAX_ERROR);
	return false;
};

