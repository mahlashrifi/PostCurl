package ui.gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Center panel class
 * this class is singleton
 */
public class RequestPanel extends JPanel{

    private static RequestPanel requestPanel = null ;
    private static ArrayList<PairPanel.OneLinePairPanel> multiPartFormData;
    private static ArrayList<PairPanel.OneLinePairPanel> urlEncodedData;
    private static JTextArea json;
    private static String binaryPath ;

    private String bodyTypeName ;

    private static JTextArea query ;

    private static ArrayList<PairPanel.OneLinePairPanel> headers ;

    private JComboBox method ;
    private JTextField url ;
    private JButton send ;
    private JButton save ;

    private JComboBox bodyType ;

    private PairPanel.MultiLinePairPanel multiPartPanel ;
    private PairPanel.MultiLinePairPanel urlEncodedPanel ;
    private BinaryPanel binaryPanel ;
    private JsonPanel jsonPanel ;
    private JPanel emptyPanel ;
    private QueryPanel queryPanel ;
    private JPanel authPanel ;
    private PairPanel.MultiLinePairPanel headersPanel ;
    private SendPanel sendPanel ;
    private JPanel centerPanel ;

    private JTextField binaryPanelTextField ;
    private File binaryFile ;
    private RequestPanel() {
        initializePanels();
        init();
    }

    /**
     * A way to reset the panel to its original state and delete all available data
     */
    public void resetPanel(){

        resetBodies();
        headersPanel.resetMultiLinePairPanel();
        queryPanel.resetQueryPanel();
        url.setText("");
        bodyType.setSelectedIndex(4);
        method.setSelectedIndex(0);

    }

    public static RequestPanel getInstance(){
        if(requestPanel == null)
            requestPanel = new RequestPanel();
        return requestPanel ;
    }

    /**
     * Method to initial panel which are exist in center panel
     */
    public void initializePanels(){
        multiPartFormData = new ArrayList<>();
        multiPartPanel = new PairPanel.MultiLinePairPanel(multiPartFormData,true,"Form Data");

        urlEncodedData = new ArrayList<>();
        urlEncodedPanel = new PairPanel.MultiLinePairPanel(urlEncodedData,false,"Url Encoded");

        binaryPanel = new BinaryPanel();

        jsonPanel = new JsonPanel();

        emptyPanel = new JPanel();

        queryPanel = new QueryPanel();

        authPanel = new JPanel();

        headers = new ArrayList<>();
        headersPanel = new PairPanel.MultiLinePairPanel(headers,false,"Header");

        sendPanel = new SendPanel();
    }

