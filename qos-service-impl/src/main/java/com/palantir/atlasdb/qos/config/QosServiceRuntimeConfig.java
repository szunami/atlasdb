/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.atlasdb.qos.config;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = ImmutableQosServiceRuntimeConfig.class)
@JsonSerialize(as = ImmutableQosServiceRuntimeConfig.class)
@Value.Immutable
public abstract class QosServiceRuntimeConfig {
    @JsonProperty("client-limits")
    public abstract Map<String, QosClientLimitsConfig> clientLimits();

    @JsonProperty("qos-cassandra-metrics")
    @Value.Default
    public QosCassandraMetricsRuntimeConfig qosCassandraMetricsConfig() {
        return ImmutableQosCassandraMetricsRuntimeConfig.builder().build();
    }
}
