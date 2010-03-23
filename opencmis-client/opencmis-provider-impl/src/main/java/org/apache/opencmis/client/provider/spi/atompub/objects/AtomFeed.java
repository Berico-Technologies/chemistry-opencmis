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
package org.apache.opencmis.client.provider.spi.atompub.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public class AtomFeed extends AtomBase {

  private static final long serialVersionUID = 1L;

  private List<AtomEntry> fEntries = new ArrayList<AtomEntry>();

  public AtomFeed() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.spi.atompub.objects.AtomBase#getType()
   */
  @Override
  public String getType() {
    return "Atom Feed";
  }

  public List<AtomEntry> getEntries() {
    return fEntries;
  }

  public void addEntry(AtomEntry entry) {
    if (entry != null) {
      fEntries.add(entry);
    }
  }

  @Override
  public String toString() {
    return "Feed : " + getElements() + " " + fEntries;
  }
}