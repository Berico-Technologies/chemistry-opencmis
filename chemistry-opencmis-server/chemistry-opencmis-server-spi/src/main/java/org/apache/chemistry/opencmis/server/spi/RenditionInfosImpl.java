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
package org.apache.chemistry.opencmis.server.spi;

import java.math.BigInteger;

/**
 * Implementation of the {@link RenditionInfo} interface.
 * 
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public class RenditionInfosImpl implements RenditionInfo {

  private String fId;
  private String fContentType;
  private String fKind;
  private String fTitle;
  private BigInteger fLength;

  public String getId() {
    return fId;
  }

  public void setId(String id) {
    fId = id;
  }

  public String getContenType() {
    return fContentType;
  }

  public void setContentType(String contentType) {
    fContentType = contentType;
  }

  public String getKind() {
    return fKind;
  }

  public void setKind(String kind) {
    fKind = kind;
  }

  public String getTitle() {
    return fTitle;
  }

  public void setTitle(String title) {
    fTitle = title;
  }

  public BigInteger getLength() {
    return fLength;
  }

  public void setLength(BigInteger length) {
    fLength = length;
  }
}