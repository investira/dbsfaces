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
			width : null,
			height : null,
			scaleX : null,
			scaleY : null
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
		
		if (pChartsData.dom.children.length < 1){
			pChartsData.dom.childrenCaption.css("display", "none");
		}else{
			//Cria títulos
			pChartsData.dom.children.each(function(){
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
		var xCurrentColor = tinycolor(pChartsData.dom.self.css("color")).toString();
		//Loop por todos os gráficos
		pChartsData.dom.children.each(function(){
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
	
	pvInitializeAnalizeValues: function(pChartsData){
		pChartsData.height = pChartsData.dom.charts[0].getBoundingClientRect().height;
		pChartsData.width = pChartsData.dom.charts[0].getBoundingClientRect().width;
		var xMax = null;
		var xMin = null;
		var xScaleX = 0;
		var xScaleY = 0;
		//Verifica menor e maior valor existentes em todos os gráficos para cálcular a escala
		for (var xI = 0; xI < pChartsData.dom.children.length; xI++){
			var xChartData = $(pChartsData.dom.children[xI]).data("data");
			if (xMin == null || xChartData.dom.chartValueMin.data("data").value.value < xMin.data("data").value.value){
				xMin = xChartData.dom.chartValueMin;
			}
			if (xMax == null || xChartData.dom.chartValueMax.data("data").value.value > xMax.data("data").value.value){
				xMax = xChartData.dom.chartValueMax;
			}
			if ((xChartData.originalValues.length - 1) > xScaleX){
				xScaleX = xChartData.originalValues.length - 1;
			}
			if (xI == 0){
				pChartsData.height = xChartData.dom.chart[0].getBoundingClientRect().height;
				pChartsData.width = xChartData.dom.chart[0].getBoundingClientRect().width;
			}
		}
		xScaleX = pChartsData.width / xScaleX;
		xScaleY = pChartsData.height / (-xMax.data("data").value.value + xMin.data("data").value.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior
		pChartsData.dom.chartValueMin = xMin;
		pChartsData.dom.chartValueMax = xMax;
		pChartsData.scaleX = xScaleX;
		pChartsData.scaleY = xScaleY;
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
		var xX = pChartValueData.index * pChartsData.scaleX;
		var xY = (pChartValueData.value.value - pChartsData.dom.chartValueMax.data("data").value.value) * pChartsData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
		
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
			xPath = pChartData.dom.path.attr("d") + "L";
		}
		xPath += xX + "," + xY;
		pChartData.dom.path.attr("d", xPath);

		//Ajusta Box
		var xLabelWidth = pChartValueData.dom.infoLabel[0].textLength.baseVal.value;
		var xValueWidth = pChartValueData.dom.infoValue[0].textLength.baseVal.value;
		var xLabelHeight = pChartValueData.dom.infoLabel.height();
		var xValueHeight = pChartValueData.dom.infoValue.height();
		var xBoxHeight = (xLabelHeight + xValueHeight) * 1.30;
		var xBoxWidth = Math.max(xLabelWidth, xValueWidth) * 1.30;
		pChartValueData.dom.infoLabel.svgAttr("x", xX)
				  				     .svgAttr("y", xY);
		pChartValueData.dom.infoValue.svgAttr("x", xX)
		   		  				      .svgAttr("y", xY);
		pChartValueData.dom.infoBox.svgAttr("width", xBoxWidth)
							       .svgAttr("height", xBoxHeight)
							       .svgAttr("x", xX)
							       .svgAttr("y", xY);
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
	}
		
};

