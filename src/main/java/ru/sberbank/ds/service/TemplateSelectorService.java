package ru.webfluxExample.ds.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.webfluxExample.ds.dto.enums.TemplateSelectorExpectedData;
import ru.webfluxExample.ds.dto.enums.TemplateSelectorParametersName;
import ru.webfluxExample.ds.dto.mlr.MlrRequiredParams;
import ru.webfluxExample.ds.dto.request.SelectTemplateRequest;
import ru.webfluxExample.ds.dto.request.SampleDevelopmentType;
import ru.webfluxExample.ds.dto.request.SampleValidationType;
import ru.webfluxExample.ds.exceptions.TemplateSelectorUnknownMethodException;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelWrongValuePropertyException;
import ru.webfluxExample.ds.props.template.MethodicProperties;

import java.util.UUID;

import static ru.webfluxExample.ds.dto.enums.TemplateSelectorExpectedData.EXPECTED_DATA_TYPE;
import static ru.webfluxExample.ds.dto.enums.TemplateSelectorExpectedData.EXPECTED_TASK_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateSelectorService {

    private final MethodicProperties methodicProperties;

    /**
     * значение которое должно быть в свойстве CDS_BLOCK для перехода на методику РБ(Розничный Бизнес)
     */
    private final String EXPECTED_CDS_BLOCK_RB_ID = "10148647";

    /**
     * значение которое должно быть в свойстве CDS_BLOCK для перехода на методику КИБ(Коорпоративно-инвестиционный бизнес)
     */
    private final String EXPECTED_CDS_BLOCK_KIB_ID = "10292128";
    /**
     * значение которое должно быть в свойстве DEVELOPMENT_BLOCK для перехода на методику РБ(Розничный Бизнес)
     */
    private final String EXPECTED_DEVELOPMENT_BLOCK_RB = "10148647";

    private final String EXPECTED_MODEL_BLACKBOX = "true";


    public UUID selectTemplate(SelectTemplateRequest request, MlrRequiredParams mlrRequiredParams) {
        if (!mlrRequiredParams.isBlackBox()) {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_MODEL_BLACKBOX.getParameterName(),
                    String.valueOf(mlrRequiredParams.isBlackBox()),
                    EXPECTED_MODEL_BLACKBOX
            );
        }
        validateRequest(request);
        if (mlrRequiredParams.getCdsBlock().equalsIgnoreCase(EXPECTED_CDS_BLOCK_KIB_ID)) {
            return templateDefinitonKIB(request);
        } else if (mlrRequiredParams.getCdsBlock().equalsIgnoreCase(EXPECTED_CDS_BLOCK_RB_ID)) {
            return templateDefinitonRB(request, mlrRequiredParams.getDevelopmentBlock());
        } else {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_MODEL_CDS_BLOCK.getParameterName(),
                    mlrRequiredParams.getCdsBlock()
            );
        }
    }

    private UUID templateDefinitonKIB(SelectTemplateRequest request) {
        if ((request.getSample().getForValidation().stream().findFirst().isPresent() &&
                request.getSample().getForValidation().stream().findFirst().get().equals(SampleValidationType.OOS) &&
                request.getSample().getForValidation().size() == 1)) {
            return methodicProperties.getKib().getOos();
        }
        if ((request.getSample().getForValidation().stream().findFirst().isPresent() &&
                request.getSample().getForValidation().stream().findFirst().get().equals(SampleValidationType.OOT) &&
                request.getSample().getForValidation().size() == 1)) {
            return methodicProperties.getKib().getOot();
        }
        throw new TemplateSelectorUnknownMethodException("templateDefinitonKIB", "Check value for parameter 'FOR_VALIDATION'");
    }

    private UUID templateDefinitonRB(SelectTemplateRequest request,
                                     String modelDeveloperBlock) {
        if (!modelDeveloperBlock.equalsIgnoreCase(EXPECTED_DEVELOPMENT_BLOCK_RB)) {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_MODEL_DEVELOPER_BLOCK.getParameterName(),
                    modelDeveloperBlock,
                    EXPECTED_DEVELOPMENT_BLOCK_RB
            );
        }
        //для разработки
        if (request.getSample().getForDevelopment().size() == 1 && request.getSample().getForDevelopment().stream().findFirst().get().equals(SampleDevelopmentType.TRAIN)) {
            if (request.getSample().getForValidation().size() == 2) {
                return methodicProperties.getRb().getTrainOosOot();
            } else if (request.getSample().getForValidation().size() == 1) {
                if (request.getSample().getForValidation().stream().findFirst().get().equals(SampleValidationType.OOS)) {
                    return methodicProperties.getRb().getTrainOos();
                }
                if (request.getSample().getForValidation().stream().findFirst().get().equals(SampleValidationType.OOT)) {
                    return methodicProperties.getRb().getTrainOot();
                }
                throw new TemplateSelectorUnknownMethodException("templateDefinitonRB + TRAIN", "Check value for parameter 'FOR_VALIDATION'");
            } else {
                throw new TemplateSelectorUnknownMethodException("templateDefinitonRB + TRAIN", "Check value for parameter 'FOR_VALIDATION'");
            }
        } else {
            throw new TemplateSelectorUnknownMethodException("templateDefinitonRB", "Check value for parameter 'FOR_DEVELOPMENT'");
        }
    }

    private void validateRequest(SelectTemplateRequest request) {
        if (!request.getDataType().equalsIgnoreCase(EXPECTED_DATA_TYPE.getExpectedName())) {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_DATA_TYPE.getParameterName(),
                    request.getDataType(),
                    TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName()
            );
        }
        if (!request.getTaskType().equalsIgnoreCase(EXPECTED_TASK_TYPE.getExpectedName())) {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_TASK_TYPE.getParameterName(),
                    request.getTaskType(),
                    TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName()
            );
        }
        if (request.isDevNeedsNoKeyMetrics()) {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_DEV_NEEDS_NO_KEY_METRICS.getParameterName(),
                    Boolean.valueOf(request.isDevNeedsNoKeyMetrics()).toString(),
                    TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()
            );
        }
        if (!request.getMetricsName().equalsIgnoreCase(TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName())) {
            throw new MlrModelWrongValuePropertyException(
                    TemplateSelectorParametersName.PROPERTY_NAME_METRICS_NAME.getParameterName(),
                    request.getMetricsName(),
                    TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName()
            );
        }
    }
}
