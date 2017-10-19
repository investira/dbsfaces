package br.com.dbsoft.ui.component.charts;

import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.chart.IDBSChartDelta;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSCharts.COMPONENT_TYPE)
public class DBSCharts extends DBSUIInput implements NamingContainer{

	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTS;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public final static String FACET_FILTER = "filter";


	protected enum PropertyKeys {
		type,
		caption,
		footer,
		valueDecimalPlaces,
		valuePrefix,
		valueSufix,
		showValue,
		showLabel,
		showDelta,
		groupId,
		pieInternalCircleFator,
		deltaList;

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
		
		public String getStyleClass(){
			return "-" + wName;
		}
		/**
		 * Se é um gráfico de linhas ou colunas(Matrix X x Y)
		 * @return
		 */
		public Boolean isMatrix(){
			return wMatrix;
		}

		public static TYPE get(String pType) {
			if (pType == null){
				return LINE;
			}			
			pType = pType.trim().toLowerCase();
	    	for (TYPE xP:TYPE.values()) {
	    		if (xP.getName().equals(pType)){
	    			return xP;
	    		}
	    	}
	    	return LINE;
		}
	}
	

	public DBSCharts(){
		setRendererType(DBSCharts.RENDERER_TYPE);
    }
	

	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, TYPE.BAR.getName());
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
	
	public Boolean getShowValue() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showValue, true);
	}
	public void setShowValue(Boolean pShowValue) {
		getStateHelper().put(PropertyKeys.showValue, pShowValue);
		handleAttribute("showValue", pShowValue);
	}

	public void setShowLabel(Boolean pShowLabel) {
		getStateHelper().put(PropertyKeys.showLabel, pShowLabel);
		handleAttribute("showLabel", pShowLabel);
	}
	
	public Boolean getShowLabel() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showLabel, true);
	}
	
	public void setDeltaList(List<IDBSChartDelta> pDeltaList) {
		getStateHelper().put(PropertyKeys.deltaList, pDeltaList);
		handleAttribute("deltaList", pDeltaList);
	}

	public Boolean getShowDelta() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showDelta, null);
	}
	public void setShowDelta(Boolean pShowDelta) {
		getStateHelper().put(PropertyKeys.showDelta, pShowDelta);
		handleAttribute("showDelta", pShowDelta);
	} 

	/**
	 * Lista com os valores dos deltas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<IDBSChartDelta> getDeltaList() {
		return (List<IDBSChartDelta>) getStateHelper().eval(PropertyKeys.deltaList, null);
	}

	public Double getPieInternalCircleFator() {
		return (Double) getStateHelper().eval(PropertyKeys.pieInternalCircleFator, 2D);
	}
	public void setPieInternalCircleFator(Double pPieInternalCircleFator) {
		getStateHelper().put(PropertyKeys.pieInternalCircleFator, pPieInternalCircleFator);
		handleAttribute("pieInternalCircleFator", pPieInternalCircleFator);
	}

	public String getValuePrefix() {
		return (String) getStateHelper().eval(PropertyKeys.valuePrefix, null);
	}
	
	public void setValuePrefix(String pValuePrefix) {
		getStateHelper().put(PropertyKeys.valuePrefix, pValuePrefix);
		handleAttribute("valuePrefix", pValuePrefix);
	}
	
	public String getValueSufix() {
		return (String) getStateHelper().eval(PropertyKeys.valueSufix, null);
	}
	
	public void setValueSufix(String pValueSufix) {
		getStateHelper().put(PropertyKeys.valueSufix, pValueSufix);
		handleAttribute("valueSufix", pValueSufix);
	}

	public Integer getValueDecimalPlaces() {
		return (Integer) getStateHelper().eval(PropertyKeys.valueDecimalPlaces, 0);
	}
	public void setValueDecimalPlaces(Integer pValueDecimalPlaces) {
		getStateHelper().put(PropertyKeys.valueDecimalPlaces, pValueDecimalPlaces);
		handleAttribute("valueDecimalPlaces", pValueDecimalPlaces);
	}

	public String getGroupId() {
		return (String) getStateHelper().eval(PropertyKeys.groupId, null);
	}
	public void setGroupId(String pGroupId) {
		getStateHelper().put(PropertyKeys.groupId, pGroupId);
		handleAttribute("groupId", pGroupId);
	}


}
