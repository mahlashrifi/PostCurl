import logic.Core;
import logic.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import ui.console.ConsoleView;

import java.io.IOException;

public class ConsoleViewTest {
    static ConsoleView consoleView ;


    public static void main(String[] args) throws Exception {

        try {
            consoleView = new ConsoleView();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        printEntityTest();

        consoleView.run();


    }

    private static void printEntityTest() {
        try {
            consoleView = new ConsoleView();

            String url = "https://en-maktoob.yahoo.com/?p=us&guccounter=1";
            Request request = new Request("empty" , null , null , null ,
                    null , "Get" , url , true , null);

            Core core = Core.getInstance();
            CloseableHttpResponse response = core.getResponseOfRequest(request);

//            consoleView.printEntity(response.getEntity());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
