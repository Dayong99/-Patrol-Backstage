package com.qqkj.inspection.common.authentication;


import com.qqkj.inspection.common.utils.HttpContextUtil;
import com.qqkj.inspection.common.utils.IPUtil;
import com.qqkj.inspection.common.utils.OilUtil;
import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.entity.TUserToken;
import com.qqkj.inspection.inspection.mapper.TMenuMapper;
import com.qqkj.inspection.inspection.mapper.TUserMapper;
import com.qqkj.inspection.inspection.service.ShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @author qqkj
 */
@Component
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Resource
    private ShiroService shiroService;
    @Resource
    private TUserMapper userMapper;
    @Resource
    private TMenuMapper menuMapper;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**`
     * 授权模块，获取用户角色和权限
     *
     * @param token token
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection token) {
        log.info("授权模块: 查询到token"+token);
        TUser user = (TUser) token.getPrimaryPrincipal();
        String username=user.getUsername();
        log.info("授权模块: 查询到用户名"+username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 获取用户角色集
        Set<String> role=userMapper.getRole(username);
        simpleAuthorizationInfo.setRoles(role);
        List<Integer>integers=menuMapper.getParentid(username);
        // 获取用户权限集
        Set<String>perms=new HashSet<>();
        for (int i=0;i<integers.size();i++){
            Set<String>perm=menuMapper.getList(integers.get(i));
            for (String str:perm){
                log.info("查询到权限"+str);
                if(str!=null&&str.length()!=0){
                    perms.add(str);
                }

            }
        }
//        Set<String> permissionSet = userManager.getUserPermissions(username);
        simpleAuthorizationInfo.setStringPermissions(perms);
        return new SimpleAuthorizationInfo();
    }

    /**
     * 用户认证
     *
     * @param authenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        // 从 redis里获取这个 token
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        String ip = IPUtil.getIpAddr(request);
        //未加密的token
        String accessToken = (String) authenticationToken.getPrincipal();
        //加密后的token
        String encryptToken = OilUtil.encryptToken(accessToken);
        String username = JWTUtil.getUsername(accessToken);
        log.info("用户名"+username);
//        根据accessToken，查询用户信息
        TUserToken tokenEntity = shiroService.queryByToken(encryptToken);
        //token失效
        if(tokenEntity == null || tokenEntity.getExpireTime().getTime() < System.currentTimeMillis()){
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }

        //查询用户信息
        TUser user = shiroService.queryUser(tokenEntity.getUserId());
        log.info("查询到用户:"+user);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;

    }
}
