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

import com.ixortalk.assetmgmt.AbstractSpringIntegrationTest;
import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.domain.AssetId;
import org.junit.Test;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

public class AssetController_Update_IntegrationTest extends AbstractSpringIntegrationTest {

    @Test
    public void entityExists() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        asset.getAssetProperties().getProperties().put("description", "A recently edited test asset");

        given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .body(objectMapper.writeValueAsString(asset))
                .when()
                .put(ASSETS_BASE_PATH + "/" + asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_OK);

        assertThat(assetRepository.findOne(asset.getAssetId())).isEqualToComparingFieldByFieldRecursively(asset);
    }

    @Test
    public void noAdminRole() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        given()
                .contentType(JSON)
                .auth().oauth2(userToken().getValue())
                .body(objectMapper.writeValueAsString(asset))
                .when()
                .put(ASSETS_BASE_PATH + "/" + asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void idMismatch() throws IOException {
        Asset asset = objectMapper.readValue(VALID_ASSET_JSON, Asset.class);

        AssetId urlId = AssetId.create();
        AssetId bodyId = asset.getAssetId();

        String locationHeader =
                given()
                        .contentType(JSON)
                        .auth().oauth2(adminToken().getValue())
                        .body(objectMapper.writeValueAsString(asset))
                        .when()
                        .put(ASSETS_BASE_PATH + "/" + urlId.stringValue())
                        .then()
                        .statusCode(HTTP_CREATED)
                        .extract().header(LOCATION);

        assertThat(parseAssetIdFromUrl(locationHeader)).isEqualTo(urlId).isNotEqualTo(bodyId);

    }

    @Test
    public void entityDoesNotExist() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        assetRepository.delete(asset);

        given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .body(objectMapper.writeValueAsString(asset))
                .when()
                .put(ASSETS_BASE_PATH + "/" + asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_CREATED);
    }

    @Test
    public void entityIsNotValid() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        asset.getAssetProperties().getProperties().clear();

        given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .body(objectMapper.writeValueAsString(asset))
                .when()
                .put(ASSETS_BASE_PATH + "/" + asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_BAD_REQUEST);
    }
}
