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


import java.util.Set;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;

import static java.lang.String.format;

public class AssetValidationMongoEventListener extends ValidatingMongoEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AssetValidationMongoEventListener.class);

    private final Validator validator;

    public AssetValidationMongoEventListener(Validator validator) {
        super(validator);
        this.validator = validator;
    }

    @Override
    public void onAfterConvert(AfterConvertEvent<Object> event) {
        Object source = event.getSource();
        Set violations = this.validator.validate(source, new Class[0]);
        if(!violations.isEmpty()) {
            String errorMessage = format("During onAfterConvert: %s validation violations found: %s", source, violations);
            LOG.error(errorMessage);
            throw new ConstraintViolationException(errorMessage, violations);
        }
    }

}
