import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private LSA lsa;

    private HashMap<String, List<GraphData>> nodesToEndNodes;

    public void drawPath(java.util.List<GraphData> graphDataList, java.util.List<String> path){
        this.graphXAdapter = new GraphVisualization().getShortestPathGraphByPath(graphDataList, path);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public void drawGraph(List<GraphData> graphDataList){
        this.graphXAdapter = new GraphVisualization().getNetworkGraph(graphDataList);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public void setNodesToEndNodes(List<String> linesInFile) {
        this.nodesToEndNodes = new HashMap<>();
        for (String line : linesInFile) {
            String nodesKey = line.substring(0,1);
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

    public void setDrawGraph(HashMap<String, List<GraphData>> allNodesToEndNodes, String startNode) {
        List<GraphData> graphDataList = new ArrayList<>();
        for (Map.Entry<String, List<GraphData>> node : allNodesToEndNodes.entrySet()) {
            graphDataList.addAll(node.getValue());
        }

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

    public LSA getLsa() {
        return this.lsa;
    }

//    public LSRCompute(List<GraphData> graphDataList, String node, String loadingFileInfoText) {
    public LSRCompute(String startNode, String fileName, String mode) {
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

        selectSourceComboBox.setSelectedItem(startNode);

        if (!mode.isEmpty() && mode.toUpperCase().equals(CA_MODE)) {
            // CA mode here
            getLsa().computeAll();
            getLsa().printAllTable();
        } else if (!mode.isEmpty() && mode.toUpperCase().equals(SS_MODE)) {
            // SS mode here
            getLsa().computeNext();
            getLsa().printTable();
            Scanner input = new Scanner(System.in);
            while (true) {
                if (input.nextLine().equals("E")) {
                    break;
                } else {
                    getLsa().computeNext();
                    getLsa().printTable();
                }
            }
        }
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
