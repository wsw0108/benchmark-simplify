# benchmarks
Benchmarks for some java libraries.

## Simplify

### Run

- Run SimpleBenchmark.main(), OR
- Run `java -jar target\benchmarks.jar SimpleBenchmark`

### Reference Result

| Benchmark                                                |  Mode  | Cnt |   Score | Error   | Units |
| -------------------------------------------------------- | ------ | --- | ------: | ------: | ----- |
| SimpleBenchmark.testSimplifyUsingJTS                     | thrpt  | 10  | 163.950 | ± 9.065 | ops/s |
| SimpleBenchmark.testSimplifyUsingJTSDontEnsureValid      | thrpt  | 10  | 170.157 | ± 3.090 | ops/s |
| SimpleBenchmark.testSimplifyUsingJsPort                  | thrpt  | 10  | 128.122 | ± 3.705 | ops/s |
| SimpleBenchmark.testSimplifyUsingJsPortNoHighestQuality  | thrpt  | 10  | 377.431 | ± 10.792| ops/s |

