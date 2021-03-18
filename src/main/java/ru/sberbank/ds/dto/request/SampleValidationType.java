package ru.webfluxExample.ds.dto.request;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SampleValidationType {
    OOS("Out-of-Sample"),
    OOT("Out-of-Time");

    private final String localName;

    SampleValidationType(String localName){
        this.localName = localName;
    }

    @JsonValue
    String getLocalName(){
        return this.localName;
    }
}
