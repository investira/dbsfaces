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
		dbsfaces.chartsX.pvInitializeLayout(pCharts);
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do -charts. Possível bug do safari quando display = flex.
		setTimeout(function(e){
			pCharts.removeClass("-hide");
			pCharts.data("charts").addClass("-hide");
		},0);
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do -charts. Possível bug do safari quando display = flex.
		setTimeout(function(e){
			dbsfaces.chartsX.pvInitializeAnalizeValues(pCharts);
			dbsfaces.chartsX.pvInitializeDraw(pCharts);
			pCharts.data("charts").removeClass("-hide");
		},0);
	},

	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		pCharts.data("type", pCharts.attr("type"));
		pCharts.data("container", pCharts.children(".-container"));
		pCharts.data("caption", pCharts.data("container").children(".-caption"));
		pCharts.data("childrencaption", pCharts.data("container").children(".-childrenCaption"));
		pCharts.data("childrencaptioncontainer", pCharts.data("childrencaption").children(".-container"));
		pCharts.data("charts", pCharts.data("container").children(".-charts"));
		pCharts.data("children", pCharts.data("charts").children(".dbs_chartX"));
		pCharts.data("footer", pCharts.data("container").children(".-footer"));
		pCharts.data("defs", pCharts.find("svg > defs").first());
		pCharts.data("data", pCharts.data("container").children(".-data"));
		pCharts.data("showdelta", pCharts.hasClass("-showDelta"));
		pCharts.data("showlabel", pCharts.hasClass("-showLabel"));
		pCharts.data("showgrid", pCharts.hasClass("-showGrid"));
		pCharts.data("showgridvalue", pCharts.hasClass("-showGridValue"));
	},


	//Ajusta posição das informações no gráfico pie
	pvInitializeLayout: function(pCharts){
		dbsfaces.chartsX.pvInitializeLayoutCreateChildrenCaption(pCharts);
		dbsfaces.chartsX.pvInitializeLayoutColor(pCharts);
	},
	
	pvInitializeLayoutCreateChildrenCaption: function(pCharts){
		var xChildrenCaption = pCharts.data("childrencaption");
		var xChildrenCaptionContainer = pCharts.data("childrencaptioncontainer");
		xChildrenCaptionContainer.empty();
		if (pCharts.data("children").length < 1){
			xChildrenCaption.css("display", "none");
		}else{
			pCharts.data("children").each(function(){
				//Caption
				var xElement = $(document.createElement("div"));
				xElement.addClass("-caption -th_col")
						.attr("id", $(this)[0].id +"_caption")
						.attr("chartid", $(this)[0].id);
				//Text do caption
				var xElementText = $(document.createElement("div"));
				xElementText.addClass("-text")
							.text($(this).attr("caption"));
				xElement.append(xElementText);
				//Incorpora elemento Caption
				xChildrenCaptionContainer.append(xElement);
			});
			//Seleciona o primeiro gráfico
			dbsfaces.chartsX.selectChart(pCharts, pCharts.data("children")[0].id);
			//Captura seleção de outro gráfico
			pCharts.data("childrencaptioncontainer").children().on("mousedown touchstart", function(e){
				dbsfaces.chartsX.selectChart(pCharts, $(this).attr("chartid"));
				e.stopImmediatePropagation();
				e.preventDefault();	
			});
		}
	},
	
	pvInitializeLayoutColor: function(pCharts){
		var xCurrentColor = tinycolor(pCharts.css("color")).toString();
		//Loop por todos os gráficos
		pCharts.data("children").each(function(){
			var xThis = $(this);
			var xUserColor = xThis.attr("color");
			//Caption
			var xChildrenCaption = $(dbsfaces.util.jsid(this.id + "_caption"));
			var xChildrenCaptionText = xChildrenCaption.children(".-text");
			if (typeof xUserColor == "undefined"){
				xUserColor = xCurrentColor;
			}
			var xColor = tinycolor(xUserColor);
			var xInverted = tinycolor(xUserColor).invertLightness().setAlpha(1);
			xChildrenCaptionText.css("color", xColor);
			xChildrenCaption.css("background-color", xColor)
							.css("border-color", xColor.setAlpha(.5))
							.css("color", xInverted);
		});
	},
	
	pvInitializeAnalizeValues: function(pCharts){
		var xChildren = pCharts.data("children");
		var xMax = null;
		var xMin = null;
		var xHeight = pCharts.data("charts")[0].getBoundingClientRect().height;
		var xWidth = pCharts.data("charts")[0].getBoundingClientRect().width;
		var xScaleX = 0;
		var xScaleY = 0;
		//Verifica menor e maior valor existentes em todos os gráficos para cálcular a escala
		for (var xI = 0; xI < xChildren.length; xI++){
			var xChartData = $(xChildren[xI]).data("data");
			if (xMin == null || xChartData.chartValueMin.data("data").value.value < xMin.data("data").value.value){
				xMin = xChartData.chartValueMin;
			}
			if (xMax == null || xChartData.chartValueMax.data("data").value.value > xMax.data("data").value.value){
				xMax = xChartData.chartValueMax;
			}
			if ((xChartData.values.length - 1) > xScaleX){
				xScaleX = xChartData.values.length - 1;
			}
			if (xI == 0){
				xHeight = xChartData.chart[0].getBoundingClientRect().height;
				xWidth = xChartData.chart[0].getBoundingClientRect().width;
			}
		}
		xScaleX = xWidth / xScaleX;
		xScaleY = xHeight / (-xMax.data("data").value.value + xMin.data("data").value.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior
		pCharts.data("chartvaluemin", xMin);
		pCharts.data("chartvaluemax", xMax);
		pCharts.data("width", xWidth);
		pCharts.data("height", xHeight);
		pCharts.data("scalex", xScaleX);
		pCharts.data("scaley", xScaleY);
	},

	pvInitializeDraw: function(pCharts){
		var xChildren = pCharts.data("children");
		//Loop em todos os gráficos
		for (var xI = 0; xI < xChildren.length; xI++){
			var xChartData = $(xChildren[xI]).data("data");
			var xChartChildren = xChartData.children;
			//DrawPoints
			//Loop em todos os pontos gráfico
			for (var xN = 0; xN < xChartChildren.length; xN++){
				var xChartValueData = $(xChartChildren[xN]).data("data");
				if (xChartData.type == "line"){
					dbsfaces.chartsX.pvInitializeDrawChartLine(pCharts, xChartData, xChartValueData);
				}
			}
//			if (xType == "line"){
//				dbsfaces.chartsX.pvInitializeDrawChartLine(xData);
//			}
		}
	},

	pvInitializeDrawChartLine: function(pCharts, pChartData, pChartValueData){
//		var xValue = pChartValue.data("value");
//		var xPoint = pChartValue.data("point");
//		var xIndex = pChartValue.data("index");
//		var xInfoLabel = pChartValue.data("infolabel");
//		var xInfoValue = pChartValue.data("infovalue");
//		var xInfoBox = pChartValue.data("infobox");
		var xMaxChartValue = pCharts.data("chartvaluemax");
		var xScaleX = pCharts.data("scalex");
		var xScaleY = pCharts.data("scaley");
		var xX = pChartValueData.index * xScaleX;
		var xY = (pChartValueData.value.value - xMaxChartValue.data("data").value.value) * xScaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
		
		xY = dbsfaces.math.round(xY, 0);
		xX = dbsfaces.math.round(xX, 0);
		
		//Salva coordenadas
		pChartValueData.x = xX
		pChartValueData.y = xY;
		
		//Posiciona ponto
		pChartValueData.point.svgAttr("cx", xX)
			  				 .svgAttr("cy", xY);
//			  				 .svgAttr("r", xPoint.css("r"));
		
		//Cria linha que conecta pontos
		var xPath;
		if (pChartValueData.index == 0){
			xPath = "M";
		}else{
			xPath = pChartData.path.attr("d") + "L";
		}
		xPath += xX + "," + xY;
		pChartData.path.attr("d", xPath);

		//Ajusta Box
		var xLabelWidth = pChartValueData.infoLabel[0].textLength.baseVal.value;
		var xValueWidth = pChartValueData.infoValue[0].textLength.baseVal.value;
		var xLabelHeight = pChartValueData.infoLabel.height();
		var xValueHeight = pChartValueData.infoValue.height();
		var xBoxHeight = (xLabelHeight + xValueHeight) * 1.30;
		var xBoxWidth = Math.max(xLabelWidth, xValueWidth) * 1.30;
		pChartValueData.infoLabel.svgAttr("x", xX)
				  				 .svgAttr("y", xY);
		pChartValueData.infoValue.svgAttr("x", xX)
		   		  				 .svgAttr("y", xY);
		pChartValueData.infoBox.svgAttr("width", xBoxWidth)
							   .svgAttr("height", xBoxHeight)
							   .svgAttr("x", xX)
							   .svgAttr("y", xY);
//		if (pData.chartValue[0] ==  pData.maxChartValue[0]){
//			dbsfaces.ui.moveToFront(pData.chartValue);
//			pData.chartValue.addClass("-max");
//			dbsfaces.chartsX.pvInitializeDrawChartLineMinMax(pData);
//		}
//		if (pData.chartValue[0] ==  pData.minChartValue[0]){
//			dbsfaces.ui.moveToFront(pData.chartValue);
//			pData.chartValue.addClass("-min");
//			dbsfaces.chartsX.pvInitializeDrawChartLineMinMax(pData);
//		}
	},
	
//	pvInitializeDrawChartLineMinMax: function(pData){
//		dbsfaces.ui.moveToFront(pData.chartValue);
//		if (pData.chartValue.data("index") <= (pData.chart.data("children").length / 2)){
//			pData.chartValue.addClass("-right");
//		}else{
//			pData.chartValue.addClass("-left");
//		}
//	},
	
	pvInitializeDrawBar: function(pCharts){
	},
	pvInitializeDrawPie: function(pCharts){
	},

	resize: function(pCharts){
	},

	selectChart: function(pCharts, pChartId){
		pCharts.data("childrencaptioncontainer").children().removeClass("-selected");
		pCharts.data("children").removeClass("-selected");
		var xChart = $(dbsfaces.util.jsid(pChartId));
		var xChartCaption = $(dbsfaces.util.jsid(pChartId + "_caption"));
		xChart.addClass("-selected");
		xChartCaption.addClass("-selected");
	}
		
};

