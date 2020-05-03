import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;


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
    private LSA lsa;

    private HashMap<String, List<GraphData>> nodesToEndNodes;

    public boolean checkNewNodeTextField() {
        if (this.newNodeTextField.getText().equals("")) {
            return false;
        }
        return true;
    }

    public void drawPath(java.util.List<GraphData> graphDataList, java.util.List<String> path){
        this.graphXAdapter = new GraphVisualization().getShortestPathGraphByPath(graphDataList, path);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public void drawGraph(List<GraphData> graphDataList){
        this.graphXAdapter = new GraphVisualization().getNetworkGraph(graphDataList);
        graphPanel.removeAll();
        graphPanel.revalidate();
        graphPanel.repaint();
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public void setNodesToEndNodes(List<String> linesInFile) {
        this.nodesToEndNodes = new HashMap<>();
        for (String line : linesInFile) {
            String nodesKey = line.substring(0,line.indexOf(":"));
            String tempToEndNodes[] = line.split(" ");
            List<GraphData> nodeToAllEndNodes = new ArrayList<>();
            for (int i = 1; i < tempToEndNodes.length; i++) {
                String endNodeAndWeight[] = tempToEndNodes[i].split(":");
                nodeToAllEndNodes.add(new GraphData(nodesKey, endNodeAndWeight[0], Integer.parseInt(endNodeAndWeight[1])));
                nodeToAllEndNodes.add(new GraphData(endNodeAndWeight[0], nodesKey, Integer.parseInt(endNodeAndWeight[1])));
            }
            nodesToEndNodes.put(nodesKey, nodeToAllEndNodes);
        }
    }

    public void setNodesToEndNodes(HashMap<String, List<GraphData>> newNodesToEndNodes) {
        this.nodesToEndNodes = newNodesToEndNodes;
    }

    public void setDrawGraph(HashMap<String, List<GraphData>> allNodesToEndNodes, String startNode) {
        List<GraphData> graphDataList = new ArrayList<>();
        for (Map.Entry<String, List<GraphData>> node : allNodesToEndNodes.entrySet()) {
            graphDataList.addAll(node.getValue());
        }

        // set lsa
        this.lsa = new LSA(graphDataList, startNode);
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

    public LSA getLsa() {
        return this.lsa;
    }

    public String getSingleStatusOutput(HashMap<String, Object> singleTable){
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < ((List<String>)singleTable.get("path")).size(); i++) {
            if (i == 0) {
                path.append(((List<String>)singleTable.get("path")).get(i));
            } else {
                path.append(">" + ((List<String>)singleTable.get("path")).get(i));
            }
        }
        return "Found " + (String)singleTable.get("found") + " Path: " + path.toString() + " Cost: " + singleTable.get("cost");
    }

    public String getNewNodeTextFieldValue() {
        return this.newNodeTextField.getText();
    }

    public boolean checkDuplicateNodes(String newNodeStr) {
        String newNode = newNodeStr.substring(0,newNodeStr.indexOf(":"));
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

    public void updateLsaObject(String startNode, String removeNode) {
        if (getNodesToEndNodes().containsKey(removeNode)) {
            this.nodesToEndNodes.remove(removeNode);
        }
        // TODO: delete the node, also need to delete the linkages
        for (Map.Entry<String, List<GraphData>> entry : this.nodesToEndNodes.entrySet()) {
//            List<GraphData> temp = entry.getValue();
            for (GraphData gd : entry.getValue()) {
                if (gd.getFrom().equals(removeNode) || gd.getTo().equals(removeNode)) {
                    entry.getValue().remove(gd);
                }
            }
//            this.nodesToEndNodes.put(entry.getKey(),temp);
        }
        setSelectSourceComboBox(getNodesToEndNodes());
        setRemoveNodeComboBox(getNodesToEndNodes());
        selectSourceComboBox.setSelectedItem(startNode);

        setDrawGraph(getNodesToEndNodes(), startNode);
    }

    public JTextArea getStatusOutputTextArea() {
        return this.statusOutputTextArea;
    }

    public JTextArea getNewNodeAddedTextArea() {
        return newNodeAddedTextArea;
    }

    public JTextArea getLinkBrokenInfoTextArea() {
        return linkBrokenInfoTextArea;
    }

    public void contentReset(String startNode, String fileName, String mode) {
        StringBuilder loadingFileInfo = new StringBuilder();
        List<String> linesInFile = new ArrayList<>();

        try {
            File fileInput = new File("src/"+fileName);
            Scanner fileReader = new Scanner(fileInput);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                loadingFileInfo.append(line+"\n");
                linesInFile.add(line);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        this.loadingFileInfoTextArea.setText(loadingFileInfo.toString());

        setNodesToEndNodes(linesInFile);

        setDrawGraph(getNodesToEndNodes(), startNode);

        setSelectSourceComboBox(getNodesToEndNodes());
        setRemoveNodeComboBox(getNodesToEndNodes());

        selectSourceComboBox.setSelectedItem(startNode);

        if (!mode.isEmpty() && mode.toUpperCase().equals(CA_MODE)) {
            // CA mode here
            this.statusOutputTextArea.removeAll();
            getLsa().computeAll();
            for (HashMap<String, Object> table : getLsa().getAllTables()) {
                this.statusOutputTextArea.append(getSingleStatusOutput(table) + "\n");
            }
        } else if (!mode.isEmpty() && mode.toUpperCase().equals(SS_MODE)) {
            // SS mode here
            getLsa().computeNext();
            this.statusOutputTextArea.append(getSingleStatusOutput(getLsa().getTable()));
        }
    }

    public LSRCompute(String startNode, String fileName, String mode) {
        singleStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkNewNodeTextField()) {
                    // have string in newNodeTextField
                    if (!checkDuplicateNodes(getNewNodeTextFieldValue())){
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
                        // TODO: update lsa object
                        updateLsaObject(getSourceNodeComboBoxValue(), getRemoveNodeComboBoxValue());
                        getLsa().computeNext();
                        getStatusOutputTextArea().append(getSingleStatusOutput(getLsa().getTable()));
                        getNewNodeTextField().setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "There is duplicated node");
                    }
                } else {
                    // no string in newNodeTextField
                    if (getRemoveNodeComboBoxValue() != null) {
                        updateLsaObject(getSourceNodeComboBoxValue(), getRemoveNodeComboBoxValue());
                    }
                    getLsa().computeNext();
                    getStatusOutputTextArea().append("\n"+getSingleStatusOutput(getLsa().getTable()));
                }
            }
        });

        resetLoadingFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getStatusOutputTextArea().setText("");
                getNewNodeAddedTextArea().setText("");
                getLinkBrokenInfoTextArea().setText("");
                getNewNodeTextField().setText("");
                contentReset(startNode, fileName, mode);
            }
        });

        computeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: compute all
            }
        });

        contentReset(startNode, fileName, mode);
    }



    public static void main(String args[]) {
        String readFileName;
        String startNode;
        String computeMode;
        try {
            readFileName = args[0];
            startNode = args[1];
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Invalid parameter input");
            System.out.println("No filename or start node");
            return;
        }

        try {
            computeMode = args[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            computeMode = "";
        }

        JFrame frame = new JFrame("LSRCompute");
        frame.setSize(900, 900);
        frame.setContentPane(new LSRCompute(startNode, readFileName, computeMode).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
