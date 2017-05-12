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
//		dbsfaces.chartsX.pvInitializeDefs(pCharts);
		var xChartsData = dbsfaces.chartsX.pvInitializeData(pCharts);
		dbsfaces.chartsX.pvInitializeLayout(xChartsData);
		dbsfaces.chartsX.pvInitializeAnalizeValues(xChartsData);
		dbsfaces.chartsX.pvInitializeDraw(xChartsData);
		xChartsData.dom.container.removeClass("-hide");
	},
	
//	pvInitializeDefs: function(pCharts){
//		var xSvg = dbsfaces.svg.svg(pCharts, null, null, null, null, null, null, {x:0, y:0});
//		var xDef = dbsfaces.svg.createElement(xSvg, "defs");
//		var xFilter = dbsfaces.svg.createElement(xDef, "filter", {id:"chartsblur", x:"-50%", y:"-50%", width:"200%", height:"200%", filterUnits:"userSpaceOnUse"});
//		var xFilterGaussian = dbsfaces.svg.createElement(xFilter, "feGaussianBlur", {in:"SourceGraphic", stdDeviation:1});
//	},
	
	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		var xData = {
			dom : {
				self : pCharts, //O próprio charts
				childrenData : null,
				container : pCharts.children(".-container"), //Container do charts
				caption : null, //Caption
				childrenCaption : null,
				footer : null,
				charts : null, //Container dos chart
				childrenCaptionContainer : null,
				minChartValueData: null, //ChartValue que contém menor valor
				maxChartValueData: null, //ChartValue que contém maior valor
				maxLabelChartValueData : null //chartValue que contém o maior label
			},
			type : pCharts.attr("type"),
			showLabel : pCharts.hasClass("-showLabel"),
			showValue : pCharts.hasClass("-showValue"),
			showDelta : pCharts.hasClass("-showDelta"),
			isPerc: false, //Se valores são percentuais. usado no cálculo do delta
			width : null, //Largura do espaço que contém o gráfico incluindo sem as colunas e linhas de informação
			height : null, //Altura do espaço que contém o grático incluindo sem as colunas e linhas de informação
			scaleX : null, //Fator de proporção dos ponto do gráfico com os ponto em tela
			scaleY : null, //Fator de proporção dos ponto do gráfico com os ponto em tela
			infoWidth: 0, //Largura da coluna de informação(value)
			infoHeight: 0, //ALtura da linha de informação(label)
			globalSequencesCount: 0, //Quantidade total de chartvalues considerando todos os gráficos 
			currentColorInverted: tinycolor(pCharts.css("color")).invertLightness().setAlpha(1).toString()
		}
		pCharts.data("data", xData);
		xData.dom.caption = xData.dom.container.children(".-caption");
		xData.dom.childrenCaption = xData.dom.container.children(".-childrenCaption");
		xData.dom.charts = xData.dom.container.children(".-charts");
		xData.dom.footer = xData.dom.container.children(".-footer");
		xData.dom.childrenCaptionContainer = xData.dom.childrenCaption.children(".-container");
		xData.dom.childrenData = [];
		//Configura numero do index do chart.
		xData.dom.charts.children(".dbs_chartX").each(function(pIndex){
			$(this).data("data").index = pIndex;
			//Adiciona elemento chart na lista de filhos do charts. Artifício para manter a ordem dos elementos independentemente da ordem dentro do com pai.
			xData.dom.childrenData.push($(this).data("data"));
		});
		return xData;
	},


	//Ajusta posição das informações no gráfico pie
	pvInitializeLayout: function(pChartsData){
		dbsfaces.chartsX.pvInitializeLayoutCreateChildrenCaption(pChartsData);
	},
	
	pvInitializeLayoutCreateChildrenCaption: function(pChartsData){
		//Exclui títulos existentes se houver
		pChartsData.dom.childrenCaptionContainer.empty();
		
		if (pChartsData.dom.childrenData.length < 2){
			pChartsData.dom.childrenCaption.css("display", "none");
		}
		if (pChartsData.dom.childrenData.length > 0){
			//Cria títulos
			pChartsData.dom.childrenData.forEach(function(pChartData){
				//Caption
				var xElement = $(document.createElement("div"));
				xElement.addClass("-caption -th_col")
						.attr("id", pChartData.dom.self[0].id +"_caption")
						.attr("chartid", pChartData.dom.self[0].id); //Id do chart a quem pertence este caption
				//Text do caption
				var xElementText = $(document.createElement("div"));
				xElementText.addClass("-text")
							.text(pChartData.dom.self.attr("caption"));
				xElement.append(xElementText);
				//Atualiza data do chart
				pChartData.dom.caption = xElement;
				pChartData.dom.captionText = xElementText;
				//Incorpora elemento Caption
				pChartsData.dom.childrenCaptionContainer.append(xElement);
			});
			//Seleciona o primeiro gráfico
			dbsfaces.chartsX.selectChart(pChartsData, pChartsData.dom.childrenData[0].dom.self[0].id);
			//Captura seleção de outro gráfico
			pChartsData.dom.childrenCaptionContainer.children().on("mousedown touchstart", function(e){
				dbsfaces.chartsX.selectChart(pChartsData, $(this).attr("chartid"));
				e.stopImmediatePropagation();
				e.preventDefault();	
			});
		}
	},
	
	pvInitializeAnalizeValues: function(pChartsData){
		//Inicializa com o largura e altura máximo
		if (pChartsData.dom.childrenData.length > 0){
			var xMaxCount = 0;
			//Verifica menor e maior valor existentes em todos os gráficos para cálcular a escala
			pChartsData.dom.childrenData.forEach(function(pChartData, pI) {
				if (pChartsData.dom.minChartValueData == null || pChartData.dom.minChartValueData.value < pChartsData.dom.minChartValueData.value){
					pChartsData.dom.minChartValueData = pChartData.dom.minChartValueData;
				}
				if (pChartsData.dom.maxChartValueData == null || pChartData.dom.maxChartValueData.value > pChartsData.dom.maxChartValueData.value){
					pChartsData.dom.maxChartValueData = pChartData.dom.maxChartValueData;
				}
				//Salva maior label
				if (pChartsData.dom.maxLabelChartValueData == null || pChartData.dom.maxLabelChartValueData.label.length > pChartsData.dom.maxLabelChartValueData.label.length){
					pChartsData.dom.maxLabelChartValueData = pChartData.dom.maxLabelChartValueData;
				}

				//Quantidade máxima de itens de todos os gráficos
				if ((pChartData.originalValues.length - 1) > xMaxCount){
					xMaxCount = pChartData.originalValues.length - 1;
				}
				//Utiliza tamanho do primeiro chart para configurar o tamanho padrão das áreas de todos os chart.
				if (pI == 0){
					pChartsData.height = pChartData.dom.chart[0].getBoundingClientRect().height; 
					pChartsData.width = pChartData.dom.chart[0].getBoundingClientRect().width;
				}
			});
			if (pChartsData.type == "line"){
				if (pChartsData.showLabel){
					pChartsData.infoHeight = Math.round(parseFloat(pChartsData.dom.maxChartValueData.dom.infoLabel.css("font-size")) * 1.2, 0);
				}
				if (pChartsData.showValue){
					pChartsData.infoWidth = Math.round(Math.max(pChartsData.dom.maxChartValueData.dom.infoValue[0].getBoundingClientRect().width, 
																pChartsData.dom.minChartValueData.dom.infoValue[0].getBoundingClientRect().width) * 1.4, 0);
				}
				//Trai espaço dos infos da dimensão disponível para o gráfico principal
				pChartsData.width -= pChartsData.infoWidth;
				pChartsData.height -= pChartsData.infoHeight;
				//Força a exibição do value da linha
				pChartsData.dom.minChartValueData.dom.self.addClass("-showValue"); 
				pChartsData.dom.maxChartValueData.dom.self.addClass("-showValue");
				//Escale para ajustar as coordenadas dentro do espaço do gráfico
				pChartsData.scaleX = pChartsData.width / xMaxCount;
				pChartsData.scaleY = -pChartsData.height / (pChartsData.dom.maxChartValueData.value - pChartsData.dom.minChartValueData.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior;
			}else if (pChartsData.type == "pie"){
				pChartsData.infoWidth = pChartsData.dom.maxLabelChartValueData.dom.infoLabel[0].getComputedTextLength() * 1.10; 
				pChartsData.infoHeight = 0; //Será configurado posteriomente como a altura do caption do relationalgroup(se houver);
			}
		}else{
			
			pChartsData.height = pChartsData.dom.charts[0].getBoundingClientRect().height;
			pChartsData.width = pChartsData.dom.charts[0].getBoundingClientRect().width;
		}
	},

	pvInitializeDraw: function(pChartsData){
		//Loop em todos os gráficos
		pChartsData.dom.childrenData.forEach(function(pChartData){
			//Configura qual o sequence
			pChartData.globalSequence = pChartsData.globalSequencesCount + 1;
			//Centro do gráfico
			pChartData.center.x = pChartsData.width / 2;
			pChartData.center.y = pChartsData.height / 2;
			
			if (pChartsData.type == "pie"){
				dbsfaces.chartsX.pvInitializeDrawInitializePie(pChartsData, pChartData);
			}

			//DrawPoints
			//Loop em todos os pontos gráfico
			pChartData.dom.childrenData.forEach(function(pChartValueData, pI){
				if (pChartsData.type == "line"){
					dbsfaces.chartsX.pvInitializeDrawChartLine(pChartsData, pChartData, pChartValueData);
				}else if (pChartsData.type == "pie"){
					dbsfaces.chartsX.pvInitializeDrawChartPie(pChartsData, pChartData, pChartValueData);
				}
				//Soma um item a quantidade global de chartvalues
				pChartsData.globalSequencesCount++;
				//Configura o sequence global deste chartvalue
				pChartValueData.globalSequence = pChartsData.globalSequencesCount;
			});
			
			
			//Configura posição inicial dos controles do delta 
			dbsfaces.chartsX.pvInitializeDrawDelta(pChartsData, pChartData);
			if (pChartsData.type == "line"){
			}else if (pChartsData.type == "pie"){
				//Desenha relacionamentos
				dbsfaces.chartsX.pvInitializeDrawRelationships(pChartsData, pChartData);
				//Desenha infos
//				dbsfaces.chartsX.pXInitializeDrawChartPieInfos(pChartData);
			}
		});
		//Configura cor
		dbsfaces.chartsX.pvInitializeDrawSetColor(pChartsData);
	},

	pvInitializeDrawInitializePie: function(pChartsData, pChartData){
		//Configura medida do arco de cada relationa group
		//Divide diametro entres os relationalGroups
		pChartData.arcFator = dbsfaces.math.PIDiameterFactor / pChartData.relationalCaptionsCount;
		//Se houver mais de um grupo de label, diminuir espaço entre os gráficos do espáco total do arco
		if (pChartData.relationalCaptionsCount > 1){
			pChartData.arcFator -= pChartData.arcSpace;
		}

		//Diametro do chart. Menor valor entre a altura e a largura
		pChartData.diameter = Math.min(pChartsData.width, pChartsData.height);
		
		//Largura do arco. Utiliza a largura definida no primeiro chartvalue
		pChartsData.infoHeight = 0;
		//Caminho que receberá o caption do relationalGroup
		if (pChartData.relationalCaptions.length > 1){
			pChartData.relationalCaptions.forEach(function(pRelationalCaption, pI){
				var xPathElementId = pChartData.dom.self[0].id + ":relationalPath_" + pI;
				var xPathElement = $(dbsfaces.util.jsid(xPathElementId));
				pChartsData.infoHeight = parseInt(xPathElement.css("font-size")) / 1.5; 
				//Desenha elemento do arco
				dbsfaces.chartsX.pvDrawArc(pChartData, 
										   xPathElement, 
										   (pChartData.diameter / 2) - (pChartsData.infoHeight / 3), //Raio externo do arco 
										   0, //Largura da arco
										   pI, 
										   0, 
										   100, 
										   true);
			});
		}

		var xCircleStrokeWidth = parseFloat(pChartData.dom.deltaCircle.css("stroke-width")) + 1;
		//Largura do arco do ponto
		pChartData.arcWidth = pChartsData.infoWidth; 
		//Raio do posição do arco do ponto
		pChartData.pointRadius = (pChartData.diameter / 2) - pChartsData.infoHeight;
		//Raio do posição do arco que liga o ponto ao círculo central
		pChartData.pointLinkRadius = pChartData.pointRadius - pChartData.arcWidth;
		//Largura do posição do arco que liga o ponto ao círculo central
		pChartData.pointLinkWidth =  (pChartData.pointLinkRadius / 3) + xCircleStrokeWidth;
		
		//Define dimensão do circulo interno do delta
		pChartData.dom.deltaCircle.svgAttr("cx", pChartData.center.x);
		pChartData.dom.deltaCircle.svgAttr("cy", pChartData.center.y);
		pChartData.dom.deltaCircle.svgAttr("r", pChartData.pointLinkRadius - pChartData.pointLinkWidth + xCircleStrokeWidth);
	},
	

	pvInitializeDrawChartLine: function(pChartsData, pChartData, pChartValueData){
		var xX = pChartsData.infoWidth;
		var xY = pChartsData.infoHeight;
		xX += pChartValueData.index * pChartsData.scaleX;
		xY += (pChartValueData.value - pChartsData.dom.maxChartValueData.value) * pChartsData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
		
		xY = dbsfaces.math.round(xY, 0);
		xX = dbsfaces.math.round(xX, 0);
		
		//Salva coordenadas
		pChartValueData.x = xX
		pChartValueData.y = xY;
		
		//Posiciona ponto
		pChartValueData.dom.point.svgAttr("cx", xX)
			  				     .svgAttr("cy", xY);
		
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
		var xLabelBoxHeight;
		var xLabelBoxWidth;
		var xValueWidth;
		var xValueHeight;
		var xValueBoxHeight;
		var xValueBoxWidth;
		var xStyleBox = "fill:" + pChartsData.currentColorInverted;
		var xHalf;
		//AJusta Layout Label
		if (pChartsData.showLabel){
			//Dimensões
			xLabelWidth = pChartValueData.dom.infoLabel[0].getBoundingClientRect().width;
			xLabelHeight = pChartsData.infoHeight;
			xLabelBoxHeight = xLabelHeight // pChartValueData.dom.infoLabel.height(); //;
			xLabelBoxWidth = xLabelWidth * 1.10;
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
			pChartValueData.dom.infoLabelBox.svgAttr("width", xLabelBoxWidth)
							       		.svgAttr("height", xLabelBoxHeight)
							       		.svgAttr("x", xX - (xLabelBoxWidth * .05))
							       		.svgAttr("y", "-.2em")
							       		.svgAttr("style", xStyleBox);
		}
		//AJusta Layout Value
		if (pChartsData.showValue){
			xValueWidth = pChartsData.infoWidth;
			xValueHeight = pChartValueData.dom.infoValue.height();
			xValueBoxHeight = xValueHeight;
			xValueBoxWidth = xValueWidth * 0.90;
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
			pChartValueData.dom.infoValueBox.svgAttr("width", xValueBoxWidth)
						   					.svgAttr("height", xValueBoxHeight)
						   					.svgAttr("x", 0)
						   					.svgAttr("y", xY - (xValueBoxHeight / 2))
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
		//Desenha linha guia entre o vale e value
		pChartValueData.dom.infoPath.svgAttr("d", xPath);
	},


	pvInitializeDrawChartPie: function(pChartsData, pChartData, pChartValueData){
		//Calcula o percentual que valore representa sobre o total
		pChartValueData.perc = (Math.abs(pChartValueData.value) / pChartData.totalValue) * 100;
		if (pChartValueData.perc > 99.99){
			pChartValueData.perc = 100;
		}

		//Calcula posição do arco anterior desde que seja do mesmo relationalGroup
		var xArcPercValuePrevious = 0;
		if (pChartValueData.index > 0){
			if (pChartValueData.relationalGroupIndex == pChartData.dom.childrenData[pChartValueData.index - 1].relationalGroupIndex){
				xArcPercValuePrevious = (pChartData.dom.childrenData[pChartValueData.index - 1].totalValue  / pChartData.totalValue) * 100;
			}
		}
		
		//Desenha elemento do arco - Point
		var xArcInfo = dbsfaces.chartsX.pvDrawArc(pChartData, 
												  pChartValueData.dom.point,
												  pChartData.pointRadius, 
												  pChartData.arcWidth,
												  pChartValueData.relationalGroupIndex, 
												  xArcPercValuePrevious, 
												  pChartValueData.perc,
												  false);
		pChartValueData.arcInfo = xArcInfo;

		//Define o ponto interno como ponto do cahrtvalue
		pChartValueData.x = xArcInfo.internalPoint.x;
		pChartValueData.y = xArcInfo.internalPoint.y;

		//Transform-origin 
		dbsfaces.ui.cssAllBrowser(pChartValueData.dom.self, "transform-origin", xArcInfo.externalPoint.x + "px " + xArcInfo.externalPoint.y + "px");

		//Desenha elemento do arco que fazer o link do centro com o chartvalue;
		var xArcLink = dbsfaces.chartsX.pvDrawArc(pChartData, 
 								   				  pChartValueData.dom.pointLink,
 								   				  pChartData.pointLinkRadius + 1, //Adiciona 1 para encobrir espaço entre os elementos
 								   				  pChartData.pointLinkWidth, 
 								   				  pChartValueData.relationalGroupIndex, 
 								   				  xArcPercValuePrevious, 
 								   				  pChartValueData.perc,
 								   				  false);
		//Transform-origin 
		dbsfaces.ui.cssAllBrowser(pChartValueData.dom.pointLink, "transform-origin", xArcLink.externalPoint.x + "px " + xArcLink.externalPoint.y + "px");
		
		//Configura Infos
		var xTransformInfo = "translate(" + xArcInfo.internalPoint.x + " " + xArcInfo.internalPoint.y + ") rotate(" + (xArcInfo.degrees - 90) + ")";
		var xInfoPercX = pChartValueData.dom.infoPerc.attr("x");
		var xInfoLabelX = pChartValueData.dom.infoLabel.attr("x");
		var xInvert = " scale(-1, -1)";
		//Inverte posição das informações a esquerda 
		if (xArcInfo.degrees > 180){
			pChartValueData.dom.infoValues.addClass("-invert");
			pChartValueData.dom.infoPercBox.attr("transform", xInvert);
			xTransformInfo += xInvert;
			xInfoLabelX = "-" + xInfoLabelX;
			xInfoPercX = "-" + xInfoPercX;
		}
		pChartValueData.dom.info.attr("transform", xTransformInfo);
		pChartValueData.dom.infoLabel.attr("x", xInfoLabelX);
		pChartValueData.dom.infoValue.attr("x", xInfoLabelX);
		pChartValueData.dom.infoPerc.attr("x", xInfoPercX)
									.attr("v",pChartValueData.perc);
		
//		var xHeight = pChartValueData.dom.point[0].getBoundingClientRect().height;
		var xHeight = pChartValueData.dom.point[0].getTotalLength() * 0.7;
		if (parseFloat(pChartValueData.dom.infoLabel.css("font-size")) > xHeight){
			pChartValueData.dom.infoLabel.css("font-size", xHeight);
		}
	},



	
	pvInitializeDrawDelta: function(pChartsData, pChartData){
		if (pChartsData.showDelta){
			if (pChartData.type == "line"){
				//Primeiro item do delta
				dbsfaces.chartX.setMovingDeltaHandleData(pChartData, pChartData.dom.leftDeltaHandleData);
				dbsfaces.chartX.selectChartValue(pChartData, pChartData.dom.childrenData[0]);
				dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
				//Último item do delta
				dbsfaces.chartX.setMovingDeltaHandleData(pChartData, pChartData.dom.rightDeltaHandleData);
				dbsfaces.chartX.selectChartValue(pChartData, pChartData.dom.childrenData[pChartData.dom.childrenData.length - 1]);
				dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
				//Calcula centroY
				var xMiddleY = (pChartsData.height + pChartsData.infoHeight) / 2;
				var xMiddleX = (pChartsData.width + pChartsData.infoWidth) / 2;
				//handle a esqueda
				pChartData.dom.leftDeltaHandleData.dom.handle.svgAttr("y", xMiddleY);
				pChartData.dom.leftDeltaHandleData.dom.rect.svgAttr("y", pChartsData.infoHeight);
				pChartData.dom.leftDeltaHandleData.dom.rect.svgAttr("height", pChartsData.height);
				//handle a direita
				pChartData.dom.rightDeltaHandleData.dom.handle.svgAttr("y", xMiddleY);
				pChartData.dom.rightDeltaHandleData.dom.rect.svgAttr("y", pChartsData.infoHeight);
				pChartData.dom.rightDeltaHandleData.dom.rect.svgAttr("height", pChartsData.height);
				//Texto do valor
				pChartData.dom.deltaPerc.svgAttr("y", xMiddleY);
				pChartData.dom.deltaPerc.svgAttr("x", xMiddleX);
			}else if (pChartData.type == "pie"){
				pChartData.dom.deltaInfo.svgAttr("transform", "translate(" + pChartData.center.x + " " + pChartData.center.y + ")");
//				pChartData.dom.deltaInfo.svgAttr("transform-origin", "" + pChartData.center.x + " " + pChartData.center.y + "");
				//Define posição do texto do valor do delta
//				pChartData.dom.deltaInfo.svgAttr("x", pChartData.center.x);
//				pChartData.dom.deltaInfo.svgAttr("y", pChartData.center.y);
//				//Define posição do texto do valor do delta
//				pChartData.dom.deltaValue.svgAttr("x", pChartData.center.x);
//				pChartData.dom.deltaValue.svgAttr("y", pChartData.center.y);
			}
		}
	},
	
	//Desenha link dos relacionamentos
	pvInitializeDrawRelationships: function(pChartsData, pChartData){
		pChartData.relationships.forEach(function(pRelationship){
			//Arco total 
			var xLinkArc = (pChartData.arcFator * (pRelationship.total / pChartData.totalValue)) * 100;
			//Lê lista com o index que compõem o relacionamento do valor informado
			var xKeys = dbsfaces.chartsX.pvGetKeys(pRelationship.key);
			//Analise combinatória entre todaos os index para criar o link entre eles
			for (var xA = 0; xA < xKeys.length - 1; xA++){
				//ChartValue A
				var xChartValueDataA = dbsfaces.chartsX.pvGetChartValueDataFromKey(pChartData, xKeys[xA]);
				for (var xB = xA + 1; xB < xKeys.length; xB++){
					//ChartValue B
					var xChartValueDataB = dbsfaces.chartsX.pvGetChartValueDataFromKey(pChartData, xKeys[xB]);
//					console.log(xChartValueDataA.label + "\t" + xChartValueDataA.perc + "\t" + xChartValueDataB.label + "\t" + xChartValueDataB.perc + "\t" + pRelationship.total + "\t" + pChartData.totalValue);
					//Arco do valor A
					dbsfaces.chartsX.pvInitializeDrawRelationshipsArc(pChartData, xChartValueDataA, xLinkArc, xKeys[xA], xKeys[xB]);
					//Arco do valor B
					dbsfaces.chartsX.pvInitializeDrawRelationshipsArc(pChartData, xChartValueDataB, xLinkArc, xKeys[xB], xKeys[xA]);
					//Largura da linha
//					var xStrokeWidth = dbsfaces.math.round(xLinkArc * (pRelationship.total / pChartData.totalValue),2);
//					if (xStrokeWidth < 0.3){
//						xStrokeWidth = 0.3;
//					}
//					xStrokeWidth = xLinkArc;
					//Linha do link entre chartValue A e B
//					var xPath = "M" + xChartValueDataA.arcInfo.internalPoint.x + "," + xChartValueDataA.arcInfo.internalPoint.y;
//					xPath += "L" + (pChartsData.width / 2) + "," + (pChartsData.height / 2) + "L" + xChartValueDataB.arcInfo.internalPoint.x + "," + xChartValueDataB.arcInfo.internalPoint.y;
//					var xPath = "M" + xChartValueDataA.arcInfo.internalPoint.x + "," + xChartValueDataA.arcInfo.internalPoint.y;
//					xPath += "S" + (pChartsData.width / 2) + "," + (pChartsData.height / 2) + " " + xChartValueDataB.arcInfo.internalPoint.x + "," + xChartValueDataB.arcInfo.internalPoint.y;
//					dbsfaces.svg.path(pChartData.dom.links, xPath, "-link", null, {a:xKeys[xA], b:xKeys[xB], "stroke-width":xStrokeWidth});
				}
			}
		});
	},
	
	//Desenha link dos relacionamentos
	pvInitializeDrawRelationshipsArc: function(pChartData, pChartValueData, pRelationalArcAngle, pKey, pKeyB){
		var xAngleScale;
		//Distância entre os angulos
		xAngleScale = pChartValueData.arcInfo.endAngle - pChartValueData.arcInfo.startAngle;
		xAngleScale -= pRelationalArcAngle;
		xAngleScale /= 2;
		var xA1 = dbsfaces.math.circlePoint(pChartData.center, pChartValueData.arcInfo.internalRadius - 1, pChartValueData.arcInfo.startAngle + xAngleScale);
		var xA2 = dbsfaces.math.circlePoint(pChartData.center, pChartValueData.arcInfo.internalRadius - 1, pChartValueData.arcInfo.startAngle + xAngleScale + pRelationalArcAngle);
	    //Cria Arco
		var xD = "";
		var xPath = null;
		xD = "M" + xA1.x + "," + xA1.y; //Ponto inicial do arco 
		xD += "A" + (pChartValueData.arcInfo.internalRadius - 1) + "," + (pChartValueData.arcInfo.internalRadius - 1) + " 0 " + pChartValueData.arcInfo.big + " " + pChartValueData.arcInfo.direction + " " + dbsfaces.math.round(xA2.x, 2) + "," + dbsfaces.math.round(xA2.y,2); //Arco externo até o ponto final 
		xD += "L" + pChartData.center.x + "," + pChartData.center.y;
		xD += "L" + xA1.x + "," + xA1.y;
		xD += "Z";
		xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-link", null, {key:pKey, b:pKeyB});
		//Exibe ao menos uma linha, mesmo que a distância seja praticamente zero
		if (dbsfaces.math.distanceBetweenTwoPoints(xA1.x, xA1.y, xA2.x, xA2.y) < 0.5){
			xPath.svgAttr("stroke-width", 0.3);
			xPath.svgAttr("stroke", "currentColor");
		}else{
			xPath.svgAttr("stroke-width", "none");
			xPath.svgAttr("stroke", "none");
		}
		//Transform-origin 
		dbsfaces.ui.cssAllBrowser(xPath, "transform-origin", pChartData.center.x + "px " + pChartData.center.y + "px");

		//Arco que para ligar o link ao chartvalue
		xA1 = dbsfaces.math.circlePoint(pChartData.center, pChartValueData.arcInfo.internalRadius - .5, pChartValueData.arcInfo.startAngle + xAngleScale);
		xA2 = dbsfaces.math.circlePoint(pChartData.center, pChartValueData.arcInfo.internalRadius - .5, pChartValueData.arcInfo.startAngle + xAngleScale + pRelationalArcAngle);
		xD = "M" + xA1.x + "," + xA1.y; 
		xD += "A" + (pChartValueData.arcInfo.internalRadius - .5) + "," + (pChartValueData.arcInfo.internalRadius - .5) + " 0 " + pChartValueData.arcInfo.big + " " + pChartValueData.arcInfo.direction + " " + dbsfaces.math.round(xA2.x, 2) + "," + dbsfaces.math.round(xA2.y,2); //Arco externo até o ponto final 
		xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-linkHover", null, {key:pKey, b:pKeyB});
	},

	
	//Retorna lista com o index que compõem o relacionamento do valor informado
	pvGetKeys: function(pKey){
		var xKeys = [];
		for (var xKey = 0; xKey < pKey.length; xKey++){
			if (pKey.substr(pKey.length - xKey - 1, 1) == "1"){
				xKeys.push(xKey);
			}
		}
		return xKeys;
	},

	//Retorna chartvaluedata a partir da chave 
	pvGetChartValueDataFromKey: function(pChartData, pKey){
		for (var xI = 0; xI < pChartData.dom.childrenData.length; xI++){
			if (pChartData.dom.childrenData[xI].key == pKey){
				return pChartData.dom.childrenData[xI];
			}
		}
	},
	
	pvInitializeDrawSetColor: function(pChartsData){
		//Loop por todos os gráficos
		pChartsData.dom.childrenData.forEach(function(pChartData){
			//Defini a cor principal do gráfico
			var xColor = dbsfaces.chartsX.pvSetColor(pChartsData, pChartData, true);
			//Caption
			pChartData.colorTransparent = tinycolor(xColor.toString()).setAlpha(.5).toString();
			pChartData.colorInverted = tinycolor(xColor.toString()).invertLightness().setAlpha(1).toString();
			if (pChartData.dom.caption != null){
				pChartData.dom.captionText.css("color", xColor);
				pChartData.dom.caption.css("background-color", xColor)
									  .css("border-color", pChartData.colorTransparent)
									  .css("color", pChartData.colorInverted);
			}
			//Define colores dos chartvalue
			pChartData.dom.childrenData.forEach(function(pChartValueData, pI){
				var xColor = dbsfaces.chartsX.pvSetColor(pChartsData, pChartValueData, false);
				pChartValueData.colorInverted = tinycolor(xColor).invertLightness().setAlpha(1).toString();
				if (pChartData.type == "pie"){
					//Cor que dos links
					pChartData.dom.self.find("> .-chart > .-links > [key='" + pChartValueData.key + "']").svgAttr("color", xColor);
					//Cor do texto das infos
					pChartValueData.dom.info.attr("fill", pChartValueData.colorInverted);
				}
			});
		});
	},
	
	pvSetColor: function(pChartsData, pElementData, pIsChart){
		var xColor = pElementData.color;
		var xL;
		//Se não foi definida a cor pelo usuário, utiliza a cor corrente
		if (xColor == null){
			//Cor corrente
			xColor = tinycolor(pElementData.dom.self.css("color"));
			//Se for chart ou o chart não possui cor definida pelo usuário, ajusta a cor em degradê conforme a posição do item na sequencia global
			if (pIsChart || pElementData.dom.parent.data("data").color == null){
				xL = (pElementData.globalSequence / pChartsData.globalSequencesCount);
			//Se chart tem cor definida pelo usuário
			}else{
				//Ajusta a cor em degradê conforme a posição do item dentro do próprio chart
				xL = ((pElementData.index) / pElementData.dom.parent.data("data").dom.childrenData.length);
			}
			xL *= 20;
			if (xColor.isDark()){
				xColor.lighten(xL);
			}else{
				xColor.darken(xL);
			}
		}else{
			xColor = tinycolor(xColor);
		}
		pElementData.dom.self.css("color", xColor);
		return xColor;
	},

	//Desenha arco
	pvDrawArc: function(pChartData, pPathElement, pExternalRadius, pWidth, pRelationalGroupIndex, pPercPrevious, pPerc, pAlwaysUp){
		//Retorno
		var xInfo = {
			startAngle: null, //Angulo inicial do arco
			endAngle: null, //Angulo final do arco
			centerAngle: null, //Ángulo do ponto no centro do arco
			degrees: 0, //Graus de rotação. 0 é o 12hrs
			externalRadius: null, //Raio externo
			centerRadius: null, //Raio no centro vertical do arco
			internalRadius: null, //Raio interno
			externalPoint: null, //Ponto no centro externo
			centerPoint: null, //Ponto no centro
			internalPoint: null, //Ponto no centro interno
			direction: 1, //Direção do arco
			big: 0 //Se arco é grande
		}
		
//		xDiametro = pChartData.diameter - 90;
//		var xDiametro = pChartData.diameter;
		var xArcPercValue = pPerc;
		var xArcPercValuePrevious = pPercPrevious;
		if (xArcPercValue > 99.99){
			xArcPercValue = 99.99999; //Artifício para evitar uma volta completa anulando a exibição de conetúdo
		}

		//Angulo inicial e final do arco
		xInfo.startAngle = xArcPercValuePrevious * pChartData.arcFator; //Posição inicial básica
		xInfo.startAngle += pChartData.arcFator * pRelationalGroupIndex * 100; //Posição com o shift em relação ao index do chart
		xInfo.startAngle += pChartData.arcSpace * (pRelationalGroupIndex + 1) * 100; //Espaço entre os chart
		xInfo.startAngle -= (pChartData.arcSpace / 2) * 100; //Centralização do espaço entre os chart
		xInfo.endAngle = xInfo.startAngle + (xArcPercValue * pChartData.arcFator);
		xInfo.centerAngle = xInfo.startAngle + ((xInfo.endAngle - xInfo.startAngle) / 2), //Ángulo do ponto no centro do arco
		xInfo.externalRadius = pExternalRadius;
		xInfo.centerRadius = xInfo.externalRadius - (pWidth / 2);
		xInfo.internalRadius = xInfo.externalRadius - pWidth;
		xInfo.degrees = dbsfaces.math.round(180 * (xInfo.centerAngle / Math.PI), 2);
		xInfo.externalPoint = dbsfaces.math.circlePoint(pChartData.center, xInfo.externalRadius, xInfo.centerAngle);
		xInfo.internalPoint = dbsfaces.math.circlePoint(pChartData.center, xInfo.internalRadius, xInfo.centerAngle);
		xInfo.centerPoint = dbsfaces.math.circlePoint(pChartData.center, xInfo.centerRadius, xInfo.centerAngle);

		//Inverte direção do arco para evitar que 'norte' do arco aponte para baixo.
		if (pAlwaysUp && (xInfo.degrees > 90 && xInfo.degrees < 270)){
			var xTmp = xInfo.endAngle;
			xInfo.endAngle = xInfo.startAngle;
			xInfo.startAngle = xTmp;
			xInfo.direction = 0;
		}

		//Calcula as coordenadas do arco 
		var x1 = dbsfaces.math.circlePoint(pChartData.center, xInfo.centerRadius, xInfo.startAngle);
		var x2 = dbsfaces.math.circlePoint(pChartData.center, xInfo.centerRadius, xInfo.endAngle);

	    if (xInfo.endAngle - xInfo.startAngle > Math.PI) {
	        xInfo.big = 1;
	    }
	    //Cria Arco
		var xPath = "";
		xPath += "M" + x1.x + "," + x1.y; //Ponto inicial do arco 
		xPath += "A" + xInfo.centerRadius + "," + xInfo.centerRadius + " 0 " + xInfo.big + " " + xInfo.direction + " " + x2.x + "," + x2.y; //Arco externo até o ponto final 
		pPathElement.svgAttr("d", xPath);
		//Define a largura do arco
		pPathElement.css("stroke-width", pWidth);
		//Salva stroke-width original para ser utilizado na restauração após alguma mudança do style
		pPathElement.data("sw", parseFloat(pWidth) + 1);

		return xInfo;
	},

	resize: function(pChartsData){
	},

	selectChart: function(pChartsData, pChartId){
		pChartsData.dom.childrenCaptionContainer.children().removeClass("-selected");
		pChartsData.dom.childrenData.forEach(function(pChartData) {
			pChartData.dom.self.removeClass("-selected");
		});
		var xChart = $(dbsfaces.util.jsid(pChartId));
		var xChartCaption = $(dbsfaces.util.jsid(pChartId + "_caption"));
		xChart.addClass("-selected");
		xChartCaption.addClass("-selected");
		dbsfaces.ui.moveToFront(xChart);
	}
	
};

