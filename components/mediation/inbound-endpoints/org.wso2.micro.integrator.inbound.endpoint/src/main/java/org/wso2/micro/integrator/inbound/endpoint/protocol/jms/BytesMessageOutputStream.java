/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.micro.integrator.inbound.endpoint.protocol.jms;

import java.io.OutputStream;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class BytesMessageOutputStream extends OutputStream {
    private final BytesMessage message;

    public BytesMessageOutputStream(BytesMessage message) {
        this.message = message;
    }

    @Override
    public void write(int b) throws JMSExceptionWrapper {
        try {
            message.writeByte((byte) b);
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws JMSExceptionWrapper {
        try {
            message.writeBytes(b, off, len);
        } catch (JMSException ex) {
            new JMSExceptionWrapper(ex);
        }
    }

    @Override
    public void write(byte[] b) throws JMSExceptionWrapper {
        try {
            message.writeBytes(b);
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }
}
