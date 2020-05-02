import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.util.ArrayList;

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

    public LSRCompute(String args[]) {
        try {
            this.loadingFileInfoTextArea.setText(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {

        }
        String a = "A1";
        String b = "B2";
        String c = "C3";
        String d = "D4";
        String e = "E5";
        String f = "F6";


        List<GraphData> graphDataList = new ArrayList<>();
        graphDataList.add(new GraphData(a, b, 5));
        graphDataList.add(new GraphData(b, a, 5));

        graphDataList.add(new GraphData(a, c, 3));
        graphDataList.add(new GraphData(c, a, 3));

        graphDataList.add(new GraphData(a, d, 5));
        graphDataList.add(new GraphData(d, a, 5));

        graphDataList.add(new GraphData(c, b, 4));
        graphDataList.add(new GraphData(b, c, 4));

        graphDataList.add(new GraphData(c, d, 1));
        graphDataList.add(new GraphData(d, c, 1));

        graphDataList.add(new GraphData(b, f, 2));
        graphDataList.add(new GraphData(f, b, 2));

        graphDataList.add(new GraphData(b, e, 3));
        graphDataList.add(new GraphData(e, b, 3));

        graphDataList.add(new GraphData(d, e, 3));
        graphDataList.add(new GraphData(e, d, 3));

//        JFrame frame = new JFrame("App");
//        Main main = new Main();
        this.lsa = new LSA(graphDataList, a);
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
