package br.com.dbsoft.ui.component.inputtextarea;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSInputTextArea.COMPONENT_TYPE)
public class DBSInputTextArea extends DBSUIInputText {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.INPUTTEXTAREA;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		cols,
		rows,
		resize,
		secret;

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
	
    public DBSInputTextArea(){
		setRendererType(DBSInputTextArea.RENDERER_TYPE);
    }
	
	public Integer getCols() {
		return (Integer) getStateHelper().eval(PropertyKeys.cols, 10);
	}
	public void setCols(Integer pCols) {
		getStateHelper().put(PropertyKeys.cols, pCols);
		handleAttribute("cols", pCols);
	}
	
	public Integer getRows() {
		return (Integer) getStateHelper().eval(PropertyKeys.rows, 2);
	}
	public void setRows(Integer pRows) {
		getStateHelper().put(PropertyKeys.rows, pRows);
		handleAttribute("rows", pRows);
	}

	public void setSecret(Boolean pSecret) {
		getStateHelper().put(PropertyKeys.secret, pSecret);
		handleAttribute("secret", pSecret);
	}
	public Boolean getSecret() {
		return (Boolean) getStateHelper().eval(PropertyKeys.secret, false);
	}	
	
	public void setResize(Boolean pResize) {
		getStateHelper().put(PropertyKeys.resize, pResize);
		handleAttribute("resize", pResize);
	}
	public Boolean getResize() {
		return (Boolean) getStateHelper().eval(PropertyKeys.resize, false);
	}	

}