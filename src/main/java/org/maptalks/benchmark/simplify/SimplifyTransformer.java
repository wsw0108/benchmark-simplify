package org.maptalks.benchmark.simplify;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

public class SimplifyTransformer extends GeometryTransformer {
    static PointExtractor<Coordinate> pointExtractor = new PointExtractor<Coordinate>() {
        @Override
        public double getX(Coordinate point) {
            return point.getOrdinate(0);
        }

        @Override
        public double getY(Coordinate point) {
            return point.getOrdinate(1);
        }
    };

    private double tolerance;
    private boolean highestQuality = true;
    private Simplify<Coordinate> simplify = new Simplify<>(new Coordinate[] {}, pointExtractor);

    public SimplifyTransformer(double distanceTolerance) {
        this.tolerance = distanceTolerance;
    }

    public void setHighestQuality(boolean highestQuality) {
        this.highestQuality = highestQuality;
    }

    @Override
    protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent) {
        Coordinate[] points = coords.toCoordinateArray();
        Coordinate[] result = simplify.simplify(points, tolerance, highestQuality);
        return new CoordinateArraySequence(result);
    }
}
