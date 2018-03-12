package org.maptalks.benchmark.jts;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.maptalks.benchmark.geojson.GeoJSONBenchmark;

public class FeatureTest {

    private static String json;
    private static ObjectMapper mapper;

    static {
        try {
            readJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapper = new ObjectMapper();
        mapper.registerModule(new com.bedatadriven.jackson.datatype.jts.JtsModule());
    }

    private static void readJSON() throws IOException {
        String name = "/geojson/small/county_point.geojson.gz";
        json = GeoJSONBenchmark.readGzipGeoJSON(name);
    }

    @Test
    public void testFeatureDeserialize() throws IOException {
        FeatureCollection collection = mapper.readValue(json, FeatureCollection.class);
        Assert.assertNotNull(collection);
        Assert.assertNotNull(collection.getFeatures());
        Assert.assertEquals(2862, collection.getFeatures().size());
    }

}
