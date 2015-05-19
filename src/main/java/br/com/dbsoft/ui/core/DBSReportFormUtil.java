package br.com.dbsoft.ui.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.JRXmlExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

import org.apache.log4j.Logger;

import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSFile;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSHttp;
import br.com.dbsoft.util.DBSNumber;

/**
 * @author Avila
 * Pasta padrão para relatórios: resources/relatorios/
 * Sendo necessário apenas informar a pasta /relatorios/.
 */
public class DBSReportFormUtil {
	
	private static Logger wLogger = Logger.getLogger(DBSReportFormUtil.class);
	//Os jrxml devem estar na pasta src/main/resources/reports e deve existir uma pasta report a partir da pasta raiz web.
	private static final String REPORT_FOLDER = "reports" + File.separator;
	private static final String SUBREPORT_SUFIX = "_subReport"; 

	//---------------------------------Métodos Públicos ---------------------------------

	/**
	 * Gera o relatorio para ser exportado ou impresso.
	 * verifica se o arquivo .jasper existe, e caso não exista compila o arquivo .jrxml
	 * 
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 * @return JasperPrint Relatorio pronto para ser exportado
	 * @throws JRException
	 * @throws IOException
	 */
	public static JasperPrint createJasperPrint(String pReportFileName, Map<String, Object> pReportParameters, Object pReportData) throws JRException, IOException {
		JasperPrint xPrint = null;
		String 		xReportFilePathJASPER = pvGetReportFilePath(DBSFile.getFileNameJASPER(pReportFileName));
		String 		xReportFilePathJRXML = pvGetReportFilePath(DBSFile.getFileNameJRXML(pReportFileName));
		InputStream xReportInputStream = null;
		Integer		xCount;

		//Se as datas dos arquivos forem igual ou não encontrou um deles, cria o arquivo JASPER novamente
		if (!DBSFile.isEqualDate(xReportFilePathJASPER, xReportFilePathJRXML)){
			//Chama metodo para criar e retorna TRUE se criou
			//Se não criou..
			if (!pvCreateJasperFile(pReportFileName)){
				return null;
			} else {
				//Compila o cabeçalho se o .jasper nao for encontrado
				if (!DBSFile.exists(pvGetReportFilePath(DBSFile.getFileNameJASPER("DBSTemplate_Header")))) {
					String xSubReportFilePathJRXML = pvGetReportFilePath(DBSFile.getFileNameJRXML("DBSTemplate_Header"));
					String xSubReportFilePathJASPER = pvGetReportFilePath(DBSFile.getFileNameJASPER("DBSTemplate_Header"));
					if (!DBSFile.isEqualDate(xSubReportFilePathJASPER, xSubReportFilePathJRXML)){
						pvCreateJasperFile("DBSTemplate_Header");
					}
				}
				//Compila os sub-relatórios, caso eles existam 
				xCount = 1;
				while (DBSFile.exists(pvGetReportFilePath(DBSFile.getFileNameJRXML(pReportFileName + SUBREPORT_SUFIX + xCount)))) {
					String xSubReportFilePathJRXML = pvGetReportFilePath(DBSFile.getFileNameJRXML(pReportFileName + SUBREPORT_SUFIX + xCount));
					String xSubReportFilePathJASPER = pvGetReportFilePath(DBSFile.getFileNameJASPER(pReportFileName + SUBREPORT_SUFIX + xCount));
					if (!DBSFile.isEqualDate(xSubReportFilePathJASPER, xSubReportFilePathJRXML)){
						pvCreateJasperFile(pReportFileName + SUBREPORT_SUFIX + xCount);
					}
					xCount++;
				}
			}
		}
		//Cria stream que será utilizado para gerar o relatório
		xReportInputStream = new FileInputStream(new File(xReportFilePathJASPER));

		// Define os parametros do cabeçalho do relatorio
		if (pReportParameters == null) {
			pReportParameters = new HashMap<String, Object>();
		}

		pReportParameters.put("pDATA_IMPRESSAO", DBSFormat.getFormattedDateTime(System.currentTimeMillis()));
		pReportParameters.put("pSUBREPORT_DIR", DBSHttp.getResourcePath() + File.separator + REPORT_FOLDER); 
		
		// Verifica se foi passada uma conexão ou uma coleção de dados.
		if (JRBeanCollectionDataSource.class.isInstance(pReportData)) {
			JRBeanCollectionDataSource xDados = (JRBeanCollectionDataSource) pReportData;
			pReportParameters.put("pCOUNT_TOTAL", xDados.getRecordCount()); // Contagem total de registros a serem impressos.
			//Processa o relatório
			xPrint = JasperFillManager.fillReport(xReportInputStream, pReportParameters, xDados);
		} else {
			Connection xCn = (Connection) pReportData;
			//Processa o relatório
			xPrint = JasperFillManager.fillReport(xReportInputStream, pReportParameters, xCn);
		}
		xReportInputStream.close();
		return xPrint;
	}

