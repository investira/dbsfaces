package br.com.dbsoft.ui.component.progress;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSProgress.RENDERER_TYPE)
public class DBSProgressRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}	
	
    @Override
	public boolean getRendersChildren() {
		return false; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
    
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSProgress xProgress = (DBSProgress) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xProgress.getClientId(pContext);
		String xClass = DBSFaces.CSS.PROGRESS.MAIN;
		if (xProgress.getStyleClass()!=null){
			xClass = xClass + xProgress.getStyleClass();
		}
		xWriter.startElement("div", xProgress);
			if (shouldWriteIdAttribute(pComponent)){
				xWriter.writeAttribute("id", xClientId, "id");
				xWriter.writeAttribute("name", xClientId, "name");
			}
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xProgress.getStyle(), null);
//				DBSFaces.encodeLabel(pContext, xProgress, xWriter);
				pvEncodeProgress(xProgress, xWriter);
			DBSFaces.encodeTooltip(pContext, xProgress, xProgress.getTooltip().toString());
		xWriter.endElement("div");
	}
	
	
	private void pvEncodeProgress(DBSProgress pProgress, ResponseWriter pWriter) throws IOException{
		Double xM = DBSNumber.toDouble(pProgress.getMaxValue());
		Double xV = DBSNumber.toDouble(pProgress.getValue());
		Double xF = 0D;
		Double xWidth = DBSNumber.toInteger(pProgress.getWidth(), 16).doubleValue();
		Double xValueWidth = 0D;
		String xValueStyle = "";
		String xStyle = "width:" + xWidth + "px";
		String xClassLabel = DBSFaces.CSS.MODIFIER.LABEL;
		String xTextLabel = "";
		String xClassLoading = "";
		//Exibe já preenchido se não houver valor máximo
		if (xM == 0D){
			xF = 1D; 
		//Exibe já vázio se valor atual for zero
		}else if (xV == 0D){
			xF = 0D;
		}else{
			xF = (xV / xM); //Calcula fator
		}
		xValueWidth = xWidth * xF;
		xValueStyle = "width:" + xValueWidth.intValue() + "px;";
		xF *= 100D; //Calcula percentual
		if (xF > 45){
			//Finalizado
			if (xF == 100){
				xClassLabel += "-green";
			//Pouco menos da metado concluído
			}else{
				xClassLabel += "-white";
			}
		}else{
			//Iniciado
			xClassLabel += "-black";
		}
		//Finalizado
		if (xF == 100){
			xTextLabel  = "Ok";
		//Iniciado
		}else if (xF > 0){
			xClassLoading = DBSFaces.CSS.MODIFIER.LOADING;
			xTextLabel = xF.intValue() + "%";
		}
		pWriter.startElement("div", pProgress);
		DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
			pWriter.startElement("div", pProgress);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
				DBSFaces.setAttribute(pWriter, "style", xStyle, null);
				pWriter.startElement("div", pProgress);
					DBSFaces.setAttribute(pWriter, "class", xClassLoading, null);
				pWriter.endElement("div");
				pWriter.startElement("div", pProgress);
					DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.VALUE, null);
					DBSFaces.setAttribute(pWriter, "style", xValueStyle, null);
				pWriter.endElement("div");
				pWriter.startElement("div", pProgress);
					DBSFaces.setAttribute(pWriter, "class", xClassLabel, null);
					pWriter.write(xTextLabel);
				pWriter.endElement("div");
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	
}






