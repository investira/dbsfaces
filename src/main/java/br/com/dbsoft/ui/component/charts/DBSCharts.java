package br.com.dbsoft.ui.component.charts;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSCharts.COMPONENT_TYPE)
public class DBSCharts extends DBSUIInput implements NamingContainer{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTS;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	public static Integer 	PieInternalPadding = 2;
	public static Double 	TopicsHeight = .7 * 1.7; //Font em .7 * 1.7(170%)
	public static Double 	LabelsWidth = .35; //Font em .7(70%)
	public static Double 	LabelsHeight = .7 * .66; //Font em .7 * .66(66%)


	public static enum TYPE {
		BAR 			("bar", true),
		LINE 			("line", true),	
	    PIE 			("pie", false);
		
		private String 	wName;
		private Boolean	wMatrix;
		
		private TYPE(String pName, Boolean pMatrix) {
			this.wName = pName;
			this.wMatrix = pMatrix;
		}

		public String getName() {
			return wName;
		}
		
		/**
		 * Se é um gráfico de linhas ou colunas(Matrix X x Y)
		 * @return
		 */
		public Boolean isMatrix(){
			return wMatrix;
		}

		public static TYPE get(String pCode) {
			if (pCode == null){
				return LINE;
			}			
			pCode = pCode.trim().toLowerCase();
			switch (pCode) {
			case "bar":
				return BAR;
			case "line":
				return LINE;
			case "pie":
				return PIE;
			default:
				return LINE;
			}
		}	
	}
	

	protected enum PropertyKeys {
		type,
		caption,
		footer,
		width,
		height,
		padding,
		valueFormatMask,
		showGrid,
		showGridValue,
		showLabel,
		groupId,
		pieInternalCircleFator,

		//Variáveis de trabalho
		maxValue,
		minValue,
		rowScale,
		chartWidth,
		chartHeight,
		labelMaxHeight,
		labelMaxWidth,
		captionsMaxHeight,
		deltaListMaxHeight,
		itensCount,
		chartValueItensCount,
		numberOfGridLines,
		fontSize,
		showDelta,
		showDeltaList;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}

