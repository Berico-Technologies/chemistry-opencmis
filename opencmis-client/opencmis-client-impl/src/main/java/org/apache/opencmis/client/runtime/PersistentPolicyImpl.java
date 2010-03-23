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
package org.apache.opencmis.client.runtime;

import org.apache.opencmis.client.api.OperationContext;
import org.apache.opencmis.client.api.Policy;
import org.apache.opencmis.client.api.objecttype.ObjectType;
import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.provider.ObjectData;

public class PersistentPolicyImpl extends AbstractPersistentFilableCmisObject implements Policy {

  /**
   * Constructor.
   */
  public PersistentPolicyImpl(PersistentSessionImpl session, ObjectType objectType,
      ObjectData objectData, OperationContext context) {
    initialize(session, objectType, objectData, context);
  }

  public String getPolicyText() {
    return getPropertyValue(PropertyIds.CMIS_POLICY_TEXT);
  }

}