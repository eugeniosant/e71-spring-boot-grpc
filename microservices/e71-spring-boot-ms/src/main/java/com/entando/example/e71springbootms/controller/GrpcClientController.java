package com.entando.example.e71springbootms.controller;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import java.util.List;
//import javax.annotation.security.RolesAllowed;
import org.entando.entando.ent.system.remotebean.annotation.ProxyAutowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GrpcClientController {
    
    @ProxyAutowired
    private IGroupManager groupManager;
    
    @CrossOrigin
    @GetMapping("/api/groups")
    // @RolesAllowed("role1")
    public MyGroupResponse getExample() {
        return new MyGroupResponse(this.groupManager);
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

