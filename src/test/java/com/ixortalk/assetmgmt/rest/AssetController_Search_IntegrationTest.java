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

import com.ixortalk.assetmgmt.AbstractSpringIntegrationTest;
import com.ixortalk.assetmgmt.domain.Asset;
import org.junit.Test;

import java.io.IOException;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class AssetController_Search_IntegrationTest extends AbstractSpringIntegrationTest {

    @Test
    public void search() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        Asset[] result =
                given()
                        .contentType(JSON)
                        .auth().oauth2(adminToken().getValue())
                        .when()
                        .body("{\"hostname\" : \"" + asset.getAssetProperty("hostname") + "\"}")
                        .post(ASSETS_BASE_PATH + "/search/property")
                        .then()
                        .statusCode(HTTP_OK)
                        .extract().response().as(Asset[].class);

        assertThat(result).hasSize(1).usingRecursiveFieldByFieldElementComparator().containsOnly(asset);
    }

    @Test
    public void props() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        Asset[] result =
                given()
                        .contentType(JSON)
                        .auth().oauth2(adminToken().getValue())
                        .when()
                        .get(ASSETS_BASE_PATH + "/search/property/hostname/" + asset.getAssetProperty("hostname"))
                        .then()
                        .statusCode(HTTP_OK)
                        .extract().response().as(Asset[].class);

        assertThat(result).hasSize(1).usingRecursiveFieldByFieldElementComparator().containsOnly(asset);
    }

    @Test
    public void noResults() throws IOException {
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        Asset[] result =
                given()
                        .contentType(JSON)
                        .auth().oauth2(adminToken().getValue())
                        .when()
                        .body("{\"hostname\" : \"notFoundHostname\"}")
                        .post(ASSETS_BASE_PATH + "/search/property")
                        .then()
                        .statusCode(HTTP_OK)
                        .extract().response().as(Asset[].class);

        assertThat(result).isEmpty();
    }

    @Test
    public void multipleResults() throws IOException {
        Asset asset = objectMapper.readValue(VALID_ASSET_JSON, Asset.class);

        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        Asset[] result =
                given()
                        .contentType(JSON)
                        .auth().oauth2(adminToken().getValue())
                        .when()
                        .body("{\"hostname\" : \"" + asset.getAssetProperty("hostname") + "\"}")
                        .post(ASSETS_BASE_PATH + "/search/property")
                        .then()
                        .statusCode(HTTP_OK)
                        .extract().response().as(Asset[].class);

        assertThat(result).hasSize(2);
    }
}
