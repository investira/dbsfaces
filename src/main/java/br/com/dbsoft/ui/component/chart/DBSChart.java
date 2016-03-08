package br.com.dbsoft.ui.component.chart;

import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIData;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;


@FacesComponent(DBSChart.COMPONENT_TYPE)
public class DBSChart extends DBSUIData implements ClientBehaviorHolder{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHART;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
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
		colorHue,
		colorBrightness,
		showDelta,
		//Variáveis de trabalho
		savedState,
		itensCount,
		index,
		columnScale,
		totalValue;

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

	public DBSChart(){
		setRendererType(DBSChart.RENDERER_TYPE);
    }
	
	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, null);
	}
	
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}

	public Object getSavedState() {
		return getStateHelper().eval(PropertyKeys.savedState, null);
	}

	public void setSavedState(Object pSavedState) {
		getStateHelper().put(PropertyKeys.savedState, pSavedState);
		handleAttribute("savedState", pSavedState);
	}

	public Double getTotalValue() {
		return DBSNumber.toDouble(getStateHelper().eval(PropertyKeys.totalValue, 0D));
	}
	public void setTotalValue(Double pTotalValue) {
		getStateHelper().put(PropertyKeys.totalValue, pTotalValue);
		handleAttribute("totalValue", pTotalValue);
	}

	/**
	 * Indice que identifica este gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public Integer getIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.index, 1);
	}
	/**
	 * Indice que identifica este gráfico.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public void setIndex(Integer pIndex) {
		getStateHelper().put(PropertyKeys.index, pIndex);
		handleAttribute("index", pIndex);
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
	
	public Double getColumnScale() {
		return (Double) getStateHelper().eval(PropertyKeys.columnScale, 0D);
	}
	public void setColumnScale(Double pColumnScale) {
		getStateHelper().put(PropertyKeys.columnScale, pColumnScale);
		handleAttribute("columnScale", pColumnScale);
	}

	public void setShowDelta(Boolean pShowDelta) {
		getStateHelper().put(PropertyKeys.showDelta, pShowDelta);
		handleAttribute("showDelta", pShowDelta);
	}
	
	public Boolean getShowDelta() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showDelta, true);
	}

	
	public Float getColorHue() {
		return (Float) getStateHelper().eval(PropertyKeys.colorHue, null);
	}
	public void setColorHue(Float pColorHue) {
		if (pColorHue != null){
			if (pColorHue > 1
			 || pColorHue < 0){
				pColorHue = null;
			}
		}
		getStateHelper().put(PropertyKeys.colorHue, pColorHue);
		handleAttribute("colorHue", pColorHue);
	}
	public Float getColorBrightness() {
		return (Float) getStateHelper().eval(PropertyKeys.colorBrightness, null);
	}
	public void setColorBrightness(Float pColorBrightness) {
		if (pColorBrightness != null){
			if (pColorBrightness > 1
			 || pColorBrightness < 0){
				pColorBrightness = null;
			}
		}
		getStateHelper().put(PropertyKeys.colorBrightness, pColorBrightness);
		handleAttribute("colorBrightness", pColorBrightness);
	}

}
