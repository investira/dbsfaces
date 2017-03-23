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
			showValue : pCharts.hasClass("-showValue"),
			showDelta : pCharts.hasClass("-showDelta"),
			isPerc: pCharts.attr("perc"),
			width : null, //Largura do espaço que contém o gráfico incluindo sem as colunas e linhas de informação
			height : null, //Altura do espaço que contém o grático incluindo sem as colunas e linhas de informação
			scaleX : null, //Fator de proporção dos ponto do gráfico com os ponto em tela
			scaleY : null, //Fator de proporção dos ponto do gráfico com os ponto em tela
			infoWidth: 0, //Largura da coluna de informação(value)
			infoHeight: 0, //ALtura da linha de informação(label)
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
					pChartsData.height = xChartData.dom.chart[0].getBoundingClientRect().height; //xChartData.height;
					pChartsData.width = xChartData.dom.chart[0].getBoundingClientRect().width; //xChartData.width;
				}
			}
			if (pChartsData.showLabel){
//				pChartsData.infoHeight = Math.round(xChartValueMaxData.dom.infoLabel.height() * 1.2, 0);
				pChartsData.infoHeight = Math.round(parseFloat(xChartValueMaxData.dom.infoLabel.css("font-size")) * 1.2, 0);
			}
			if (pChartsData.showValue){
				pChartsData.infoWidth = Math.round(Math.max(xChartValueMaxData.dom.infoValue[0].getBoundingClientRect().width, xChartValueMinData.dom.infoValue[0].getBoundingClientRect().width) * 1.4, 0);
			}
			pChartsData.width -= pChartsData.infoWidth;
//			pChartsData.height -= pChartsData.infoHeight;
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
				if (pChartsData.type == "line"){
					dbsfaces.chartsX.pvInitializeDrawChartLine(pChartsData, xChartData, xChartValueData);
					//Primeiro item
					if (xN == 0){
						dbsfaces.chartX.setMovingDeltaHandleData(xChartData, xChartData.dom.leftDeltaHandleData);
						dbsfaces.chartX.selectChartValue(xChartData, xChartValueData);
						dbsfaces.chartX.setMovingDeltaHandleData(xChartData, null);
					//Último item
					}else if (xN == xChartData.dom.children.length - 1){
						dbsfaces.chartX.setMovingDeltaHandleData(xChartData, xChartData.dom.rightDeltaHandleData);
						dbsfaces.chartX.selectChartValue(xChartData, xChartValueData);
						dbsfaces.chartX.setMovingDeltaHandleData(xChartData, null);
					}
				}
			}
			if (pChartsData.type == "line"){
				//Configura posição inicial dos controles do delta 
				if (pChartsData.showDelta){
					//Calcula centroY
					var xMiddleY = (pChartsData.height + pChartsData.infoHeight) / 2;
					var xMiddleX = (pChartsData.width + pChartsData.infoWidth) / 2;
					//handle a esqueda
					xChartData.dom.leftDeltaHandleData.dom.handle.svgAttr("y", xMiddleY);
					xChartData.dom.leftDeltaHandleData.dom.rect.svgAttr("y", pChartsData.infoHeight);
					xChartData.dom.leftDeltaHandleData.dom.rect.svgAttr("height", pChartsData.height);
					//handle a direita
					xChartData.dom.rightDeltaHandleData.dom.handle.svgAttr("y", xMiddleY);
					xChartData.dom.rightDeltaHandleData.dom.rect.svgAttr("y", pChartsData.infoHeight);
					xChartData.dom.rightDeltaHandleData.dom.rect.svgAttr("height", pChartsData.height);
					//Texto do valor
					xChartData.dom.deltaValue.svgAttr("y", xMiddleY);
					xChartData.dom.deltaValue.svgAttr("x", xMiddleX);
				}
			}
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
		var xLabelHeight;
		var xBoxLabelHeight;
		var xBoxLabelWidth;
		var xValueWidth;
		var xValueHeight;
		var xBoxValueHeight;
		var xBoxValueWidth;
		var xStyleBox = "fill:" + pChartsData.currentColorInverted;
		if (pChartData.type == "line"
		 || pChartData.type == "bar"){
			var xHalf;
			//AJusta Layout Label
			if (pChartsData.showLabel){
				//Dimensões
				xLabelWidth = pChartValueData.dom.infoLabel[0].getBoundingClientRect().width;
				xLabelHeight = pChartsData.infoHeight;
				xBoxLabelHeight = xLabelHeight // pChartValueData.dom.infoLabel.height(); //;
				xBoxLabelWidth = xLabelWidth * 1.10;
				//Verifica limites
				xHalf = (xLabelWidth / 2);
				xX -= xHalf;
				if (xX + xLabelWidth > (pChartsData.width + pChartsData.infoWidth)){
					xX -= (xX + xLabelWidth) - (pChartsData.width + pChartsData.infoWidth);
				}else if (xX < pChartsData.infoWidth){
					xX = pChartsData.infoWidth;
				}
				//Configura elementos
				pChartValueData.dom.infoLabel.svgAttr("x", xX)
				     						 .svgAttr("y", ".7em");
				pChartValueData.dom.infoBoxLabel.svgAttr("width", xBoxLabelWidth)
								       		.svgAttr("height", xBoxLabelHeight)
								       		.svgAttr("x", xX - (xBoxLabelWidth * .05))
								       		.svgAttr("y", "-.2em")
								       		.svgAttr("style", xStyleBox);
			}
			//AJusta Layout Value
			if (pChartsData.showValue){
				xValueWidth = pChartsData.infoWidth;
				xValueHeight = pChartsData.infoHeight; //pChartValueData.dom.infoValue.height();
				xBoxValueHeight = xValueHeight;
				xBoxValueWidth = xValueWidth * 0.90;
				//Verifica limites
				xHalf = (xValueHeight / 2);
				if (xY + xHalf > (pChartsData.height + pChartsData.infoHeight)){
					xY -= (xY + xHalf) - (pChartsData.height + pChartsData.infoHeight);
				}else if (xY - xHalf < pChartsData.infoHeight){
					xY += xHalf;
				}
				//Configura elementos
				pChartValueData.dom.infoValue.svgAttr("x", pChartsData.infoWidth * .80)
				      						 .svgAttr("y", xY);
				pChartValueData.dom.infoBoxValue.svgAttr("width", xBoxValueWidth)
							   					.svgAttr("height", xBoxValueHeight)
							   					.svgAttr("x", 0)
							   					.svgAttr("y", xY - (xBoxValueHeight / 2))
							   					.svgAttr("style", xStyleBox);
			}
			//Configura linha guida entre o label e o value
			if (pChartsData.showLabel
			 && pChartsData.showValue){
				xPath = "M" + pChartValueData.x + "," + pChartsData.infoHeight;
				xPath += "L" + pChartValueData.x + "," + pChartValueData.y
				xPath += "L" + pChartsData.infoWidth + "," + pChartValueData.y;
			}else if(pChartsData.showLabel){
				xPath = "M" + pChartValueData.x + "," + pChartValueData.y;
				xPath += "L" + pChartValueData.x + "," + pChartsData.infoHeight;
			}else if(pChartsData.showValue){
				xPath = "M" + pChartValueData.x + "," + pChartValueData.y;
				xPath += "L" + pChartsData.infoWidth + "," + pChartValueData.y;
			}
		}
		//Desenha linha guia entre o vale e value
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

