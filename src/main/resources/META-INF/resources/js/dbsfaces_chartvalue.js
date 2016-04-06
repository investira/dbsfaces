dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
	
	dbsfaces.chartValue.initialize(xChartValue);
	
//	dbsfaces.chartValue.selectDelta(xChart, xChartValue);
	//Seleciona nova posição do delta e do item selecionado
	$(pId).on("mouseenter", function(e){
//		console.log("chartvalue\t" + e.originalEvent.type);
		dbsfaces.chartValue.select(xChartValue, null);
		e.preventDefault();
		return false;
	});

	$(pId).on("mousedown touchstart", function(e){
//		console.log("chartvalue\t" + e.originalEvent.type);
		dbsfaces.chartValue.select(xChartValue, true);
		e.preventDefault();
		return false;
	});

//	$(pId).on("mouseleave", function(e){
//		console.log("chartvalue\t" + e.originalEvent.type);
//		dbsfaces.chartValue.unSelect(xChartValue);
//		e.preventDefault();
//		return false;
//	});
	
//	//Inicia seleção para cálculo do delta (computer e mobile)
//	$(pId).on("mousedown touchstart", function(e){
//		console.log("mousedown touchstart");
//		dbsfaces.chart.startDeltaDrag(xChart);
//		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
//		e.preventDefault();
//		return false;
//	});
//
//	//Desmaca item selecionado
//	$(pId).mouseleave(function (e){
////		console.log("mouseleave");
//		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, false);
//	});
//	
//	//Seleciona nova posição do delta e do item selecionado
//	$(pId).mousemove(function (e){
////		console.log("mousemove");
//		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
//	});
//
//	//Seleciona nova posição do delta e do item selecionado (Mobile)
//	$(pId).on("≈", function(e){
//		console.log("touchmove");
//		//Verifica se elemento é um dbs_chartValue
//		var xTarget = document.elementFromPoint(e.originalEvent.touches[0].clientX, e.originalEvent.touches[0].clientY);
//		if (typeof(xTarget) != "undefined"){
//			var xTargetChartValue = $(xTarget).parent();
//			if (typeof(xTargetChartValue) != "undefined"){
//				var xClass = xTargetChartValue.attr("class");
//				if (typeof(xClass) != "undefined" 
//				 && xClass.indexOf('dbs_chartValue') != -1){
//					var xTargetChart = xTargetChartValue.closest(".dbs_chart");
//					if (xTargetChart.get(0) == xChart.get(0)){
//						dbsfaces.chartValue.selectValue(xTargetChartValue.closest(".dbs_charts"), xTargetChart, xTargetChartValue, true);
//						e.preventDefault();
//						return false;
//					}
//				}
//			}
//		}
//	});
};


//function Point(x, y) {
//	  this.x = x;
//	  this.y = y;
//	}
//	Now you can create Point objects using the new keyword:
//
//	var p = new Point(4.5, 19.0);
//	To create an array of Point objects you simply create an array, and put Point objects in it:
//
//	var a = [ new Point(1,2), new Point(5,6), new Point(-1,14) ];
//	Or:
//
//	var a = [];
//	a.push(new Point(1,2));
//	a.push(new Point(5,6));
//	a.push(new Point(-1,14));
//	You use the . operator to access the properties in the Point object. Example:
//
//	alert(a[2].x);
//	Or:
//
//	var p = a[2];
//	alert(p.x + ',' + p.y);

