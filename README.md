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
