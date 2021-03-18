package ru.webfluxExample.ds.dto;

/**
 * Статус проекта
 *
 */
public enum ProjectExecutionStatus {
    /**
     * Проект выполнен успешно
     */
    DONE,

    /**
     * Во время выполнения проекта возникла ошибка
     */
    FAILED,

    /**
     * Проект ещё выполняется
     */
    IN_PROGRESS
}
