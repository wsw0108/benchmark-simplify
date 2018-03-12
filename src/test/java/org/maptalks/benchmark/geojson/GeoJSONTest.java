package org.maptalks.benchmark.geojson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoJSONTest {

    private static String testJson;

    @BeforeClass
    public static void beforeClass() throws IOException {
        String name = "/geojson/small/county_point.geojson.gz";
        testJson = GeoJSONBenchmark.readGzipGeoJSON(name);
    }

    @AfterClass
    public static void afterClass() {
    }

    @Test
    public void testMaptalksGeojson4j() throws Exception {
        org.maptalks.geojson.GeoJSON geojson = org.maptalks.geojson.json.GeoJSONFactory.create(testJson);
        System.out.printf(geojson.getType());
    }

    @Test
    public void testWololoGeoJSON() throws Exception {
        org.wololo.geojson.GeoJSON geojson = org.wololo.geojson.GeoJSONFactory.create(testJson);
        System.out.printf(geojson.getType());
    }

    @Test
    public void testOpendatalabGeoJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        org.geojson.GeoJsonObject geojson = mapper.readValue(testJson, org.geojson.GeoJsonObject.class);
        if (geojson instanceof org.geojson.FeatureCollection) {
            org.geojson.FeatureCollection collection = (org.geojson.FeatureCollection) geojson;
            System.out.printf("");
        }
    }

}
