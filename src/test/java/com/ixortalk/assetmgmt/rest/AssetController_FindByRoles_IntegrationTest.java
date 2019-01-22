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
import com.jayway.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.newHashSet;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class AssetController_FindByRoles_IntegrationTest extends AbstractSpringIntegrationTest {

    private Asset asset, asset2;
    private Set<String> roles, roles2;
    private final String role = "ROLE_ORGANIZATION_X_ADMIN";

    @Before
    public void before() throws IOException {
        roles = newHashSet();
        roles.add(role);
        roles2 = newHashSet();
        roles2.add(role);

        asset = objectMapper.readValue(VALID_ASSET_JSON, Asset.class);
        asset2 = objectMapper.readValue(ANOTHER_ASSET_JSON, Asset.class);

        roles.addAll(asset.getRoles());
        roles2.addAll(asset2.getRoles());
        setField(asset, "roles", roles);
        setField(asset2, "roles", roles2);

        assetRepository.save(asset);
        assetRepository.save(asset2);
    }

    @Test
    public void asAdmin() {


        JsonPath assets = given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .get(ASSETS_BASE_PATH +"/search/findByRoles?role="+role)
                .then()
                .statusCode(HTTP_OK)
                .extract().jsonPath();

        assertThat(assets.getString("_embedded.assets"))
                .contains(asset.getAssetId().stringValue(), asset2.getAssetId().stringValue());
    }

    @Test
    public void asUser() {

        given()
                .contentType(JSON)
                .auth().oauth2(userToken().getValue())
                .get(ASSETS_BASE_PATH +"/search/findByRoles?role="+role)
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void whenRoleDoesNotExist() {

        JsonPath result = given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .get(ASSETS_BASE_PATH +"/search/findByRoles?role=randomRole")
                .then()
                .statusCode(HTTP_OK)
                .extract().jsonPath();

        assertThat(result.getString("_embedded.assets")).doesNotContain(asset.getAssetId().stringValue());
    }

    @Test
    public void whenAssetsDoNotExist() {

        assetRepository.delete(asset);
        assetRepository.delete(asset2);

        JsonPath result = given()
                .contentType(JSON)
                .auth().oauth2(adminToken().getValue())
                .get(ASSETS_BASE_PATH +"/search/findByRoles?role="+role)
                .then()
                .statusCode(HTTP_OK)
                .extract().jsonPath();

        assertThat(result.getString("_embedded.assets")).doesNotContain(asset.getAssetId().stringValue());
    }
}
