package it.cybion.monitoring.rtwup.storage;

import java.io.Serializable;

public class DomainPageCouple implements Serializable {

    private static final long serialVersionUID = 6302182471770813830L;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((page == null) ? 0 : page.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainPageCouple other = (DomainPageCouple) obj;
		return this.getDomain().equals(other.getDomain()) && this.getPage().equals(other.getPage());
	}

	
}
