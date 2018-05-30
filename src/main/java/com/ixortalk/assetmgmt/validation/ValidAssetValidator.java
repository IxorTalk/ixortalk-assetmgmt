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
package com.ixortalk.assetmgmt.validation;

import java.io.IOException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.ixortalk.assetmgmt.domain.Asset;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.logging.LogFactory.getLog;


public class ValidAssetValidator implements ConstraintValidator<ValidAsset, Asset> {

    private static final Log log = getLog(ValidAssetValidator.class);
 

    @Autowired
    private JsonSchema jsonSchema;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void initialize(ValidAsset constraintAnnotation) {
    }

    @Override
    public boolean isValid(Asset asset, ConstraintValidatorContext context) {
        try {
            ProcessingReport report;
            JsonNode jsonNode = JsonLoader.fromString(objectMapper.writeValueAsString(asset));
            report = jsonSchema.validate(jsonNode);

            if (!report.isSuccess()) {
                for (final ProcessingMessage message : report) {
                    context
                            .buildConstraintViolationWithTemplate("Asset invalid : " + message.getMessage())
                            .addConstraintViolation();
                }
                log.error("Asset invalid : " + report);
            }

            return report.isSuccess();


        } catch (IOException | ProcessingException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).
                    addConstraintViolation();
            return false;
        }
    }

}