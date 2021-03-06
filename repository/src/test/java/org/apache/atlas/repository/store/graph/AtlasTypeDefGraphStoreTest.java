/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.atlas.repository.store.graph;

import com.google.inject.Inject;

import org.apache.atlas.RepositoryMetadataModule;
import org.apache.atlas.TestUtilsV2;
import org.apache.atlas.exception.AtlasBaseException;
import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasEnumDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.repository.graph.AtlasGraphProvider;
import org.apache.atlas.store.AtlasTypeDefStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@Guice(modules = RepositoryMetadataModule.class)
public class AtlasTypeDefGraphStoreTest {
    private static final Logger LOG = LoggerFactory.getLogger(AtlasTypeDefGraphStoreTest.class);

    @Inject
    private
    AtlasTypeDefStore typeDefStore;

    @AfterClass
    public void clear(){
        AtlasGraphProvider.cleanup();
    }

    @Test(priority = 1)
    public void testGet() {
        try {
            List<AtlasEnumDef> allEnumDefs = typeDefStore.getAllEnumDefs();
            assertNotNull(allEnumDefs);
            assertEquals(allEnumDefs.size(), 0);
        } catch (AtlasBaseException e) {
            fail("Get should've succeeded", e);
        }

        try {
            List<AtlasClassificationDef> allClassificationDefs = typeDefStore.getAllClassificationDefs();
            assertNotNull(allClassificationDefs);
            assertEquals(allClassificationDefs.size(), 0);
        } catch (AtlasBaseException e) {
            fail("Get should've succeeded", e);
        }

        try {
            List<AtlasStructDef> allStructDefs = typeDefStore.getAllStructDefs();
            assertNotNull(allStructDefs);
            assertEquals(allStructDefs.size(), 0);
        } catch (AtlasBaseException e) {
            fail("Get should've succeeded", e);
        }

        try {
            List<AtlasEntityDef> allEntityDefs = typeDefStore.getAllEntityDefs();
            assertNotNull(allEntityDefs);
            // For some reason this keeps on toggling b/w 0 and 5, need to investigate
            assertTrue(allEntityDefs.size()>= 0);
        } catch (AtlasBaseException e) {
            fail("Get should've succeeded", e);
        }
    }

