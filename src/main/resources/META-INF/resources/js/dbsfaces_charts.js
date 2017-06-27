dbs_charts = function(pId) {
	var xCharts = $(pId);
	//Prepara gráfico com a altura e largura
	$(window).resize(function(e){
		dbsfaces.charts.refresh(xCharts);
	});
	
	dbsfaces.charts.initialize(xCharts);
};

dbsfaces.charts = {
	initialize: function(pCharts){
//		dbsfaces.charts.pvInitializeDefs(pCharts);
		var xChartsData = dbsfaces.charts.pvInitializeData(pCharts);
		dbsfaces.charts.pvInitializeLayout(xChartsData);
		dbsfaces.charts.pvInitializeAnalizeValues(xChartsData);
		dbsfaces.charts.pvInitializeDraw(xChartsData);
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
				maxDisplayValueChartValueData : null,//chartValue que contém o maior display value
				maxLabelChartValueData : null,//chartValue que contém o maior label
				chartValuesByIndex: [] //ChartValues agrupados por index
			},
			type : pCharts.attr("type"),
			showLabel : pCharts.hasClass("-showLabel"),
			showValue : pCharts.hasClass("-showValue"),
			showDelta : pCharts.hasClass("-showDelta"),
			width : null, //Largura do espaço que contém o gráfico incluindo sem as colunas e linhas de informação
			height : null, //Altura do espaço que contém o grático incluindo sem as colunas e linhas de informação
			scaleX : null, //Fator de proporção dos ponto do gráfico com os ponto em tela
			scaleY : null, //Fator de proporção dos ponto do gráfico com os ponto em tela
			pointWhiteSpace : 0, //Espaço entre os pontos dos gráficos(bar)
			infoWidth: 0, //Largura da coluna de informação(value)
			infoHeight: 0, //ALtura da linha de informação(label)
			infoFontSize: 0, //Fonte 
			diameter: 0, //diametro do máximo (menor valor entre a altura e largura
			center : {x:0, y:0}, //Centro do gráfico
			arcWidth: 0, //largura do arco principal
			arcSpace: 0.0005, //Espaço entre os arcos dos relationalgroups
			arcFator: null, //Arco de cada relationalGroup(Divide diametro entres os relationalGroups)
			pointRadius: 0, //Raio da posição do arco
			pointLinkRadius: 0, //Raio da posição do arco que liga o arco principal do chartvalue ao centro
			pointLinkWidth: 0, //Largura do arco que liga o arco principal do chartvalue ao centro
			relationalCaptionsCount: 1, //Quantidade de grupos de labels
			globalSequencesCount: 0, //Quantidade total de chartvalues considerando todos os gráficos 
			currentColorInverted: tinycolor(pCharts.css("color")).invertLightness().setAlpha(1).toString(),
			refreshTimeout : null,
		}
		pCharts.data("data", xData);
		xData.dom.caption = xData.dom.container.children(".-caption");
		xData.dom.childrenCaption = xData.dom.container.children(".-childrenCaption");
		xData.dom.charts = xData.dom.container.children(".-charts");
		xData.dom.footer = xData.dom.container.children(".-footer");
		xData.dom.childrenCaptionContainer = xData.dom.childrenCaption.children(".-container");
		xData.dom.childrenData = [];
		//Configura numero do index do chart.
		xData.dom.charts.children(".dbs_chart").each(function(pIndex){
			$(this).data("data").index = pIndex;
			//Adiciona elemento chart na lista de filhos do charts. Artifício para manter a ordem dos elementos independentemente da ordem dentro do com pai.
			xData.dom.childrenData.push($(this).data("data"));
		});
		return xData;
	},


	//Ajusta posição das informações no gráfico pie
	pvInitializeLayout: function(pChartsData){
		dbsfaces.charts.pvInitializeLayoutCreateChildrenCaption(pChartsData);
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
			dbsfaces.charts.selectChart(pChartsData, pChartsData.dom.childrenData[0].dom.self[0].id);
			//Captura seleção de outro gráfico
			pChartsData.dom.childrenCaptionContainer.children().on("mousedown touchstart", function(e){
				dbsfaces.charts.selectChart(pChartsData, $(this).attr("chartid"));
				e.stopImmediatePropagation();
				e.preventDefault();	
			});
		}
	},
	
	pvInitializeAnalizeValues: function(pChartsData){
		pChartsData.dom.minChartValueData = null;
		pChartsData.dom.maxChartValueData = null;
		pChartsData.dom.maxDisplayValueChartValueData = null;
		pChartsData.dom.maxLabelChartValueData = null;
		pChartsData.relationalCaptionsCount = 1;
		//Inicializa com o largura e altura máximo
		if (pChartsData.dom.childrenData.length > 0){
			var xTotalValue = 0;
			var xMaxCount = 0;
			//Verifica menor e maior valor existentes em todos os gráficos para cálcular a escala
			pChartsData.dom.childrenData.forEach(function(pChartData, pI) {
				if ((pChartData.type != "line" && pChartData.dom.childrenData.length > 0)
				 || (pChartData.type == "line" && pChartData.dom.childrenData.length > 1)){
					//Salva menor valor entre todos os valores de todoas os gráficos
					if (pChartsData.dom.minChartValueData == null || pChartData.dom.minChartValueData.value < pChartsData.dom.minChartValueData.value){
						pChartsData.dom.minChartValueData = pChartData.dom.minChartValueData;
					}
					//Salva maior valor entre todos os valores de todoas os gráficos
					if (pChartsData.dom.maxChartValueData == null || pChartData.dom.maxChartValueData.value > pChartsData.dom.maxChartValueData.value){
						pChartsData.dom.maxChartValueData = pChartData.dom.maxChartValueData;
					}
					//Salva maior display value entre todos os valores de todoas os gráficos
					if (pChartsData.dom.maxDisplayValueChartValueData == null || pChartData.dom.maxDisplayValueChartValueData.displayValue.length > pChartsData.dom.maxDisplayValueChartValueData.displayValue.length){
						pChartsData.dom.maxDisplayValueChartValueData = pChartData.dom.maxDisplayValueChartValueData;
					}
					//Salva maior label entre todos os valores de todoas os gráficos
					if (pChartsData.dom.maxLabelChartValueData == null || pChartData.dom.maxLabelChartValueData.label.length > pChartsData.dom.maxLabelChartValueData.label.length){
						pChartsData.dom.maxLabelChartValueData = pChartData.dom.maxLabelChartValueData;
					}
					//Salva maior label entre todos os valores de todoas os gráficos
					if (pChartsData.relationalCaptionsCount == null || pChartData.relationalCaptionsCount > pChartsData.relationalCaptionsCount){
						pChartsData.relationalCaptionsCount = pChartData.relationalCaptionsCount;
					}
					//Quantidade máxima de itens de todos os gráficos
					if (pChartData.originalValues.length > xMaxCount){
						xMaxCount = pChartData.originalValues.length;
					}
					//Utiliza tamanho do primeiro chart para configurar o tamanho padrão das áreas de todos os chart.
					if (pI == 0){
						pChartsData.height = pChartData.dom.chart[0].getBoundingClientRect().height; 
						pChartsData.width = pChartData.dom.chart[0].getBoundingClientRect().width;
						xTotalValue = pChartData.totalValue;
					}
				}
			});
			if (pChartsData.dom.minChartValueData != null
			 && pChartsData.dom.maxChartValueData != null){
				if (pChartsData.type == "line" 
				 || pChartsData.type == "bar"){
					if (pChartsData.showLabel){
						pChartsData.infoHeight = Math.round(parseFloat(pChartsData.dom.maxChartValueData.dom.infoLabel.css("font-size")) * 1.2, 0);
					}
					if (pChartsData.showValue){
						pChartsData.infoWidth = Math.round(pChartsData.dom.maxDisplayValueChartValueData.dom.infoValue[0].getBoundingClientRect().width * 1.2, 0);
					}
					//Trai espaço dos infos da dimensão disponível para o gráfico principal
					pChartsData.width -= pChartsData.infoWidth;
					pChartsData.height -= pChartsData.infoHeight;
					//Força a exibição do value da linha
					pChartsData.dom.minChartValueData.dom.self.addClass("-showValue"); 
					pChartsData.dom.maxChartValueData.dom.self.addClass("-showValue");
					//Escale para ajustar as coordenadas dentro do espaço do gráfico
					if (pChartsData.type == "line"){
						xMaxCount--;
					}
					pChartsData.scaleX = pChartsData.width / xMaxCount;
					if (pChartsData.type == "bar"){
						pChartsData.pointWhiteSpace = pChartsData.scaleX * 0.1;
						pChartsData.scaleX -= pChartsData.pointWhiteSpace;
					}
					pChartsData.scaleY = -pChartsData.height / (pChartsData.dom.maxChartValueData.value - pChartsData.dom.minChartValueData.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior;
				}else if (pChartsData.type == "pie"){
					//Configura medida do arco de cada relationa group
					//Divide diametro entres os relationalGroups
					pChartsData.arcFator = dbsfaces.math.PICircleFactor / pChartsData.relationalCaptionsCount;
					//Se houver mais de um grupo de label, diminuir espaço entre os gráficos do espáco total do arco
					if (pChartsData.relationalCaptionsCount > 1){
						pChartsData.arcFator -= pChartsData.arcSpace;
					}

					
					//Diametro do chart. Menor valor entre a altura e a largura
					pChartsData.diameter = Math.min(pChartsData.width, pChartsData.height);
					
					//Calcula largura do arco dos points.
					pChartsData.infoWidth = 0;
					var xHeightScale = ((pChartsData.diameter / pChartsData.relationalCaptionsCount) / xTotalValue) * 1.3;
					pChartsData.dom.maxDisplayValueChartValueData.dom.infoLabel.css("font-size", "");
					var xMainFontSize = parseFloat(pChartsData.dom.container.css("font-size"));
					//Salva o comprimento do texto mais longo
					var xMaxTextLegth = Math.max(pChartsData.dom.maxDisplayValueChartValueData.dom.infoLabel[0].getComputedTextLength(),
												 pChartsData.dom.maxLabelChartValueData.dom.infoLabel[0].getComputedTextLength());
					//Redutor para equalizar a largura do arco com o diametro, preservando o espaço no centro do círculo.
					var xRedutor = (pChartsData.diameter / 7) / xMaxTextLegth;
					if (xRedutor > 1){
						xRedutor = 1;
					}
					//Verifica qual o point possui maior width
					pChartsData.dom.childrenData.forEach(function(pChartData) {
						pChartData.dom.delta.css("font-size" , xRedutor + "em");
						pChartData.dom.childrenData.forEach(function(pChartValueData) {
							pChartValueData.dom.infoLabel.css("font-size", Math.min(xHeightScale * pChartValueData.value * xRedutor, xMainFontSize * xRedutor));
							var xWidth = pChartValueData.dom.infoLabel[0].getComputedTextLength() * 1.1;
							if (xWidth > pChartsData.infoWidth){
								pChartsData.infoWidth = xWidth;
							}
						});
					});
					pChartsData.infoHeight = 0; //Será configurado posteriomente como a altura do caption do relationalgroup(se houver);
				}
			}
		}else{
			
			pChartsData.height = pChartsData.dom.charts[0].getBoundingClientRect().height;
			pChartsData.width = pChartsData.dom.charts[0].getBoundingClientRect().width;
		}
	},
	
	pvInitializeDraw: function(pChartsData){
		//Loop em todos os gráficos
		pChartsData.globalSequencesCount = 0;
		pChartsData.dom.childrenData.forEach(function(pChartData){
			if ((pChartsData.type != "line" && pChartData.dom.childrenData.length > 0)
			 || (pChartsData.type == "line" && pChartData.dom.childrenData.length > 1)){
				//Configura qual o sequence
				pChartData.globalSequence = pChartsData.globalSequencesCount + 1;
				//Centro do gráfico
				pChartsData.center.x = pChartsData.width / 2;
				pChartsData.center.y = pChartsData.height / 2;
				
				if (pChartsData.type == "pie"){
					dbsfaces.charts.pvInitializeDrawInitializePie(pChartsData, pChartData);
				}
	
				//DrawPoints
				//Loop em todos os pontos gráfico
				pChartData.dom.childrenData.forEach(function(pChartValueData, pI){
					if (pChartsData.type == "line"){
						dbsfaces.charts.pvInitializeDrawChartLine(pChartsData, pChartData, pChartValueData);
					}else if (pChartsData.type == "pie"){
						dbsfaces.charts.pvInitializeDrawChartPie(pChartsData, pChartData, pChartValueData);
					}else if (pChartsData.type == "bar"){
						dbsfaces.charts.pvInitializeDrawChartBar(pChartsData, pChartData, pChartValueData);
					}
					//Soma um item a quantidade global de chartvalues
					pChartsData.globalSequencesCount++;
					//Configura o sequence global deste chartvalue
					pChartValueData.globalSequence = pChartsData.globalSequencesCount;
				});
				
				
				//Configura posição inicial dos controles do delta 
				dbsfaces.charts.pvInitializeDrawDelta(pChartsData, pChartData);
				if (pChartsData.type == "line"){
					//empty
				}else if (pChartsData.type == "pie"){
					//Desenha relacionamentos
					dbsfaces.charts.pvInitializeDrawRelationships(pChartsData, pChartData);
					//Desenha infos
	//				dbsfaces.charts.pXInitializeDrawChartPieInfos(pChartData);
				}else if (pChartsData.type == "box"){
				}
			}
		});
		//Configura cor
		dbsfaces.charts.pvInitializeDrawSetColor(pChartsData);
	},

	pvInitializeDrawInitializePie: function(pChartsData, pChartData){
		//Largura do arco. Utiliza a largura definida no primeiro chartvalue
		pChartsData.infoHeight = 0;
		//Caminho que receberá o caption do relationalGroup
		if (pChartData.relationalCaptions.length > 1){
			pChartData.relationalCaptions.forEach(function(pRelationalCaption, pI){
				var xPathElementId = pChartData.dom.self[0].id + ":relationalPath_" + pI;
				var xPathElement = $(dbsfaces.util.jsid(xPathElementId));
				pChartsData.infoHeight = parseInt(xPathElement.css("font-size")) / 1.5; 
				//Desenha elemento do arco
				dbsfaces.charts.pvDrawArc(pChartsData,
										  pChartData,
										  xPathElement, 
										  (pChartsData.diameter / 2) - (pChartsData.infoHeight / 3), //Raio externo do arco 
										  0, //Largura da arco
										  pI, 
										  0, 
										  100, 
										  true);
			});
		}

		var xCircleStrokeWidth = parseFloat(pChartData.dom.deltaCircle.css("stroke-width")) + 1;
		//Largura do arco do ponto
		pChartsData.arcWidth = pChartsData.infoWidth; 
		//Raio do posição do arco do ponto
		pChartsData.pointRadius = (pChartsData.diameter / 2) - pChartsData.infoHeight;
		//Raio do posição do arco que liga o ponto ao círculo central
		pChartsData.pointLinkRadius = pChartsData.pointRadius - pChartsData.arcWidth;
		//Largura do posição do arco que liga o ponto ao círculo central
		pChartsData.pointLinkWidth =  (pChartsData.pointLinkRadius / 3) + xCircleStrokeWidth;
		
		//Define dimensão do circulo interno do delta
		pChartData.dom.deltaCircle.svgAttr("cx", pChartsData.center.x);
		pChartData.dom.deltaCircle.svgAttr("cy", pChartsData.center.y);
		pChartData.dom.deltaCircle.svgAttr("r", Math.max(pChartsData.pointLinkRadius - pChartsData.pointLinkWidth + xCircleStrokeWidth, 0.1));
//		pChartData.dom.deltaInfo.css("font-size", pChartsData.dom.container.css("font-size"));
		dbsfaces.chart.pvShowDeltaValue(pChartData, null, null);
		dbsfaces.ui.recreate(pChartData.dom.delta); //Artifício para corrigir problema no chrome onde o alinhamento do texto não esta funcionando na criação
	},
	
	pvInitializeDrawChartBarGroupByIndex: function(pChartsData, pChartValueData){
		if (pChartValueData.index > pChartsData.dom.chartValuesByIndex.length - 1){
			pChartsData.dom.chartValuesByIndex.push([]);
		}
		pChartsData.dom.chartValuesByIndex[pChartValueData.index].push(pChartValueData);
	},
	
	pvInitializeDrawChartBar: function(pChartsData, pChartData, pChartValueData){
		dbsfaces.charts.pvInitializeDrawChartBarGroupByIndex(pChartsData, pChartValueData);

		var xX;
		var xY;
		var xZero;
		var xLenght;
		var xPath;
		var xXIndex;

//		if (true){
//			xX = pChartsData.infoHeight;
//			xY = pChartsData.infoWidth;
//			xZero = -pChartsData.dom.maxChartValueData.value * pChartsData.scaleX + xX;
//			xX += (pChartValueData.index * pChartsData.scaleX) + (pChartsData.scaleX / 2);
//			xY += (pChartValueData.value - pChartsData.dom.maxChartValueData.value) * pChartsData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
//		}else{
			xX = pChartsData.infoWidth + (pChartsData.pointWhiteSpace * pChartValueData.index);
			xY = pChartsData.infoHeight;
			xZero = -pChartsData.dom.maxChartValueData.value * pChartsData.scaleY + xY;
			xX += (pChartsData.scaleX * pChartValueData.index) + (pChartsData.scaleX / 2);
			xY += (pChartValueData.value - pChartsData.dom.maxChartValueData.value) * pChartsData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
			xXIndex = xX - (pChartsData.scaleX / pChartsData.dom.childrenData.length) * pChartData.index;
			xXIndex += (pChartsData.scaleX / pChartsData.dom.childrenData.length) / 2;
//		}

		
		xY = dbsfaces.math.round(xY, 0);
		xX = dbsfaces.math.round(xX, 0);
		
		if (xY > 0){
			xLength = xY;
		}else if(xY < 0){
			xLength = -xY;
		}else{ //Exibe, ao menos, uma linha.
			xLength = 1;
			xZero = -.5;
		}
		
		//Salva coordenadas
		pChartValueData.x = xXIndex;
		pChartValueData.y = xY;

		xPath = "M" + xXIndex + "," + xZero;
		xPath += "L" + xXIndex + "," + xLength;
		
		//Posiciona ponto
		pChartValueData.dom.point.svgAttr("d", xPath)
								 .svgAttr("stroke-width", pChartsData.scaleX / pChartsData.dom.childrenData.length);
		
		
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
		//Configura linha guia entre o label e o value
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

	pvInitializeDrawChartLine: function(pChartsData, pChartData, pChartValueData){
		var xX = pChartsData.infoWidth;
		var xY = pChartsData.infoHeight;
		var xPath;
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
			pChartValueData.dom.infoValue.svgAttr("x", xValueBoxWidth - 4)
			      						 .svgAttr("y", xY);
			pChartValueData.dom.infoValueBox.svgAttr("width", xValueBoxWidth)
						   					.svgAttr("height", xValueBoxHeight)
						   					.svgAttr("x", 0)
						   					.svgAttr("y", xY - (xValueBoxHeight / 2))
						   					.svgAttr("style", xStyleBox);
		}
		//Configura linha guia entre o label e o value
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
		var xArcInfo = dbsfaces.charts.pvDrawArc(pChartsData,
												 pChartData, 
												 pChartValueData.dom.point,
												 pChartsData.pointRadius, 
												 pChartsData.arcWidth,
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
		var xArcLink = dbsfaces.charts.pvDrawArc(pChartsData,
												 pChartData, 
 								   				 pChartValueData.dom.pointLink,
 								   				 pChartsData.pointLinkRadius + 1, //Adiciona 1 para encobrir espaço entre os elementos
 								   				 pChartsData.pointLinkWidth, 
 								   				 pChartValueData.relationalGroupIndex, 
 								   				 xArcPercValuePrevious, 
 								   				 pChartValueData.perc,
 								   				 false);
		//Transform-origin 
		dbsfaces.ui.cssAllBrowser(pChartValueData.dom.pointLink, "transform-origin", xArcLink.externalPoint.x + "px " + xArcLink.externalPoint.y + "px");
		
		//Configura Infos
		var xTransformInfo = "translate(" + xArcInfo.internalPoint.x + " " + xArcInfo.internalPoint.y + ") rotate(" + (xArcInfo.degrees - 90) + ")";
		var xInfoPercX = parseFloat(pChartValueData.dom.infoPerc.attr("x"));
		var xInfoLabelX = parseFloat(pChartValueData.dom.infoLabel.attr("x"));
		var xInvert = " scale(-1, -1)";
		//Inverte posição das informações a esquerda 
		if (xArcInfo.degrees > 180){
			pChartValueData.dom.infoValues.addClass("-invert");
			pChartValueData.dom.infoPercBox.attr("transform", xInvert);
			xTransformInfo += xInvert;
			xInfoLabelX = "-" + Math.abs(xInfoLabelX);
			xInfoPercX = "-" + Math.abs(xInfoPercX);
		}
		pChartValueData.dom.info.attr("transform", xTransformInfo);
		pChartValueData.dom.infoLabel.attr("x", xInfoLabelX);
		pChartValueData.dom.infoValue.attr("x", xInfoLabelX);
		pChartValueData.dom.infoPerc.attr("x", xInfoPercX)
									.attr("v",pChartValueData.perc);
		
		
		
//		pChartValueData.dom.infoLabel.css("font-size", pChartsData.infoFontSize);
//		var xHeight = pChartValueData.dom.point[0].getBoundingClientRect().height;
		//Reduz fonte se altura do texto for maior que altura do ponto
		//Reduz fonte se largura do texto for maior que largura do ponto
//		if (pChartValueData.dom.infoLabel[0].getComputedTextLength() > (pChartsData.infoWidth * .95)){
//			pChartValueData.dom.infoLabel.css("font-size", pChartsData.infoWidth / (pChartValueData.label.length * 0.6));
//			return false;
//		}
//		var xHeight = pChartValueData.dom.point[0].getTotalLength() * 0.5;
//		if (parseFloat(pChartValueData.dom.infoLabel.css("font-size")) > xHeight){
//			pChartValueData.dom.infoLabel.css("font-size", xHeight);
//			return false;
//		}
//		pChartValueData.dom.infoLabel.textfill({
//	        maxFontPixels: 36
//	    });
//		pChartValueData.dom.infoPerc.fontSizeFit(pChartValueData.dom.info);
//		pChartValueData.dom.infoLabel.fontSizeFit(pChartValueData.dom.info);

		return true;
	},



	
	pvInitializeDrawDelta: function(pChartsData, pChartData){
		if (pChartData.type == "line"){
			if (pChartsData.showDelta){
				//Primeiro item do delta
				dbsfaces.chart.setMovingDeltaHandleData(pChartData, pChartData.dom.leftDeltaHandleData);
				dbsfaces.chart.selectChartValue(pChartData, pChartData.dom.childrenData[0]);
				dbsfaces.chart.setMovingDeltaHandleData(pChartData, null);
				//Último item do delta
				dbsfaces.chart.setMovingDeltaHandleData(pChartData, pChartData.dom.rightDeltaHandleData);
				dbsfaces.chart.selectChartValue(pChartData, pChartData.dom.childrenData[pChartData.dom.childrenData.length - 1]);
				dbsfaces.chart.setMovingDeltaHandleData(pChartData, null);
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
			}
		}else if(pChartData.type == "pie"){
			pChartData.dom.deltaInfo.svgAttr("transform", "translate(" + pChartsData.center.x + " " + pChartsData.center.y + ")");
		}
	},
	
	
	//Desenha link dos relacionamentos
	pvInitializeDrawRelationships: function(pChartsData, pChartData){
		pChartData.dom.links.empty();
		pChartData.relationships.forEach(function(pRelationship){
			//Arco total 
			var xLinkPerc = (pRelationship.total / pChartData.totalValue) * 100;
			var xLinkArc = pChartsData.arcFator * xLinkPerc;
			//Lê lista com o index que compõem o relacionamento do valor informado
			var xKeys = dbsfaces.charts.pvGetKeys(pRelationship.key);
			//Analise combinatória entre todaos os index para criar o link entre eles
			for (var xA = 0; xA < xKeys.length - 1; xA++){
				//ChartValue A
				var xChartValueDataA = dbsfaces.charts.pvGetChartValueDataFromKey(pChartData, xKeys[xA]);
				for (var xB = xA + 1; xB < xKeys.length; xB++){
					//ChartValue B
					var xChartValueDataB = dbsfaces.charts.pvGetChartValueDataFromKey(pChartData, xKeys[xB]);
					//Arco do valor A
					dbsfaces.charts.pvInitializeDrawRelationshipsArc(pChartsData, pChartData, xChartValueDataA, xLinkArc, xKeys[xA], xKeys[xB], pRelationship.total, xLinkPerc);
					//Arco do valor B
					dbsfaces.charts.pvInitializeDrawRelationshipsArc(pChartsData, pChartData, xChartValueDataB, xLinkArc, xKeys[xB], xKeys[xA], pRelationship.total, xLinkPerc);
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
	pvInitializeDrawRelationshipsArc: function(pChartsData, pChartData, pChartValueData, pRelationalArcAngle, pKey, pKeyB, pValue, pPerc){
		var xAngleScale;
		//Distância entre os angulos
		xAngleScale = pChartValueData.arcInfo.endAngle - pChartValueData.arcInfo.startAngle;
		xAngleScale -= pRelationalArcAngle;
		xAngleScale /= 2;
		var xA1 = dbsfaces.math.circlePoint(pChartsData.center, pChartValueData.arcInfo.internalRadius - 1, pChartValueData.arcInfo.startAngle + xAngleScale);
		var xA2 = dbsfaces.math.circlePoint(pChartsData.center, pChartValueData.arcInfo.internalRadius - 1, pChartValueData.arcInfo.startAngle + xAngleScale + pRelationalArcAngle);
	    //Cria Arco
		var xD = "";
		var xPath = null;
		xD = "M" + xA1.x + "," + xA1.y; //Ponto inicial do arco 
		xD += "A" + (pChartValueData.arcInfo.internalRadius - 1) + "," + (pChartValueData.arcInfo.internalRadius - 1) + " 0 " + pChartValueData.arcInfo.big + " " + pChartValueData.arcInfo.direction + " " + dbsfaces.math.round(xA2.x, 2) + "," + dbsfaces.math.round(xA2.y,2); //Arco externo até o ponto final 
		xD += "L" + pChartsData.center.x + "," + pChartsData.center.y;
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
		dbsfaces.ui.cssAllBrowser(xPath, "transform-origin", pChartsData.center.x + "px " + pChartsData.center.y + "px");

		//Arco que para ligar o link ao chartvalue(proporcional)
		xA1 = dbsfaces.math.circlePoint(pChartsData.center, pChartValueData.arcInfo.internalRadius - .5, pChartValueData.arcInfo.startAngle + xAngleScale);
		xA2 = dbsfaces.math.circlePoint(pChartsData.center, pChartValueData.arcInfo.internalRadius - .5, pChartValueData.arcInfo.startAngle + xAngleScale + pRelationalArcAngle);
		xD = "M" + xA1.x + "," + xA1.y; 
		xD += "A" + (pChartValueData.arcInfo.internalRadius - .5) + "," + (pChartValueData.arcInfo.internalRadius - .5) + " 0 " + pChartValueData.arcInfo.big + " " + pChartValueData.arcInfo.direction + " " + dbsfaces.math.round(xA2.x, 2) + "," + dbsfaces.math.round(xA2.y,2); //Arco externo até o ponto final 
		xPath = dbsfaces.svg.path(pChartData.dom.links, xD, "-linkHover", null, {key:pKey, b:pKeyB});
		//Salva valores proporcionais do relacionamento para serem exibidos quando hoverlink for selecionado
		xPath.data("data", {value:pValue, perc:pPerc});
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
			if ((pChartsData.type != "line" && pChartData.dom.childrenData.length > 0)
			 || (pChartsData.type == "line" && pChartData.dom.childrenData.length > 1)){
				//Defini a cor principal do gráfico
				var xColor = dbsfaces.charts.pvSetColor(pChartsData, pChartData, true);
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
					var xColor = dbsfaces.charts.pvSetColor(pChartsData, pChartValueData, false);
					pChartValueData.colorInverted = tinycolor(xColor).invertLightness().setAlpha(1).toString();
					if (pChartData.type == "pie"){
						//Cor que dos links
						pChartData.dom.self.find("> .-chart > .-links > [key='" + pChartValueData.key + "']").svgAttr("color", xColor);
						//Cor do texto das infos
						pChartValueData.dom.info.attr("fill", pChartValueData.colorInverted);
					}
				});
			}
		});
	},
	
	pvSetColor: function(pChartsData, pElementData, pIsChart){
		var xColor = pElementData.color;
		var xL;
		//Se não foi definida a cor pelo usuário, utiliza a cor corrente
		if (xColor == null){
			//Remove cor configurada anteriormente. 
			pElementData.dom.self.css("color", "");
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
	pvDrawArc: function(pChartsData, pChartData, pPathElement, pExternalRadius, pWidth, pRelationalGroupIndex, pPercPrevious, pPerc, pAlwaysUp){
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
		
//		xDiametro = pChartsData.diameter - 90;
//		var xDiametro = pChartsData.diameter;
		var xArcPercValue = pPerc;
		var xArcPercValuePrevious = pPercPrevious;
		if (xArcPercValue > 99.99){
			xArcPercValue = 99.99999; //Artifício para evitar uma volta completa anulando a exibição de conetúdo
		}

		//Angulo inicial e final do arco
		xInfo.startAngle = xArcPercValuePrevious * pChartsData.arcFator; //Posição inicial básica
		xInfo.startAngle += pChartsData.arcFator * pRelationalGroupIndex * 100; //Posição com o shift em relação ao index do chart
		xInfo.startAngle += pChartsData.arcSpace * (pRelationalGroupIndex + 1) * 100; //Espaço entre os chart
		xInfo.startAngle -= (pChartsData.arcSpace / 2) * 100; //Centralização do espaço entre os chart
		xInfo.endAngle = xInfo.startAngle + (xArcPercValue * pChartsData.arcFator);
		xInfo.centerAngle = xInfo.startAngle + ((xInfo.endAngle - xInfo.startAngle) / 2), //Ángulo do ponto no centro do arco
		xInfo.externalRadius = pExternalRadius;
		xInfo.centerRadius = xInfo.externalRadius - (pWidth / 2);
		xInfo.internalRadius = xInfo.externalRadius - pWidth;
		xInfo.degrees = dbsfaces.math.round(180 * (xInfo.centerAngle / Math.PI), 2);
		xInfo.externalPoint = dbsfaces.math.circlePoint(pChartsData.center, xInfo.externalRadius, xInfo.centerAngle);
		xInfo.internalPoint = dbsfaces.math.circlePoint(pChartsData.center, xInfo.internalRadius, xInfo.centerAngle);
		xInfo.centerPoint = dbsfaces.math.circlePoint(pChartsData.center, xInfo.centerRadius, xInfo.centerAngle);

		//Inverte direção do arco para evitar que 'norte' do arco aponte para baixo.
		if (pAlwaysUp && (xInfo.degrees > 90 && xInfo.degrees < 270)){
			var xTmp = xInfo.endAngle;
			xInfo.endAngle = xInfo.startAngle;
			xInfo.startAngle = xTmp;
			xInfo.direction = 0;
		}

		//Calcula as coordenadas do arco 
		var x1 = dbsfaces.math.circlePoint(pChartsData.center, xInfo.centerRadius, xInfo.startAngle);
		var x2 = dbsfaces.math.circlePoint(pChartsData.center, xInfo.centerRadius, xInfo.endAngle);

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

	refresh: function(pCharts){
		if (pCharts == null || typeof pCharts == "undefined" || pCharts.length == 0){return;}

		var xChartsData = pCharts.data("data");
//		clearTimeout(xChartsData.refreshTimeout);
//		xChartsData.refreshTimeout = setTimeout(function(){
			xChartsData.dom.container.addClass("-hide");
			xChartsData.dom.childrenData.forEach(function(pChartData) {
				dbsfaces.chart.refresh(pChartData.dom.self);
			});
			dbsfaces.charts.pvInitializeAnalizeValues(xChartsData);
			dbsfaces.charts.pvInitializeDraw(xChartsData);
			xChartsData.dom.container.removeClass("-hide");
//		},2);
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

