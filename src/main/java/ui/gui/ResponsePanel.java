package ui.gui;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import logic.ResponseInfo;
import org.apache.http.Header;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Right panel class that includes all information of response
 */
public class ResponsePanel extends JPanel {
    private static ResponsePanel responsePanel = null ;
    private HashMap<String,String> headers ;

    private JLabel status ;
    private JLabel contentLength ;
    private JLabel timeToGetResponse;

    private JButton copyToClipBoard ;

    private RawPanel rawPanel;
    private PreviewPanel previewPanel;
    private ResponseHeadersPanel headersPanel;
    private JPanel informationPanel;
    private JPanel menuPanel;
    private JPanel centerPanel;


    private ResponsePanel(){
        super();
        init();
    }
    public static ResponsePanel getInstance() {
        if(responsePanel == null)
            responsePanel = new ResponsePanel();
        return responsePanel ;
    }

    /**
     * A method to make menu panel  which includes header button / preview and raw and alo copy to clipboard button
     */
    private void makeMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBorder(new EmptyBorder(5 , 8 , 4 , 8));

        String[] titles = new String[]{"Raw"  , "Preview"};
        JComboBox<String> messageTypes = new JComboBox<>(titles);
        messageTypes.addActionListener(new responseBodyTypeHandler());

        JButton headerButton = new JButton("Header");
        headerButton.addActionListener(e->makeACentralPanelVisible(3));

        menuPanel.add(messageTypes);
        menuPanel.add(headerButton);

