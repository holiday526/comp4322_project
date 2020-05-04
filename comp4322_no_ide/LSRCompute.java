import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;


public class LSRCompute {
    public final String CA_MODE = "CA";
    public final String SS_MODE = "SS";

    private JPanel infoPanel;
    private JPanel inputDataPanel;
    private JPanel diagramPanel;
    private JPanel mainPanel;
    private JTextArea loadingFileInfoTextArea;
    private JComboBox selectSourceComboBox;
    private JTextArea statusOutputTextArea;
    private JTextField newNodeTextField;
    private JComboBox removeNodeComboBox;
    private JButton singleStepButton;
    private JButton computeAllButton;
    private JTextArea linkBrokenInfoTextArea;
    private JLabel newNodeLabel;
    private JLabel topologyUpdateLabel;
    private JLabel removeNodeLabel;
    private JLabel linkBrokenLabel;
    private JLabel diagramLabel;
    private JLabel loadingFileLabel;
    private JLabel selectSourceLabel;
    private JLabel statusOutputLabel;
    private JPanel loadingFilePanel;
    private JPanel selectSourcePanel;
    private JPanel statusOutputPanel;
    private JPanel topologyUpdatePanel;
    private JPanel linkBrokenPanel;

    private List<GraphData> dataList;
    private JGraphXAdapter graphXAdapter;
    private JPanel graphPanel;
    private JPanel newNodePanel;
    private JTextArea newNodeAddedTextArea;
    private JLabel newNodeAddedLabel;
    private JButton resetLoadingFileButton;
    private JButton removeNodeButton;
    private JTextField linkBrokenTextField;
    private JButton linkBrokenSubmitButton;
    private JButton newNodeButton;
    private LSR lsr;

    private HashMap<String, List<GraphData>> nodesToEndNodes;

    public boolean checkNewNodeTextField() {
        if (this.newNodeTextField.getText().equals("")) {
            return false;
        }
        return true;
    }

