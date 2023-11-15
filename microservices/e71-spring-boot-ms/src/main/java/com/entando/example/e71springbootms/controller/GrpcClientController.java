package com.entando.example.e71springbootms.controller;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import ir.comuneroma.service.IRomaService;
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
    
    @ProxyAutowired
    private IRomaService romaService;
    
    @CrossOrigin
    @GetMapping(value = "/api/groups", produces = "application/json")
    public MyGroupResponse getExample() {
        try {
            return new MyGroupResponse(this.groupManager);
        } catch (Exception e) {
            logger.error("Errore", e);
            throw e;
        }
    }

    @CrossOrigin
    @GetMapping(value = "/api/roma", produces = "application/json")
    public String getRoma() {
        try {
            return this.romaService.getMessage();
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

