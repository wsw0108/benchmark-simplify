package org.maptalks.benchmark.simplify;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

public class SimpleBenchmark {
    private static PointExtractor<Coordinate> pointExtractor = new PointExtractor<Coordinate>() {
        @Override
        public double getX(Coordinate point) {
            return point.getOrdinate(0);
        }

        @Override
        public double getY(Coordinate point) {
            return point.getOrdinate(1);
        }
    };
    private static Simplify<Coordinate> simplify = new Simplify<>(new Coordinate[] {}, pointExtractor);
    private static double TOLERANCE = 3;
    private static double TOLERANCE2 = TOLERANCE * TOLERANCE;
    private List<Geometry> geometryList = new ArrayList<>();
    private List<Geometry> jsPortResult = new ArrayList<>();
    private List<Geometry> jsPortResult2 = new ArrayList<>();
    private List<Geometry> jtsResult = new ArrayList<>();
    private List<Geometry> jtsResult2 = new ArrayList<>();

    @Setup
    public void setup() throws IOException {
        URL url = getClass().getResource("/province_region.shp");
        ShapefileDataStore store = new ShapefileDataStore(url);
        store.setCharset(Charset.forName("GBK"));
        SimpleFeatureSource source = store.getFeatureSource();
        SimpleFeatureCollection collection = source.getFeatures();
        try (SimpleFeatureIterator iter = collection.features()) {
            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                geometryList.add(geometry);
            }
        }
    }

    @TearDown
    public void tearDown() {
        jsPortResult.clear();
        jtsResult.clear();
        jsPortResult = null;
        jtsResult = null;
        jsPortResult2.clear();
        jtsResult2.clear();
        jsPortResult2 = null;
        jtsResult2 = null;
    }

    @Benchmark
    public void testSimplifyUsingJsPort() {
        SimplifyTransformer transformer = new SimplifyTransformer(TOLERANCE);
        transformer.setHighestQuality(true);
        for (Geometry geometry : geometryList) {
            jsPortResult.add(transformer.transform(geometry));
        }
    }

    @Benchmark
    public void testSimplifyUsingJsPortNoHigestQuality() {
        SimplifyTransformer transformer = new SimplifyTransformer(TOLERANCE);
        transformer.setHighestQuality(false);
        for (Geometry geometry : geometryList) {
            jsPortResult2.add(transformer.transform(geometry));
        }
    }

    @Benchmark
    public void testSimplifyUsingJTS() {
        for (Geometry geometry : geometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(TOLERANCE2);
            tss.setEnsureValid(true);
            jtsResult.add(tss.getResultGeometry());
        }
    }

    @Benchmark
    public void testSimplifyUsingJTSDontEnsureValid() {
        for (Geometry geometry : geometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(TOLERANCE2);
            tss.setEnsureValid(false);
            jtsResult2.add(tss.getResultGeometry());
        }
    }

    private class SimplifyTransformer extends GeometryTransformer {
        private double tolerance;
        private boolean highestQuality = true;

        SimplifyTransformer(double tolerance) {
            this.tolerance = tolerance;
        }

        void setHighestQuality(boolean highestQuality) {
            this.highestQuality = highestQuality;
        }

        @Override
        protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent) {
            Coordinate[] points = coords.toCoordinateArray();
            Coordinate[] result = simplify.simplify(points, tolerance, highestQuality);
            return new CoordinateArraySequence(result);
        }
    }
}
