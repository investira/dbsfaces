package br.com.dbsoft.ui.component.calendar;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.combobox.DBSCombobox;

import java.io.IOException;
import java.sql.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSDate;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCalendar.RENDERER_TYPE)
public class DBSCalendarRenderer extends DBSRenderer {

	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		//DBSCalendar xCalendar = (DBSCalendar) pComponent;
	}

	
	@Override 
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
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
		DBSCalendar xCalendar = (DBSCalendar) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xCalendar.getClientId(pContext);
		String xClass = CSS.CALENDAR.MAIN;
		
		if (xCalendar.getStyleClass()!=null){
			xClass += xCalendar.getStyleClass();
		}
		
		xWriter.startElement("div", xCalendar);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xCalendar.getStyle());

			//Container
			xWriter.startElement("div", xCalendar);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
	
				pvEncodeHeader(pContext, xWriter, xCalendar);
				pvEncodeDays(xWriter, xCalendar);

				xWriter.endElement("div");
		xWriter.endElement("div");
	}
	
	private void pvEncodeHeader(FacesContext pContext, ResponseWriter pWriter, DBSCalendar pCalendar) throws IOException{
		/*Título Mes/Anos*/
		pWriter.startElement("div", pCalendar);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION);
			
			pvCreateComboMes(pContext, pCalendar);
			pvCreateInputAno(pContext, pCalendar);

		pWriter.endElement("div");	
		pWriter.startElement("span", pCalendar);
			DBSFaces.setAttribute(pWriter, "class", CSS.HORIZONTAL_LINE_WHITE);
		pWriter.endElement("span");		
	}

	
	private void pvEncodeDays(ResponseWriter pWriter, DBSCalendar pCalendar) throws IOException{
		/*Dias*/
		pWriter.startElement("div", pCalendar);
			DBSFaces.setAttribute(pWriter, "class", CSS.CALENDAR.DAYS);
			pWriter.startElement("div", pCalendar);
				pWriter.startElement("div", pCalendar);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION);
					pWriter.startElement("span", pCalendar);
						pWriter.write("S");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						pWriter.write("T");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						pWriter.write("Q");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						pWriter.write("Q");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						pWriter.write("S");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						DBSFaces.setAttribute(pWriter, "style", "opacity:0.4;");
						pWriter.write("S");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						DBSFaces.setAttribute(pWriter, "style", "opacity:0.4;");
						pWriter.write("D");
					pWriter.endElement("span");
				pWriter.endElement("div");
				pWriter.startElement("div", pCalendar);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
					pWriter.startElement("div", pCalendar);
						pvEndodeDias(pWriter,pCalendar);
					pWriter.endElement("div");
				pWriter.endElement("div");
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEndodeDias(ResponseWriter pWriter, DBSCalendar pCalendar) throws IOException{
		Date xDate = DBSDate.toDate(pCalendar.getValue());
		
		Date xInicio = pvGetPrimeiraSegundaFeira(xDate);
		Date xFim = pvGetUltimoDomingo(xDate);
		Integer xMes = DBSDate.getMonth(xDate);
		while (!xInicio.equals(xFim)){
			pWriter.startElement("span", pCalendar);
				if (DBSDate.getMonth(xInicio) != xMes){
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.DISABLED);
				}else if(xInicio.equals(xDate)){
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SELECTED);
				}
				pWriter.write(DBSDate.getDay(xInicio).toString());
			pWriter.endElement("span");
			xInicio = DBSDate.getNextDate(null, xInicio, 1, false);
		}
	}
	
	private Date pvGetPrimeiraSegundaFeira(Date pDate){
		pDate = DBSDate.getFirstDayOfTheMonth(null, pDate, false);
		/*Procura a primeira data que for segunda feira*/
		while (DBSDate.getWeekdayNumber(pDate) != 2){
			pDate = DBSDate.getNextDate(null, pDate, -1, false);
		}
		return pDate;
	}

	private Date pvGetUltimoDomingo(Date pDate){
		pDate = DBSDate.getLastDayOfTheMonth(null, pDate, false);
		/*última data que for segunda feira*/
		while (DBSDate.getWeekdayNumber(pDate) != 2){
			pDate = DBSDate.getNextDate(null, pDate, 1, false);
		}
		return pDate;
	}

	public static void pvCreateComboMes(FacesContext pContext, DBSCalendar pCalendar) throws IOException{
//			String 			xClientId = pCalendar.getClientId(xContext);
		DBSCombobox 	xComboMes = (DBSCombobox) pCalendar.getFacet("mes");
		if (xComboMes == null){
			xComboMes = (DBSCombobox) pContext.getApplication().createComponent(DBSCombobox.COMPONENT_TYPE);
			xComboMes.setId("mes");
			xComboMes.setStyleClass(CSS.CALENDAR.MONTH);
			xComboMes.setSize(10);
			
			//Inclui nomes dos meses na lista
			String[] xMeses = DBSDate.getMonthsNames();
			for (int xI = 0; xI < xMeses.length-1; xI++){
				xComboMes.getList().put(xI, xMeses[xI]);
			}
			pCalendar.getFacets().put("mes", xComboMes);
		}
		xComboMes.encodeAll(pContext);

	}
	
	public static void pvCreateInputAno(FacesContext pContext, DBSCalendar pCalendar) throws IOException{
		DBSCombobox 	xComboAno = (DBSCombobox) pCalendar.getFacet("ano");
		if (xComboAno == null){
			xComboAno = (DBSCombobox) pContext.getApplication().createComponent(DBSCombobox.COMPONENT_TYPE);
			xComboAno.setId("ano");
			xComboAno.setStyleClass(CSS.CALENDAR.YEAR);
			xComboAno.setSize(6);
			
			//Inclui nomes dos meses na lista
			for (Integer xI = 1940; xI < 2140; xI++){
				xComboAno.getList().put(xI, xI.toString());
			}
			pCalendar.getFacets().put("ano", xComboAno);
		}
		xComboAno.encodeAll(pContext);
	}
	
}
