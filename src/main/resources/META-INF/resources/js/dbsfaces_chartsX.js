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
		var xChartsData = dbsfaces.chartsX.pvInitializeData(pCharts);
		dbsfaces.chartsX.pvInitializeLayout(xChartsData);
		dbsfaces.chartsX.pvInitializeAnalizeValues(xChartsData);
		dbsfaces.chartsX.pvInitializeDraw(xChartsData);
		xChartsData.dom.container.removeClass("-hide");
	},

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
			isPerc: pCharts.attr("perc"), //Se valores são percentuais. usado no cálculo do delta
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
				pChartsData.infoWidth = pChartsData.dom.maxLabelChartValueData.dom.infoLabel[0].getBoundingClientRect().width * 1.10;
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
			if (pChartsData.type == "line"){
				//Desenha delta
				dbsfaces.chartsX.pvInitializeDrawDelta(pChartsData, pChartData);
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

	pvInitializeDrawInitializePie(pChartsData, pChartData){
		//Configura medida do arco de cada relationa group
		//Divide diametro entres os relationalGroups
		pChartData.arcFator = dbsfaces.math.PIDiameterFactor / pChartData.relationalCaptionsCount;
		//Se houver mais de um grupo de label, diminuir espaço entre os gráficos do espáco total do arco
		if (pChartData.relationalCaptionsCount > 1){
			pChartData.arcFator -= pChartData.arcSpace;
		}

		//Diametro do chart. Menor valor entre a altura e a largura
		pChartData.diameter = Math.min(pChartsData.width, pChartsData.height);
		
		//Define posição do texto do valor do delta
		pChartData.dom.deltaValue.attr("x", pChartData.center.x);
		pChartData.dom.deltaValue.attr("y", pChartData.center.y);
		
		//Largura do arco. Utiliza a largura definida no primeiro chartvalue
		pChartsData.infoHeight = 0;
		if (pChartData.relationalCaptions.length > 1){
			//Caminho que receberá o caption do relationalGroup
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

		//Largura do arco do ponto
		pChartData.arcWidth = pChartsData.infoWidth; 
		//Raio do posição do arco do ponto
		pChartData.pointRadius = (pChartData.diameter / 2) - pChartsData.infoHeight;
		//Raio do posição do arco que liga o ponto ao círculo central
		pChartData.pointLinkRadius = pChartData.pointRadius - pChartData.arcWidth;
		//Largura do posição do arco que liga o ponto ao círculo central
		pChartData.pointLinkWidth =  pChartData.pointLinkRadius / 3;
		
		//Define dimensão do circulo interno do delta
		pChartData.dom.deltaCircle.svgAttr("cx", pChartData.center.x);
		pChartData.dom.deltaCircle.svgAttr("cy", pChartData.center.y);
		pChartData.dom.deltaCircle.svgAttr("r", (pChartData.diameter / 2) - pChartsData.infoHeight - pChartsData.infoWidth - 15);
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
		
		//Desenha elemento do arco
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
 								   				  pChartData.pointLinkWidth + 1, //Adiciona 1 para encobrir espaço entre os elementos
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
		pChartValueData.dom.info.attr("fill", tinycolor(pChartValueData).invertLightness().setAlpha(1).toString());
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



	
	pvInitializeDrawDelta(pChartsData, pChartData){
		if (pChartsData.showDelta){
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
			pChartData.dom.deltaValue.svgAttr("y", xMiddleY);
			pChartData.dom.deltaValue.svgAttr("x", xMiddleX);
		}
	},
	
	//Desenha link dos relacionamentos
	pvInitializeDrawRelationships(pChartsData, pChartData){
//		var xLinkArc = dbsfaces.math.round((pChartData.width / pChartData.relationalCaptionsCount) / 10, 0);
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
					console.log(xChartValueDataA.label + "\t" + xChartValueDataA.perc + "\t" + xChartValueDataB.label + "\t" + xChartValueDataB.perc + "\t" + pRelationship.total + "\t" + pChartData.totalValue);
					dbsfaces.chartsX.pvInitializeDrawRelationshipsArc(pChartData, xChartValueDataA, xLinkArc, xKeys[xA], xKeys[xB]);
					dbsfaces.chartsX.pvInitializeDrawRelationshipsArc(pChartData, xChartValueDataB, xLinkArc, xKeys[xB], xKeys[xA]);
					
//					var xAngleScale;
//					xAngleScale = xChartValueDataA.arcInfo.endAngle - xChartValueDataA.arcInfo.startAngle;
//					xAngleScale -= xLinkArc;
//					xAngleScale /= 2;
//					var xA1 = dbsfaces.math.circlePoint(pChartData.center, xChartValueDataA.arcInfo.internalRadius, xChartValueDataA.arcInfo.startAngle + xAngleScale);
//					var xA2 = dbsfaces.math.circlePoint(pChartData.center, xChartValueDataA.arcInfo.internalRadius, xChartValueDataA.arcInfo.startAngle + xAngleScale + xLinkArc);
//					
//					xAngleScale = xChartValueDataB.arcInfo.endAngle - xChartValueDataB.arcInfo.startAngle;
//					xAngleScale -= xLinkArc;
//					xAngleScale /= 2;
//					var xB1 = dbsfaces.math.circlePoint(pChartData.center, xChartValueDataB.arcInfo.internalRadius, xChartValueDataB.arcInfo.startAngle + xAngleScale);
//					var xB2 = dbsfaces.math.circlePoint(pChartData.center, xChartValueDataB.arcInfo.internalRadius, xChartValueDataB.arcInfo.startAngle + xAngleScale + xLinkArc);
//
//				    //Cria Arco
//					var xD = "";
//					var xPath = null;
////					var xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-link", null, {a:xKeys[xA], b:xKeys[xB]});
//					xD = "M" + dbsfaces.math.round(xA1.x,2) + "," + dbsfaces.math.round(xA1.y, 2); //Ponto inicial do arco 
//					xD += "A" + xChartValueDataA.arcInfo.internalRadius + "," + xChartValueDataA.arcInfo.internalRadius + " 0 " + xChartValueDataA.arcInfo.big + " " + xChartValueDataA.arcInfo.direction + " " + dbsfaces.math.round(xA2.x, 2) + "," + dbsfaces.math.round(xA2.y,2); //Arco externo até o ponto final 
//					xD += "L" + pChartData.center.x + "," + pChartData.center.y;
//					xD += "L" + dbsfaces.math.round(xA1.x,2) + "," + dbsfaces.math.round(xA1.y, 2);
//					xD += "Z";
//					xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-link", null, {key:xKeys[xA], b:xKeys[xB]});
//					//Transform-origin 
//					dbsfaces.ui.cssAllBrowser(xPath, "transform-origin", pChartData.center.x + "px " + pChartData.center.y + "px");
//
//					xD = "M" + dbsfaces.math.round(xB1.x,2) + "," + dbsfaces.math.round(xB1.y, 2); //Ponto inicial do arco 
//					xD += "A" + xChartValueDataB.arcInfo.internalRadius + "," + xChartValueDataB.arcInfo.internalRadius + " 0 " + xChartValueDataB.arcInfo.big + " " + xChartValueDataB.arcInfo.direction + " " + dbsfaces.math.round(xB2.x, 2) + "," + dbsfaces.math.round(xB2.y,2); //Arco externo até o ponto final 
//					xD += "L" + pChartData.center.x + "," + pChartData.center.y;
//					xD += "L" + dbsfaces.math.round(xB1.x,2) + "," + dbsfaces.math.round(xB1.y, 2);
//					xD += "Z";
//					xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-link", null, {key:xKeys[xB], b:xKeys[xA]});
//					//Transform-origin 
//					dbsfaces.ui.cssAllBrowser(xPath, "transform-origin", pChartData.center.x + "px " + pChartData.center.y + "px");
					
					//Largura da linha
//					var xStrokeWidth = dbsfaces.math.round(xLinkArc * (pRelationship.total / pChartData.totalValue),2);
//					if (xStrokeWidth < 0.3){
//						xStrokeWidth = 0.3;
//					}
//					xStrokeWidth = xLinkArc;
					//Linha do link entre chartValue A e B
//					var xPath = "M" + xChartValueDataA.arcInfo.internalPoint.x + "," + xChartValueDataA.arcInfo.internalPoint.y;
//					xPath += "S" + (pChartsData.width / 2) + "," + (pChartsData.height / 2) + " " + xChartValueDataB.arcInfo.internalPoint.x + "," + xChartValueDataB.arcInfo.internalPoint.y;
//					var xPath = "M" + xChartValueDataA.arcInfo.internalPoint.x + "," + xChartValueDataA.arcInfo.internalPoint.y;
//					xPath += "L" + (pChartsData.width / 2) + "," + (pChartsData.height / 2) + "L" + xChartValueDataB.arcInfo.internalPoint.x + "," + xChartValueDataB.arcInfo.internalPoint.y;
					//Desenha elementodo arco e salva dados
//					pChartValueData.arcInfo = dbsfaces.chartsX.pvDrawArc(pChartData, 
//																				    pChartValueData.dom.point,
//																				    ((pChartData.diameter / 2) - pChartsData.infoHeight), //Raio externo do arco
//																				    pChartData.arcWidth, //Largura do arco
//																				    pChartValueData.relationalGroupIndex, 
//																				    xArcPercValuePrevious, 
//																				    pChartValueData.perc,
//																				    false);
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
	pvInitializeDrawRelationshipsArc(pChartData, pChartValueData, pRelationalArcAngle, pKey, pKeyB){
		var xAngleScale;
		//Distância entre os angulos
		xAngleScale = pChartValueData.arcInfo.endAngle - pChartValueData.arcInfo.startAngle;
		xAngleScale -= pRelationalArcAngle;
		xAngleScale /= 2;
		var xA1 = dbsfaces.math.circlePoint(pChartData.center, pChartValueData.arcInfo.internalRadius, pChartValueData.arcInfo.startAngle + xAngleScale);
		var xA2 = dbsfaces.math.circlePoint(pChartData.center, pChartValueData.arcInfo.internalRadius, pChartValueData.arcInfo.startAngle + xAngleScale + pRelationalArcAngle);
		
	    //Cria Arco
		var xD = "";
		var xPath = null;
	//	var xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-link", null, {a:xKeys[xA], b:xKeys[xB]});
		xD = "M" + dbsfaces.math.round(xA1.x,2) + "," + dbsfaces.math.round(xA1.y, 2); //Ponto inicial do arco 
		xD += "A" + pChartValueData.arcInfo.internalRadius + "," + pChartValueData.arcInfo.internalRadius + " 0 " + pChartValueData.arcInfo.big + " " + pChartValueData.arcInfo.direction + " " + dbsfaces.math.round(xA2.x, 2) + "," + dbsfaces.math.round(xA2.y,2); //Arco externo até o ponto final 
		xD += "L" + pChartData.center.x + "," + pChartData.center.y;
		xD += "L" + dbsfaces.math.round(xA1.x,2) + "," + dbsfaces.math.round(xA1.y, 2);
		xD += "Z";
		xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-link", null, {key:pKey, b:pKeyB});
		//Transform-origin 
		dbsfaces.ui.cssAllBrowser(xPath, "transform-origin", pChartData.center.x + "px " + pChartData.center.y + "px");
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
				pChartData.dom.self.find("> .-chart > .-links > [key='" + pChartValueData.key + "']").svgAttr("fill", xColor);
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
			xArcPercValue = 99.99; //Artifício para evitar uma volta completa anulando a exibição de conetúdo
		}

		//Angulo inicial e final do arco
		xInfo.startAngle = dbsfaces.math.round(xArcPercValuePrevious * pChartData.arcFator, 4); //Posição inicial básica
		xInfo.startAngle += pChartData.arcFator * pRelationalGroupIndex * 100; //Posição com o shift em relação ao index do chart
		xInfo.startAngle += pChartData.arcSpace * (pRelationalGroupIndex + 1) * 100; //Espaço entre os chart
		xInfo.startAngle -= (pChartData.arcSpace / 2) * 100; //Centralização do espaço entre os chart
		xInfo.endAngle = dbsfaces.math.round(xInfo.startAngle + (xArcPercValue * pChartData.arcFator), 4);
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
		xPath += "M" + dbsfaces.math.round(x1.x,2) + "," + dbsfaces.math.round(x1.y, 2); //Ponto inicial do arco 
		xPath += "A" + xInfo.centerRadius + "," + xInfo.centerRadius + " 0 " + xInfo.big + " " + xInfo.direction + " " + dbsfaces.math.round(x2.x, 2) + "," + dbsfaces.math.round(x2.y,2); //Arco externo até o ponto final 
		pPathElement.svgAttr("d", xPath);
		//Define a largura do arco
		pPathElement.css("stroke-width", pWidth);

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
	},
		
	
	
	pXInitializeDrawChartPie: function(pChartsData, pChartData, pChartValueData){
		//Calcula o percentual que valore representa sobre o total
		pChartValueData.perc = (Math.abs(pChartValueData.value) / pChartData.totalValue) * 100;
		if (pChartValueData.perc > 99.99){
			pChartValueData.perc = 100;
		}

		//Formata percentual
		var xPercInt = parseInt(pChartValueData.perc);
		var xPercDec = String(dbsfaces.math.round(pChartValueData.perc - xPercInt, 2)).substring(1) + "%";
		pChartValueData.dom.infoPercInt.text(xPercInt);
		pChartValueData.dom.infoPercDec.text(xPercDec);


		//Desenha arco
		//Calcula posição do arco anterior desde que seja do mesmo relationalGroup
		var xArcPercValuePrevious = 0;
		if (pChartValueData.index > 0){
			if (pChartValueData.relationalGroupIndex == pChartData.dom.childrenData[pChartValueData.index - 1].relationalGroupIndex){
				xArcPercValuePrevious = (pChartData.dom.childrenData[pChartValueData.index - 1].totalValue  / pChartData.totalValue) * 100;
			}
		}
		//Desenha elementodo arco e salva dados
		pChartValueData.arcInfo = dbsfaces.chartsX.pvDrawArc(pChartData, 
														     pChartValueData.dom.point,
														     ((pChartData.diameter / 2) - pChartsData.infoHeight), //Raio externo do arco
														     pChartData.arcWidth, //Largura do arco
														     pChartValueData.relationalGroupIndex, 
														     xArcPercValuePrevious, 
														     pChartValueData.perc,
														     false);

		//Define o ponto interno como ponto do chartvalue
		pChartValueData.x = pChartValueData.arcInfo.externalPoint.x;
		pChartValueData.y = pChartValueData.arcInfo.externalPoint.y;
	},

	pXInitializeDrawChartPieInfos: function(pChartData){
		var xLeft = [];
		var xRight = [];
		var xCaptionHeightFator = 1.5;

		//Crialista com os chartvalues a esquerda e a direita
		pChartData.dom.childrenData.forEach(function(pChartValueData){
			if (pChartValueData.arcInfo.degrees > 180){
				xLeft.push(pChartValueData);
				pChartValueData.dom.info.addClass("-left");
			}else{
				xRight.push(pChartValueData);
				pChartValueData.dom.info.addClass("-right");
			}
		});
		xLeft.sort(function(a, b){
			return b.index - a.index;
		});

		//Cria lista com os relationaisgroups existentes a esquerda e a direita
		var xLeftRelationalGroups = dbsfaces.chartsX.pXInitializeDrawChartPieInfosRelationalGroupCount(xLeft);
		var xRightRelationalGroups = dbsfaces.chartsX.pXInitializeDrawChartPieInfosRelationalGroupCount(xRight);
		
		
		var xMaxItens = Math.max(xLeft.length + ((xLeftRelationalGroups.length - 1) * xCaptionHeightFator), xRight.length + ((xRightRelationalGroups.length - 1) * xCaptionHeightFator));
		var xValueSpace = pChartData.height / xMaxItens;
		//Posição esquerder
		dbsfaces.chartsX.pXInitializeDrawChartPieInfosPosition(true, pChartData.center.x - (pChartData.diameter/2) + 70, xLeft, xValueSpace, xCaptionHeightFator);
		//Posição direita
		dbsfaces.chartsX.pXInitializeDrawChartPieInfosPosition(false, pChartData.center.x + (pChartData.diameter/2) - 70, xRight, xValueSpace, xCaptionHeightFator);
	},
	

	//Posiciona labels
	pXInitializeDrawChartPieInfosPosition: function(pLeft, pX, pListChartValue, pLineHeight, pCaptionHeightFator){
		var xIndexAnterior = -1;
		var xY = 0;
		var xTransformInfo = null;
		var xPrintCaption = false;
		for (var xI=0; xI < pListChartValue.length;  xI++){
			xPrintCaption = (xIndexAnterior != pListChartValue[xI].relationalGroupIndex);
			xIndexAnterior = pListChartValue[xI].relationalGroupIndex;
			xTransformInfo = "translate(" + pX + " " + xY + ")";
			if (xPrintCaption){
				xY += pLineHeight * pCaptionHeightFator;
				xI--; //Mantém no mesmo item;
			}else{
				pListChartValue[xI].dom.info.attr("transform", xTransformInfo);
				xY += pLineHeight;
			}
		}
	},

	//Conta quandos relationalsgroups existem na lista
	pXInitializeDrawChartPieInfosRelationalGroupCount: function(pListChartValue){
		var xRelationalGroups = [];
		pListChartValue.forEach(function(pChartValueData){
			//Procura se já existe index na lista
			var xRelationalGroupIndex = null;
			for (var xI=0; xI < xRelationalGroups.length; xI++){
				if (xRelationalGroups[xI] == pChartValueData.relationalGroupIndex){
					xRelationalGroupIndex = pChartValueData.relationalGroupIndex;
					break;
				}
			}
			//Inclui se não existir 
			if (xRelationalGroupIndex == null){
				xRelationalGroups.push(pChartValueData.relationalGroupIndex);
			}
		});
		return xRelationalGroups;
	}
	
};

