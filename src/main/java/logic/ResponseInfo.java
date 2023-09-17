package logic;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.Arrays;

/**
 * A class to store information of response
 */
public class ResponseInfo {
    /**
     * contentLength : The space that the response occupies
     */
    private CloseableHttpResponse response ;
    private Header[] headers ;
    private byte[] body ;
    private String statusCode ;
    private String contentLength ;
    private String timeOfSend ;



    public ResponseInfo(CloseableHttpResponse response) throws IOException {
        this.response = response;
        setContents();
    }

    private void setContents() throws IOException {
        headers = response.getAllHeaders();
        body = new byte[0];
        HttpEntity entity = response.getEntity();
        if(entity!=null)
        body = EntityUtils.toByteArray(entity);
        contentLength = body.length + " B";
        statusCode = response.getStatusLine().getStatusCode()+" "+response.getStatusLine().getReasonPhrase();


    }

    public Header[] getHeaders() {
        return headers;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setTimeOfSend(String timeOfSend) {
        this.timeOfSend = timeOfSend;
    }

    public String getTimeOfSend() {
        return timeOfSend;
    }

    public byte[] getBody() {
        return body;
    }

//
//    public String getBody() {
//        return body;
//    }

    @Override
    public String toString() {
        return "ResponseInfo{" +
                "response=" + response +
                ", headers=" + Arrays.toString(headers) +
                ", statusCode='" + statusCode + '\'' +
                ", contentLength='" + contentLength + '\'' +
                ", timeOfSend='" + timeOfSend + '\'' +
                '}';
    }
}