	public DBSCharts(){
		setRendererType(DBSCharts.RENDERER_TYPE);
    }
	

	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, null);
	}
	
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}

	public String getFooter() {
		return (String) getStateHelper().eval(PropertyKeys.footer, null);
	}
	
	public void setFooter(String pFooter) {
		getStateHelper().put(PropertyKeys.footer, pFooter);
		handleAttribute("footer", pFooter);
	}
	
	public Boolean getShowGrid() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showGrid, true);
	}
	public void setShowGrid(Boolean pShowGrid) {
		getStateHelper().put(PropertyKeys.showGrid, pShowGrid);
		handleAttribute("showGrid", pShowGrid);
	}

	public Boolean getShowGridValue() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showGridValue, true);
	}
	public void setShowGridValue(Boolean pShowGridValue) {
		getStateHelper().put(PropertyKeys.showGridValue, pShowGridValue);
		handleAttribute("showGridValue", pShowGridValue);
	}

	public Boolean getShowDelta() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showDelta, false);
	}
	public void setShowDelta(Boolean pShowDelta) {
		getStateHelper().put(PropertyKeys.showDelta, pShowDelta);
		handleAttribute("showDelta", pShowDelta);
	} 
	
	public Boolean getShowDeltaList() {
		//DeltaList só é exibido quando for também exibido os labels e deltas
		return (Boolean) getStateHelper().eval(PropertyKeys.showDeltaList, false);
	}

	public void setShowDeltaList(Boolean pShowDeltaList) {
		getStateHelper().put(PropertyKeys.showDeltaList, pShowDeltaList);
		handleAttribute("showDeltaList", pShowDeltaList);
	} 

	public Integer getHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.height, 80);
	}
	public void setHeight(Integer pHeight) {
		if (pHeight == null || pHeight < 1){
			pHeight = 80;
		}
		getStateHelper().put(PropertyKeys.height, pHeight);
		handleAttribute("hight", pHeight);
	}

	public Integer getWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.width, 80);
	}
	public void setWidth(Integer pWidth) {
		if (pWidth == null || pWidth < 1){
			pWidth = 80;
		}
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("width", pWidth);
	}

	public Integer getPadding() {
		return (Integer) getStateHelper().eval(PropertyKeys.padding, 2);
	}
	public void setPadding(Integer pPadding) {
		getStateHelper().put(PropertyKeys.padding, pPadding);
		handleAttribute("padding", pPadding);
	}

	public Double getPieInternalCircleFator() {
		return (Double) getStateHelper().eval(PropertyKeys.pieInternalCircleFator, 2D);
	}
	public void setPieInternalCircleFator(Double pPieInternalCircleFator) {
		getStateHelper().put(PropertyKeys.pieInternalCircleFator, pPieInternalCircleFator);
		handleAttribute("pieInternalCircleFator", pPieInternalCircleFator);
	}

	public String getValueFormatMask() {
		return (String) getStateHelper().eval(PropertyKeys.valueFormatMask, "");
	}
	public void setValueFormatMask(String pValueFormatMask) {
		getStateHelper().put(PropertyKeys.valueFormatMask, pValueFormatMask);
		handleAttribute("valueFormatMask", pValueFormatMask);
	}

	//--------------------
	public String getGroupId() {
		return (String) getStateHelper().eval(PropertyKeys.groupId, null);
	}
	public void setGroupId(String pGroupId) {
		getStateHelper().put(PropertyKeys.groupId, pGroupId);
		handleAttribute("groupId", pGroupId);
	}


	public Double getMaxValue() {
		return (Double) getStateHelper().eval(PropertyKeys.maxValue, null);
	}
	public void setMaxValue(Double pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}

	public Double getMinValue() {
		return (Double) getStateHelper().eval(PropertyKeys.minValue, null);
	}
	public void setMinValue(Double pMinValue) {
		getStateHelper().put(PropertyKeys.minValue, pMinValue);
		handleAttribute("minValue", pMinValue);
	}

	public Double getRowScale() {
		return (Double) getStateHelper().eval(PropertyKeys.rowScale, 0D);
	}
	public void setRowScale(Double pRowScale) {
		getStateHelper().put(PropertyKeys.rowScale, pRowScale);
		handleAttribute("rowScale", pRowScale);
	}

	public Integer getLabelMaxWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.labelMaxWidth, 0D);
	}
	public void setLabelMaxWidth(Integer pLabelMaxWidth) {
		getStateHelper().put(PropertyKeys.labelMaxWidth, pLabelMaxWidth);
		handleAttribute("labelMaxWidth", pLabelMaxWidth);
	}

	public Integer getLabelMaxHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.labelMaxHeight, 0D);
	}
	public void setLabelMaxHeight(Integer pLabelMaxHeight) {
		getStateHelper().put(PropertyKeys.labelMaxHeight, pLabelMaxHeight);
		handleAttribute("labelMaxHeight", pLabelMaxHeight);
	}

	public Integer getCaptionsMaxHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.captionsMaxHeight, 0D);
	}
	public void setCaptionsMaxHeight(Integer pCaptionsMaxHeight) {
		getStateHelper().put(PropertyKeys.captionsMaxHeight, pCaptionsMaxHeight);
		handleAttribute("captionsMaxHeight", pCaptionsMaxHeight);
	}

	public Integer getDeltaListMaxHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.deltaListMaxHeight, 0D);
	}
	public void setDeltaListMaxHeight(Integer pDeltaListMaxHeight) {
		getStateHelper().put(PropertyKeys.deltaListMaxHeight, pDeltaListMaxHeight);
		handleAttribute("deltaListMaxHeight", pDeltaListMaxHeight);
	}

	/**
	 * Quantidade de valores dentro deste gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public Integer getItensCount() {
		return (Integer) getStateHelper().eval(PropertyKeys.itensCount, 0);
	}

	/**
	 * Quantidade de gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public void setItensCount(Integer pItensCount) {
		getStateHelper().put(PropertyKeys.itensCount, pItensCount);
		handleAttribute("itensCount", pItensCount);
	}

	/**
	 * Quantidade de graficos.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public Integer getChartValueItensCount() {
		return (Integer) getStateHelper().eval(PropertyKeys.chartValueItensCount, 0);
	}

	/**
	 * Quantidade de valores total dentro deste gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public void setChartValueItensCount(Integer pChartValueItensCount) {
		getStateHelper().put(PropertyKeys.chartValueItensCount, pChartValueItensCount);
		handleAttribute("chartValueItensCount", pChartValueItensCount);
	}
	
	public Integer getNumberOfGridLines() {
		return (Integer) getStateHelper().eval(PropertyKeys.numberOfGridLines, 6);
	}
	public void setNumberOfGridLines(Integer pNumberOfGridLines) {
		getStateHelper().put(PropertyKeys.numberOfGridLines, pNumberOfGridLines);
		handleAttribute("numberOfGridLines", pNumberOfGridLines);
	}

	public Integer getFontSize() {
		return (Integer) getStateHelper().eval(PropertyKeys.fontSize, 11);
	}
	public void setFontSize(Integer pFontSize) {
		getStateHelper().put(PropertyKeys.fontSize, pFontSize);
		handleAttribute("fontSize", pFontSize);
	}
	
	public void setShowLabel(Boolean pShowLabel) {
		getStateHelper().put(PropertyKeys.showLabel, pShowLabel);
		handleAttribute("showLabel", pShowLabel);
//		pvSetChartHeight(getHeight());
	}
	
	public Boolean getShowLabel() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showLabel, true);
	}

	public Double getTotalValue() {
		return DBSNumber.subtract(getMaxValue(), getMinValue()).doubleValue();
	}
	
//	public Integer getPieLabelPadding() {
//		return DBSNumber.toInteger(getFontSize());
//	}


	/**
	 * Retorna posição Y do valor zero.
	 * @return
	 */
	public Integer getZeroPosition(){
		BigDecimal xValue = DBSNumber.multiply(DBSNumber.divide(-getMinValue(), 
											 					getTotalValue()),
											   getChartHeight());
		return xValue.intValue();
	}

	public Integer getChartHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.chartHeight, 0);
	}

	public Integer getChartWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.chartWidth, 0);
	}

	/**
	 * Converte valor px para valor real
	 * @param pPxValue
	 * @return
	 */
	public Double convertYPxToValue(Double pPxValue){
		BigDecimal xValue = DBSNumber.divide(DBSNumber.multiply(pPxValue, 
	 														    getTotalValue()),
											 getChartHeight());
		xValue = DBSNumber.add(xValue, getMinValue());
		return xValue.doubleValue();
	}

	public void setChartWidthAndHeight(TYPE pType){
		// TODO Auto-generated method stub
		pvSetChartWidth(pType, getWidth());
		pvSetChartHeight(pType, getHeight());
	}

	/**
	 * Diametro: Considera o menor valor entre a altura e a largura.
	 * @return
	 */
	public Double getDiameter(){
		return Math.min(getChartWidth().doubleValue(), getChartHeight().doubleValue());
	}

	/**
	 * Centro do gráfico
	 * @return
	 */
	public Point2D getCenter(){
		Double xPadding = PieInternalPadding.doubleValue();
		if (getPadding() != 0){
			xPadding += getPadding().doubleValue();
		}

		return DBSNumber.centerPoint(getDiameter(), getChartHeight() + xPadding);
	}

	/**
	 * Largura de cada gráfico
	 * @return
	 */
	public Double getPieChartWidth(){
		Double xPneuLargura = getPieChartRadius(); //Retira padding principal
		xPneuLargura -= DBSCharts.PieInternalPadding * (getItensCount() - 1 + getPieInternalCircleFator()); //Retira padding de cada pneu
		xPneuLargura /= getItensCount() + getPieInternalCircleFator(); 
		return xPneuLargura;
	}
	
	/**
	 * Raio do círculo
	 * @return
	 */
	public Double getPieChartRadius(){
		return (getDiameter() / 2D);
	}
	
	private void pvSetChartHeight(@SuppressWarnings("unused") TYPE pType, Integer pHeight){
		pHeight -= (getPadding() * 2); //Retira o espaço para o padding do top e bottom
		pHeight -= getTopHeight();
		pHeight -= getBottomHeight();
			
		getStateHelper().put(PropertyKeys.chartHeight, pHeight);
		handleAttribute("chartHeight", pHeight);
	}

	private void pvSetChartWidth(TYPE pType, Integer pWidth){
		Double xWidth = pWidth.doubleValue();
		if (pType.isMatrix()){
			if (getShowGrid() 
			 && getShowGridValue()
			 && getValueFormatMask().length() > 0){
				//Subtrai da largura o comprimento em pixel a partir da mascará de formatação do valor das linhas
				xWidth -= getLabelMaxWidth() * getFontSize() * DBSCharts.LabelsWidth;
			}
		}
		xWidth -= (getPadding() * 2);  //Retira o espaço para o padding do left e right
		getStateHelper().put(PropertyKeys.chartWidth, xWidth.intValue());
		handleAttribute("chartWidth", xWidth);
	}
	
	/**
	 * Espaço no topo do gráfico: Caption principal e Captions dos DBSChart e 
	 * @return
	 */
	public Integer getTopHeight(){
		Double xHeight = 0D;
		if (TYPE.get(getType()) != TYPE.PIE){
			if (getItensCount() > 1){
				//Font em .7 * 1.7(70%)
				xHeight += getFontSize() * DBSCharts.TopicsHeight;
			}
		}

		xHeight += getCaptionHeight();

		return xHeight.intValue();
	}

	
	/**
	 * Espaço embaixo do gráfico: Footer, Labels das colunas e Deltalist
	 * @return
	 */
	public Integer getBottomHeight(){
		Double xHeight = 0D;
		if (TYPE.get(getType()) != TYPE.PIE){
			//DeltaList
			if (getShowDeltaList()){
				//Font em .7 * 1.7(70%)
				xHeight += DBSNumber.toInteger(getFontSize() * DBSCharts.TopicsHeight);
			}
			//Column Labels
			if (getShowLabel()){
				xHeight += getLabelMaxHeight() * getFontSize() * DBSCharts.LabelsHeight;
			}
		}
		xHeight += getFooterHeight();
		return xHeight.intValue();
	}
	
	/**
	 * Altura do cabeçado
	 * @return
	 */
	public Integer getCaptionHeight(){
		if (getCaption() != null){
			//Font em .7 * 1.7(70%)
			return DBSNumber.toInteger(getFontSize() * DBSCharts.TopicsHeight);
		}		
		return 0;
	}
	/**
	 * ALtura do footer
	 * @return
	 */
	public Integer getFooterHeight(){
		if (getFooter() != null){
			//Font em .7 * 1.7(70%)
			return DBSNumber.toInteger(getFontSize() * DBSCharts.TopicsHeight);
		}
		return 0;
	}

}
