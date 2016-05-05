dbs_chart = function(pId) {
	var xChart = $(pId);
	
	dbsfaces.chart.initialize(xChart);

	if (xChart.attr("type") == "line"){
		//Não existe cálculo delta
		if (xChart.data("deltagroup") != null){
			$(pId + " > .-delta > .-guide").on("mousedown touchstart", function(e){
//				console.log(".-delta > .-guide mousedown\t" + e.originalEvent.type);
				dbsfaces.chart.setGuideIndex(xChart, Number($(this).attr("guide")));
				e.stopImmediatePropagation();
				return false;
			});
			$(pId + " > .-delta > .-guide").on("mouseup touchend", function(e){
//				console.log(".-delta > .-guide mouseup\t" + e.originalEvent.type);
				dbsfaces.chart.setGuideIndex(xChart, 0);
				e.stopImmediatePropagation();
				return false;
			});
			$(pId + " > .-delta > .-guide").on("mousemove touchmove", function(e){
				if (e.originalEvent.type == "mousemove" 
				 && e.which == 0){
//					console.log(".-delta > .-guide mousemove\t" + e.originalEvent.type);
					dbsfaces.chart.setGuideIndex(xChart, 0);
					return;
				}
				dbsfaces.chart.findPoint(e, xChart);
				e.stopImmediatePropagation();
				return false;
			});		
		}
		//Procura por chartvalue a partir da linha do gráfico
		$(pId + " > .-path > .-mask").on("mousemove touchmove touchstart", function(e){
//				console.log("XXchart\t" + e.originalEvent.type + "\t" + document.elementFromPoint(e.originalEvent.pageX, e.originalEvent.pageY).id);
			//Ignora findpoint se não houver guide ativo.
//			if (e.originalEvent.type == "mousemove" 
//			 && e.which == 0){
//				return;
//			}
			dbsfaces.chart.findPoint(e, xChart);
			e.stopImmediatePropagation();
			return false;
		});
		
//		$(pId + " > .-path > path").on("mousedown touchstart", function(e){
//			console.log("chart path\t" + e.originalEvent.type);
//		})
		$(pId + " > .-path > .-mask").on("mouseleave", function(e){
			dbsfaces.charts.lostFocus(xChart.data("parent"));
			e.stopImmediatePropagation();
			return false;
		});

	} 
	
};



