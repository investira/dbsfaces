package br.com.dbsoft.ui.component.chartvalue;

import java.io.Serializable;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSChartValue.COMPONENT_TYPE)
public class DBSChartValue extends DBSUIInput implements Serializable, NamingContainer {
	
	private static final long serialVersionUID = 2431823370810712385L;
	/**
	 * 
	 */
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTVALUE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		index,
		color,
		displayValue;

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

	public DBSChartValue(){
		setRendererType(DBSChartValue.RENDERER_TYPE);
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
	 * Indice que identifica este valor considerando todos os gráficos.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */

	public String getColor() {
		return (String) getStateHelper().eval(PropertyKeys.color, null);
	}
	public void setColor(String pColor) {
		getStateHelper().put(PropertyKeys.color, pColor);
		handleAttribute("color", pColor);
//		setDBSColor(DBSColor.fromString(pColor));
	}


	@Override
	public Double getValue() {
		return DBSNumber.toDouble(super.getValue());
	}

	public Double getDisplayValue() {
		return (Double) getStateHelper().eval(PropertyKeys.displayValue, null);
	}

	public void setDisplayValue(Double pDisplayValue) {
		getStateHelper().put(PropertyKeys.displayValue, pDisplayValue);
		handleAttribute("displayValue", pDisplayValue);
	}


}
