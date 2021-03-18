package ru.webfluxExample.ds.dto.enums;

/**
 * template selector model parameters
 * from income model and model service(BM service)
 */

public enum TemplateSelectorParametersName {

    /**
     * income properties
     *
     * @see ru.webfluxExample.ds.dto.request.SelectTemplateRequest
     */
    PROPERTY_NAME_DATA_TYPE("DATA_PARAMS_DATA_TYPE"),

    PROPERTY_NAME_TASK_TYPE("DATA_PARAMS_TASK_TYPE"),

    PROPERTY_NAME_DEV_NEEDS_NO_KEY_METRICS("DEV_NEEDS_NO_KEY_METRICS"),

    PROPERTY_NAME_METRICS_NAME("METRICS_NAME"),

    /**
     * properties from model service
     */
    PROPERTY_NAME_MODEL_DEVELOPER_BLOCK("MODEL_DEVELOPER_BLOCK"),

    PROPERTY_NAME_MODEL_BLACKBOX("MODEL_BLACKBOX"),

    PROPERTY_NAME_MODEL_CDS_BLOCK("MODEL_CDS_BLOCK");


    private String parameterName;

    TemplateSelectorParametersName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName(){
        return parameterName;
    }
}
