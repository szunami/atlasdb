/*
 * Copyright 2018 Palantir Technologies, Inc. All rights reserved.
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

package com.palantir.atlasdb.containers;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.rules.ExternalResource;

import com.palantir.atlasdb.cassandra.CassandraKeyValueServiceConfig;
import com.palantir.atlasdb.keyvalue.api.KeyValueService;
import com.palantir.atlasdb.keyvalue.cassandra.CassandraKeyValueService;
import com.palantir.atlasdb.keyvalue.cassandra.CassandraKeyValueServiceImpl;
import com.palantir.atlasdb.keyvalue.impl.CloseableResourceManager;
import com.palantir.atlasdb.transaction.api.TransactionManager;

public class CassandraResource extends ExternalResource {
    private final CassandraContainer containerInstance = new CassandraContainer();
    private final CloseableResourceManager closeableResourceManager;
    private final Containers containers;

    public CassandraResource(Class<?> classToSaveLogsFor) {
        containers = new Containers(classToSaveLogsFor).with(containerInstance);
        closeableResourceManager = new CloseableResourceManager(() -> CassandraKeyValueServiceImpl
                .createForTesting(containerInstance.getConfig(), CassandraContainer.LEADER_CONFIG));
    }

    public CassandraResource(Class<?> classToSaveLogsFor, Supplier<KeyValueService> supplier) {
        containers = new Containers(classToSaveLogsFor).with(containerInstance);
        closeableResourceManager = new CloseableResourceManager(supplier);
    }

    @Override
    public void before() throws Throwable {
        containers.before();
    }

    @Override
    public void after() {
        closeableResourceManager.after();
    }

    public CassandraKeyValueService getDefaultKvs() {
        return (CassandraKeyValueService) closeableResourceManager.getKvs();
    }

    public void registerKvs(KeyValueService kvs) {
        closeableResourceManager.registerKvs(kvs);
    }

    public Optional<KeyValueService> getRegisteredKvs() {
        return closeableResourceManager.getRegisteredKvs();
    }

    public void registerTransactionManager(TransactionManager manager) {
        closeableResourceManager.registerTransactionManager(manager);
    }

    public Optional<TransactionManager> getRegisteredTransactionManager() {
        return closeableResourceManager.getRegisteredTransactionManager();
    }

    public CassandraKeyValueServiceConfig getConfig() {
        return containerInstance.getConfig();
    }
}
