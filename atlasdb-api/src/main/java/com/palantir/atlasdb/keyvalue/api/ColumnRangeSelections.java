/**
 * Copyright 2015 Palantir Technologies
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
package com.palantir.atlasdb.keyvalue.api;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.palantir.common.persist.Persistable;

public class ColumnRangeSelections implements Serializable {
    public static SizedColumnRangeSelection createPrefixRange(byte[] prefix, int batchSize) {
        byte[] startCol = Preconditions.checkNotNull(prefix).clone();
        byte[] endCol = RangeRequests.createEndNameForPrefixScan(prefix);
        return new SizedColumnRangeSelection(startCol, endCol, batchSize);
    }

    public static SizedColumnRangeSelection createPrefixRange(Persistable persistable, int batchSize) {
        return createPrefixRange(persistable.persistToBytes(), batchSize);
    }
}
