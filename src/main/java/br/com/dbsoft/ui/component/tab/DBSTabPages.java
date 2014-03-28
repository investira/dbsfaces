package br.com.dbsoft.ui.component.tab;

import java.util.ArrayList;
import java.util.List;

import br.com.dbsoft.ui.component.tabpage.DBSTabPage;

public class DBSTabPages {
	
	private List<DBSTabPage> wTabPages =  new ArrayList<DBSTabPage>();
	private String wSelectTabPageCaption = null;
	
	public final void addTabPage(DBSTabPage pTabPage){
		if (pvGetTabPageIndex(pTabPage.getCaption())==-1){
			wTabPages.add(pTabPage);
		}
		this.setSelectTabPageCaption(pTabPage.getCaption());
	}

	public final void addTabPage(String pCaption, String pFile, Boolean pCloseble, String pCloseAction){
		if (pvGetTabPageIndex(pCaption)==-1){
			//Cria nova TabPage
			DBSTabPage xTabPage = new DBSTabPage(pCaption,pFile,pCloseble, pCloseAction, null, null,null);
			//Marca como selecionada para ser logo exibida
			//xTabPage.setSelected(true);
			//Adiciona a lista de tabPages;
			wTabPages.add(xTabPage);
		}
		this.setSelectTabPageCaption(pCaption);
	}
	
	public final void addTabPage(String pCaption, String pFile, Boolean pCloseble){
		if (pvGetTabPageIndex(pCaption)==-1){
			//Cria nova TabPage
			DBSTabPage xTabPage = new DBSTabPage(pCaption,pFile,pCloseble, null, null, null, null);
			wTabPages.add(xTabPage);
		}
		this.setSelectTabPageCaption(pCaption);
	}
	
	public final void removeTabPage(String pCaption){
		int xI =  pvGetTabPageIndex(pCaption);
		this.removeTabPage(xI);
	}
	
	public final void removeTabPage(int pIndex){
		if (pIndex!=-1){
			if (pIndex<= wTabPages.size()){
				boolean xS = wTabPages.get(pIndex).getSelected();//Indica que está sendo excluido um tab que está com foco
				wTabPages.remove(pIndex);
				if (xS){ //Se foi excluido um tab que está com foco...
					if (wTabPages.size()>0){ //Se houver mais tabpages posiciona no tab anterior
						this.setSelectTabPageCaption(wTabPages.get(pIndex-1).getCaption());
					}
				}
			}
		}
	}
	
	public final void removeTabPageUsingClientId(String pClientId){
		int xI =  pvGetTabPageIndexUsingClientId(pClientId);
		this.removeTabPage(xI);
//		if (xI!=-1){
//			boolean xS = wTabPages.get(xI).getSelected();//Indica que está sendo excluido um tab que está com foco
//			wTabPages.remove(xI);
//			if (xS){ //Se foi excluido um tab que está com foco...
//				if (wTabPages.size()>0){ //Se houver mais tabpages posiciona no tab anterior
//					this.setSelectTabPageCaption(wTabPages.get(xI-1).getCaption());
//				}
//			}
//		}
	}
	
	public final List<DBSTabPage>getTabPages(){
		return wTabPages;
	}

	public final void setSelectTabPageCaption(String pCaption){
		wSelectTabPageCaption = pCaption;
	}
	
	public final String getSelectTabPageCaption(){
		return wSelectTabPageCaption;
	}
	
	public final void removeAll(){
		wTabPages.clear();
	}
	
	//Procura o index da tabPage a partir no caption
	private final int pvGetTabPageIndex(String pCaption){
		if (pCaption != null){
			if (wTabPages != null){
				if (wTabPages.size()>0){
					for(int i=0; i < wTabPages.size(); i++){
						if (wTabPages.get(i).getCaption().equals(pCaption)){
							return i;
						}
					}
				}
			}
		}
		return -1;
	}
	
	private final int pvGetTabPageIndexUsingClientId(String pClientId){
		if (pClientId != null){
			if (wTabPages != null){
				if (wTabPages.size()>0){
					for(int i=0; i < wTabPages.size(); i++){
						if (wTabPages.get(i).getClientId()!=null){
							if (wTabPages.get(i).getClientId().equals(pClientId)){
								return i;
							}
						}
					}
				}
			}
		}
		return -1;
	}
	
	/*
	 * OK
	 * 
	 * 	
	
	private Map<String, DBSTabPage> wTabPages =  new HashMap<String, DBSTabPage>();
	private String wSelectTabPageCaption = null;
	
	public void addTabPage(String pCaption, String pFile, Boolean pCloseble){
		if (!wTabPages.containsKey(pCaption)){
			DBSTabPage xTabPage = new DBSTabPage(pCaption,pFile,pCloseble,null,null);
			xTabPage.setSelected(true);
			wTabPages.put(pCaption, xTabPage);
		}
		this.setSelectTabPageCaption(pCaption);
	}
	
	public void removeTabPage(String pCaption){
		if (wTabPages.containsKey(pCaption)){
			wTabPages.remove(pCaption);
		}
	}
	
	public List<DBSTabPage>getTabPages(){
		return new ArrayList<DBSTabPage>(wTabPages.values());
	}

	public void setSelectTabPageCaption(String pCaption){
		wSelectTabPageCaption = pCaption;
	}
	
	public String getSelectTabPageCaption(){
		return wSelectTabPageCaption;
	}


	 */
	
//	public void setSelectedTabPage(String pCaption){
//		if (wTabPages.containsKey(pCaption)){
//			wTabPages.get(wsSelected).setSelected(false);
//			wTabPages.get(pCaption).setSelected(true);
//			wsSelected = pCaption;
//		}
//	}
//	
//	public String getSelectedTabPage(){
//		return wsSelected;
//	}
	
//	private int pvGetTabPageIndex(String pCaption, String pFile){
//		if (wTabPages != null){
//			if (wTabPages.size()>0){
//				for(int i=0; i < wTabPages.size(); i++){
//					if (wTabPages.get(i).equals(pCaption, pFile)){
//						return i;
//					}
//				}
//			}
//		}
//		return -1;
//	}
	
//	private int pvGetTabPageIndex(String pId){
//		if (wTabPages != null){
//			if (wTabPages.size()>0){
//				for(int i=0; i < wTabPages.size(); i++){
//					if (wTabPages.get(i).getId().equals(pId)){
//						return i;
//					}
//				}
//			}
//		}
//		return -1;
//	}
	
//	public Boolean equals(String pCaption, String pFile){
//		if (this.getId()!=null){
//			return (Boolean) (this.getId().equals(pvFormatId(pCaption, pFile)));
//		}else{
//			return (Boolean) (pvFormatId(pCaption, pFile).equals(pvFormatId(this.getCaption(), this.getFile())));
//		}
//	}
	
//	private String pvFormatId(String pCaption, String pFile){
//		String xId = pCaption + pFile;
//		xId = xId.replace("/", "_")
//				 .replace(":", "_")
//				 .replace(" ", "_")
//				 .replace(",", "_")
//				 .replace(".", "_")
//				 .replace(";", "_");
//		return xId;
//	}
	
	
/*
 * 
 *	public void addTabViewPage(String pCaption, String pFile, Boolean pCloseble){
		System.out.println("ADD TABVIEWPAGE");
		int xI = pvGetTabViewPageIndex(pCaption,pFile); 
		if (xI==-1){
			wsTabViewPages.add(0, new DBSTabViewPage(pCaption,pFile,pCloseble));
			setActiveIndex(0);
		}else{
			setActiveIndex(xI);
		}
	}
 
 */
	

	
}
