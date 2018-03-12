package org.maptalks.benchmark.jts;

import static org.maptalks.benchmark.geojson.GeoJSONBenchmark.readGzipGeoJSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.maptalks.geojson.json.GeoJSONFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class DeserializeFeatureBenchmark {

    private static String smallGeoJSON;
    private static String mediumGeoJSON;
    private static String smallFeaturesArrayJSON;
    private static String mediumFeaturesArrayJSON;
    private static ObjectMapper mapper;

    static {
        try {
            smallGeoJSON = readSmallGeoJSON();
            mediumGeoJSON = readMediumGeoJSON();
            smallFeaturesArrayJSON = genFeaturesArrayJSON(smallGeoJSON);
            mediumFeaturesArrayJSON = genFeaturesArrayJSON(mediumGeoJSON);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapper = new ObjectMapper();
        mapper.registerModule(new com.bedatadriven.jackson.datatype.jts.JtsModule());
    }

    private static String readSmallGeoJSON() throws IOException {
        String name = "/geojson/small/county_point.geojson.gz";
        return readGzipGeoJSON(name);
    }

    private static String readMediumGeoJSON() throws IOException {
        String name = "/geojson/medium/entry_point.geojson.gz";
        return readGzipGeoJSON(name);
    }

    private static Feature feature2feature(org.maptalks.geojson.Feature maptalks) {
        Feature feature = new Feature();
        feature.setId(maptalks.getId());
        feature.setProperties(maptalks.getProperties());
        feature.setGeometry(org.maptalks.geojson.jts.GeoJSONReader.read(maptalks.getGeometry()));
        return feature;
    }

    private static String genFeaturesArrayJSON(String input) {
        org.maptalks.geojson.GeoJSON geojson = org.maptalks.geojson.json.GeoJSONFactory.create(input);
        org.maptalks.geojson.FeatureCollection collection = (org.maptalks.geojson.FeatureCollection) geojson;
        return com.alibaba.fastjson.JSON.toJSONString(collection.getFeatures());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(DeserializeFeatureBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void benchSmallDataMaptalksGeoJSON() {
        FeatureCollection collection = new FeatureCollection();
        org.maptalks.geojson.Feature[] features = GeoJSONFactory.createFeatureArray(smallFeaturesArrayJSON);
        List<Feature> newFeatures = new ArrayList<>();
        for (org.maptalks.geojson.Feature feature : features) {
            newFeatures.add(feature2feature(feature));
        }
        collection.setFeatures(newFeatures);
    }

    @SuppressWarnings("unused")
    @Benchmark
    public void benchSmallDataJacksonDataJTS() throws IOException {
        FeatureCollection collection = mapper.readValue(smallGeoJSON, FeatureCollection.class);
    }

    @Benchmark
    public void benchMediumDataMaptalksGeoJSON() {
        FeatureCollection collection = new FeatureCollection();
        org.maptalks.geojson.Feature[] features = GeoJSONFactory.createFeatureArray(mediumFeaturesArrayJSON);
        List<Feature> newFeatures = new ArrayList<>();
        for (org.maptalks.geojson.Feature feature : features) {
            newFeatures.add(feature2feature(feature));
        }
        collection.setFeatures(newFeatures);
    }

    @SuppressWarnings("unused")
    @Benchmark
    public void benchMediumDataJacksonDataJTS() throws IOException {
        FeatureCollection collection = mapper.readValue(mediumGeoJSON, FeatureCollection.class);
    }

}
