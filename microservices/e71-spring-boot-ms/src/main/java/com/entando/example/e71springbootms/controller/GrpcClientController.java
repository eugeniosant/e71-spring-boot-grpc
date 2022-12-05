package com.entando.example.e71springbootms.controller;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.entando.en7.MsApplicationContext;
import java.util.List;
//import javax.annotation.security.RolesAllowed;
import org.entando.entando.ent.system.remotebean.annotation.ProxyAutowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class GrpcClientController {
    
    private static final Logger logger = LoggerFactory.getLogger(GrpcClientController.class);
    
    @ProxyAutowired
    private IGroupManager groupManager;
    
    @CrossOrigin
    @GetMapping("/api/groups")
    // @RolesAllowed("role1")
    public MyGroupResponse getExample() {
        logger.error("INVOCATO SERVIZIO - {}", groupManager);
        try {
            return new MyGroupResponse(this.groupManager);
        } catch (Exception e) {
            logger.error("Errore", e);
            throw e;
        }
        
    }

    public static class MyGroupResponse {
        private final List<Group> payload;
        public MyGroupResponse(IGroupManager groupManager) {
            this.payload = groupManager.getGroups();
        }
        public List<Group> getPayload() {
            return payload;
        }
    }
    
}

