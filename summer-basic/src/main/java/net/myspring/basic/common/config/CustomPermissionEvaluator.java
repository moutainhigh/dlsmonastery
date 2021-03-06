package net.myspring.basic.common.config;

import net.myspring.basic.common.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean hasPermission(Authentication authentication, Object o, Object permission) {
        String accountId = RequestUtils.getAccountId();
        if (!RequestUtils.getAdmin()) {
            String key = "authorityCache:" + accountId;
            List<String> permissionList = (List<String>) redisTemplate.opsForValue().get(key);
            if (!permissionList.contains(permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return true;
    }
}