    /**
     * Method to initial whole panel
     */
    private void init(){

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1,4,5,6));
        jPanel.setBorder(new EmptyBorder(10,8,10,8));

        bodyType = new JComboBox<String>(new String[]{"Form Data" , "Url Encoded" , "Binary" ,"JSON","Empty"});
        bodyType.addActionListener(new MessageBodyTypeHandler());

        bodyTypeName = "Form Data";
        JButton queryButton = new JButton("Query");
        queryButton.addActionListener(new menuButtonHandler(7));
        JButton headerButton = new JButton("Header");
        headerButton.addActionListener(new menuButtonHandler(8));
        JButton authButton = new JButton("Authorization");
        authButton.addActionListener(new menuButtonHandler(6));
        jPanel.add(bodyType);
        jPanel.add(queryButton);
        jPanel.add(headerButton);
        jPanel.add(authButton);
        JPanel upPanelOfRequestPanel = new JPanel();
        upPanelOfRequestPanel.setLayout(new BorderLayout());
        upPanelOfRequestPanel.add(sendPanel,BorderLayout.NORTH);
        upPanelOfRequestPanel.add(jPanel,BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(upPanelOfRequestPanel,BorderLayout.NORTH);
        makeCenterPanel();
        makeACentralPanelVisible(1);
        add(centerPanel,BorderLayout.CENTER);
    }

    /**
     * Make center panel and panel which are exist in center panel
     */
    private void makeCenterPanel() {
        centerPanel = new JPanel(new CardLayout());
        centerPanel.setBorder(new LineBorder(Color.LIGHT_GRAY,8,false));
        centerPanel.add(multiPartPanel,"Form Data");
        centerPanel.add(urlEncodedPanel,"Url Encoded");
        centerPanel.add(binaryPanel,"Binary");
        centerPanel.add(jsonPanel,"JSON");
        centerPanel.add(emptyPanel,"Empty");
        centerPanel.add(authPanel,"Authorization");
        centerPanel.add(queryPanel,"Query");
        centerPanel.add(headersPanel,"Header");

    }

    private void makeACentralPanelVisible(int indexOfPanelWhichShouldBeVisible){
        CardLayout layout = (CardLayout)centerPanel.getLayout();
        switch (indexOfPanelWhichShouldBeVisible){
            case 1:
                layout.show(centerPanel, "Form Data");
                break;
            case 2:
                layout.show(centerPanel, "Url Encoded");
                break;
            case 3:
                layout.show(centerPanel, "Binary");
                break;
            case 4:
                layout.show(centerPanel, "JSON");
                break;
            case 5:
                layout.show(centerPanel, "Empty");
                break;
            case 6:
                layout.show(centerPanel, "Authorization");
                break;
            case 7:
                layout.show(centerPanel, "Query");
                break;
            case 8:
                layout.show(centerPanel, "Header");
                break;
        }
    }

    /**
     * A method to reset available data of bodies to firs state
     */
    private void resetBodies(){
        multiPartPanel.resetMultiLinePairPanel();
        urlEncodedPanel.resetMultiLinePairPanel();
        binaryPanel.resetBinaryPanel();
        jsonPanel.resetJsonPanel();

    }

    /**
     * A handler af a combo box which use to set body type
     */
    private class MessageBodyTypeHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            requestPanel.getUrlEncodedData().forEach(c-> System.out.println("1"+c.getKey()+"*****"+c.getTextValue()+"*****"+c.getFilePath()));
            requestPanel.getUrlEncodedPanel().getLines().forEach(c-> System.out.println("2"+c.getKey()+"*****"+c.getTextValue()+"*****"+c.getFilePath()));
            JComboBox comboBox = (JComboBox) e.getSource();
            String type = (String) comboBox.getSelectedItem();
            switch (type) {
                case "Form Data":
                    if(resetBodiesProcess("Form Data"))
                        makeACentralPanelVisible(1);
                    break;
                case "Url Encoded":
                    if(resetBodiesProcess("Url Encoded"))
                        makeACentralPanelVisible(2);
                    break;
                case "Binary":
                    if(resetBodiesProcess("Binary"))
                        makeACentralPanelVisible(3);
                    break;
                case "JSON":
                    if(resetBodiesProcess("JSON"))
                        makeACentralPanelVisible(4);
                    break;
                case "Empty":
                    if(resetBodiesProcess("Empty"))
                        makeACentralPanelVisible(5);
                    break;
            }
            requestPanel.getUrlEncodedData().forEach(c-> System.err.println("1"+c.getKey()+"*****"+c.getTextValue()+"*****"+c.getFilePath()));
            requestPanel.getUrlEncodedPanel().getLines().forEach(c-> System.err.println("2"+c.getKey()+"*****"+c.getTextValue()+"*****"+c.getFilePath()));
        }


        private boolean resetBodiesProcess(String selectedItem) {
            if(!selectedItem.equals(bodyTypeName) && !LeftPanel.isItsMe()) {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int confirm = JOptionPane.showConfirmDialog(centerPanel , "Current body will be lost . Are you sure you want to continue?" , "Switch Body Type?" , dialogButton);
                if (confirm == JOptionPane.YES_OPTION) {
                    resetBodies();
                    bodyTypeName = selectedItem ;
                    return true;
                }
                bodyType.setSelectedItem(bodyTypeName);
                return false ;
            }
            return true ;
        }
    }


    private class menuButtonHandler implements ActionListener{
        private int index ;
        public menuButtonHandler(int buttonIndex) {
            index = buttonIndex ;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            makeACentralPanelVisible(index);
        }
    }

    /**
     * Query panel class
     */
    public class QueryPanel extends JPanel{

        public QueryPanel() {
            makeQueryPanel();
        }

        private void makeQueryPanel() {
            setLayout(new BorderLayout());
            query = new JTextArea(24 , 60);
            query.setLineWrap(false);
            JScrollPane jp = new JScrollPane(query,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(jp , BorderLayout.CENTER);
        }
        public void resetQueryPanel(){
            query.setText("");
        }


    }

    /**
     * Json panel class
     */
    public class JsonPanel extends JPanel{

        public JsonPanel() {
            makeJsonPanel();

        }

        private void makeJsonPanel() {
            setLayout(new BorderLayout());
            json = new JTextArea(24 , 60);
            json.setLineWrap(true);
            JScrollPane jp = new JScrollPane(json,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            add(jp , BorderLayout.CENTER);
        }

        public void resetJsonPanel(){
            json.setText("");
        }

    }

    /**
     * Binary panel class
     */
    public class BinaryPanel extends JPanel {
        private JPanel jPanel ;
        private JButton selectFileButton ;
        private JButton delete ;

        public BinaryPanel() {
            jPanel = new JPanel();
            selectFileButton = new JButton("Select File");
            makeBinaryPanel();
            delete = new JButton(" Reset ");
            binaryPanelTextField = new JTextField();
        }

        private void makeBinaryPanel() {
            setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY , 8 , false) , new EmptyBorder(10 , 10 , 10 , 10)));
            add(selectFileButton);
            selectFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    binaryPanelTextField.setEditable(false);
                    jPanel.setLayout(new FlowLayout());
                    jPanel.setBackground(Color.LIGHT_GRAY);
                    jPanel.setBorder(new LineBorder(Color.lightGray , 1 , true));

                    delete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            int dialogButton = JOptionPane.YES_NO_OPTION;
                            int confirm = JOptionPane.showConfirmDialog(BinaryPanel.this , "Are you sure to reset file?" , "Confirm" , dialogButton);
                            if (confirm == JOptionPane.YES_OPTION) {
                                resetBinaryPanel();
                            }
                        }
                    });
                    if (e.getSource() == selectFileButton) {
                        JFileChooser fc = new JFileChooser();
                        int i = fc.showOpenDialog(BinaryPanel.this);
                        if (i == JFileChooser.APPROVE_OPTION) {
                            changeLookOfBinaryPanel();
                            binaryFile = fc.getSelectedFile();
                            setFileOfBinaryPanel(binaryFile);

                        }
                    }
                }
            });
        }

        public void changeLookOfBinaryPanel(){
            jPanel.add(binaryPanelTextField);
            jPanel.add(delete);
            add(jPanel);
            selectFileButton.setVisible(false);
            jPanel.setVisible(true);
        }

        public void resetBinaryPanel(){
            binaryPath = null ;
            jPanel.setVisible(false);
            selectFileButton.setVisible(true);
        }
        public void setFileOfBinaryPanel(File f){
            binaryPath =  f.getPath();
            binaryPanelTextField.setText(" "+f.getName()+ " (" + findFileSize(f.length()) + ") ");
        }

        private String findFileSize(long size) {
            String stringToReturn;
            if (size < 1024 * 1024) {
                stringToReturn = "" + Math.round(((float) size / 1024) * 10) / 10.0 + "KB";
            } else
                stringToReturn = "" + Math.round(((float) size / 1024 + 1024) * 10) / 10.0 + "MB";
            return stringToReturn;
        }
    }

    /**
     * Send panel which contain url  and method of request and also send and save button
     */
    public class SendPanel extends JPanel{
        private boolean isTextFilledEmpty ;

        public SendPanel() {
            makeSendPanel();
        }

        private void makeSendPanel(){
            setLayout(new BorderLayout());
            setBorder(new LineBorder(Color.LIGHT_GRAY,6,true));
            setBackground(Color.LIGHT_GRAY);
            add(makeMethodComboBox(),BorderLayout.WEST);
            add(makeAPartWhereTheUrlIsEntered(),BorderLayout.CENTER);
            add(makeSendAndSaveButton(),BorderLayout.EAST);
        }

        private JComboBox makeMethodComboBox(){
            method = new JComboBox<String>(new String[]{"GET" , "DELETE" , "POST" , "PUT" , "PATCH"});
            method.setBorder(new LineBorder(Color.LIGHT_GRAY,5,false));
            return method;
        }
        private JTextField makeAPartWhereTheUrlIsEntered(){
            url = new JTextField();
            url.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY,6,false),new EmptyBorder(5,6,5,3)));

            url.setText(" Enter Request URL");
            url.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if(url.getText().equals(" Enter Request URL")) {
                        url.setSelectedTextColor(Color.BLACK);
                        url.setText("");
                    }
                    else
                        isTextFilledEmpty = false;
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if(url.getText().equals("")) {
                        url.setSelectedTextColor(Color.LIGHT_GRAY);
                        url.setText(" Enter Request URL");
                        isTextFilledEmpty  = true ;
                    }
                }
            });
            return (JTextField) url ;
        }
        private JPanel makeSendAndSaveButton(){
            JPanel jPanel = new JPanel();
            jPanel.setBackground(Color.LIGHT_GRAY);
            jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            send = new JButton("Send");
            save = new JButton("Save");
            jPanel.add(send);
            jPanel.add(save);
            return jPanel;
        }

        public boolean isTextFilledEmpty() {
            return isTextFilledEmpty;
        }
    }

    /**
     * A handler of delete button that is exist in OneLinePairPanel class
     * @param parentName
     * @param index
     */
    public void deleteButtonProcessForAOneLinePairPanel( String parentName , int index ) {
        switch (parentName){
            case "Form Data":
                (multiPartPanel).deleteALine(index);
                break;
            case "Url Encoded":
                (urlEncodedPanel).deleteALine(index);
                break;
            case "Header":
                (headersPanel).deleteALine(index);
                break;
        }

    }

    public static RequestPanel getRequestPanel() {
        return requestPanel;
    }

    public ArrayList<PairPanel.OneLinePairPanel> getMultiPartFormData() {
        return multiPartFormData;
    }

    public ArrayList<PairPanel.OneLinePairPanel> getUrlEncodedData() {
        return urlEncodedData;
    }

    public JTextArea getJson() {
        return json;
    }

    public String getBinaryPath() {
        return binaryPath;
    }

    public String getBodyTypeName() {
        return bodyTypeName;
    }

    public JTextArea getQuery() {
        return query;
    }

    public ArrayList<PairPanel.OneLinePairPanel> getHeaders() {
        return headers;
    }

    public JComboBox getMethod() {
        return method;
    }

    public JTextField getUrl() {
        return url;
    }

    public JButton getSend() {
        return send;
    }

    public JButton getSave() {
        return save;
    }

    public JComboBox getBodyType() {
        return bodyType;
    }

    public PairPanel.MultiLinePairPanel getMultiPartPanel() {
        return multiPartPanel;
    }

    public PairPanel.MultiLinePairPanel getUrlEncodedPanel() {
        return urlEncodedPanel;
    }

    public BinaryPanel getBinaryPanel() {
        return binaryPanel;
    }

    public JsonPanel getJsonPanel() {
        return jsonPanel;
    }

    public JPanel getEmptyPanel() {
        return emptyPanel;
    }

    public QueryPanel getQueryPanel() {
        return queryPanel;
    }

    public JPanel getAuthPanel() {
        return authPanel;
    }

    public PairPanel.MultiLinePairPanel getHeadersPanel() {
        return headersPanel;
    }

    public SendPanel getSendPanel() {
        return sendPanel;
    }

    public JPanel getCenterPanel() {
        return centerPanel;
    }

    public JTextField getBinaryPanelTextField() {
        return binaryPanelTextField;
    }

    public void setBodyTypeName(String bodyTypeName) {
        this.bodyTypeName = bodyTypeName;
    }

    public File getBinaryFile() {
        return binaryFile;
    }
}