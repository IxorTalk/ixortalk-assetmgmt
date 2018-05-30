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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.inject.Inject;

import com.ixortalk.assetmgmt.AbstractSpringIntegrationTest;
import com.ixortalk.assetmgmt.rest.AssetRepository;
import org.junit.After;
import org.junit.Before;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.nio.file.Files.deleteIfExists;
import static java.util.Arrays.stream;

public abstract class AbstractWritePrometheusStaticConfigsServiceIntegrationTest extends AbstractSpringIntegrationTest {

    public static final String TYPE_A = "type-a";
    public static final String TYPE_B = "type-b";

    @Inject
    protected AssetRepository assetRepository;

    @Inject
    protected WritePrometheusStaticConfigsService writePrometheusStaticConfigsService;

    @Before
    public void createStaticConfigsPath() {
        Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath()).toFile().mkdirs();
    }

    @After
    public void removeStaticConfigFiles() throws IOException {
        File staticConfigsPath = Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath()).toFile();
        if (staticConfigsPath.exists() && staticConfigsPath.isDirectory()) {
            stream(staticConfigsPath.listFiles()).forEach(file -> file.delete());
        }
        deleteIfExists(Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath()));
    }

    protected void callWriteStaticConfigsWithoutSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();

        writePrometheusStaticConfigsService.writeStaticConfigs();

        SecurityContextHolder.setContext(securityContext);
    }
}
