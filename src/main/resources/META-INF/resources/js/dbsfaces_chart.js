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
			dbsfaces.chart.findPoint(e, xChart);
			e.stopImmediatePropagation();
			return false;
		});
		
		$(pId + " > .-path > .-mask").on("mouseleave", function(e){
			dbsfaces.charts.lostFocus(xChart.data("parent"));
			e.stopImmediatePropagation();
			return false;
		});
	}else{
		$(pId).on("mouseleave", function(e){
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
		var xShowDelta = (pChart.data("deltagroup") != null);
		//Verifica sopreposição dos labels e cor da linhas
		if (pChart.attr("type") == "bar"){
			dbsfaces.chart.pvInitializeLineAndLabels(xCharts, pChart, xChartChildren, xShowLabel, false);
		}else if (pChart.attr("type") == "line"){
			dbsfaces.chart.pvInitializeLineAndLabels(xCharts, pChart, xChartChildren, xShowLabel, true);
		}
		//Cria espaço exibir o resultado das seleções
		if (xShowDelta){
			dbsfaces.chart.pvInitializeDelta(xCharts, pChart);
		}
		dbsfaces.chart.pvSetFontSize(xCharts, pChart);
		dbsfaces.chart.dataRefreshSelection(xCharts, pChart);
	},

	pvInitializeData: function(pChart){
		var xCharts = pChart.closest(".dbs_charts");
		pChart.data("parent", xCharts);
		//Salva chartvalues vinculados a este chart
		var xChartChildren = pChart.children(".dbs_chartValue");
		pChart.data("children", xChartChildren);
		pChart.data("deltagroup", null);
		pChart.data("deltainfogroup", null);
		pChart.data("mask", null);
		//Tamanho máximo do label da coluna
		pChart.data("maxlabelheight", 0);
		//Tamanho máximo do label da linha
		pChart.data("maxlabelwidth", 0);
		if (typeof(xCharts.attr("showdelta")) != "undefined"){
			var xDeltaGroup = dbsfaces.util.getNotUndefined(pChart.children(".-delta"), null);
			pChart.data("deltagroup", xDeltaGroup);
			var xDeltaInfoGroup = xDeltaGroup.children(".-info");
			if (xDeltaInfoGroup.length != 0){
				pChart.data("deltainfogroup", xDeltaInfoGroup);
			}			
		}
		pChart.data("pathgroup", dbsfaces.util.getNotUndefined(pChart.children(".-path"), null));
		if (pChart.data("pathgroup") != null){
			pChart.data("mask", dbsfaces.util.getNotUndefined(pChart.data("pathgroup").children(".-mask"), null));
		}
		//Reseta guia 
		dbsfaces.chart.setGuideIndex(pChart, 0);
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
//		var xSize = pCharts.attr("diameter") / 45;
//		pChart.css("font-size", xSize + "em");
//		pChart.css("font-size", "1em");
	},

	//Definir a tamanho do fonte do delta a partir do tamanho do gráfico
	pvChartLineSetFontSize: function(pCharts, pChart){
		if (typeof(pCharts.attr("showdelta")) != "undefined"){
			var xSize = Number(pChart.data("mask").attr("width")) / 6.5;
			xSize = dbsfaces.math.trunc(xSize /  2, 0) * 2; 
			var xDiameter = pCharts.attr("diameter");
			if (xSize > xDiameter){
				xSize = xDiameter;
			}
			pChart.data("deltainfogroup").svgAttr("font-size", xSize + "px");
		}
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
		var xLabelIndex = 1;
		var xLabelPadding = 0;
		var xPreviousLabel = "";
		//Loop nos valores por ordem do index para garantir o loop na ordem em que foram criados
		for (var i=1; i <= pChartValues.length; i++){
			xChartValue = pChartValues.filter("[index='" + i + "']");
			//Esconde as labels sobrepostas
			if (pShowLabel){
				xChartValueLabel = xChartValue.data("infogroup").children(".-label");
				xChartValueLabelLine = xChartValue.data("value");
				xChartValueLabelText = xChartValueLabel.children("text"); //xChartValueLabelText
				xChartValueLabelSmall = xChartValueLabelText.children(".-small");
				xChartValueLabelNormal = xChartValueLabelText.children(".-normal");//xChartValueLabelText
				xPos = Number(xChartValueLabel.attr("x"));
				//Exibe primeira label obrigatóriamente
				if (i == 1){
					xChartValueLabel.svgRemoveClass("-hide");
					xLabelPadding = xPos;
					var xLabelTotalWidth = Number(pChart.data("mask").svgAttr("width")) - xLabelPadding;
					//Calcula quantidade máxima de labels sem haver sobreposição.
					var xLabelMaxItens = Math.round(xLabelTotalWidth / (parseFloat(pChartValues.css("font-size")) * 2));
					xLabelScale = xLabelTotalWidth / (xLabelMaxItens + 1);
					xPreviousLabel = xChartValue.attr("label");
				}else{
					//Esconde label se não for a última e estiver sobreposta
					if (i < (pChartValues.length) 
					&& xPos < ((xLabelIndex * xLabelScale) + xLabelPadding)){
						xChartValueLabel.svgAddClass("-hide");
					//Exibe label
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
				if (xChartValueLabel[0].getBoundingClientRect().height > pChart.data("maxlabelheight")){
					pChart.data("maxlabelheight", xChartValueLabel[0].getBoundingClientRect().height); 
				}
				if (xChartValueLabelLine[0].getBoundingClientRect().width > pChart.data("maxlabelwidth")){
					pChart.data("maxlabelwidth", xChartValueLabelLine[0].getBoundingClientRect().width); 
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
					xStarColor = xChartValue.data("dc");
				}else {
					xStringPath += "L";
				}
				if (i == pChartValues.length){
					//Salva cor do último ponto
					xStopColor = xChartValue.data("dc");
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
		//Cria a linha e cor que conectam os pontos
		if (xStringPath != ""){
			//Cria cor
			var xSvg = pCharts.find("> .-container > .-data > .-container > defs");
			var xLG = dbsfaces.svg.linearGradient(xSvg, pChart.get(0).id + "_linestroke");
			dbsfaces.svg.stop(xLG, 0, xStarColor);
			dbsfaces.svg.stop(xLG, "100%", xStopColor);
			//Cria Linha
			var xPath = dbsfaces.svg.path(pChart.data("pathgroup"), xStringPath, null, null, {"stroke": "url(#" + pChart.get(0).id + "_linestroke)"});
			//Salva path no próprio componente para agilizar o findPoint
			pChart.data("path", xPath.get(0));
		}else{
			pChart.data("path", null);
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
		dbsfaces.chart.pvInitializeDeltaInfo(pCharts, pChart);

		if (pChart.attr("type") == "line"){
			var xChartChildren = pChart.data("children");
			if (pChart.data("path") == null){return;}
			var xCV1 = null;
			var xCV2 = null;
			if (xChartChildren.length > 0){
				xCV1 = $(xChartChildren.get(0));
				xCV2 = $(xChartChildren.get(xChartChildren.length - 1));
			}
			dbsfaces.chart.pvInitializeDeltaAreaChartLine(pCharts, pChart, xCV1, 1);
			dbsfaces.chart.pvInitializeDeltaAreaChartLine(pCharts, pChart, xCV2, 2);
		}else if (pCharts.attr("type") == "pie"){
			dbsfaces.chart.pvInitializeDeltaAreaChartPie(pChart);
		}
	},
	
	pvInitializeDeltaInfo: function(pCharts, pChart){
		var xDeltaGroup = pChart.data("deltagroup");
		var xDeltaInfoGroup = xDeltaGroup.children(".-info");
		if (xDeltaInfoGroup.length == 0){
			xDeltaInfoGroup = dbsfaces.svg.g(xDeltaGroup, null, null, "-info", null, null);
			pChart.data("deltainfogroup", xDeltaInfoGroup);
		}
		var xDeltaValue = xDeltaInfoGroup.children(".-value");
		if (xDeltaValue.length == 0){
			xDeltaValue = dbsfaces.svg.text(xDeltaInfoGroup, null, null, null, "-value", null, null);
			if (pCharts.attr("type") == "pie"){
				var xDeltaPathId;
				var xDeltaValuePath;
				var xClass;
				//Path a direita - Percentual
				xClass = "deltapath_r";
				xDeltaPathId = pChart.get(0).id + "_" + xClass;
				xDeltaValuePath = dbsfaces.svg.textPath(xDeltaValue, xDeltaPathId, "", "-" + xClass, null, {"startOffset": "0%"});
				pChart.data("deltavalue_r", xDeltaValuePath);

				//Path a esquerda - Valor
				xClass = "deltapath_l";
				xDeltaPathId = pChart.get(0).id + "_" + xClass;
				xDeltaValuePath = dbsfaces.svg.textPath(xDeltaValue, xDeltaPathId, "", "-" + xClass, null, {"startOffset": "100%"});
				pChart.data("deltavalue_l", xDeltaValuePath);
			}else{
				var xLineStrokeId = pChart.get(0).id + "_linestroke";
				xDeltaValue.svgAttr("fill", "url(#" + xLineStrokeId + ")");
				xDeltaValue.svgAttr("stroke", "url(#" + xLineStrokeId + ")");
				pChart.data("deltavalue", xDeltaValue);
			}
		}
	},
	
	pvInitializeDeltaAreaChartLine: function(pCharts, pChart, pChartValue, pGuideIndex){
		var xItem = (pGuideIndex == 1 ? "l": "r");
		var xDeltaGroup = pChart.data("deltagroup");
		var xDeltaArea = xDeltaGroup.children(".-deltaarea_" + xItem);
		var xChartMask = pChart.data("mask");
		var xHeight = xChartMask.attr("height");
		var xX = (pGuideIndex == 1 ? xChartMask.attr("x") : -(Number(xChartMask.attr("width")) + 1));
		if (xDeltaArea.length == 0){
			xDeltaArea = dbsfaces.svg.rect(xDeltaGroup, xX, xChartMask.attr("y"), 0, xHeight, null, null, "-deltaarea_" + xItem, null, null);
			xDeltaArea.svgAttr("fill", "url(#" + pChart.get(0).id + "_linestroke)");
			if (pGuideIndex == 2){
				//Inverte relação das coordernadas
				dbsfaces.ui.cssTransform(xDeltaArea, "scale(-1,1)");
			}
			pChart.data("deltaarea_" + xItem, xDeltaArea);
		}
		//Inicializa guia para o primeiro ponto
		dbsfaces.chart.pvInitializeGuide(pCharts, pChart, pChartValue, pGuideIndex);
	},
	
	pvInitializeDeltaAreaChartPie: function(pChart){
	},

	
	//Cria guias 
	pvInitializeGuide: function(pCharts, pChart, pChartValue, pGuideIndex){
		var xDeltaGroup = pChart.data("deltagroup");
		var xGuide = xDeltaGroup.children(".-guide[guide='" + pGuideIndex + "']");
		if (xGuide.length == 0){
			xGuide = dbsfaces.svg.use(xDeltaGroup, pCharts.get(0).id + "_guide", "-guide", null, {"guide" : pGuideIndex});
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
		if (xChartPath == null){return;}
		var xChartMask = pChart.data("mask");
		var xDecimals = 1;
		var xPosition = xChartMask.offset();
		var xCurrentX = dbsfaces.math.round(e.originalEvent.pageX - xPosition.left, xDecimals);
		if (xCurrentX < 0){return;}
	    var xBeginning = xCurrentX;
        var xEnd = dbsfaces.math.round(xChartPath.getTotalLength(), xDecimals);
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
	
	//Atualiza lista dos itens selecionados e calcula somatórios dos itens selecionados
	dataRefreshSelection: function(pCharts, pChart){
		pChart.data("selection", pChart.children(".-selected"));
		pChart.data("totval", null);
		pChart.data("totper", null);
		if (typeof(pCharts.attr("showdelta")) == "undefined"){return;}
		//Calcula somatórios dos itens selecionados
		if (pChart.attr("type") != "line"){
			var xTotVal = 0;
			var xTotPer = 0;
			pChart.data("selection").each(function(){
				xTotVal += Number($(this).data("dv"));
				xTotPer += Number($(this).data("dp"));
			});
			pChart.data("totval", dbsfaces.math.round(xTotVal,2));
			pChart.data("totper", dbsfaces.math.round(xTotPer, 2));
			if (pChart.attr("type") == "pie"){
				dbsfaces.chart.pvShowDeltaChartPie(pChart);
			}
		}
	},
	

	select: function(pChartValue, pSelect){
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");

		//Exibe delta
		dbsfaces.chart.pvShowDelta(xCharts, xChart, pChartValue, pSelect);
		
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

		//Exibe delta
		dbsfaces.chart.pvShowDelta(xCharts, xChart, pChartValue, false);
		
		//Desmarca item selecionado anteriormente
		dbsfaces.charts.unSelect(pChartValue);
		//Apaga ponto selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, false);
	},
	

	pvSelectChart: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		dbsfaces.charts.select(pChartValue, pSelect);
	},
		
	pvShowDelta: function(pCharts, pChart, pChartValue, pSelect){
		var xDeltaGroup = pChart.data("deltagroup");
		if (xDeltaGroup == null 
		 || xDeltaGroup.length == 0){return;}
		if (pChart.attr("type") == "line"){
			//Reseta posição dos guias
			dbsfaces.chart.pvSetGuide(pCharts, pChart, pChartValue, pSelect);
		}
	},
	
	pvSetGuide: function(pCharts, pChart, pChartValue, pSelect){
		if (pChart.attr("type") != "line"){return;}
		var xChartGuideIndex = pChart.data("guideIndex");
		if (xChartGuideIndex == 0){return;}
		if (typeof(pCharts.attr("showdelta")) == "undefined"){return;}
		var xChartPathGuide =  pChart.data("guide" + xChartGuideIndex);
		var xChartPathGuideOther =  pChart.data("guide" + (xChartGuideIndex == 1 ? 2 : 1));
		pChart.data("guide" + pChart.data("guideIndex"));
		if (pSelect == null || pSelect){
			var xX = pChartValue.data("dx");
			var xY = pChartValue.data("dy");
			//Copia cor do ponto para a cor da guia
			var xColor = pChartValue.data("pointgroup").css("stroke");
			xChartPathGuide.css("color", xColor);
			//Reposiciona guia nas coordenadas do chartvalue
			var xCenterY = Number(pChart.data("mask").attr("height")) / 2;
	        dbsfaces.ui.cssTransform(xChartPathGuide, "translate3d(" + xX + "px ," + xCenterY + "px,0)");
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
			xChartPathGuide.show();
		}
		dbsfaces.chart.pvShowDeltaChartLine(pChart);
		//Exibe delta
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

	pvShowDeltaChartLine: function(pChart){
		var xCV1 = pChart.data("guide1").data("cv");
		var xCV2 = pChart.data("guide2").data("cv");
		dbsfaces.chart.pvShowDeltaChartLineArea(pChart, xCV1, xCV2);
		dbsfaces.chart.pvShowDeltaChartLineValue(pChart, xCV1, xCV2);
	},

	pvShowDeltaChartPie: function(pChart){
		var xDeltaValue;
		//Percentual
		xDeltaValue = pChart.data("deltavalue_r");
		if (pChart.data("totper") == 0){
			xDeltaValue.text("");
		}else{
			xDeltaValue.text(pChart.data("totper") + "%");
		}
		//Valor
		xDeltaValue = pChart.data("deltavalue_l");
		if (pChart.data("totval") == 0){
			xDeltaValue.text("");
		}else{
			xDeltaValue.text(dbsfaces.format.numberSimplify(pChart.data("totval")));
		}
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
			if (Number(pCV1.attr("index")) < Number(pCV2.attr("index"))){
				xValue = dbsfaces.chart.pvCalcDelta(pChart, pCV1.data("dv"), pCV2.data("dv"));
			}else{
				xValue = dbsfaces.chart.pvCalcDelta(pChart, pCV2.data("dv"), pCV1.data("dv"));
			}
			xDeltaValue.svgAttr("dy", ".38em");
			xDeltaValue.text("");
			//Valor
			var xSpanValue =  dbsfaces.svg.createElement("tspan", null);
			xSpanValue.text(xValue);
			xDeltaValue.append(xSpanValue);
			//Simbolo do percentual
			var xSpanPerc =  dbsfaces.svg.createElement("tspan", {"class": "-label"});
			xSpanPerc.text("%");
			xDeltaValue.append(xSpanPerc);
		}		

		var xDeltaInfoGroup = pChart.data("deltainfogroup");
		var xCenterX = Number(pChart.data("mask").attr("width")) / 2;
		var xCenterY = Number(pChart.data("mask").attr("height")) / 2;
		xDeltaInfoGroup.svgAttr("style", "transform:translate(" + xCenterX + "px, " + xCenterY + "px);");
	},
	
	//Coloca item como primeiro elemento para aparecer acima dos demais
	pvCalcDelta: function(pChart, pDV1, pDV2){
		if (pDV1 == 0
		 || pDV2 == 0){
			return "(na)";
		}
		var xValue;
		if (pChart.data("parent").attr("perc")){
			xValue = (pDV2 - pDV1);
			xValue *= 100;
		}else{
			if (pDV1 < 0){
				xValue = (pDV1 / pDV2);
			}else{
				xValue = (pDV2 / pDV1);
			}
			xValue = dbsfaces.math.round(xValue, 4);
			xValue = (xValue - 1) * 100;
		}
		return dbsfaces.format.number(xValue, 2);
	},

	pvSimplifyLabel: function(pPreviuosLabel, pCurrentLabel){
		//Delimitador no final
		//Delimitador no inicio
		var xRE = new RegExp(/(?=[ \-.:\r\n/\\]+)/);
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

};

