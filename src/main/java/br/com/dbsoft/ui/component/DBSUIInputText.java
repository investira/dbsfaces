package br.com.dbsoft.ui.component;

import br.com.dbsoft.ui.core.DBSFaces;

public abstract class DBSUIInputText extends DBSUIInput {
	
	protected enum PropertyKeys {
		maxlength;

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
	
	public java.lang.Integer getMaxLength() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxlength, 0);
	}
	
	public void setMaxLength(java.lang.Integer pmaxlength) {
		getStateHelper().put(PropertyKeys.maxlength, pmaxlength);
		handleAttribute("maxlength", pmaxlength);
	}
}
