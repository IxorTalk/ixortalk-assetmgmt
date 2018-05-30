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
package com.ixortalk.assetmgmt.rest.prometheus;

import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static com.google.common.collect.Maps.newHashMap;

@ConfigurationProperties(prefix = "ixortalk")
@Validated
public class IxorTalkConfigProperties {

    @Valid
    private Prometheus prometheus = new Prometheus();

    public Prometheus getPrometheus() {
        return prometheus;
    }

    public static class Prometheus {

        @Valid
        private Scrape scrape = new Scrape();

        private Map<String, Map<String, String>> filters = newHashMap();

        private String staticConfigsPath;

        public Scrape getScrape() {
            return scrape;
        }

        public String getStaticConfigsPath() {
            return staticConfigsPath;
        }

        public void setStaticConfigsPath(String staticConfigsPath) {
            this.staticConfigsPath = staticConfigsPath;
        }

        public Map<String, Map<String, String>> getFilters() {
            return filters;
        }

        public void setFilters(Map<String, Map<String, String>> filters) {
            this.filters = filters;
        }
    }

    public static class Scrape {

        @Valid
        private Config config = new Config();

        public Config getConfig() {
            return config;
        }
    }

    public static class Config {

        @NotEmpty
        private String hostPropertyName;

        @NotEmpty
        private String portPropertyName;

        public String getHostPropertyName() {
            return hostPropertyName;
        }

        public void setHostPropertyName(String hostPropertyName) {
            this.hostPropertyName = hostPropertyName;
        }

        public String getPortPropertyName() {
            return portPropertyName;
        }

        public void setPortPropertyName(String portPropertyName) {
            this.portPropertyName = portPropertyName;
        }
    }
}
