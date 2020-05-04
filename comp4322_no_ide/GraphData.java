public class GraphData {
    public String from;
    public String to;
    public Integer weight;

    public GraphData(String from, String to, Integer weight){
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public GraphData(String from, String to){
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }
}
