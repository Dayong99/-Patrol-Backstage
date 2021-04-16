package com.qqkj.inspection.common.authentication;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qqkj.inspection.common.properties.OilProperties;
import com.qqkj.inspection.common.utils.OilUtil;
import com.qqkj.inspection.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter
{
    public static void main(String[] args) {
        JWTToken jwtToken = new JWTToken(OilUtil.decryptToken("49df9485929810c72a0535c70a7cfcd7e0f5b2b36334978f1ba63d1df99f573563dfbda4ec1a0961a6b16692cb6800ee4749687aeb9332a215822724eaff2367be6991bc90f75bb8fd193db8430416931af8fc03a88a06ae58b70cded8fcf042cda0f6862c2ce3862c6a1aa03dfe5d3bdad27ce1ba382e21f0c58ea4669bda659b13f201db9e6806"));
        String username = JWTUtil.getUsername(jwtToken.getToken());
        System.out.println(username);
    }

    private static final String TOKEN = "Authorization";

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws UnauthorizedException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        OilProperties oilProperties = SpringContextUtil.getBean(OilProperties.class);
        String[] anonUrl = StringUtils.splitByWholeSeparatorPreserveAllTokens(oilProperties.getShiro().getAnonUrl(), StringPool.COMMA);

        boolean match = false;
        for (String u : anonUrl) {
            if (pathMatcher.match(u, httpServletRequest.getRequestURI()))
                match = true;
        }
        if (match){
            if (isLoginAttempt(request, response)) {
                return executeLogin(request, response);
            }
            return true;
        }

        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader(TOKEN);
        return token != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(TOKEN);
        JWTToken jwtToken = new JWTToken(OilUtil.decryptToken(token.replace("Bearer ","")));
        try {
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个 option请求，这里我们给 option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
