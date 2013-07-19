package view;

public class DomainPageCouple {

	private String domain;
	private String page;
	
	public DomainPageCouple(String domain, String page) {
		this.domain = domain;
		this.page = page;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

}
