package ru.webfluxExample.ds.props.template;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@ConfigurationProperties("template.methodic")
@ConstructorBinding
public class MethodicProperties {
    @NotNull
    private final MethodicRB rb;
    @NotNull
    private final MethodicKIB kib;

    @Data
    public static class MethodicRB {
        @NotNull
        private final UUID trainOosOot;

        @NotNull
        private final UUID trainOos;

        @NotNull
        private final UUID trainOot;
    }

    @Data
    public static class MethodicKIB {
        @NotNull
        private final UUID oos;

        @NotNull
        private final UUID oot;
    }
}
