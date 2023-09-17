package ui.gui;

import logic.Request;
import java.io.File;
import java.util.Map;
import static ui.console.ConsoleView.getScanner;

/**
 * A class that gets a saved request and fill Request panel instance
 * Request panel is singleton class and have only one instance
 * All the methods which starts with "set" are used to set information of request panel
 */
public class RequestPanelMaker {
    RequestPanel requestPanel ;
    Request request ;

    public RequestPanelMaker(Request request) {
        this.request = request;
        requestPanel = RequestPanel.getInstance();
        initPanels();
    }

    /**
     * A method to call all the methods which use to set information
     */
    private void initPanels() {
        requestPanel.resetPanel();
        requestPanel.getHeaders().forEach(e-> System.out.println("1"+e.getKey()+"*****"+e.getTextValue()+"*****"+e.getFilePath()));
        requestPanel.getMultiPartFormData().forEach(e-> System.out.println("2"+e.getKey()+"*****"+e.getTextValue()+"*****"+e.getFilePath()));
        requestPanel.getUrlEncodedData().forEach(e-> System.out.println("3"+e.getKey()+"*****"+e.getTextValue()+"*****"+e.getFilePath()));
        System.out.println("\n\n");
        setMethod();
        setBody();
        setFollowRedirect();
        setHeaders();
        setUrl();
        requestPanel.getHeaders().forEach(e-> System.err.println("1"+e.getKey()+"*****"+e.getTextValue()+"*****"+e.getFilePath()+"\n"));
        requestPanel.getMultiPartFormData().forEach(e-> System.err.println("2"+e.getKey()+"*****"+e.getTextValue()+"*****"+e.getFilePath()+"\n"));
        requestPanel.getUrlEncodedData().forEach(e-> System.err.println("3"+e.getKey()+"*****"+e.getTextValue()+"*****"+e.getFilePath()+"\n"));

        System.out.println("\n\n\n");
    }


    private void setHeaders(){
        int i=0 ;
        for (Map.Entry<String, String> entry : request.getMapOfHeaders().entrySet()) {
            PairPanel.OneLinePairPanel data = new PairPanel.OneLinePairPanel(i,false,"Header");
            data.getKeyComponent().setText(entry.getKey());
            data.getTextValueComponent().setText(entry.getValue());
            requestPanel.getHeaders().add(data);
            i++;
        }
        requestPanel.getHeadersPanel().updatePanel();
    }
    private void setFollowRedirect(){

        MenuBar.getInstance().setFollowRedirect(request.shouldFollowRedirects());

    }
    private void setUrl(){

        requestPanel.getUrl().setText(request.getUrl());

    }

    private void setMethod(){
        int index = 0 ;
        switch(request.getMethod()){
            case "Get" :
                index = 0;
                break;
            case "Delete" :
                index = 1;
                break;
            case "Post" :
                index = 2;
                break;
            case "Put" :
                index = 3;
                break;
            case "Patch" :
                index = 4;
                break;

        }
        requestPanel.getMethod().setSelectedIndex(index);

        requestPanel.revalidate();
    }

    private void setBody(){
        switch (request.getEntityType()){
            case "multiPart":
                setMultiPartFormDataBody();
                break;
            case "urlEncoded":
                setUrlEncodedBody();
                break;
            case "binary":
                setBinaryBody();
                break;
            case "json":
                setJsonBody();
                break;
            case "empty":
                setEmptyBody();
                break;
        }

    }

    private void setMultiPartFormDataBody() {

        requestPanel.getBodyType().setSelectedIndex(0);
        requestPanel.setBodyTypeName("Form Data");

        int i=0 ;
        for (Map.Entry<String, String> entry : request.getStringPairs().entrySet()) {
            PairPanel.OneLinePairPanel data = new PairPanel.OneLinePairPanel(i,true,"Form Data");
            data.getKeyComponent().setText(entry.getKey());
            data.getTextValueComponent().setText(entry.getValue());
            requestPanel.getMultiPartFormData().add(data);
            i++;
        }
        int j =0 ;
        for (Map.Entry<String, String> entry : request.getFilesPairs().entrySet()) {
            PairPanel.OneLinePairPanel data = new PairPanel.OneLinePairPanel(j,true,"Form Data");
            data.getTypeOfKey().setSelectedItem("File");
            data.getKeyComponent().setText(entry.getKey());
            File file = new File(entry.getValue());
            data.setFilePath(file.getPath());
            data.getSelectFile().setText(file.getName());
            requestPanel.getMultiPartFormData().add(data);
            j++;
        }
        requestPanel.getMultiPartPanel().updatePanel();
    }

    private void setUrlEncodedBody() {

        requestPanel.getBodyType().setSelectedIndex(1);
        requestPanel.setBodyTypeName("Url Encoded");

        int i=0 ;
        for (Map.Entry<String, String> entry : request.getStringPairs().entrySet()) {
            PairPanel.OneLinePairPanel data = new PairPanel.OneLinePairPanel(i,false,"Url Encoded");
            data.getKeyComponent().setText(entry.getKey());
            data.getTextValueComponent().setText(entry.getValue());
            requestPanel.getUrlEncodedData().add(data);
            i++;
        }
        requestPanel.getUrlEncodedPanel().updatePanel();
        requestPanel.getUrlEncodedData().forEach(x-> System.out.println(x.getKey()+"*"+x.getTextValue()+"\n"));
        requestPanel.getUrlEncodedPanel().getLines().forEach(n-> System.out.println(n.getKey()+"____"+n.getTextValue()));
    }

    private void setBinaryBody() {

        requestPanel.getBodyType().setSelectedIndex(2);
        requestPanel.setBodyTypeName("Binary");

        if(request.getBinaryPath()!=null){
            File file = new File(request.getBinaryPath());
            requestPanel.getBinaryPanel().setFileOfBinaryPanel(file);
            requestPanel.getBinaryPanel().changeLookOfBinaryPanel();


        }
    }

    private void setJsonBody() {

        requestPanel.getBodyType().setSelectedIndex(3);
        requestPanel.setBodyTypeName("JSON");

        if(request.getJSon()!=null)
            requestPanel.getJson().setText(request.getJSon());
    }

    private void setEmptyBody(){

        requestPanel.getBodyType().setSelectedIndex(4);
        requestPanel.setBodyTypeName("Empty");
    }


}

