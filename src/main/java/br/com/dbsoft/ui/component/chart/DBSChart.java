package br.com.dbsoft.ui.component.chart;

import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIData;
import br.com.dbsoft.ui.component.charts.DBSCharts;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSColor;
import br.com.dbsoft.util.DBSNumber;


@FacesComponent(DBSChart.COMPONENT_TYPE)
public class DBSChart extends DBSUIData implements ClientBehaviorHolder{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHART;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		color,
		showDelta,
		deltaList,
		caption,
		//Variáveis de trabalho
		savedState,
		itensCount,
		index,
		columnScale,
		totalValue,
		showDeltaList,
		dbscolor;

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
//		setRowStatePreserved(true);
		setRendererType(DBSChart.RENDERER_TYPE);
    }

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}

	public Object getSavedState() {
		return getStateHelper().eval(PropertyKeys.savedState, null);
	}

	public void setSavedState(Object pSavedState) {
		getStateHelper().put(PropertyKeys.savedState, pSavedState);
		handleAttribute("savedState", pSavedState);
	}

	public Boolean getShowDeltaList() {
		//DeltaList só é exibido quando for também exibido os labels e deltas
		return (Boolean) getStateHelper().eval(PropertyKeys.showDeltaList, false);
	}

	public void setShowDeltaList(Boolean pShowDeltaList) {
		getStateHelper().put(PropertyKeys.showDeltaList, pShowDeltaList);
		handleAttribute("showDeltaList", pShowDeltaList);
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

	public void setDeltaList(List<IDBSChartDelta> pDeltaList) {
		getStateHelper().put(PropertyKeys.deltaList, pDeltaList);
		handleAttribute("deltaList", pDeltaList);
	}
	
	/**
	 * Lista com os valores dos deltas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<IDBSChartDelta> getDeltaList() {
		return (List<IDBSChartDelta>) getStateHelper().eval(PropertyKeys.deltaList, null);
	}

	/**
	 * Raio referente o chart conforme index dele
	 * @param pCharts
	 * @param pRelativeWidth
	 * @return
	 */
	public Double getPieChartRelativeRadius(DBSCharts pCharts){
		Double xPieChartWidth  = pCharts.getPieChartWidth();
		Double xRodaRaio = xPieChartWidth;
//		Double xAfastamento = ((xPieChartWidth + DBSCharts.PieInternalPadding) * (pCharts.getItensCount() + pCharts.getPieInternalCircleFator() - getIndex()));
//		Double xAfastamento = ((xPieChartWidth + DBSCharts.PieInternalPadding) * (pCharts.getItensCount() + pCharts.getPieInternalCircleFator() - getIndex()));
		Double xAfastamento = xPieChartWidth + DBSCharts.PieInternalPadding;
		xAfastamento *= pCharts.getItensCount() - 1 + pCharts.getPieInternalCircleFator() - getIndex();
		xRodaRaio += xAfastamento;

		return xRodaRaio;
	}
	
	public String getColor() {
		return (String) getStateHelper().eval(PropertyKeys.color, null);
	}
	public void setColor(String pColor) {
		getStateHelper().put(PropertyKeys.color, pColor);
		handleAttribute("color", pColor);
		pvSetDBSColor(DBSColor.fromString(pColor));
	}

	public DBSColor getDBSColor() {
		return (DBSColor) getStateHelper().eval(PropertyKeys.dbscolor, null);
	}
	private void pvSetDBSColor(DBSColor pDBSColor) {
		getStateHelper().put(PropertyKeys.dbscolor, pDBSColor);
		handleAttribute("dbscolor", pDBSColor);
	}


}
