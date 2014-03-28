package br.com.dbsoft.ui.component.listbox;

import java.util.Map;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSListbox.COMPONENT_TYPE)
public class DBSListbox extends DBSUIInput{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.LISTBOX;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		size,
		lines,
		update,
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

    public DBSListbox(){
		setRendererType(DBSListbox.RENDERER_TYPE);
    }

 
	public Integer getSize() {
		return (Integer) getStateHelper().eval(PropertyKeys.size, 0);
	}
	
	public void setSize(Integer pSize) {
		getStateHelper().put(PropertyKeys.size, pSize);
		handleAttribute("size", pSize);
	}

	public String getLines() {
		return (String) getStateHelper().eval(PropertyKeys.lines, "3");
	}
	
	public void setLines(String pLines) {
		getStateHelper().put(PropertyKeys.size, pLines);
		handleAttribute("lines", pLines);
	}

	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}
	
	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, null);
	}

	
	public void setList(Map<String, Object> pList) {
		getStateHelper().put(PropertyKeys.list, pList);
		handleAttribute("list", pList);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getList() {
		return (Map<String, Object>) getStateHelper().eval(PropertyKeys.list, null);
	}



}
