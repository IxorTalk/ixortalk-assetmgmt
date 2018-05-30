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
import org.junit.Test;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AssetController_FindOne_RestDocTest extends AbstractRestDocTest {

    @Test
    public void success() throws IOException {
        assetRepository.save(objectMapper.readValue(ASSET_JSON, Asset.class));

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(userToken().getValue())
                .filter(
                        document("assets/find-one",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER)))
                .when()
                .body("{\"hostname\" : \"" + objectMapper.readValue(ASSET_JSON, Asset.class).getAssetProperty("hostname") + "\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    public void noResults() {
        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(userToken().getValue())
                .filter(
                        document("assets/find-one-no-result",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER)))
                .when()
                .body("{\"hostname\" : \"notFoundHostname\"}")
                .post(ASSETS_BASE_PATH + "/find-one")
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void multipleResults() throws IOException {
        assetRepository.save(objectMapper.readValue(ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ASSET_JSON, Asset.class));

        given(this.spec)
                .accept(JSON)
                .contentType(JSON)
                .auth().preemptive().oauth2(userToken().getValue())
                .filter(
                        document("assets/find-one-multiple-result",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(AUTHORIZATION_TOKEN_HEADER)))
                .when()
                .body("{\"hostname\" : \"" + objectMapper.readValue(ASSET_JSON, Asset.class).getAssetProperty("hostname") + "\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_BAD_REQUEST);
    }
}
