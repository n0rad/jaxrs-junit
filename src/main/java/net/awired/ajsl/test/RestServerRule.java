package net.awired.ajsl.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.awired.ajsl.ws.rest.RestBuilder;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.junit.rules.ExternalResource;

public class RestServerRule extends ExternalResource {
    private Map<Class<?>, Object> resources = new HashMap<Class<?>, Object>();
    private Server server;
    private String listenAddress;
    private boolean log = true;
    private final List<Object> additionalProviders = new ArrayList<Object>();
    private final List<Interceptor<? extends Message>> inInterceptors = new ArrayList<Interceptor<? extends Message>>();
    private final List<Interceptor<? extends Message>> outInterceptors = new ArrayList<Interceptor<? extends Message>>();

    /**
     * @param listenAddress
     *            ex: http://localhost:7686
     */
    public RestServerRule(String listenAddress, Object... resource) {
        this.listenAddress = listenAddress;
        for (Object obj : resource) {
            try {
                resources.put(obj.getClass(), obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public RestServerRule addInInterceptor(Interceptor<? extends Message> inInterceptor) {
        inInterceptors.add(inInterceptor);
        return this;
    }

    public RestServerRule addProvider(Object provider) {
        additionalProviders.add(provider);
        return this;
    }

    public RestServerRule addOutInterceptor(Interceptor<? extends Message> outInterceptor) {
        outInterceptors.add(outInterceptor);
        return this;
    }

    @Override
    public void before() throws Throwable {
        RestBuilder restContext = new RestBuilder();
        restContext.addAllInInterceptor(inInterceptors);
        restContext.addAllOutInterceptor(outInterceptors);
        restContext.addAllProvider(additionalProviders);
        server = restContext.buildServer(listenAddress, resources.values());
    }

    @Override
    public void after() {
        server.stop();
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

}
