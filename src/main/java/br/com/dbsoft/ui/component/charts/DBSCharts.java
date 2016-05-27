package br.com.dbsoft.ui.component.charts;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.chart.DBSChart.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSCharts.COMPONENT_TYPE)
public class DBSCharts extends DBSUIInput implements NamingContainer{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTS;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	public static Integer 	FontSize = 8;
	public static Integer 	PieInternalPadding = 2;
	public static Integer 	PieLabelPadding = DBSNumber.toInteger(FontSize * 1.5);


	
	protected enum PropertyKeys {
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

		//Variáveis de trabalho
		maxValue,
		minValue,
		rowScale,
		chartWidth,
		chartHeight,
		itensCount,
		numberOfGridLines,
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
		if (this.getShowDelta() 
		 && this.getShowLabel()){
			return (Boolean) getStateHelper().eval(PropertyKeys.showDeltaList, false);
		}
		return false;
	}
	public void setShowDeltaList(Boolean pShowDeltaList) {
		getStateHelper().put(PropertyKeys.showDeltaList, pShowDeltaList);
		handleAttribute("showDeltaList", pShowDeltaList);
	} 
	
	public Integer getHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.height, 50);
	}
	public void setHeight(Integer pHeight) {
		getStateHelper().put(PropertyKeys.height, pHeight);
		handleAttribute("hight", pHeight);
	}

	public Integer getWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.width, 50);
	}
	public void setWidth(Integer pWidth) {
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

	/**
	 * Quantidade de valores dentro deste gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public Integer getItensCount() {
		return (Integer) getStateHelper().eval(PropertyKeys.itensCount, 0);
	}

	/**
	 * Quantidade de valores dentro deste gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public void setItensCount(Integer pItensCount) {
		getStateHelper().put(PropertyKeys.itensCount, pItensCount);
		handleAttribute("itensCount", pItensCount);
	}

	
	public Integer getNumberOfGridLines() {
		return (Integer) getStateHelper().eval(PropertyKeys.numberOfGridLines, 6);
	}
	public void setNumberOfGridLines(Integer pNumberOfGridLines) {
		getStateHelper().put(PropertyKeys.numberOfGridLines, pNumberOfGridLines);
		handleAttribute("numberOfGridLines", pNumberOfGridLines);
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

	public void setChartWidthHeight(TYPE pType){
		// TODO Auto-generated method stub
		pvSetChartWidth(pType, getWidth());
		pvSetChartHeight(pType, getHeight());
	}

	/**
	 * Diametro: Considera o menor valor entre a altura e a largura.
	 * @return
	 */
	public Double getDiameter(){
		if (getChartWidth().doubleValue() < getChartHeight().doubleValue()){
			return getChartWidth().doubleValue();
		}else{
			return getChartHeight().doubleValue();
		}
	}

	/**
	 * Centro do gráfico
	 * @return
	 */
	public Point2D getCenter(){
		Double xPadding = 0D;
		if (getPadding() != 0){
			xPadding = getPadding().doubleValue() / 2;
		}

		return DBSNumber.centerPoint(getChartWidth() + xPadding, getChartHeight() + xPadding);
	}

	/**
	 * Largura de cada gráfico
	 * @return
	 */
	public Double getPieChartWidth(){
		Double xPneuLargura = getPieChartRadius() - DBSCharts.PieLabelPadding; //Retirna padding principal
		xPneuLargura -= (DBSCharts.PieInternalPadding * (getItensCount() - 1)); //Retina padding de cada pneu
		xPneuLargura /= (getItensCount() + 0.5); // 0.5 = metade da roda
		return xPneuLargura;
	}
	
//	xPneuLargura = xDiametro - xLabelPadding; //Retirna padding principal
//	xPneuLargura -= (xPneuInternalPadding * (pCharts.getItensCount() - 1)); //Retina padding de cada pneu
//	xPneuLargura /= (pCharts.getItensCount() + 0.5); // 0.5 = metado da roda
	
	/**
	 * Raio do círculo
	 * @return
	 */
	public Double getPieChartRadius(){
		return (getDiameter() / 2) - getPadding();
	}
	
	private void pvSetChartHeight(TYPE pType, Integer pHeight){
		if (pType.isMatrix()){
			if (getShowLabel()){
				//Subtrai da altura do a altura do texto da coluna 
				pHeight -= DBSCharts.FontSize;
			}
		}
		pHeight -= (getPadding() * 2); //Retira o espaço para o padding do top e bottom
		getStateHelper().put(PropertyKeys.chartHeight, pHeight);
		handleAttribute("chartHeight", pHeight);
	}

	private void pvSetChartWidth(TYPE pType, Integer pWidth){
		if (pType.isMatrix()){
			if (getShowGrid() 
			 && getShowGridValue()
			 && getValueFormatMask().length() > 0){
				//Subtrai da largura o comprimento em pixel a partir da mascará de formatação do valor das linhas
				pWidth -= DBSNumber.multiply(getValueFormatMask().length(), 6D).intValue();
			}
		}
		pWidth -= (getPadding() * 2);  //Retira o espaço para o padding do left e right
		getStateHelper().put(PropertyKeys.chartWidth, pWidth);
		handleAttribute("chartWidth", pWidth);
	}
}
