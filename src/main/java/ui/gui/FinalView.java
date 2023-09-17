package ui.gui;

import logic.Core;
import logic.Request;

import javax.naming.NamingEnumeration;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * A class to make final frame of programme
 */
public class FinalView {
    private static FinalView finalView = null;

    private static JFrame jFrame ;
    private static LeftPanel savePanel ;
    private RequestPanel requestPanel ;
    private JPanel menuBar ;

    private Core core ;

    private JPanel responsePanel ;
    private WaitingPanel waitingPanel ;
    private JPanel nothing ;
    private ResponsePanel response;


    private FinalView() throws Exception {
        jFrame = new JFrame("Star");
        jFrame.setSize(1300,650);
        jFrame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - jFrame.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - jFrame.getSize().height) / 2);
        jFrame.setLayout(new BorderLayout());

        MenuBar menuBar = MenuBar.getInstance();
        jFrame.add(menuBar.getJMenuBar() , BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,2));

        requestPanel = RequestPanel.getInstance();

        responsePanel = new JPanel();
        responsePanel.setLayout(new CardLayout());
        response = ResponsePanel.getInstance();
        responsePanel.add(response,"Response");
        waitingPanel = WaitingPanel.getInstance();
        responsePanel.add(waitingPanel,"WaitingPanel");
        nothing = new JPanel();
        nothing.setLayout(new BorderLayout());
        responsePanel.add(nothing,"Nothing");
        ((CardLayout)responsePanel.getLayout()).show(responsePanel,"Nothing");

        mainPanel.add(requestPanel);
        mainPanel.add(responsePanel);

        savePanel = LeftPanel.getInstance();

        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, savePanel, requestPanel);
        JSplitPane total = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftSplitPane,responsePanel);

        jFrame.add(total ,BorderLayout.CENTER);
        jFrame.setVisible(true);
        core = Core.getInstance();

        requestPanel.getSend().addActionListener(new sendButtonHandler());
        requestPanel.getSave().addActionListener(new saveButtonHandler());

    }

    public static FinalView getInstance() throws Exception {
        if(finalView == null)
            finalView = new FinalView();
        return finalView;
    }

    /**
     * Right panel include 2 kinds of panel .
     * one them is instance of ResponsePanel and the other is instance of WaitingPanel
     * Waiting panel is visible when a request is executing
     */
    private static class WaitingPanel extends JPanel{

        private static WaitingPanel waitingPanel = null ;
        private JLabel spentTime ;
        private JButton cancel ;

        private WaitingPanel() {
            setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2,1));
            panel.setBorder(new EmptyBorder(50,50,0,50));

            spentTime = new JLabel("");
            spentTime.setFont(new Font("Arial",Font.BOLD,18));
            panel.add(spentTime,BorderLayout.NORTH);

            cancel = new JButton("\n   Cancel   \n");
            panel.add(cancel,BorderLayout.CENTER);

            add(panel,BorderLayout.NORTH);
        }

        public static WaitingPanel getInstance(){
            if(waitingPanel == null)
                waitingPanel = new WaitingPanel();
            return waitingPanel ;
        }

        public JLabel getSpentTime() {
            return spentTime;
        }

        public JButton getCancelButton() {
            return cancel;
        }
    }

    /**
     * A handler for send button of request panel
     */
    public class sendButtonHandler implements ActionListener{

       @Override
       public void actionPerformed(ActionEvent e) {
           try {

               Request request =new GuiRequestMaker(requestPanel).getRequest();
               GuiResponseMaker g = new GuiResponseMaker(request,responsePanel,response,nothing,waitingPanel.getCancelButton(),
                       waitingPanel.getSpentTime());
               Date startTime = new Date();
               Thread time1 = new Thread() {
                   @Override
                   public void run () {
                       while (!g.isDone()) {
                           Double timeInSecond;
                           Date secondaryDate = new Date();
                           timeInSecond = (secondaryDate.getTime() - startTime.getTime()) / 1000.0;
                           double t = Math.round(timeInSecond*10.0)/10.0 ;
                           waitingPanel.spentTime.setText("                          "+t + " s");
                       }
                   }
               };
               time1.start();
               g.execute();
               ((CardLayout)responsePanel.getLayout()).show(responsePanel,"Response");
           } catch (Exception ex) {
               JLabel label = new JLabel(String.valueOf(ex.getMessage()));
               label.setForeground(Color.RED);
               label.setFont(new Font("Verdana", Font.PLAIN, 12));
               nothing.add(label,BorderLayout.NORTH);
               ((CardLayout) responsePanel.getLayout()).show(responsePanel,"Nothing");

           }
       }
    }

    /**
     * A handler for save button of request panel class
     */
    public class saveButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Request current ;
           try {
                current = new GuiRequestMaker(requestPanel).getRequest();

                JFrame jFrame = new JFrame("Choose one of the groups !");
                jFrame.setSize(350,400);
                jFrame.setVisible(true);
                jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                jFrame.setLayout(new BorderLayout());

                 ArrayList<File> groups = core.getGroupsOfRequests();
                 String[] groupsNames = new String[groups.size()];
                 for (int i = 0; i < groups.size() ;i++) {
                     groupsNames[i] = groups.get(i).getName();
                 }
                 JComboBox box = new JComboBox(groupsNames);
                 box.addActionListener(d -> {
                     try {
                         core.saveRequest(current , box.getSelectedItem().toString() , null);
                         jFrame.setVisible(false);
                     } catch (IOException ex) {
                         ex.printStackTrace();
                     }
                 });
               jFrame.add(box,BorderLayout.NORTH);

            } catch (Exception ex) {
                JFrame frame2 = new JFrame("Error");
                JOptionPane.showMessageDialog(frame2,ex.getMessage()+" !");

            }

        }
     }
    public static JFrame getJFrame() {
        return jFrame;
    }

    public static LeftPanel getSavePanel() {
        return savePanel;
    }

    public RequestPanel getRequestPanel() {
        return requestPanel;
    }

    public JPanel getResponsePanel() {
        return responsePanel;
    }

    public JPanel getMenu() {
        return menuBar;
    }
}
