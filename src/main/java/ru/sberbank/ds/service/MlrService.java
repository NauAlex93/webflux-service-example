package ru.webfluxExample.ds.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.mlr.MlrRequiredParams;
import ru.webfluxExample.ds.dto.mlr.response.UnstableEntitiesResponse;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelEntityNotFoundException;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelNotInDevelopmentStatusException;
import ru.webfluxExample.ds.exceptions.mlr.MlrModelPropertyNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class MlrService {

    private final MlrWebService mlrWebService;

    public Mono<MlrRequiredParams> mlrRequiredParamsForValidation(long modelId, MultiValueMap<String, String> tokens) {
        return mlrWebService.executeApiUnstableEntitiesRequest(modelId, tokens)
                .flatMap(unstableEntitiesResponse -> {
                    Optional<UnstableEntitiesResponse.Entity> entity = unstableEntitiesResponse.getEntities()
                            .stream()
                            .findFirst();
                    if (!entity.isPresent()) {
                        return Mono.error(new MlrModelEntityNotFoundException(modelId));
                    } else {
                        List<UnstableEntitiesResponse.Entity.Param> params = entity.get().getParams();
                        if (entity.isPresent() && entity.get().getStatus().equalsIgnoreCase("MODEL_DEVELOPMENT")) {
                            String modelBlackbox = getParamValue("MODEL_BLACKBOX", entity.get().getParams());
                            String modelCdsBlock = getParamValue("MODEL_CDS_BLOCK", entity.get().getParams());
                            String modelDeveloperBlock = getParamValue("MODEL_DEVELOPER_BLOCK", entity.get().getParams());
                            return Mono.just(
                                    new MlrRequiredParams(entity.get().getId(),
                                            modelBlackbox.equals("Да"),
                                            modelCdsBlock,
                                            modelDeveloperBlock
                                    ));
                        } else {
                            return Mono.error(new MlrModelNotInDevelopmentStatusException(entity.get().getStatus()));
                        }

                    }
                });
    }

    private String getParamValue(String paramId, List<UnstableEntitiesResponse.Entity.Param> params) {
        return params.stream()
                .filter(param -> param.getId().equals(paramId))
                .findFirst()
                .flatMap(param -> param.getValues().stream().findFirst())
                .orElseThrow(() -> new MlrModelPropertyNotFoundException(paramId));
    }
}
