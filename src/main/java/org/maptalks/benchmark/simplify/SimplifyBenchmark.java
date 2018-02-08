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
import java.util.concurrent.atomic.AtomicReference;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class SimplifyBenchmark {
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
    private static double TOLERANCE = 3;
    private static double TOLERANCE2 = TOLERANCE * TOLERANCE;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(SimplifyBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void testSimplifyUsingJsPort(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        SimplifyTransformer transformer = new SimplifyTransformer(TOLERANCE);
        transformer.setHighestQuality(true);
        for (Geometry geometry : state.geometryList) {
            result.get().add(transformer.transform(geometry));
        }
    }

    @Benchmark
    public void testSimplifyUsingJsPortNoHighestQuality(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        SimplifyTransformer transformer = new SimplifyTransformer(TOLERANCE);
        transformer.setHighestQuality(false);
        for (Geometry geometry : state.geometryList) {
            result.get().add(transformer.transform(geometry));
        }
    }

    @Benchmark
    public void testSimplifyUsingJTS(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        for (Geometry geometry : state.geometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(TOLERANCE2);
            tss.setEnsureValid(true);
            result.get().add(tss.getResultGeometry());
        }
    }

    @Benchmark
    public void testSimplifyUsingJTSDontEnsureValid(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        for (Geometry geometry : state.geometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(TOLERANCE2);
            tss.setEnsureValid(false);
            result.get().add(tss.getResultGeometry());
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile List<Geometry> geometryList = new ArrayList<>();

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
        }
    }

    private class SimplifyTransformer extends GeometryTransformer {
        private double tolerance;
        private boolean highestQuality = true;
        private Simplify<Coordinate> simplify = new Simplify<>(new Coordinate[] {}, pointExtractor);

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
