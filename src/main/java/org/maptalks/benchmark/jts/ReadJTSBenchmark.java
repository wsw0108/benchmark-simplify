package org.maptalks.benchmark.jts;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.maptalks.benchmark.geojson.GeoJSONBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class ReadJTSBenchmark {

    private static String geometryCollectionJSON;
    private static org.wololo.jts2geojson.GeoJSONReader wololoJTSReader;
    private static ObjectMapper jacksonDatatypeMapper;
    private static org.locationtech.jts.io.geojson.GeoJsonReader locationTechReader;

    static {
        try {
            geometryCollectionJSON = genGeometryCollectionJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }

        wololoJTSReader = new org.wololo.jts2geojson.GeoJSONReader();

        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        locationTechReader = new org.locationtech.jts.io.geojson.GeoJsonReader(factory);

        jacksonDatatypeMapper = new ObjectMapper();
        jacksonDatatypeMapper.registerModule(new com.bedatadriven.jackson.datatype.jts.JtsModule());
    }

    static String genGeometryCollectionJSON() throws IOException {
        String name = "/geojson/medium/entry_point.geojson.gz";
        String text = GeoJSONBenchmark.readGzipGeoJSON(name);
        ObjectMapper mapper = new ObjectMapper();
        org.geojson.FeatureCollection collection = mapper.readValue(text, org.geojson.FeatureCollection.class);
        org.geojson.GeometryCollection geometries = new org.geojson.GeometryCollection();
        for (org.geojson.Feature feature : collection.getFeatures()) {
            geometries.add(feature.getGeometry());
        }
        return mapper.writeValueAsString(geometries);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ReadJTSBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void benchMaptalksGeoJSON() {
        org.maptalks.geojson.jts.GeoJSONReader.read(geometryCollectionJSON);
    }

    @Benchmark
    public void benchWololoGeoJSON() {
        wololoJTSReader.read(geometryCollectionJSON);
    }

    @Benchmark
    public void benchLocationTechGeoJSON() throws org.locationtech.jts.io.ParseException {
        locationTechReader.read(geometryCollectionJSON);
    }

    @Benchmark
    public void benchJacksonDatatypeJTS() throws IOException {
        jacksonDatatypeMapper.readValue(geometryCollectionJSON, com.vividsolutions.jts.geom.Geometry.class);
    }

}
