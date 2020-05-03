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

    public void drawPath(java.util.List<GraphData> graphDataList, java.util.List<String> path){
        this.graphXAdapter = new GraphVisualization().getShortestPathGraphByPath(graphDataList, path);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public void drawGraph(List<GraphData> graphDataList){
        this.graphXAdapter = new GraphVisualization().getNetworkGraph(graphDataList);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

//    public LSRCompute(List<GraphData> graphDataList, String node, String loadingFileInfoText) {
    public LSRCompute(String args[]) {
        StringBuilder loadingFileInfo = new StringBuilder();
        List<String> linesInFile = new ArrayList<>();

        try {
            File fileInput = new File("src/"+args[0]);
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

        try {
            this.loadingFileInfoTextArea.setText(loadingFileInfo.toString());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No input parameter detected!");
        }

        HashMap<String, List<GraphData>> nodesToEndNodes = new HashMap<>();
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

        List<GraphData> graphDataList = new ArrayList<>();

        for (Map.Entry<String, List<GraphData>> node : nodesToEndNodes.entrySet()) {
            graphDataList.addAll(node.getValue());
        }

        this.lsa = new LSA(graphDataList, "A");
        this.drawGraph(graphDataList);
        this.dataList = graphDataList;
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("LSRCompute");
        frame.setSize(900, 900);
        frame.setContentPane(new LSRCompute(args).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}
