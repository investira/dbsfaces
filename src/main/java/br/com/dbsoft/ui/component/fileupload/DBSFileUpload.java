package br.com.dbsoft.ui.component.fileupload;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSFileUpload.COMPONENT_TYPE)
public class DBSFileUpload extends DBSUIInput implements NamingContainer{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.FILEUPLOAD;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		fileUploadServletPath,
		multiple;

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

    public DBSFileUpload(){
		setRendererType(DBSFileUpload.RENDERER_TYPE);
    }


	public String getFileUploadServletPath() {
		return (String) getStateHelper().eval(PropertyKeys.fileUploadServletPath, null);
	}


	public void setFileUploadServletPath(String pFileUploadServletPath) {
		getStateHelper().put(PropertyKeys.fileUploadServletPath, pFileUploadServletPath);
	}

	public void setMultiple(Boolean pMultiple) {
		getStateHelper().put(PropertyKeys.multiple, pMultiple);
		handleAttribute("multiple", pMultiple);
	}
	
	public Boolean getMultiple() {
		return (Boolean) getStateHelper().eval(PropertyKeys.multiple, true);
	}
}
