package org.maptalks.benchmark.geojson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class GeoJSONBenchmark {

    private static String smallGeoJSON;
    private static String mediumGeoJSON;
    private static String bigGeoJSON;

    static {
        try {
            smallGeoJSON = readSmallGeoJSON();
            mediumGeoJSON = readMediumGeoJSON();
            bigGeoJSON = readBigGeoJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readGzipGeoJSON(String name) throws IOException {
        InputStream is = GeoJSONBenchmark.class.getResourceAsStream(name);
        GZIPInputStream gis = new GZIPInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = gis.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        return baos.toString();
    }

    private static String readSmallGeoJSON() throws IOException {
        String name = "/geojson/small/county_point.geojson.gz";
        return readGzipGeoJSON(name);
    }

    private static String readMediumGeoJSON() throws IOException {
        String name = "/geojson/medium/entry_point.geojson.gz";
        return readGzipGeoJSON(name);
    }

    private static String readBigGeoJSON() throws IOException {
        String name = "/geojson/big/bus_station_point.geojson.gz";
        return readGzipGeoJSON(name);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(GeoJSONBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void benchSmallDataMaptalksGeoJSON() {
        org.maptalks.geojson.json.GeoJSONFactory.create(smallGeoJSON);
    }

    @Benchmark
    public void benchSmallDataWololoGeoJSON() {
        org.wololo.geojson.GeoJSONFactory.create(smallGeoJSON);
    }

    @Benchmark
    public void benchSmallDataOpendatalabGeoJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(smallGeoJSON, org.geojson.GeoJsonObject.class);
    }

    @Benchmark
    public void benchMediumDataMaptalksGeoJSON() {
        org.maptalks.geojson.json.GeoJSONFactory.create(mediumGeoJSON);
    }

    @Benchmark
    public void benchMediumDataWololoGeoJSON() {
        org.wololo.geojson.GeoJSONFactory.create(mediumGeoJSON);
    }

    @Benchmark
    public void benchMediumDataOpendatalabGeoJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(mediumGeoJSON, org.geojson.GeoJsonObject.class);
    }

    @Benchmark
    public void benchBigDataMaptalksGeoJSON() {
        org.maptalks.geojson.json.GeoJSONFactory.create(bigGeoJSON);
    }

    @Benchmark
    public void benchBigDataWololoGeoJSON() {
        org.wololo.geojson.GeoJSONFactory.create(bigGeoJSON);
    }

    @Benchmark
    public void benchBigDataOpendatalabGeoJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(bigGeoJSON, org.geojson.GeoJsonObject.class);
    }

}
