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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.github.fge.jackson.JsonLoader.fromFile;
import static com.github.fge.jackson.JsonLoader.fromResource;
import static com.github.fge.jackson.JsonLoader.fromURL;

@Component
public class JsonSchemaResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaResolver.class);

    public JsonNode getJsonSchema(String jsonSchemaUrl) {
        JsonNode schemaNode = null;

        try {
            schemaNode = isURL(jsonSchemaUrl) ? fromURL(new URL(jsonSchemaUrl)) : fromResource(jsonSchemaUrl);
        } catch (IOException ex) {
            try {
                schemaNode = fromFile(new File(jsonSchemaUrl));
            } catch (IOException ex2) {
                LOGGER.error("Unable to load jsonSchemaUrl " + jsonSchemaUrl);
            }
        }
        return schemaNode;
    }

    private static boolean isURL(String jsonSchemaUrl) {
        try {
            new URL(jsonSchemaUrl);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
