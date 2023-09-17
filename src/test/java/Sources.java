

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sources {

    public static HashMap<Integer,String> getStatuses(){

        HashMap<Integer,String> statuses = new HashMap<>();
        Scanner scanner ;
        try {
             scanner = new Scanner(new File("statusCodes.txt"));
             while (scanner.hasNextLine()){
             statuses.put(Integer.parseInt(scanner.next("\\d\\d\\d")),scanner.next("(\\w|\\s)+"));
            }
        } catch (FileNotFoundException e) {
            statuses.put(200,"OK");
        }
        return statuses ;
    }


}
