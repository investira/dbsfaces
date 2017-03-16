dbs_chartsX = function(pId) {
	var xCharts = $(pId);
	//Prepara gráfico com a altura e largura
	$(window).resize(function(e){
		dbsfaces.chartsX.resize(xCharts);
	});
	
	dbsfaces.chartsX.initialize(xCharts);
};

dbsfaces.chartsX = {

	initialize: function(pCharts){
		dbsfaces.chartsX.pvInitializeData(pCharts);
		var xChartsData = pCharts.data("data");
		dbsfaces.chartsX.pvInitializeLayout(xChartsData);
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do -charts. Possível bug do safari quando display = flex.
		setTimeout(function(e){
			pCharts.removeClass("-hide");
			xChartsData.dom.charts.addClass("-hide");
		},0);
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do -charts. Possível bug do safari quando display = flex.
		setTimeout(function(e){
			dbsfaces.chartsX.pvInitializeAnalizeValues(xChartsData);
			dbsfaces.chartsX.pvInitializeDraw(xChartsData);
			xChartsData.dom.charts.removeClass("-hide");
		},0);
	},

	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		var xData = {
			dom : {
				self : pCharts, //O próprio charts
				children : null,
				container : pCharts.children(".-container"), //Container do charts
				caption : null, //Caption
				childrenCaption : null,
				footer : null,
				charts : null,
				childrenCaptionContainer : null,
				chartValueMin: null,
				chartValueMax: null
			},
			type : pCharts.attr("type"),
			showLabel : pCharts.hasClass("-showLabel"),
			showDelta : pCharts.hasClass("-showDelta"),
			showGrid : pCharts.hasClass("-showGrid"),
			showGridValue : pCharts.hasClass("-showGridValue"),
			width : null, //Largura do espaço que contém o gráfico
			height : null, //Altura do espaço que contém o grático
			scaleX : null,
			scaleY : null,
			infoWidth: null, //Largura da coluna de informação(value)
			infoHeight: null, //ALtura da linha de informação(label)
			currentColorInverted: tinycolor(pCharts.css("color")).invertLightness().setAlpha(1).toString()
		}
		pCharts.data("data", xData);
		xData.dom.caption = xData.dom.container.children(".-caption");
		xData.dom.childrenCaption = xData.dom.container.children(".-childrenCaption");
		xData.dom.charts = xData.dom.container.children(".-charts");
		xData.dom.footer = xData.dom.container.children(".-footer");
		xData.dom.childrenCaptionContainer = xData.dom.childrenCaption.children(".-container");
		xData.dom.children = xData.dom.charts.children(".dbs_chartX");
	},


	//Ajusta posição das informações no gráfico pie
	pvInitializeLayout: function(pChartsData){
		dbsfaces.chartsX.pvInitializeLayoutCreateChildrenCaption(pChartsData);
		dbsfaces.chartsX.pvInitializeLayoutColor(pChartsData);
	},
	
	pvInitializeLayoutCreateChildrenCaption: function(pChartsData){
		//Exclui títulos existentes se houver
		pChartsData.dom.childrenCaptionContainer.empty();
		
		if (pChartsData.dom.children.length < 2){
			pChartsData.dom.childrenCaption.css("display", "none");
		}
		if (pChartsData.dom.children.length >0){
			//Cria títulos
			pChartsData.dom.children.each(function(){
				//Caption
				var xElement = $(document.createElement("div"));
				var xChartData = $(this).data("data");
				xElement.addClass("-caption -th_col")
						.attr("id", xChartData.dom.self[0].id +"_caption")
						.attr("chartid", this.id);
				//Text do caption
				var xElementText = $(document.createElement("div"));
				xElementText.addClass("-text")
							.text(xChartData.dom.self.attr("caption"));
				xElement.append(xElementText);
				//Atualiza data do chart
				xChartData.dom.caption = xElement;
				xChartData.dom.captionText = xElementText;
				//Incorpora elemento Caption
				pChartsData.dom.childrenCaptionContainer.append(xElement);
			});
			//Seleciona o primeiro gráfico
			dbsfaces.chartsX.selectChart(pChartsData, pChartsData.dom.children[0].id);
			//Captura seleção de outro gráfico
			pChartsData.dom.childrenCaptionContainer.children().on("mousedown touchstart", function(e){
				dbsfaces.chartsX.selectChart(pChartsData, $(this).attr("chartid"));
				e.stopImmediatePropagation();
				e.preventDefault();	
			});
		}
	},
	
	pvInitializeLayoutColor: function(pChartsData){
		//Loop por todos os gráficos
		pChartsData.dom.children.each(function(){
			var xChartData = $(this).data("data");
			//Caption
			xChartData.colorTransparent = tinycolor(xChartData.color).setAlpha(.5).toString();
			xChartData.colorInverted = tinycolor(xChartData.color).invertLightness().setAlpha(1).toString();
			if (xChartData.dom.caption != null){
				xChartData.dom.captionText.css("color", xChartData.color);
				xChartData.dom.caption.css("background-color", xChartData.color)
									  .css("border-color", xChartData.colorTransparent)
									  .css("color", xChartData.colorInverted);
			}
		});
	},
	
	pvInitializeAnalizeValues: function(pChartsData){
		//Inicializa com o largura e altura máximo
		if (pChartsData.dom.children.length > 0){
			var xChartValueMinData = null;
			var xChartValueMaxData = null;
			var xMaxCount = 0;
			//Verifica menor e maior valor existentes em todos os gráficos para cálcular a escala
			for (var xI = 0; xI < pChartsData.dom.children.length; xI++){
				var xChartData = $(pChartsData.dom.children[xI]).data("data");
				if (xChartValueMinData == null || xChartData.dom.chartValueMin.data("data").value.value < xChartValueMinData.value.value){
					xChartValueMinData = xChartData.dom.chartValueMin.data("data");
				}
				if (xChartValueMaxData == null || xChartData.dom.chartValueMax.data("data").value.value > xChartValueMaxData.value.value){
					xChartValueMaxData = xChartData.dom.chartValueMax.data("data");
				}
				//Quantidade máxima de itens de todos os gráficos
				if ((xChartData.originalValues.length - 1) > xMaxCount){
					xMaxCount = xChartData.originalValues.length - 1;
				}
				//Utiliza tamanho do primeiro gráfico para configurar o tamanho da áreas de gráfico
				if (xI == 0){
					pChartsData.height = xChartData.dom.chart[0].getBoundingClientRect().height;
					pChartsData.width = xChartData.dom.chart[0].getBoundingClientRect().width;
				}
			}
			pChartsData.infoHeight = xChartValueMaxData.dom.infoLabel.height() * 1.2;
			pChartsData.infoWidth = Math.max(xChartValueMaxData.dom.infoValue[0].textLength.baseVal.value, xChartValueMinData.dom.infoValue[0].textLength.baseVal.value) * 1.4;
			pChartsData.width -= pChartsData.infoWidth;
			pChartsData.height -= pChartsData.infoHeight;
			pChartsData.dom.chartValueMin = xChartValueMinData.dom.self;
			pChartsData.dom.chartValueMax = xChartValueMaxData.dom.self;
			xChartValueMinData.dom.self.addClass("-showValue"); //Força a exibição do value da linha
			xChartValueMaxData.dom.self.addClass("-showValue"); //Força a exibição do value da linha

			pChartsData.scaleX = pChartsData.width / xMaxCount;
			pChartsData.scaleY = pChartsData.height / (-xChartValueMaxData.value.value + xChartValueMinData.value.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior;
		}else{
			pChartsData.height = pChartsData.dom.charts[0].getBoundingClientRect().height;
			pChartsData.width = pChartsData.dom.charts[0].getBoundingClientRect().width;
		}
	},

	pvInitializeDraw: function(pChartsData){
		//Loop em todos os gráficos
		for (var xI = 0; xI < pChartsData.dom.children.length; xI++){
			var xChartData = $(pChartsData.dom.children[xI]).data("data");
			var xChartChildren = xChartData.dom.children;
			//DrawPoints
			//Loop em todos os pontos gráfico
			for (var xN = 0; xN < xChartData.dom.children.length; xN++){
				var xChartValueData = $(xChartData.dom.children[xN]).data("data");
				if (xChartData.type == "line"){
					dbsfaces.chartsX.pvInitializeDrawChartLine(pChartsData, xChartData, xChartValueData);
				}
			}
//			if (xType == "line"){
//				dbsfaces.chartsX.pvInitializeDrawChartLine(xData);
//			}
		}
	},

	pvInitializeDrawChartLine: function(pChartsData, pChartData, pChartValueData){
		var xX = pChartsData.infoWidth;
		var xY = pChartsData.infoHeight;
		xX += pChartValueData.index * pChartsData.scaleX;
		xY += (pChartValueData.value.value - pChartsData.dom.chartValueMax.data("data").value.value) * pChartsData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
		
		xY = dbsfaces.math.round(xY, 0);
		xX = dbsfaces.math.round(xX, 0);
		
		//Salva coordenadas
		pChartValueData.x = xX
		pChartValueData.y = xY;
		
		//Posiciona ponto
		pChartValueData.dom.point.svgAttr("cx", xX)
			  				     .svgAttr("cy", xY);
//			  				 .svgAttr("r", xPoint.css("r"));
		
		//Cria linha que conecta pontos
		var xPath;
		if (pChartValueData.index == 0){
			xPath = "M";
		}else{
			xPath = pChartData.dom.path.svgAttr("d") + "L";
		}
		xPath += xX + "," + xY;
		pChartData.dom.path.svgAttr("d", xPath);

		//Info
		xPath = null;
		var xLabelWidth;
		var xValueWidth;
		var xLabelHeight;
		var xValueHeight;
		var xBoxLabelHeight;
		var xBoxLabelWidth;
		var xBoxValueHeight;
		var xBoxValueWidth;
		var xStyleBox = "fill:" + pChartsData.currentColorInverted;
//		var xStyleText = "fill:" + pChartData.color;
//		var xBoxX = 0;
		if (pChartData.type == "line"
		 || pChartData.type == "bar"){
			xLabelWidth = pChartValueData.dom.infoLabel[0].textLength.baseVal.value;
			xValueWidth = pChartsData.infoWidth;
			xLabelHeight = pChartsData.infoHeight;
			xValueHeight = pChartValueData.dom.infoValue.height();
			xBoxLabelHeight = pChartValueData.dom.infoLabel.height(); //xLabelHeight;
			xBoxLabelWidth = xLabelWidth * 1.10;
			xBoxValueHeight = xValueHeight;
			xBoxValueWidth = xValueWidth * 0.90;
//			xBoxWidth = Math.max(xLabelWidth, xValueWidth) * 1.30;
//			dbsfaces.ui.cssTransform(pChartValueData.dom.info, "translateX(" + xBoxX + ")");
			xPath = "M" + xX + "," + pChartsData.infoHeight;
			xPath += "L" + xX + "," + xY
			xPath += "L" + pChartsData.infoWidth + "," + xY;
			//Ajuste X
			var xHalf = (xLabelWidth / 2);
			xX -= xHalf;
			if (xX + xLabelWidth > (pChartsData.width + pChartsData.infoWidth)){
				xX -= (xX + xLabelWidth) - (pChartsData.width + pChartsData.infoWidth);
			}else if (xX < pChartsData.infoWidth){
				xX = pChartsData.infoWidth;
			}
			//Ajuste Y
			xHalf = (xValueHeight / 2);
			if (xY + xHalf > (pChartsData.height + pChartsData.infoHeight)){
				xY -= (xY + xHalf) - (pChartsData.height + pChartsData.infoHeight);
			}else if (xY - xHalf < pChartsData.infoHeight){
				xY += xHalf;
			}
		}
		pChartValueData.dom.infoLabel.svgAttr("x", xX)
				  				     .svgAttr("y", ".7em");
//				  				     .svgAttr("style", xStyleText);
		pChartValueData.dom.infoValue.svgAttr("x", pChartsData.infoWidth * .80)
		   		  				      .svgAttr("y", xY);
//		   		  				      .svgAttr("style", xStyleText);
		pChartValueData.dom.infoBoxLabel.svgAttr("width", xBoxLabelWidth)
							       		.svgAttr("height", xBoxLabelHeight)
							       		.svgAttr("x", xX - (xBoxLabelWidth * .05))
							       		.svgAttr("y", "-.2em")
							       		.svgAttr("style", xStyleBox);
		pChartValueData.dom.infoBoxValue.svgAttr("width", xBoxValueWidth)
								   		.svgAttr("height", xBoxValueHeight)
								   		.svgAttr("x", 0)
								   		.svgAttr("y", xY - (xBoxValueHeight / 2))
								   		.svgAttr("style", xStyleBox);
		
		pChartValueData.dom.infoPath.svgAttr("d", xPath);
	},
	
	pvInitializeDrawBar: function(pChartsData){
	},
	pvInitializeDrawPie: function(pChartsData){
	},

	resize: function(pChartsData){
	},

	selectChart: function(pChartsData, pChartId){
		pChartsData.dom.childrenCaptionContainer.children().removeClass("-selected");
		pChartsData.dom.children.removeClass("-selected");
		var xChart = $(dbsfaces.util.jsid(pChartId));
		var xChartCaption = $(dbsfaces.util.jsid(pChartId + "_caption"));
		xChart.addClass("-selected");
		xChartCaption.addClass("-selected");
		dbsfaces.ui.moveToFront(xChart);
	}
		
};

