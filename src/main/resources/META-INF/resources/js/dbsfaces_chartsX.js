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
				maxChartValueData: null //ChartValue que contém maior valor
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
			var xMinChartValueData = null;
			var xMaxChartValueData = null;
			var xMaxCount = 0;
			//Verifica menor e maior valor existentes em todos os gráficos para cálcular a escala
			pChartsData.dom.childrenData.forEach(function(pChartData, pI) {
				if (xMinChartValueData == null || pChartData.dom.minChartValueData.value < xMinChartValueData.value){
					xMinChartValueData = pChartData.dom.minChartValueData;
				}
				if (xMaxChartValueData == null || pChartData.dom.maxChartValueData.value > xMaxChartValueData.value){
					xMaxChartValueData = pChartData.dom.maxChartValueData;
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
			if (pChartsData.showLabel){
//				pChartsData.infoHeight = Math.round(xMaxChartValueData.dom.infoLabel.height() * 1.2, 0);
				pChartsData.infoHeight = Math.round(parseFloat(xMaxChartValueData.dom.infoLabel.css("font-size")) * 1.2, 0);
			}
			if (pChartsData.showValue){
				pChartsData.infoWidth = Math.round(Math.max(xMaxChartValueData.dom.infoValue[0].getBoundingClientRect().width, xMinChartValueData.dom.infoValue[0].getBoundingClientRect().width) * 1.4, 0);
			}
			//Trai espaço dos infos da dimensão disponível para o gráfico principal
			pChartsData.width -= pChartsData.infoWidth;
			pChartsData.height -= pChartsData.infoHeight;
			//Salva chartvalue minimo e máximo
			pChartsData.dom.minChartValueData = xMinChartValueData;
			pChartsData.dom.maxChartValueData = xMaxChartValueData;
			//Força a exibição do value da linha
			xMinChartValueData.dom.self.addClass("-showValue"); 
			xMaxChartValueData.dom.self.addClass("-showValue");
			//Escale para ajustar as coordenadas dentro do espaço do gráfico
			pChartsData.scaleX = pChartsData.width / xMaxCount;
			pChartsData.scaleY = -pChartsData.height / (xMaxChartValueData.value - xMinChartValueData.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior;
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
			pChartData.center.x = pChartData.width / 2;
			pChartData.center.y = pChartData.height / 2;
			
			if (pChartsData.type == "pie"){
				dbsfaces.chartsX.pvInitializeDrawInitializePie(pChartData);
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
				dbsfaces.chartsX.pvInitializeDrawRelationships(pChartData);
			}
		});
		//Configura cor
		dbsfaces.chartsX.pvInitializeDrawSetColor(pChartsData);
	},

	pvInitializeDrawInitializePie(pChartData){
		//Configura medida do arco de cada relationa group
		//Divide diametro entres os relationalGroups
		pChartData.arcFator = dbsfaces.math.PIDiameterFactor / pChartData.relationalCaptionsCount;
		//Se houver mais de um grupo de label, diminuir espaço entre os gráficos do espáco total do arco
		if (pChartData.relationalCaptionsCount > 1){
			pChartData.arcFator -= pChartData.arcSpace;
		}
		//Diametro do chart
		pChartData.diameter = Math.max(pChartData.width, pChartData.height);
		
		//Largura do arco. Utiliza a largura definida no primeiro chartvalue
		pChartData.arcWidth = parseInt(pChartData.dom.childrenData[0].dom.point.css("stroke-width"));
		

		//Caminho que receberá o caption do relationalGroup
		pChartData.relationalCaptions.forEach(function(pRelationalCaption, pI){
			var xPathElementId = pChartData.dom.self[0].id + ":relationalPath_" + pI;
			var xPathElement = $(dbsfaces.util.jsid(xPathElementId));
			//Desenha elemento do arco
			dbsfaces.chartsX.pvInitializeDrawArc(pChartData, 
												 xPathElement, 
												 pI, 
												 0, 
												 100, 
												 true);
		});
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
		if (pChartData.type == "line"
		 || pChartData.type == "bar"){
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
		var xArcInfo = dbsfaces.chartsX.pvInitializeDrawArc(pChartData, 
														    pChartValueData.dom.point, 
														    pChartValueData.relationalGroupIndex, 
														    xArcPercValuePrevious, 
														    pChartValueData.perc,
														    false);

		//Define o ponto interno como ponto do cahrtvalue
		pChartValueData.x = xArcInfo.internalPoint.x;
		pChartValueData.y = xArcInfo.internalPoint.y;

		//Configura Infos
		var xTransformInfo = "translate(" + xArcInfo.externalPoint.x + " " + xArcInfo.externalPoint.y + ") rotate(" + (xArcInfo.degrees - 90) + ")";
		var xInfoPercX = pChartValueData.dom.infoPerc.attr("x");
		var xInfoValueX = pChartValueData.dom.infoValue.attr("x");
		var xInvert = " scale(-1, -1)";
		//Inverte posição das informações a esquerda 
		if (xArcInfo.degrees > 180){
			pChartValueData.dom.infoValues.addClass("-invert");
			pChartValueData.dom.infoPercBox.attr("transform", xInvert);
			xTransformInfo += xInvert;
			xInfoValueX = "-" + xInfoValueX;
			xInfoPercX = "-" + xInfoPercX;
		}
		pChartValueData.dom.info.attr("transform", xTransformInfo);
		pChartValueData.dom.infoLabel.attr("x", xInfoValueX);
		pChartValueData.dom.infoValue.attr("x", xInfoValueX);
		pChartValueData.dom.infoPerc.attr("x", xInfoPercX)
									.attr("v",pChartValueData.perc);
		var xPercInt = parseInt(pChartValueData.perc);
		var xPercDec = String(dbsfaces.math.round(pChartValueData.perc - xPercInt, 2)).substring(1) + "%";
//		String xLabelPerc = DBSFormat.getFormattedNumber(xParcValueInt, 0);
		pChartValueData.dom.infoPercInt.text(xPercInt);
		pChartValueData.dom.infoPercDec.text(xPercDec);
	},


	pvInitializeDrawArc: function(pChartData, pPathElement, pRelationalGroupIndex, pPercPrevious, pPerc, pInvertDirection){
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
			internalPoint: null //Ponto no centro interno
		}
		var xDiametro = pChartData.diameter - 90;
		var xDirection = 1;
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
		xInfo.externalRadius = xDiametro / 3;
		xInfo.centerRadius = xInfo.externalRadius - (pChartData.arcWidth / 2);
		xInfo.internalRadius = xInfo.externalRadius - pChartData.arcWidth;
		xInfo.degrees = dbsfaces.math.round(180 * (xInfo.centerAngle / Math.PI), 2);
		xInfo.externalPoint = dbsfaces.math.circlePoint(pChartData.center, xInfo.externalRadius, xInfo.centerAngle);
		xInfo.internalPoint = dbsfaces.math.circlePoint(pChartData.center, xInfo.internalRadius, xInfo.centerAngle);

		if (pInvertDirection && (xInfo.degrees > 90 && xInfo.degrees < 270)){
			var xTmp = xInfo.endAngle;
			xInfo.endAngle = xInfo.startAngle;
			xInfo.startAngle = xTmp;
			xDirection = 0;
		}

		//Calcula as coordenadas do arco 
		var x1 = dbsfaces.math.circlePoint(pChartData.center, xInfo.centerRadius, xInfo.startAngle);
		var x2 = dbsfaces.math.circlePoint(pChartData.center, xInfo.centerRadius, xInfo.endAngle);

		var xBig = 0;
	    if (xInfo.endAngle - xInfo.startAngle > Math.PI) {
	        xBig = 1;
	    }
	    //Cria Arco
		var xPath = "";
		xPath += "M" + dbsfaces.math.round(x1.x,2) + "," + dbsfaces.math.round(x1.y, 2); //Ponto inicial do arco 
		xPath += "A" + xInfo.centerRadius + "," + xInfo.centerRadius + " 0 " + xBig + " " + xDirection + " " + dbsfaces.math.round(x2.x, 2) + "," + dbsfaces.math.round(x2.y,2); //Arco externo até o ponto final 
		pPathElement.svgAttr("d", xPath);

		return xInfo;
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
	
	pvInitializeDrawRelationships(pChartData){
		var xLinkArc = dbsfaces.math.round((pChartData.width / pChartData.relationalCaptionsCount) / 10, 0);
		pChartData.relationships.forEach(function(pRelationship){
			//Lê lista com o index que compõem o relacionamento do valor informado
			var xKeys = dbsfaces.chartsX.pvGetKeys(pRelationship.key);
			//Analise combinatória entre todaos os index para criar o link entre eles
			for (var xA = 0; xA < xKeys.length - 1; xA++){
				//ChartValue A
				var xChartValueDataA = dbsfaces.chartsX.pvGetChartValueDataFromKey(pChartData, xKeys[xA]);
				for (var xB = xA + 1; xB < xKeys.length; xB++){
					//ChartValue B
					var xChartValueDataB = dbsfaces.chartsX.pvGetChartValueDataFromKey(pChartData, xKeys[xB]);
					//Largura da linha
					var xStrokeWidth = dbsfaces.math.round(xLinkArc * (pRelationship.total / pChartData.totalValue),2);
					if (xStrokeWidth < 0.3){
						xStrokeWidth = 0.3;
					}
					//Linha do link entre chartValue A e B
					var xPath = "M" + xChartValueDataA.x + "," + xChartValueDataA.y;
					xPath += "S" + (pChartData.width / 2) + "," + (pChartData.height / 2) + " " + xChartValueDataB.x + "," + xChartValueDataB.y;
					dbsfaces.svg.path(pChartData.dom.links, xPath, "-link", null, {a:xKeys[xA], b:xKeys[xB], "stroke-width":xStrokeWidth});
				}
			}
		});
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
				if (pChartsData.type == "pie"){
					pChartValueData.dom.infoPerc.css("fill", pChartData.colorInverted);
				}
				dbsfaces.chartsX.pvSetColor(pChartsData, pChartValueData, false);
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

