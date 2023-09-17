
import logic.Core;
import logic.Request;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CoreTest {
    private static Core core;

    static {
        try {
            core = Core.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    // tests statusCode via get method with no header and body
    public void statusCodeTest() throws Exception{
        HashMap<Integer, String> statuses = Sources.getStatuses();
        for (Map.Entry<Integer, String> entry : statuses.entrySet()) {
            String url = new StringBuilder("http://apapi.haditabatabaei.ir/status/").append(entry.getKey()).toString();

            Request request = new Request("empty" , null , null , null ,
                    null , "Get" , url , true , null);

            CloseableHttpResponse response = null;
            try {
                response = core.getResponseOfRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int actualValueOfCode = response.getStatusLine().getStatusCode();
            int expectedValueOfCode = entry.getKey();

            Assert.assertEquals(expectedValueOfCode , actualValueOfCode);

            String actualValueOfReason = response.getStatusLine().getReasonPhrase();
            String expectedValueOfReason = entry.getValue();

            Assert.assertEquals(expectedValueOfReason , actualValueOfReason);
        }
    }


    public static void main(String[] args) throws Exception {
        // getMethodTest();
        // getMethodTest2();
        // getMethodTest3();
        // postMethodTest1();
        // postMethodTest();
        // postMethodTest2();
        // deleteMethodTest();
        // putMethodTest();
        // patchMethodTest();
        // createNewGroupTest();
        //editUrlTest();

    }


    //request : query         response : json
    public static void getMethodTest() {
        String url = "http://apapi.haditabatabaei.ir/tests/get/json";
        Request request = new Request("empty" , null , null , null ,
                null , "Get" , url , true , null);

        try (CloseableHttpResponse response = core.getResponseOfRequest(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null)
                System.out.println(EntityUtils.toString(entity));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void getMethodTest2() throws Exception {
        String url = "http://apapi.haditabatabaei.ir/tests/get/buffer/pic?bgcolor=rgb(180,115,60)&text=World";
        //String url = "https://www.dhakatribune.com/showtime/2019/01/01/in-pictures-new-year-wishes-from-tv-stars";

        Request request = new Request("empty" , null , null , null ,
                null , "Get" , url , true , null);

        core.downloadResponse(core.getResponseOfRequest(request).getEntity(),"test.png");
        }

    //request :        response : text / headers
    public static void getMethodTest3() throws Exception {
        String url = "http://apapi.haditabatabaei.ir/tests/get/file";
        Request request = new Request("empty" , null , null , null ,
                null , "Get" , url , true , null);

        core.downloadResponse(core.getResponseOfRequest(request).getEntity(),"./src/test/java/text.txt");
    }


    //request :post / url encoded : just string        response : text / headers
    public static void postMethodTest() {
        String url = "http://apapi.haditabatabaei.ir/tests/post/urlencoded";

        HashMap<String , String> stringPairs = new HashMap<>();
        stringPairs.put("Item1","value1");
        stringPairs.put("item2","value2");

        HashMap<String,String> headers = new HashMap<>();
       // headers.put("Content-Type","application/x-www-form-urlencoded");

        Request request = new Request("urlEncoded" ,null, stringPairs , null ,
                null , "Post" , url , true , headers);

        try ( CloseableHttpResponse response = core.getResponseOfRequest(request)) {
            HttpEntity entity =  response.getEntity();
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
                for (Header header : response.getAllHeaders()) {
                    System.out.println(header.getName()+" : "+header.getValue());
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //request :post / form data : both String and file        response : text / headers
    public static void postMethodTest1() {
        String url = "http://apapi.haditabatabaei.ir/tests/post/formdata";

        HashMap<String , String> filesPairs = new HashMap<>();
        filesPairs.put("pictureAsFile","./src/test/java/picture.png");
        filesPairs.put("textAsFile","./src/test/java/text.txt");

        HashMap<String , String> stringPairs = new HashMap<>();
        stringPairs.put("name","Star");
        stringPairs.put("id","36");

        Request request = new Request("multiPart" ,filesPairs, stringPairs , null ,
                null , "Post" , url , true , null);

        try ( CloseableHttpResponse response = core.getResponseOfRequest(request)) {
            HttpEntity entity =  response.getEntity();
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
                for (Header header : response.getAllHeaders()) {
                    System.out.println(header.getName()+" : "+header.getValue());
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //request :post / binary         response : text
    public static void postMethodTest2() {
        String url = "http://apapi.haditabatabaei.ir/tests/post/binary";
        Request request = new Request("binary" ,null, null , "./src/test/java/picture.png" ,
                null , "Post" , url , true , null);
        try ( CloseableHttpResponse response = core.getResponseOfRequest(request)) {
            HttpEntity entity =  response.getEntity();
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //request :delete        response : status
    public static void deleteMethodTest() {
        String url = "http://apapi.haditabatabaei.ir/tests/delete/60";
        Request request = new Request("empty" ,null, null , null ,
                null , "Delete" , url , true , null);
        try ( CloseableHttpResponse response = core.getResponseOfRequest(request)) {
                System.out.println(response.getStatusLine().toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //request :post / binary         response : text
    public static void putMethodTest() {
        String url = "http://apapi.haditabatabaei.ir/tests/put/23";

        HashMap<String , String> stringPairs = new HashMap<>();
        stringPairs.put("hello","world");
        stringPairs.put("what","happend");

        Request request = new Request("multiPart" ,null, stringPairs , null ,
                null , "Put" , url , true , null);
        try ( CloseableHttpResponse response = core.getResponseOfRequest(request)) {
            HttpEntity entity =  response.getEntity();
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //request :Patch / json         response : text
    public static void patchMethodTest() {
        String url = "http://apapi.haditabatabaei.ir/tests/patch/3";

        String json = new JSONObject("{\"status\":\"ok\",\"message\":\"Thank you 861 times!. :)\"}").toString();

        Request request = new Request("json" ,null, null , null ,
                json , "Patch" , url , true , null);
        try ( CloseableHttpResponse response = core.getResponseOfRequest(request)) {
            HttpEntity entity =  response.getEntity();
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void download(){
    }

    public static void createNewGroupTest() throws Exception {
    core.createNewGroup("hello");

    }


    public static void editUrlTest()  {
        try {

            String url = core.editUrl("http://yahoo.com");
            System.out.println(url);

//            String url2 = core.editUrl("htp://yahoo.com");
//            System.out.println(url);

            String url3 = core.editUrl("https:/yahoo.com");
            System.out.println(url);

            String url4 = core.editUrl("yahoo.com");
            System.out.println(url);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


