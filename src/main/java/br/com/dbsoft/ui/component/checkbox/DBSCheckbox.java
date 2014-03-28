package br.com.dbsoft.ui.component.checkbox;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSCheckbox.COMPONENT_TYPE)
public class DBSCheckbox extends DBSUIInput{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHECKBOX;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		update,
		execute,
		invertLabel;

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

    public DBSCheckbox(){
		setRendererType(DBSCheckbox.RENDERER_TYPE);
    }


//	@Override
//	public Object getValue() {
//		return DBSBoolean.toBoolean(super.getValue());
//	}
//
//	@Override
//	public void setValue(Object pValue) {
//		System.out.println(pValue);
//		super.setValue(DBSBoolean.toBoolean(pValue));
//	}
    /**
     * <p>Return the local value of the selected state of this component.
     * This method is a typesafe alias for <code>getValue()</code>.</p>
     */
    public boolean isSelected() {
        Boolean value = (Boolean) getValue();
        if (value != null) {
            return (value.booleanValue());
        } else {
            return (false);
        }

    }

     /**
     * <p>Set the local value of the selected state of this component.
     * This method is a typesafe alias for <code>setValue()</code>.</p>
     *
     * @param selected The new selected state
     */
    public void setSelected(boolean selected) {

        if (selected) {
            setValue(Boolean.TRUE);
        } else {
            setValue(Boolean.FALSE);
        }

    }


    // ---------------------------------------------------------------- Bindings


    /**
     * <p>Return any {@link ValueExpression} set for <code>value</code>
     * if a {@link ValueExpression} for <code>selected</code> is
     * requested; otherwise, perform the default superclass processing
     * for this method.</p>
     *
     * @param name Name of the attribute or property for which to retrieve
     *  a {@link ValueExpression}
     *
     * @throws NullPointerException if <code>name</code>
     *  is <code>null</code>
     * @since 1.2
     */
    @Override
	public ValueExpression getValueExpression(String name) {

        if ("selected".equals(name)) {
            return (super.getValueExpression("value"));
        } else {
            return (super.getValueExpression(name));
        }

    }
    
    /**
     * <p>Store any {@link ValueExpression} specified for <code>selected</code>
     * under <code>value</code> instead; otherwise, perform the default
     * superclass processing for this method.</p>
     *
     * @param name Name of the attribute or property for which to set
     *  a {@link ValueExpression}
     * @param binding The {@link ValueExpression} to set, or <code>null</code>
     *  to remove any currently set {@link ValueExpression}
     *
     * @throws NullPointerException if <code>name</code>
     *  is <code>null</code>
     * @since 1.2
     */
    @Override
	public void setValueExpression(String name, ValueExpression binding) {

        if ("selected".equals(name)) {
            super.setValueExpression("value", binding);
        } else {
            super.setValueExpression(name, binding);
        }

    }

	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}
	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, null);
	}

	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}
	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, "");
	}
	
	
	public void setInvertLabel(Boolean pInvertLabel) {
		getStateHelper().put(PropertyKeys.invertLabel, pInvertLabel);
		handleAttribute("invertLabel", pInvertLabel);
	}
	
	public Boolean getInvertLabel() {
		return (Boolean) getStateHelper().eval(PropertyKeys.invertLabel, false);
	}

}
