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
package org.apache.chemistry.opencmis.commons.exceptions;

import java.math.BigInteger;

/**
 * CMIS Runtime Exception.
 */
public class CmisRuntimeException extends CmisBaseException {

    private static final long serialVersionUID = 1L;
    public static final String EXCEPTION_NAME = "runtime";

    public CmisRuntimeException() {
        super();
    }

    public CmisRuntimeException(String message, BigInteger code, Throwable cause) {
        super(message, code, cause);
    }

    public CmisRuntimeException(String message, String errorContent) {
        super(message, errorContent);
    }

    public CmisRuntimeException(String message, BigInteger code) {
        super(message, code);
    }

    public CmisRuntimeException(String message, BigInteger code, String errorContent) {
        super(message, code, errorContent);
    }
    
    public CmisRuntimeException(String message, String errorContent, Throwable cause) {
        super(message, errorContent, cause);
    }

    public CmisRuntimeException(String message, Throwable cause) {
        super(message, BigInteger.ZERO, cause);
    }

    public CmisRuntimeException(String message) {
        super(message, BigInteger.ZERO);
    }
    
    @Override
    public String getExceptionName() {
        return EXCEPTION_NAME;
    }
}
