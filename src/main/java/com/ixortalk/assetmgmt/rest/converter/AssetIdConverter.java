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
package com.ixortalk.assetmgmt.rest.converter;

import java.io.Serializable;

import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.domain.AssetId;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

import static com.ixortalk.assetmgmt.domain.AssetId.assetId;

public class AssetIdConverter implements BackendIdConverter {

    @Override
    public Serializable fromRequestId(String id, Class<?> entityType) {
        return id != null ? assetId(id) : null;
    }

    @Override
    public String toRequestId(Serializable id, Class<?> entityType) {
        return id != null ? ((AssetId) id).stringValue() : null;
    }

    @Override
    public boolean supports(Class<?> delimiter) {
        return delimiter.equals(Asset.class);
    }
}
