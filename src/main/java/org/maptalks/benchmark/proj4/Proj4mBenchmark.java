package org.maptalks.benchmark.proj4;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.maptalks.proj4lite.PointAdaptor;
import org.maptalks.proj4lite.Proj4Exception;
import org.opengis.feature.simple.SimpleFeature;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Proj4mBenchmark {

    private static List<Geometry> geometryList = new ArrayList<>();

    static {
        URL url = Proj4mBenchmark.class.getResource("/province_region.shp");
        ShapefileDataStore store = new ShapefileDataStore(url);
        store.setCharset(Charset.forName("GBK"));
        geometryList.clear();
        try {
            SimpleFeatureSource source = store.getFeatureSource();
            SimpleFeatureCollection collection = source.getFeatures();
            try (SimpleFeatureIterator iter = collection.features()) {
                while (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    geometryList.add(geometry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(Proj4mBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void benchProj4m() throws org.maptalks.proj4.Proj4Exception {
        CoordinateSequenceFilter filter = new Proj4mCRSFilter("GCJ02", "GCJ02MC");
        List<Geometry> copy = new ArrayList<>();
        for (Geometry geometry : geometryList) {
            copy.add((Geometry) geometry.clone());
        }
        for (Geometry geometry : copy) {
            geometry.apply(filter);
        }
    }

    @Benchmark
    public void benchProj4mLite() throws org.maptalks.proj4lite.Proj4Exception {
        CoordinateSequenceFilter filter = new Proj4mLiteCRSFilter("GCJ02", "GCJ02MC");
        List<Geometry> copy = new ArrayList<>();
        for (Geometry geometry : geometryList) {
            copy.add((Geometry) geometry.clone());
        }
        for (Geometry geometry : copy) {
            geometry.apply(filter);
        }
    }

    private static class Proj4mLiteCRSFilter implements CoordinateSequenceFilter {
        private static PointAdaptor<Coordinate> pointAdaptor = new PointAdaptor<Coordinate>() {
            @Override
            public double getX(Coordinate point) {
                return point.getOrdinate(0);
            }

            @Override
            public double getY(Coordinate point) {
                return point.getOrdinate(1);
            }

            @Override
            public void setX(Coordinate point, double x) {
                point.setOrdinate(0, x);
            }

            @Override
            public void setY(Coordinate point, double y) {
                point.setOrdinate(1, y);
            }
        };
        private org.maptalks.proj4lite.Proj4<Coordinate> proj;

        Proj4mLiteCRSFilter(String src, String dst) throws org.maptalks.proj4lite.Proj4Exception {
            this.proj = new org.maptalks.proj4lite.Proj4<>(src, dst, pointAdaptor);
        }

        @Override
        public void filter(CoordinateSequence seq, int i) {
            Coordinate coord = seq.getCoordinate(i);
            try {
                // transform in place
                proj.forward(coord);
            } catch (Proj4Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isGeometryChanged() {
            return true;
        }
    }

    private static class Proj4mCRSFilter implements CoordinateSequenceFilter {
        private org.maptalks.proj4.Proj4 proj;

        Proj4mCRSFilter(String src, String dst) throws org.maptalks.proj4.Proj4Exception {
            this.proj = new org.maptalks.proj4.Proj4(src, dst);
        }

        @Override
        public void filter(CoordinateSequence seq, int i) {
            double x = seq.getOrdinate(i, Coordinate.X);
            double y = seq.getOrdinate(i, Coordinate.Y);
            try {
                double[] p = proj.forward(new double[] {x, y});
                // change x/y of input coordinate
                seq.setOrdinate(i, Coordinate.X, p[0]);
                seq.setOrdinate(i, Coordinate.Y, p[1]);
            } catch (org.maptalks.proj4.Proj4Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isGeometryChanged() {
            return true;
        }
    }
}
