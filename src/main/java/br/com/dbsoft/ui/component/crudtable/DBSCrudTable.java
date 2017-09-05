package br.com.dbsoft.ui.component.crudtable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.datatable.DBSDataTable;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSCrudTable.COMPONENT_TYPE)
public class DBSCrudTable extends DBSUIComponentBase implements NamingContainer, ClientBehaviorHolder { 


	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CRUDTABLE;
	public final static String RENDERER_TYPE = "/resources/component/crudTable.xhtml";

	private DBSDataTable wDataTable;

	protected enum PropertyKeys {
		crudBean,
		insertSelected,
		update;
		
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
	
	public DBSCrudTable(){
		setRendererType(DBSCrudTable.RENDERER_TYPE);
	}
	
    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	public DBSCrudOldBean getCrudBean() {
		return (DBSCrudOldBean) getStateHelper().eval(PropertyKeys.crudBean, null);
	}


	public void setCrudBean(DBSCrudOldBean pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
		//handleAttribute("crudBean", pCrudBean);
	}

	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, "");
	}

	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}

	public Boolean getInsertSelected() {
		return (Boolean) getStateHelper().eval(PropertyKeys.insertSelected, false);
	}

	public void setInsertSelected(Boolean pInsertSelected) {
		getStateHelper().put(PropertyKeys.insertSelected, pInsertSelected);
		handleAttribute("insertSelected", pInsertSelected);
	}

	public ValueExpression viewOneAction() {
		FacesContext xContext = FacesContext.getCurrentInstance();
		String		 xMethodEL = "";
		if (getInsertSelected()){
			xMethodEL = "cc.crudBean.insertSelected()";
		}else{
			xMethodEL = "cc.crudBean.view()";
		}
		return DBSFaces.createValueExpression(xContext, xMethodEL, String.class);
	}
	
	/**
	 * DataTable vinculado a este crudtable
	 * @return
	 */
	public DBSDataTable getDataTable(){
		return wDataTable;
	}
	/**
	 * DataTable vinculado a este crudtable
	 * @return
	 */
	public void setDataTable(DBSDataTable pDataTable){
		wDataTable = pDataTable;
	}

	/**
	 * Move os filhos do crudTable, que normalmente são as colunas definidas pelo usuário,  para dentro do dataTable
	 */
	public void insertChildren(){
		wDataTable.getChildren().addAll(this.getChildren());
//		System.out.println("clear");
//		wDataTable.getFacets().putAll(this.getFacets());
	}
	
	private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("change", "click", "blur", "dblclick","keydown", "keypress", "keyup", "mousedown", "mousemove","mouseout", "mouseover", "mouseup", "select"));

	@Override
	public Collection<String> getEventNames() {
		return EVENT_NAMES;
	}

	@Override
	public String getDefaultEventName() {
		return "select";
	}
}