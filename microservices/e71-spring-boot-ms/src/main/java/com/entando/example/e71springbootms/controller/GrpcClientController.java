package com.entando.example.e71springbootms.controller;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.role.IRoleManager;
import com.agiletec.aps.system.services.role.Role;
import com.entando.en7.MsApplicationContext;
import java.util.List;
//import javax.annotation.security.RolesAllowed;
import org.entando.entando.ent.system.remotebean.annotation.ProxyAutowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

@RestController
public class GrpcClientController implements ApplicationContextAware {
    
    private static final Logger logger = LoggerFactory.getLogger(GrpcClientController.class);
    
    @ProxyAutowired
    private IGroupManager groupManager;
    
    @ProxyAutowired
    private IRoleManager roleManager;
    
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @CrossOrigin
    @GetMapping("/api/groups")
    // @RolesAllowed("role1")
    public MyGroupResponse getExample() {
        logger.error("INVOCATO SERVIZIO - groupManager {}", groupManager);
        logger.error("INVOCATO SERVIZIO - {}", roleManager);
        logger.error("INVOCATO applicationContext - {}", applicationContext);
        try {
            return new MyGroupResponse(this.roleManager);
        } catch (Exception e) {
            logger.error("Errore", e);
            throw e;
        }
        
    }

    public static class MyGroupResponse {
        private final List<Role> payload;
        public MyGroupResponse(IRoleManager roleManager) {
            this.payload = roleManager.getRoles();
        }
        public List<Role> getPayload() {
            return payload;
        }
    }
    
}

