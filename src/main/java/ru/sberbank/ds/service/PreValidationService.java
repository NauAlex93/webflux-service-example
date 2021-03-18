package ru.webfluxExample.ds.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.request.SelectTemplateRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreValidationService {

//    private final MlrService mlrService;

    private final TemplateSelectorService templateSelectorService;

    /**
     * method for select uuid template for validation from application.yml by methodic
     *
     * @param request incoming parameters for find validation methodics
     * @return UUID for selected template
     */
    public Mono<UUID> selectTemplate(SelectTemplateRequest request) {
        MultiValueMap<String, String> tokens = new LinkedMultiValueMap<>();
        return Mono.just(templateSelectorService.selectTemplate(request, request.getMlrRequiredParams()));
//        return mlrService.mlrRequiredParamsForValidation(request.getModelId(), tokens)
//                .map(mlrRequiredParams -> templateSelectorService.selectTemplate(request, mlrRequiredParams));
    }


}
