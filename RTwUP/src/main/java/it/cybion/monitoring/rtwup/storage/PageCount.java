package it.cybion.monitoring.rtwup.storage;

public class PageCount {

	private String page;

    private String count;
	
	public PageCount(String page, String count) {
		this.page = page;
		this.count = count;
	}
	
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
}