	/**
	 * Visualizar relatorio na página HTML.
	 * 
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 * @return Retorna o caminho completo do relatório pdf gerado, a partir da raiz do projeto  
	 */
	public static String createPDFFile(String pReportFileName, JasperPrint pJasperPrint) {
		try {
			// Verifica se o relatório gerado contem alguma página
			if (pJasperPrint == null) {
				DBSFaces.sendMessage("mensagemErro", FacesMessage.SEVERITY_ERROR, "Relatório [" + pReportFileName + "] não encontrado.");
			} else if (pJasperPrint.getPages() == null || pJasperPrint.getPages().size() <= 0) {
				DBSFaces.sendMessage("mensagemErro", FacesMessage.SEVERITY_ERROR, "Nenhum informação encontrada.");
			} else {
				//Define o nome do relatório contendo também o número da seção para evitar que um sessão sobreesvreva o relatório de outra. 
//				String xReportFileName = pReportFileName + DBSSession.getSession().getId();
				
				String xDate = DBSFormat.getFormattedDateCustom(DBSDate.getNowTimestamp(),"yyyyMMddhhmmss"); 
				String xReportFileName = pReportFileName + xDate + DBSNumber.getOnlyNumber(DBSHttp.getHttpServletRequest().getRemoteAddr().toString());
				//Gera o relatório em formato PDF e salva na pasta web

				JasperExportManager.exportReportToPdfFile(pJasperPrint, pvGetReportFilePathWeb(DBSFile.getFileNamePDF(xReportFileName)));
				//Retorna caminho completo do arquivo gerado
				return pvGetReportRelativeFilePath(DBSFile.getFileNamePDF(xReportFileName));
			}
		} catch (JRException e) {
			wLogger.error("Erro ao gerar relatório " + pReportFileName + ": " + e);
			DBSFaces.sendMessage("mensagemErro", FacesMessage.SEVERITY_ERROR, "Erro ao gerar relatório: " + e.getMessage());
		}
		return "";
	}
//	
//
//	/**
//	 * Envia o relatório PDF já gerado para o browser
//	 * @param pReportFilePathPDF Nome do relatório contendo o caminho local completo
//	 */
//	public static void savePDF(String pReportFilePathPDF) {
//		String xReportFilePath = DBSHttp.getRealPath(pReportFilePathPDF);
//		String xSessionId = DBSHttp.getSession().getId();
//		String xRemoteFileName = DBSString.changeStr(pReportFilePathPDF, xSessionId, "");
//		xRemoteFileName = DBSString.changeStr(xRemoteFileName, REPORT_FOLDER, "");
//		DBSHttp.sendFile(xReportFilePath, xRemoteFileName);
//	}


