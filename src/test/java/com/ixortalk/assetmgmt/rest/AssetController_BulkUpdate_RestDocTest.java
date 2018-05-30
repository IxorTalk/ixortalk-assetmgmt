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

import com.ixortalk.assetmgmt.AbstractRestDocTest;
import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.domain.AssetId;
import org.junit.Test;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.assetmgmt.config.OAuth2ExtendedConfiguration.ROLE_CUSTOMER;
import static com.ixortalk.assetmgmt.config.OAuth2ExtendedConfiguration.customerRoleToken;
import static com.ixortalk.assetmgmt.domain.AssetPropertiesTestBuilder.anAssetProperties;
import static com.ixortalk.assetmgmt.domain.AssetTestBuilder.anAsset;
import static com.ixortalk.assetmgmt.rest.dto.BulkUpdateTestBuilder.aBulkUpdate;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AssetController_BulkUpdate_RestDocTest extends AbstractRestDocTest {

    @Test
    public void success() throws IOException {
        Asset asset1 = assetRepository.save(anAsset().withAssetProperties(anAssetProperties().withHostname("asset1").withPort(1).build()).build());
        Asset asset2 = assetRepository.save(anAsset().withAssetProperties(anAssetProperties().withHostname("asset2").withPort(2).build()).build());
        Asset asset3 = assetRepository.save(anAsset().withAssetProperties(anAssetProperties().withHostname("asset3").withPort(3).build()).build());

        given(this.spec)
                .contentType(JSON)
                .auth().preemptive().oauth2(adminToken().getValue())
                .filter(
                        document("assets/update-bulk/ok",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                requestFields(
                                        fieldWithPath("assetIds").description("A list of asset id's to update in bulk"),
                                        fieldWithPath("properties").description("A key-value map containing the properties to update.")
                                )
                        )
                )
                .body(objectMapper.writeValueAsString(
                        aBulkUpdate()
                                .withAssetIds(asset1.getAssetId(), asset3.getAssetId())
                                .withProperty("port", 4)
                                .build()))
                .when()
                .patch(ASSETS_BASE_PATH + "/bulk")
                .then()
                .statusCode(HTTP_OK);

        assertThat(assetRepository.findOne(asset1.getAssetId()).getAssetProperties().getProperty("port")).isEqualTo(4);
        assertThat(assetRepository.findOne(asset2.getAssetId()).getAssetProperties().getProperty("port")).isEqualTo(2);
        assertThat(assetRepository.findOne(asset3.getAssetId()).getAssetProperties().getProperty("port")).isEqualTo(4);
    }

    @Test
    public void oneAssetIdNotFound() throws IOException {
        Asset asset1 = assetRepository.save(anAsset().withAssetProperties(anAssetProperties().withHostname("asset1").withPort(1).build()).build());
        Asset asset2 = assetRepository.save(anAsset().withAssetProperties(anAssetProperties().withHostname("asset2").withPort(2).build()).build());
        Asset asset3 = assetRepository.save(anAsset().withAssetProperties(anAssetProperties().withHostname("asset3").withPort(3).build()).build());

        AssetId unexistingAssetId = AssetId.create();
        given(this.spec)
                .contentType(JSON)
                .auth().preemptive().oauth2(adminToken().getValue())
                .filter(
                        document("assets/update-bulk/one-not-found",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                requestFields(
                                        fieldWithPath("assetIds").description("A list of asset id's to update in bulk"),
                                        fieldWithPath("properties").description("A key-value map containing the properties to update.")
                                )
                        )
                )
                .body(objectMapper.writeValueAsString(
                        aBulkUpdate()
                                .withAssetIds(asset1.getAssetId(), asset2.getAssetId(), asset3.getAssetId(), unexistingAssetId)
                                .withProperty("port", 4)
                                .build()))
                .when()
                .patch(ASSETS_BASE_PATH + "/bulk")
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body(containsString(unexistingAssetId.stringValue()));
    }

    @Test
    public void oneAssetIdNotAuthorized() throws IOException {
        Asset asset1 = assetRepository.save(anAsset().withRoles(ROLE_CUSTOMER).withAssetProperties(anAssetProperties().withHostname("asset1").withPort(1).build()).build());
        Asset asset2 = assetRepository.save(anAsset().withRoles(ROLE_CUSTOMER).withAssetProperties(anAssetProperties().withHostname("asset2").withPort(2).build()).build());
        Asset asset3 = assetRepository.save(anAsset().withRoles("ROLE_OTHER").withAssetProperties(anAssetProperties().withHostname("asset3").withPort(3).build()).build());

        given(this.spec)
                .contentType(JSON)
                .auth().preemptive().oauth2(customerRoleToken().getValue())
                .filter(
                        document("assets/update-bulk/one-not-authorized",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                requestFields(
                                        fieldWithPath("assetIds").description("A list of asset id's to update in bulk"),
                                        fieldWithPath("properties").description("A key-value map containing the properties to update.")
                                )
                        )
                )
                .body(objectMapper.writeValueAsString(
                        aBulkUpdate()
                                .withAssetIds(asset1.getAssetId(), asset2.getAssetId(), asset3.getAssetId())
                                .withProperty("port", 4)
                                .build()))
                .when()
                .patch(ASSETS_BASE_PATH + "/bulk")
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }
}
