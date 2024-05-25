package org.example.labgeo;

public class Segment implements Comparable<Segment> {

    private Point p1;
    private Point p2;
    double value;

    public Segment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.calculateValue(this.first().getXCoord());
    }

    public Point first() {
        if(p1.getXCoord() <= p2.getXCoord()) {
            return p1;
        } else {
            return p2;
        }
    }

    public Point second() {
        if(p1.getXCoord() <= p2.getXCoord()) {
            return p2;
        } else {
            return p1;
        }
    }

    public void calculateValue(double value) {
        double x1 = this.first().getXCoord();
        double x2 = this.second().getXCoord();
        double y1 = this.first().getYCoord();
        double y2 = this.second().getYCoord();
        this.value = y1 + (((y2 - y1) / (x2 - x1)) * (value - x1));
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public int compareTo(Segment other) {
        return Double.compare(this.value, other.value);
    }
}
