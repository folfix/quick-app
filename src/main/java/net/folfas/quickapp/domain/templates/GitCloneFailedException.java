package net.folfas.quickapp.domain.templates;

public class GitCloneFailedException extends Exception {

    public GitCloneFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
