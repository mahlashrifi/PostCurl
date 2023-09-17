package logic;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.time.LocalDateTime.*;

/**
 * A class to send a request according to entered information
 */
public class Core {
    private ArrayList<File> groupsOfRequests;
    private static Core core = null;

    private Core() throws Exception {
        groupsOfRequests = new ArrayList<>();
        loadGroupsOfRequests();
    }

    public static Core getInstance() throws Exception {
        if (core == null)
            core = new Core();
        return core;
    }

    /**
     * A method to send a request
     * @param primaryRequest information of request
     * @return response of request
     */
    public CloseableHttpResponse getResponseOfRequest(Request primaryRequest) throws Exception {
        Method method = findMethodOfRequest(primaryRequest);
        HttpEntity entity = makeEntityOfRequest(primaryRequest);
        String url = editUrl(primaryRequest.getUrl());
        boolean followRedirect = primaryRequest.shouldFollowRedirects();

        Header[] headers = makeHeaders(primaryRequest.getMapOfHeaders());

        HttpRequestBase request;
        switch (method) {
            case GET:
                request = new HttpGet(url);
                break;
            case POST:
                request = new HttpPost(url);
                ((HttpPost) request).setEntity(entity);
                break;
            case PUT:
                request = new HttpPut(url);
                ((HttpPut) request).setEntity(entity);
                break;
            case PATCH:
                request = new HttpPatch(url);
                ((HttpPatch) request).setEntity(entity);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            default:
                request = new HttpRequestBase() {
                    @Override
                    public String getMethod() {
                        return null;
                    }
                };
                break;
        }

        request.setHeaders(headers);

        try {

            CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new MyRedirectStrategy()).build();
            return httpClient.execute(request);
        } catch (ClientProtocolException e) {
            throw new Exception("Could not resolve host: " + primaryRequest.getUrl());
        }


    }

    public static class MyRedirectStrategy extends DefaultRedirectStrategy {
        @Override
        public boolean isRedirected(HttpRequest request , HttpResponse response , HttpContext context) {
            return false;
        }

    }


    /**
     * A "method" that the request should be sent with
     * @return one of the Method class members that is enum
     */
    public Method findMethodOfRequest(Request request) throws Exception {
        switch (request.getMethod()) {
            case "Get":
                return Method.GET;
            case "Post":
                return Method.POST;
            case "Put":
                return Method.PUT;
            case "Patch":
                return Method.PATCH;
            case "Delete":
                return Method.DELETE;
            default:
                throw new Exception("Invalid method for sending request !");
        }

    }

    /**
     * A method to add http to url . and check protocol is valid or not
     * @param url input
     * @return edited input
     */
    public String editUrl(String url) throws Exception {
        Pattern pattern = Pattern.compile("(\\w+):(\\/*)(.+)");
        Matcher matcher = pattern.matcher(url);

        StringBuilder editedUrl = new StringBuilder();

        if (matcher.matches()) {

            if (matcher.group(2).toCharArray().length == 0)
                throw new Exception("Port number ended with '" + matcher.group(3).toCharArray()[0] + "'");
            if (matcher.group(2).toCharArray().length > 3)
                throw new Exception("Bad URL !");
            if (!(matcher.group(1).equals("http") || matcher.group(1).equals("https")))
                throw new Exception("Protocol " + "\"" + (matcher.group(1) + "\"" + " not supported ."));

            editedUrl.append(matcher.group(1)).append("://").append(matcher.group(3));
        } else editedUrl.append("http://").append(url);
        return editedUrl.toString();

    }

    /**
     * A method to make entity of request according to type of it s entity
     * @return created entity
     */
    public HttpEntity makeEntityOfRequest(Request request) throws Exception {
        switch (request.getEntityType()) {
            case "multiPart":
                return makeMultiPartFormDataEntity(request.getFilesPairs() , request.getStringPairs());
            case "binary":
                addBinaryHeaders(request);
                return makeBinaryEntity(request.getBinaryPath());
            case "json":
                addJsonHeaders(request);
                return makeJsonEntity(request.getJSon());
            case "urlEncoded":
                return urlEncodedEntity(request.getStringPairs());
            case "empty":
                return null;
            default:
                throw new Exception("Invalid entity exception !");

        }

    }

    /**
     * A method to add Content-type and Accept header automatically if entered data is json
     */
    public void addJsonHeaders(Request request) {
        if (request.getEntityType().equals("json")) {
            request.getMapOfHeaders().put("Accept" , "application/json");
            request.getMapOfHeaders().put("Content-type" , "application/json");

        }

    }

    /**
     * A method to add Content-type and Accept header automatically if entered data is binary
     */
    public void addBinaryHeaders(Request request) {
        if (request.getEntityType().equals("binary")) {
            request.getMapOfHeaders().put("Accept" , "application/octet-stream");
            request.getMapOfHeaders().put("Content-type" , "application/octet-stream");
        }
    }

    public HttpEntity makeMultiPartFormDataEntity(HashMap<String, String> filesPairs , HashMap<String, String> stringPairs) {


        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        ContentType plainAsciiContentType = ContentType.create("text/plain" , Consts.ASCII);
        if (stringPairs != null)
            for (Map.Entry<String, String> entry : stringPairs.entrySet()) {
                entityBuilder.addPart(entry.getKey() , new StringBody(entry.getValue() , plainAsciiContentType));
            }
        if (filesPairs != null)
            for (Map.Entry<String, String> entry : filesPairs.entrySet()) {
                File file = new File(entry.getValue());
                entityBuilder.addPart(entry.getKey() , new FileBody(file));
            }

        return entityBuilder.build();
    }

    public HttpEntity makeBinaryEntity(String binaryPath) {
        File file = new File(binaryPath);
        return new FileEntity(file);
    }

    public HttpEntity makeJsonEntity(String json) {
        return new StringEntity(json , ContentType.APPLICATION_JSON);
    }

    public HttpEntity urlEncodedEntity(HashMap<String, String> stringPairs) throws UnsupportedEncodingException {

        List<NameValuePair> data = new ArrayList<>();

        for (Map.Entry<String, String> entry : stringPairs.entrySet()) {
            data.add(new BasicNameValuePair(entry.getKey() , entry.getValue()));
        }
        return new UrlEncodedFormEntity(data);
    }

    /**
     * A method to make an array from headers of request
     * @param mapOfHeaders map of headers
     * @return array of headers
     */
    public Header[] makeHeaders(HashMap<String, String> mapOfHeaders) {
        if (mapOfHeaders == null)
            return null;

        Header[] headers = new Header[mapOfHeaders.size()];
        int counter = 0;
        for (Map.Entry<String, String> entry : mapOfHeaders.entrySet()) {
            headers[counter] = new BasicHeader(entry.getKey() , entry.getValue());
            counter++;
        }
        return headers;
    }

    /**
     * A method to download response of request .
     * @param entity  entity which should be save
     * @param filePath filePath than entity should save on it . if filePath is null . current time will be the name of file
     */
    public void downloadResponse(HttpEntity entity , String filePath) {
        File fileToSaveInIt;
        if (filePath == null) {
            DateTimeFormatter currentTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
            LocalDateTime now = now();
            String fileName = "src/main/resources/SavedResponses/output_" + "[ " + currentTime.format(now) + " ]";
            fileToSaveInIt = new File(fileName);
        } else fileToSaveInIt = new File("src/main/resources/SavedResponses/" + filePath);
        try {

            if (entity != null) {
                BufferedInputStream in = new BufferedInputStream(entity.getContent());
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileToSaveInIt));
                int intByte;
                while ((intByte = in.read()) != -1) out.write(intByte);
                out.flush();
                in.close();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to create new group of requests
     * @param groupName name of group
     */
    public void createNewGroup(String groupName) {
        new File("src/main/resources/SavedRequests/" + groupName).mkdir();
        File file = new File("src/main/resources/SavedRequests/" + groupName);
        groupsOfRequests.add(file);
    }

    /**
     * A method that checks whether a group does exist in list of groups or not
     * @param groupName name of group
     */
    public boolean doesGroupExist(String groupName) {
        return Files.isDirectory(Paths.get("src/main/resources/SavedRequests/" + groupName));

    }

    /**
     * load groups
     */
    public void loadGroupsOfRequests() throws Exception {
        DirectoryStream<Path> directoryStream =
                Files.newDirectoryStream(Paths.get("src/main/resources/SavedRequests"));
        for (Path p : directoryStream)
            if (Files.isDirectory(p))
                createNewGroup(p.getFileName().toString());
    }

    /**
     * A method to get all requests of a group
     * @param groupName name of group
     * @return array of requests
     */
    public ArrayList<Request> getRequestsOfAGroup(String groupName) throws Exception {
        if (doesGroupExist(groupName)) {
            ArrayList<Request> requests = new ArrayList<>();
            DirectoryStream<Path> directoryStream =
                    Files.newDirectoryStream(Paths.get("src/main/resources/SavedRequests/" + groupName));
            for (Path p : directoryStream) {
                requests.add(getASaveRequest(groupName , p.getFileName().toString()));
            }
            return requests;
        }
        throw new Exception("There is not any group with entered name !");
    }

    /**
     * A method to get names of all requests of an special group
     * @param groupName group name
     * @return array list of request names
     */
    public ArrayList<String> getRequestsNameOfAGroup(String groupName) throws Exception {
        if (doesGroupExist(groupName)) {
            ArrayList<String> requests = new ArrayList<>();
            DirectoryStream<Path> directoryStream =
                    Files.newDirectoryStream(Paths.get("src/main/resources/SavedRequests/" + groupName));
            for (Path p : directoryStream) {
                requests.add(p.getFileName().toString());
            }
            return requests;
        }
        throw new Exception("There is not any group with entered name !");


    }

    /**
     * A method to save request in special group
     */
    public void saveRequest(Request request , String groupName , String requestName) throws IOException {
        String fileAddress;
        if (requestName == null) {
            DateTimeFormatter currentTime = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH-mm-ss");
            LocalDateTime now = now();
            requestName = "[" + currentTime.format(now) + "]";
        }

        fileAddress = "src/main/resources/SavedRequests/" + groupName + "/" + requestName;

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(fileAddress)));
        out.writeObject(request);
        out.close();
    }

    /**
     * A method to get a saved request
     * @param groupName name of group that request does exist in it
     * @param requestName name of request
     * @return request
     */
    public Request getASaveRequest(String groupName , String requestName) throws IOException, ClassNotFoundException {
        String fileAddress = "src/main/resources/SavedRequests/" + groupName + "/" + requestName;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileAddress)));
        Request request = (Request) in.readObject();
        in.close();
        return request;
    }

    public ArrayList<File> getGroupsOfRequests() {
        return groupsOfRequests;
    }
}