	/**
	 * Salvar relatório em PDF.
	 * 	
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 */
	public static void savePDF(String pReportFileName, JasperPrint pJasperPrint) {
        try {
        	ByteArrayOutputStream 	xFilePDF = new ByteArrayOutputStream();
            JRPdfExporter 			xExporterPDF = new JRPdfExporter();    
            xExporterPDF.setParameter(JRPdfExporterParameter.JASPER_PRINT, pJasperPrint);
            xExporterPDF.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, xFilePDF);
            xExporterPDF.exportReport();
			DBSHttp.sendFile(xFilePDF, DBSFile.getFileNamePDF(pReportFileName), DBSFile.CONTENT_TYPE.PDF);
		} catch (JRException e) {
			wLogger.error(e);
		}
	}

	/**
	 * Salvar relatório em XML.
	 * 	
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 */
	public static void saveXML(String pReportFileName, JasperPrint pJasperPrint) {
        try {
        	ByteArrayOutputStream 	xFileXML = new ByteArrayOutputStream();
            JRXmlExporter 			xExporterXML = new JRXmlExporter();    
            xExporterXML.setParameter(JRXmlExporterParameter.JASPER_PRINT, pJasperPrint);
            xExporterXML.setParameter(JRXmlExporterParameter.OUTPUT_STREAM, xFileXML);
            xExporterXML.setParameter(JRXmlExporterParameter.IS_EMBEDDING_IMAGES, true);
			xExporterXML.exportReport();
			DBSHttp.sendFile(xFileXML, DBSFile.getFileNameXML(pReportFileName), DBSFile.CONTENT_TYPE.XML);
		} catch (JRException e) {
			wLogger.error(e);
		}
	}
	
	/**
	 * Salvar relatório em XLS.
	 * 	
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 */
	@Deprecated
	public static void saveXLS(String pReportFileName, JasperPrint pJasperPrint) {
        try {
        	ByteArrayOutputStream 	xFileXLS = new ByteArrayOutputStream();
        	JRXlsxExporter			xExporterXLSX = new JRXlsxExporter();    
            xExporterXLSX.setParameter(JRXlsExporterParameter.JASPER_PRINT, pJasperPrint);
            xExporterXLSX.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xFileXLS);
			xExporterXLSX.exportReport();
			DBSHttp.sendFile(xFileXLS, DBSFile.getFileNameXLS(pReportFileName), DBSFile.CONTENT_TYPE.XLS);
		} catch (JRException e) {
			wLogger.error(e);
		}
	}
	
	/**
	 * Salvar relatório em XLS.
	 * 	
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 */
	public static void saveXLSX(String pReportFileName, JasperPrint pJasperPrint) {
        try {
        	ByteArrayOutputStream 	xFileXLS = new ByteArrayOutputStream();
        	JRXlsxExporter			xExporterXLSX = new JRXlsxExporter();    
            xExporterXLSX.setParameter(JRXlsExporterParameter.JASPER_PRINT, pJasperPrint);
            xExporterXLSX.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xFileXLS);
			xExporterXLSX.exportReport();
			DBSHttp.sendFile(xFileXLS, DBSFile.getFileNameXLSX(pReportFileName), DBSFile.CONTENT_TYPE.XLSX);
		} catch (JRException e) {
			wLogger.error(e);
		}
	}
	
	/**
	 * Salvar relatório em HTML.
	 * 	
	 * @param pReportFileName Nome do Arquivo sem a extensão
	 * @param pReportParameters Parametros para o Relatorio (Titulo 1, Titulo 2...)
	 * @param pReportData Connection Ou JRBeanCollectionDataSource
	 */
	public static void saveHTML(String pReportFileName, JasperPrint pJasperPrint) {
		HttpServletResponse xResponse = DBSHttp.getHttpServletResponse();
		try {
			JasperExportManager.exportReportToHtmlFile(pJasperPrint, pvGetReportFilePathWeb(DBSFile.getFileNameHTML(pReportFileName)));
			xResponse.getWriter().println("<script>window.open(\""+ pvGetReportRelativeFilePath(DBSFile.getFileNameHTML(pReportFileName)) + "\")</script>");  
		} catch (JRException | IOException e) {
			wLogger.error(e);
			DBSFaces.sendMessage("mensagemErro", FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
	}

	
	// ====================================================================================================================
	// 	PRIVATE
	// ====================================================================================================================
	/**
	 * Retorna caminho completo do relatório na pasta de resources
	 * @param pReportFileName
	 * @return
	 */
	private static String pvGetReportFilePath(String pReportFileName){
		return DBSHttp.getResourcePath() + File.separator + pvGetReportRelativeFilePath(pReportFileName);
	}
	

	/**
	 * Retorna o caminho completo do relatório a partir da pasta web inicial
	 * @param pReportFileName
	 * @return
	 */
	private static String pvGetReportFilePathWeb(String pReportFileName){
		return DBSHttp.getRealPath("") + File.separator + pvGetReportRelativeFilePath(pReportFileName);
	}

	/**
	 * Retorna o nome do arquivo com o nome da pasta relativa aos relatórios.
	 * Não retorna o caminho completo 
	 * @param pReportFileName
	 * @return
	 */
	private static String pvGetReportRelativeFilePath(String pReportFileName){
		return REPORT_FOLDER + pReportFileName;
	}

	
	
	/**
	 * Compila o arquivo .jrxml para .jasper
	 * 
	 * @param pReportFileName Nome do relatório .jrxml sem a extensão 
	 * @return String Caminho para o arquivo .jasper
	 * @throws JRException
	 */
	private static Boolean pvCreateJasperFile(String pReportFileName) throws JRException {
		String xReportFilePathJRXML;
		xReportFilePathJRXML = pvGetReportFilePath(DBSFile.getFileNameJRXML(pReportFileName)); 
		if (!DBSFile.exists(xReportFilePathJRXML)) {
			wLogger.error("Relatório [" + xReportFilePathJRXML  + "] não encontrado.");
		} else {
			String xReportFilePathJASPER = pvGetReportFilePath(DBSFile.getFileNameJASPER(pReportFileName));
			JasperCompileManager.compileReportToFile(xReportFilePathJRXML, xReportFilePathJASPER);
			if (DBSFile.exists(xReportFilePathJASPER)){
				//Copia a data da última modificação para o arquivo Jasper como forma de a sua versão ao arquivo original
				DBSFile.copyLastModifiedData(xReportFilePathJRXML, xReportFilePathJASPER);
				return true;
			}
		}
		return false;
	}
	
}

