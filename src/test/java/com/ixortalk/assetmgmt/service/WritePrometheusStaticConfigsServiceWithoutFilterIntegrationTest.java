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
package com.ixortalk.assetmgmt.service;

import java.io.IOException;
import java.nio.file.Paths;

import com.ixortalk.assetmgmt.domain.ComplexObject;
import org.junit.Test;

import static com.ixortalk.assetmgmt.domain.AssetPropertiesTestBuilder.anAssetProperties;
import static com.ixortalk.assetmgmt.domain.AssetTestBuilder.anAsset;
import static com.ixortalk.test.util.FileUtil.file;
import static java.nio.file.Files.write;
import static org.assertj.core.api.Assertions.assertThat;

public class WritePrometheusStaticConfigsServiceWithoutFilterIntegrationTest extends AbstractWritePrometheusStaticConfigsServiceIntegrationTest {

    @Test
    public void writeStaticConfigs() {
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostA")
                                        .withPort(9191)
                                        .withType(TYPE_A)
                                        .build())
                        .build());
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostB")
                                        .withPort(9192)
                                        .withType(TYPE_A)
                                        .build())
                        .build());
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostC")
                                        .withPort(9193)
                                        .withType(TYPE_B)
                                        .build())
                        .build());

        callWriteStaticConfigsWithoutSecurityContext();

        assertThat(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), "assets.yml").toFile())
                .exists()
                .hasSameContentAs(file("assets.yml"));
    }

    @Test
    public void complexObjectPropertiesOmitted() {
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostA")
                                        .withPort(9191)
                                        .withType(TYPE_A)
                                        .withComplexObject(new ComplexObject(0, 1))
                                        .build())
                        .build());
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostB")
                                        .withPort(9192)
                                        .withType(TYPE_A)
                                        .build())
                        .build());

        callWriteStaticConfigsWithoutSecurityContext();

        assertThat(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), "assets.yml").toFile())
                .exists()
                .hasSameContentAs(file("type-a-s.yml"));
    }

    @Test
    public void propertyWithNullValuesOmitted() {
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostA")
                                        .withPort(9191)
                                        .withType(TYPE_A)
                                        .withComplexObject(null)
                                        .build())
                        .build());
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostB")
                                        .withPort(9192)
                                        .withType(TYPE_A)
                                        .build())
                        .build());

        callWriteStaticConfigsWithoutSecurityContext();

        assertThat(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), "assets.yml").toFile())
                .exists()
                .hasSameContentAs(file("type-a-s.yml"));
    }

    @Test
    public void dashesReplacedByUnderscores() {
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostA")
                                        .withPort(9191)
                                        .withLabelWithDashes("labelWithDashesValue")
                                        .withType(TYPE_A)
                                        .build())
                        .build());

        callWriteStaticConfigsWithoutSecurityContext();

        assertThat(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), "assets.yml").toFile())
                .exists()
                .hasSameContentAs(file("underscores.yml"));
    }

    @Test
    public void fileExists() throws IOException {
        write(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), "assets.yml"), "some content".getBytes());

        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostA")
                                        .withPort(9191)
                                        .withType(TYPE_A)
                                        .build())
                        .build());
        assetRepository.save(
                anAsset()
                        .withAssetProperties(
                                anAssetProperties()
                                        .withHostname("hostB")
                                        .withPort(9192)
                                        .withType(TYPE_A)
                                        .build())
                        .build());

        callWriteStaticConfigsWithoutSecurityContext();

        assertThat(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), "assets.yml").toFile())
                .exists()
                .hasSameContentAs(file("type-a-s.yml"));
    }
}