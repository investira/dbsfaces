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
		String xClass = DBSFaces.CSS.CALENDAR.MAIN;
		
		if (xCalendar.getStyleClass()!=null){
			xClass = xClass + " " + xCalendar.getStyleClass();
		}
		
		
		xWriter.startElement("div", xCalendar);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xCalendar.getStyle(), null);

			//Container
			xWriter.startElement("div", xCalendar);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");
	
				pvEncodeHeader(pContext, xWriter, xCalendar);
				pvEncodeDays(xWriter, xCalendar);

				xWriter.endElement("div");
		xWriter.endElement("div");
	}
	
	private void pvEncodeHeader(FacesContext pContext, ResponseWriter pWriter, DBSCalendar pCalendar) throws IOException{
		/*Título Mes/Anos*/
		pWriter.startElement("div", pCalendar);
			pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CAPTION, "class");
			
			pvCreateComboMes(pContext, pCalendar);
			pvCreateInputAno(pContext, pCalendar);

		pWriter.endElement("div");	
		pWriter.startElement("span", pCalendar);
			pWriter.writeAttribute("class", DBSFaces.CSS.HORIZONTAL_LINE_WHITE, "class");
		pWriter.endElement("span");		
	}

	
	private void pvEncodeDays(ResponseWriter pWriter, DBSCalendar pCalendar) throws IOException{
		/*Dias*/
		pWriter.startElement("div", pCalendar);
			pWriter.writeAttribute("class", DBSFaces.CSS.CALENDAR.DAYS, "class");
			pWriter.startElement("div", pCalendar);
				pWriter.startElement("div", pCalendar);
					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CAPTION, "class");
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
						pWriter.writeAttribute("style", "opacity:0.4;", null);
						pWriter.write("S");
					pWriter.endElement("span");
					pWriter.startElement("span", pCalendar);
						pWriter.writeAttribute("style", "opacity:0.4;", null);
						pWriter.write("D");
					pWriter.endElement("span");
				pWriter.endElement("div");
				pWriter.startElement("div", pCalendar);
					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, "class");
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
					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.DISABLED, "class");
				}else if(xInicio.equals(xDate)){
					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.SELECTED, "class");
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
			xComboMes.setStyleClass(DBSFaces.CSS.CALENDAR.MONTH);
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
			xComboAno.setStyleClass(DBSFaces.CSS.CALENDAR.YEAR);
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
