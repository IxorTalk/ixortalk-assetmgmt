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

import com.fasterxml.jackson.databind.JsonNode;
import com.ixortalk.assetmgmt.AbstractSpringIntegrationTest;
import com.ixortalk.assetmgmt.domain.Asset;
import org.junit.Test;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.assetmgmt.config.OAuth2ExtendedConfiguration.ROLE_CUSTOMER;
import static com.ixortalk.assetmgmt.config.OAuth2ExtendedConfiguration.customerRoleToken;
import static com.ixortalk.assetmgmt.domain.AssetPropertiesTestBuilder.anAssetProperties;
import static com.ixortalk.assetmgmt.domain.AssetTestBuilder.anAsset;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class AssetController_GetAssets_IntegrationTest extends AbstractSpringIntegrationTest {

    private static final String FIRST_ASSET_FOR_CUSTOMER = "firstAssetForCustomer";
    private static final String SECOND_ASSET_FOR_CUSTOMER = "secondAssetForCustomer";

    @Test
    public void noExistingAssets() {
        JsonNode page =
                given()
                        .auth().oauth2(userToken().getValue())
                        .when()
                        .get(ASSETS_BASE_PATH)
                        .then()
                        .statusCode(HTTP_OK)
                        .extract()
                        .response()
                        .as(JsonNode.class);

        JsonNode assets = page.get("_embedded").get("assets");

        assertThat(assets.isArray()).isTrue();
        assertThat(assets.size()).isEqualTo(0);
    }

    @Test
    public void getAssets() throws IOException {
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        JsonNode page = given()
                .auth().oauth2(userToken().getValue())
                .when()
                .get(ASSETS_BASE_PATH)
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response()
                .as(JsonNode.class);

        JsonNode assets = page.get("_embedded").get("assets");

        assertThat(assets.size()).isEqualTo(3);
    }

    @Test
    public void filteringOnRole() {
        assetRepository.save(anAsset().withRoles(ROLE_CUSTOMER).withAssetProperties(anAssetProperties().withHostname(FIRST_ASSET_FOR_CUSTOMER).build()).build());
        assetRepository.save(anAsset().withRoles("ROLE_FOR_SOME_OTHER_CUSTOMER").withAssetProperties(anAssetProperties().withHostname("assetForAnotherCustomer").build()).build());
        assetRepository.save(anAsset().withRoles(ROLE_CUSTOMER).withAssetProperties(anAssetProperties().withHostname(SECOND_ASSET_FOR_CUSTOMER).build()).build());

        JsonNode page = given()
                .auth().oauth2(customerRoleToken().getValue())
                .when()
                .get(ASSETS_BASE_PATH)
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response()
                .as(JsonNode.class);

        JsonNode assets = page.get("_embedded").get("assets");

        assertThat(assets.size()).isEqualTo(2);
        assertThat(assets.get(0).get("assetProperties").get("properties").get("hostname").asText()).isEqualTo(FIRST_ASSET_FOR_CUSTOMER);
        assertThat(assets.get(1).get("assetProperties").get("properties").get("hostname").asText()).isEqualTo(SECOND_ASSET_FOR_CUSTOMER);
    }

    @Test
    public void oneInvalid() throws Exception {
        Asset asset = objectMapper.readValue(INVALID_ASSET_JSON, Asset.class);
        saveToMongoDirectly(asset);
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));
        assetRepository.save(objectMapper.readValue(VALID_ASSET_JSON, Asset.class));

        JsonNode page = given()
                .auth().oauth2(userToken().getValue())
                .when()
                .get(ASSETS_BASE_PATH)
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response()
                .as(JsonNode.class);
        JsonNode assets = page.get("_embedded").get("assets");

        assertThat(assets.size()).isEqualTo(2);
    }
}
