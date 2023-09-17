package ui.gui;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;

public class PairPanel {
    /**
     * A panel which contain array of OneLinePairPanels and place these panels together
     */
    public static class MultiLinePairPanel extends JPanel {
        private String panelName;
        private ArrayList<OneLinePairPanel> lines;
        private JPanel lastLine;
        private GridBagConstraints gbc = new GridBagConstraints();
        private boolean fileSelectable;
        private JPanel scroll;

        public MultiLinePairPanel(ArrayList<OneLinePairPanel> lines , boolean fileSelectable , String panelName) {
            super();
            this.panelName = panelName;
            this.lines = lines;
            this.fileSelectable = fileSelectable;
            makePanel();
        }


        private void makePanel() {
            scroll = new JPanel();
            setLayout(new BorderLayout());
            JScrollPane jScrollPane = new JScrollPane(scroll , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            add(jScrollPane , BorderLayout.CENTER);
            GridBagLayout layout = new GridBagLayout();
            scroll.setLayout(layout);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            updatePanel();

        }

        public JPanel getScroll() {
            return scroll;
        }

        private void addNewLine() {
            lines.add(new OneLinePairPanel(lines.size() , fileSelectable , panelName));
            updatePanel();
        }

        public void deleteALine(int lineIndex) {
            lines.remove(lineIndex);
            updatePanel();
        }

        private JPanel makeLastLine() {
            JPanel lastPanel = new JPanel();
            lastPanel.setLayout(new BorderLayout(5 , 5));
            JPanel texts = new JPanel();
            texts.setLayout(new GridLayout(1 , 2 ,5,5));
            JTextField key = new JTextField();
            key.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    addNewLine();
                }
            });
            texts.add(key);

            JTextField value = new JTextField();
            value.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    addNewLine();
                }
            });
            texts.add(value);
            JButton reset = new JButton();
            reset.setText("⚙");
            reset.setOpaque(false);
            reset.setContentAreaFilled(false);
            reset.setFont(new Font("Dialog" , Font.BOLD , 18));
            reset.addActionListener(e -> {

                int confirm = JOptionPane.showConfirmDialog(null , "Are you sure you want to delete all the headers?" , "Confirm frame" , JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    resetMultiLinePairPanel();
                }
            });
            lastPanel.add(reset , BorderLayout.WEST);
            lastPanel.add(texts , BorderLayout.CENTER);
            lastPanel.setBorder(new EmptyBorder(5 , 3 , 0 , fileSelectable ? 176 : 110));
            return lastPanel;
        }

        public ArrayList<OneLinePairPanel> getLines() {
            return lines;
        }

        public void updatePanel() {

            scroll.removeAll();
            for (int i = 0; i < lines.size(); i++) {
                lines.get(i).setIndex(i);
                gbc.gridy = (i);
                scroll.add(lines.get(i) , gbc);
            }
            lastLine = makeLastLine();
            gbc.gridy = lines.size();
            scroll.add(lastLine , gbc);
            scroll.revalidate();
            scroll.repaint();
        }

        public void resetMultiLinePairPanel() {
           while (lines.size()>0)
               deleteALine(lines.size()-1);
        }
    }

    /**
     * this is a panel which contains key and value . for example for header or multiPart form data is used
     */
    public static class OneLinePairPanel extends JPanel {
        private RequestPanel requestPanel = RequestPanel.getInstance();
        private int index;
        private String filePath;
        private boolean isValueAFile;
        private String parentPanelName;
        private boolean fileSelectable;
        private JTextField key;
        private JPanel value;
        private JTextField textValue;
        private JButton selectFile;
        private JComboBox<String> typeOfKey;
        private JCheckBox activation;
        private JButton deleteButton;

        public OneLinePairPanel(int index , boolean fileSelectable , String parentPanelName) {
            this.index = index;
            this.parentPanelName = parentPanelName;
            this.fileSelectable = fileSelectable;
            isValueAFile = false;
            key = new JTextField();
            key.setMinimumSize(key.getPreferredSize());
            value = new JPanel();
            textValue = new JTextField();
            textValue.setMinimumSize(textValue.getPreferredSize());
            selectFile = new JButton("select File");
            value.setLayout(new BorderLayout());
            value.add(selectFile , BorderLayout.CENTER);
            selectFile.setVisible(false);
            value.add(textValue , BorderLayout.CENTER);

            typeOfKey = makeTypeOfKeyComboBox();
            activation = new JCheckBox();
            activation.setSelected(true);
            deleteButton = makeDeleteButton();
            placeDifferentPartsOfPairPanel();
        }

        private JComboBox makeTypeOfKeyComboBox() {

            selectFile.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int i = fileChooser.showOpenDialog(OneLinePairPanel.this);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    filePath = f.getPath();
                    String fileName = f.getName();
                    selectFile.setText(fileName);
                    selectFile.setToolTipText(filePath);
                }
            });
            String[] types = new String[]{"Text" , "File"};
            JComboBox typeOfKey = new JComboBox<>(types);
            typeOfKey.addActionListener(e -> {

                JComboBox comboBox = (JComboBox) e.getSource();
                String type = (String) comboBox.getSelectedItem();
                 if (type.equals("File")) {
                    isValueAFile = true;
                    textValue.setVisible(false);
                    selectFile.setVisible(true);
                    value.add(selectFile , BorderLayout.CENTER);
                }
                else if (type.equals("Text")) {
                    isValueAFile = false;
                    textValue.setVisible(true);
                    selectFile.setVisible(false);
                    value.add(textValue , BorderLayout.CENTER);
                }


            });
            return typeOfKey;
        }

        private JButton makeDeleteButton() {
            int counter = 0;
            JButton delete = new JButton("🗑️" );
            delete.setFont(new Font("Segoe UI Emoji",Font.BOLD,14));
            delete.addActionListener(e -> requestPanel.deleteButtonProcessForAOneLinePairPanel(parentPanelName , index));
            return delete;
        }

        private void placeDifferentPartsOfPairPanel() {
            BorderLayout layout = new BorderLayout();
            setLayout(layout);
            JPanel east = new JPanel();
            east.setLayout(new FlowLayout(FlowLayout.LEFT));
            if (fileSelectable)
                east.add(typeOfKey);
            east.add(activation);
            east.add(deleteButton);

            JPanel center = new JPanel();
            center.setLayout(new GridLayout(1 , 2 , 5 , 5));
           setBorder(new EmptyBorder(5,42,5,0));
            center.add(key);
            center.add(value);

            add(center , BorderLayout.CENTER);
            add(east , BorderLayout.EAST);
        }

        public int getIndex() {
            return index;
        }

        public String getKey() {
            return key.getText();
        }
        public JTextField getKeyComponent(){
            return key ;
        }

        public String getTextValue() {
            return textValue.getText();
        }

        public JTextField getTextValueComponent(){
            return textValue ;
        }
        public boolean isValueAFile() {

            return typeOfKey.getSelectedItem().equals("File");
        }

        public boolean isActive() {
            return activation.isSelected();
        }

        public String getFilePath() {
            return filePath;
        }

        public String getParentPanelName() {
            return parentPanelName;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public JComboBox<String> getTypeOfKey() {
            return typeOfKey;
        }

        public JButton getSelectFile() {
            return selectFile;
        }
    }

}
