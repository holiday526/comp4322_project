import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class LSA {
    private int[][] weights;
    private List<String> nodes = new ArrayList<>();
    private String[] parents;
    private String start;
    private int currentStep = 1;
    private int stepCount;
    private boolean[] inserted;
    private int[] distances;
    private String nearest;

    private void extractFeature(List<GraphData> dataList){
        // Get number of Nodes
        for(GraphData data: dataList){
            if(!nodes.contains(data.to)){
                nodes.add(data.to);
            }
        }
        for(GraphData data: dataList){
            if(!nodes.contains(data.from)) {
                nodes.add(data.from);
            }
        }
        // Form the weight table for Dijkstra
        weights = new int[nodes.size()][nodes.size()];

        //initialize weight table
        for(int i=0; i<weights.length; i++){
            for(int j=0; j<weights[i].length; j++){
                weights[i][j] = 0;
            }
        }
        //map value to table
        for(GraphData data: dataList){
            weights[nodes.indexOf(data.from)][nodes.indexOf(data.to)] = data.weight;
        }
    }

    public boolean computeNext(){
        if(currentStep <= stepCount){
            nearest = start;
            int nearestIndex = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for(int index=0; index<stepCount; index++){
                if((shortestDistance > distances[index]) && !inserted[index]){
                    nearestIndex = index;
                    nearest = nodes.get(nearestIndex);
                    shortestDistance = distances[nearestIndex];
                }
            }
            inserted[nearestIndex] = true;

            //update table
            for(int index=0; index<stepCount; index++){
                int distance = weights[nearestIndex][index];
                if(distance > 0 && ( (distances[index]) > (shortestDistance + distance))){
                    parents[index] = nearest;
                    distances[index] = shortestDistance + distance;
                }
            }
            currentStep++;
            return true;
        }
        return false;
    }

    public void computeAll(){
        while(computeNext());
    }

    private void initDijkstra(String start){
        int startIndex = nodes.indexOf(start);
        stepCount = weights[0].length;
        distances = new int[stepCount];
        inserted = new boolean[stepCount];
        this.start = start;

        //init the list
        for(int i=0; i<stepCount; i++){
            distances[i] = Integer.MAX_VALUE;
            inserted[i] = false;
        }

        distances[startIndex] = 0; //the starting point has 0 distance value
        parents = new String[stepCount];
        parents[startIndex] = this.start;

        // force run first step to skip the base case
        computeNext();
    }

    private List<String> findPath(String target, List<String> path){
        int nodeIndex = nodes.indexOf(target);
        path.add(0, parents[nodeIndex]);
        if(parents[nodeIndex].compareTo(start) != 0){
            return findPath(parents[nodeIndex], path);
        }
        return path;
    }

    public String getCurrentNearest(){
        return nearest;
    }

    public List<String> getPathTo(String target){
        List<String> result = findPath(target, new ArrayList<>());
        if(target != start){
            result.add(target);
        }
        return result;
    }

    public void printAllTable(){
        for (String node: nodes){
            if(node != start){
                printTable(node);
            }
        }
    }

    public void printTable(){
        printTable(nearest);
    }

    public void printTable(String target){
        System.out.print("Found " + target + " : ");
        System.out.print("Cost: " + distances[nodes.indexOf(target)]);
        System.out.println(" path: " + getPathTo(target));
    }

    public LSA(List<GraphData> dataList, String start){
        extractFeature(dataList);
        initDijkstra(start);
    }

}
