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
package com.ixortalk.assetmgmt.domain;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class AssetProperties {

    private Map<String, Object> properties = new HashMap<>();

    AssetProperties() {
    }

    static AssetProperties assetPropertiesWith(String key, Object value) {
        AssetProperties assetProperties = new AssetProperties();
        assetProperties.put(key, value);
        return assetProperties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    private void put(String key, Object value) {
        this.properties.put(key, value);
    }

    public void putAll(Map<String, Object> newProperties) {
        this.properties.putAll(newProperties);
    }

    public Object getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    public boolean matchesProperties(Map<String, String> filters) {
        return filters.entrySet().stream()
                .allMatch(entry ->
                        properties.containsKey(entry.getKey()) && valueOf(properties.get(entry.getKey())).equals(entry.getValue()));
    }
}
