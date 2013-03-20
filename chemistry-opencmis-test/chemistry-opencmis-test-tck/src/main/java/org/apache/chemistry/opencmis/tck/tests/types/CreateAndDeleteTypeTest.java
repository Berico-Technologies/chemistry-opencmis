/*
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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.tck.tests.types;

import static org.apache.chemistry.opencmis.tck.CmisTestResultStatus.FAILURE;
import static org.apache.chemistry.opencmis.tck.CmisTestResultStatus.SKIPPED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.CreatablePropertyTypes;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AbstractPropertyDefinition;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.DocumentTypeDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDecimalDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyHtmlDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyUriDefinitionImpl;
import org.apache.chemistry.opencmis.tck.CmisTestResult;
import org.apache.chemistry.opencmis.tck.impl.AbstractSessionTest;

public class CreateAndDeleteTypeTest extends AbstractSessionTest {
    @Override
    public void init(Map<String, String> parameters) {
        super.init(parameters);
        setName("Create and Delete Type Test");
        setDescription("Creates a document type and deletes it again.");
    }

    @Override
    public void run(Session session) {
        if (session.getRepositoryInfo().getCmisVersion() == CmisVersion.CMIS_1_0) {
            addResult(createResult(SKIPPED, "Items are not supporetd by CMIS 1.0. Test skipped!"));
            return;
        }

        ObjectType parentType = session.getTypeDefinition(getDocumentTestTypeId());
        if (parentType.getTypeMutability() == null || !Boolean.TRUE.equals(parentType.getTypeMutability().canCreate())) {
            addResult(createResult(SKIPPED, "Test document type doesn't allow creating a sub-type. Test skipped!"));
            return;
        }

        createTypeWithoutProperties(session, parentType);
        createTypeWithProperties(session, parentType);
    }

    private void createTypeWithoutProperties(Session session, ObjectType parentType) {
        CmisTestResult failure = null;

        // define the type
        DocumentTypeDefinitionImpl newTypeDef = createDocumentTypeDefinition("tck:testid_without_properties",
                parentType);

        // create the type
        ObjectType newType = createType(session, newTypeDef);
        if (newType == null) {
            return;
        }

        // get the type
        ObjectType newType2 = null;
        try {
            newType2 = session.getTypeDefinition(newType.getId());

            // assert type definitions
            failure = createResult(FAILURE,
                    "The type definition returned by createType() doesn't match the type definition returned by getTypeDefinition()!");
            addResult(assertEquals(newType, newType2, null, failure));
        } catch (CmisObjectNotFoundException e) {
            addResult(createResult(FAILURE, "Newly created type can not be fetched. Id: " + newType.getId(), e, false));
        }

        // delete the type
        deleteType(session, newType.getId());
    }

    private void createTypeWithProperties(Session session, ObjectType parentType) {
        CmisTestResult failure = null;

        CreatablePropertyTypes cpt = session.getRepositoryInfo().getCapabilities().getCreatablePropertyTypes();
        if (cpt == null || cpt.canCreate() == null || cpt.canCreate().isEmpty()) {
            addResult(createResult(FAILURE, "Repository Info does not indicate, which property types can be created!"));
            return;
        }

        // define the type
        DocumentTypeDefinitionImpl newTypeDef = createDocumentTypeDefinition("tck:testid_with_properties", parentType);

        // add a property for each creatable property type
        for (PropertyType propType : PropertyType.values()) {
            if (!cpt.canCreate().contains(propType)) {
                continue;
            }

            newTypeDef.addPropertyDefinition(createPropertyDefinition(propType));
        }

        // create the type
        ObjectType newType = createType(session, newTypeDef);
        if (newType == null) {
            return;
        }

        // get the type
        ObjectType newType2 = null;
        try {
            newType2 = session.getTypeDefinition(newType.getId());

            // assert type definitions
            failure = createResult(FAILURE,
                    "The type definition returned by createType() doesn't match the type definition returned by getTypeDefinition()!");
            addResult(assertEquals(newType, newType2, null, failure));
        } catch (CmisObjectNotFoundException e) {
            addResult(createResult(FAILURE, "Newly created type can not be fetched. Id: " + newType.getId(), e, false));
        }

        // check properties
        List<PropertyDefinition<?>> newPropDefs = new ArrayList<PropertyDefinition<?>>();
        for (Map.Entry<String, PropertyDefinition<?>> propDef : newType.getPropertyDefinitions().entrySet()) {
            if (Boolean.FALSE.equals(propDef.getValue().isInherited())) {
                newPropDefs.add(propDef.getValue());
            }
        }

        failure = createResult(FAILURE,
                "The number of defined properties and the number of non-inherited properties don't match!");
        addResult(assertEquals(newTypeDef.getPropertyDefinitions().size(), newPropDefs.size(), null, failure));

        // check the order of the properties, which must match the order of the original type definition
        // (OpenCMIS keeps the order of the property definitions.)
        int i = 0;
        for (Map.Entry<String, PropertyDefinition<?>> propDef : newTypeDef.getPropertyDefinitions().entrySet()) {
            PropertyDefinition<?> newPropDef = newPropDefs.get(i);

            failure = createResult(FAILURE, "Property " + (i + 1) + " must be of type "
                    + propDef.getValue().getPropertyType() + " but is of type " + newPropDef.getPropertyType() + "!");
            addResult(assertEquals(propDef.getValue().getPropertyType(), newPropDef.getPropertyType(), null, failure));

            addResult(createInfoResult("Repository assigned the property '" + propDef.getValue().getId()
                    + "' the following property id: " + newPropDef.getId()));

            i++;
        }

        // delete the type
        deleteType(session, newType.getId());
    }

    private DocumentTypeDefinitionImpl createDocumentTypeDefinition(String typeId, ObjectType parentType) {
        DocumentTypeDefinitionImpl result = new DocumentTypeDefinitionImpl();

        result.setId(typeId);
        result.setBaseTypeId(parentType.getBaseTypeId());
        result.setParentTypeId(parentType.getId());
        result.setLocalName("tck:testlocal");
        result.setLocalNamespace("tck:testlocalnamespace");
        result.setDisplayName("TCK Document Type");
        result.setDescription("This is the TCK document type");
        result.setQueryName("tck:testqueryname");
        result.setIsQueryable(false);
        result.setIsFulltextIndexed(false);
        result.setIsIncludedInSupertypeQuery(true);
        result.setIsControllableAcl(false);
        result.setIsControllablePolicy(false);
        result.setIsCreatable(true);
        result.setIsFileable(true);
        result.setIsVersionable(false);
        result.setContentStreamAllowed(ContentStreamAllowed.ALLOWED);

        return result;
    }

    private AbstractPropertyDefinition<?> createPropertyDefinition(PropertyType propertyType) {
        AbstractPropertyDefinition<?> result = null;

        switch (propertyType) {
        case BOOLEAN:
            result = new PropertyBooleanDefinitionImpl();
            break;
        case ID:
            result = new PropertyIdDefinitionImpl();
            break;
        case INTEGER:
            result = new PropertyIntegerDefinitionImpl();
            break;
        case DATETIME:
            result = new PropertyDateTimeDefinitionImpl();
            break;
        case DECIMAL:
            result = new PropertyDecimalDefinitionImpl();
            break;
        case HTML:
            result = new PropertyHtmlDefinitionImpl();
            break;
        case URI:
            result = new PropertyUriDefinitionImpl();
            break;
        default:
            result = new PropertyStringDefinitionImpl();
        }

        result.setPropertyType(propertyType);
        result.setId("tck:" + propertyType.value());
        result.setLocalName("tck:local_" + propertyType.value());
        result.setLocalNamespace("tck:testlocalnamespace");
        result.setDisplayName("TCK " + propertyType.value() + " propertry");
        result.setQueryName("tck:" + propertyType.value());
        result.setDescription("TCK " + propertyType.value() + " propertry");
        result.setCardinality(Cardinality.SINGLE);
        result.setUpdatability(Updatability.READWRITE);
        result.setIsInherited(false);
        result.setIsQueryable(false);
        result.setIsOrderable(false);
        result.setIsRequired(false);
        result.setIsOpenChoice(true);

        return result;
    }
}