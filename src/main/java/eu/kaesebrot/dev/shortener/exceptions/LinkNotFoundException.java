package eu.kaesebrot.dev.shortener.exceptions;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String id) {
        super("Could not find link with id " + id);
    }
}
