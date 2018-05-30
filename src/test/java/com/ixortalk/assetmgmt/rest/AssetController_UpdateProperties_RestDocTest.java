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
package com.ixortalk.assetmgmt.rest;

import java.io.IOException;
import java.util.Map;

import com.ixortalk.assetmgmt.AbstractRestDocTest;
import com.ixortalk.assetmgmt.domain.Asset;
import org.junit.Test;

import static com.google.common.collect.Maps.newHashMap;
import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AssetController_UpdateProperties_RestDocTest extends AbstractRestDocTest {

    @Test
    public void updateProperties_EntityExists() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(ASSET_JSON, Asset.class));

        Map<String, String> newProperties = newHashMap();
        newProperties.put("hostname", "newHostname");
        newProperties.put("description", "newDescription");

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(adminToken().getValue())
                .filter(
                        document("assets/put-properties/ok",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ))
                )
                .body(objectMapper.writeValueAsString(newProperties))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}/properties", asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    public void updateProperties_EntityDoesNotExist() throws IOException {
        Map<String, String> newProperties = newHashMap();
        newProperties.put("hostname", "newHostname");
        newProperties.put("description", "newDescription");

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(adminToken().getValue())
                .filter(
                        document("assets/put-properties/not-found",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ))
                )
                .body(objectMapper.writeValueAsString(newProperties))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}/properties", "unexisting")
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }
}
