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
package com.ixortalk.assetmgmt;

import static com.ixortalk.assetmgmt.TestConstants.ASSETS_BASE_PATH;
import static com.ixortalk.assetmgmt.domain.AssetId.assetId;
import static com.ixortalk.test.util.FileUtil.fileContent;
import static com.ixortalk.test.util.FileUtil.jsonFile;
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static com.mongodb.util.JSON.parse;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import javax.inject.Inject;

import com.ixortalk.assetmgmt.domain.AssetId;
import com.ixortalk.assetmgmt.rest.AssetRepository;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.rest.prometheus.IxorTalkConfigProperties;
import com.ixortalk.test.data.CleanCrudRepositoriesRule;
import com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer;
import com.jayway.restassured.RestAssured;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AssetMgmtApplication.class, OAuth2EmbeddedTestServer.class, CleanCrudRepositoriesRule.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
@EnableConfigurationProperties(IxorTalkConfigProperties.class)
public abstract class AbstractSpringIntegrationTest {

    protected static final String VALID_ASSET_JSON = jsonFile("asset.json");
    protected static final String ANOTHER_ASSET_JSON = jsonFile("anotherAsset.json");
    protected static final String INVALID_ASSET_JSON = jsonFile("invalidAsset.json");
    protected static final String EXPECTED_RESPONSE = fileContent("schemas/", "asset-schema.json");

    @Inject
    private CrudRepository<?, ?>[] crudRepositories;

    @Inject
    protected IxorTalkConfigProperties ixorTalkConfigProperties;

    @Inject
    protected ObjectMapper objectMapper;
    
    @LocalServerPort
    protected int port;

    @Value("${server.context-path}")
    protected String contextPath;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Inject
    private MongoClient mongoClient;

    @Inject
    protected AssetRepository assetRepository;

    protected static AssetId parseAssetIdFromUrl(String url) {
        return assetId(substringAfter(url, ASSETS_BASE_PATH + "/"));
    }

    @Before
    public void restAssured() {
        RestAssured.port = port;
        RestAssured.basePath = contextPath;
        RestAssured.config = config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @After
    public void cleanCrudRepositories() {
        stream(crudRepositories).forEach(crudRepository -> crudRepository.deleteAll());
    }

    protected void  saveToMongoDirectly(Asset asset) throws Exception {
        asset.generateAssetId();

        MongoDatabase db = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = db.getCollection("asset");
        //use a vanilla ObjectMapper or Spring will add fancy HAL properties 

        DBObject assetIdDBObject = (DBObject) parse(new ObjectMapper().writeValueAsString(asset.getAssetId()));
        DBObject assetPropertiesDBObject = (DBObject) parse(new ObjectMapper().writeValueAsString(asset.getAssetProperties()));

        Document assetDocument = new Document();
        assetDocument.put("_id", assetIdDBObject);
        assetDocument.put("_class", asset.getClass().getName());
        assetDocument.put("assetProperties", assetPropertiesDBObject);

        collection.insertOne(assetDocument);
    }
}
