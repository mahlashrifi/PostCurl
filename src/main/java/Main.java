import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import ui.console.ConsoleView;
import ui.gui.FinalView;
import ui.gui.PairPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Please Enter the type of view .\n1.Consular\n2.Graphical");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if(input.equals("1")) {
            try {
                ConsoleView consoleView = new ConsoleView();
                consoleView.run();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        else if(input.equals("2")) {
            try {

                UIManager.setLookAndFeel( new FlatIntelliJLaf());
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }

            FinalView finalView = FinalView.getInstance();


        }

    }
}