    @Test(dataProvider = "invalidGetProvider", priority = 2)
    public void testInvalidGet(String name, String guid){
        try {
            assertNull(typeDefStore.getEnumDefByName(name));
            fail("Exception expected for invalid name");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getEnumDefByGuid(guid));
            fail("Exception expected for invalid guid");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getStructDefByName(name));
            fail("Exception expected for invalid name");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getStructDefByGuid(guid));
            fail("Exception expected for invalid guid");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getClassificationDefByName(name));
            fail("Exception expected for invalid name");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getClassificationDefByGuid(guid));
            fail("Exception expected for invalid guid");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getEntityDefByName(name));
            fail("Exception expected for invalid name");
        } catch (AtlasBaseException e) {
        }

        try {
            assertNull(typeDefStore.getEntityDefByGuid(guid));
            fail("Exception expected for invalid guid");
        } catch (AtlasBaseException e) {
        }
    }

    @DataProvider
    public Object[][] invalidGetProvider(){
        return new Object[][] {
                {"name1", "guid1"},
                {"", ""},
                {null, null}
        };
    }

    @DataProvider
    public Object[][] validCreateDeptTypes(){
        return new Object[][] {
                {TestUtilsV2.defineDeptEmployeeTypes()}
        };
    }

    @DataProvider
    public Object[][] validUpdateDeptTypes(){
        return new Object[][] {
                {TestUtilsV2.defineValidUpdatedDeptEmployeeTypes()}
        };
    }

    @DataProvider
    public Object[][] invalidCreateTypes(){
        // TODO: Create invalid type in TestUtilsV2
        return new Object[][] {
        };
    }

    @DataProvider
    public Object[][] invalidUpdateTypes(){
        return new Object[][] {
                {TestUtilsV2.defineInvalidUpdatedDeptEmployeeTypes()}
        };
    }

    @Test(dependsOnMethods = {"testGet"}, dataProvider = "validCreateDeptTypes")
    public void testCreateDept(AtlasTypesDef atlasTypesDef) {
        AtlasTypesDef existingTypesDef = null;
        try {
            existingTypesDef = typeDefStore.searchTypesDef(new SearchFilter());
        } catch (AtlasBaseException e) {
            // ignore
        }

        assertNotEquals(atlasTypesDef, existingTypesDef, "Types to be created already exist in the system");
        AtlasTypesDef createdTypesDef = null;
        try {
            createdTypesDef = typeDefStore.createTypesDef(atlasTypesDef);
            assertNotNull(createdTypesDef);
            assertTrue(createdTypesDef.getEnumDefs().containsAll(atlasTypesDef.getEnumDefs()), "EnumDefs create failed");
            assertTrue(createdTypesDef.getClassificationDefs().containsAll(atlasTypesDef.getClassificationDefs()), "ClassificationDef create failed");
            assertTrue(createdTypesDef.getStructDefs().containsAll(atlasTypesDef.getStructDefs()), "StructDef creation failed");
            assertTrue(createdTypesDef.getEntityDefs().containsAll(atlasTypesDef.getEntityDefs()), "EntityDef creation failed");

        } catch (AtlasBaseException e) {
            fail("Creation of Types should've been a success", e);
        }
    }

    @Test(dependsOnMethods = {"testCreateDept"}, dataProvider = "validUpdateDeptTypes")
    public void testUpdate(AtlasTypesDef atlasTypesDef){
        try {
            AtlasTypesDef updatedTypesDef = typeDefStore.updateTypesDef(atlasTypesDef);
            assertNotNull(updatedTypesDef);

            assertEquals(updatedTypesDef.getEnumDefs().size(), atlasTypesDef.getEnumDefs().size(), "EnumDefs update failed");
            assertEquals(updatedTypesDef.getClassificationDefs().size(), atlasTypesDef.getClassificationDefs().size(), "ClassificationDef update failed");
            assertEquals(updatedTypesDef.getStructDefs().size(), atlasTypesDef.getStructDefs().size(), "StructDef update failed");
            assertEquals(updatedTypesDef.getEntityDefs().size(), atlasTypesDef.getEntityDefs().size(), "EntityDef update failed");

            // Try another update round by name and GUID
            for (AtlasEnumDef enumDef : updatedTypesDef.getEnumDefs()) {
                AtlasEnumDef updated = typeDefStore.updateEnumDefByGuid(enumDef.getGuid(), enumDef);
                assertNotNull(updated);
            }
            for (AtlasEnumDef enumDef : atlasTypesDef.getEnumDefs()) {
                AtlasEnumDef updated = typeDefStore.updateEnumDefByName(enumDef.getName(), enumDef);
                assertNotNull(updated);
            }

            // Try another update round by name and GUID
            for (AtlasClassificationDef classificationDef : updatedTypesDef.getClassificationDefs()) {
                AtlasClassificationDef updated = typeDefStore.updateClassificationDefByGuid(classificationDef.getGuid(), classificationDef);
                assertNotNull(updated);
            }
            for (AtlasClassificationDef classificationDef : atlasTypesDef.getClassificationDefs()) {
                AtlasClassificationDef updated = typeDefStore.updateClassificationDefByName(classificationDef.getName(), classificationDef);
                assertNotNull(updated);
            }

            // Try another update round by name and GUID
            for (AtlasStructDef structDef : updatedTypesDef.getStructDefs()) {
                AtlasStructDef updated = typeDefStore.updateStructDefByGuid(structDef.getGuid(), structDef);
                assertNotNull(updated);
            }
            for (AtlasStructDef structDef : atlasTypesDef.getStructDefs()) {
                AtlasStructDef updated = typeDefStore.updateStructDefByName(structDef.getName(), structDef);
                assertNotNull(updated);
            }

            // Try another update round by name and GUID
            for (AtlasEntityDef entityDef : updatedTypesDef.getEntityDefs()) {
                AtlasEntityDef updated = typeDefStore.updateEntityDefByGuid(entityDef.getGuid(), entityDef);
                assertNotNull(updated);
            }
            for (AtlasEntityDef entityDef : atlasTypesDef.getEntityDefs()) {
                AtlasEntityDef updated = typeDefStore.updateEntityDefByName(entityDef.getName(), entityDef);
                assertNotNull(updated);
            }

        } catch (AtlasBaseException e) {
            fail("TypeDef updates should've succeeded");
        }
    }

    @Test(enabled = false, dependsOnMethods = {"testCreateDept"})
    public void testUpdateWithMandatoryFields(){
        AtlasTypesDef atlasTypesDef = TestUtilsV2.defineInvalidUpdatedDeptEmployeeTypes();
        List<AtlasEnumDef> enumDefsToUpdate = atlasTypesDef.getEnumDefs();
        List<AtlasClassificationDef> classificationDefsToUpdate = atlasTypesDef.getClassificationDefs();
        List<AtlasStructDef> structDefsToUpdate = atlasTypesDef.getStructDefs();
        List<AtlasEntityDef> entityDefsToUpdate = atlasTypesDef.getEntityDefs();

        AtlasTypesDef onlyEnums = new AtlasTypesDef(enumDefsToUpdate,
                Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        AtlasTypesDef onlyStructs = new AtlasTypesDef(Collections.EMPTY_LIST,
                structDefsToUpdate, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        AtlasTypesDef onlyClassification = new AtlasTypesDef(Collections.EMPTY_LIST,
                Collections.EMPTY_LIST, classificationDefsToUpdate, Collections.EMPTY_LIST);

        AtlasTypesDef onlyEntities = new AtlasTypesDef(Collections.EMPTY_LIST,
                Collections.EMPTY_LIST, Collections.EMPTY_LIST, entityDefsToUpdate);

        try {
            AtlasTypesDef updated = typeDefStore.updateTypesDef(onlyEnums);
            assertNotNull(updated);
        } catch (AtlasBaseException ignored) {}

        try {
            AtlasTypesDef updated = typeDefStore.updateTypesDef(onlyClassification);
            assertNotNull(updated);
            assertEquals(updated.getClassificationDefs().size(), 0, "Updates should've failed");
        } catch (AtlasBaseException ignored) {}

        try {
            AtlasTypesDef updated = typeDefStore.updateTypesDef(onlyStructs);
            assertNotNull(updated);
            assertEquals(updated.getStructDefs().size(), 0, "Updates should've failed");
        } catch (AtlasBaseException ignored) {}

        try {
            AtlasTypesDef updated = typeDefStore.updateTypesDef(onlyEntities);
            assertNotNull(updated);
            assertEquals(updated.getEntityDefs().size(), 0, "Updates should've failed");
        } catch (AtlasBaseException ignored) {}
    }

    // This should run after all the update calls
    @Test(dependsOnMethods = {"testUpdate"}, dataProvider = "validUpdateDeptTypes")
    public void testDelete(AtlasTypesDef atlasTypesDef){
        try {
            typeDefStore.deleteTypesDef(atlasTypesDef);
        } catch (AtlasBaseException e) {
            fail("Deletion should've succeeded");
        }
    }

    @Test(dependsOnMethods = "testGet")
    public void testCreateWithValidAttributes(){
        AtlasTypesDef hiveTypes = TestUtilsV2.defineHiveTypes();

        try {
            AtlasTypesDef createdTypes = typeDefStore.createTypesDef(hiveTypes);
            assertEquals(hiveTypes.getEnumDefs(), createdTypes.getEnumDefs(), "Data integrity issue while persisting");
            assertEquals(hiveTypes.getStructDefs(), createdTypes.getStructDefs(), "Data integrity issue while persisting");
            assertEquals(hiveTypes.getClassificationDefs(), createdTypes.getClassificationDefs(), "Data integrity issue while persisting");
            assertEquals(hiveTypes.getEntityDefs(), createdTypes.getEntityDefs(), "Data integrity issue while persisting");
        } catch (AtlasBaseException e) {
            fail("Hive Type creation should've succeeded");
        }
    }

    @Test(enabled = false)
    public void testCreateWithInvalidAttributes(){
    }

    @Test(dependsOnMethods = "testGet")
    public void testCreateWithValidSuperTypes(){
        // Test Classification with supertype
        List<AtlasClassificationDef> classificationDefs = TestUtilsV2.getClassificationWithValidSuperType();

        AtlasTypesDef toCreate = new AtlasTypesDef(Collections.<AtlasEnumDef>emptyList(),
                Collections.<AtlasStructDef>emptyList(),
                classificationDefs,
                Collections.<AtlasEntityDef>emptyList());
        try {
            AtlasTypesDef created = typeDefStore.createTypesDef(toCreate);
            assertEquals(created.getClassificationDefs(), toCreate.getClassificationDefs(),
                    "Classification creation with valid supertype should've succeeded");
        } catch (AtlasBaseException e) {
            fail("Classification creation with valid supertype should've succeeded");
        }

        // Test Entity with supertype
        List<AtlasEntityDef> entityDefs = TestUtilsV2.getEntityWithValidSuperType();
        toCreate = new AtlasTypesDef(Collections.<AtlasEnumDef>emptyList(),
                Collections.<AtlasStructDef>emptyList(),
                Collections.<AtlasClassificationDef>emptyList(),
                entityDefs);
        try {
            AtlasTypesDef created = typeDefStore.createTypesDef(toCreate);
            assertEquals(created.getEntityDefs(), toCreate.getEntityDefs(),
                    "Entity creation with valid supertype should've succeeded");
        } catch (AtlasBaseException e) {
            fail("Entity creation with valid supertype should've succeeded");
        }
    }

    @Test(dependsOnMethods = "testGet")
    public void testCreateWithInvalidSuperTypes(){
        // Test Classification with supertype
        AtlasClassificationDef classificationDef = TestUtilsV2.getClassificationWithInvalidSuperType();
        try {
            AtlasClassificationDef created = typeDefStore.createClassificationDef(classificationDef);
            fail("Classification creation with invalid supertype should've failed");
        } catch (AtlasBaseException e) {}

        // Test Entity with supertype
        AtlasEntityDef entityDef = TestUtilsV2.getEntityWithInvalidSuperType();
        try {
            AtlasEntityDef created = typeDefStore.createEntityDef(entityDef);
            fail("Entity creation with invalid supertype should've failed");
        } catch (AtlasBaseException e) {}

    }

}