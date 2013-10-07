package storage;

import java.util.List;
import java.util.ArrayList;

public class DomainPageList {

	private List<PageCount> pageCountList;
	
	public DomainPageList() {
		this.pageCountList = new ArrayList<PageCount>();
	}

	public List<PageCount> getPageCountList() {
		return pageCountList;
	}

	public void setPageCountList(List<PageCount> pageCountList) {
		this.pageCountList = pageCountList;
	}

	public void addPageCountToList(PageCount pc) {
		this.pageCountList.add(pc);
	}
	
	public void addPageCountToList(String page, String count) {
		PageCount pc = new PageCount(page, count);
		this.pageCountList.add(pc);
	}
}