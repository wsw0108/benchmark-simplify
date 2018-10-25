package org.maptalks.benchmark.size;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.maptalks.benchmark.jts.ReadJTSBenchmark;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class EncodedSize {
    private static WKTWriter wktWriter = new WKTWriter();
    private static WKBWriter wkbWriter = new WKBWriter();

    public static void main(String[] args) throws IOException {
        List<String> wktList = new ArrayList<>();
        List<String> wkbBase64List = new ArrayList<>();

        province(wktList, wkbBase64List);

        county(wktList, wkbBase64List);
    }

    private static void province(List<String> wktList, List<String> wkbBase64List) throws IOException {
        wktList.clear();
        wkbBase64List.clear();

        URL url = EncodedSize.class.getResource("/province_region.shp");
        ShapefileDataStore store = new ShapefileDataStore(url);
        store.setCharset(Charset.forName("GBK"));
        SimpleFeatureSource source = store.getFeatureSource();
        SimpleFeatureCollection collection = source.getFeatures();
        try (SimpleFeatureIterator iterator = collection.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                String wkt = wktWriter.write(geometry);
                wktList.add(wkt);
                byte[] bytes = wkbWriter.write(geometry);
                String wkbBase64 = Base64.getEncoder().encodeToString(bytes);
                wkbBase64List.add(wkbBase64);
            }
        }

        String title = String.format("province(type:MultiPolygon, size:%d)", wktList.size());
        result(title, wktList, wkbBase64List);
    }

    private static void county(List<String> wktList, List<String> wkbBase64List) throws IOException {
        wktList.clear();
        wkbBase64List.clear();

        String json = ReadJTSBenchmark.genGeometryCollectionJSON();
        List<Geometry> geometries = ReadJTSBenchmark.parseGeometryCollection(json);
        for (Geometry geometry : geometries) {
            String wkt = wktWriter.write(geometry);
            wktList.add(wkt);
            byte[] bytes = wkbWriter.write(geometry);
            String wkbBase64 = Base64.getEncoder().encodeToString(bytes);
            wkbBase64List.add(wkbBase64);
        }

        String title = String.format("county(type:Point, size:%d)", wktList.size());
        result(title, wktList, wkbBase64List);
    }

    private static void result(String title, List<String> wktList, List<String> wkbBase64List) {
        Double avgWktLength = wktList
            .stream()
            .collect(Collectors.averagingInt(String::length));
        Double avgWkbBase64Length = wkbBase64List
            .stream()
            .collect(Collectors.averagingInt(String::length));
        System.out.println(title);
        System.out.println("Average WKT length:\t\t\t" + avgWktLength);
        System.out.println("Average Base64-WKB length:\t" + avgWkbBase64Length);
    }
}
