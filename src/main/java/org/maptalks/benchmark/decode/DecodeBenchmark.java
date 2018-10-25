package org.maptalks.benchmark.decode;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Base64;

public class DecodeBenchmark {
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final WKBReader wkbReaderDecodeWKB = new WKBReader(geometryFactory);
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private static final WKBReader wkbReaderDecodeBase64WKB = new WKBReader(geometryFactory);
    private static final WKTReader wktReader = new WKTReader(geometryFactory);
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JtsModule());
    private static byte[] wkb;
    private static String base64wkb;
    private static String wkt;
    private static String geojson;

    static {
        CoordinateSequence sequence = new PackedCoordinateSequence.Double(
            new double[]{
                1, 2,
                3, 4,
                5, 6,
                7, 8,
                9, 0
            },
            2
        );
        Geometry geometry = geometryFactory.createLineString(sequence);
        WKBWriter wkbWriter = new WKBWriter();
        WKTWriter wktWriter = new WKTWriter();
        wkb = wkbWriter.write(geometry);
        base64wkb = Base64.getEncoder().encodeToString(wkb);
        wkt = wktWriter.write(geometry);
        try {
            geojson = mapper.writeValueAsString(geometry);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(DecodeBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @SuppressWarnings("unused")
    @Benchmark
    public void benchDecodeWKB() throws Exception {
        Geometry g = wkbReaderDecodeWKB.read(wkb);
    }

    @SuppressWarnings("unused")
    @Benchmark
    public void benchDecodeBase64WKB() throws Exception {
        byte[] decode = base64Decoder.decode(base64wkb);
        Geometry g = wkbReaderDecodeBase64WKB.read(decode);
    }

    @SuppressWarnings("unused")
    @Benchmark
    public void benchDecodeWKT() throws Exception {
        Geometry g = wktReader.read(wkt);
    }

    @SuppressWarnings("unused")
    @Benchmark
    public void benchDecodeGeoJSON() throws Exception {
        Geometry g = mapper.readValue(geojson, Geometry.class);
    }
}
