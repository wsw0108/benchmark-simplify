package org.maptalks.benchmark.simplify;

import static org.maptalks.benchmark.simplify.SimplifyParameters.distanceTolerance;

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
    public void benchSimplifyUsingJsPort(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        SimplifyTransformer simplifier = new SimplifyTransformer(distanceTolerance);
        simplifier.setHighestQuality(true);
        for (Geometry geometry : state.geometryList) {
            result.get().add(simplifier.transform(geometry));
        }
    }

    @Benchmark
    public void benchSimplifyUsingJsPortNoHighestQuality(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        SimplifyTransformer simplifier = new SimplifyTransformer(distanceTolerance);
        simplifier.setHighestQuality(false);
        for (Geometry geometry : state.geometryList) {
            result.get().add(simplifier.transform(geometry));
        }
    }

    @Benchmark
    public void benchSimplifyUsingJTS(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        for (Geometry geometry : state.geometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(distanceTolerance);
            tss.setEnsureValid(true);
            result.get().add(tss.getResultGeometry());
        }
    }

    @Benchmark
    public void benchSimplifyUsingJTSDontEnsureValid(BenchmarkState state) {
        AtomicReference<List<Geometry>> result = new AtomicReference<>(new ArrayList<>());
        for (Geometry geometry : state.geometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(distanceTolerance);
            tss.setEnsureValid(false);
            result.get().add(tss.getResultGeometry());
        }
    }

    private static class NormalizeTransformer extends GeometryTransformer {
        private static double projectX(double x) {
            return x / 360 + 0.5;
        }

        private static double projectY(double y) {
            double sin = Math.sin(y * Math.PI / 180);
            double y2 = 0.5 - 0.25 * Math.log((1 + sin) / (1 - sin)) / Math.PI;
            return y2 < 0 ? 0 : y2 > 1 ? 1 : y2;
        }

        @Override
        protected CoordinateSequence transformCoordinates(CoordinateSequence input, Geometry parent) {
            int size = input.size();
            // int dim = 2;
            // CoordinateSequence output = new PackedCoordinateSequence.Double(size, dim);
            CoordinateSequence output = new CoordinateArraySequence(size);
            for (int i = 0; i < size; i++) {
                double x = input.getX(i);
                double y = input.getY(i);
                x = projectX(x);
                y = projectY(y);
                output.setOrdinate(i, CoordinateSequence.X, x);
                output.setOrdinate(i, CoordinateSequence.Y, y);
            }
            return output;
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
            NormalizeTransformer normalizer = new NormalizeTransformer();
            try (SimpleFeatureIterator iter = collection.features()) {
                while (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    geometryList.add(normalizer.transform(geometry));
                }
            }
        }

        @TearDown
        public void tearDown() {
        }
    }

}
