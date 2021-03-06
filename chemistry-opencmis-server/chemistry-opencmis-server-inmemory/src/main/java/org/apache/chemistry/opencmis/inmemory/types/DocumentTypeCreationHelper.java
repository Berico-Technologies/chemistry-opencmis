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

package org.apache.chemistry.opencmis.inmemory.types;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;

public class DocumentTypeCreationHelper {

    private static final List<TypeDefinition> defaultTypes = createCmisDefaultTypes();

    private DocumentTypeCreationHelper() {
    }

    public static List<TypeDefinition> createMapWithDefaultTypes() {
        List<TypeDefinition> typesList = new LinkedList<TypeDefinition>();
        typesList.addAll(defaultTypes);
        return typesList;
    }

    public static final List<TypeDefinition> getDefaultTypes() {
        return defaultTypes;
    }

    private static List<TypeDefinition> createCmisDefaultTypes() {
        List<TypeDefinition> typesList = new LinkedList<TypeDefinition>();

        // create root types:
        TypeDefinition cmisType = InMemoryDocumentTypeDefinition.getRootDocumentType();
        typesList.add(cmisType);

        cmisType = InMemoryFolderTypeDefinition.getRootFolderType();
        typesList.add(cmisType);

        // cmisType = RelationshipTypeDefinition.getRootRelationshipType();
        // typesList.add(cmisType);
        //
        // cmisType = PolicyTypeDefinition.getRootPolicyType();
        // typesList.add(cmisType);

        return typesList;
    }

    /**
     * create root types and a collection of sample types
     *
     * @return typesMap map filled with created types
     */
    public static List<TypeDefinition> createDefaultTypes() {
        List<TypeDefinition> typesList = createCmisDefaultTypes();

        return typesList;
    }

