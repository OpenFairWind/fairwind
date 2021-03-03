package it.uniparthenope.fairwind.services.logger;

/**
 * Created by raffaelemontella on 11/11/2017.
 */

public class PostException extends Exception {
    public PostException(Exception ex) {
        super(ex);
    }
}
