package br.com.dbsoft.ui.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.util.DBSFile;
import br.com.dbsoft.util.DBSIO;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


/**
 * @author ricardo.villar
 * Deve-se configura a servlet que herdará esta, com as anotações abaixo.<br/>
 * <b>MultipartConfig</b><br/>
 * <b>WebServlet(value='caminho e nome da servlet')</b><br/>
 */
public abstract class DBSFileUploadServlet extends HttpServlet{

	private static final long serialVersionUID = 1063600676650691271L;

	protected Logger			wLogger = Logger.getLogger(this.getClass());
	
	private List<IDBSFileUploadServletEventsListener>	wEventListeners = new ArrayList<IDBSFileUploadServletEventsListener>();

	private String wLocalPath = "";
	private String wFileName = "";
	
	@PreDestroy
	private void finalizeClass(){
		wEventListeners.clear();
	}

	/**
	 * Classe que receberá as chamadas dos eventos quando ocorrerem.<br/>
	 * Para isso, classe deverá implementar a interface IDBSFileUploadServletEventsListener.<br/>
	 * Lembre-se de remove-la utilizando removeEventListener quando a classe for destruida, para evitar que ela seja chamada quando já não deveria. 
	 * @param pEventListener Classe
	 */
	public void addEventListener(IDBSFileUploadServletEventsListener pEventListener) {
		if (!wEventListeners.contains(pEventListener)){
			wEventListeners.add(pEventListener);
		}
	}

	
	public void removeEventListener(IDBSFileUploadServletEventsListener pEventListener) {
		if (wEventListeners.contains(pEventListener)){
			wEventListeners.remove(pEventListener);
		}
	}	
	
    /**
	 * Caminho pasta local onde o arquivo recebido será salvo.
     * @return
     */
    public String getLocalPath() {return wLocalPath;}

	/**
	 * Caminho pasta local onde o arquivo recebido será salvo.
	 * @param pLocalPath
	 */
	public void setLocalPath(String pLocalPath) {
		wLocalPath = DBSFile.getPathFromFolderName(pLocalPath);
	}
	
