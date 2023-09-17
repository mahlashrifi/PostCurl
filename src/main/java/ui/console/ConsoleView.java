package ui.console;

import logic.Core;
import logic.Request;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that analyzes user input information and executes the appropriate command
 */
public class ConsoleView {
    private static Scanner scanner;
    private Core core;
    private RequestMaker requestMaker ;

    public ConsoleView() throws Exception {
        scanner = new Scanner(System.in);
        core = Core.getInstance();

    }

    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * A method tat get command and call appropriate method according to user request till "End" command enter
     */
    public void run() {
        while (true) {
            String[] input = CommandTranslator.translateCommandline(scanner.nextLine());

            if(input.length==1 && input[0].equalsIgnoreCase("End"))
                System.exit(0);
            try {
                isFirstArgumentValid(input);
                if (input.length == 2 && input[1].equals("list")) {
                    showListOfGroups();
                }else if (input.length == 2 && (input[1].equals("--help")||input[1].equals("-h"))){
                    showHelps();
                }
                else if (input.length == 3 && input[1].equals("create")) {
                    makeNewGroup(input);
                } else if (input.length == 3 && input[1].equals("list")) {
                    showRequestsOfAGroup(input);
                } else if (input.length > 3 && input[1].equals("fire")) {
                    runSelectedRequests(input);
                } else {
                    showResponseOfRequest(new RequestMaker(input));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * A method to show help
     */
    private void showHelps() throws FileNotFoundException {
        File file = new File("./src/main/resources/help.txt");
        Scanner scanner = new Scanner(file);
        String input = "";

        while (scanner.hasNextLine()){
            input = scanner.nextLine();
            System.out.println(input);
        }

    }

    /**
     * A method that checks whether first word is "Star" or not
     */
    private void isFirstArgumentValid(String[] input)throws Exception{
        if (input.length < 2 || !input[0].equalsIgnoreCase("Star"))
            throw new Exception("Invalid Command !");

    }

    /**
     * A method to show lists of groups
     */
    private void showListOfGroups () {
        int counter = 1 ;
        for (File group : core.getGroupsOfRequests()) {
            System.out.println(counter+" ) "+group.getName());
            counter++;
        }
    }

    /**
     * A method to  mae new group
     */
    private void makeNewGroup(String[] input) throws Exception {
      if(core.getGroupsOfRequests() != null)
          for (File group : core.getGroupsOfRequests()) {
              if(group.getName().equals(input[2]))
                  throw new Exception("Selective name for new group is already exist !");
        }
        System.out.println(input[2]);
        core.createNewGroup(input[2]);
    }

    /**
     * A method to show requests of an special group
     */
    private void showRequestsOfAGroup (String[] input) throws Exception {
        int i  = 1;
        for (Request request : core.getRequestsOfAGroup(input[2])) {
            System.out.println(i+" . "+getRequestInfo(request)+"\n");
            i++ ;
        }
    }

    /**
     * A method that use to print information of a request
     */
    private String getRequestInfo(Request request){
        StringBuilder requestInfo = new StringBuilder();
        requestInfo.append(" url: ").append(request.getUrl()).append(" ");
        requestInfo.append("| method: ").append(request.getMethod()).append(" ");

        requestInfo.append("| headers:");
        for(Map.Entry<String,String> entry : request.getMapOfHeaders().entrySet()){
            requestInfo.append(" \"").append(entry.getKey()).append("= ").append(entry.getValue()).append("\"");
        }

        switch (request.getEntityType()){
            case "multiPart":
                requestInfo.append("| multi part form data as body :");
                for(Map.Entry<String,String> entry : request.getFilesPairs().entrySet()){
                    requestInfo.append(" \"").append(entry.getKey()).append("= ").append(entry.getValue()).append("\"");
                }
                for(Map.Entry<String,String> entry : request.getStringPairs().entrySet()){
                    requestInfo.append(" \"").append(entry.getKey()).append("= ").append(entry.getValue()).append("\"");
                }
                break;
            case "urlEncoded" :
                requestInfo.append("| url encoded as body :");
                for(Map.Entry<String,String> entry : request.getStringPairs().entrySet()){
                    requestInfo.append(" \"").append(entry.getKey()).append("= ").append(entry.getValue()).append("\"");
                }
                break;
            case "binary":
                requestInfo.append("| path of the binary file as body :");
                requestInfo.append(" ").append(request.getBinaryPath());
                break;
            case "json":
                requestInfo.append("| json object as body :");
                requestInfo.append(" ").append(request.getJSon());
                break;
            case "empty":
                break;
        }
        requestInfo.append("| follow redirect:");
        requestInfo.append(request.shouldFollowRedirects()?" true":" false");

        return requestInfo.toString();
    }

    /**
     * A method to execute a saved request
     */
    private void runSelectedRequests(String[] input) throws Exception {
        ArrayList<Request> requests = core.getRequestsOfAGroup(input[2]);
        for (int i = 3 ; i<input.length ; i++) {
            try {
                Pattern pattern = Pattern.compile("(-I|-i)?(\\d+)");
                Matcher matcher = pattern.matcher(input[i]);
                if (matcher.matches()) {
                    if (matcher.group(1) == null) {
                        showResponseOfRequest(makeARequestMakerForSavedRequests(requests.get(Integer.parseInt(matcher.group(2)) - 1),true,false));
                    }
                    else if (matcher.group(1) .equals("-i") ) {
                        showResponseOfRequest(makeARequestMakerForSavedRequests(requests.get(Integer.parseInt(matcher.group(2)) - 1),true,true));
                    }
                    else if (matcher.group(1) .equals("-I")) {
                        showResponseOfRequest(makeARequestMakerForSavedRequests(requests.get(Integer.parseInt(matcher.group(2)) - 1),false,true));
                    }
                }
                else throw new Exception("Invalid format to choose a request !valid formats : -I+number, -i + number, number ");
            }

            catch (IndexOutOfBoundsException | NumberFormatException e){
                throw new Exception("Invalid index of request!");
           }
        }
    }

    /**
     * A method to show response
     */
    private void showResponseOfRequest(RequestMaker requestMaker) throws Exception {

        boolean sendAgainToNewHost = true;

        boolean followRedirect ;

        boolean isRedirect = false;
        CloseableHttpResponse response = null;

        while (sendAgainToNewHost) {
            if (isRedirect) {
                Request request = requestMaker.getRequest();
                request.setUrl(findNewLocation(response.getAllHeaders()));
                requestMaker.setRequest(request);
            }

            followRedirect = requestMaker.getRequest().shouldFollowRedirects();

            Request request = requestMaker.getRequest();

            response = core.getResponseOfRequest(request);
            HttpEntity entity = response.getEntity();


            if (requestMaker.shouldDownloadResponse()) {
                core.downloadResponse(entity , requestMaker.getFileAddressToSaveResponse());
                System.err.println("Response body has downloaded !");
            } else {

                if (requestMaker.shouldSaveRequest()) {
                    core.saveRequest(request , requestMaker.getGroupNameToSaveRequest() , null);
                    System.err.println("Request saved correctly in group: " + requestMaker.getGroupNameToSaveRequest());
                }

                if (requestMaker.shouldDisplayResponseHeaders()) {
                    System.out.println(response.getStatusLine());
                    for (Header header : response.getAllHeaders()) {
                        System.out.println(header.getName() + ": " + header.getValue());
                    }
                    System.out.println();
                }

                if (requestMaker.shouldDisplayResponseEntity() && entity!=null)
                    System.out.println(EntityUtils.toString(entity));

            }

            if (followRedirect && (response.getStatusLine().getStatusCode() / 100) == 3) {
                sendAgainToNewHost = true;
                isRedirect = true;
            } else sendAgainToNewHost = false;
        }
    }

    /**
     * an Auxiliary method to make a Request maker and use it for executing a request
     * @return a request maker
     */
    public RequestMaker makeARequestMakerForSavedRequests(Request request , boolean showEntity , boolean showHeaders) throws Exception {
        RequestMaker requestMaker = new RequestMaker(CommandTranslator.translateCommandline("star "+request.getUrl()+"i"));
        requestMaker.setRequest(request);
        requestMaker.setDisplayResponseEntity(showEntity);
        requestMaker.setDisplayResponseHeaders(showHeaders);
        System.err.println(request);

        return requestMaker ;

    }


    public static String findNewLocation(Header[] headers) throws Exception {
        for (Header header : headers) {
            if(header.getName().equals("Location"))
                return header.getValue();
        }
        throw new Exception("Can not find new Location!");
    }






    }

