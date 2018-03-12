package org.maptalks.benchmark.simplify;

import static org.maptalks.benchmark.simplify.SimplifyParameters.distanceTolerance;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class SimplifyTest {
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 800;
    private static List<Geometry> normalizedGeometryList = new ArrayList<>();

    private static DrawContext createDrawContext() {
        return new DrawContext(WIDTH, HEIGHT);
    }

    private static ShapeFilter createShapeFilter() {
        return new ShapeFilter(WIDTH, HEIGHT);
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        URL url = SimplifyTest.class.getResource("/province_region.shp");
        ShapefileDataStore store = new ShapefileDataStore(url);
        store.setCharset(Charset.forName("GBK"));
        SimpleFeatureSource source = store.getFeatureSource();
        SimpleFeatureCollection collection = source.getFeatures();
        ReferencedEnvelope envelope = collection.getBounds();
        double minx = envelope.getMinX();
        double miny = envelope.getMinY();
        double maxx = envelope.getMaxX();
        double maxy = envelope.getMaxY();
        NormalizeTransformer normalizer = new NormalizeTransformer(minx, miny, maxx, maxy);
        try (SimpleFeatureIterator iter = collection.features()) {
            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                normalizedGeometryList.add(normalizer.transform(geometry));
            }
        }
    }

    @AfterClass
    public static void afterClass() {
    }

    @Test
    public void testSimplifyNoSimplify() throws IOException {
        DrawContext context = createDrawContext();
        ShapeFilter filter = createShapeFilter();
        for (Geometry geometry : normalizedGeometryList) {
            geometry.apply(filter);
            context.draw(filter.getShape());
        }
        String file = Thread.currentThread().getStackTrace()[1].getMethodName();
        context.write(file + ".png");
    }

    @Test
    public void testSimplifyUsingJsPort() throws IOException {
        DrawContext context = createDrawContext();
        ShapeFilter filter = createShapeFilter();
        SimplifyTransformer simplifier = new SimplifyTransformer(distanceTolerance);
        simplifier.setHighestQuality(true);
        for (Geometry geometry : normalizedGeometryList) {
            Geometry simplified = simplifier.transform(geometry);
            simplified.apply(filter);
            context.draw(filter.getShape());
        }
        String file = Thread.currentThread().getStackTrace()[1].getMethodName();
        context.write(file + ".png");
    }

    @Test
    public void testSimplifyUsingJsPortNoHighestQuality() throws IOException {
        DrawContext context = createDrawContext();
        ShapeFilter filter = createShapeFilter();
        SimplifyTransformer simplifier = new SimplifyTransformer(distanceTolerance);
        simplifier.setHighestQuality(false);
        for (Geometry geometry : normalizedGeometryList) {
            Geometry simplified = simplifier.transform(geometry);
            simplified.apply(filter);
            context.draw(filter.getShape());
        }
        String file = Thread.currentThread().getStackTrace()[1].getMethodName();
        context.write(file + ".png");
    }

    @Test
    public void testSimplifyUsingJTS() throws IOException {
        DrawContext context = createDrawContext();
        ShapeFilter filter = createShapeFilter();
        for (Geometry geometry : normalizedGeometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(distanceTolerance);
            tss.setEnsureValid(true);
            Geometry simplified = tss.getResultGeometry();
            simplified.apply(filter);
            context.draw(filter.getShape());
        }
        String file = Thread.currentThread().getStackTrace()[1].getMethodName();
        context.write(file + ".png");
    }

    @Test
    public void testSimplifyUsingJTSDontEnsureValid() throws IOException {
        DrawContext context = createDrawContext();
        ShapeFilter filter = createShapeFilter();
        for (Geometry geometry : normalizedGeometryList) {
            DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geometry);
            tss.setDistanceTolerance(distanceTolerance);
            tss.setEnsureValid(false);
            Geometry simplified = tss.getResultGeometry();
            simplified.apply(filter);
            context.draw(filter.getShape());
        }
        String file = Thread.currentThread().getStackTrace()[1].getMethodName();
        context.write(file + ".png");
    }

    private static class DrawContext {
        BufferedImage image;
        Graphics2D graphics;

        DrawContext(int width, int height) {
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            this.graphics = image.createGraphics();
        }

        void draw(Shape shape) {
            graphics.draw(shape);
        }

        void write(String file) throws IOException {
            write(new File(file));
        }

        void write(File file) throws IOException {
            ImageIO.write(image, "png", file);
        }
    }

    private static class NormalizeTransformer extends GeometryTransformer {
        private double minx;
        private double miny;
        private double maxx;
        private double maxy;
        private double width;
        private double height;

        NormalizeTransformer(double minx, double miny, double maxx, double maxy) {
            this.minx = projectX(minx);
            this.miny = projectY(miny);
            this.maxx = projectX(maxx);
            this.maxy = projectY(maxy);
            this.width = this.maxx - this.minx;
            this.height = this.maxy - this.miny;
        }

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
                x = (x - minx) / width;
                y = (y - miny) / height;
                output.setOrdinate(i, CoordinateSequence.X, x);
                output.setOrdinate(i, CoordinateSequence.Y, y);
            }
            return output;
        }
    }

    private static class ShapeFilter implements CoordinateSequenceFilter {
        private int width;
        private int height;
        private Path2D path;

        ShapeFilter(int width, int height) {
            this.width = width;
            this.height = height;
            this.path = new Path2D.Double();
        }

        Shape getShape() {
            return path;
        }

        @Override
        public void filter(CoordinateSequence seq, int i) {
            double x = seq.getX(i) * width;
            double y = seq.getY(i) * height;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isGeometryChanged() {
            return false;
        }
    }
}
