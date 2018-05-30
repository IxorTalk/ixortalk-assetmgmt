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

import java.io.IOException;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.ixortalk.assetmgmt.AssetMgmtApplication;
import com.ixortalk.test.util.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AssetMgmtApplication.class)
@ActiveProfiles("test")
public class AssetValidationTest {

    private static final String VALID_ASSET = "asset_with_children.json";
    private static final String INVALID_ASSET = "invalidAsset.json";

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private JsonSchema jsonSchema;

    @Test
    public void validateAssetJson_isValid() throws ProcessingException, IOException {
        Asset asset = objectMapper.readValue(FileUtil.jsonFile(VALID_ASSET), Asset.class);
        JsonNode jsonNode = JsonLoader.fromString(objectMapper.writeValueAsString(asset));

        ProcessingReport processingReport = jsonSchema.validate(jsonNode);

        assertThat(processingReport.isSuccess()).describedAs(processingReport.toString()).isTrue();
    }

    @Test
    public void validateAssetJson_isNotValid() throws IOException, ProcessingException {
        Asset asset = objectMapper.readValue(FileUtil.jsonFile(INVALID_ASSET), Asset.class);
        JsonNode jsonNode = JsonLoader.fromString(objectMapper.writeValueAsString(asset));

        assertThat(jsonSchema.validate(jsonNode).isSuccess()).isFalse();
    }
}
