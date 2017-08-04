/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.atlasdb.timelock.benchmarks;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.palantir.atlasdb.config.AtlasDbConfig;
import com.palantir.atlasdb.factory.TransactionManagers;
import com.palantir.atlasdb.transaction.impl.SerializableTransactionManager;

public class BenchmarksResource implements BenchmarksService {

    private final AtlasDbConfig config;
    private final SerializableTransactionManager txnManager;

    public BenchmarksResource(AtlasDbConfig config) {
        this.config = config;
        this.txnManager = TransactionManagers.create(config, ImmutableSet.of(), res -> { }, true);
    }

    @Override
    public Map<String, Object> writeTransaction(int numClients, int numRequestsPerClient) {
        return WriteTransactionBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> contendedWriteTransaction(int numClients, int numRequestsPerClient) {
        return ContendedWriteTransactionBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> readTransaction(int numClients, int numRequestsPerClient) {
        return ReadTransactionBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> kvsWrite(int numClients, int numRequestsPerClient) {
        return KvsWriteBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> kvsCas(int numClients, int numRequestsPerClient) {
        return KvsCasBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> kvsRead(int numClients, int numRequestsPerClient) {
        return KvsReadBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> timestamp(int numClients, int numRequestsPerClient) {
        return TimestampBenchmark.execute(txnManager, numClients, numRequestsPerClient);
    }

    @Override
    public Map<String, Object> jkongTimestamp() {
        Set<String> clientSet = IntStream.rangeClosed(1, 6)
                .boxed()
                .flatMap(i -> Stream.of("l" + i, "h" + i))
                .collect(Collectors.toSet());

        Map<String, Integer> clientToNumClients = Maps.newHashMap();
        Map<String, Integer> clientToTimestampsPerClient = Maps.newHashMap();
        for (String client : clientSet) {
            clientToNumClients.put(client, client.startsWith("h") ? 128 : 8);
            clientToTimestampsPerClient.put(client, 500);
        }

        return new MultiServiceTimestampBenchmark(
                clientToNumClients,
                clientToTimestampsPerClient,
                config).execute();
    }

    @Override
    public Map<String, Object> jkongTimestampAL() {
        return new MultiServiceTimestampBenchmark(
                ImmutableMap.<String, Integer>builder()
                        .put("l1", 8)
                        .put("l2", 8)
                        .put("l3", 8)
                        .put("l4", 8)
                        .put("l5", 8)
                        .put("h1", 8)
                        .put("h2", 8)
                        .put("h3", 8)
                        .put("h4", 8)
                        .put("h5", 8)
                        .build(),
                ImmutableMap.<String, Integer>builder()
                        .put("l1", 500)
                        .put("l2", 500)
                        .put("l3", 500)
                        .put("l4", 500)
                        .put("l5", 500)
                        .put("h1", 500)
                        .put("h2", 500)
                        .put("h3", 500)
                        .put("h4", 500)
                        .put("h5", 500)
                        .build(),
                config).execute();
    }

    @Override
    public Map<String, Object> jkongTimestampAH() {
        return new MultiServiceTimestampBenchmark(
                ImmutableMap.<String, Integer>builder()
                        .put("l1", 128)
                        .put("l2", 128)
                        .put("l3", 128)
                        .put("l4", 128)
                        .put("l5", 128)
                        .put("h1", 128)
                        .put("h2", 128)
                        .put("h3", 128)
                        .put("h4", 128)
                        .put("h5", 128)
                        .build(),
                ImmutableMap.<String, Integer>builder()
                        .put("l1", 500)
                        .put("l2", 500)
                        .put("l3", 500)
                        .put("l4", 500)
                        .put("l5", 500)
                        .put("h1", 500)
                        .put("h2", 500)
                        .put("h3", 500)
                        .put("h4", 500)
                        .put("h5", 500)
                        .build(),
                config).execute();
    }

    @Override
    public Map<String, Object> jkongTimestampByNumbers(
            int numClients,
            int numThreadsPerClient,
            int numRequestsPerClient) {
        Set<String> clients = IntStream.rangeClosed(1, numClients)
                .mapToObj(x -> String.format("h%d", x))
                .collect(Collectors.toSet());
        Map<String, Integer> clientsToThreads = clients.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        unused -> numThreadsPerClient));
        Map<String, Integer> clientsToNumRequests = clients.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        unused -> numRequestsPerClient));
        return new MultiServiceTimestampBenchmark(clientsToThreads, clientsToNumRequests, config).execute();
    }

    @Override
    public Map<String, Object> jkongSimpleTimestampByNumbers(int numClients) {
        return jkongTimestampByNumbers(numClients, 10, 1000);
    }
}