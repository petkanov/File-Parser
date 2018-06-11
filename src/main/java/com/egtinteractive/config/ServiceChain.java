package com.egtinteractive.config;

public interface ServiceChain {

    void acceptFile(String fileName);

    void setNextLink(ServiceChain nextLink);

    ServiceChain getNextLink();
    
    default void bla(String as) {
	acceptFile(as);
	System.out.println("SSSSSS");
    }
}
