package br.com.dbsoft.ui.component.chartvalue;

import java.awt.geom.Point2D;
import java.io.Serializable;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSChartValue.COMPONENT_TYPE)
public class DBSChartValue extends DBSUIInput implements NamingContainer, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2431823370810712385L;
	/**
	 * 
	 */
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTVALUE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		index,
		fillColor,
		ajustedValue,
		//Variáveis de trabalho
		savedState,
		previousValue,
		point;

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
	
	public Object getSavedState() {
		return getStateHelper().eval(PropertyKeys.savedState, null);
	}

	public void setSavedState(Object pSavedState) {
		getStateHelper().put(PropertyKeys.savedState, pSavedState);
		handleAttribute("savedState", pSavedState);
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

	public String getFillColor() {
		return (String) getStateHelper().eval(PropertyKeys.fillColor, null);
	}
	public void setFillColor(String pFillColor) {
		getStateHelper().put(PropertyKeys.fillColor, pFillColor);
		handleAttribute("fillColor", pFillColor);
	}
	
	public Double getPreviousValue() {
		return DBSNumber.toDouble(getStateHelper().eval(PropertyKeys.previousValue, 0D));
	}
	public void setPreviousValue(Double pPreviousValue) {
		getStateHelper().put(PropertyKeys.previousValue, pPreviousValue);
		handleAttribute("previousValue", pPreviousValue);
	}

	public Point2D getPoint() {
		return (Point2D) getStateHelper().eval(PropertyKeys.point, null);
	}
	public void setPoint(Point2D pPoint) {
		getStateHelper().put(PropertyKeys.point, pPoint);
		handleAttribute("point", pPoint);
	}

	@Override
	public Double getValue() {
		return DBSNumber.toDouble(super.getValue());
	}
	
	public Double getAjustedValue() {
		return (Double) getStateHelper().eval(PropertyKeys.ajustedValue, null);
	}
	public void setAjustedValue(Double pAjustedValue) {
		getStateHelper().put(PropertyKeys.ajustedValue, pAjustedValue);
		handleAttribute("ajustedValue", pAjustedValue);
	}

}
