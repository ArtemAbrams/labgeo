package org.example.labgeo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event implements Comparable<Event> {

    private final Point point;
    private final List<Segment> segments;
    private final double value;
    private final int type;

    public Event(Point p, Segment s, int type) {
        this.point = p;
        this.segments = new ArrayList<>(Collections.singletonList(s));
        this.value = p.getXCoord();
        this.type = type;
    }

    public Event(Point p, List<Segment> s, int type) {
        this.point = p;
        this.segments = s;
        this.value = p.getXCoord();
        this.type = type;
    }

    public Point getPoint() {
        return this.point;
    }

    public List<Segment> getSegments() {
        return this.segments;
    }

    public int getType() {
        return this.type;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.getValue(), other.getValue());
    }
}
