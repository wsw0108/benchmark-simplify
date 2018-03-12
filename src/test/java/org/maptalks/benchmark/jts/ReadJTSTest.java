package org.maptalks.benchmark.jts;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReadJTSTest {

    private static String geometryCollectionJSON;
    private static org.wololo.jts2geojson.GeoJSONReader wololoJTSReader;
    private static ObjectMapper jacksonDatatypeMapper;
    private static org.locationtech.jts.io.geojson.GeoJsonReader locationTechReader;

    @BeforeClass
    public static void beforeClass() throws IOException {
        geometryCollectionJSON = ReadJTSBenchmark.genGeometryCollectionJSON();

        wololoJTSReader = new org.wololo.jts2geojson.GeoJSONReader();

        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        locationTechReader = new org.locationtech.jts.io.geojson.GeoJsonReader(factory);

        jacksonDatatypeMapper = new ObjectMapper();
        jacksonDatatypeMapper.registerModule(new com.bedatadriven.jackson.datatype.jts.JtsModule());
    }

    @Test
    public void testMaptalksGeoJSON() {
        com.vividsolutions.jts.geom.Geometry geometry = org.maptalks.geojson.jts.GeoJSONReader.read(geometryCollectionJSON);
        System.out.printf(geometry.getGeometryType());
    }

    @Test
    public void testWololoGeoJSON() {
        com.vividsolutions.jts.geom.Geometry geometry = wololoJTSReader.read(geometryCollectionJSON);
        System.out.printf(geometry.getGeometryType());
    }

    @Test
    public void testLocationTechGeoJSON() throws org.locationtech.jts.io.ParseException {
        org.locationtech.jts.geom.Geometry geometry = locationTechReader.read(geometryCollectionJSON);
        System.out.printf(geometry.getGeometryType());
    }

    @Test
    public void testJacksonDatatypeJTS() throws IOException {
        com.vividsolutions.jts.geom.Geometry geometry = jacksonDatatypeMapper.readValue(geometryCollectionJSON, com.vividsolutions.jts.geom.Geometry.class);
        System.out.printf(geometry.getGeometryType());
    }

}
