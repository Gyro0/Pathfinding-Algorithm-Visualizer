package models;

public class Edge {
    private final Cell destination;
    private double weight;

    public Edge(Cell destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public Cell getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
