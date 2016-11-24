package br.com.dbsoft.ui.component.progress;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSProgress.RENDERER_TYPE)
public class DBSProgressRenderer extends DBSRenderer {
	
    @Override
	public boolean getRendersChildren() {
		return false; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSProgress xProgress = (DBSProgress) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xProgress.getClientId(pContext);
		String xClass = CSS.PROGRESS.MAIN + " -left";
		
		if (xProgress.getStyleClass()!=null){
			xClass = xClass + xProgress.getStyleClass();
		}
		xWriter.startElement("div", xProgress);
			if (shouldWriteIdAttribute(pComponent)){
				DBSFaces.encodeAttribute(xWriter, "id", xClientId);
				DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			}
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xProgress.getStyle());
				xWriter.startElement("div", xProgress);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
					pvEncodeProgress(xProgress, xWriter);
				xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xProgress, xProgress.getTooltip().toString());
			pvEncodeJS(xProgress, xWriter);
		xWriter.endElement("div");
	}
	
	
	private void pvEncodeProgress(DBSProgress pProgress, ResponseWriter pWriter) throws IOException{
		Double xF = pvGetPercent(pProgress);
		String xStyleValue = "width:" + xF + "%";

		pWriter.startElement("div", pProgress);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
			DBSFaces.encodeAttribute(pWriter, "style", xStyleValue);
		pWriter.endElement("div");
		
		pvEncodeLabel(pProgress, xF, pWriter);
	}

	private void pvEncodeLabel(DBSProgress pProgress, Double pPercent, ResponseWriter pWriter) throws IOException{
		String xTextLabel = "";
		String xClassLabel = "";
		if (pPercent > 45){
			xClassLabel += " -th_i ";
		}
		//Finalizado
		if (pPercent == 100){
			xTextLabel  = "Ok";
		//Iniciado
		}else if (pPercent > 0){
			xTextLabel = pPercent.intValue() + "%";
		}
		pWriter.startElement("div", pProgress);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL + CSS.THEME.FC + xClassLabel);
			pWriter.write(xTextLabel);
		pWriter.endElement("div");
	}
	
	private Double pvGetPercent(DBSProgress pProgress) throws IOException{
		Double xM = DBSNumber.toDouble(pProgress.getMaxValue());
		Double xV = DBSNumber.toDouble(pProgress.getValue());
		Double xFator = 0D;
		//Exibe já preenchido se não houver valor máximo
		if (xM == 0D){
			xFator = 1D; 
		//Exibe já vázio se valor atual for zero
		}else if (xV == 0D){
			xFator = 0D;
		}else{
			xFator = (xV / xM); //Calcula fator
		}
		xFator *= 100D; //Calcula percentual
		return xFator;
	}
	
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xProgressId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_progress(xProgressId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}






