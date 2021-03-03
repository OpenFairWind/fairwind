package it.uniparthenope.fairwind.services.web;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.util.StreamUtils;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.HashMap;

import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;

import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapView;
import it.uniparthenope.fairwind.sdk.maps.source.tiledlayer.CustomTileSource;
import it.uniparthenope.fairwind.sdk.maps.source.tiledlayer.TiledLayerPreferences;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;


/**
 * Created by raffaelemontella on 09/09/15.
 */
public class WebServiceWorker extends NanoHTTPD {
    // The Log tag
    private static final String LOG_TAG = "WEBSERVICE_WORKER";


    private WebServer webServer;
    private RestApiHandler restApiHandler;
    private SignalKModel signalKModel;

    private final TileWriter mTileWriter;

    public WebServiceWorker(WebServer webServer, int port) {
        super(port);
        Log.d(LOG_TAG, "Constructing...");
        mTileWriter = new TileWriter();
        this.webServer = webServer;
        Log.d(LOG_TAG, "Getting the SignalKModel");
        signalKModel = (SignalKModel) FairWindApplication.getFairWindModel();
        try {

            String mimeTypes = webServer.getTextFromFile(R.raw.mime).toString();
            File outputDir = FairWindApplication.getInstance().getCacheDir(); // context being the Activity pointer
            File outputFile = File.createTempFile("mime.", ".types", outputDir);
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(mimeTypes);
            fileWriter.flush();
            fileWriter.close();

            MIME_TYPES = new HashMap<String, String>();
            FileReader fileReader = new FileReader(outputFile.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    MIME_TYPES.put(parts[1], parts[0]);
                }
            }
            bufferedReader.close();
            fileReader.close();

            Log.d(LOG_TAG, "Instancing RestApiHandler -> " + outputFile.getAbsolutePath() + " ...");
            restApiHandler = new RestApiHandler(outputFile.getAbsolutePath());
            Log.d(LOG_TAG, "...RestApiHandler done!");
            outputFile.delete();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        Log.d(LOG_TAG, "...constructing done!");
    }

