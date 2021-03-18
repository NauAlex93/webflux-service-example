package ru.webfluxExample.ds.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.webfluxExample.ds.dto.enums.TemplateSelectorParametersName;
import ru.webfluxExample.ds.dto.mlr.MlrRequiredParams;
import ru.webfluxExample.ds.dto.mlr.response.UnstableEntitiesResponse;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelEntityNotFoundException;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelNotInDevelopmentStatusException;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelPropertyNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static ru.webfluxExample.ds.dto.enums.TemplateSelectorParametersName.PROPERTY_NAME_MODEL_BLACKBOX;
import static ru.webfluxExample.ds.dto.enums.TemplateSelectorParametersName.PROPERTY_NAME_MODEL_CDS_BLOCK;
import static ru.webfluxExample.ds.dto.enums.TemplateSelectorParametersName.PROPERTY_NAME_MODEL_DEVELOPER_BLOCK;

class MlrServiceTest {

    private final MlrWebService mlrWebService = Mockito.mock(MlrWebService.class);

    MlrService mlrService = new MlrService(mlrWebService);

    private final MultiValueMap<String, String> tokens = new LinkedMultiValueMap<>();


    @Test
    public void shouldReceiveMlrRequiredParamsCorrect() {
        List<UnstableEntitiesResponse.Entity.Param> paramsList = new ArrayList<>();
        UnstableEntitiesResponse.Entity.Param blackBoxModel = new UnstableEntitiesResponse.Entity.Param(
                PROPERTY_NAME_MODEL_BLACKBOX.getParameterName(),
                Collections.singletonList("Да")
        );
        UnstableEntitiesResponse.Entity.Param cdsBlock = new UnstableEntitiesResponse.Entity.Param(
                PROPERTY_NAME_MODEL_CDS_BLOCK.getParameterName(),
                Collections.singletonList("10148647")
        );
        UnstableEntitiesResponse.Entity.Param developerBlock = new UnstableEntitiesResponse.Entity.Param(
                PROPERTY_NAME_MODEL_DEVELOPER_BLOCK.getParameterName(),
                Collections.singletonList("10148647")
        );
        paramsList.add(blackBoxModel);
        paramsList.add(cdsBlock);
        paramsList.add(developerBlock);
        UnstableEntitiesResponse.Entity entity = new UnstableEntitiesResponse.Entity(123456, "MODEL_DEVELOPMENT", paramsList);

        final UnstableEntitiesResponse response = new UnstableEntitiesResponse();
        response.setEntities(Collections.singletonList(entity));

        doReturn(Mono.just(response))
                .when(mlrWebService)
                .executeApiUnstableEntitiesRequest(anyLong(), any());

        final MlrRequiredParams expectedRequiredParams = new MlrRequiredParams(123456, true, "10148647", "10148647");

        final Mono<MlrRequiredParams> mlrRequiredParams = mlrService.mlrRequiredParamsForValidation(1234, tokens);

        StepVerifier
                .create(mlrRequiredParams)
                .expectNext(expectedRequiredParams)
                .expectComplete().verify();
    }

    @Test
    public void shouldReceiveErrorBecauseEntityNotFound() {
        final UnstableEntitiesResponse response = new UnstableEntitiesResponse();

        doReturn(Mono.just(response))
                .when(mlrWebService)
                .executeApiUnstableEntitiesRequest(anyLong(), any());

        final Mono<MlrRequiredParams> mlrRequiredParams = mlrService.mlrRequiredParamsForValidation(1234, tokens);

        StepVerifier
                .create(mlrRequiredParams)
                .expectErrorMatches(throwable -> throwable instanceof MlrModelEntityNotFoundException &&
                        ((MlrModelEntityNotFoundException) throwable).getModelId() == 1234)
                .verify();
    }


    @Test
    public void shouldReceiveErrorBecauseNotInDevelopmentStatus() {

        UnstableEntitiesResponse.Entity entity = new UnstableEntitiesResponse.Entity(123456, "MODEL_NOT_DEVELOPMENT", Collections.emptyList());
        final UnstableEntitiesResponse response = new UnstableEntitiesResponse();
        response.setEntities(Collections.singletonList(entity));

        doReturn(Mono.just(response))
                .when(mlrWebService)
                .executeApiUnstableEntitiesRequest(anyLong(), any());

        final Mono<MlrRequiredParams> mlrRequiredParams = mlrService.mlrRequiredParamsForValidation(1234, tokens);

        StepVerifier
                .create(mlrRequiredParams)
                .expectErrorMatches(throwable -> throwable instanceof MlrModelNotInDevelopmentStatusException &&
                        ((MlrModelNotInDevelopmentStatusException) throwable).getStatus().equalsIgnoreCase("MODEL_NOT_DEVELOPMENT"))
                .verify();
    }