	// ========================================================================================================
	@Override
    protected void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) {
 		try {
 		   //Dispara evento 
 	       if (!pvFireEventBeforeUpload()
	         || DBSObject.isEmpty(getLocalPath())){
	        	return;
	        }
			for (Part xPart : pRequest.getParts()) {
			    wFileName = "";
			    for (String xS : xPart.getHeader("content-disposition").split(";")) {
			        if (xS.trim().startsWith("filename")) {
			        	wFileName = xS.split("=")[1].replaceAll("\"", "");
			        }
			    }
			    //Dispara evento 
			    if (pvFireEventBeforeSave()){
			        if (!DBSObject.isEmpty(wFileName)){
			        	//Verifica se a pasta existe
			        	if (!DBSFile.exists(wLocalPath+wFileName)) {
			        		//Cria a pasta caso ela não exista
			        		String xAbsolutePath = wLocalPath+wFileName;
			        		DBSFile.mkDir(DBSString.getSubString(xAbsolutePath, 1, xAbsolutePath.lastIndexOf(File.separator)));
			        	}
			            xPart.write(wLocalPath + wFileName);
			            //Dispara evento 
			            pvFireEventAfterSave();
			        }
			    }
			}
			//Dispara evento 
			pvFireEventAfterUpload();
		} catch (Exception e) { //java.io.FileNotFoundException
			try {
//				System.out.println("EXCEPTION:" + e.getMessage());
				pResponse.getWriter().print(e.getMessage());
				pResponse.flushBuffer();
				pResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				DBSIO.throwIOException(e);
			} catch (DBSIOException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	//Eventos locais=================================================================
	/**
	 * Evento ocorre antes de iniciar o upload.<br/>
	 * Este evento ocorre somente uma vez, mesmo que esteja sendo efetuado o upload de diversos arquivos.<br/>
	 * Para tratar arquivos individualmente, utilize o evento <b>beforeSave</b>.<br/>  
	 * Para impedir o upload, deve-se setar <b>setOk(False)</b>.
	 * Neste evento deve-se configurar o caminho local onde o arquivo será salvo utilizando o método <b>setLocalPath</b>, 
	 * bem como os listners ser houver.<br/>
	 */
	protected abstract void beforeUpload(DBSFileUploadServletEvent pEvent) throws DBSIOException;
	
	/**
	 * Evento ocorre após finializado o upload.<br/>
	 * @param pEvent 
	 */
	protected void afterUpload(DBSFileUploadServletEvent pEvent) throws DBSIOException {}


	/**
	 * Evento ocorre após finalizado o upload do arquivo e antes que ele seja salvo localmente.<br/>
	 * Pode-se neste evento, alterar o nome do arquivo utilizando <b>setFileName</b> para que seja salvo com outro nome.
	 * Pode-se impedir que ele seja salvo,  setando <b>setOk(False)</b>.<br/>
	 * @param pEvent 
	 */
	protected void beforeSave(DBSFileUploadServletEvent pEvent) throws DBSIOException{}
	
	/**
	 * Evento ocorre após o arquivo ter sido salvo localmente.<br/>
	 * @param pEvent 
	 */
	protected void afterSave(DBSFileUploadServletEvent pEvent) throws DBSIOException{}
	
	/**
	 * @param pEvent  
	 */
	protected void onError(DBSFileUploadServletEvent pEvent) throws DBSIOException{}

	//Fire Events =========================================================================
	private boolean pvFireEventBeforeUpload() throws DBSIOException{
		DBSFileUploadServletEvent xE = new DBSFileUploadServletEvent(this);
		try{
//			openConnection();
			//Chame o metodo(evento) local para quando esta classe for extendida
			beforeUpload(xE);
			if (xE.isOk()){
				//Chama a metodo(evento) dentro das classe foram adicionadas na lista que possuem a implementação da respectiva interface
				for (int xX=0; xX<wEventListeners.size(); xX++){
					wEventListeners.get(xX).beforeUpload(xE);
					//Sai em caso de erro
					if (!xE.isOk()){break;}
		        }
			}
			return xE.isOk();
		}catch(Exception e){
			wLogger.error("BeforeUpload:", e);
			throw e;
		}finally{
//			closeConnection();
		}
	}

	private void pvFireEventAfterUpload() throws DBSIOException{
		DBSFileUploadServletEvent xE = new DBSFileUploadServletEvent(this);
		try{
			afterUpload(xE);
			if (xE.isOk()){
				//Chama a metodo(evento) dentro das classe foram adicionadas na lista que possuem a implementação da respectiva interface
				for (int xX=0; xX<wEventListeners.size(); xX++){
					wEventListeners.get(xX).beforeUpload(xE);
					//Sai em caso de erro
					if (!xE.isOk()){break;}
		        }
			}
		}catch(Exception e){
			wLogger.error("AfterUpload:", e);
			throw e;
		}
	}
	
	private boolean pvFireEventBeforeSave() throws DBSIOException{
		DBSFileUploadServletEvent xE = new DBSFileUploadServletEvent(this);
		xE.setFileName(wFileName);
		try{
			//Chame o metodo(evento) local para quando esta classe for extendida
			beforeSave(xE);
			if (xE.isOk()){
				//Chama a metodo(evento) dentro das classe foram adicionadas na lista que possuem a implementação da respectiva interface
				for (int xX=0; xX<wEventListeners.size(); xX++){
					wEventListeners.get(xX).beforeSave(xE);
					//Sai em caso de erro
					if (!xE.isOk()){break;}
		        }
			}
			wFileName = xE.getFileName();
			return xE.isOk();
		}catch(Exception e){
			wLogger.error("beforeSave:", e);
			throw e;
		}
	}

	private void pvFireEventAfterSave() throws DBSIOException{
		DBSFileUploadServletEvent xE = new DBSFileUploadServletEvent(this);
		xE.setFileName(wFileName);
		try{
			afterSave(xE);
			if (xE.isOk()){
				//Chama a metodo(evento) dentro das classe foram adicionadas na lista que possuem a implementação da respectiva interface
				for (int xX=0; xX<wEventListeners.size(); xX++){
					wEventListeners.get(xX).afterSave(xE);
					//Sai em caso de erro
					if (!xE.isOk()){break;}
		        }
			}
		}catch(Exception e){
			wLogger.error("afterSave:", e);
			throw e;
		}
	}


}
	

