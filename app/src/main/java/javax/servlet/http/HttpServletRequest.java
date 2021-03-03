package javax.servlet.http;


import java.io.Reader;

import javax.servlet.ServletRequest;

/**
 * Created by raffaelemontella on 08/09/15.
 */
public interface HttpServletRequest extends ServletRequest {
    public String getPathInfo();
    public Reader getReader();
}
