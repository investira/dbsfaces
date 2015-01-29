package br.com.dbsoft.ui.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.util.DBSIO;
import br.com.dbsoft.util.DBSObject;



/**
 * @author ricardo.villar
 * Deve-se configura a servlet que herdará esta, com as anotações abaixo.<br/>
 * <b>MultipartConfig</b><br/>
 * <b>WebServlet(value='caminho e nome da servlet')</b><br/>
 */
public abstract class DBSFileUploadServlet extends  HttpServlet {

	private static final long serialVersionUID = 1063600676650691271L;

	private String wLocalPath = "";
	
    /**
	 * Caminho pasta local onde o arquivo recebido será salvo.
     * @return
     */
    public String getLocalPath() {return wLocalPath;}

	/**
	 * Caminho pasta local onde o arquivo recebido será salvo.
	 * @param pLocalPath
	 */
	public void setLocalPath(String pLocalPath) {wLocalPath = pLocalPath;}
	

	@Override
    protected void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) {
        if (!beforeUpload()
         || DBSObject.isEmpty(getLocalPath())){
        	return;
        }
		try {
			for (Part xPart : pRequest.getParts()) {
			    String xFilename = "";
			    for (String xS : xPart.getHeader("content-disposition").split(";")) {
			        if (xS.trim().startsWith("filename")) {
			            xFilename = xS.split("=")[1].replaceAll("\"", "");
			        }
			    }
			    if (beforeSaveFile(xFilename)){
			        if (!DBSObject.isEmpty(xFilename)){
			            xPart.write(wLocalPath + xFilename);
			            afterSaveFile();
			        }
			    }
			}
	        afterUpload();
		} catch (IOException | ServletException e) {
			try {
				DBSIO.throwIOException(e);
			} catch (DBSIOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Evento ocorre antes de iniciar o upload.<br/>
	 * Neste evento deve-se configurar o caminho local onde o arquivo será salvo.<br/>
	 * Pode-se evitar o inicio do upload, retornando <b>false</b>.<br/>.
	 * É obrigatório o retorno <b>true</b> para dar início ao upload.<br/>
	 * utilizando o método <b>setLocalPath</b>. 
	 * @return
	 */
	protected abstract boolean beforeUpload();
	
	/**
	 * Evento ocorre após finializado o upload.<br/>
	 */
	protected void afterUpload(){}

	/**
	 * Evento ocorre após finalizado o upload do arquivo e antes que ele seja salvo localmente.<br/>
	 */
	protected boolean beforeSaveFile(String pFileName){return true;}
	
	/**
	 * Evento ocorre após o arquivo ter sido salvo localmente.<br/>
	 */
	protected void afterSaveFile(){}
	
	protected void onError(){	}
}
	

