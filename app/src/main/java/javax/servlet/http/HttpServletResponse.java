package javax.servlet.http;


import javax.servlet.ServletResponse;


public interface HttpServletResponse  extends ServletResponse {
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_OK = 200;
    public static final int SC_NOT_FOUND = 404;

    public void sendRedirect(java.lang.String location) throws java.io.IOException;
    public void setStatus(int sc);

}
