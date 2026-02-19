package org.envycorp.userservice.exception.role;

public class RoleIsAlreadyExisted extends RuntimeException {
    public RoleIsAlreadyExisted(String message) {
        super(message);
    }
}
