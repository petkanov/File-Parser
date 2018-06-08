package com.egtinteractive.config;

public interface ServiceChain {

    void acceptFile(String fileName);

    void setNextLink(ServiceChain nextLink);

    ServiceChain getNextLink();
}
