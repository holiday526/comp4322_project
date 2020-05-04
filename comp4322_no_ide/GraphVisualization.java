import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import java.util.*;


class MyWeightedEdge extends DefaultWeightedEdge {
    @Override
    public String toString() {
        return Integer.toString((int) getWeight());
    }
}

public class GraphVisualization{
    private static JGraphXAdapter<String, MyWeightedEdge> jgxAdapter;
    private Graph<String, MyWeightedEdge> graph;

    public GraphVisualization() {

    }

    private void initGraph(){
        this.graph = GraphTypeBuilder.<String, MyWeightedEdge>
                directed()
                .weighted(true)
                .allowingMultipleEdges(true)
                .allowingSelfLoops(false)
                .edgeClass(MyWeightedEdge.class)
                .buildGraph();
    }

    private void setEdge(String a, String b, Integer weight){
        MyWeightedEdge edge = this.graph.addEdge(a, b);
        this.graph.setEdgeWeight(edge, weight);
    }

    private void drawGraph(List<GraphData> graphDataList){
        List<String> insertedVertex = new ArrayList<>();
        for (GraphData data: graphDataList) {
            if(!insertedVertex.contains(data.from)) {
                insertedVertex.add(data.from);
            }
        }
        for (GraphData data: graphDataList) {
            if(!insertedVertex.contains(data.to)) {
                insertedVertex.add(data.to);
            }
        }
        for (String vertex: insertedVertex){
            this.graph.addVertex(vertex);
        }
        for (GraphData data: graphDataList) {
            setEdge(data.from, data.to, data.weight);
        }
    }

    private Object[] getShortPathEdge(HashMap<MyWeightedEdge, mxICell> edgeToCellMap, List<GraphData> path){
        List<Object> edges = new ArrayList<>();
        for (GraphData data: path){
            MyWeightedEdge edge = this.graph.getEdge(data.from, data.to);
            edges.add(edgeToCellMap.get(edge));
            MyWeightedEdge biDirectionEdge = this.graph.getEdge(data.to, data.from);
            if(edgeToCellMap.containsKey(biDirectionEdge)){
                edges.add(edgeToCellMap.get(biDirectionEdge));
            }
        }
        return edges.toArray();
    }

    private Object[] getShortPathVertex(HashMap<String,com.mxgraph.model.mxICell> vertexToCellMap, List<GraphData> path){
        Object[] vertices = new Object[path.size()+1];
        int i=0;
        for (GraphData data: path){
            vertices[i++] = vertexToCellMap.get(data.from);
        }
        vertices[i] = vertexToCellMap.get(path.get(i-1).to);
        return vertices;
    }

    public JGraphXAdapter getNetworkGraph(List<GraphData> graphDataList){
        initGraph();
        drawGraph(graphDataList);
        ListenableGraph<String, MyWeightedEdge> g = new DefaultListenableGraph<>(this.graph);
        jgxAdapter = new JGraphXAdapter<>(g);
        draw();
        return jgxAdapter;
    }

    public JGraphXAdapter getShortestPathGraphByPath(List<GraphData> graphDataList, List<String> path){
        List<GraphData> pathList = new ArrayList<>();
        for(int i=1; i<path.size(); i++){
            pathList.add(new GraphData(path.get(i-1), path.get(i)));
        }
        return getShortestPathGraph(graphDataList, pathList);
    }

    public JGraphXAdapter getShortestPathGraph(List<GraphData> graphDataList, List<GraphData> path){
        initGraph();
        drawGraph(graphDataList);
        ListenableGraph<String, MyWeightedEdge> g = new DefaultListenableGraph<>(this.graph);
        jgxAdapter = new JGraphXAdapter<>(g);

        // find the edges in the shortest path & change the color
        HashMap<MyWeightedEdge, mxICell> edgeToCellMap = jgxAdapter.getEdgeToCellMap();
        jgxAdapter.setCellStyle("strokeColor=#FF0000", getShortPathEdge(edgeToCellMap, path));

        // find the vertices in the shortest path & change the color
        HashMap<String,com.mxgraph.model.mxICell> vertexToCellMap = jgxAdapter.getVertexToCellMap();
        jgxAdapter.setCellStyle("fillColor=#FF0000", getShortPathVertex(vertexToCellMap, path));

        draw();
        return jgxAdapter;
    }

    private void draw(){
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxGraphModel graphModel = (mxGraphModel) graphComponent.getGraph().getModel();
        Collection<Object> cells = graphModel.getCells().values();
        mxStyleUtils.setCellStyles(graphModel, cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
        // jgxAdapter.setCellsEditable(false);
        // jgxAdapter.setCellsMovable(false);
        // jgxAdapter.setCellsSelectable(false);
        // jgxAdapter.setEdgeLabelsMovable(false);
        // jgxAdapter.setEnabled(false);
    }
}
