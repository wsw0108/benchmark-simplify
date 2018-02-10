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

