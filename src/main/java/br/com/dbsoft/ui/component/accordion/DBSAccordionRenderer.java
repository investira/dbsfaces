package br.com.dbsoft.ui.component.accordion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;


import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSAccordion.RENDERER_TYPE)
public class DBSAccordionRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSAccordion xAccordion = (DBSAccordion) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xAccordion.getClientId(pContext);
		String xClass = CSS.ACCORDION.MAIN;
		
		if (xAccordion.getStyleClass()!=null){
			xClass = xClass + xAccordion.getStyleClass();
		}
		
		List<DBSAccordionSection> xAccordionSection=new ArrayList<DBSAccordionSection>();
		
		if (xAccordion.getAccordionSection()!=null){
			xAccordionSection = xAccordion.getAccordionSection();
			//Excluir qualquais outros filhos que eventualmente tenha sido definidos de forma estática dentro do código xhtml
			//xAccordion.getChildren().clear(); //Comentado em 14/Nov/2012 -> Não excluir filhos posi é efetuado o encodeall dos mesmo no código abaixo
		}else{
			for (int xX=0; xX<xAccordion.getChildren().size(); xX++){
				if (xAccordion.getChildren().get(xX) instanceof DBSAccordionSection){
					xAccordionSection.add((DBSAccordionSection) xAccordion.getChildren().get(xX));
				}
			}
			//xAccordion.getChildren().clear();  //Comentado em 14/Nov/2012 -> Não excluir filhos pois é efetuado o encodeall dos mesmo no código abaixo
		}
		
		if (xAccordionSection.size() > 0){
			xWriter.startElement("div", xAccordion);
				DBSFaces.encodeAttribute(xWriter, "id", xClientId);
				DBSFaces.encodeAttribute(xWriter, "name", xClientId);
				DBSFaces.encodeAttribute(xWriter, "class", xClass);
				DBSFaces.encodeAttribute(xWriter, "style", xAccordion.getStyle());
				for (int xX=0; xX<xAccordionSection.size(); xX++){
					/*section*/
					xWriter.startElement("div", xAccordion);
						DBSFaces.encodeAttribute(xWriter, "class", CSS.ACCORDION.SECTION + CSS.MODIFIER.NORMAL);
						xWriter.startElement("div", xAccordion);
							/*header*/
							xWriter.startElement("div", xAccordion);
								DBSFaces.encodeAttribute(xWriter, "class", CSS.ACCORDION.SECTION_CAPTION + CSS.NOT_SELECTABLE);
								if (xAccordionSection.get(xX).getIconClass()!=null){
									xWriter.startElement("span", xAccordion);
										DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.ICON + xAccordionSection.get(xX).getIconClass());
									xWriter.endElement("span");
								}
								if (xAccordionSection.get(xX).getCaption()!=null){
									xWriter.startElement("span", xAccordion);
										DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CAPTION);
										xWriter.write(xAccordionSection.get(xX).getCaption());
									xWriter.endElement("span");
								}
							xWriter.endElement("div");
							/*container*/
							xWriter.startElement("div", xAccordion);
								DBSFaces.encodeAttribute(xWriter, "class", CSS.ACCORDION.SECTION_CONTAINER);
								
								xWriter.startElement("div", xAccordion);
									DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
									//***Render filhos do dbsaccordionsection***
									
									xAccordionSection.get(xX).encodeAll(pContext);
									//Comentado em 14/Nov/2012 -> Não excluir filhos é efetuado o encodeall dos mesmo no código acima
									//xAccordion.getChildren().add(xAccordionSection.get(xX));
									//renderChildren(pContext, xAccordionSection.get(xX));
									//*******************
								xWriter.endElement("div");
								
								xWriter.startElement("div", xAccordion);
									DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.COVER);
								xWriter.endElement("div");
							xWriter.endElement("div");
						xWriter.endElement("div");
					xWriter.endElement("div");
				}
			xWriter.endElement("div");
			
			DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xAccordionId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
					     " dbs_accordion(xAccordionId); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);					
		}
	}
}
