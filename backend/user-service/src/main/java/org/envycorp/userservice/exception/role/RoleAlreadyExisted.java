package org.envycorp.userservice.exception.role;

public class RoleAlreadyExisted extends RuntimeException {
    public RoleAlreadyExisted(String message) {
        super(message);
    }
}
