package br.com.dbsoft.ui.component.charts;

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
	public static final Integer FontSize = 8;
	
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

		//Variáveis de trabalho
		maxValue,
		minValue,
		rowScale,
		chartWidth,
		chartHeight,
		itensCount,
		numberOfGridLines;

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
//		FacesContext xContext = FacesContext.getCurrentInstance();
//		xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class,this);
//		xContext.getViewRoot().subscribeToViewEvent(PreRenderComponentEvent.class,this);
    }
	
//	@Override
//	public void processEvent(SystemEvent event) throws AbortProcessingException {
//		DBSFaces.initializeChartsValues(this);
//	}
//	
//	@Override
//	public boolean isListenerForSource(Object pSource) {
//		return pSource.equals(this);
//	}

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
	public Double getZeroPosition(){
		BigDecimal xValue = DBSNumber.multiply(DBSNumber.divide(-getMinValue(), 
											 					getTotalValue()),
											   getChartHeight());
		return xValue.doubleValue();
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
