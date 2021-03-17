package de.raidcraft.loot;

public class TypeRegistrationException extends RuntimeException {

    public TypeRegistrationException() {
    }

    public TypeRegistrationException(String message) {
        super(message);
    }

    public TypeRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeRegistrationException(Throwable cause) {
        super(cause);
    }
}
