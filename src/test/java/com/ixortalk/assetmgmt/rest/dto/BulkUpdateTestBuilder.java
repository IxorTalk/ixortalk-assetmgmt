/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.assetmgmt.rest.dto;

import java.util.Map;
import java.util.Set;

import com.ixortalk.assetmgmt.domain.AssetId;
import com.ixortalk.test.builder.ReflectionInstanceTestBuilder;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class BulkUpdateTestBuilder extends ReflectionInstanceTestBuilder<BulkUpdate> {

    private Set<AssetId> assetIds = newHashSet();
    private Map<String, Object> properties = newHashMap();

    private BulkUpdateTestBuilder() {}

    public static BulkUpdateTestBuilder aBulkUpdate() {
        return new BulkUpdateTestBuilder();
    }

    @Override
    public void setFields(BulkUpdate bulkUpdate) {
        setField(bulkUpdate, "assetIds", assetIds);
        setField(bulkUpdate, "properties", properties);
    }

    public BulkUpdateTestBuilder withAssetIds(AssetId... assetIds) {
        this.assetIds.addAll(newHashSet(assetIds));
        return this;
    }

    public BulkUpdateTestBuilder withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }
}