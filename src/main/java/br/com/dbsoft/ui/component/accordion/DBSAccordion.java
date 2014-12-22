package br.com.dbsoft.ui.component.accordion;

import java.util.List;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSAccordion.COMPONENT_TYPE)
public class DBSAccordion extends DBSUIOutput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.ACCORDION;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		accordionSections;

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
    public DBSAccordion(){
		setRendererType(DBSAccordion.RENDERER_TYPE);
    }
	
	
	@SuppressWarnings("unchecked")
	public List<DBSAccordionSection> getAccordionSection() {
		return (List<DBSAccordionSection>) getStateHelper().eval(PropertyKeys.accordionSections, null);
	}
	
	public void setAccordionSection(List<DBSAccordionSection> pAccordionSection) {
		getStateHelper().put(PropertyKeys.accordionSections, pAccordionSection);
		handleAttribute("accordionSection", pAccordionSection);
	}
	
}
