package it.uniparthenope.fairwind.services.web;

import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.iki.elonen.NanoHTTPD;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.DeltaToMapConverter;
import nz.co.fortytwo.signalk.handler.FullToMapConverter;
import nz.co.fortytwo.signalk.handler.JsonGetHandler;
import nz.co.fortytwo.signalk.handler.JsonListHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import static nz.co.fortytwo.signalk.util.ConfigConstants.STORAGE_ROOT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sources;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;


import static nz.co.fortytwo.signalk.util.SignalKConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.GET;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PATH;

/**
 * Created by raffaelemontella on 05/02/16.
 */
public class RestApiHandler extends nz.co.fortytwo.signalk.handler.RestApiHandler {
    private static final String LOG_TAG = "REST_API_HANDLER";

    private static final String SLASH = "/";
    private static final String LIST = "list";
    private JsonSerializer ser = new JsonSerializer();
    private JsonListHandler listHandler = new JsonListHandler();
    private JsonGetHandler getHandler = new JsonGetHandler();
    private File storageDir = new File(Util.getConfigProperty(STORAGE_ROOT));
    private Map<String, String> mimeMap = new HashMap<String, String>();

    private static Logger logger = LogManager.getLogger(RestApiHandler.class);

    public RestApiHandler() throws IOException {
        super();
    }

    public RestApiHandler(String absolutePath) throws IOException {
        super(absolutePath);
    }

    public SignalKModel processPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        SignalKModel signalKObject=null;
        BufferedReader bufferedReader=new BufferedReader(httpServletRequest.getReader());
        String jsonString = bufferedReader.readLine();
        Json json = Json.read(jsonString);

        Log.i(LOG_TAG, "files to json: "+json);

