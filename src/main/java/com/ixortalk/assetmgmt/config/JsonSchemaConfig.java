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
package com.ixortalk.assetmgmt.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.inject.Inject;


@Configuration
public class JsonSchemaConfig {

    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.DEBUG;
    public static final LogLevel DEFAULT_EXCEPTION_THRESHOLD = LogLevel.FATAL;

    @Value("${assetmgmt.schemas.asset}")
    private String jsonSchemaUrl;

    @Inject
    private JsonSchemaResolver jsonSchemaResolver;

    @Bean
    public JsonSchema jsonSchema() throws Exception {

        final JsonSchema jsonSchema = JsonSchemaFactory.newBuilder()
                .setReportProvider(new ListReportProvider(DEFAULT_LOG_LEVEL, DEFAULT_EXCEPTION_THRESHOLD))
                .freeze()
                .getJsonSchema(jsonNode());

        return jsonSchema;
    }

    @Bean
    public JsonNode jsonNode() {
        return jsonSchemaResolver.getJsonSchema(jsonSchemaUrl);
    }

    // Needed to inject Validator objects with injection support
    @Bean
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
}
