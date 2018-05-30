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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.rest.AssetRepository;
import com.ixortalk.assetmgmt.rest.prometheus.IxorTalkConfigProperties;
import com.ixortalk.assetmgmt.rest.prometheus.StaticConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.google.common.collect.Maps.newHashMap;
import static java.nio.file.Files.move;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.beans.BeanUtils.isSimpleValueType;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;

@Named
@EnableConfigurationProperties(IxorTalkConfigProperties.class)
public class WritePrometheusStaticConfigsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WritePrometheusStaticConfigsService.class);

    private static final Map<String, String> NO_FILTERING = newHashMap();

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private ObjectMapper yamlObjectMapper;

    @Inject
    private IxorTalkConfigProperties ixorTalkConfigProperties;

    @Scheduled(fixedRateString = "${ixortalk.prometheus.write-static-configs-interval-milliseconds}")
    public void writeStaticConfigs() {
        try {
            SecurityContext scheduledTaskContext = createEmptyContext();
            scheduledTaskContext.setAuthentication(new AnonymousAuthenticationToken("scheduled-task", "scheduled-task", createAuthorityList("ROLE_SCHEDULED_TASK")));
            SecurityContextHolder.setContext(scheduledTaskContext);

            if (ixorTalkConfigProperties.getPrometheus().getFilters().isEmpty()) {
                writeStaticConfigsFile("assets", NO_FILTERING);
            } else {
                ixorTalkConfigProperties.getPrometheus().getFilters().forEach((filterName, filterProperties) -> writeStaticConfigsFile(filterName, filterProperties));
            }
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void writeStaticConfigsFile(String filterName, Map<String, String> filterProperties) {
        Path tempYmlPath = Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), filterName + ".yml.tmp");
        Path ymlPath = Paths.get(ixorTalkConfigProperties.getPrometheus().getStaticConfigsPath(), filterName + ".yml");

        try (BufferedWriter writer = newBufferedWriter(tempYmlPath)) {
            writer.write(yamlObjectMapper.writeValueAsString(
                    assetRepository.findAll()
                            .stream()
                            .filter(asset -> asset.matchesProperties(filterProperties))
                            .map(asset ->
                                    new StaticConfig(
                                            createTarget(asset),
                                            transformToValidLabels(asset.getAssetProperties().getProperties())))
                            .collect(toList())));
            move(tempYmlPath, ymlPath, REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Error writing static configs: " + e.getMessage(), e);
        }
    }

    private static Map<String, Object> transformToValidLabels(Map<String, Object> properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getValue() != null && isSimpleValueType(entry.getValue().getClass()))
                .collect(toMap(
                        e -> e.getKey().replace('-', '_'),
                        Map.Entry::getValue));
    }

    private String createTarget(Asset asset) {
        Object host = asset.getAssetProperty(ixorTalkConfigProperties.getPrometheus().getScrape().getConfig().getHostPropertyName());
        Object port = asset.getAssetProperty(ixorTalkConfigProperties.getPrometheus().getScrape().getConfig().getPortPropertyName());

        notNull(host, "Missing host property '" + ixorTalkConfigProperties.getPrometheus().getScrape().getConfig().getHostPropertyName() + "' for asset with id: " + asset.getAssetId().stringValue());
        notNull(port, "Missing port property '" + ixorTalkConfigProperties.getPrometheus().getScrape().getConfig().getPortPropertyName() + "' for asset with id: " + asset.getAssetId().stringValue());

        return host + ":" + port;
    }
}
