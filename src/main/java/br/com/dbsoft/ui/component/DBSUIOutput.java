package br.com.dbsoft.ui.component;


import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIOutput;

import br.com.dbsoft.ui.core.DBSFaces;

@ResourceDependencies({
	// Estas libraries serão carregadas junto com o projeto
	@ResourceDependency(library = "css", name = "dbsfaces.min.css", target = "head"),
	@ResourceDependency(library = "js", name = "jquery-3.1.1.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "jquery.actual.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "js.cookie.js", target = "head"),
	@ResourceDependency(library = "js", name = "tinycolor.js", target = "head"),
	@ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces.min.js", target = "head")
//	@ResourceDependency(library = "js", name = "eventsource.js", target = "head")
})

public abstract class DBSUIOutput extends UIOutput implements IDBSUIComponentBase{

	protected enum PropertyKeys {
		style,
		styleClass;

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

	@Override
	public String getFamily() {
		return DBSFaces.FAMILY;
	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}

	@Override
	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}

	public String getValueExpressionString(String pPropertyKeysName){
		return this.getValueExpression(pPropertyKeysName).getExpressionString();
	}
	
}
