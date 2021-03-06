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
package com.palantir.atlasdb.timelock;

import java.io.Closeable;
import java.util.Set;

import com.palantir.atlasdb.timelock.lock.AsyncResult;
import com.palantir.atlasdb.timelock.paxos.ManagedTimestampService;
import com.palantir.lock.v2.IdentifiedTimeLockRequest;
import com.palantir.lock.v2.LockImmutableTimestampResponse;
import com.palantir.lock.v2.LockRequest;
import com.palantir.lock.v2.LockToken;
import com.palantir.lock.v2.WaitForLocksRequest;

public interface AsyncTimelockService extends ManagedTimestampService, Closeable {

    long currentTimeMillis();

    Set<LockToken> unlock(Set<LockToken> tokens);

    Set<LockToken> refreshLockLeases(Set<LockToken> tokens);

    AsyncResult<Void> waitForLocks(WaitForLocksRequest request);

    AsyncResult<LockToken> lock(LockRequest request);

    long getImmutableTimestamp();

    LockImmutableTimestampResponse lockImmutableTimestamp(IdentifiedTimeLockRequest request);

}
