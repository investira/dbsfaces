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
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do -charts. Possível bug do safari quando display = flex.
//		setTimeout(function(e){
			pCharts.removeClass("-hide");
//			xChartsData.dom.charts.addClass("-hide");
//		},0);
		//setTimeout utilizado como artifício para resolver problema do Safari no controle da altura do -charts. Possível bug do safari quando display = flex.
//		setTimeout(function(e){
			dbsfaces.chartsX.pvInitializeAnalizeValues(xChartsData);
			dbsfaces.chartsX.pvInitializeDraw(xChartsData);
//			xChartsData.dom.charts.removeClass("-hide");
//		},0);
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
				if (xMinChartValueData == null || pChartData.dom.minChartValueData.value.value < xMinChartValueData.value.value){
					xMinChartValueData = pChartData.dom.minChartValueData;
				}
				if (xMaxChartValueData == null || pChartData.dom.maxChartValueData.value.value > xMaxChartValueData.value.value){
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
			pChartsData.scaleY = -pChartsData.height / (xMaxChartValueData.value.value - xMinChartValueData.value.value); //Scale vertical. obs:invertida já que a coordenada do svg desce quando o valor é maior;
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
			//DrawPoints
			//Loop em todos os pontos gráfico
			pChartData.dom.childrenData.forEach(function(pChartValueData, pI){
				if (pChartsData.type == "line"){
					dbsfaces.chartsX.pvInitializeDrawChartLine(pChartsData, pChartData, pChartValueData);
					//Primeiro item do delta
					if (pI == 0){
						dbsfaces.chartX.setMovingDeltaHandleData(pChartData, pChartData.dom.leftDeltaHandleData);
						dbsfaces.chartX.selectChartValue(pChartData, pChartValueData);
						dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
					//Último item do delta
					}else if (pI == pChartData.dom.childrenData.length - 1){
						dbsfaces.chartX.setMovingDeltaHandleData(pChartData, pChartData.dom.rightDeltaHandleData);
						dbsfaces.chartX.selectChartValue(pChartData, pChartValueData);
						dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
					}
				}else if (pChartsData.type == "pie"){
					dbsfaces.chartsX.pvInitializeDrawChartPie(pChartsData, pChartData, pChartValueData);
				}
				//Soma um item a quantidade total de chartvalues
				pChartsData.globalSequencesCount++;
				//Configura qual o sequence
				pChartValueData.globalSequence = pChartsData.globalSequencesCount;
			});
			//Configura posição inicial dos controles do delta 
			if (pChartsData.type == "line"){
				if (pChartsData.showDelta){
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
			}
		});
		//Configura cor
		dbsfaces.chartsX.pvInitializeDrawSetColor(pChartsData);
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
				pChartValueData.dom.infoPerc.css("fill", pChartData.colorInverted);
				dbsfaces.chartsX.pvSetColor(pChartsData, pChartValueData, false);
			});
		});
	},
	
	pvSetColor: function(pChartsData, pElementData, pIsChart){
		var xColor = pElementData.color;
		var xPerc;
		//Se não foi definida a cor pelo usuário, utiliza a cor corrente
		if (xColor == null){
			//Cor corrente
			xColor = tinycolor(pElementData.dom.self.css("color"));
			//Se for chart ou o chart não possui cor definida pelo usuário, ajusta a cor em degradê conforme a posição do item na sequencia global
			if (pIsChart || pElementData.dom.parent.data("data").color == null){
				xPerc = (pElementData.globalSequence / pChartsData.globalSequencesCount);
			//Se chart tem cor definida pelo usuário
			}else{
				//Ajusta a cor em degradê conforme a posição do item dentro do próprio chart
				xPerc = ((pElementData.index) / pElementData.dom.parent.data("data").dom.childrenData.length);
			}
			xPerc *= 20;
			if (xColor.isDark()){
				xColor.lighten(xPerc);
			}else{
				xColor.darken(xPerc);
			}
		}else{
			xColor = tinycolor(xColor);
		}
		pElementData.dom.self.css("color", xColor);
		return xColor;
	},

	pvInitializeDrawChartPie: function(pChartsData, pChartData, pChartValueData){
		var xChartArcSpace = 0.0002;
		var xChartArcFator = dbsfaces.math.PIDiameterFactor / pChartsData.dom.childrenData.length;
		var xChartArcWidth = 10;
		var xCentro = {
			x: pChartData.width / 2,
			y: pChartData.height / 2
		};
		
		var xDiametro = Math.max(pChartData.width, pChartData.height);
		//Calcula o percentual que valore representa sobre o total
		pChartValueData.perc = (Math.abs(pChartValueData.value.value) / pChartData.totalValue) * 100;

		var xArcPercValue = pChartValueData.perc;
		var xArcPercValuePrevious = 0;
		if (pChartValueData.perc > 99.99){
			pChartValueData.perc = 100;
			xArcPercValue = 99.99; //Artifício para evitar uma volta completa anulando a exibição de conetúdo
		}
		if (pChartsData.dom.childrenData.length > 1){
			xChartArcFator -= xChartArcSpace;
		}
		if (pChartValueData.index > 0){
			xArcPercValuePrevious = (pChartData.dom.childrenData[pChartValueData.index - 1].totalValue  / pChartData.totalValue) * 100;
		}
		//Angulo inicial e final do arco
		var xStartAngle = dbsfaces.math.round(xArcPercValuePrevious * xChartArcFator, 4); //Posição inicial básica
		xStartAngle += xChartArcFator * pChartData.index * 100; //Posição com o shift em relação ao index do chart
		xStartAngle += xChartArcSpace * (pChartData.index + 1) * 100; //Espaço entre os chart
		xStartAngle -= (xChartArcSpace / 2) * 100; //Centralização do espáco entre os chart
		var xEndAngle = dbsfaces.math.round(xStartAngle + (xArcPercValue * xChartArcFator), 4);

		//Ángulo do ponto no centro do arco para servir de referencia para o label
		var xPointAngle = xStartAngle + ((xEndAngle - xStartAngle) / 2);
		//Graus do ponto no centro do arco para servir de referencia para o label
		var xGraus = dbsfaces.math.round((180 * (xPointAngle/Math.PI)) - 90,2);
//		console.log(xPointAngle xChartArcFator  + "\t" + pChartData.index  + "\t" +  xStartAngle + "\t" + xEndAngle);
		//Metade da largura, pois o stroke terá a largura integral.
//		xPneuRaioExterno = pChart.getPieChartRelativeRadius(pCharts) + pCharts.getPieChartWidth();
//		xPneuRaioCentro = xPneuRaioExterno - (pCharts.getPieChartWidth() / 2);
		var xPneuRaioExterno = xDiametro / 3;
		var xPneuRaioCentro = xPneuRaioExterno - (xChartArcWidth / 2);

		//Ponto no centro e na tangente do arco para servir de referencia para o label
		var xPointAnchor = dbsfaces.math.circlePoint(xCentro, xPneuRaioExterno + (xChartArcWidth / 2), xPointAngle);

		//Calcula as coordenadas do arco 
		var x1 = dbsfaces.math.circlePoint(xCentro, xPneuRaioCentro, xStartAngle);
		var x2 = dbsfaces.math.circlePoint(xCentro, xPneuRaioCentro, xEndAngle);

		var xBig = 0;
	    if (xEndAngle - xStartAngle > Math.PI) {
	        xBig = 1;
	    }
	    //Cria Arco
		var xPath = "";
		xPath += "M" + dbsfaces.math.round(x1.x,2) + "," + dbsfaces.math.round(x1.y, 2); //Ponto inicial do arco 
		xPath += "A" + xPneuRaioCentro + "," + xPneuRaioCentro + " 0 " + xBig + " 1 " + dbsfaces.math.round(x2.x, 2) + "," + dbsfaces.math.round(x2.y,2); //Arco externo até o ponto final 
		pChartValueData.dom.point.attr("d", xPath); 

		//Configura Infos
		var xTransformInfo = "translate(" + xPointAnchor.x + " " + xPointAnchor.y + ") rotate(" + xGraus + ")";
//		var xBoxWidth = (parseFloat(pChartValueData.dom.infoPercBox.attr("width")) + 0.2) + "em";
		var xInfoValueX = pChartValueData.dom.infoValue.attr("x");
		var xInvert = " scale(-1, -1)";
		//Inverte posição das informações
		if (xGraus > 90){
			pChartValueData.dom.infoValues.addClass("-invert");
			pChartValueData.dom.infoPercBox.attr("transform", xInvert);
			xTransformInfo += xInvert;
			xInfoValueX = "-" + xInfoValueX;
		}
		pChartValueData.dom.info.attr("transform", xTransformInfo);
		pChartValueData.dom.infoLabel.attr("x", xInfoValueX);
		pChartValueData.dom.infoValue.attr("x", xInfoValueX);
		pChartValueData.dom.infoPerc.text(dbsfaces.math.round(pChartValueData.perc, 2));
		
	},

//	public Double getPieChartRelativeRadius(DBSCharts pCharts){
//		Double xPieChartWidth  = pCharts.getPieChartWidth();
//		Double xRodaRaio = xPieChartWidth;
//		Double xAfastamento = xPieChartWidth + DBSCharts.PieInternalPadding;
//		xAfastamento *= pCharts.getItensCount() - 1 + pCharts.getPieInternalCircleFator() - getIndex();
//		xRodaRaio += xAfastamento;
//
//		return xRodaRaio;
//	}
	
	pvInitializeDrawChartLine: function(pChartsData, pChartData, pChartValueData){
		var xX = pChartsData.infoWidth;
		var xY = pChartsData.infoHeight;
		xX += pChartValueData.index * pChartsData.scaleX;
		xY += (pChartValueData.value.value - pChartsData.dom.maxChartValueData.value.value) * pChartsData.scaleY; //obs:invertida já que a coordenada do svg desce quando o valor é maior
		
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

