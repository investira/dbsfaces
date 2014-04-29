package br.com.dbsoft.ui.component;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;

import br.com.dbsoft.ui.core.DBSFaces;


@ResourceDependencies({
	// Estas libraries serão carregadas junto com o projeto
	@ResourceDependency(library = "css", name = "dbsfaces.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_accordion.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_button.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_calendar.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_combobox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_componenttree.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudform.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudtable.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_datatable.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_group.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_dialog.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputtext.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputtextarea.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputdate.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputnumber.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputmask.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_checkbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_messagelist.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_menu.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_progress.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_reportform.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_report.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_tab.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_table.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_tooltip.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_radio.css", target = "head"),
	@ResourceDependency(library = "js", name = "jquery-1.11.0.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsmask.js", target = "head"),
	@ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_accordion.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_button.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_calendar.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_checkbox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_componenttree.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_combobox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudform.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudtable.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_datatable.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_dialog.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_group.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputtext.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputtextarea.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputdate.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputnumber.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputmask.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_menu.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_messagelist.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_progress.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_push.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_radio.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_reportform.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_report.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tab.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tooltip.js", target = "head"),
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
	
	
//	public void handleAttribute(String name, Object value) {
//        @SuppressWarnings("unchecked")
//		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
//        if (setAttributes == null) {
//            String cname = this.getClass().getName();
//            if (cname != null && cname.startsWith(DBSFaces.OPTIMIZED_PACKAGE)) {
//                setAttributes = new ArrayList<String>(6);
//                this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
//            }
//        }
//        if (setAttributes != null) {
//            if (value == null) {
//                ValueExpression ve = getValueExpression(name);
//                if (ve == null) {
//                    setAttributes.remove(name);
//                }
//            } else if (!setAttributes.contains(name)) {
//                setAttributes.add(name);
//            }
//        }
//    }
	
	
//	public static Object getManagedBean(final String beanName) {
//		    FacesContext fc = FacesContext.getCurrentInstance();
//		    Object bean;
//		    try {
//		        ELContext elContext = fc.getELContext();
//		        bean = elContext.getELResolver().getValue(elContext, null, beanName);
//		    } catch (RuntimeException e) {
//		        throw new FacesException(e.getMessage(), e);
//		    }
//		    if (bean == null) {
//		        throw new FacesException("Managed bean with name '" + beanName
//		            + "' was not found. Check your faces-config.xml or @ManagedBean annotation.");
//		    }
//		    return bean;
//		}

	
//	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//	HttpSession session = (HttpSession) ec.getSession(false);
//	session.invalidate();
//	 
//	// create new session
//	((HttpServletRequest) ec.getRequest()).getSession(true);
//	 
//	// restore last used user settings because login / logout pages reference "userSettings"
//	FacesAccessor.setValue2ValueExpression(userSettings, "#{userSettings}");
//	 
//	// redirect to the specified logout page
//	ec.redirect(ec.getRequestContextPath() + "/views/logout.jsf");


}