    public static void setBasicPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions) {

        PropertyStringDefinitionImpl propS = PropertyCreationHelper.createStringDefinition(PropertyIds.NAME,
                "CMIS Name Property", Updatability.READWRITE);
        propS.setIsRequired(true);
        propertyDefinitions.put(propS.getId(), propS);

        PropertyIdDefinitionImpl propId = PropertyCreationHelper.createIdDefinition(PropertyIds.OBJECT_ID,
                "CMIS Object Id Property", Updatability.READONLY);
        propertyDefinitions.put(propId.getId(), propId);

        propId = PropertyCreationHelper.createIdDefinition(PropertyIds.OBJECT_TYPE_ID, "CMIS Object Type Id Property", Updatability.ONCREATE);
        propId.setIsRequired(true);
        propertyDefinitions.put(propId.getId(), propId);

        propId = PropertyCreationHelper.createIdDefinition(PropertyIds.BASE_TYPE_ID, "CMIS Base Type Id Property", Updatability.READONLY);
        propertyDefinitions.put(propId.getId(), propId);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.CREATED_BY, "CMIS Created By Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

        PropertyDateTimeDefinitionImpl propD = PropertyCreationHelper.createDateTimeDefinition(
                PropertyIds.CREATION_DATE, "CMIS Creation Date Property", Updatability.READONLY);
        propertyDefinitions.put(propD.getId(), propD);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.LAST_MODIFIED_BY,
                "CMIS Last Modified By Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

        propD = PropertyCreationHelper.createDateTimeDefinition(PropertyIds.LAST_MODIFICATION_DATE,
                "CMIS Last Modification Date Property", Updatability.READONLY);
        propertyDefinitions.put(propD.getId(), propD);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.CHANGE_TOKEN, "CMIS Change Token Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);
    }

    public static void setBasicDocumentPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions) {

        setBasicPropertyDefinitions(propertyDefinitions);
        PropertyBooleanDefinitionImpl propB = PropertyCreationHelper.createBooleanDefinition(PropertyIds.IS_IMMUTABLE,
                "CMIS Is Immutable Property", Updatability.READONLY);
        propertyDefinitions.put(propB.getId(), propB);

        propB = PropertyCreationHelper.createBooleanDefinition(PropertyIds.IS_LATEST_VERSION,
                "CMIS Is Latest Version Property", Updatability.READONLY);
        propertyDefinitions.put(propB.getId(), propB);

        propB = PropertyCreationHelper.createBooleanDefinition(PropertyIds.IS_MAJOR_VERSION,
                "CMIS Is Major Version Property", Updatability.READONLY);
        propertyDefinitions.put(propB.getId(), propB);

        propB = PropertyCreationHelper.createBooleanDefinition(PropertyIds.IS_LATEST_MAJOR_VERSION,
                "CMIS Is Latest Major Version Property", Updatability.READONLY);
        propertyDefinitions.put(propB.getId(), propB);

        PropertyStringDefinitionImpl propS = PropertyCreationHelper.createStringDefinition(PropertyIds.VERSION_LABEL,
                "CMIS Version Label Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

        PropertyIdDefinitionImpl propId = PropertyCreationHelper.createIdDefinition(PropertyIds.VERSION_SERIES_ID,
                "CMIS Version Series Id Property", Updatability.READONLY);
        propertyDefinitions.put(propId.getId(), propId);

        propB = PropertyCreationHelper.createBooleanDefinition(PropertyIds.IS_VERSION_SERIES_CHECKED_OUT,
                "CMIS Is Version Series Checked Out Property", Updatability.READONLY);
        propertyDefinitions.put(propB.getId(), propB);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.VERSION_SERIES_CHECKED_OUT_BY,
                "CMIS Version Series Checked Out By Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

        propId = PropertyCreationHelper.createIdDefinition(PropertyIds.VERSION_SERIES_CHECKED_OUT_ID,
                "CMIS Version Series Checked Out Id Property", Updatability.READONLY);
        propertyDefinitions.put(propId.getId(), propId);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.CHECKIN_COMMENT,
                "CMIS Checkin Comment Property", Updatability.READONLY);
        // read-only, because
        // not set as property
        propertyDefinitions.put(propS.getId(), propS);

        PropertyIntegerDefinitionImpl propI = PropertyCreationHelper.createIntegerDefinition(
                PropertyIds.CONTENT_STREAM_LENGTH, "CMIS Content Stream Length Property", Updatability.READONLY);
        propertyDefinitions.put(propI.getId(), propI);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.CONTENT_STREAM_MIME_TYPE,
                "CMIS Content Stream Mime Type Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

        propS = PropertyCreationHelper.createStringDefinition(PropertyIds.CONTENT_STREAM_FILE_NAME,
                "CMIS Content Stream File Name Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

        propId = PropertyCreationHelper.createIdDefinition(PropertyIds.CONTENT_STREAM_ID, "CMIS Stream Id Property", Updatability.READONLY);
        propertyDefinitions.put(propId.getId(), propId);

    }

    public static void setBasicFolderPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions) {

        setBasicPropertyDefinitions(propertyDefinitions);
        PropertyIdDefinitionImpl propId = PropertyCreationHelper.createIdDefinition(PropertyIds.PARENT_ID,
                "CMIS Parent Id Property", Updatability.READONLY);
        propertyDefinitions.put(propId.getId(), propId);

        propId = PropertyCreationHelper.createIdDefinition(PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS,
                "CMIS Allowed Childe Object Type Ids Property", Updatability.READONLY);
        propId.setCardinality(Cardinality.MULTI);
        propertyDefinitions.put(propId.getId(), propId);

        PropertyStringDefinitionImpl propS = PropertyCreationHelper.createStringDefinition(PropertyIds.PATH,
                "CMIS Path Property", Updatability.READONLY);
        propertyDefinitions.put(propS.getId(), propS);

    }

    public static void setBasicPolicyPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions) {

        setBasicPropertyDefinitions(propertyDefinitions);
        PropertyStringDefinitionImpl propS = PropertyCreationHelper.createStringDefinition(PropertyIds.POLICY_TEXT,
                "CMIS Policy Text Property", Updatability.READONLY);
        propS.setIsRequired(true);
        propertyDefinitions.put(propS.getId(), propS);
    }

    public static void setBasicRelationshipPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions) {

        setBasicPropertyDefinitions(propertyDefinitions);
        PropertyIdDefinitionImpl propId = PropertyCreationHelper.createIdDefinition(PropertyIds.SOURCE_ID,
                "CMIS Source Id Property", Updatability.READONLY);
        propId.setIsRequired(true);
        propertyDefinitions.put(propId.getId(), propId);

        propId = PropertyCreationHelper.createIdDefinition(PropertyIds.TARGET_ID, "CMIS Target Id Property", Updatability.READONLY);
        propId.setIsRequired(true);
        propertyDefinitions.put(propId.getId(), propId);
    }

    public static void mergePropertyDefinitions(Map<String, PropertyDefinition<?>> existingPpropertyDefinitions,
            Map<String, PropertyDefinition<?>> newPropertyDefinitions) {
        for (String propId : newPropertyDefinitions.keySet()) {
            if (existingPpropertyDefinitions.containsKey(propId)) {
                throw new RuntimeException("You can't set a property with id " + propId
                        + ". This property id already exists already or exists in supertype");
            }
        }
        existingPpropertyDefinitions.putAll(newPropertyDefinitions);
    }

}
