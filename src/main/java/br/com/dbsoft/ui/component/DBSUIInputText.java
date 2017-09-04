package br.com.dbsoft.ui.component;


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
	
	public Integer getMaxLength() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxlength, 0);
	}
	
	public void setMaxLength(Integer pmaxlength) {
		getStateHelper().put(PropertyKeys.maxlength, pmaxlength);
		handleAttribute("maxlength", pmaxlength);
	}
}
