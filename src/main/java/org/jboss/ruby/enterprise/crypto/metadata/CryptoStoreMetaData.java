package org.jboss.ruby.enterprise.crypto.metadata;

public class CryptoStoreMetaData {

	private String name;
	private String store;
	private String password;

	public CryptoStoreMetaData() {

	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setStore(String store) {
		this.store = store;
	}
	
	public String getStore() {
		return this.store;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String toString() {
		return "[CryptoStoreMetaData: name=" + name + "; store=" + store + "; password=(protected)]";
	}
}