dbsfaces.chart = {
	
	initialize: function(pChart){
		dbsfaces.chart.pvInitializeData(pChart);

		var xCharts = pChart.data("parent");
		var xChartChildren = pChart.data("children");
		var xShowLabel = (typeof(xCharts.attr("showlabel")) != "undefined" && pChart.data("mask").length > 0) ;
		var xShowDelta = (typeof(xChart.attr("showdelta")) != "undefined");
		var xDrawLine = (pChart.attr("type") == "line");
		if (pChart.attr("type") == "bar"
		 || xDrawLine){
			//Verifica sopreposição dos labels e cor da linhas
			dbsfaces.chart.pvInitializeLineAndLabels(xCharts, pChart, xChartChildren, xShowLabel, xDrawLine);
		}
		//Cria guia principal
		if (xShowDelta){
			dbsfaces.chart.pvInitializeDelta(xCharts, pChart);
		}
		dbsfaces.chart.pvSetFontSize(xCharts, pChart);
	},

	//Definir a tamanho do fonte a partir do tamanho do gráfico
	pvSetFontSize: function(pCharts, pChart){
		if (pChart.attr("type") == "pie"){
			dbsfaces.chart.pvChartPieSetFontSize(pCharts, pChart);
		}else if (pChart.attr("type") == "line"){
			dbsfaces.chart.pvChartLineSetFontSize(pCharts, pChart);
		}
	},

	//Definir a tamanho do fonte a partir do tamanho do gráfico
	pvChartPieSetFontSize: function(pCharts, pChart){
		var xSize = pCharts.attr("diameter") / 45;
		pChart.css("font-size", xSize + "px");	
	},

	//Definir a tamanho do fonte a partir do tamanho do gráfico
	pvChartLineSetFontSize: function(pCharts, pChart){
		if (typeof(pChart.attr("showdelta")) != "undefined"){
			var xSize = Number(pChart.data("mask").attr("width")) / 5;
			var xDiameter = pCharts.attr("diameter");
			if (xSize > xDiameter){
				xSize = xDiameter;
			}
			pChart.data("deltainfogroup").svgAttr("font-size", xSize + "px");	
		}
	},

	pvInitializeData: function(pChart){
		var xCharts = pChart.closest(".dbs_charts");
		pChart.data("parent", xCharts);
		//Salva chartvalues vinculados a este chart
		var xChartChildren = pChart.children(".dbs_chartValue");
		pChart.data("children", xChartChildren);
		if (typeof(pChart.attr("showdelta")) != "undefined"){
			var xDeltaGroup = dbsfaces.util.getNotUndefined(pChart.children(".-delta"), null);
			pChart.data("deltagroup", xDeltaGroup);
		}else{
			pChart.data("deltagroup", null);

		}
		pChart.data("pathgroup", dbsfaces.util.getNotUndefined(pChart.children(".-path"), null));
		if (pChart.data("pathgroup") == null){
			pChart.data("mask", null);
		}else{
			pChart.data("mask", dbsfaces.util.getNotUndefined(pChart.data("pathgroup").children(".-mask"), null));
		}
		//Reseta guia 
		dbsfaces.chart.setGuideIndex(pChart, 0);
		dbsfaces.chart.dataRefreshSelection(pChart);
	},
	
	//Verifica sopreposição dos labels 
	pvInitializeLineAndLabels: function(pCharts, pChart, pChartValues, pShowLabel, pDrawLine){
		var xChartValue;
		var xChartValueLabel;
		var xChartValuePoint;
		var xPos;
		var xPosAnt = 0;
		var xStringPath = "";
		var xStarColor;
		var xEndColor;
		var xLabelInterval;
		var xLabelIndex = 1;
		var xLabelPadding = 0;
		var xPreviousLabel = "";
//		var xContainerData = pCharts.find(".-container > .-data");
//		var xContainerDataHeight = xContainerData.attr("height");
		//Loop nos valores por ordem do index para garantir o loop na ordem em que foram criados
		for (var i=1; i <= pChartValues.length; i++){
			xChartValue = pChartValues.filter("[index='" + i + "']");
			//Esconde as labels sobrepostas
			if (pShowLabel){
				xChartValueLabel = xChartValue.data("infogroup").children(".-label");
				xChartValueLabelText = xChartValueLabel.children("text"); //xChartValueLabelText
				xChartValueLabelSmall = xChartValueLabelText.children(".-small");
				xChartValueLabelNormal = xChartValueLabelText.children(".-normal");//xChartValueLabelText
				xPos = Number(xChartValueLabelText.attr("x"));
//				console.log(xChartValueLabelSmall.get(0).getBoundingClientRect().height + "\t" + 
//							xChartValueLabelText.get(0).getBoundingClientRect().height);
				//Armazena maior altura para posretirmente configurar a altura efetiva do gráfico. Artifício necessário pois o firefox precisa de a dimensão seja explicita.
//				if (xContainerDataHeight < xChartValueLabelSmall.get(0).getBoundingClientRect().height){
//					xContainerDataHeight = xChartValueLabelSmall.get(0).getBoundingClientRect().height;
//					xContainerData.svgAttr("height", xContainerDataHeight);
//				}
				//Exibe primeira label obrigatóriamente
				if (i == 1){
					xChartValueLabel.svgRemoveClass("-hide");
					xLabelPadding = xPos;
					var xLabelTotalWidth = (pChart.data("mask").get(0).getBoundingClientRect().width) - xLabelPadding;
					var xLabelMaxItens = Math.round(xLabelTotalWidth / (Number(dbsfaces.number.getOnlyNumber(pChartValues.css("font-size"))) * 2));
					xLabelScale = xLabelTotalWidth / (xLabelMaxItens + 1);
					xPreviousLabel = xChartValue.attr("label");
				}else{
					//Esconde label se não for a última e estiver sobreposta
					if (i < (pChartValues.length) 
					&& xPos < ((xLabelIndex * xLabelScale) + xLabelPadding)){
						xChartValueLabel.svgAddClass("-hide");
					}else{
						//Simplifica o texto do label para evitar repetição de informação
						if (i < pChartValues.length){
							xChartValueLabelSmall.text(dbsfaces.chart.pvSimplifyLabel(xPreviousLabel, xChartValueLabelNormal.text()));
							xPreviousLabel = xChartValueLabelNormal.text();
						}
						xChartValueLabel.svgRemoveClass("-hide");
						xLabelIndex++;
					}
				}
//				xChartValueLabel.children("svg").svgAttr("height", xChartValueLabelSmall.get(0).getBoundingClientRect().height);
			}
			var xAntX;
			var xAntY;
			var xY;
			var xX;
			//Configura a linha conectando os pontos
			if (pDrawLine){
				if (i==1){
					xStringPath = "M";
					//Salva cor do primeiro ponto
					xStarColor = xChartValue.data("df");
				}else {
					xStringPath += "L";
				}
				if (i == pChartValues.length){
					//Salva cor do último ponto
					xStopColor = xChartValue.data("df");
				}
				xX = Number(xChartValue.data("dx"));
				xY = Number(xChartValue.data("dy"));
				//Artificio para corrigir o problema em que a linha com gradient não é exibida quando é uma reta perfeita.
				if (xX==xAntX){xX+=0.0001}
				if (xY==xAntY){xY+=0.0001}
				xStringPath += xX + "," + xY;

				xAntX = xChartValue.data("dx");
				xAntY = xChartValue.data("dy");
			}
		}
		//Cria a linha e cor que conecta os pontos
		if (xStringPath != ""){
			//Cria cor
			var xSvg = pCharts.find(".-container > .-data > .-container > defs");
			var xLG = dbsfaces.svg.linearGradient(xSvg, pChart.get(0).id + "_linestroke");
			dbsfaces.svg.stop(xLG, 0, xStarColor);
			dbsfaces.svg.stop(xLG, "100%", xStopColor);
			//Path do gráfico
			var xPath = dbsfaces.svg.path(pChart.data("pathgroup"), xStringPath, null, null, null);
			xPath.svgAttr("stroke", "url(#" + pChart.get(0).id + "_linestroke)");
			//Salva path no próprio componente para agilizar o findPoint
			pChart.data("path", xPath.get(0));
		}
	},
	
	//Inicializa delta 
	pvInitializeDelta: function(pCharts, pChart){
		//Cria area marcada
		dbsfaces.chart.pvInitializeDeltaAreas(pCharts, pChart);

		dbsfaces.chart.setGuideIndex(pChart, 0);
		if (pChart.data("mask").lenght > 0){
			dbsfaces.ui.moveToFront(pChart.data("mask"));
		}
	},

	//Cria area entre as guias para indicar seleção 
	pvInitializeDeltaAreas: function(pCharts, pChart){
		//Cria texto com o delta
		dbsfaces.chart.pvInitializeDeltaInfo(pChart);

		if (pChart.attr("type") == "line"){
			var xChartChildren = pChart.data("children");
			var xCV1 = null;
			var xCV2 = null;
			if (xChartChildren.length > 0){
				xCV1 = $(xChartChildren.get(0));
				xCV2 = $(xChartChildren.get(xChartChildren.length - 1));
			}
			dbsfaces.chart.pvInitializeDeltaAreaChartLine(pCharts, pChart, xCV1, 1);
			dbsfaces.chart.pvInitializeDeltaAreaChartLine(pCharts, pChart, xCV2, 2);
		}else{
			dbsfaces.chart.pvInitializeDeltaAreaChartPie(pChart);
		}
	},

	pvInitializeDeltaAreaChartLine: function(pCharts, pChart, pChartValue, pGuideIndex){
		var xItem = (pGuideIndex == 1 ? "l": "r");
		var xChartDeltaGroup = pChart.data("deltagroup");
		var xDeltaArea = xChartDeltaGroup.children(".-deltaarea_" + xItem);
		var xChartMask = pChart.data("mask");
		var xHeight = xChartMask.attr("height");
		var xX = (pGuideIndex == 1 ? xChartMask.attr("x") : -(Number(xChartMask.attr("width")) + 1));
		if (xDeltaArea.length == 0){
			xDeltaArea = dbsfaces.svg.rect(xChartDeltaGroup, xX, xChartMask.attr("y"), 0, xHeight, null, null, "-deltaarea_" + xItem, null, null);
			xDeltaArea.svgAttr("fill", "url(#" + pChart.get(0).id + "_linestroke)");
			if (pGuideIndex == 2){
				dbsfaces.ui.cssTransform(xDeltaArea, "scale(-1,1)");
			}
			pChart.data("deltaarea_" + xItem, xDeltaArea);
		}
		//Inicializa guia para o primeiro ponto
		dbsfaces.chart.pvInitializeGuide(pCharts, pChart, pChartValue, pGuideIndex);
	},
	
	pvInitializeDeltaAreaChartPie: function(pChart){
		var xChartDeltaGroup = pChart.data("deltagroup");
		var xDeltaArea = xChartDeltaGroup.children(".-deltaarea");
//		var xChartMask = pChart.data("mask");
//		var xHeight = xChartMask.attr("height");
//		var xX = (pItem == "l" ? xChartMask.attr("x") : -(Number(xChartMask.attr("width")) + 1));
		if (xDeltaArea.length == 0){
			xDeltaArea = dbsfaces.svg.ellipse(xChartDeltaGroup,  null, null, "1em", "1em", null, null, "none");
			xDeltaArea.svgAttr("fill", "url(#" + pChart.get(0).id + "_linestroke)");
			pChart.data("deltaarea", xDeltaArea);
		}
	},
	
	pvInitializeDeltaInfo: function(pChart){
		var xChartDeltaGroup = pChart.data("deltagroup");
		var xDeltaInfoGroup = xChartDeltaGroup.children(".-info");
//		var xCenterX = Number(pChart.data("mask").attr("width")) / 2;
//		var xCenterY = Number(pChart.data("mask").attr("height")) / 2;
		var xFontSize = Number(pChart.data("parent").attr("diameter")) / 40;
//		xFontSize = dbsfaces.math.round(xFontSize,0) * 10;
		if (xDeltaInfoGroup.length == 0){
//			xDeltaInfoGroup = dbsfaces.svg.g(xChartDeltaGroup, null, null, "-info", "transform: translate(" + xCenterX + "px, " + xCenterY + "px);", null);
			xDeltaInfoGroup = dbsfaces.svg.g(xChartDeltaGroup, null, null, "-info", null, null);
//			xDeltaInfoGroup.svgAttr("font-size", xFontSize);
			pChart.data("deltainfogroup", xDeltaInfoGroup);
		}
		var xDeltaValue = xDeltaInfoGroup.children(".-value");
		if (xDeltaValue.length == 0){
			xDeltaValue = dbsfaces.svg.text(xDeltaInfoGroup, null, null, null, "-value", null, null);
//			xDeltaValue.svgAttr("font-siez", );
			xDeltaValue.svgAttr("fill", "url(#" + pChart.get(0).id + "_linestroke)");
			xDeltaValue.svgAttr("stroke", "url(#" + pChart.get(0).id + "_linestroke)");
			pChart.data("deltavalue", xDeltaValue);
		}
	},
	
	//Cria guias 
	pvInitializeGuide: function(pCharts, pChart, pChartValue, pGuideIndex){
		var xChartDeltaGroup = pChart.data("deltagroup");
		var xGuide = xChartDeltaGroup.children(".-guide[guide='" + pGuideIndex + "']");
		if (xGuide.length == 0){
			xGuide = dbsfaces.svg.use(xChartDeltaGroup, pCharts.get(0).id + "_guide", "-guide", null);
			xGuide.svgAttr("guide", pGuideIndex);
			xGuide.svgAttr("style", "");
			//Salva o guia do gráfico
			pChart.data("guide" + pGuideIndex, xGuide);
			//Inicializa chartValue do guia como nulo
			pChart.data("guide" + pGuideIndex).data("cv", pChartValue);
		}
		dbsfaces.chart.setGuideIndex(pChart, pGuideIndex);
		return xGuide;
	},

	//Enconta o ponto(cv) a partir da posição do cursor
	findPoint: function(e, pChart){
		var xChartPathGroup = pChart.data("pathgroup");
		var xChartPath = pChart.data("path");
		var xChartMask = pChart.data("mask");
		var xDecimals = 1;
		var xPosition = xChartMask.offset();
		var xCurrentX = dbsfaces.math.round(e.originalEvent.pageX - xPosition.left, xDecimals);
		if (xCurrentX < 0){return;}
	    var xBeginning = xCurrentX;
        var xEnd =  dbsfaces.math.round(xChartPath.getTotalLength(), xDecimals);
        var xTargetLenght;
        var xTargetPos;
        var xTargetPosX;
        //Procura ponto da caminho(path) que o X é iqual a posição X selecionada
//        var xX = 0;
        while (Math.abs(xBeginning - xEnd) > 1) {
//        	xX++;
        	xTargetLenght = xBeginning +  dbsfaces.math.round((xEnd - xBeginning) / 2, xDecimals); //Meio do caminho
			xTargetPos = xChartPath.getPointAtLength(xTargetLenght); //Ponto do path 
			xTargetPosX = dbsfaces.math.round(xTargetPos.x, xDecimals);
			if (xTargetPosX < xCurrentX){
				xBeginning = xTargetLenght;
			}else if (xTargetPosX > xCurrentX){
				xEnd = xTargetLenght;
			}else{
				break; //Encontrou posição
			}
        }
//        $("#contador").text($("#contador").text() + "\n" + xX);
        //Seleciona o chartValue 
		if (typeof(xTargetPos) != "undefined"){
			//Procura qual dos chartsValues está mais próximo a posição do cursor
			var xChartChildren = pChart.data("children");
			var xIndex = xChartPath.getPathSegAtLength(xTargetLenght);
			var xTotalSegs = $(xChartPath).svgGetPathTotalSegs();
			var xClosestX = xCurrentX;
			var xChartValue = $(xChartChildren.get(xIndex));
			var xX = Number(xChartValue.data("dx"));
			var xY = Number(xChartValue.data("dy"));
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX){
				if (xIndex > 0){
					xClosestX = $(xChartChildren.get(xIndex - 1)).data("dx");
				}
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX){
				if (xIndex < xTotalSegs){
					xClosestX = $(xChartChildren.get(xIndex + 1)).data("dx");
				}
			}
			var xXMiddle = (Number(xClosestX) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValue = $(xChartChildren.get(xIndex - 1));
			}
			//Seleciona chartvalue encontrado
			dbsfaces.chartValue.select(xChartValue, null);
		}
	},

	lostFocus: function(pChart){
		dbsfaces.chart.setGuideIndex(pChart, 0);
	},

	setGuideIndex: function(pChart, pGuideIndex){
		pChart.data("guideIndex", pGuideIndex);
	},
	
	
	dataRefreshSelection: function(pChart){
		pChart.data("selection", pChart.children(".-selected"));
	},
	

	select: function(pChartValue, pSelect){
//		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");
		//Reseta posição dos guias
		dbsfaces.chart.pvSetGuide(xChart, pChartValue, pSelect);
//		if (pSelect == null){
			var xHover = xCharts.data("hover");
			//Ignora se for para selecionar item já selecionado
			if (xHover != null){
				var xHoverChart = xHover.data("parent");
				if (xHoverChart[0].id == xChart[0].id){
					return;
				}
			}
//		}
		//Desmarca item selecionado anteriormente
		dbsfaces.charts.select(pChartValue, pSelect);
		//Desmarca item selecionado anteriormente
		dbsfaces.chart.pvSelectChart(xCharts, xChart, xHover, false);
		//Marca item selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, pSelect);
	},

	unSelect: function(pChartValue){
		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");

		//Reseta posição dos guias
		dbsfaces.chart.pvSetGuide(xChart, pChartValue, false);

		//Desmarca item selecionado anteriormente
		dbsfaces.charts.unSelect(pChartValue);
		//Apaga ponto selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, false);
	},
	
	pvSelectChart: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		dbsfaces.charts.select(pChartValue, pSelect);
	},
		
	
	pvSetGuide: function(pChart, pChartValue, pSelect){
		if (pChart.attr("type") != "line"){return;}
		var xChartGuideIndex = pChart.data("guideIndex");
		if (xChartGuideIndex == 0){return;}
		if (typeof(pChart.attr("showdelta")) == 'undefined'){return;}
		var xChartPathGuide =  pChart.data("guide" + xChartGuideIndex);
		var xChartPathGuideOther =  pChart.data("guide" + (xChartGuideIndex == 1 ? 2 : 1));
		pChart.data("guide" + pChart.data("guideIndex"));
		if (pSelect == null || pSelect){
			var xX = pChartValue.data("dx");
			var xY = pChartValue.data("dy");
			//Copia cor do ponto para a cor da guia
			var xColor = pChartValue.children(".-point").css("stroke");
			xChartPathGuide.css("color", xColor);
			//Reposiciona guia nas coordenadas do chartvalue
			var xCenterY = Number(pChart.data("mask").attr("height")) / 2;
//			console.log(Number(pChart.data("mask").attr("height")) / 2);
	        dbsfaces.ui.cssTransform(xChartPathGuide, "translate3d(" + xX + "px ," + xCenterY + "px,0)");
//	        if (pSelect){
	        	//Retira marcação do item anterior se for diferente da marcação do outro guide
		        if (typeof(xChartPathGuideOther) != "undefined"){
		        	if (xChartPathGuide.data("cv")[0].id != xChartPathGuideOther.data("cv")[0].id
    	        	|| !xChartPathGuideOther.data("cv").svgHasClass("-selected")){
    		        	xChartPathGuide.data("cv").svgRemoveClass("-selected");
    	        	} 
		        }
		        //Seta novo item
	        	xChartPathGuide.data("cv", pChartValue);
	        	//Marca novo item
		        xChartPathGuide.data("cv").svgAddClass("-selected");
//	        }
//		}else{
//	        xChartPathGuide.data("cv", null);
//			xChartPathGuide.hide();
			xChartPathGuide.show();
		}
		
		dbsfaces.chart.pvShowDelta(pChart);
		dbsfaces.chart.pvSetGuideAround(pChart, pChartValue, xChartGuideIndex, pSelect);
	},

	pvSetGuideAround: function(pChart, pChartValue, pChartGuideIndex, pSelect){
		var xClass = "-around" + pChartGuideIndex;
		pChartValue.svgRemoveClass(xClass);
		pChartValue.siblings(".dbs_chartValue." + xClass).svgRemoveClass(xClass);
		var xFontSize = pChart.data("parent").data("fontsize");
		var xXLeft = pChartValue.data("dx") - xFontSize;
		var xXRight = pChartValue.data("dx") + xFontSize;
		var xLoop = true;
		var xElement;
		xElement = pChartValue;
		while (true){
			xElement = xElement.prev(".dbs_chartValue");
			if (xElement.length == 0
			 || xElement.data("dx") < xXLeft){
				break;
			}
			xElement.svgAddClass(xClass);
		}
		xElement = pChartValue;
		while (true){
			xElement = xElement.next(".dbs_chartValue");
			if (xElement.length == 0
			 || xElement.data("dx") > xXRight){
				break;
			}
			xElement.svgAddClass(xClass);
		}
	},
	
	pvShowDelta: function(pChart){
		var xChartDeltaGroup = pChart.data("deltagroup");
		if (xChartDeltaGroup.lenght == 0){return;}
		if (pChart.attr("type") == "line"){
			dbsfaces.chart.pvShowDeltaChartLine(pChart);
		}
	},

	pvShowDeltaChartLine: function(pChart){
		var xCV1 = pChart.data("guide1").data("cv");
		var xCV2 = pChart.data("guide2").data("cv");
		dbsfaces.chart.pvShowDeltaChartLineArea(pChart, xCV1, xCV2);
		dbsfaces.chart.pvShowDeltaChartLineValue(pChart, xCV1, xCV2);
	},
	
	pvShowDeltaChartLineArea: function(pChart, pCV1, pCV2){
		var xDeltaAreaR = pChart.data("deltaarea_r");
		var xDeltaAreaL = pChart.data("deltaarea_l");
		var xCV1 = pCV1;
		var xCV2 = pCV2;
		if (xCV1.data("dx") > xCV2.data("dx")){
			xCV1 = pCV2; 
			xCV2 = pCV1;
		}
		xDeltaAreaL.svgAttr("width", xCV1.data("dx") - Number(xDeltaAreaL.attr("x")));
		xDeltaAreaR.svgAttr("width", -(xCV2.data("dx") + Number(xDeltaAreaR.attr("x"))));
	},

	pvShowDeltaChartLineValue: function(pChart, pCV1, pCV2){
		var xDeltaValue = pChart.data("deltavalue");
		var xValue;
		if (pCV1.attr("index") == 1
		 && pCV2.attr("index") == 1){
			xDeltaValue.text("");
		}else{
			if (pCV1.attr("index") < pCV2.attr("index")){
				xValue = dbsfaces.chart.pvCalcDelta(pCV1.data("dv"), pCV2.data("dv"));
			}else{
				xValue = dbsfaces.chart.pvCalcDelta(pCV2.data("dv"), pCV1.data("dv"));
			}
			xDeltaValue.svgAttr("dy", ".38em");
			xDeltaValue.text("");
			var xSpanValue =  dbsfaces.svg.createElement("tspan");
			xSpanValue.text(xValue);
			xDeltaValue.append(xSpanValue);
			var xSpanPerc =  dbsfaces.svg.createElement("tspan");
			xSpanPerc.svgAttr("class", "-label");
			xSpanPerc.text("%");
			xDeltaValue.append(xSpanPerc);
		}		

		var xDeltaInfoGroup = pChart.data("deltainfogroup");
		var xCenterX = Number(pChart.data("mask").attr("width")) / 2;
		var xCenterY = Number(pChart.data("mask").attr("height")) / 2;
		xDeltaInfoGroup.svgAttr("style", "transform:translate(" + xCenterX + "px, " + xCenterY + "px);");
	},
	
	//Coloca item como primeiro elemento para aparecer acima dos demais
	pvCalcDelta: function(pDV1, pDV2){
		if (pDV1 == 0
		 || pDV2 == 0){
			return "-";
		}
		var xValue;
		if (pDV1 < 0){
			xValue = (pDV1 / pDV2);
		}else{
			xValue = (pDV2 / pDV1);
		}
		xValue = dbsfaces.math.round(xValue, 4);
		xValue = (xValue - 1) * 100;
		return dbsfaces.format.number(xValue, 2);
	},

	pvSimplifyLabel: function(pPreviuosLabel, pCurrentLabel){
		//Delimitador no final
//		var xRE = new RegExp(/[^ \-.:\r\n/\\]+[ \-.:\r\n/\\]*/g);
//		var xP = pPreviuosLabel.match(xRE);
//		var xC = pCurrentLabel.match(xRE);
		//Delimitador no inicio
		var xRE = new RegExp(/(?=[ \-.:\r\n/\\]+)/);
//		var xRE = new RegExp(/([ \-.:\r\n/\\]+)/);
		var xP = pPreviuosLabel.split(xRE);
		var xC = pCurrentLabel.split(xRE);
		var xS = "";
		for (var i=0; i < xC.length; i++){
			if (xP.length >= i){
				if (xP[i] != xC[i]){
					for (k=i; k < xC.length; k++){
						xS += xC[k];
					}
					break;
				}
			}
		}
		return xS;
	}

//	
//	//Escoder todos os deltas
//	//Chamado pelo dbsfaces_charts
//	hideDelta: function(pCharts, pChart){
//		dbsfaces.chart.hideChartValueDelta(pChart);
//		
//		//Procura por todos os charts da mesma familia para esconder o delta, se existir.
//		var xMembers = pCharts.data("groupMembers");
//		if (xMembers != null) {
//			xMembers.each(function(){
//				xCharts = $(this);
//				var xChart = $(this).data("children").not(pChart);
//				xChart.each(function(){
//					dbsfaces.chart.hideChartValueDelta($(this));
//				});
//			});
//		}
//	},
//	
//	hideChartValueDelta: function(pChart){
//		//Esconde delta após 5 segundos caso não tenha sido selecionado o intervalo definitivo
//		if (pChart.attr("type") == "line"
//	     && typeof(pChart.attr("showdelta")) != 'undefined'){
//			dbsfaces.chartValue.hideDelta(pChart);
//		}
//	}

};

