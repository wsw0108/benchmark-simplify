package org.maptalks.benchmark.simplify;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.LineSegment;

// jts/simplify/DouglasPeuckerLineSimplifier.java
public class DouglasPeuckerLineSimplifier {
    private Coordinate[] pts;
    private boolean[] usePt;
    private double distanceTolerance;
    private LineSegment seg = new LineSegment();

    public DouglasPeuckerLineSimplifier(Coordinate[] pts) {
        this.pts = pts;
    }

    public static Coordinate[] simplify(Coordinate[] pts, double distanceTolerance) {
        DouglasPeuckerLineSimplifier simp = new DouglasPeuckerLineSimplifier(pts);
        simp.setDistanceTolerance(distanceTolerance);
        return simp.simplify();
    }

    /**
     * Sets the distance tolerance for the simplification.
     * All vertices in the simplified linestring will be within this
     * distance of the original linestring.
     *
     * @param distanceTolerance the approximation tolerance to use
     */
    public void setDistanceTolerance(double distanceTolerance) {
        this.distanceTolerance = distanceTolerance;
    }

    public Coordinate[] simplify() {
        usePt = new boolean[pts.length];
        for (int i = 0; i < pts.length; i++) {
            usePt[i] = true;
        }
        simplifySection(0, pts.length - 1);
        CoordinateList coordList = new CoordinateList();
        for (int i = 0; i < pts.length; i++) {
            if (usePt[i]) {
                coordList.add(new Coordinate(pts[i]));
            }
        }
        return coordList.toCoordinateArray();
    }

    private void simplifySection(int i, int j) {
        if ((i + 1) == j) {
            return;
        }
        seg.p0 = pts[i];
        seg.p1 = pts[j];
        double maxDistance = -1.0;
        int maxIndex = i;
        for (int k = i + 1; k < j; k++) {
            double distance = seg.distance(pts[k]);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxIndex = k;
            }
        }
        if (maxDistance <= distanceTolerance) {
            for (int k = i + 1; k < j; k++) {
                usePt[k] = false;
            }
        } else {
            simplifySection(i, maxIndex);
            simplifySection(maxIndex, j);
        }
    }

}
