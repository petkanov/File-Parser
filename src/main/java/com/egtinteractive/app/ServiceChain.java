package com.egtinteractive.app;

public interface ServiceChain {

    void setNextLink(ServiceChain nextLink);

    ServiceChain getNextLink();

    void acceptFile(String name);

}
