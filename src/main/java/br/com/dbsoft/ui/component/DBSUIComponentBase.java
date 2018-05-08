package br.com.dbsoft.ui.component;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;

import br.com.dbsoft.ui.core.DBSFaces;


@ResourceDependencies({
	// Estas libraries serão carregadas junto com o projeto
	@ResourceDependency(library = "css", name = "dbsfaces.min.css", target = "head"),
	@ResourceDependency(library = "js", name = "jquery-3.1.1.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "jquery.actual.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "js.cookie.js", target = "head"),
	@ResourceDependency(library = "js", name = "tinycolor.js", target = "head"),
	@ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "eventsource.js", target = "head") //<- Deve ser habilitado antes de entrar em produção para funcionar SSE em todos os navegadores
})	


public abstract class DBSUIComponentBase extends UIComponentBase implements IDBSUIComponentBase  {
	
	@Override
	public String getFamily() {
		return DBSFaces.FAMILY;
	}
	
	@Override
	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}
	
}
