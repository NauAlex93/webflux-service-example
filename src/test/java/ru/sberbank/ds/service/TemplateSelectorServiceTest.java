package ru.webfluxExample.ds.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.webfluxExample.ds.dto.enums.TemplateSelectorExpectedData;
import ru.webfluxExample.ds.dto.enums.TemplateSelectorParametersName;
import ru.webfluxExample.ds.dto.mlr.MlrRequiredParams;
import ru.webfluxExample.ds.dto.request.SampleDevelopmentType;
import ru.webfluxExample.ds.dto.request.SampleValidationType;
import ru.webfluxExample.ds.dto.request.SelectTemplateRequest;
import ru.webfluxExample.ds.exceptions.TemplateSelectorUnknownMethodException;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelWrongValuePropertyException;
import ru.webfluxExample.ds.props.template.MethodicProperties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TemplateSelectorServiceTest {

    private final String KIB_METHODIC = "10292128";

    private final String RB_METHODIC = "10148647";

    private final MethodicProperties.MethodicRB methodicsRB = new MethodicProperties.MethodicRB(
            UUID.fromString("00000000-0000-0000-0000-000000000099"),
            UUID.fromString("00000000-0000-0000-0000-000000000098"),
            UUID.fromString("00000000-0000-0000-0000-000000000097")
    );

    private final MethodicProperties.MethodicKIB methodicsKIB = new MethodicProperties.MethodicKIB(
            UUID.fromString("00000000-0000-0000-0000-000000000096"),
            UUID.fromString("00000000-0000-0000-0000-000000000095")
    );

    private final MethodicProperties methodics = new MethodicProperties(methodicsRB, methodicsKIB);

    TemplateSelectorService templateSelectorService = new TemplateSelectorService(methodics);

    /**
     * RB - розничный бизнес
     * KIB - комерческо инвестиционный бизнес
     */

    @Test
    public void shouldReceive_RB_Train_OOS_UUID() {
        Set<SampleDevelopmentType> forDevelopments = Collections.singleton(SampleDevelopmentType.TRAIN);
        Set<SampleValidationType> forValidation = Collections.singleton(SampleValidationType.OOS);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(forDevelopments, forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148647"));

        UUID uuidMono = templateSelectorService.selectTemplate(request, request.getMlrRequiredParams());
        Assert.assertEquals(uuidMono, methodics.getRb().getTrainOos());
    }


    @Test
    public void shouldReceive_RB_Train_OOT_UUID() {
        Set<SampleDevelopmentType> forDevelopments = Collections.singleton(SampleDevelopmentType.TRAIN);
        Set<SampleValidationType> forValidation = Collections.singleton(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(forDevelopments, forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148647"));

        UUID uuidMono = templateSelectorService.selectTemplate(request, request.getMlrRequiredParams());

        Assert.assertEquals(uuidMono, methodics.getRb().getTrainOot());
    }

    @Test
    public void shouldReceive_RB_Train_OOT_OOS_UUID() {
        Set<SampleDevelopmentType> forDevelopments = Collections.singleton(SampleDevelopmentType.TRAIN);
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        forValidation.add(SampleValidationType.OOS);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(forDevelopments, forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148647"));

        UUID uuidMono = templateSelectorService.selectTemplate(request, request.getMlrRequiredParams());

        Assert.assertEquals(uuidMono, methodics.getRb().getTrainOosOot());
    }


    @Test
    public void shouldReceive_KIB_OOS_UUID() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOS);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, KIB_METHODIC, "10148647"));

        UUID uuidMono = templateSelectorService.selectTemplate(request, request.getMlrRequiredParams());
        Assert.assertEquals(uuidMono, methodics.getKib().getOos());
    }

    @Test
    public void shouldReceive_KIB_OOT_UUID() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, KIB_METHODIC, "10148647"));

        UUID uuidMono = templateSelectorService.selectTemplate(request, request.getMlrRequiredParams());
        Assert.assertEquals(uuidMono, methodics.getKib().getOot());
    }

    @Test
    public void shouldReceiveError_wrong_value_PROPERTY_NAME_MODEL_BLACKBOX() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, false, "10148647", "10148647"));

        MlrModelWrongValuePropertyException mlrModelWrongValuePropertyException = Assert.assertThrows(
                MlrModelWrongValuePropertyException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );
        Assert.assertEquals("false", mlrModelWrongValuePropertyException.getCurrentValue());
        Assert.assertEquals("true", mlrModelWrongValuePropertyException.getExpectedValue());
        Assert.assertEquals(mlrModelWrongValuePropertyException.getParameterName(), TemplateSelectorParametersName.PROPERTY_NAME_MODEL_BLACKBOX.getParameterName());
    }

    @Test
    public void shouldReceiveErrorUnknownMethodic_KIB_because_OOT_and_OOS() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        forValidation.add(SampleValidationType.OOS);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, KIB_METHODIC, "10148647"));

        TemplateSelectorUnknownMethodException mlrModelWrongValuePropertyException = Assert.assertThrows(TemplateSelectorUnknownMethodException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );
        Assert.assertEquals("Check value for parameter 'FOR_VALIDATION'", mlrModelWrongValuePropertyException.getHint());
        Assert.assertEquals("templateDefinitonKIB", mlrModelWrongValuePropertyException.getPerhapsMethod());
    }

    @Test
    public void shouldReceiveError_wrong_value_PROPERTY_NAME_MODEL_CDS_BLOCK() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, "10292127", "10148647"));

        MlrModelWrongValuePropertyException mlrModelWrongValuePropertyException = Assert.assertThrows(
                MlrModelWrongValuePropertyException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );

        Assert.assertEquals("10292127", mlrModelWrongValuePropertyException.getCurrentValue());
        Assert.assertEquals(mlrModelWrongValuePropertyException.getParameterName(), TemplateSelectorParametersName.PROPERTY_NAME_MODEL_CDS_BLOCK.getParameterName());
    }

    @Test
    public void shouldReceiveError_wrong_value_PROPERTY_NAME_MODEL_DEVELOPER_BLOCK_RB_METHODIC() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        forValidation.add(SampleValidationType.OOS);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148648"));

        MlrModelWrongValuePropertyException mlrModelWrongValuePropertyException = Assert.assertThrows(
                MlrModelWrongValuePropertyException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );

        Assert.assertEquals("10148648", mlrModelWrongValuePropertyException.getCurrentValue());
        Assert.assertEquals(mlrModelWrongValuePropertyException.getParameterName(), TemplateSelectorParametersName.PROPERTY_NAME_MODEL_DEVELOPER_BLOCK.getParameterName());
    }

    @Test
    public void shouldReceiveError_wrong_value_templateDefinitonRB_without_train() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);

        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148647"));

        TemplateSelectorUnknownMethodException mlrModelWrongValuePropertyException = Assert.assertThrows(
                TemplateSelectorUnknownMethodException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );
        Assert.assertEquals("Check value for parameter 'FOR_DEVELOPMENT'", mlrModelWrongValuePropertyException.getHint());
        Assert.assertEquals("templateDefinitonRB", mlrModelWrongValuePropertyException.getPerhapsMethod());

    }

    @Test
    public void shouldReceiveError_wrong_value_of_SelectTemplateRequest_PROPERTY_NAME_DATA_TYPE() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);
        final String expectedValueOfDataType = "wrong data type";
        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                expectedValueOfDataType,
                TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName(),
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148647"));

        MlrModelWrongValuePropertyException mlrModelWrongValuePropertyException = Assert.assertThrows(
                MlrModelWrongValuePropertyException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );

        Assert.assertEquals(mlrModelWrongValuePropertyException.getCurrentValue(), expectedValueOfDataType);
        Assert.assertEquals(mlrModelWrongValuePropertyException.getExpectedValue(), TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName());
        Assert.assertEquals(mlrModelWrongValuePropertyException.getParameterName(), TemplateSelectorParametersName.PROPERTY_NAME_DATA_TYPE.getParameterName());

    }

    @Test
    public void shouldReceiveError_wrong_value_of_SelectTemplateRequest_PROPERTY_NAME_TASK_TYPE() {
        Set<SampleValidationType> forValidation = new HashSet<>();
        forValidation.add(SampleValidationType.OOT);
        SelectTemplateRequest.Sample sample = new SelectTemplateRequest.Sample(Collections.emptySet(), forValidation);
        final String expectedValueOfTaskType = "wrong task type";
        final SelectTemplateRequest request = new SelectTemplateRequest(1234,
                TemplateSelectorExpectedData.EXPECTED_DATA_TYPE.getExpectedName(),
                expectedValueOfTaskType,
                Boolean.parseBoolean(TemplateSelectorExpectedData.EXPECTED_DEV_NEEDS_NO_KEY_METRICS.getExpectedName()),
                TemplateSelectorExpectedData.EXPECTED_METRICS_NAME.getExpectedName(),
                sample,
                new MlrRequiredParams(123456, true, RB_METHODIC, "10148647"));

        MlrModelWrongValuePropertyException mlrModelWrongValuePropertyException = Assert.assertThrows(
                MlrModelWrongValuePropertyException.class,
                () -> templateSelectorService.selectTemplate(request, request.getMlrRequiredParams())
        );

        Assert.assertEquals(mlrModelWrongValuePropertyException.getCurrentValue(), expectedValueOfTaskType);
        Assert.assertEquals(mlrModelWrongValuePropertyException.getExpectedValue(), TemplateSelectorExpectedData.EXPECTED_TASK_TYPE.getExpectedName());
        Assert.assertEquals(mlrModelWrongValuePropertyException.getParameterName(), TemplateSelectorParametersName.PROPERTY_NAME_TASK_TYPE.getParameterName());

    }
}
