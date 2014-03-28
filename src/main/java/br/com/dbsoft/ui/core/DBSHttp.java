package br.com.dbsoft.ui.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import br.com.dbsoft.util.DBSObject;

public class DBSHttp {

	private static Logger wLogger = Logger.getLogger(DBSHttp.class);

	/**
	 * Retorna o response da conexão web atual
	 * @return
	 */
	public static HttpServletResponse getHttpServletResponse(){ 		
		FacesContext xContext = FacesContext.getCurrentInstance();
		return (HttpServletResponse) xContext.getExternalContext().getResponse();
	}

	public static HttpServletRequest getHttpServletRequest(){ 		
		FacesContext xContext = FacesContext.getCurrentInstance();
		return (HttpServletRequest) xContext.getExternalContext().getRequest();
	}
	
	/**
	 * Evia um arquivo local para o browser
	 */
	public static Boolean sendFile(ByteArrayOutputStream pByteArrayOutputStream, String pRemoteFileName, String pContentType){
		FacesContext 	xFC = FacesContext.getCurrentInstance();
		ExternalContext xEC = xFC.getExternalContext();
		
		//Verifica se arquivo existe. Se não existir...
		try {
			//Se o arquivo já existir apenas entrega-o para o browser
			if (!DBSObject.isEmpty(pByteArrayOutputStream)) {
				xEC.responseReset();
				xEC.setResponseContentType(pContentType);
				xEC.setResponseContentLength(pByteArrayOutputStream.size());
				xEC.setResponseHeader("Content-Disposition", "attachment;filename=\"" + pRemoteFileName + "\"");
				OutputStream xOutputStream = xEC.getResponseOutputStream();
				if (xOutputStream != null){
					pByteArrayOutputStream.writeTo(xOutputStream);
				}
				xFC.responseComplete();
				return true;
			}
			return false;
		} catch (IOException e) {
			wLogger.error(e);
			return false;
		}			
	}
	
	public static String getResourcePath(){
		return getRealPath("WEB-INF" + File.separator + "classes" + File.separator);
	}
	
	public static String getRealPath(String pRelativePath){
		return FacesContext.getCurrentInstance().getExternalContext().getRealPath(pRelativePath);
	}

}


///**
// * Evia um arquivo local para o browser
// * @param pLocalFilePath Nome do arquivo contendo o caminho completo
// * @return
// */
//public static Boolean sendFile(String pLocalFilePath){
//	return sendFile(pLocalFilePath, "");
//}
//
///**
// * Evia um arquivo local para o browser
// * @param pLocalFilePath Nome do arquivo contendo o caminho completo
// * @param pRemoteFileName Nome simples do arquivo que será utilizado remotamente.
// * @return
// */
//public static Boolean sendFile(String pLocalFilePath, String pRemoteFileName){
//	String			xRemoteFileName = pRemoteFileName; 
//	FacesContext 	xFC = FacesContext.getCurrentInstance();
//	ExternalContext xEC = xFC.getExternalContext();
//	
//	InputStream 	xReportInputStream = null;
//	File			xFile = new File(pLocalFilePath);
//	
//	//Verifica se arquivo existe. Se não existir...
//	try {
//		if (DBSFile.exists(pLocalFilePath)){
//			xReportInputStream = new FileInputStream(xFile);
//		}
//		//Se o arquivo já existir apenas entrega-o para o browser
//		if (!DBSObject.isNull(xReportInputStream)) {
//			if (xRemoteFileName.equals("")){
//				xRemoteFileName = xFile.getName();
//			}
//			xEC.responseReset();
//			xEC.setResponseContentType(xEC.getMimeType(pLocalFilePath));
//			xEC.setResponseContentLength((int) xFile.length());
//			xEC.setResponseHeader("Content-Disposition", "attachment;filename=\"" + xRemoteFileName + "\"");
//			OutputStream xOutputStream = xEC.getResponseOutputStream();
//			if (xOutputStream != null){
//				IOUtils.copy(xReportInputStream, xOutputStream);
//			}
//			xFC.responseComplete();
//			return true;
//		}
//		return false;
//	} catch (IOException e) {
//		wLogger.error(e);
//		return false;
//	}		
//}
//// create new session
//((HttpServletRequest) ec.getRequest()).getSession(true);
// 
//// restore last used user settings because login / logout pages reference "userSettings"
//FacesAccessor.setValue2ValueExpression(userSettings, "#{userSettings}");
// 
//// redirect to the specified logout page
//ec.redirect(ec.getRequestContextPath() + "/views/logout.jsf");

