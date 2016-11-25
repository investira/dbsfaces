package br.com.dbsoft.ui.component.progress;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.progress.DBSProgress.TYPE;
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
		String xClass = CSS.PROGRESS.MAIN + " -hide ";
		
		TYPE xType = TYPE.get(xProgress.getType());
		
		xClass += xType.getStyleClass();
		
		if (xProgress.getAnimated()){
			xClass += " -ani ";
		}
		if (xProgress.getStyleClass()!=null){
			xClass += xProgress.getStyleClass();
		}
		xWriter.startElement("div", xProgress);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xProgress.getStyle());
			DBSFaces.encodeAttribute(xWriter, "v", pvGetPercent(xProgress));
			xWriter.startElement("div", xProgress);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.NOT_SELECTABLE);
				pvEncodeContent(xProgress, xType, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xProgress, xProgress.getTooltip().toString());
			pvEncodeJS(xProgress, xWriter);
		xWriter.endElement("div");
	}
	
	
	private void pvEncodeContent(DBSProgress pProgress, TYPE pType, ResponseWriter pWriter) throws IOException{
		if (pType == TYPE.CIRCLE){
			pvEncodeCircle(pProgress, pWriter);
		}else{
			pvEncodeHorizontalVertical(pProgress, pWriter);
		}
	}

	private void pvEncodeHorizontalVertical(DBSProgress pProgress, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pProgress);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			pvEncodeHorizontalVerticalLabel(pProgress, pWriter);
		pWriter.endElement("div");
	}

	private void pvEncodeHorizontalVerticalLabel(DBSProgress pProgress, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pProgress);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL + CSS.THEME.FC);
			pWriter.startElement("div", pProgress);
				DBSFaces.encodeAttribute(pWriter, "class", "-value");
			pWriter.endElement("div");
			pWriter.startElement("div", pProgress);
				DBSFaces.encodeAttribute(pWriter, "class", "-sufix");
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeCircle(DBSProgress pProgress, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("svg", pProgress);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			pWriter.startElement("defs", pProgress);
				pWriter.startElement("linearGradient", pProgress);
					DBSFaces.encodeAttribute(pWriter, "id", pProgress.getClientId() + "_color");
					pWriter.startElement("stop", pProgress);
						DBSFaces.encodeAttribute(pWriter, "offset", "0");
						DBSFaces.encodeAttribute(pWriter, "stop-color", "");
					pWriter.endElement("stop");	
					pWriter.startElement("stop", pProgress);
						DBSFaces.encodeAttribute(pWriter, "offset", "100%");
						DBSFaces.encodeAttribute(pWriter, "stop-color", "");
					pWriter.endElement("stop");	
				pWriter.endElement("linearGradient");
			pWriter.endElement("defs");
			pWriter.startElement("g", pProgress);
				DBSFaces.encodeSVGPath(pProgress, pWriter, "", "-back", null, null);
				DBSFaces.encodeSVGPath(pProgress, pWriter, "", CSS.MODIFIER.POINT, null, "stroke=url(#" + pProgress.getClientId() + "_color)");
				DBSFaces.encodeSVGText(pProgress, pWriter, 0, 0,  "<tspan class='-value'/><tspan class='-sufix'></tspan>", CSS.MODIFIER.LABEL + CSS.THEME.FC, null, null);
			pWriter.endElement("g");
		pWriter.endElement("svg");
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






