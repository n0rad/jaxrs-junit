/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.jaxrs.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.junit.rules.ExternalResource;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import fr.norad.jaxrs.client.server.resource.mapper.ErrorExceptionMapper;
import fr.norad.jaxrs.client.server.rest.RestBuilder;

public class RestServerRule extends ExternalResource {
    private Server server;
    private final List<Object> additionalProviders = new ArrayList<Object>();
    private final List<Interceptor<? extends Message>> inInterceptors = new ArrayList<Interceptor<? extends Message>>();
    private final List<Interceptor<? extends Message>> outInterceptors = new ArrayList<Interceptor<? extends Message>>();

    /**
     * @param listenAddress ex: http://localhost:7686
     */
    public RestServerRule(String listenAddress, Object... resource) {
        this(new RestBuilder()
                .addProvider(new JacksonJaxbJsonProvider())
                .addProvider(new ErrorExceptionMapper()), listenAddress, resource);
    }

    public RestServerRule(RestBuilder restBuilder, String listenAddress, Object... resource) {
        Map<Class<?>, Object> resources = new HashMap<Class<?>, Object>();
        for (Object obj : resource) {
            try {
                resources.put(obj.getClass(), obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        server = restBuilder.buildServer(listenAddress, resources.values());
    }

    //        restContext.addAllInInterceptor(inInterceptors);
    //        restContext.addAllOutInterceptor(outInterceptors);
    //        restContext.addAllProvider(additionalProviders);
    //    public RestServerRule addInInterceptor(Interceptor<? extends Message> inInterceptor) {
    //        inInterceptors.add(inInterceptor);
    //        return this;
    //    }
    //
    //    public RestServerRule addProvider(Object provider) {
    //        additionalProviders.add(provider);
    //        return this;
    //    }
    //
    //    public RestServerRule addOutInterceptor(Interceptor<? extends Message> outInterceptor) {
    //        outInterceptors.add(outInterceptor);
    //        return this;
    //    }

    @Override
    public void after() {
        server.stop();
    }

}
