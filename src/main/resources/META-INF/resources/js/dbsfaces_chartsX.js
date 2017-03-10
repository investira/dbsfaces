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
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do chartscontainer. Possível bug do safari quando display = flex.
		setTimeout(function(e){
			pCharts.removeClass("-hide");
			pCharts.data("chartscontainer").addClass("-hide");
		},0);
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do chartscontainer. Possível bug do safari quando display = flex.
		setTimeout(function(e){
			dbsfaces.chartsX.pvInitializeAnalizeValues(pCharts);
			dbsfaces.chartsX.pvInitializeDraw(pCharts);
			pCharts.data("chartscontainer").removeClass("-hide");
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
		pCharts.data("chartscontainer", pCharts.data("charts").children(".-container"));
		pCharts.data("children", pCharts.data("chartscontainer").children(".dbs_chartX"));
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
		var xHeight = pCharts.data("chartscontainer")[0].getBoundingClientRect().height;
		var xWidth = pCharts.data("chartscontainer")[0].getBoundingClientRect().width;
		var xScaleX = 0;
		var xScaleY = 0;
		//Verifica menor e maior valor existentes em todos os gráficos para cálcular o escale
		for (var xI = 0; xI < xChildren.length; xI++){
			var xChart = $(xChildren[xI]);
			if (xMin == null || xChart.data("min") < xMin){
				xMin = xChart.data("min");
			}
			if (xMax == null || xChart.data("max") > xMax){
				xMax = xChart.data("max");
			}
			if ((xChart.data("values").length - 1) > xScaleX){
				xScaleX = xChart.data("values").length - 1;
			}
			if (xI == 0){
				xHeight = xChart[0].getBoundingClientRect().height;
				xWidth = xChart[0].getBoundingClientRect().width;
			}
		}
		xScaleX = xWidth / xScaleX;
		xScaleY = xHeight / (-xMax + xMin); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior
//		xScaleY = xHeight / (xMax - xMin); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior
		pCharts.data("min", xMin);
		pCharts.data("max", xMax);
		pCharts.data("width", xWidth);
		pCharts.data("height", xHeight);
		pCharts.data("scalex", xScaleX);
		pCharts.data("scaley", xScaleY);
	},

	pvInitializeDraw: function(pCharts){
		var xChildren = pCharts.data("children");
		var xScaleX = pCharts.data("scalex");
		var xScaleY = pCharts.data("scaley");
		var xType = pCharts.data("type");
		var xMin = pCharts.data("min");
		var xMax = pCharts.data("max");
		for (var xI = 0; xI < xChildren.length; xI++){
			var xChart = $(xChildren[xI]);
			var xChartChildren = xChart.data("children");
//			for (var xChildrenChartValue in xChartChildren){
			//DrawPoints
			for (var xN = 0; xN < xChartChildren.length; xN++){
				var xChartValue = $(xChartChildren[xN]);
				var xData = {
					chart:	xChart,
					chartValue:	xChartValue,
					value : xChartValue.data("value"),
					point : xChartValue.data("point"),
					label : xChartValue.data("label"),
					index : xChartValue.data("index"),
					info : xChartValue.data("info"),
					infoLabel : xChartValue.data("infolabel"),
					infoValue : xChartValue.data("infovalue"),
					infoBox : xChartValue.data("infobox"),
					min: xMin,
					max: xMax,
					scaleX : xScaleX,
					scaleY : xScaleY
				}
				if (xType == "line"){
					dbsfaces.chartsX.pvInitializeDrawChartLine(xData);
				}
			}
//			if (xType == "line"){
//				dbsfaces.chartsX.pvInitializeDrawChartLine(xData);
//			}
		}
	},

	pvInitializeDrawChartLine: function(pData){
		var xX = pData.index * pData.scaleX;
		var xY = (pData.value.value - pData.max) * pData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
		//Salva coordenadas
		pData.chartValue.data("x", xX)
						.data("y", xY);
		//Posiciona ponto
		pData.point.svgAttr("cx", xX)
			       .svgAttr("cy", xY);
		//Cria linha que conecta pontos
		var xPath;
		if (pData.index == 0){
			xPath = "M";
		}else{
			xPath = pData.chart.data("linepath").attr("d") + "L";
		}
		xPath += xX + "," + xY;
		pData.chart.data("linepath").attr("d", xPath);
		//Ajusta Box
		var xLabelWidth = pData.infoLabel[0].textLength.baseVal.value;
		var xValueWidth = pData.infoValue[0].textLength.baseVal.value;
		var xLabelHeight = pData.infoLabel.height();
		var xValueHeight = pData.infoValue.height();
		var xBoxHeight = xLabelHeight + xValueHeight;
		var xBoxWidth = Math.max(xLabelWidth, xValueWidth);
		pData.infoLabel.svgAttr("x", xX)
					   .svgAttr("y", xY);
		pData.infoValue.svgAttr("x", xX)
		   			   .svgAttr("y", xY);
		pData.infoBox.svgAttr("width", xBoxWidth)
					 .svgAttr("height", xBoxHeight)
					 .svgAttr("x", xX - (xBoxWidth / 2))
					 .svgAttr("y", xY - (xBoxHeight * 2));
	},
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