    @Test
    public void shouldReceiveErrorBecauseNotFoundBlackBoxModel() {

        UnstableEntitiesResponse.Entity entity = new UnstableEntitiesResponse.Entity(123456, "MODEL_DEVELOPMENT", Collections.emptyList());
        final UnstableEntitiesResponse response = new UnstableEntitiesResponse();
        response.setEntities(Collections.singletonList(entity));

        doReturn(Mono.just(response))
                .when(mlrWebService)
                .executeApiUnstableEntitiesRequest(anyLong(), any());

        final Mono<MlrRequiredParams> mlrRequiredParams = mlrService.mlrRequiredParamsForValidation(1234, tokens);

        StepVerifier
                .create(mlrRequiredParams)
                .expectErrorMatches(throwable -> throwable instanceof MlrModelPropertyNotFoundException &&
                        ((MlrModelPropertyNotFoundException) throwable).getParameterName().equalsIgnoreCase(TemplateSelectorParametersName.PROPERTY_NAME_MODEL_BLACKBOX.getParameterName()))
                .verify();
    }

    @Test
    public void shouldReceiveErrorBecauseNotFoundCDSBlock() {

        List<UnstableEntitiesResponse.Entity.Param> paramsList = new ArrayList<>();
        UnstableEntitiesResponse.Entity.Param blackBoxModel = new UnstableEntitiesResponse.Entity.Param(
                PROPERTY_NAME_MODEL_BLACKBOX.getParameterName(),
                Collections.singletonList("Да")
        );
        paramsList.add(blackBoxModel);

        UnstableEntitiesResponse.Entity entity = new UnstableEntitiesResponse.Entity(123456, "MODEL_DEVELOPMENT", paramsList);
        final UnstableEntitiesResponse response = new UnstableEntitiesResponse();
        response.setEntities(Collections.singletonList(entity));

        doReturn(Mono.just(response))
                .when(mlrWebService)
                .executeApiUnstableEntitiesRequest(anyLong(), any());

        final Mono<MlrRequiredParams> mlrRequiredParams = mlrService.mlrRequiredParamsForValidation(1234, tokens);

        StepVerifier
                .create(mlrRequiredParams)
                .expectErrorMatches(throwable -> throwable instanceof MlrModelPropertyNotFoundException &&
                        ((MlrModelPropertyNotFoundException) throwable).getParameterName().equalsIgnoreCase(PROPERTY_NAME_MODEL_CDS_BLOCK.getParameterName()))
                .verify();
    }

    @Test
    public void shouldReceiveErrorBecauseNotFoundDeveloperBlock() {

        List<UnstableEntitiesResponse.Entity.Param> paramsList = new ArrayList<>();
        UnstableEntitiesResponse.Entity.Param blackBoxModel = new UnstableEntitiesResponse.Entity.Param(
                PROPERTY_NAME_MODEL_BLACKBOX.getParameterName(),
                Collections.singletonList("Да")
        );
        UnstableEntitiesResponse.Entity.Param cdsBlock = new UnstableEntitiesResponse.Entity.Param(
                PROPERTY_NAME_MODEL_CDS_BLOCK.getParameterName(),
                Collections.singletonList("10148647")
        );

        paramsList.add(cdsBlock);
        paramsList.add(blackBoxModel);

        UnstableEntitiesResponse.Entity entity = new UnstableEntitiesResponse.Entity(123456, "MODEL_DEVELOPMENT", paramsList);
        final UnstableEntitiesResponse response = new UnstableEntitiesResponse();
        response.setEntities(Collections.singletonList(entity));

        doReturn(Mono.just(response))
                .when(mlrWebService)
                .executeApiUnstableEntitiesRequest(anyLong(), any());

        final Mono<MlrRequiredParams> mlrRequiredParams = mlrService.mlrRequiredParamsForValidation(1234, tokens);

        StepVerifier
                .create(mlrRequiredParams)
                .expectErrorMatches(throwable -> throwable instanceof MlrModelPropertyNotFoundException &&
                        ((MlrModelPropertyNotFoundException) throwable).getParameterName().equalsIgnoreCase(PROPERTY_NAME_MODEL_DEVELOPER_BLOCK.getParameterName()))
                .verify();
    }
}
