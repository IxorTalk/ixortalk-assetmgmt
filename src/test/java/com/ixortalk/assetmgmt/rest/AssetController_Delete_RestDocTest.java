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
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.assetmgmt.domain.AssetId.assetId;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AssetController_Delete_RestDocTest extends AbstractRestDocTest {

    @Test
    public void success() throws IOException {
        AssetId assetId = assetRepository.save(objectMapper.readValue(ASSET_JSON, Asset.class)).getAssetId();

        OAuth2AccessToken accessTokenWithAdminRole = adminToken();

        given(this.spec)
                .auth().preemptive().oauth2(accessTokenWithAdminRole.getValue())
                .filter(
                        document("assets/delete/ok",
                                preprocessRequest(staticUris(), prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ))
                )
                .when()
                .delete(ASSETS_BASE_PATH + "/{id}", assetId.stringValue())
                .then()
                .statusCode(HTTP_NO_CONTENT);
    }

    @Test
    public void notFound() {
        AssetId assetId = assetId("unexistingid");

        OAuth2AccessToken accessTokenWithAdminRole = adminToken();

        given(this.spec)
                .auth().preemptive().oauth2(accessTokenWithAdminRole.getValue())
                .filter(
                        document("assets/delete/not_found",
                                preprocessRequest(staticUris(), prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ))
                )
                .when()
                .delete(ASSETS_BASE_PATH + "/{id}", assetId.stringValue())
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void forbidden() throws IOException {
        AssetId assetId = assetRepository.save(objectMapper.readValue(ASSET_JSON, Asset.class)).getAssetId();

        OAuth2AccessToken accessTokenWithUserRole = userToken();

        given(this.spec)
                .auth().preemptive().oauth2(accessTokenWithUserRole.getValue())
                .filter(
                        document("assets/delete/forbidden",
                                preprocessRequest(staticUris(), prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                pathParameters(
                                        parameterWithName("id").description("The asset's id")
                                ))
                )
                .when()
                .delete(ASSETS_BASE_PATH + "/{id}", assetId.stringValue())
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }
}
