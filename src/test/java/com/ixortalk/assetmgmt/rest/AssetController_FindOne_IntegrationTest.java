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
import static com.ixortalk.assetmgmt.config.OAuth2ExtendedConfiguration.ROLE_CUSTOMER;
import static com.ixortalk.assetmgmt.config.OAuth2ExtendedConfiguration.customerRoleToken;
import static com.ixortalk.assetmgmt.domain.AssetTestBuilder.anAsset;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class AssetController_FindOne_IntegrationTest extends AbstractSpringIntegrationTest {
	
    @Test
    public void success() throws IOException {

        Asset asset = assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        Asset result = given()
                .accept(JSON)
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .when()
                .body("{\"hostname\" : \"" + asset.getAssetProperty("hostname") + "\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_OK)
                .extract().response().as(Asset.class);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(asset);
    }

    @Test
    public void filteringOnRole_WithAccess() {
        Asset persistedAsset = assetRepository.save(anAsset().withRoles(ROLE_CUSTOMER).build());

        Asset result = given()
                .accept(JSON)
                .contentType(JSON)
                .auth().oauth2(customerRoleToken().getValue())
                .when()
                .body("{\"hostname\" : \"" + persistedAsset.getAssetProperty("hostname") + "\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_OK)
                .extract().response().as(Asset.class);

        assertThat(result).isNotNull();
    }

    @Test
    public void filteringOnRole_WithoutAccess() {
        Asset persistedAsset = assetRepository.save(anAsset().withRoles("ROLE_FOR_SOMEONE_ELSE").build());

        given()
                .accept(JSON)
                .contentType(JSON)
                .auth().oauth2(customerRoleToken().getValue())
                .when()
                .body("{\"hostname\" : \"" + persistedAsset.getAssetProperty("hostname") + "\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void noResults() throws IOException {
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        given()
                .accept(JSON)
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .when()
                .body("{\"hostname\" : \"notFoundHostname\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void multipleResults() throws IOException {
        Asset asset = objectMapper.readValue(VALID_ASSET_JSON, Asset.class);

        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class));

        given()
                .accept(JSON)
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .when()
                .body("{\"hostname\" : \"" + asset.getAssetProperty("hostname") + "\"}")
                .post(ASSETS_BASE_PATH + "/find/property")
                .then()
                .statusCode(HTTP_BAD_REQUEST);
    }
}
