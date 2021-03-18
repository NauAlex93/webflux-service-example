package ru.webfluxExample.ds.util;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDUtilsTest {

    @Test
    public void shouldSuccessfullyCheckUid() {
        assertThat(UUIDUtils.validateUUID(UUID.randomUUID().toString())).isTrue();
        assertThat(UUIDUtils.validateUUID("wrong UUID")).isFalse();
        assertThat(UUIDUtils.validateUUID(null)).isFalse();
    }
}
