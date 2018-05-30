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

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.ixortalk.assetmgmt.validation.ValidAsset;
import org.springframework.data.annotation.Id;

import static com.google.common.collect.Sets.newHashSet;
import static com.ixortalk.assetmgmt.domain.AssetId.create;
import static com.ixortalk.assetmgmt.domain.AssetProperties.assetPropertiesWith;

@ValidAsset
public class Asset {

    @Id
    @JsonUnwrapped
    private AssetId assetId;

    private AssetProperties assetProperties;

    private Set<Asset> children = newHashSet();

    private Set<String> roles = newHashSet();

    Asset() {}

    public AssetId getAssetId() {
        return assetId;
    }

    public AssetProperties getAssetProperties() { return assetProperties; }

    public Set<Asset> getChildren() {
        return children;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Asset generateAssetId() {
        this.assetId = create();
        return this;
    }

    public static Asset assetWith(String key, Object value) {
        Asset asset = blankAsset();
        asset.assetProperties = assetPropertiesWith(key, value);
        return asset;
    }

    public static Asset assetWithProperties(Map<String, Object> properties) {
        Asset asset = blankAsset();
        asset.assetProperties = new AssetProperties();
        asset.assetProperties.getProperties().putAll(properties);
        asset.children = null;
        asset.roles = null;
        return asset;
    }

	public static Asset blankAsset() {
        Asset asset = new Asset();
        asset.assetId = null;
        return asset;
	}

    public Object getAssetProperty(String propertyName) {
        return getAssetProperties().getProperty(propertyName);
    }

    public boolean matchesProperties(Map<String, String> filters) {
        return this.assetProperties.matchesProperties(filters);
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetId=" + assetId +
                '}';
    }
}
