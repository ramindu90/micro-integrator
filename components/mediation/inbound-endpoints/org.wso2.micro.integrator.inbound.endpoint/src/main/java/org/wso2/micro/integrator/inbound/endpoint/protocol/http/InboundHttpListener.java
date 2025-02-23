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

package org.wso2.micro.integrator.inbound.endpoint.protocol.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.inbound.InboundEndpoint;
import org.apache.synapse.inbound.InboundProcessorParams;
import org.apache.synapse.inbound.InboundRequestProcessor;
import org.apache.synapse.transport.passthru.api.PassThroughInboundEndpointHandler;
import org.wso2.micro.integrator.inbound.endpoint.protocol.http.management.HTTPEndpointManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;

/**
 * Listener class for HttpInboundEndpoint which is trigger by inbound core and
 * responsible for start ListeningEndpoint related to given port
 */
public class InboundHttpListener implements InboundRequestProcessor {

    private static final Log log = LogFactory.getLog(InboundHttpListener.class);

    private String name;
    private int port;
    private InboundProcessorParams processorParams;

    public InboundHttpListener(InboundProcessorParams params) {
        processorParams = params;
        String portParam = params.getProperties()
                .getProperty(InboundHttpConstants.INBOUND_ENDPOINT_PARAMETER_HTTP_PORT);
        try {
            port = Integer.parseInt(portParam);
        } catch (NumberFormatException e) {
            handleException("Please provide port number as integer  instead of  port  " + portParam, e);
        }
        name = params.getName();
    }

    @Override
    public void init() {
        if (isPortUsedByAnotherApplication(port)) {
            log.warn("Port " + port + " used by inbound endpoint " + name + " is already used by another application "
                             + "hence undeploying inbound endpoint");
            throw new SynapseException("Port " + port + " used by inbound endpoint " + name + " is already used by "
                                               + "another application.");
        } else {
            HTTPEndpointManager.getInstance().startEndpoint(port, name, processorParams);
        }
    }

    @Override
    public void destroy() {
        HTTPEndpointManager.getInstance().closeEndpoint(port);
    }

    protected void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new SynapseException(msg, e);
    }

    protected boolean isPortUsedByAnotherApplication(int port) {
        if (PassThroughInboundEndpointHandler.isEndpointRunning(port)) {
            return false;
        } else {
            try {
                ServerSocket srv = new ServerSocket(port);
                srv.close();
                srv = null;
                return false;
            } catch (IOException e) {
                return true;
            }
        }
    }

    protected void destoryInbound() {
        if (processorParams.getSynapseEnvironment() != null) {
            Collection<InboundEndpoint> inboundEndpoints = processorParams.getSynapseEnvironment().
                    getSynapseConfiguration().getInboundEndpoints();
            {
                for (InboundEndpoint inboundEndpoint : inboundEndpoints) {
                    if (inboundEndpoint.getName().equals(name)) {
                        processorParams.getSynapseEnvironment().
                                getSynapseConfiguration().removeInboundEndpoint(name);
                        break;
                    }
                }
            }
        }
    }
}