    @Override
    public Response serve(IHTTPSession session) {
        // https://github.com/SignalK/specification/wiki/APIs-and-Conventions

        Response.Status status = Response.Status.OK;

        String html = "";
        String uri = session.getUri();
        Method method = session.getMethod();

        Log.d(LOG_TAG, "Parse:" + uri);
        Log.i(LOG_TAG, "Method: " + method);
        String body = null;
        Json json = Json.object();
        HttpServletRequest httpServletRequest = new it.uniparthenope.fairwind.services.web.impl.HttpServletRequest(session);
        HttpServletResponse httpServletResponse = new it.uniparthenope.fairwind.services.web.impl.HttpServletResponse();
        if (Method.GET.equals(method)) { //if the method is GET...
            Log.d(LOG_TAG, "in Method: " + method);
            if (uri.startsWith(SignalKConstants.SIGNALK_DISCOVERY) || uri.startsWith(Constants.FAIRWIND_DISCOVERY)) {

                if (uri.startsWith(SignalKConstants.SIGNALK_DISCOVERY)) {
                    Log.d(LOG_TAG, SignalKConstants.SIGNALK_DISCOVERY);
                    if (uri.startsWith(SignalKConstants.SIGNALK_API)) {
                        Log.d(LOG_TAG, SignalKConstants.SIGNALK_API);
                        try {
                            String pathInfo = httpServletRequest.getPathInfo();
                            Log.d(LOG_TAG, "httpServletRequest.getPathInfo:" + pathInfo);
                            json = (Json) restApiHandler.processGet(httpServletRequest, httpServletResponse, signalKModel);
                            if (json != null) {
                                Log.d(LOG_TAG, "json PRE: " + json.toString());
                                /*
                                if (jsonResult.isArray()) {
                                    json = jsonResult;
                                } else
                                if (jsonResult.at(SignalKConstants.vessels,null)!=null) {
                                    json=jsonResult.at(SignalKConstants.vessels,null);
                                } else {
                                    json = jsonResult;
                                }
                                */
                                pathInfo = pathInfo.substring(SignalKConstants.SIGNALK_API.length());
                                if (pathInfo.startsWith("/"))
                                    pathInfo = pathInfo.substring(1);
                                if (!pathInfo.isEmpty()) {
                                    pathInfo = pathInfo.replace("self", SignalKConstants.self);
                                    String[] paths = pathInfo.split("/");

                                    for (String path : paths) {
                                        Json json1 = json.at(path);
                                        if (json1 != null) {
                                            json = json1;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                Log.d(LOG_TAG, "json POST: " + json.toString());
                            } else {
                                Log.d(LOG_TAG, "json is null: " + json);
                                //Log.i(LOG_TAG, "response getStatus: ");
                            }
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    } else if (uri.startsWith(SignalKConstants.SIGNALK_AUTH)) {
                        Log.d(LOG_TAG, "Auth");
                    } else if (uri.startsWith(SignalKConstants.SIGNALK_WS)) {
                        Log.d(LOG_TAG, SignalKConstants.SIGNALK_WS);
                        if (uri.endsWith("primus.js")) {
                            Log.d(LOG_TAG, "Primus.js:" + uri);
                            return NanoHTTPD.newFixedLengthResponse(status, "application/javascript", webServer.getTextFromFile(R.raw.primus));
                        }
                        Log.d(LOG_TAG, "Web Socket");

                        if (isWebsocketRequested(session) == true) {
                            return webServer.getWebSocketWorker().serve(session);
                        }

                        return NanoWSD.newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoWSD.MIME_PLAINTEXT, "Upgrade needed");

                    } else {
                        Log.d(LOG_TAG, SignalKConstants.SIGNALK_DISCOVERY + " default -> getEndpoints");
                        json = webServer.getEndpoints();
                    }

                }
                if (body == null) {
                    body = json.toString();
                }
                httpServletResponse.setContentType("application/json");
                return ((it.uniparthenope.fairwind.services.web.impl.HttpServletResponse) httpServletResponse).getNanoHTTPDResponse(body);
            } else {
                // Behaves as a regular HTTP Server
                String fileName = "";
                try {
                    // Checks if the uri is not null
                    if (uri != null) {
                        // If exists, set the default page
                        if (uri.endsWith("/")) {
                            if (new File(webServer.getDocumentRoot() + File.separator + uri.substring(1) + "index.html").exists()) {
                                uri += "index.html";
                            }
                        }
                        // Check if a directory listing is required
                        if (uri.endsWith("/") == false) {
                            // Serve an actual resource

                            // Get the document root
                            String documentRoot = webServer.getDocumentRoot();
                            /*
                            if (uri.startsWith("/tiles/")) {
                                documentRoot = Environment.getExternalStorageDirectory().getPath() + "/osmdroid/";
                            }
                            */
                            Log.d(LOG_TAG, "Serving:" + uri);
                            Log.i(LOG_TAG, "webServer Root: " + documentRoot);
                            Log.i(LOG_TAG, "uri: " + uri + " - substring: " + uri.substring(1));



                            // Check if the request is about tiles
                            if (uri.startsWith("/tiles/")) {
                                // Tiles are stored in a different location used by
                                // OSMDroid CacheManager
                                fileName = serveTiles(uri);
                            } else {
                                // Retrieve the resource file name
                                fileName = documentRoot + File.separator + uri.substring(1);
                            }

                            fileName=fileName.replace("//","/");
                            Log.d(LOG_TAG, "Local resource:" + fileName);

                            // Create the file object
                            File file = new File(fileName);

                            // Create the file tream object
                            FileInputStream fis = new FileInputStream(file);

                            // Get the mime type from url
                            String mimeType = getMimeTypeForFile(uri);
                            Log.d(LOG_TAG, "Mime type:" + mimeType);

                            // Return the result
                            return NanoHTTPD.newFixedLengthResponse(status, mimeType, fis, file.length());
                        } else {
                            // List the directory content
                            html = serveDirectoryList(uri);

                            // Return the result
                            return NanoHTTPD.newFixedLengthResponse(status, "text/html", html);
                        }


                    }
                } catch (FileNotFoundException e) {
                    // Set the resource not found page
                    html = webServer.get404HTML();
                    status = Response.Status.NOT_FOUND;
                    Log.d(LOG_TAG, "Error opening file: " + fileName +" "+uri.substring(1));
                    e.printStackTrace();
                }
            }
        } else if (Method.POST.equals(method)) { //if the method is POST...
            Log.d(LOG_TAG, "in Method: " + method);
            if (uri.startsWith(SignalKConstants.SIGNALK_DISCOVERY) || uri.startsWith(Constants.FAIRWIND_DISCOVERY)) {

                if (uri.startsWith(SignalKConstants.SIGNALK_DISCOVERY)) {
                    Log.d(LOG_TAG, SignalKConstants.SIGNALK_DISCOVERY);
                    if (uri.startsWith(SignalKConstants.SIGNALK_API)) {
                        Log.d(LOG_TAG, SignalKConstants.SIGNALK_API);
                        try {
                            SignalKModel signalkObject = restApiHandler.processPost(httpServletRequest, httpServletResponse);
                            if (signalkObject != null) {
                                webServer.process(signalkObject);
                                json.set("SignaK", "ok");
                            }
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        if (body == null) {
                            body = json.toString();
                        }
                        httpServletResponse.setContentType("application/json");
                        return ((it.uniparthenope.fairwind.services.web.impl.HttpServletResponse) httpServletResponse).getNanoHTTPDResponse(body);
                    }
                }
            }
        }

        return NanoHTTPD.newFixedLengthResponse(status, "text/html", html);
    }


    private boolean isWebSocketConnectionHeader(Map<String, String> headers) {
        String connection = headers.get(NanoWSD.HEADER_CONNECTION);
        return connection != null && connection.toLowerCase().contains(NanoWSD.HEADER_CONNECTION_VALUE.toLowerCase());
    }

    protected boolean isWebsocketRequested(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        String upgrade = headers.get(NanoWSD.HEADER_UPGRADE);
        boolean isCorrectConnection = isWebSocketConnectionHeader(headers);
        boolean isUpgrade = NanoWSD.HEADER_UPGRADE_VALUE.equalsIgnoreCase(upgrade);
        return isUpgrade && isCorrectConnection;
    }


    private boolean isValidPath(String path) {
        if (StringUtils.isBlank(path))
            return false;
        if (path.equals(SignalKConstants.SIGNALK_DISCOVERY))
            return true;
        if (path.startsWith(SignalKConstants.SIGNALK_API))
            return true;
        return false;
    }

    private String standardizePath(String path) {

        // check valid request.

        path = path.substring(SignalKConstants.SIGNALK_API.length());
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);

        path = path.replace("/", ".");

        return path;
    }

    /**
     * true if the path contains any * or ? for a wildcard match
     *
     * @param path
     * @return
     */
    private boolean containsWildcard(String path) {
        if (StringUtils.isBlank(path))
            return false;
        if (path.contains("*") || path.contains("?"))
            return true;
        return false;
    }

    private String serveTiles(String uri) {
        Log.d(LOG_TAG, "serveTiles:" + uri);
        // Add the string ".tile" in order to avoid image gallery index
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/osmdroid/" + File.separator + uri.substring(1)+ ".tile";

        // Check if the file doesn't exists
        File file = new File(fileName);
        if (file.exists() == false) {
            // The tile is not available offline

            // Remove the heading and split the url in parts
            String[] parts = uri.replace("/tiles/", "").split("/");

            // Get the tiled layer name
            String tiledLayerName = parts[0];

            // Get the context instance
            Context context = FairWindApplication.getInstance();

            // Get Maps Config Overlays as Json
            Json jsonMapsConfigOverlays = FairWindMapView.getMapsConfigOverlaysAsJson(FairWindApplication.getFairWindModel(), context);

            // Get the map overlay map
            Map<String, Json> overlayMap = jsonMapsConfigOverlays.asJsonMap();

            // Get the json preference
            Json overlayJson = overlayMap.get(tiledLayerName);

            // Check if the tiled layer name is consistent
            if (overlayJson != null) {

                // Get the zoom, x, y
                int y = Integer.parseInt(parts[parts.length - 1].replace(".png", ""));
                int x = Integer.parseInt(parts[parts.length - 2]);
                int zoom = Integer.parseInt(parts[parts.length - 3]);

                // Create a tiled layer preference by preferences
                TiledLayerPreferences tiledLayerPreferences = new TiledLayerPreferences();
                tiledLayerPreferences.byJson(overlayJson);

                // Instance the custom tile source
                CustomTileSource tileSource = new CustomTileSource(tiledLayerPreferences.getName(), tiledLayerPreferences.getMinZoom(), tiledLayerPreferences.getMaxZoom(), tiledLayerPreferences.getTileSize(), tiledLayerPreferences.getImageEnding(), tiledLayerPreferences.getUrls().toArray(new String[0]), tiledLayerPreferences.getParams());

                // Definie the tile to be downloaded
                MapTile tile = new MapTile(zoom, x, y);

                InputStream in = null;
                OutputStream out = null;
                try {

                    // Get the final URL
                    final String tileURLString = tileSource.getTileURLString(tile);

                    // Create the http client
                    final HttpClient client = HttpClientBuilder.create().build();

                    // Create the request
                    final HttpUriRequest head = new HttpGet(tileURLString);

                    head.addHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.119 Safari/537.36");

                    // Perform the get method invocation
                    final HttpResponse response = client.execute(head);

                    // Check to see if we got success
                    final StatusLine line = response.getStatusLine();
                    if (line.getStatusCode() == 200) {

                        // The image has been downloaded
                        final HttpEntity entity = response.getEntity();

                        // Check if the entity is consistent
                        if (entity != null) {

                            // Read the entity
                            in = entity.getContent();

                            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                            out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
                            StreamUtils.copy(in, out);
                            out.flush();
                            final byte[] data = dataStream.toByteArray();
                            final ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

                            // Save the data to the filesystem cache
                            mTileWriter.saveFile(tileSource, tile, byteStream);

                            byteStream.reset();

                            StreamUtils.closeStream(in);
                            StreamUtils.closeStream(out);

                        } else {
                            Log.w(LOG_TAG, "No content downloading MapTile: " + tile);
                        }
                    } else {
                        Log.w(LOG_TAG, "Problem downloading MapTile: " + tile + " HTTP response: " + line);
                    }


                } catch (final UnknownHostException e) {
                    // no network connection
                    Log.w(LOG_TAG, "UnknownHostException downloading MapTile: " + tile + " : " + e);
                } catch (final FileNotFoundException e) {
                    Log.w(LOG_TAG, "Tile not found: " + tile + " : " + e);
                } catch (final IOException e) {
                    Log.w(LOG_TAG, "IOException downloading MapTile: " + tile + " : " + e);
                } catch (final Throwable e) {
                    Log.e(LOG_TAG, "Error downloading MapTile: " + tile, e);
                }
            }
        }
        return fileName;
    }

    private String serveDirectoryList(String uri) {
        String documentRoot=webServer.getDocumentRoot();
        if (uri.startsWith("/tiles/")) {
            documentRoot=Environment.getExternalStorageDirectory().getPath()+"/osmdroid/";
        }
        File path=new File(documentRoot + File.separator + uri.substring(1));
        File[] files=path.listFiles();
        String html="<html><head></head>";
        html+="<body>";
        for(File file:files) {
            String fileName=file.getName();
            if (file.isDirectory()) {
                fileName+="/";
            } else {
                fileName=fileName.replace("null.tile",".png").replace(".png.tile",".png");
            }
            html+="<a href=\"" + fileName + "\">" + fileName + "</a><br/>";
        }
        html+="</body></html>";
        return html;
    }
}