        if(json.has("context")){
            Log.i(LOG_TAG, "jsonSimple.has: "+json.has("context"));
            DeltaToMapConverter deltaToMapConverter = new DeltaToMapConverter();

            signalKObject = deltaToMapConverter.handle(json);
            if(signalKObject!=null && !signalKObject.getData().isEmpty())
                Log.d(LOG_TAG, "DeltaToMapConverter: COMPLETE " +signalKObject);
            else
                Log.d(LOG_TAG, "DeltaToMapConverter: return NULL");

        }else{
            Log.i(LOG_TAG, "jsonFormat: FULL");
            FullToMapConverter fullToMapConverter = new FullToMapConverter();
            signalKObject = fullToMapConverter.handle(json);
            if(signalKObject!=null && !signalKObject.getData().isEmpty())
                Log.d(LOG_TAG, "FullToMapConverter: COMPLETE " +signalKObject);
            else
                Log.d(LOG_TAG, "FullToMapConverter: return NULL");
        }
        return signalKObject;
    }

    /**
     * Process a signalk GET message. The method will recover the appropriate
     * json object at the urls path from the provided SignalKModel. <b/> If the
     * request is invalid or not found the response will have the appropriate
     * HTTP error codes set, and null will be returned <b/> If found the
     * response will have HTTP 200 set, and the json object will be returned.
     *
     * @param request
     * @param response
     * @param signalkModel
     * @return
     * @throws Exception
     */
    public Object processGet(HttpServletRequest request, HttpServletResponse response, SignalKModel signalkModel)
            throws Exception {
        // use Restlet API to create the response
        String path = request.getPathInfo();
        // String path = exchange.getIn().getHeader(Exchange.HTTP_URI,
        // String.class);
        if (logger.isDebugEnabled())
            logger.debug("We are processing the path = " + path);

        // check valid request.
        if (path.length() < SignalKConstants.SIGNALK_API.length() || !path.startsWith(SignalKConstants.SIGNALK_API)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        path = path.substring(SignalKConstants.SIGNALK_API.length());

        if (path.startsWith(SLASH))
            path = path.substring(1);

        if (path.endsWith(SLASH))
            // Added 0,
            path = path.substring(0, path.length() - 1) + "*";

        if (logger.isDebugEnabled())
            logger.debug("We are processing the extension:" + path);
        // list
        if (path.startsWith(LIST)) {

            path = path.substring(LIST.length()).replace(SLASH, dot);
            if (path.startsWith(dot))
                path = path.substring(1);
            // find the 'vessels.* or as much as exists
            int pos1 = -1;
            int pos2 = -1;
            String context = "vessels.*";
            if (path.length() > 0) {
                pos1 = path.indexOf(dot) + 1;
                pos2 = path.indexOf(dot, pos1);
            }
            if (pos2 > -1) {
                // we have a potential vessel name
                context = path.substring(0, pos2);
                path = path.substring(pos2 + 1);
            }
            path = path + "*";

            List<String> rslt = listHandler.getMatchingPaths(path);
            Json pathList = Json.array();
            for (String p : rslt) {
                pathList.add(context + dot + p);
            }
            response.setContentType("application/json");

            // SEND RESPONSE
            response.setStatus(HttpServletResponse.SC_OK);
            return pathList;
        }
        else if (path.startsWith(vessels)) {
            // convert .self to .motu
            path = path.replace(SLASH, dot);
            path = path.replace(".self", dot + SignalKConstants.self);
            String context = Util.getContext(path);
            path = path.substring(context.length());
            if (path.startsWith(dot))
                path = path.substring(1);
            //if (!path.endsWith("*"))
            //    path = path + "*";
            Json getJson = Json.object();
            getJson.set(SignalKConstants.CONTEXT, context);
            Json getPath = Json.object();
            getPath.set(SignalKConstants.PATH, path);
            getPath.set(SignalKConstants.FORMAT, SignalKConstants.FORMAT_FULL);
            Json getArray = Json.array();
            getArray.add(getPath);
            getJson.set(SignalKConstants.GET, getArray);

            //SignalKModel keys = getHandler.handle(signalkModel, getJson);
            SignalKModel keys = handle(signalkModel, getJson);
            //NavigableMap<String, Object> keys = signalkModel.getSubMap(path.replace(SLASH, dot));

            if (keys.getData().size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }


            if (logger.isDebugEnabled())
                logger.debug("Returning:" + keys);

            response.setContentType("application/json");

            // SEND RESPONSE
            response.setStatus(HttpServletResponse.SC_OK);
            return ser.writeJson(keys);
        }
        // storage dir
        else if (path.startsWith(resources)||path.startsWith(sources)) {

            path = path.replace(SLASH, dot);
            //path = path.replace(".self", dot + SignalKConstants.self);
            String context = Util.getContext(path);

            path = path.substring(context.length());
            if (path.startsWith(dot))
                path = path.substring(1);

            Json getJson = Json.object();
            getJson.set(SignalKConstants.CONTEXT, context);
            Json getPath = Json.object();
            getPath.set(SignalKConstants.PATH, path);
            getPath.set(SignalKConstants.FORMAT, SignalKConstants.FORMAT_FULL);
            Json getArray = Json.array();
            getArray.add(getPath);
            getJson.set(SignalKConstants.GET, getArray);

            SignalKModel keys = handle(signalkModel, getJson);


            if (keys.getFullData().size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }


            if (logger.isDebugEnabled())
                logger.debug("Returning:" + keys);

            response.setContentType("application/json");

            // SEND RESPONSE
            response.setStatus(HttpServletResponse.SC_OK);
            return ser.writeJson(keys);

        } else if (path.isEmpty()) {
            Json getJson = Json.object();
            getJson.set(SignalKConstants.CONTEXT, "");
            Json getPath = Json.object();
            getPath.set(SignalKConstants.PATH, "*");
            getPath.set(SignalKConstants.FORMAT, SignalKConstants.FORMAT_FULL);
            Json getArray = Json.array();
            getArray.add(getPath);
            getJson.set(SignalKConstants.GET, getArray);

            //SignalKModel keys = getHandler.handle(signalkModel, getJson);
            SignalKModel keys = handle(signalkModel, getJson);

            if (keys.getData().size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }


            if (logger.isDebugEnabled())
                logger.debug("Returning:" + keys);

            response.setContentType("application/json");

            // SEND RESPONSE
            response.setStatus(HttpServletResponse.SC_OK);
            return ser.writeJson(keys);
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }

    /**
     * Processes the getNode against the signalKModel and returns a temporary signalkModel with the matching paths
     * Supports * and ? wildcards.
     *
     * @param signalkModel
     * @param getNode
     * @return
     * @throws Exception
     */
    public SignalKModel handle(SignalKModel signalkModel, Json getNode) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Checking for get  " + getNode);
        //TODO: fails on  {"context":"","get":[{"path":"resources.vessels.s...

        // go to context
        String context = getNode.at(CONTEXT).asString();

        Json paths = getNode.at(GET);
        SignalKModel tree = SignalKModelFactory.getCleanInstance();
        if (paths != null) {
            if (paths.isArray()) {

                for (Json path : paths.asJsonList()) {
                    parseGet(signalkModel, context, path, tree);
                }
            }
            if (logger.isDebugEnabled())
                logger.debug("Processed get  " + getNode);
        }

        return tree;

    }

    /**
     * Copies a context and path from the signalkModel to the temp model.
     * Supports * and ? wildcards.
     *
     * @param signalkModel
     * @param context
     * @param path
     * @param tree
     * @throws Exception
     */
    public void parseGet(SignalKModel signalkModel, String context, Json path, SignalKModel tree) throws Exception {
        // get values
        String regexKey = context + SignalKConstants.dot + path.at(PATH).asString();
        //convert SignalKConstants.self to SignalKConstants.self
        regexKey = Util.fixSelfKey(regexKey);
        if (logger.isDebugEnabled())
            logger.debug("Parsing get  " + regexKey);

        List<String> rslt = getMatchingPaths(signalkModel, regexKey);
        if (rslt.isEmpty()) {
            rslt = getMatchingPaths(signalkModel, regexKey+"*");
        }
        // add to tree
        for (String p : rslt) {
            //TODO: this may be calling recursive paths, need to do each path only once
            if (logger.isTraceEnabled())
                logger.trace("Parsing key  " + p);
            Util.populateTree(signalkModel, tree, p);
        }

    }

    /**
     * Returns a get of paths that this implementation is currently providing.
     * The get is filtered by the key if it is not null or empty in which case a full get is returned,
     * supports * and ? wildcards.
     *
     * @param regex
     * @return
     */
    public List<String> getMatchingPaths(SignalKModel signalkModel, String regex) {
        if (StringUtils.isBlank(regex)) {
            return ImmutableList.copyOf(signalkModel.getKeys());
        }
        regex = sanitizePath(regex);
        Pattern pattern = regexPath(regex);
        List<String> paths = new ArrayList<String>();
        for (String p : signalkModel.getKeys()) {
            if (pattern.matcher(p).matches()) {
                if (logger.isTraceEnabled())
                    logger.trace("Adding path:" + p);
                paths.add(p);
            }
        }
        return paths;
    }

    public static String sanitizePath(String newPath) {
        newPath = newPath.replace('/', '.');
        if (newPath.startsWith(dot))
            newPath = newPath.substring(1);
        //if (!newPath.endsWith("*") || !newPath.endsWith("?"))
        //   newPath = newPath + "*";

        return newPath;
    }

    public static Pattern regexPath(String newPath) {
        // regex it
        //String regex = newPath.replaceAll(".", "[$0]").replace("[*]", ".*")
        //        .replace("[?]", ".");
        //return Pattern.compile(regex);
        String regex = newPath.replace("$", "\\$").replace(".", "[.]").replace("*", ".*").replace("?", ".");
        Pattern result = null;
        try {
            result = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
        return result;
    }

}
