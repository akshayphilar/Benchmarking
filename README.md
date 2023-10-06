# Benchmark Server

## Compression Benchmarks

### Plain text response

```shell
wrk -t1 -c100 -d30s --latency http://localhost:8080/compression
Running 30s test @ http://localhost:8080/compression
  1 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    57.35ms    2.60ms 132.21ms   95.56%
    Req/Sec     1.75k    76.38     1.82k    95.00%
  Latency Distribution
     50%   57.06ms
     75%   57.69ms
     90%   58.37ms
     99%   64.02ms
  52259 requests in 30.00s, 53.54GB read
Requests/sec:   1741.75
Transfer/sec:      1.78GB

```


### Gzip Compression

```shell
wrk -t1 -c100 -d30s --latency http://localhost:8080/compression?algorithm=gzip
Running 30s test @ http://localhost:8080/compression?algorithm=gzip
  1 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.10s   143.38ms   1.68s    94.78%
    Req/Sec    89.04      3.29   101.00     88.33%
  Latency Distribution
     50%    1.12s 
     75%    1.12s 
     90%    1.13s 
     99%    1.39s 
  2671 requests in 30.03s, 474.24MB read
  Socket errors: connect 0, read 0, write 0, timeout 7
Requests/sec:     88.96
Transfer/sec:     15.79MB

```


### Brotli Compression

```shell
wrk -t1 -c100 -d30s --latency http://localhost:8080/compression?algorithm=brotli
Running 30s test @ http://localhost:8080/compression?algorithm=brotli
  1 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   188.30ms    9.50ms 376.49ms   99.15%
    Req/Sec   531.93     10.37   555.00     68.33%
  Latency Distribution
     50%  188.38ms
     75%  190.43ms
     90%  192.40ms
     99%  196.63ms
  15884 requests in 30.01s, 3.07GB read
Requests/sec:    529.28
Transfer/sec:    104.70MB

```