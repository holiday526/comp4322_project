import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private JPanel rootPanel;
    private JLabel textLabel;
    private JPanel graphPanel;
    private JButton testButton;

    private List<GraphData> dataList;
    private JGraphXAdapter graphXAdapter;
    private LSA lsa;

    public Main() {
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
//                lsa.computeAll();
//                lsa.printAllTable();
                if(lsa.computeNext()){
                    lsa.printTable();
                    graphPanel.removeAll();
                    drawPath(dataList, lsa.getPathTo(lsa.getCurrentNearest()));
                    graphPanel.revalidate();
                    graphPanel.repaint();
                }
            }
        });
    }

    public void drawPath(List<GraphData> graphDataList, List<String> path){
        this.graphXAdapter = new GraphVisualization().getShortestPathGraphByPath(graphDataList, path);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public void drawGraph(List<GraphData> graphDataList){
        this.graphXAdapter = new GraphVisualization().getNetworkGraph(graphDataList);
        graphPanel.add(new mxGraphComponent(this.graphXAdapter));
    }

    public static void main(String[] args) {
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

        JFrame frame = new JFrame("App");
        Main main = new Main();
        main.lsa = new LSA(graphDataList, a);
        main.drawGraph(graphDataList);
        main.dataList = graphDataList;
        frame.add(main.rootPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
