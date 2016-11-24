package br.com.dbsoft.ui.component.chartvalue;

import java.awt.geom.Point2D;
import java.io.Serializable;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSColor;
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
		displayValue,
		//Variáveis de trabalho
		savedState,
		previousValue,
		dbscolor,
		point,
		globalIndex;

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

	/**
	 * Indice que identifica este valor considerando todos os gráficos.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public Integer getGlobalIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.globalIndex, 0);
	}
	/**
	 * Indice que identifica este valor considerando todos os gráficos.
	 * Indice é gerado automaticamente no DBSCharts
	 * @return
	 */
	public void setGlobalIndex(Integer pGlobalIndex) {
		getStateHelper().put(PropertyKeys.globalIndex, pGlobalIndex);
		handleAttribute("globalIndex", pGlobalIndex);
	}

	public String getColor() {
		return (String) getStateHelper().eval(PropertyKeys.color, null);
	}
	public void setColor(String pColor) {
		getStateHelper().put(PropertyKeys.color, pColor);
		handleAttribute("color", pColor);
		setDBSColor(DBSColor.fromString(pColor));
	}

	public DBSColor getDBSColor() {
		return (DBSColor) getStateHelper().eval(PropertyKeys.dbscolor, null);
	}
	public void setDBSColor(DBSColor pDBSColor) {
		getStateHelper().put(PropertyKeys.dbscolor, pDBSColor);
		handleAttribute("dbscolor", pDBSColor);
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

	public Double getDisplayValue() {
		return (Double) getStateHelper().eval(PropertyKeys.displayValue, null);
	}

	public void setDisplayValue(Double pDisplayValue) {
		getStateHelper().put(PropertyKeys.displayValue, pDisplayValue);
		handleAttribute("displayValue", pDisplayValue);
	}
//	@Override
//	public void restoreState(FacesContext context) {
//	    Object[] rtrn = new Object[2];
//	    rtrn[0] = super.saveState(context);
//	    rtrn[1] = "dummy";
//	}

//	@Override
//	public void restoreState(FacesContext pContext, Object pState) {
//	    Object rtrn[] = (Object[])pState;
//	    super.restoreState(pContext, rtrn[0]);
////	    rtrn[0] = super.saveState(context);
////	    rtrn[1] = "dummy";
//		
//	}
//	
//	@Override
//	public Object saveState(FacesContext context) {
//	    Object[] rtrn = new Object[2];
//	    rtrn[0] = super.saveState(context);
//	    rtrn[1] = "dummy";
//	    return rtrn;
//	}

}
