package it.uniparthenope.fairwind.services.logger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by raffaelemontella on 28/06/2017.
 */

public interface SecureFilePacker {
    public void pack(File source, File destination) throws IOException, GeneralSecurityException;
}
