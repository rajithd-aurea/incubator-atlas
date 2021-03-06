/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.atlas;

import com.google.common.annotations.VisibleForTesting;

import com.sun.jersey.api.client.WebResource;

import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.instance.AtlasClassification.AtlasClassifications;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.EntityMutationResponse;
import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

public class AtlasEntitiesClientV2 extends AtlasBaseClient {

    public static final String ENTITY_API = BASE_URI + "v2/entity/";
    public static final String ENTITIES_API = BASE_URI + "v2/entities/";

    private static final APIInfo GET_ENTITY_BY_GUID = new APIInfo(ENTITY_API + "guid/", HttpMethod.GET, Response.Status.OK);
    private static final APIInfo CREATE_ENTITY = new APIInfo(ENTITY_API, HttpMethod.POST, Response.Status.OK);
    private static final APIInfo UPDATE_ENTITY = CREATE_ENTITY;
    private static final APIInfo UPDATE_ENTITY_BY_GUID = new APIInfo(ENTITY_API + "guid/", HttpMethod.PUT, Response.Status.OK);
    private static final APIInfo DELETE_ENTITY_BY_GUID = new APIInfo(ENTITY_API + "guid/", HttpMethod.DELETE, Response.Status.OK);
    private static final APIInfo GET_CLASSIFICATIONS = new APIInfo(ENTITY_API + "guid/%s/classifications", HttpMethod.GET, Response.Status.OK);
    private static final APIInfo ADD_CLASSIFICATIONS = new APIInfo(ENTITY_API + "guid/%s/classifications", HttpMethod.POST, Response.Status.OK);
    private static final APIInfo UPDATE_CLASSIFICATIONS = new APIInfo(ENTITY_API + "guid/%s/classifications", HttpMethod.PUT, Response.Status.OK);
    private static final APIInfo DELETE_CLASSIFICATION = new APIInfo(ENTITY_API + "guid/%s/classification/%s", HttpMethod.DELETE, Response.Status.OK);

    private static final APIInfo GET_ENTITIES = new APIInfo(ENTITIES_API + "guids/", HttpMethod.GET, Response.Status.OK);
    private static final APIInfo CREATE_ENTITIES = new APIInfo(ENTITIES_API, HttpMethod.POST, Response.Status.OK);
    private static final APIInfo UPDATE_ENTITIES = CREATE_ENTITIES;
    private static final APIInfo DELETE_ENTITIES = new APIInfo(ENTITIES_API + "guids/", HttpMethod.GET, Response.Status.OK);
    private static final APIInfo SEARCH_ENTITIES = new APIInfo(ENTITIES_API, HttpMethod.GET, Response.Status.OK);

    public AtlasEntitiesClientV2(String[] baseUrl, String[] basicAuthUserNamePassword) {
        super(baseUrl, basicAuthUserNamePassword);
    }

    public AtlasEntitiesClientV2(String... baseUrls) throws AtlasException {
        super(baseUrls);
    }

    public AtlasEntitiesClientV2(UserGroupInformation ugi, String doAsUser, String... baseUrls) {
        super(ugi, doAsUser, baseUrls);
    }

    protected AtlasEntitiesClientV2() {
        super();
    }

    @VisibleForTesting
    AtlasEntitiesClientV2(WebResource service, Configuration configuration) {
        super(service, configuration);
    }

    public AtlasEntity getEntityByGuid(String guid) throws AtlasServiceException {
        return callAPI(GET_ENTITY_BY_GUID, null, AtlasEntity.class, guid);
    }

    public EntityMutationResponse createEntity(AtlasEntity atlasEntity) throws AtlasServiceException {
        return callAPI(CREATE_ENTITY, atlasEntity, EntityMutationResponse.class);
    }

    public EntityMutationResponse updateEntity(AtlasEntity atlasEntity) throws AtlasServiceException {
        return callAPI(UPDATE_ENTITY, atlasEntity, EntityMutationResponse.class);
    }

    public EntityMutationResponse updateEntity(String guid, AtlasEntity atlasEntity) throws AtlasServiceException {
        return callAPI(UPDATE_ENTITY, atlasEntity, EntityMutationResponse.class, guid);
    }

    public AtlasEntity deleteEntityByGuid(String guid) throws AtlasServiceException {
        return callAPI(DELETE_ENTITY_BY_GUID, null, AtlasEntity.class, guid);
    }

    public AtlasClassifications getClassifications(String guid) throws AtlasServiceException {
        return callAPI(formatPath(GET_CLASSIFICATIONS, guid), null, AtlasClassifications.class);
    }

    public void addClassifications(String guid, List<AtlasClassification> classifications) throws AtlasServiceException {
        callAPI(formatPath(ADD_CLASSIFICATIONS, guid), classifications, AtlasClassifications.class);
    }

    public void updateClassifications(String guid, List<AtlasClassification> classifications) throws AtlasServiceException {
        callAPI(formatPath(UPDATE_CLASSIFICATIONS, guid), classifications, AtlasClassifications.class);
    }

    public void deleteClassifications(String guid, List<AtlasClassification> classifications) throws AtlasServiceException {
        callAPI(formatPath(GET_CLASSIFICATIONS, guid), classifications, AtlasClassifications.class);
    }

    public void deleteClassification(String guid, String classificationName) throws AtlasServiceException {
        callAPI(formatPath(DELETE_CLASSIFICATION, guid, classificationName), null, AtlasClassifications.class);
    }

    // Entities operations
    public List<AtlasEntity> getEntities(List<String> entityIds) {
        // TODO Map the query params correctly
        return null;
    }

    public List<AtlasEntity> createEntities(List<AtlasEntity> atlasEntities) throws AtlasServiceException {
        return (List<AtlasEntity>)callAPI(CREATE_ENTITIES, atlasEntities, List.class);
    }

    public List<AtlasEntity> updateEntities(List<AtlasEntity> atlasEntities) throws AtlasServiceException {
        return (List<AtlasEntity>)callAPI(UPDATE_ENTITIES, atlasEntities, List.class);
    }

    public AtlasEntity.AtlasEntities searchEntities(SearchFilter searchFilter) throws AtlasServiceException {
        return callAPI(GET_ENTITIES, null, AtlasEntity.AtlasEntities.class, searchFilter.getParams());
    }
}
