package com.qqkj.inspection.inspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqkj.inspection.common.authentication.JWTToken;
import com.qqkj.inspection.common.authentication.JWTUtil;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.exception.OilException;
import com.qqkj.inspection.common.properties.OilProperties;
import com.qqkj.inspection.common.utils.DateUtil;
import com.qqkj.inspection.common.utils.MD5Util;
import com.qqkj.inspection.common.utils.OilUtil;
import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.entity.TUserToken;
import com.qqkj.inspection.inspection.service.ITUserService;
import com.qqkj.inspection.inspection.service.ITUserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Validated
@RestController
@Slf4j
public class LoginController {


    //12小时后过期
    private final static int EXPIRE = 3600 * 12;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OilProperties properties;

    @Autowired
    private ITUserTokenService userTokenService;

//    @Autowired
//    private ISysCaptchaService sysCaptchaService;

    @Autowired
    ITUserService userService;

    public static void main(String[] args) {
        String passworld= MD5Util.encrypt("123456");
        System.out.println(passworld);
    }

    @PostMapping("/login")
//    @Limit(key = "login", period = 60, count = 20, name = "登录接口", prefix = "limit")
    public ResultVO login(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password, HttpServletRequest request) throws Exception {
        password = MD5Util.encrypt(username, password);
        log.info("登录账号为 :"+username+"登录密码为 :"+password);

        final String errorMessage = "用户名或密码错误";
        LambdaQueryWrapper<TUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TUser::getUsername, username);
        TUser user = userService.getOne(lambdaQueryWrapper);

        if (user == null)
            throw new OilException(errorMessage);
        if (!StringUtils.equals(user.getPassword(), password))
            throw new OilException(errorMessage);

        String token = OilUtil.encryptToken(JWTUtil.sign(username, password));
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(properties.getShiro().getJwtTimeOut());
        String expireTimeStr = DateUtil.formatFullTime(expireTime);
        JWTToken jwtToken = new JWTToken(token, expireTimeStr);
        this.saveTokenToMysql(user, jwtToken, request);
        Map<String, Object> userInfo = this.generateUserInfo(jwtToken, user);
        Set<String>rolename=userService.rolename(username);
        userInfo.put("role",rolename);
        return ResultVO.success(userInfo);
    }

    @GetMapping("logout")
    public ResultVO logout(@NotNull(message = "{required}") Long id) {
        userTokenService.logout(id);
        return ResultVO.success();
    }

    private void saveTokenToMysql(TUser user, JWTToken token, HttpServletRequest request) throws Exception {
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = DateUtil.getDateByFullTimePattern(token.getExipreAt() );

        //判断是否生成过token
        TUserToken tokenEntity = userTokenService.getOne(new LambdaQueryWrapper<TUserToken>().eq(TUserToken::getUserId, user.getId()));
        if(tokenEntity == null){
            tokenEntity = new TUserToken();
            tokenEntity.setUserId(user.getId());
            tokenEntity.setToken(token.getToken());
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //保存token
            userTokenService.save(tokenEntity);
        }else{
            tokenEntity.setToken(token.getToken());
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //更新token
            userTokenService.updateById(tokenEntity);
        }
    }

    /**
     * 生成前端需要的用户信息，包括：
     * 1. token
     * 2. Vue Router
     * 3. 用户角色
     * 4. 用户权限
     * 5. 前端系统个性化配置信息
     * @param token token
     * @param user  用户信息
     * @return UserInfo
     */
    private Map<String, Object> generateUserInfo(JWTToken token, TUser user) {
        String username = user.getUsername();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("token", token.getToken());
        userInfo.put("id", user.getId());
        userInfo.put("exipreTime", token.getExipreAt());

        user.setPassword("it's a secret");
        userInfo.put("user", user);
        return userInfo;
    }
}
