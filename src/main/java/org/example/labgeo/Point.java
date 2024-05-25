package org.example.labgeo;

public class Point {

    private double xCoord;
    private double yCoord;

    public Point(double x, double y) {
        this.xCoord = x;
        this.yCoord = y;
    }

    public double getXCoord() {
        return this.xCoord;
    }

    public double getYCoord() {
        return this.yCoord;
    }
}
