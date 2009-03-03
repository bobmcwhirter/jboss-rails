package org.jboss.ruby.enterprise.crypto.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CryptoMetaData {
	
	private Map<String,CryptoStoreMetaData> stores = new HashMap<String, CryptoStoreMetaData>();
	
	public CryptoMetaData() {
		
	}
	
	public void addCryptoStoreMetaData(CryptoStoreMetaData cryptoStoreMetaData) {
		this.stores.put( cryptoStoreMetaData.getName(), cryptoStoreMetaData );
	}
	
	public CryptoStoreMetaData getCryptoStoreMetaData(String name) {
		return this.stores.get( name );
	}
	
	public Collection<CryptoStoreMetaData> getCryptoStoreMetaDatas() {
		return this.stores.values();
	}
	
	public String toString() {
		return "[CryptoMetaData: stores=" + stores + "]";
	}

}
