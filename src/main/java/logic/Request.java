package logic;

import java.io.Serializable;
import java.util.HashMap;

/**
 * A class that implements serializable and will be use to save requests information and load it aging to programme
 */
public class Request implements Serializable {
    private String entityType ;
    private HashMap<String,String> filesPairs ;
    private HashMap<String,String> stringPairs;
    private String binaryPath ;
    private String jSon ;
    private String method ;
    private String url ;
    private boolean followRedirect ;
    private HashMap<String,String> mapOfHeaders ;

    public Request(String entityType , HashMap<String, String> filesPairs , HashMap<String, String> stringPairs
            , String binaryPath , String jSon , String method , String url , boolean followRedirect , HashMap<String
            , String> mapOfHeaders) {
        this.entityType = entityType;
        this.filesPairs = filesPairs;
        this.stringPairs = stringPairs;
        this.binaryPath = binaryPath;
        this.jSon = jSon;
        this.method = method;
        this.url = url;
        this.followRedirect = followRedirect;
        this.mapOfHeaders = mapOfHeaders;
    }

    public String getEntityType() {
        return entityType;
    }

    public HashMap<String, String> getFilesPairs() {
        return filesPairs;
    }

    public HashMap<String, String> getStringPairs() {
        return stringPairs;
    }

    public String getBinaryPath() {
        return binaryPath;
    }

    public String getJSon() {
        return jSon;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public boolean shouldFollowRedirects() {
        return followRedirect;
    }

    public HashMap<String, String> getMapOfHeaders() {
        return mapOfHeaders;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setFilesPairs(HashMap<String, String> filesPairs) {
        this.filesPairs = filesPairs;
    }

    public void setStringPairs(HashMap<String, String> stringPairs) {
        this.stringPairs = stringPairs;
    }

    public void setBinaryPath(String binaryPath) {
        this.binaryPath = binaryPath;
    }

    public void setJSon(String jSon) {
        this.jSon = jSon;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFollowRedirect(boolean followRedirect) {
        this.followRedirect = followRedirect;
    }

    public void setMapOfHeaders(HashMap<String, String> mapOfHeaders) {
        this.mapOfHeaders = mapOfHeaders;
    }

    @Override
    public String toString() {
        return "Request{" +
                "entityType='" + entityType + '\'' +
                ", filesPairs=" + filesPairs +
                ", stringPairs=" + stringPairs +
                ", binaryPath='" + binaryPath + '\'' +
                ", jSon='" + jSon + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", followRedirect=" + followRedirect +
                ", mapOfHeaders=" + mapOfHeaders +
                '}';
    }
}
