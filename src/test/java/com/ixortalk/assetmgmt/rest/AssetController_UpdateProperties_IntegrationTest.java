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

import com.ixortalk.assetmgmt.AbstractSpringIntegrationTest;
import com.ixortalk.assetmgmt.domain.Asset;
import org.junit.Test;

import static com.google.common.collect.Maps.newHashMap;
import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class AssetController_UpdateProperties_IntegrationTest extends AbstractSpringIntegrationTest {

    @Test
    public void entityExists() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        Map<String, String> newProperties = newHashMap();
        newProperties.put("hostname", "newHostname");
        newProperties.put("description", "newDescription");

        given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .body(objectMapper.writeValueAsString(newProperties))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}/properties", asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_OK);

        assertThat(assetRepository.findOne(asset.getAssetId()).getAssetProperties().getProperties())
                .contains(
                        entry("hostname", "newHostname"),
                        entry("description", "newDescription"));
    }

    @Test
    public void entityDoesNotExist() throws IOException {
        Map<String, String> newProperties = newHashMap();
        newProperties.put("hostname", "newHostname");
        newProperties.put("description", "newDescription");

        given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .body(objectMapper.writeValueAsString(newProperties))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}/properties", "unexisting")
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void noAdminRole() throws IOException {
        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        Map<String, String> newProperties = newHashMap();
        newProperties.put("hostname", "newHostname");
        newProperties.put("description", "newDescription");

        given()
                .accept(JSON)
                .contentType(JSON)
                .auth().oauth2(userToken().getValue())
                .body(objectMapper.writeValueAsString(newProperties))
                .when()
                .put(ASSETS_BASE_PATH + "/{id}/properties", asset.getAssetId().stringValue())
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }
}
