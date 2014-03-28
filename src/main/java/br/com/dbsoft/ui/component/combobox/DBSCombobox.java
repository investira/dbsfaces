package br.com.dbsoft.ui.component.combobox;

import java.util.LinkedHashMap;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSCombobox.COMPONENT_TYPE)
public class DBSCombobox extends DBSUIInput{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.COMBOBOX;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;


	protected enum PropertyKeys {
		size,
		list;

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

    public DBSCombobox(){
		setRendererType(DBSCombobox.RENDERER_TYPE);
    }

	public java.lang.Integer getSize() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.size, 0);
	}
	
	public void setSize(java.lang.Integer pSize) {
		getStateHelper().put(PropertyKeys.size, pSize);
		handleAttribute("size", pSize);
	}

	
	public void setList(LinkedHashMap<Object, Object> pList) {
		getStateHelper().put(PropertyKeys.list, pList);
		handleAttribute("list", pList);
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<Object, Object> getList() {
		LinkedHashMap<Object, Object> xList = (LinkedHashMap<Object, Object>) getStateHelper().eval(PropertyKeys.list, null);
		if (xList == null){
			xList = new LinkedHashMap<Object, Object>();
			setList(xList);
		}
		return xList;
	}



}
