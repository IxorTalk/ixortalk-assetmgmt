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

import java.util.Set;

import com.ixortalk.test.builder.ReflectionInstanceTestBuilder;

import static com.google.common.collect.Sets.newHashSet;
import static com.ixortalk.assetmgmt.domain.AssetPropertiesTestBuilder.anAssetProperties;
import static java.util.Arrays.asList;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class AssetTestBuilder extends ReflectionInstanceTestBuilder<Asset> {

    private AssetProperties assetProperties = anAssetProperties().build();
    private Set<String> children = newHashSet();
    private Set<String> roles = newHashSet();

    private AssetTestBuilder() {
    }

    public static AssetTestBuilder anAsset() {
        return new AssetTestBuilder();
    }

    @Override
    public void setFields(Asset instance) {
        setField(instance, "assetProperties", assetProperties);
        setField(instance, "children", children);
        setField(instance, "roles", roles);
    }

    public AssetTestBuilder withAssetProperties(AssetProperties assetProperties) {
        this.assetProperties = assetProperties;
        return this;
    }

    public AssetTestBuilder withChildren(Set<String> children) {
        this.children = children;
        return this;
    }

    public AssetTestBuilder withRoles(String... roles) {
        this.roles.addAll(asList(roles));
        return this;
    }
}