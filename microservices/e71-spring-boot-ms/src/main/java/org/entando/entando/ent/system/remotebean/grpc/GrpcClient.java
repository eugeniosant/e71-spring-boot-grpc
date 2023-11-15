package org.entando.entando.ent.system.remotebean.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.lang.reflect.Method;
import org.entando.entando.ent.system.remotebean.IRequestResponseBodyHandler;
import org.entando.entando.ent.system.remotebean.model.Endpoint;
import org.entando.entando.ent.remotebean.grpc.RbRequest;
import org.entando.entando.ent.remotebean.grpc.RbResponse;
import org.entando.entando.ent.remotebean.grpc.RbServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author E.Santoboni
 */
@Component
public class GrpcClient implements IGrpcClient {

    private static final Logger logger = LoggerFactory.getLogger(GrpcClient.class);

    @Value("${GRPC_TLS_ACTIVE:false}")
    private boolean tlsActive;
    
    @Autowired
    private IRequestResponseBodyHandler bodyHandler;

    @Override
    public Object executeGrpcCall(Endpoint endpoint, Class<?> beanClass, Method method, Object[] args) throws Exception {
        String[] hostPort = endpoint.getGrpcHostPort().split(":");
        ManagedChannelBuilder channelBuilder = ManagedChannelBuilder.forAddress(hostPort[0], Integer.parseInt(hostPort[1]));
        if (!this.tlsActive) {
            channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        try {
            RbServiceGrpc.RbServiceBlockingStub stub = this.getStub(channel);
            RbRequest request = this.getBodyHandler().createGrpcRequest(beanClass, method, args);
            logger.error("-----1------->>>>>>>>> " + request.getMethod());
            logger.error("-----2------->>>>>>>>> " + request.getParameters());
            logger.error("-----3------->>>>>>>>> " + request.getBeanClass());
            RbResponse response = stub.invoke(request);
            logger.error("----4---rrrrrrrr----->>>>>>>>> " + response.getResult());
            return this.getBodyHandler().parseGrpcResult(method, response);
        } catch (Exception e) {
            logger.error("Error calling endpoint {}", endpoint.getGrpcHostPort(), e);
            throw e;
        } finally {
            channel.shutdown();
        }
    }
    
    protected RbServiceGrpc.RbServiceBlockingStub getStub(ManagedChannel channel) {
        return RbServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public Class<?> getBeanClass() {
        return null;
    }
    
    protected IRequestResponseBodyHandler getBodyHandler() {
        return bodyHandler;
    }

    public void setBodyHandler(IRequestResponseBodyHandler bodyHandler) {
        this.bodyHandler = bodyHandler;
    }

}