    /*
        function to call GraphVisualization class to draw the shortest path network graph
    */
    public void drawPath(List<GraphData> graphDataList, List<String> path) {
        this.graphXAdapter = new GraphVisualization().getShortestPathGraphByPath(graphDataList, path);
        graphPanel.removeAll();
        graphPanel.revalidate();
        graphPanel.repaint();
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    /*
        function to call GraphVisualization class to draw the full network graph
    */
    public void drawGraph(List<GraphData> graphDataList) {
        this.graphXAdapter = new GraphVisualization().getNetworkGraph(graphDataList);
        graphPanel.removeAll();
        graphPanel.revalidate();
        graphPanel.repaint();
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }
    
    /*
        function to transform the input data to <string, list of GraphData> for LSA 
    */
    public void setNodesToEndNodes(List<String> linesInFile) {
        this.nodesToEndNodes = new HashMap<>();
        for (String line : linesInFile) {
            String nodesKey = line.substring(0, line.indexOf(":"));
            String tempToEndNodes[] = line.split(" ");
            List<GraphData> nodeToAllEndNodes = new ArrayList<>();
            for (int i = 1; i < tempToEndNodes.length; i++) {
                String endNodeAndWeight[] = tempToEndNodes[i].split(":");
                nodeToAllEndNodes.add(new GraphData(nodesKey, endNodeAndWeight[0], Integer.parseInt(endNodeAndWeight[1])));
                // nodeToAllEndNodes.add(new GraphData(endNodeAndWeight[0], nodesKey, Integer.parseInt(endNodeAndWeight[1])));
            }
            nodesToEndNodes.put(nodesKey, nodeToAllEndNodes);
        }
    }

    public void setNodesToEndNodes(HashMap<String, List<GraphData>> newNodesToEndNodes) {
        this.nodesToEndNodes = newNodesToEndNodes;
    }

    public void setDrawPath(HashMap<String, List<GraphData>> allNodesToEndNodes) {
        List<GraphData> graphDataList = new ArrayList<>();
        for (Map.Entry<String, List<GraphData>> node : allNodesToEndNodes.entrySet()) {
            graphDataList.addAll(node.getValue());
        }

        this.drawPath(graphDataList, getLsr().getPathTo(getLsr().getCurrentNearest()));
    }

    public void setDrawGraph(HashMap<String, List<GraphData>> allNodesToEndNodes, String startNode) {
        List<GraphData> graphDataList = new ArrayList<>();
        for (Map.Entry<String, List<GraphData>> node : allNodesToEndNodes.entrySet()) {
            graphDataList.addAll(node.getValue());
        }

        // set lsr
        this.lsr = new LSR(graphDataList, startNode);
        this.drawGraph(graphDataList);
        this.dataList = graphDataList;
    }

    public HashMap<String, List<GraphData>> getNodesToEndNodes() {
        return this.nodesToEndNodes;
    }

    public void setSelectSourceComboBox(HashMap<String, List<GraphData>> allNodesToEndNodes) {
        selectSourceComboBox.removeAllItems();
        for (String nodeKey : allNodesToEndNodes.keySet()) {
            selectSourceComboBox.addItem(nodeKey);
        }
    }

    public void setRemoveNodeComboBox(HashMap<String, List<GraphData>> allNodesToEndNodes) {
        removeNodeComboBox.removeAllItems();
        removeNodeComboBox.insertItemAt("Select node", 0);
        for (String nodeKey : allNodesToEndNodes.keySet()) {
            removeNodeComboBox.addItem(nodeKey);
        }
    }

    public String getRemoveNodeComboBoxValue() {
        try {
            return String.valueOf(this.removeNodeComboBox.getSelectedItem());
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public LSR getLsr() {
        return this.lsr;
    }

    public String getSingleStatusOutput(HashMap<String, Object> singleTable) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < ((List<String>) singleTable.get("path")).size(); i++) {
            if (i == 0) {
                path.append(((List<String>) singleTable.get("path")).get(i));
            } else {
                path.append(">" + ((List<String>) singleTable.get("path")).get(i));
            }
        }
        return "Found " + (String) singleTable.get("found") + " Path: " + path.toString() + " Cost: " + singleTable.get("cost");
    }

    public String getNewNodeTextFieldValue() {
        return this.newNodeTextField.getText();
    }

    public boolean checkDuplicateNodes(String newNodeStr) {
        String newNode = newNodeStr.substring(0, newNodeStr.indexOf(":"));
        // check duplicate nodes
        if (getNodesToEndNodes().containsKey(newNode.toUpperCase())) {
            return true;
        }
        return false;
    }

    public String getSourceNodeComboBoxValue() {
        return String.valueOf(this.selectSourceComboBox.getSelectedItem());
    }

    public JTextField getNewNodeTextField() {
        return newNodeTextField;
    }

    public void removeNode(String removeNodeKey, String startNode) {
        if (getNodesToEndNodes().containsKey(removeNodeKey)) {
            this.nodesToEndNodes.remove(removeNodeKey);
        }
        // TODO: delete the node, also need to delete the linkages
        HashMap<String, List<GraphData>> tempAllNodes = new HashMap<>();
        for (Map.Entry<String, List<GraphData>> entry : getNodesToEndNodes().entrySet()) {
            List<GraphData> temp = new ArrayList<>();
            for (GraphData gd : entry.getValue()) {
                if (!(gd.getFrom().equals(removeNodeKey) || gd.getTo().equals(removeNodeKey))) {
                    temp.add(gd);
                }
            }
            tempAllNodes.put(entry.getKey(), temp);
        }
        this.nodesToEndNodes = tempAllNodes;

        setSelectSourceComboBox(getNodesToEndNodes());
        setRemoveNodeComboBox(getNodesToEndNodes());
        selectSourceComboBox.setSelectedItem(startNode);

        setDrawGraph(getNodesToEndNodes(), startNode);
    }

    public void breakLink(String from, String to, String startNode) {
        HashMap<String, List<GraphData>> tempAllNodes = new HashMap<>();
        for (Map.Entry<String, List<GraphData>> entry : getNodesToEndNodes().entrySet()) {
            List<GraphData> temp = new ArrayList<>();
            for (GraphData gd : entry.getValue()) {
                if (gd.getFrom().equals(from) && gd.getTo().equals(to)) {
                    continue;
                }
                if (gd.getFrom().equals(to) && gd.getTo().equals(from)) {
                    continue;
                }
                temp.add(gd);
            }
            tempAllNodes.put(entry.getKey(), temp);
        }
        this.nodesToEndNodes = tempAllNodes;

        setSelectSourceComboBox(getNodesToEndNodes());
        setRemoveNodeComboBox(getNodesToEndNodes());
        selectSourceComboBox.setSelectedItem(startNode);

        setDrawGraph(getNodesToEndNodes(), startNode);
    }

    public void updateLsaObject(String startNode) {

        setSelectSourceComboBox(getNodesToEndNodes());
        selectSourceComboBox.setSelectedItem(startNode);

        setDrawGraph(getNodesToEndNodes(), startNode);
    }

    public JTextArea getStatusOutputTextArea() {
        return this.statusOutputTextArea;
    }

    public JTextArea getNewNodeAddedTextArea() {
        return newNodeAddedTextArea;
    }

    public JTextField getLinkBrokenTextField() {
        return linkBrokenTextField;
    }

    public void contentReset(String startNode, String fileName, String mode) {
        StringBuilder loadingFileInfo = new StringBuilder();
        List<String> linesInFile = new ArrayList<>();

        try {
            File fileInput = new File(fileName);
            Scanner fileReader = new Scanner(fileInput);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                loadingFileInfo.append(line + "\n");
                linesInFile.add(line);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        this.loadingFileInfoTextArea.setText(loadingFileInfo.toString());

        setNodesToEndNodes(linesInFile);

        //if start node is empty (initalization)
        if(startNode == ""){
            Map.Entry<String, List<GraphData>> entry = getNodesToEndNodes().entrySet().iterator().next();
            startNode = entry.getKey();
        }

        setDrawGraph(getNodesToEndNodes(), startNode);

        setSelectSourceComboBox(getNodesToEndNodes());
        setRemoveNodeComboBox(getNodesToEndNodes());

        selectSourceComboBox.setSelectedItem(startNode);

        if (!mode.isEmpty() && mode.toUpperCase().equals(CA_MODE)) {
            // CA mode here
            this.statusOutputTextArea.removeAll();
            getLsr().computeAll();
            for (HashMap<String, Object> table : getLsr().getAllTables()) {
                this.statusOutputTextArea.append(getSingleStatusOutput(table) + "\n");
            }
        } else if (!mode.isEmpty() && mode.toUpperCase().equals(SS_MODE)) {
            // SS mode here
            getLsr().computeNext();
            setDrawPath(getNodesToEndNodes());
            this.statusOutputTextArea.append(getSingleStatusOutput(getLsr().getTable()) + "\n");
        }
    }

    public LSRCompute(String fileName, String mode) {
        singleStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!getLsr().computeNext()) return;
                setDrawPath(getNodesToEndNodes());
                getStatusOutputTextArea().append(getSingleStatusOutput(getLsr().getTable()) + "\n");
            }
        });

        resetLoadingFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getStatusOutputTextArea().setText("");
                getNewNodeAddedTextArea().setText("");
                getLinkBrokenTextField().setText("");
                getNewNodeTextField().setText("");
                contentReset("", fileName, mode);
            }
        });

        computeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getStatusOutputTextArea().setText("");
                updateLsaObject(getSourceNodeComboBoxValue());
                getLsr().computeAll();
                for (HashMap<String, Object> table : getLsr().getAllTables()) {
                    getStatusOutputTextArea().append(getSingleStatusOutput(table) + "\n");
                }
            }
        });

        removeNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeNode(getRemoveNodeComboBoxValue(), getSourceNodeComboBoxValue());
            }
        });

        linkBrokenSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(linkBrokenTextField.getText().equals(""))) {
                    String[] linkBroken = linkBrokenTextField.getText().split(">");
                    breakLink(linkBroken[0], linkBroken[1], getSourceNodeComboBoxValue());
                }
            }
        });

        newNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkNewNodeTextField()) {
                    // have string in newNodeTextField
                    if (!checkDuplicateNodes(getNewNodeTextFieldValue())) {

                        getStatusOutputTextArea().setText("");
                        String newNodeStr = getNewNodeTextFieldValue();
                        String newNode = newNodeStr.substring(0, newNodeStr.indexOf(":"));
                        String newNodeRoutesArr[] = newNodeStr.split(" ");
                        List<GraphData> newNodeRoutes = new ArrayList<>();
                        for (int i = 1; i < newNodeRoutesArr.length; i++) {
                            String[] tempArr = newNodeRoutesArr[i].split(":");
                            newNodeRoutes.add(new GraphData(newNode, tempArr[0], Integer.parseInt(tempArr[1])));
                            newNodeRoutes.add(new GraphData(tempArr[0], newNode, Integer.parseInt(tempArr[1])));
                        }
                        nodesToEndNodes.put(newNode, newNodeRoutes);

                        getNewNodeAddedTextArea().append(getNewNodeTextFieldValue() + "\n");
                        updateLsaObject(getSourceNodeComboBoxValue());
                        getNewNodeTextField().setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "There is duplicated node");
                    }
                } else {

                }
            }
        });

        contentReset("", fileName, mode);
    }

    public static void main(String args[]) {
        String readFileName;
        String startNode;
        String computeMode;
        try {
            readFileName = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid parameter input");
            System.out.println("No filename or start node");
            return;
        }

        try {
            computeMode = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            computeMode = "";
        }

        JFrame frame = new JFrame("LSRCompute");
        frame.setSize(900, 900);
        frame.setContentPane(new LSRCompute(readFileName, computeMode).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel = new JPanel();
        infoPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(infoPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        loadingFilePanel = new JPanel();
        loadingFilePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.add(loadingFilePanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        loadingFileLabel = new JLabel();
        Font loadingFileLabelFont = this.$$$getFont$$$(null, -1, 16, loadingFileLabel.getFont());
        if (loadingFileLabelFont != null) loadingFileLabel.setFont(loadingFileLabelFont);
        loadingFileLabel.setText("Loading file:");
        loadingFilePanel.add(loadingFileLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadingFileInfoTextArea = new JTextArea();
        loadingFilePanel.add(loadingFileInfoTextArea, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        resetLoadingFileButton = new JButton();
        resetLoadingFileButton.setText("Reset");
        loadingFilePanel.add(resetLoadingFileButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectSourcePanel = new JPanel();
        selectSourcePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.add(selectSourcePanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectSourceLabel = new JLabel();
        selectSourceLabel.setText("Select source: ");
        selectSourcePanel.add(selectSourceLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectSourceComboBox = new JComboBox();
        selectSourcePanel.add(selectSourceComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusOutputPanel = new JPanel();
        statusOutputPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.add(statusOutputPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        statusOutputLabel = new JLabel();
        Font statusOutputLabelFont = this.$$$getFont$$$(null, -1, 16, statusOutputLabel.getFont());
        if (statusOutputLabelFont != null) statusOutputLabel.setFont(statusOutputLabelFont);
        statusOutputLabel.setText("Status output:");
        statusOutputPanel.add(statusOutputLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusOutputTextArea = new JTextArea();
        statusOutputPanel.add(statusOutputTextArea, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        inputDataPanel = new JPanel();
        inputDataPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(inputDataPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topologyUpdatePanel = new JPanel();
        topologyUpdatePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        inputDataPanel.add(topologyUpdatePanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topologyUpdateLabel = new JLabel();
        Font topologyUpdateLabelFont = this.$$$getFont$$$(null, Font.BOLD, 22, topologyUpdateLabel.getFont());
        if (topologyUpdateLabelFont != null) topologyUpdateLabel.setFont(topologyUpdateLabelFont);
        topologyUpdateLabel.setText("Topology update:");
        topologyUpdatePanel.add(topologyUpdateLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newNodeLabel = new JLabel();
        newNodeLabel.setText("New node:");
        topologyUpdatePanel.add(newNodeLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newNodeTextField = new JTextField();
        topologyUpdatePanel.add(newNodeTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        removeNodeLabel = new JLabel();
        removeNodeLabel.setText("Remove node:");
        topologyUpdatePanel.add(removeNodeLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeNodeComboBox = new JComboBox();
        topologyUpdatePanel.add(removeNodeComboBox, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeNodeButton = new JButton();
        removeNodeButton.setText("Remove node");
        topologyUpdatePanel.add(removeNodeButton, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newNodeButton = new JButton();
        newNodeButton.setText("New node");
        topologyUpdatePanel.add(newNodeButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        linkBrokenPanel = new JPanel();
        linkBrokenPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        inputDataPanel.add(linkBrokenPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        linkBrokenLabel = new JLabel();
        linkBrokenLabel.setText("Link broken: ");
        linkBrokenPanel.add(linkBrokenLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        linkBrokenTextField = new JTextField();
        linkBrokenPanel.add(linkBrokenTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        linkBrokenSubmitButton = new JButton();
        linkBrokenSubmitButton.setText("break link");
        linkBrokenPanel.add(linkBrokenSubmitButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newNodePanel = new JPanel();
        newNodePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        inputDataPanel.add(newNodePanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newNodeAddedLabel = new JLabel();
        newNodeAddedLabel.setText("New node added:");
        newNodePanel.add(newNodeAddedLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newNodeAddedTextArea = new JTextArea();
        newNodePanel.add(newNodeAddedTextArea, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        diagramPanel = new JPanel();
        diagramPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(diagramPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        diagramLabel = new JLabel();
        Font diagramLabelFont = this.$$$getFont$$$(null, Font.BOLD, 22, diagramLabel.getFont());
        if (diagramLabelFont != null) diagramLabel.setFont(diagramLabelFont);
        diagramLabel.setText("Diagram: ");
        diagramPanel.add(diagramLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout(0, 0));
        diagramPanel.add(graphPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        diagramPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        singleStepButton = new JButton();
        singleStepButton.setText("Single step");
        panel1.add(singleStepButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        computeAllButton = new JButton();
        computeAllButton.setText("Compute all");
        panel1.add(computeAllButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
