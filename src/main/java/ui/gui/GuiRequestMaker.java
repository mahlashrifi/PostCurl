package ui.gui;

import logic.Request;
import java.util.HashMap;

/**
 * It is exactly like RequestMaker class that exist in console package which fill information of a request but
 * in this case according to entered data of Gui not console
 */
public class GuiRequestMaker {
    private Request request ;
    private RequestPanel requestPanel ;

    public GuiRequestMaker(RequestPanel requestPanel) throws Exception {
        this.requestPanel = requestPanel;
        request = new Request("empty" , null , null , null ,
                null , "Get" , null , false , null);
        init();
    }

    private void init() throws Exception {
        setBodyType();
        setBody();
        setMethod();
        setUrl();
        setHeaders();

    }
    private void setBodyType(){
        switch (requestPanel.getBodyTypeName()){
            case "Form Data":
                request.setEntityType("multiPart");
                break;
            case "Url Encoded":
                request.setEntityType("urlEncoded");
                break;
            case "Binary":
                request.setEntityType("binary");
                break;
            case "JSON":
                request.setEntityType("json");
                break;
            default:
                request.setEntityType("empty");

        }

    }
    private void setUrl(){
        if(!requestPanel.getSendPanel().isTextFilledEmpty()) {

            StringBuilder builder = new StringBuilder();
            builder.append(requestPanel.getUrl().getText());

            builder.append("?");
            builder.append(requestPanel.getQuery().getText().replaceAll("\n","&"));

            request.setUrl(builder.toString());
        }
    }
    private void setBody(){
        switch (request.getEntityType()){
            case "multiPart":
                setMultiPartBody();
                break;
            case "binary":
                setBinaryBody();
                break;
            case "json":
                setJsonBody();
                break;
            case "urlEncoded":
                setUrlEncodedBody();
                break;
            case "empty":
                break;
        }



    }

    private void setUrlEncodedBody() {

        HashMap<String, String> urlEncodedData = new HashMap<>();

        for (PairPanel.OneLinePairPanel data : requestPanel.getUrlEncodedData()) {
            if (data.isActive() && !(data.getKey().equals("") && data.getTextValue().equals("")))
                urlEncodedData.put(data.getKey() , data.getTextValue());
        }
        request.setStringPairs(urlEncodedData);
    }

    private void setJsonBody() {
        String json = requestPanel.getJson().getText();
        if(!json.equals(""))
            request.setJSon(json);
    }

    private void setBinaryBody() {
        if(requestPanel.getBinaryPath()!=null)
            request.setBinaryPath(requestPanel.getBinaryPath());
    }

    private void setMultiPartBody() {
        HashMap<String,String> stringPairs = new HashMap<>();
        HashMap<String,String> filePairs = new HashMap<>();

        for (PairPanel.OneLinePairPanel data : requestPanel.getMultiPartFormData()) {
            if(data.isActive() && data.isValueAFile() && data.getFilePath()!=null)
                filePairs.put(data.getKey(),data.getFilePath());

            else if(data.isActive() && !data.isValueAFile() && !(data.getKey().equals("") && data.getTextValue().equals("")))
                stringPairs.put(data.getKey(),data.getTextValue());

        }

        request.setStringPairs(stringPairs);
        request.setFilesPairs(filePairs);
    }

    private void setMethod(){
        String method ="Get";
        switch(requestPanel.getMethod().getSelectedIndex()){
            case 0 :
                method = "Get";
                break;
            case 1 :
                method = "Delete";
                break;
            case 2 :
                method = "Post";
                break;
            case 3 :
                method = "Put";
                break;
            case 4 :
                method = "Patch";
                break;


        }
        request.setMethod(method);
    }
    private void setHeaders(){
        HashMap<String, String> headers = new HashMap<>();

        for (PairPanel.OneLinePairPanel header : requestPanel.getHeaders()) {
            if(header.isActive() && !(header.getKey().equals("") && header.getTextValue().equals("")))
                headers.put(header.getKey(),header.getTextValue());
        }

        request.setMapOfHeaders(headers);
    }

    public Request getRequest() {
        return request;
    }

}