dbsfaces.chartValue = {
	initialize: function(pChartValue){
		//Indicar que nunca foi utilizado para cálculo do delta
		dbsfaces.chartValue.pvInitializeData(pChartValue);
		xChart = pChartValue.data("parent");
		///Inicializa conforme tipo de gráfico
		if (xChart.attr("type") == "pie"){
			dbsfaces.chartValue.pvInitializePie(pChartValue);
		}else if (xChart.attr("type") == "line"){
			dbsfaces.chartValue.pvInitializeLine(pChartValue);
		}	
	},

	pvInitializeData: function(pChartValue){
		var xChart = pChartValue.closest(".dbs_chart");
		pChartValue.data("parent", xChart);

		var xChartValuePoint = pChartValue.children(".-point");
		var xChartValueInfo = pChartValue.children(".-info");
		xValue = xChartValueInfo.children(".-value").text();
		if (xValue != 0){
			//Salva valores dentro do próprio componente para facilar o uso no momento do encode do delta
			pChartValue.data("dx", xChartValuePoint.attr("cx"));
			pChartValue.data("dy", xChartValuePoint.attr("cy"));
			pChartValue.data("df", xChartValuePoint.attr("fill"));
			pChartValue.data("dv", pChartValue.attr("value"));
			pChartValue.data("dl", pChartValue.attr("label"));
			pChartValue.data("dd", xChartValueInfo.children(".-value").text());
		}else{
			pChartValue.data("dx", null);
		}
	},

	pvInitializeLine: function(pChartValue){
	},

	pvInitializePie: function(pChartValue){
		var xChartValueInfo = pChartValue.children(".-info");
		var xChartValueValue = xChartValueInfo.children(".-value");
		var xChartValueBox = xChartValueInfo.children(".-box");
		var xTransform = "translateY(-0.70em) ";
		var xWidth = xChartValueValue.get(0).getComputedTextLength() + 5;
		//Ajusta largura do box do label
		if (xChartValueValue.length != 0){
			xChartValueBox.attr("width", xWidth);
			if (xChartValueValue.attr("style").indexOf("text-anchor:end") != -1){
				xTransform += "translateX(-" + xWidth + "px)";
			}
			dbsfaces.ui.cssTransform(xChartValueBox, xTransform);
		}
	},
	
	pvIsEqual: function(pChartValue, pChartValueNew){
		if (pChartValue != null && pChartValueNew != null){
			//Ignora se for para seleciona o mesmo item
			if (pChartValue[0].id == pChartValueNew[0].id){
				return true;
			}
		}
		return false;
	},

	select: function(pChartValue, pSelect){
		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		console.log("guide number\t" + xChart.data("guide"));
		if (xChart.data("guide") == 0){return;}
		var xCharts = xChart.data("parent");
		var xSelected;
		var xSelectedChart;
		var xSelectedCharts;
		var xSelectCV = null;
		//Se for marcação fixa
		if (pSelect != null){
			xSelectCV = false;
			xSelected = xChart.data("cv" + xChart.data("guide"));
			xSelectedChart = xChart;
			xSelectedCharts = xCharts;
			//Alterna marcação marcado/desmarcado
			if (dbsfaces.chartValue.pvIsEqual(xSelected, pChartValue)){
				//Demarca se já estiver selecionado
				xChart.data("cv" + xChart.data("guide"), null);
			}else{
				xSelectCV = true;
				//Marcar
				xChart.data("cv" + xChart.data("guide"), pChartValue);
			}
//			//Re-executa animação
//			pChartValue.svgRemoveClass("-a_single_pulse");
//			pChartValue[0].offsetWidth = pChartValue[0].offsetWidth; //Artificil para formar a re-execução da animação
//			pChartValue.svgAddClass("-a_single_pulse");
			
		//Se for marcação temporária(hover)
		}else{
			xSelected = xCharts.data("selected");
			//Se houver item selectionado
			if (xSelected != null){
				//Ignora se for para selecionar item já selecionado
				if (dbsfaces.chartValue.pvIsEqual(pChartValue, xSelected)){
					return;
				}
				xSelectedChart = xSelected.data("parent");
				xSelectedCharts = xSelectedChart.data("parent");
			}
		}
		//Configura seleção do chart pai
		dbsfaces.chart.select(pChartValue, pSelect);
		//Desmarca item selecionado anteriormente
		dbsfaces.chartValue.pvSelectChartValue(xSelectedCharts, xSelectedChart, xSelected, false);
		if (xSelectCV == null || xSelectCV){
			//Marca item selecionado
			dbsfaces.chartValue.pvSelectChartValue(xCharts, xChart, pChartValue, pSelect);
		}
		
		//Animação da seleção a ativação de guia
		if (xSelectCV != null){
			//Animação no click
			var xAnimationClass = "dbs_charts-selectedGuide";
			if (xChart.attr("type") == "line"){
				if (xSelected != null){
					//Exclui animação no item selecionado anteriormente
					xSelected.svgRemoveClass(xAnimationClass);
				}
				var xChartPathGuide =  $(xChart.data("guide" + xChart.data("guide")));
				dbsfaces.chartValue.pvAddAnimation(xSelectCV, xChartPathGuide, xAnimationClass);
			}else{
				dbsfaces.chartValue.pvAddAnimation(xSelectCV, pChartValue, xAnimationClass);
			}
			//Ativa outro guia
			if (xSelectCV){
//				 ||	typeof(xChart.attr("showdelta")) == 'undefined'){
				if (xChart.attr("type") == "line"){
					if (xChart.data("guide") == 1){
						xChart.data("guide", 2);
					}else{
						xChart.data("guide", 0);
					}
				}
			}
		}
	},
	
	pvAddAnimation: function(pSelectCV, pElement, pAnimationClass){
		pElement.svgRemoveClass(pAnimationClass);
		if (pSelectCV){
			//Artificil para formar a re-execução da animação
			setTimeout(function(){
				pElement.svgAddClass(pAnimationClass);
			},0)
		}
	},

	unSelect: function(pChartValue){
		if (pChartValue == null){return;}
		var	xChart = pChartValue.data("parent");
		var	xCharts = xChart.data("parent");

		//Desmarca chartValue valor informado
		dbsfaces.chartValue.pvSelectChartValue(xCharts, xChart, pChartValue, false);
		//Configura seleção do chart pai
		dbsfaces.chart.unSelect(pChartValue);

		return;
//		//Desmarca outros chartvalues
//		if (xCurrentCharts.data("groupMembers") != null){
//			xCurrentCharts = xCurrentCharts.data("groupMembers");
//		}
//		xCurrentCharts = xCurrentCharts.filter(".-selected");
////		xCurrentCharts = xCurrentCharts;
//		//Charts
//		xCurrentCharts.each(function(){
//			var xCharts = $(this);
//			var xChartsChildren = xCharts.data("children").not(xCurrentChart); //Ignora chart corrente, já que será matido o -selected
////			var xChartsChildren = xCharts.data("children"); //Ignora chart corrente, já que será matido o -selected
//			//Chart
//			xChartsChildren.each(function(){
//				var xChart = $(this);
//				var xChartValue = xChart.data("cv" + xChart.data("guide"));
//				if (xChartValue != null){
//					console.log("before\t" + $("#" + dbsfaces.util.jsid(xChartValue.get(0).id)).attr("class"));
//				}
//				dbsfaces.chartValue.pvSelectChartValue(xCharts, xChart, xChartValue, false);
//				if (xChartValue != null){
//					console.log("after \t" + $("#" + dbsfaces.util.jsid(xChartValue.get(0).id)).attr("class"));
//				}
////				var xChartChildren = xChart.data("children");
////				var xChartValueSelected = xChartChildren.filter(".-selected");
////				var xChartValueSelected = xChartChildren;
////				console.log(xChartValueSelected.length);
////				xChartValueSelected.each(function(){
////					console.log($(this).attr("class"));
////					console.log($("#" + dbsfaces.util.jsid($(this).get(0).id)).attr("class"));
////					dbsfaces.chartValue.pvSelectChartValue(xChart, $(this), false);
////				});
//			});
//		});
//		var xChart = pChartValue.data("parent");
//		var xCharts = xChart.data("parent");
	},

	pvSelectChartValue: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}

		//Seleciona item
		if (pSelect == null || pSelect){
//			dbsfaces.chart.select(pChartValue, pSelect);
			//Força que chartvalue seja esteja a frente
			if (pChart.attr("type") == "bar"
			 || pChart.attr("type") == "line"){
				dbsfaces.ui.moveToFront(pChartValue.get(0));
			}
			//Marca selecionado
			pChartValue.svgAddClass("-selected");
//			console.log("set\t" + pChartValue.attr("class"));
			pCharts.data("selected", pChartValue);
		}else{
			var xGuide;
			//Verifica se item pode ser dermarcado
			xGuide = pChart.data("cv1");
			if (dbsfaces.chartValue.pvIsEqual(pChartValue, xGuide)){
				return;
			}
			xGuide = pChart.data("cv2");
			if (dbsfaces.chartValue.pvIsEqual(pChartValue, xGuide)){
				return;
			}
//			dbsfaces.chart.unSelect(pChartValue);
			pChartValue.svgRemoveClass("-selected");
			pCharts.data("selected", null);
		}
		return true;
	},
	

	selectDelta: function(pChart, pChartValue){
		//Não permite seleção para gráficos diferente de line ou 
		//quando valor do ponto selecionado for zero
		if (pChart.length == 0
		 || pChart.attr("type") != "line"
		 ||	typeof(pChart.attr("showdelta")) == 'undefined'
		 || pChartValue.data("dx") == null
		 || pChartValue.data("dv") == "0.0"){
			return;
		}

		if (pChart.data("cv1") == null){
			pChart.data("cv1", pChartValue);
			pChart.data("cv2", pChartValue);
		}
		//Marca ponto origem
		if (pChart.data("guide") == 1){
			pChart.data("cv1", pChartValue);
		}else{
			pChart.data("cv2", pChartValue);
		}
		dbsfaces.chartValue.showDelta(pChart, pChartValue, -1, -1);
	},
	
	
	showDelta: function(pChart, pChartValue){
		var xDeltaGroup = pChart.data("deltagroup");
		if (xDeltaGroup == null){
			return;
		}
		var xCV1 = pChart.data("cv1");
		var xCV2 = pChart.data("cv2");
		
		//Sai se ponto incial não foi selecionado
		if (xCV1 == null
		 || xCV2 == null){
			dbsfaces.chartValue.removeDeltaGroup(xDeltaGroup);
			return;
		}

		var xTmpX1 = xCV1.data("dx");
		var xTmpY1 = xCV1.data("dy");
		var xTmpX2 = xCV2.data("dx");
		var xTmpY2 = xCV2.data("dy");

		var xTmpDV1 = xCV1.data("dv");
		if (typeof(xTmpDV1) != 'undefined'){
			xTmpDV1 = Number(xTmpDV1);
		}
		var xTmpDV2 = xCV2.data("dv");
		if (typeof(xTmpDV1) != 'undefined'){
			xTmpDV2 = Number(xTmpDV2);
		}
		//Sai de valor selecionado for zero ou sinal entre os dois valores não for o mesmo.
		if (xTmpDV2 == 0 
		 || Math.sign(xTmpDV2) != Math.sign(xTmpDV1)){
			return;
		}
		//
		var xTmpLabel1 = xCV1.data("dl");
		var xTmpValue1 = xCV1.data("dd");
		var xTmpFill1 = xCV1.data("df");
		var xTmpLabel2 = xCV2.data("dl");
		var xTmpValue2 = xCV2.data("dd");
		var xTmpFill2 = xCV2.data("df");
		
		var xX1;
		var xY1;
		var xDV1;
		var xDeltaLabel1;
		var xDeltaValue1;
		var xX2;
		var xY2;
		var xDV2;
		var xDeltaLabel2;
		var xDeltaValue2;
		var xFill;
		var xDeltaAreaWidth = xTmpX2 - xTmpX1;
		if (xDeltaAreaWidth == 0){
			xDeltaAreaWidth = 1;
		}
		if(xDeltaAreaWidth < 0){
			xDeltaAreaWidth = Math.abs(xDeltaAreaWidth);
			xFill = xTmpFill2;
			xX1 = xTmpX2;
			xY1 = xTmpY2;
			xDeltaLabel1 = xTmpLabel2;
			xDeltaValue1 = xTmpValue2;
			xDV1 = xTmpDV2;

			xX2 = xTmpX1;
			xY2 = xTmpY1;
			xDeltaLabel2 = xTmpLabel1;
			xDeltaValue2 = xTmpValue1;
			xDV2 = xTmpDV1;
		}else{
			xFill = xTmpFill1;
			xX1 = xTmpX1;
			xY1 = xTmpY1;
			xDeltaLabel1 = xTmpLabel1;
			xDeltaValue1 = xTmpValue1;
			xDV1 = xTmpDV1;

			xX2 = xTmpX2;
			xY2 = xTmpY2;
			xDeltaLabel2 = xTmpLabel2;
			xDeltaValue2 = xTmpValue2;
			xDV2 = xTmpDV2;
		}

		var xStroke = "stroke:" + xFill + ";";
		var xDeltaValue = dbsfaces.chartValue.calcDelta(xDV1, xDV2);

		//Group info----------------------
		var xDeltaInfoGroup = xDeltaGroup.children(".-info");

		var xStyle = "transform: translate3d(" + xX2 + "px ," + xY2 + "px,0);"
		//Cria Group info
		if (xDeltaInfoGroup.length == 0){
													//!!!!!!!! retirar os 4m?
			xDeltaInfoGroup = dbsfaces.svg.g(xDeltaGroup, null, null, "4em","4em", "-info", xStyle);
		}else{
			xDeltaInfoGroup.svgAttr("style", xStyle);
		}


		//Area selecionada----------------------------
		var xSvgDeltaArea = xDeltaGroup.children(".-deltaarea");
		//Cria linha
		if (xSvgDeltaArea.length == 0){
			xSvgDeltaArea = dbsfaces.svg.rect(xDeltaGroup, xX1, 1, xDeltaAreaWidth, "100%", null, null, "-deltaarea", xStroke, xFill);
			dbsfaces.ui.moveToBack(xSvgDeltaArea);
		}else{
			xSvgDeltaArea.svgAttr("x", xX1)
					 	 .svgAttr("width", xDeltaAreaWidth);
		}		


		//Box=======================
		var xWidth = 0;
		var xSvgDeltaBox = xDeltaInfoGroup.children("rect.-box");
		if (xSvgDeltaBox.length == 0){
			xSvgDeltaBox = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-5.5em", "0em", "4em", 3, 3, "-box", xStroke, null);
		}
		var xSvgDeltaBox2 = xDeltaInfoGroup.children("rect.-box2");
		if (xSvgDeltaBox2.length == 0){
			xSvgDeltaBox2 = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-5.5em", "0em", "1.5em", 3, 3, "-box2", null, xFill);
		}

		//Info Delta=======================
		var xSvgDeltaValue = xDeltaInfoGroup.children("text.-value");
		if (xSvgDeltaValue.length == 0){
			xSvgDeltaValue = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-2.8em", xDeltaValue, "-value", null, null);
		}else{
			xSvgDeltaValue.text(xDeltaValue);
		}
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaValue, null);
		
		//Info label1=======================
		var xSvgDeltaLabel1 = xDeltaInfoGroup.children("text.-label1");
		if (xSvgDeltaLabel1.length == 0){
			xSvgDeltaLabel1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-3em", "", "-label1", null, null);
		}
		xSvgDeltaLabel1.text("x:");
		if (xDeltaLabel1 != ""){
			xSvgDeltaLabel1.text(xSvgDeltaLabel1.text() + xDeltaLabel1 + ":");
		}

		var xSvgDeltaValue1 = xDeltaInfoGroup.children("text.-value1");
		if (xSvgDeltaValue1.length == 0){
			xSvgDeltaValue1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-3em", "", "-value1", null, null);
		}
		xSvgDeltaValue1.text(xDeltaValue1);
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaLabel1, xSvgDeltaValue1);

		//Info label2=======================
		var xSvgDeltaLabel2 = xDeltaInfoGroup.children("text.-label2");
		if (xSvgDeltaLabel2.length == 0){
			xSvgDeltaLabel2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-2em", "", "-label2", null, null);
		}
		xSvgDeltaLabel2.text("y:");
		if (typeof(xDeltaLabel2) != "undefined" 
		 && xDeltaLabel2 != ""){
			xSvgDeltaLabel2.text(xSvgDeltaLabel2.text() + xDeltaLabel2 + ":");
		}

		var xSvgDeltaValue2 = xDeltaInfoGroup.children("text.-value2");
		if (xSvgDeltaValue2.length == 0){
			xSvgDeltaValue2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-2em", "", "-value2", null, null);
		}
		xSvgDeltaValue2.text(xDeltaValue2);
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaLabel2, xSvgDeltaValue2);


		//Box=======================
		xWidthLeft = xWidth / 2 + 2;
		xSvgDeltaLabel1.svgAttr("x", -xWidthLeft);
		xSvgDeltaLabel2.svgAttr("x", -xWidthLeft);
		xSvgDeltaValue1.svgAttr("x", xWidthLeft);
		xSvgDeltaValue2.svgAttr("x", xWidthLeft);

		xWidth *= 1.15;
		xSvgDeltaBox.svgAttr("x",xWidth/-2)
					.svgAttr("width", xWidth);
		xSvgDeltaBox2.svgAttr("x",xWidth/-2)
			 		.svgAttr("width", xWidth);
	},
	
	getMaxWidth: function(pMax, pLabel, pValue){
		var xWidth = 0;
		if (pLabel != null){
			xWidth += pLabel.get(0).getComputedTextLength();
		}
		if (pValue != null){
			xWidth += pValue.get(0).getComputedTextLength();
		}
		if (xWidth > pMax){
			return xWidth;
		}
		return pMax;
	},

	//Coloca item como primeiro elemento para aparecer acima dos demais
	calcDelta: function(pDV1, pDV2){
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
		xValue = (xValue - 1) * 100;
		return dbsfaces.format.number(xValue, 2) + "%";
	},

	//Chamado pelo dbsfaces_chart.js
	hideDelta: function(pChart){
		pChart.data("deltagroup").fadeOut("slow", function(){
			dbsfaces.chartValue.removeDelta(pChart);
		});
	},

	removeDeltaGroup: function(pDeltaGroup){
		if (pDeltaGroup == null){return;}
		pDeltaGroup.empty();
		pDeltaGroup.show();
	},

	removeDelta: function(pChart){
		var xDeltaGroup = pChart.data("deltagroup");
		dbsfaces.chartValue.removeDeltaGroup(xDeltaGroup);
		dbsfaces.chartValue.removeDeltaAttrs(pChart);

	},
	
	removeDeltaAttrs: function(pChart){
		pChart.data("cv1", null);
		pChart.data("cv2", null);
		pChart.data("guide", null);
	}

};

