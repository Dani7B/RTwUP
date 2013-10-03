package storage;

import java.util.List;
import java.util.ArrayList;

public class DomainPageList {

	private String domain;
	private List<PageCount> pageCountList;
	
	public DomainPageList(String domain) {
		this.domain = domain;
		this.pageCountList = new ArrayList<PageCount>();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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
