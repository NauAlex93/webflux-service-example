package ru.webfluxExample.ds.dto.request;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SampleDevelopmentType {
    TRAIN("Train");

    private String localName;

    SampleDevelopmentType(String localName){
        this.localName = localName;
    }

    @JsonValue
    String getLocalName(){
        return this.localName;
    }
}

