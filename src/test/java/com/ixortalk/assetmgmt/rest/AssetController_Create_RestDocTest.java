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

import com.ixortalk.assetmgmt.AbstractRestDocTest;
import org.junit.Test;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AssetController_Create_RestDocTest extends AbstractRestDocTest {

    @Test
    public void withAdminRole() {
        OAuth2AccessToken accessTokenWithAdminRole = adminToken();

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(accessTokenWithAdminRole.getValue())
                .filter(
                        document("assets/post/created",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                                ASSET_REQUEST_FIELDS,
                                responseHeaders(
                                        headerWithName("Location").description("The location of the newly created asset")
                                )
                        )
                )
                .body(ASSET_JSON)
                .when()
                .post(ASSETS_BASE_PATH)
                .then()
                .statusCode(HTTP_CREATED);
    }

    @Test
    public void withUserRole() {
        OAuth2AccessToken accessTokenWithUserRole = userToken();

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(accessTokenWithUserRole.getValue())
                .filter(
                        document("assets/post/forbidden",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                ASSET_REQUEST_FIELDS)
                )
                .body(ASSET_JSON)
                .when()
                .post(ASSETS_BASE_PATH)
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void withoutAccessToken() {
        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2("invalid-token")
                .filter(
                        document("assets/post/unauthorized",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()), ASSET_REQUEST_FIELDS)
                )
                .body(ASSET_JSON)
                .when()
                .post(ASSETS_BASE_PATH)
                .then()
                .statusCode(HTTP_UNAUTHORIZED);
    }
}
