package ru.webfluxExample.ds.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserServiceUsersNotFoundException extends RuntimeException {

    private final String userLogin;
}
