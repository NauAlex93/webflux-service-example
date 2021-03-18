package ru.webfluxExample.ds.dto.enums;


/**
 * expected input value of parameters
 * @see ru.webfluxExample.ds.dto.request.SelectTemplateRequest
 */
public enum TemplateSelectorExpectedData {
    EXPECTED_DATA_TYPE("Структурированные данные"),
    EXPECTED_TASK_TYPE("Бинарная классификация"),
    EXPECTED_DEV_NEEDS_NO_KEY_METRICS("false"),
    EXPECTED_METRICS_NAME("Gini");


    private String expectedName;

    public String getExpectedName() {
        return expectedName;
    }

    TemplateSelectorExpectedData(String expectedName) {
        this.expectedName = expectedName;
    }
}
