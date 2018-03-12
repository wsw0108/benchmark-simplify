# benchmarks
Benchmarks for some java libraries.

## Simplify

### Run

- Run SimplifyBenchmark.main(), OR
- Run `java -jar target\benchmarks.jar SimplifyBenchmark`

### Reference Result

| Benchmark                                                  |  Mode  | Cnt |   Score | Error   | Units |
| :--------------------------------------------------------- | ------ | --- | ------: | :------ | ----- |
| SimplifyBenchmark.benchSimplifyUsingJTS                    | thrpt  | 200 | 36.460  | ± 0.234 | ops/s |
| SimplifyBenchmark.benchSimplifyUsingJTSDontEnsureValid     | thrpt  | 200 | 38.073  | ± 0.151 | ops/s |
| SimplifyBenchmark.benchSimplifyUsingJsPort                 | thrpt  | 200 | 55.067  | ± 0.136 | ops/s |
| SimplifyBenchmark.benchSimplifyUsingJsPortNoHighestQuality | thrpt  | 200 | 345.519 | ± 2.916 | ops/s |

### Benchmark against test fixtures of simplify-js

| Benchmark                                              |  Mode | Cnt |     Score |     Error | Units |
| :----------------------------------------------------- | ----- | --- | --------: | :-------- | ----  |
| JsDataSetBenchmark.simplifyUsingJTS                    | thrpt | 200 |  7211.125 | ± 319.158 | ops/s |
| JsDataSetBenchmark.simplifyUsingJsPort                 | thrpt | 200 | 12957.125 | ±  84.840 | ops/s |
| JsDataSetBenchmark.simplifyUsingJsPortNoHighestQuality | thrpt | 200 | 19633.485 | ± 270.524 | ops/s |

## GeoJSON Deserialize

### Run

- Run GeoJSONBenchmark.main(), OR
- Run `java -jar target\benchmarks.jar GeoJSONBenchmark

### Result

| Benchmark                                         |  Mode | Cnt |  Score | Error   | Units |
| :------------------------------------------------ | ----- | --- | -----: | :------ | ----  |
|GeoJSONBenchmark.benchBigDataMaptalksGeoJSON       | thrpt | 200 |   0.736| ± 0.050 | ops/s |
|GeoJSONBenchmark.benchBigDataOpendatalabGeoJSON    | thrpt | 200 |   2.980| ± 0.017 | ops/s |
|GeoJSONBenchmark.benchBigDataWololoGeoJSON         | thrpt | 200 |   0.530| ± 0.048 | ops/s |
|GeoJSONBenchmark.benchMediumDataMaptalksGeoJSON    | thrpt | 200 |  19.733| ± 0.056 | ops/s |
|GeoJSONBenchmark.benchMediumDataOpendatalabGeoJSON | thrpt | 200 |  38.090| ± 0.354 | ops/s |
|GeoJSONBenchmark.benchMediumDataWololoGeoJSON      | thrpt | 200 |  15.371| ± 0.226 | ops/s |
|GeoJSONBenchmark.benchSmallDataMaptalksGeoJSON     | thrpt | 200 | 152.130| ± 1.033 | ops/s |
|GeoJSONBenchmark.benchSmallDataOpendatalabGeoJSON  | thrpt | 200 | 277.090| ± 1.482 | ops/s |
|GeoJSONBenchmark.benchSmallDataWololoGeoJSON       | thrpt | 200 | 124.473| ± 0.604 | ops/s |