        copyToClipBoard = new JButton("Copy to Clipboard");
        menuPanel.add(copyToClipBoard);
    }

    private void makeInformationPanel() {
        informationPanel = new JPanel();

        status = new JLabel("       ");
        timeToGetResponse = new JLabel("      ");
        timeToGetResponse.setBorder( new EmptyBorder(1 , 5 , 1 , 5));
        contentLength = new JLabel("     ");
        contentLength.setBorder( new EmptyBorder(1 , 5 , 1 , 5));

        informationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        informationPanel.add(status);
        informationPanel.add(timeToGetResponse);
        informationPanel.add(contentLength);
        informationPanel.setBorder(new LineBorder(Color.LIGHT_GRAY , 11 , false));
//        informationPanel.setBackground(Color.LIGHT_GRAY);
    }

    private void init(){
        JPanel upPanelOfResponsePanel = new JPanel();
        upPanelOfResponsePanel.setLayout(new BorderLayout());
        makeMenuPanel();
        makeInformationPanel();
        upPanelOfResponsePanel.add(informationPanel , BorderLayout.NORTH);
        upPanelOfResponsePanel.add(menuPanel , BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(upPanelOfResponsePanel , BorderLayout.NORTH);

        rawPanel = new RawPanel();
        previewPanel = new PreviewPanel();

        headersPanel = new ResponseHeadersPanel();

        makeCenterPanel();
        makeACentralPanelVisible(1);
        add(centerPanel , BorderLayout.CENTER);
    }

    private void makeCenterPanel() {
        centerPanel = new JPanel(new CardLayout());
        centerPanel.add(rawPanel , "Raw");
        centerPanel.add(previewPanel , "Preview");
        centerPanel.add(headersPanel , "Header");
        centerPanel.setBorder(new LineBorder(Color.LIGHT_GRAY , 8 , false));

    }

    private void makeACentralPanelVisible(int indexOfPanelWhichShouldBeVisible) {
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        switch (indexOfPanelWhichShouldBeVisible) {
            case 1:
                layout.show(centerPanel , "Raw");
                break;
            case 2:
                layout.show(centerPanel , "Preview");
                break;
            case 3:
                layout.show(centerPanel , "Header");
                break;
        }
    }

    private class responseBodyTypeHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            String type = (String) comboBox.getSelectedItem();
            if(type == null)
                return;
            switch (type) {
                case "Raw":
                    makeACentralPanelVisible(1);
                    break;
                case "Preview":
                    makeACentralPanelVisible(2);
                    break;
            }
        }
    }


    /**
     * A method that sets information to fill instance of Response pane;
     * @param responseInfo information of response
     */
    public void fillResponsePanel(ResponseInfo responseInfo) throws IOException {

        resetPanel();
        headersPanel.makeHeadersPanel(responseInfo);
        rawPanel.makeRawPanel(responseInfo);

        previewPanel.makePreviewPanel(responseInfo);

        status.setText(" "+responseInfo.getStatusCode()+" ");
        timeToGetResponse.setText(" "+responseInfo.getTimeOfSend()+" ");
        contentLength.setText(" "+responseInfo.getContentLength()+" ");

    }

    private void resetPanel() {
        headersPanel.removeAll();
        rawPanel.removeAll();
        previewPanel.removeAll();

    }

    /**
     * Panel to show response headers
     */
    public class ResponseHeadersPanel extends JPanel {

        public void makeHeadersPanel(ResponseInfo responseInfo) {

//            ArrayList<Integer> rowHeight = new ArrayList<>();
            setLayout(new BorderLayout());
            int counter = 0 ;
            String[][] headers = new String[responseInfo.getHeaders().length][2];
            for (int i =0 ; i<responseInfo.getHeaders().length ; i++) {
                headers[counter][0] = " "+responseInfo.getHeaders()[i].getName()+" ";
                headers[counter][1] = " "+responseInfo.getHeaders()[i].getValue()+" ";
//                rowHeight.add(findHeightOfCell(headers[counter][1],(int)(this.getSize().getWidth()/2)));
                counter++;
            }
        //    counter-- ;

            String[] columnNames = {"NAME" , "VALUE"};
            JTable table = new JTable(headers , columnNames);

//            for (int i = 0 ; i<counter ; i++ ){
//                table.setRowHeight(i,rowHeight.get(i));
//            }

            table.setRowHeight(30);
            table.setDefaultEditor(Object.class , null);
            JScrollPane sp = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            sp.setBorder(new EmptyBorder(20,10,0,10));
            add(sp,BorderLayout.CENTER);

            copyToClipBoard.addActionListener(e-> {
                if(headersPanel.isVisible()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i =0 ; i<responseInfo.getHeaders().length ;i++)
                        stringBuilder.append(responseInfo.getHeaders()[i].getName()).append(": ")
                                .append(responseInfo.getHeaders()[i].getValue() ).append( "\n");
                    StringSelection stringSelection = new StringSelection(stringBuilder.toString());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection , null);
                }
            });
        }

        private int findHeightOfCell(String value , int columnWeight){
            int charNumber = value.toCharArray().length;
            System.out.println(charNumber);
            int neededLine = (charNumber*100)/columnWeight ;
            return 30*(neededLine) ;
        }

    }

    /**
     * Panel to show raw body of response
     */
    public class RawPanel extends JPanel{
       JTextArea textArea ;
        public void makeRawPanel(ResponseInfo responseInfo){
            setLayout(new BorderLayout());
            textArea = new JTextArea();
            textArea.setBorder(new EmptyBorder(10,12,10,12));
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setText(new String(responseInfo.getBody(), StandardCharsets.UTF_8));
            JScrollPane scrollPane = new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                    ,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            add(scrollPane);

        }

        public JTextArea getTextArea() {
            return textArea;
        }
    }

    /**
     * Preview panel
     */
    public class PreviewPanel extends JPanel {


        public PreviewPanel() {
            setLayout(new BorderLayout());
        }

        private void makePreviewPanel(ResponseInfo responseInfo) throws IOException {
            String bodyType = findEntityType(responseInfo.getHeaders());

            if (bodyType.contains("image")) {
                add(new ImagePanel(responseInfo.getBody()),BorderLayout.CENTER);
            }
//            else if(bodyType.contains("html")){
//                JScrollPane scroll = new JScrollPane(new JEditorPane("text/html",new String(responseInfo.getBody() , StandardCharsets.UTF_8)),
//                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
//                        ,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//                add(scroll);
//            }
            else {

                JTextArea textArea = new JTextArea();
                textArea.setBorder(new EmptyBorder(10 , 12 , 10 , 12));
                textArea.setLineWrap(true);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea , ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                        , ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                add(scrollPane,BorderLayout.CENTER);

                if (bodyType.contains("json")) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject convertedObject = new Gson().fromJson(new String(responseInfo.getBody() , StandardCharsets.UTF_8) , JsonObject.class);
                    textArea.setText(gson.toJson(convertedObject));
                }
                else {
                    textArea.setText(new String(responseInfo.getBody() , StandardCharsets.UTF_8));
                }

            }

        }

        /**
         * A panel to show an image
         */
        private class ImagePanel extends JPanel {

            private long serialVersionUID = 1L;
            private BufferedImage myImage;

            public ImagePanel(byte[] response) throws IOException {
                ByteArrayInputStream stream = new ByteArrayInputStream(response);
                myImage = ImageIO.read(stream);
            }

            public void drawImage(BufferedImage img) {
                this.myImage = img;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (myImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    int x = (getWidth() - myImage.getWidth()) / 2;
                    int y = (getHeight() - myImage.getHeight()) / 2;
                    g2d.drawImage(myImage , x , y , this);
                    g2d.dispose();
                }
            }

        }

        private String findEntityType(Header[] headers) {
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("Content-Type"))
                    return header.getValue();
            }
            return "Nothing";

        }

    }

    public RawPanel getRawPanel() {
        return rawPanel;
    }
}