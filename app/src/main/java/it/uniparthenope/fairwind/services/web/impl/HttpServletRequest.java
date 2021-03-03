package it.uniparthenope.fairwind.services.web.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by raffaelemontella on 24/01/16.
 */
public class HttpServletRequest implements javax.servlet.http.HttpServletRequest {
    private NanoHTTPD.IHTTPSession session;

    public HttpServletRequest(NanoHTTPD.IHTTPSession session) {
        this.session=session;
    }

    public String getPathInfo() {
        return session.getUri();
    }

    public Reader getReader() {
        Reader reader=null;
        byte[] postBytes=null;
        Map<String, String> data = new HashMap<String, String>();
        try {
            session.parseBody(data);
            StringBuilder postData=new StringBuilder();
            for (String line:data.values()) {
                postData.append(line);
            }
            postBytes=postData.toString().getBytes();
            InputStream inputStream= new ByteArrayInputStream(postBytes);
            return new InputStreamReader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NanoHTTPD.ResponseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
