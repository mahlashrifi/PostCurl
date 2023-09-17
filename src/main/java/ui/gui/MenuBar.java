package ui.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static java.awt.Frame.*;

/**
 * A class that makes MenuBar of Programme
 */
public class MenuBar {

    private static MenuBar menuBar = null ;
    private JRadioButton active ;
    private JMenuBar jMenuBar;
    private JMenu application;
    private JMenu view;
    private JMenu help;
    private boolean followRedirect ;
    private boolean hideInSystemTray ;
    private int timesOfVieMenuClicking;
    private MenuBar() {
        jMenuBar = new JMenuBar();
        hideInSystemTray = false ;
        timesOfVieMenuClicking = 0 ;
        init();
        jMenuBar.add(application);
        jMenuBar.add(view);
        jMenuBar.add(help);
    }

    public static MenuBar getInstance(){
        if(menuBar == null)
            menuBar = new MenuBar();
        return menuBar ;
    }
    private void init() {

        makeApplicationMenu();
        makeViewMenu();
        makeHelpMenu();
    }

    private void makeApplicationMenu() {
        application = new JMenu("Application");
        application.setMnemonic(KeyEvent.VK_A);
        JMenuItem options = new JMenuItem("Options ");
        KeyStroke ctrlO = KeyStroke.getKeyStroke("control O");
        options.setAccelerator(ctrlO);

        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeOptionFrame().setVisible(true);
            }
        });
        JMenuItem exit = new JMenuItem("Exit");
        KeyStroke ctrlE = KeyStroke.getKeyStroke("control E");
        exit.setAccelerator(ctrlE);
        exit.addActionListener(new exitHandler());
        application.add(options);
        application.add(exit);
        active = new JRadioButton("Active",true);
    }

    private JFrame makeOptionFrame() {
        JFrame optionFrame = new JFrame();
        optionFrame.setSize(420,140);
        optionFrame.setResizable(false);
        optionFrame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - optionFrame.getSize().width) / 2 , (Toolkit.getDefaultToolkit().getScreenSize().height - optionFrame.getSize().height) / 2);
        optionFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel followRedirect = new JLabel(" Follow redirect automatically :         ");
        ButtonGroup firstGroup = new ButtonGroup();
        JRadioButton inActive = new JRadioButton("Inactive",false);
        firstGroup.add(active);
        firstGroup.add(inActive);

        JLabel exitType = new JLabel(" Exit Type :       ");
        ButtonGroup secondGroup = new ButtonGroup();
        JRadioButton systemTray = new JRadioButton("Hide in System Tray",false);
        systemTray.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideInSystemTray = true ;
            }
        });
        JRadioButton close = new JRadioButton("Complete exit",true);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideInSystemTray = false ;
            }
        });
        secondGroup.add(systemTray);
        secondGroup.add(close);
        JLabel theme = new JLabel(" Theme :           ");
        ButtonGroup thirdGroup = new ButtonGroup();
        JRadioButton lightTheme = new JRadioButton("Light",true);
        lightTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    UIManager.setLookAndFeel( new FlatIntelliJLaf());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize LaF" );
                }
                SwingUtilities.updateComponentTreeUI(FinalView.getJFrame());
            }
        });
        JRadioButton darkTheme = new JRadioButton("Dark",true);
        darkTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    UIManager.setLookAndFeel( new FlatDarculaLaf());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize LaF" );
                }
                SwingUtilities.updateComponentTreeUI(FinalView.getJFrame());
            }
        });
        JLabel spaces = new JLabel("                                                                         ");
        JLabel newLine = new JLabel(spaces.getText()+""+spaces.getText());
        thirdGroup.add(lightTheme);
        thirdGroup.add(darkTheme);
        optionFrame.add(followRedirect);
        optionFrame.add(active);
        optionFrame.add(inActive);
        optionFrame.add(exitType);
        optionFrame.add(systemTray);
        optionFrame.add(close);
        optionFrame.add(theme);
        optionFrame.add(lightTheme);
        optionFrame.add(darkTheme);
        optionFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        return optionFrame;
    }

    private class exitHandler implements ActionListener {
        TrayIcon trayIcon;
        SystemTray tray;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (SystemTray.isSupported()) {
                tray = SystemTray.getSystemTray();
                BufferedImage img = null;
                try {
                    img =  ImageIO.read(new File("circle.png"));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ActionListener exitListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                };
                ActionListener openListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FinalView.getJFrame().setVisible(true);
                        FinalView.getJFrame().setExtendedState(NORMAL);
                    }
                };
                PopupMenu popup = new PopupMenu();
                MenuItem defaultItem = new MenuItem("Close");
                defaultItem.addActionListener(exitListener);
                popup.add(defaultItem);
                defaultItem = new MenuItem("Open");
                defaultItem.addActionListener(openListener);
                popup.add(defaultItem);
                trayIcon = new TrayIcon(img , "Star" , popup);
                tray.remove(trayIcon);
                trayIcon.setImageAutoSize(true);
                tray.remove(trayIcon);
                try {
                    tray.add(trayIcon);
                    FinalView.getJFrame().setVisible(false);
                } catch (AWTException ex) {
                }
            }
            else {
                System.exit(0);
            }
        }
    }

    /**
     * A method to make view menu
     */
    private void makeViewMenu(){
        view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        JMenuItem fullScreen = new JMenuItem("Toggle Full Screen ");
        KeyStroke ctrlF = KeyStroke.getKeyStroke("control F");
        fullScreen.setAccelerator(ctrlF);
        fullScreen.addActionListener(new ActionListener() {
            int width ;
            int height ;
            int x ;
            int y ;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(timesOfVieMenuClicking%2 == 0){
                    width = FinalView.getJFrame().getSize().width;
                    height = FinalView.getJFrame().getSize().height;
                    x = FinalView.getJFrame().getX();
                    y = FinalView.getJFrame().getY();
                    FinalView.getJFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
                    timesOfVieMenuClicking++ ;

                }
                else {
                    FinalView.getJFrame().setLocation(x,y);
                    FinalView.getJFrame().setSize(width,height);
                    timesOfVieMenuClicking++ ;
                }
            }
        });
        JMenuItem toggleSidebar = new JMenuItem("Toggle Sidebar ");
        KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
        toggleSidebar.setAccelerator(ctrlS);
        toggleSidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(FinalView.getSavePanel().isVisible())
                    FinalView.getSavePanel().setVisible(false);
                else
                    FinalView.getSavePanel().setVisible(true);
            }
        });
        view.add(fullScreen);
        view.add(toggleSidebar);

    }

    /**
     * A method to make help menu
     */
    private void makeHelpMenu(){
        help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem helpItem = new JMenuItem("Help");
        KeyStroke ctrlH = KeyStroke.getKeyStroke("control H");
        helpItem.setAccelerator(ctrlH);
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame helpFrame = new JFrame();
                helpFrame.setSize(420,500);
                helpFrame.setResizable(false);
                helpFrame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - helpFrame.getSize().width) / 2 , (Toolkit.getDefaultToolkit().getScreenSize().height - helpFrame.getSize().height) / 2);
                helpFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
                JTextArea jTextArea = new JTextArea();
                JScrollPane jp = new JScrollPane(jTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jp.setSize(new Dimension(400,480));
                Scanner scanner = null ;
                StringBuilder stringBuilder = new StringBuilder();
                try {

                    scanner = new Scanner(new File("src\\main\\resources\\help.txt"));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                while (scanner.hasNextLine()){
                      stringBuilder.append(scanner.nextLine());
                    }
                jTextArea.setLineWrap(true);
                jTextArea.setText(stringBuilder.toString());
                helpFrame.add(jp);
                helpFrame.setResizable(false);
                helpFrame.setVisible(true);
                helpFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            }
        });
        JMenuItem about = new JMenuItem("About ");
        KeyStroke ctrlA = KeyStroke.getKeyStroke("control A");
        about.setAccelerator(ctrlA);
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame aboutFrame = new JFrame();
                aboutFrame.setSize(420,300);
                aboutFrame.setResizable(false);
                aboutFrame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - aboutFrame.getSize().width) / 2 , (Toolkit.getDefaultToolkit().getScreenSize().height - aboutFrame.getSize().height) / 2);
                aboutFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
                JLabel label = new JLabel("about");
                label.setText(" EMAIL ADRESS : mahla7997@gmail.com");
                JLabel label1 = new JLabel("  STUDENT NUMBER : 9831035");
                aboutFrame.add(label);
                aboutFrame.add(label1);
                aboutFrame.setResizable(false);
                aboutFrame.setVisible(true);
                aboutFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

            }
        });
        help.add(helpItem);
        help.add(about);
    }
    public JMenuBar getJMenuBar() {
        return jMenuBar;
    }

    /**
     * A method to know that follow redirect is active or not
     * @return follow redirect value
     */
    public boolean isFollowRedirect() {
        if(active.isSelected())
            return true ;
        return false ;
    }

    /**
     * A method to set follow redirect argument

     */
    public void setFollowRedirect(boolean followRedirect) {
        if(followRedirect)
            active.setSelected(true);
        else active.setSelected(false);
    }
}
