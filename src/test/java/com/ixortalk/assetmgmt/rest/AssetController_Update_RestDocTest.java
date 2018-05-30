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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ixortalk.assetmgmt.AbstractRestDocTest;
import com.ixortalk.assetmgmt.domain.Asset;
import org.junit.Test;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.assetmgmt.domain.AssetPropertiesTestBuilder.anAssetProperties;
import static com.ixortalk.assetmgmt.domain.AssetTestBuilder.anAsset;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.UUID.randomUUID;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AssetController_Update_RestDocTest extends AbstractRestDocTest {

    @Test
    public void updateAsset() throws JsonProcessingException {
        Asset asset = assetRepository.save(anAsset().build());

        OAuth2AccessToken accessTokenWithAdminRole = adminToken();

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(accessTokenWithAdminRole.getValue())
                .filter(
                        document("assets/put/ok",
                                preprocessRequest(staticUris(), prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ),
                                requestFields(
                                        fieldWithPath("assetProperties").description("A wrapper around the key-value properties map"),
                                        fieldWithPath("assetProperties.properties").description("A key-value map containing the asset's properties."),
                                        fieldWithPath("children").type(ARRAY).description("An array of child assets."),
                                        fieldWithPath("roles").type(ARRAY).description("An array of roles that have access to this asset.")
                                )
                        )
                )
                .body(objectMapper.writeValueAsString(anAsset().withAssetProperties(anAssetProperties().withHostname("newHostName").build()).build()))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}", asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    public void updateAsset_EntityDoesNotExists() throws IOException {
        OAuth2AccessToken accessTokenWithAdminRole = adminToken();

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(accessTokenWithAdminRole.getValue())
                .filter(
                        document("assets/put/doesnt_exist",
                                preprocessRequest(staticUris(), prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ),
                                requestFields(
                                        fieldWithPath("assetProperties").description("A wrapper around the key-value properties map"),
                                        fieldWithPath("assetProperties.properties").description("A key-value map containing the asset's properties."),
                                        fieldWithPath("children").type(ARRAY).description("An array of child assets."),
                                        fieldWithPath("roles").type(ARRAY).description("An array of roles that have access to this asset.")
                                )
                        )
                )
                .body(objectMapper.writeValueAsString(anAsset().withAssetProperties(anAssetProperties().withHostname("newHostName").build()).build()))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}", randomUUID().toString())
                .then()
                .statusCode(HTTP_CREATED);
    }
}
