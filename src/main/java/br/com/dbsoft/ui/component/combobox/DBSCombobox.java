package br.com.dbsoft.ui.component.combobox;

import java.util.LinkedHashMap;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.core.DBSSDK.UI.COMBOBOX;
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

	public Integer getSize() {
		return (Integer) getStateHelper().eval(PropertyKeys.size, 0);
	}
	
	public void setSize(Integer pSize) {
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

	@Override
	public void setValue(Object pValue) {
		//Posiciona do primeiro item existente se valor for nulo e não houver valor nulo na lista
		LinkedHashMap<Object, Object> xList = getList();
		if (xList != null 
	     && xList.size() > 0){
			if (pValue != null){
				//Verifica se item esta na lista 
				if (!xList.containsKey(pValue)){
					pValue = null;
				}
			}
			if (pValue == null){
				//Posiciona no primeiro item 
				if (!xList.containsKey(COMBOBOX.NULL_VALUE)){
					super.setValue(xList.entrySet().iterator().next().getKey());
				}else{
					super.setValue(COMBOBOX.NULL_VALUE);
				}
				return;
			}
		}
		super.setValue(pValue);
	}
	
	@Override
	public Object getValue() {
		Object xValue = super.getValue();
		if (xValue == null){
			//Força que o valor atual tenha passado pelo setValue para execute a regra ali contida.
			setValue(xValue);
		}
		return super.getValue();
	}
	
}
