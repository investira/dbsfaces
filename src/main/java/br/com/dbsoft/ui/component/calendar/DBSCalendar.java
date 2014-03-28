package br.com.dbsoft.ui.component.calendar;

import java.sql.Date;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSDate;


@FacesComponent(DBSCalendar.COMPONENT_TYPE)
public class DBSCalendar extends DBSUIInput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CALENDAR;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		autocomplete,
		maxDate,
		minDate;

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
	
    public DBSCalendar(){
		setRendererType(DBSCalendar.RENDERER_TYPE);
    }

	public void setAutocomplete(String pAutocomplete) {
		getStateHelper().put(PropertyKeys.autocomplete, pAutocomplete);
		handleAttribute("autocomplete", pAutocomplete);
	}
	public String getAutocomplete() {
		return (String) getStateHelper().eval(PropertyKeys.autocomplete, "off");
	}	

	public Date getMinDate() {
		return (Date) getStateHelper().eval(PropertyKeys.minDate, null);
	}
	public void setMinDate(Date pMinDate) {
		getStateHelper().put(PropertyKeys.minDate, pMinDate);
		handleAttribute("minDate", pMinDate);
	}

	public Date getMaxDate() {
		return (Date) getStateHelper().eval(PropertyKeys.maxDate, null);
	}
	public void setMaxDate(Date pMaxDate) {
		getStateHelper().put(PropertyKeys.maxDate, pMaxDate);
		handleAttribute("maxDate", pMaxDate);
	}
	
	@Override
	public Object getValue(){
		if (super.getValue() == null){
			return DBSDate.getNowDate();
		}else{
			return super.getValue();
		}
	}
	
}
