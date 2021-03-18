package ru.webfluxExample.ds.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserServiceLoginsNotFoundException extends RuntimeException {

    private final String userLogin;
}
