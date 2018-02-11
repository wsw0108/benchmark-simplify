package org.maptalks.benchmark.simplify;

import static org.maptalks.benchmark.simplify.SimplifyTransformer.pointExtractor;

import com.alibaba.fastjson.JSON;
import com.goebl.simplify.Simplify;
import com.vividsolutions.jts.geom.Coordinate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JsDataSetBenchmark {
    private static double distanceTolerance = 1.0;


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(JsDataSetBenchmark.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(10)
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void simplifyUsingJTS(BenchmarkState state) {
        DouglasPeuckerLineSimplifier simplifier = new DouglasPeuckerLineSimplifier(state.pointArray);
        simplifier.setDistanceTolerance(distanceTolerance);
        simplifier.simplify();
    }

    @Benchmark
    public void simplifyUsingJsPort(BenchmarkState state) {
        Simplify<Coordinate> simplify = new Simplify<>(new Coordinate[] {}, pointExtractor);
        simplify.simplify(state.pointArray, distanceTolerance, true);
    }

    @Benchmark
    public void simplifyUsingJsPortNoHighestQuality(BenchmarkState state) {
        Simplify<Coordinate> simplify = new Simplify<>(new Coordinate[] {}, pointExtractor);
        simplify.simplify(state.pointArray, distanceTolerance, false);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        Coordinate[] pointArray;

        @Setup
        public void setup() throws IOException, URISyntaxException {
            InputStream is = JsDataSetBenchmark.class.getResourceAsStream("/simplify-js-fixtures/1k.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n;
            byte[] buf = new byte[8192];
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, n);
            }
            baos.flush();
            byte[] bytes = baos.toByteArray();
            String text = new String(bytes);
            List<Coordinate> points = JSON.parseArray(text, Coordinate.class);
            pointArray = points.toArray(new Coordinate[] {});
        }

        @TearDown
        public void tearDown() {
        }
    }
}
