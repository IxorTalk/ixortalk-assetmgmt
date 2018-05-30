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

import static com.ixortalk.test.util.Randomizer.nextInt;
import static com.ixortalk.test.util.Randomizer.nextString;

public class AssetPropertiesTestBuilder {

    private String hostname = nextString("hostname-");
    private int port = nextInt();
    private String labelWithDashes;
    private ComplexObject complexObject;
    private String type = nextString("type-");

    private AssetPropertiesTestBuilder() {
    }

    public static AssetPropertiesTestBuilder anAssetProperties() {
        return new AssetPropertiesTestBuilder();
    }

    public AssetProperties build() {
        AssetProperties assetProperties = new AssetProperties();
        assetProperties.getProperties().put("hostname", hostname);
        assetProperties.getProperties().put("port", port);
        if (labelWithDashes != null) {
            assetProperties.getProperties().put("label-with-dashes", labelWithDashes);
        }
        assetProperties.getProperties().put("complex-object-property", complexObject);
        assetProperties.getProperties().put("type", type);
        return assetProperties;
    }

    public AssetPropertiesTestBuilder withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public AssetPropertiesTestBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public AssetPropertiesTestBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public AssetPropertiesTestBuilder withLabelWithDashes(String labelWithDashes) {
        this.labelWithDashes = labelWithDashes;
        return this;
    }

    public AssetPropertiesTestBuilder withComplexObject(ComplexObject complexObject) {
        this.complexObject = complexObject;
        return this;
    }
}