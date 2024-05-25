package org.example.labgeo;

import java.util.*;

public class BentleyOttmann {

    private final Queue<Event> Q;
    private final NavigableSet<Segment> T;
    private final List<Point> X;

    public BentleyOttmann(List<Segment> inputData) {
        this.Q = new PriorityQueue<>(new EventComparator());
        this.T = new TreeSet<>(new SegmentComparator());
        this.X = new ArrayList<>();
        for(Segment s : inputData) {
            this.Q.add(new Event(s.first(), s, 0));
            this.Q.add(new Event(s.second(), s, 1));
        }
    }

    public void findIntersections() {
        while(!this.Q.isEmpty()) {
            Event e = this.Q.poll();
            double l = e.getValue();
            switch(e.getType()) {
                case 0:
                    for(Segment s : e.getSegments()) {
                        this.recalculate(l);
                        this.T.add(s);
                        if(this.T.lower(s) != null) {
                            Segment r = this.T.lower(s);
                            this.reportIntersection(r, s, l);
                        }
                        if(this.T.higher(s) != null) {
                            Segment t = this.T.higher(s);
                            this.reportIntersection(t, s, l);
                        }
                        if(this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            this.removeFuture(r, t);
                        }
                    }
                    break;
                case 1:
                    for(Segment s : e.getSegments()) {
                        if(this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            this.reportIntersection(r, t, l);
                        }
                        this.T.remove(s);
                    }
                    break;
                case 2:
                    Segment s1 = e.getSegments().get(0);
                    Segment s2 = e.getSegments().get(1);
                    this.swap(s1, s2);
                    if(s1.getValue() < s2.getValue()) {
                        if(this.T.higher(s1) != null) {
                            Segment t = this.T.higher(s1);
                            this.reportIntersection(t, s1, l);
                            this.removeFuture(t, s2);
                        }
                        if(this.T.lower(s2) != null) {
                            Segment r = this.T.lower(s2);
                            this.reportIntersection(r, s2, l);
                            this.removeFuture(r, s1);
                        }
                    } else {
                        if(this.T.higher(s2) != null) {
                            Segment t = this.T.higher(s2);
                            this.reportIntersection(t, s2, l);
                            this.removeFuture(t, s1);
                        }
                        if(this.T.lower(s1) != null) {
                            Segment r = this.T.lower(s1);
                            this.reportIntersection(r, s1, l);
                            this.removeFuture(r, s2);
                        }
                    }
                    this.X.add(e.getPoint());
                    break;
            }
        }
    }

    private void reportIntersection(Segment s1, Segment s2, double l) {
        double x1 = s1.first().getXCoord();
        double y1 = s1.first().getYCoord();
        double x2 = s1.second().getXCoord();
        double y2 = s1.second().getYCoord();
        double x3 = s2.first().getXCoord();
        double y3 = s2.first().getYCoord();
        double x4 = s2.second().getXCoord();
        double y4 = s2.second().getYCoord();

        double denom = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (denom != 0) {
            double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / denom;
            double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / denom;
            if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
                double xC = x1 + t * (x2 - x1);
                double yC = y1 + t * (y2 - y1);
                if (xC > l) {
                    Event intersectionEvent = new Event(new Point(xC, yC), Arrays.asList(s1, s2), 2);
                    if (!this.Q.contains(intersectionEvent)) {
                        this.Q.add(intersectionEvent);
                    }
                }
            }
        }
    }

    private void removeFuture(Segment s1, Segment s2) {
        this.Q.removeIf(e -> e.getType() == 2 && ((e.getSegments().get(0) == s1 && e.getSegments().get(1) == s2) || (e.getSegments().get(0) == s2 && e.getSegments().get(1) == s1)));
    }

    private void swap(Segment s1, Segment s2) {
        this.T.remove(s1);
        this.T.remove(s2);
        double value = s1.getValue();
        s1.setValue(s2.getValue());
        s2.setValue(value);
        this.T.add(s1);
        this.T.add(s2);
    }

    private void recalculate(double L) {

        for (Segment segment : this.T) {
            segment.calculateValue(L);
        }
    }

    public List<Point> getIntersections() {
        return this.X;
    }

    private static class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            return Double.compare(e1.getValue(), e2.getValue());
        }
    }

    private static class SegmentComparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            return Double.compare(s2.getValue(), s1.getValue());
        }
    }
}