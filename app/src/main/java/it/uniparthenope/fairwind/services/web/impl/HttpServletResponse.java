package it.uniparthenope.fairwind.services.web.impl;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by raffaelemontella on 24/01/16.
 */
public class HttpServletResponse implements javax.servlet.http.HttpServletResponse {
    public static final int SC_OK = NanoHTTPD.Response.Status.OK.getRequestStatus();
    public static final int SC_NOT_FOUND = NanoHTTPD.Response.Status.NOT_FOUND.getRequestStatus();
    public static final int SC_BAD_REQUEST = NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus();

    private NanoHTTPD.Response.Status mStatus;
    private String mMimeType;
    private String mRedirectTo;

    public HttpServletResponse() {

        mStatus= NanoHTTPD.Response.Status.UNAUTHORIZED;
        mMimeType="text/html";
    }



    public void setStatus(int status) {

        if (status==SC_OK) mStatus= NanoHTTPD.Response.Status.OK;
        else if (status==SC_NOT_FOUND) mStatus= NanoHTTPD.Response.Status.NOT_FOUND;
        else if (status==SC_BAD_REQUEST) mStatus= NanoHTTPD.Response.Status.BAD_REQUEST;

    }

    public void setContentType(String mimeType) {
        mMimeType=mimeType;
    }

    public void sendRedirect(String redirectTo) {
        mRedirectTo=redirectTo;
    }

    public NanoHTTPD.Response getNanoHTTPDResponse(String text) {
        return NanoHTTPD.newFixedLengthResponse(mStatus,mMimeType,text);
    }

    public NanoHTTPD.Response getNanoHTTPDResponse(InputStream inputStream, long length) {
        return NanoHTTPD.newFixedLengthResponse(mStatus,mMimeType,inputStream, length);
    }

    public NanoHTTPD.Response getNanoHTTPDResponse() {
        NanoHTTPD.Response response=NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.REDIRECT,"text/html","");
        response.addHeader("Location", mRedirectTo);
        return response;
    }
}
