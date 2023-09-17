package ui.console;

import logic.Request;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that uses to make a request form array of words that user entered
 * All methods which their name starts with set use to set the information of Request filed of class
 */
public class RequestMaker {
    private String[] input ;
    private boolean[] inputCheck ;
    private Request request ;
    private boolean displayResponseHeaders ;
    private boolean displayResponseEntity ;
    private boolean downloadResponse ;
    private boolean saveRequest ;
    private String fileAddressToSaveResponse ;
    private String groupNameToSaveRequest ;


    public RequestMaker(String[] input) throws Exception {
        this.input = input;
        init();
        callInputCheckFillerMethods();
        if(doesInputCheckHasFalseValue()) {
            throw new Exception("Invalid command !");
        }

    }


    private int[] findIndexesThatPatternIsLocated(String ... pattern){

        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i =0 ; i<input.length ; i++){
            for (String s : pattern) {
                if(!inputCheck[i]  && input[i].equals(s)) {
                    indexes.add(i);
                    inputCheck[i] = true;
                }
            }
        }

        int[] arrayToReturn = new int[indexes.size()];
        for (int i = 0 ; i< indexes.size() ; i++){
            arrayToReturn[i] = indexes.get(i);
        }

        return arrayToReturn ;
    }

    private void setUrl(){
        request.setUrl(input[1]);
        inputCheck[1] = true ;


    }

    private void setAllHeaders() throws Exception {
        HashMap<String , String> headers = new HashMap<>();
        int[] indexes = findIndexesThatPatternIsLocated("-H","--header");

        Pattern pattern =  Pattern.compile("^(.*):(.*)$");

        for (int index : indexes) {
                Matcher matcher = pattern.matcher(input[index + 1]);
                if (index+1 <inputCheck.length &&!inputCheck[index+1] && matcher.matches()) {
                    headers.put(matcher.group(1) , matcher.group(2).trim());
                    inputCheck[index + 1] = true;
                }
                else
                    throw new Exception("Invalid format for request header !");
        }

        request.setMapOfHeaders(headers);

    }

    private void setFollowRedirectArgument(){
        if(findIndexesThatPatternIsLocated("-f").length > 0)
            request.setFollowRedirect(true);
    }


    private void setDisplayResponseHeaders() {
        if(findIndexesThatPatternIsLocated("-i").length > 0)
            displayResponseHeaders = true ;
    }

    public void setDisplayResponseHeaders(boolean displayResponseHeaders) {
        this.displayResponseHeaders = displayResponseHeaders;
    }

    private void setDisplayResponseEntity(){
        if(findIndexesThatPatternIsLocated("-I").length > 0) {
            displayResponseEntity = false;
            displayResponseHeaders = true;
        }
        else displayResponseEntity = true ;
    }

    public void setDisplayResponseEntity(boolean displayResponseEntity) {
        this.displayResponseEntity = displayResponseEntity;
    }

    private void setResponseDownload() throws Exception {
        int[] indexes = findIndexesThatPatternIsLocated("-O" , "--output");

        if (indexes.length == 1) {

            downloadResponse = true;

            if (input.length >indexes[0] + 1 && !inputCheck[indexes[0] + 1]) {
                inputCheck[indexes[0] + 1] = true;
                fileAddressToSaveResponse = input[indexes[0] + 1];
            }
        }

            else if (indexes.length > 1)
                throw new Exception("Invalid number of output argument");
        }


    private void setMethodOfRequest() throws Exception {
        int[] indexes = findIndexesThatPatternIsLocated("-M","--method");

        if(indexes.length ==1)
            switch (input[indexes[0]+1]){

                case "GET" :
                    inputCheck[indexes[0]+1] = true ;
                    break;
                case "POST" :
                    request.setMethod("Post");
                    inputCheck[indexes[0]+1] = true ;
                    break;
                case "PUT" :
                    request.setMethod("Put");
                    inputCheck[indexes[0]+1] = true ;
                    break;
                case "PATCH" :
                    request.setMethod("Patch");
                    inputCheck[indexes[0]+1] = true ;
                    break;
                case "DELETE" :
                    request.setMethod("Delete");
                    inputCheck[indexes[0]+1] = true ;
                    break;
                default:
                    throw new Exception("Method not found !");

            }
        else if(indexes.length > 1)
            throw new Exception("Maximum one input method can be exist !");


    }

    /**
     * A method to checks whether number of entered body is valid or not.
     * we can have only one body type . for json and binary we should have only one argument . for from data we can hve more than one
     * @param multiPart number of form data arguments
     * @param json number of json arguments
     * @param binary number of form binary arguments
     */
    private boolean isNumberOfBodyTypeValid(int multiPart , int json , int binary){
        if ((multiPart > 0 && json > 0 )||(multiPart > 0 && binary > 0 )||(json > 0 && binary > 0 ))
            return false ;
        return true ;
    }

    /**
     *  int[] indexes = findIndexesThatPatternIsLocated("-d","--data");
     * @param indexes indexes of input array which are "-d" or "--data"
     */
    private void setMultiPartFormDataOfRequest(int[] indexes) throws Exception {

        HashMap<String , String> filesPairs = new HashMap<>();
        HashMap<String , String> stringPairs = new HashMap<>();


        Pattern filePattern =  Pattern.compile("^(.*)=@(.*)$");
        Pattern stringPattern =  Pattern.compile("^(.*)=(.*)$");

        for (int index : indexes) {
            Matcher matcher ;

            if((!inputCheck[index+1]&&(matcher = filePattern.matcher(input[index+1])).matches())) {
                filesPairs.put(matcher.group(1) , matcher.group(2));
                inputCheck[index+1] = true ;
            }

            else if((!inputCheck[index+1] &&(matcher = stringPattern.matcher(input[index+1])).matches())){
                stringPairs.put(matcher.group(1) , matcher.group(2));
                inputCheck[index+1] = true ;

            }
            else
                throw new Exception("Invalid form of body !");
        }

        request.setEntityType("multiPart");
        request.setFilesPairs(filesPairs);
        request.setStringPairs(stringPairs);
    }

    /**
     *   int[] indexes = findIndexesThatPatternIsLocated("-j","--json");
     */
    private void setJsonOfRequest(int[] index) {
        int placeOfJson = index[0]+1 ;
        if(!inputCheck[placeOfJson]) {
            request.setEntityType("json");
            request.setJSon(input[placeOfJson].trim());
            inputCheck[placeOfJson] = true ;
        }
    }

    /**
     *   int[] indexes = findIndexesThatPatternIsLocated("upload");
     */
    private void setBinaryPathOfRequest(int[] index)  {
        int placeOfBinary = index[0]+1 ;
        if(!inputCheck[placeOfBinary]) {
            request.setEntityType("binary");
            request.setBinaryPath(input[placeOfBinary].trim());
            inputCheck[placeOfBinary] = true ;
        }
    }

    private void setBodyOfRequest() throws Exception {

        int [] indexesOfFormData = findIndexesThatPatternIsLocated("-d","--data");
        int [] indexesOfJson = findIndexesThatPatternIsLocated("-j","--json");
        int [] indexesOfBinary = findIndexesThatPatternIsLocated("--upload");

        if(isNumberOfBodyTypeValid(indexesOfFormData.length , indexesOfJson.length , indexesOfBinary.length)){
            if(indexesOfFormData.length > 0)
                setMultiPartFormDataOfRequest(indexesOfFormData);
            if(indexesOfJson.length == 1)
                setJsonOfRequest(indexesOfJson);
            if(indexesOfBinary.length == 1)
                setBinaryPathOfRequest(indexesOfBinary);
        }
        else throw new Exception("Invalid number of body !");
    }
    private void setSaveRequest() throws Exception {

        int[] indexes = findIndexesThatPatternIsLocated("-S" , "--save");

        if (indexes.length == 1) {
            if (inputCheck.length >indexes[0] + 1&&!inputCheck[indexes[0] + 1]) {
                saveRequest = true ;
                inputCheck[indexes[0] + 1] = true;
                groupNameToSaveRequest = input[indexes[0] + 1];
            }
            else throw new Exception("Group that request should save in it has not been entered !");
        }

        else if (indexes.length > 1)
            throw new Exception("Invalid number of save argument !");
    }
    private void init() {
        request = new Request("empty" , null , null , null ,
                null , "Get" , null , false , null);
        inputCheck = new boolean[input.length];
        inputCheck[0] = true ;
        displayResponseHeaders = false ;
        downloadResponse = false ;
        saveRequest = false ;
        fileAddressToSaveResponse = null ;
        groupNameToSaveRequest = null ;
        displayResponseEntity = true ;
    }

    /**
     * A method to call all the set methods and make request completely
     */
    private void callInputCheckFillerMethods() throws Exception {
        setAllHeaders();
        setUrl();
        setFollowRedirectArgument();
        setMethodOfRequest();
        setBodyOfRequest();
        setDisplayResponseHeaders();
        setSaveRequest();
        setDisplayResponseEntity();
        setResponseDownload();

    }

    /**
     * A method that checks whether entity type is "url encoded " or not
     */
    private boolean isEntityUrlEncoded(Request request){
        if(request.getMapOfHeaders().containsKey("Content-Type"))
           return (request.getMapOfHeaders().get("Content-Type").equals("application/x-www-form-urlencoded"));
        if((request.getFilesPairs()==null || request.getFilesPairs().size() == 0 ) && request.getEntityType().equals("multiPart"))
            return true ;
        return false ;
    }

    /**
     * A method that checks whether does input array contain any invalid argument or not
     */
    private boolean doesInputCheckHasFalseValue(){
        for (boolean b : inputCheck) {
            if(!b)
                return true ;
        }
        return false ;
    }

    public boolean shouldDisplayResponseHeaders() {
        return displayResponseHeaders;
    }

    public boolean shouldDownloadResponse() {
        return downloadResponse;
    }

    public boolean shouldSaveRequest() {
        return saveRequest;
    }
    public boolean shouldDisplayResponseEntity(){
        return displayResponseEntity;
    }
    public String getFileAddressToSaveResponse() {
        return fileAddressToSaveResponse;
    }

    public String getGroupNameToSaveRequest() {
        return groupNameToSaveRequest;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "RequestMaker{" +
                "input=" + Arrays.toString(input) +
                ", inputCheck=" + Arrays.toString(inputCheck) +
                ", request=" + request +
                ", displayResponseHeaders=" + displayResponseHeaders +
                ",displayResponseEntity="+displayResponseEntity+
                ", downloadResponse=" + downloadResponse +
                ", saveRequest=" + saveRequest +
                ", fileAddressToSaveResponse='" + fileAddressToSaveResponse + '\'' +
                ", groupNameToSaveRequest='" + groupNameToSaveRequest + '\'' +
                '}';
    }
}
