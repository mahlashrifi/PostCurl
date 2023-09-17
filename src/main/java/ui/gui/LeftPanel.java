package ui.gui;

import logic.Core;
import logic.Request;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Left panel class that show information of saved groups and requests
 */
public class LeftPanel extends JPanel {
        private static LeftPanel leftPanel = null ;
        private ArrayList<String> openRequests ;
        private static boolean itsMe ;
        private JButton newGroup ;
        private Core core ;

            private LeftPanel() throws Exception {
            openRequests = new ArrayList<>();
            itsMe = false ;
            core = Core.getInstance();
            update();
        }

        public static LeftPanel getInstance() throws Exception {
                if(leftPanel == null)
                    leftPanel = new LeftPanel();
                return leftPanel ;
        }

    /**
     * A method to fill Left panels value
     */
    public void update() throws Exception {
            newGroup = new JButton("       New Group      ");
            newGroup.addActionListener(new newGroupButtonHandler());
            setLayout(new BorderLayout());
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Groups");
            for (File group : core.getGroupsOfRequests()) {
                DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group.getName());
                for (String requestName : core.getRequestsNameOfAGroup(group.getName())) {
                    DefaultMutableTreeNode requestNode = new DefaultMutableTreeNode(requestName);
                    groupNode.add(requestNode);
                }
                root.add(groupNode);
            }

            JTree jt = new JTree(root);
            JScrollPane scrollPane = new JScrollPane(jt,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                    ,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jt.setBorder(new EmptyBorder(5,5,5,10));
            jt.addTreeSelectionListener(new myListener());
            add(scrollPane,BorderLayout.CENTER);
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("     "));
            panel.add(newGroup);
            panel.add(new JLabel("     "));
            add(panel, BorderLayout.NORTH);
            setBorder(new EmptyBorder(5,15,15,10));
            setVisible(true);
        }

        private class myListener implements TreeSelectionListener {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Pattern pattern = Pattern.compile("(.)(Groups),\\s(.*),\\s(.*)(.)");
                Matcher matcher = pattern.matcher(e.getPath().toString());
                if (matcher.matches() ) {
                    try {
                        itsMe = true ;
                        Request request = core.getASaveRequest(matcher.group(3) , matcher.group(4));
                        RequestPanelMaker panelMaker = new RequestPanelMaker(request);
                        openRequests.add(matcher.group(4));
                        itsMe = false ;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
            private boolean isRequestOpenedBefore(String requestName){
                for (String request : openRequests) {
                    if(requestName.equals(request))
                        return true ;
                }
                return false ;
            }
        }

    /**
     * A handler for newGroup button that is used to make new group
     */
    private class newGroupButtonHandler implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Group name");
                String name = JOptionPane.showInputDialog(frame, "Enter group name.");
                if(name!=null){
                    if(core.doesGroupExist(name))
                        JOptionPane.showMessageDialog(frame,"Entered name is already exist ! ");
                    else
                    {
                        try {
                            core.createNewGroup(name);
                        } catch (Exception ex) {
                           handleException(ex);
                        }
                    }
                }
            }
        }
        private static void handleException(Exception e){
            JFrame frame2 = new JFrame("Error");
            JOptionPane.showMessageDialog(frame2,e.getMessage()+" !");

        }

    /**
     * It is very :) method .
     * actually it notify the request panel which a saved request wants to be shown and
     * There is no need to ask when changing the bodyType if it s me is true
     * @return
     */
    public static boolean isItsMe() {
        return itsMe;
    }
}
