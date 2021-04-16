package com.qqkj.inspection.common.handler;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.exception.EnterOilException;
import com.qqkj.inspection.common.exception.LimitAccessException;
import com.qqkj.inspection.common.exception.OilException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler
{
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO handleException(Exception e)
    {
        log.error("系统内部异常，异常信息：", e);
        return ResultVO.error(50000, "系统内部异常");
    }

    @ExceptionHandler(value = OilException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO handleParamsInvalidException(OilException e)
    {
        log.error("系统错误：{}", e.getMessage());
        String message = e.getMessage();
        message = !StringUtils.isNotBlank(message) ? "系统内部异常" : message;
        return ResultVO.error(50000, message);
    }

    @ExceptionHandler(value = EnterOilException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO handleParamsInvalidException(EnterOilException e)
    {
        log.error("系统错误：{}", e.getMessage());
        String message = e.getMessage();
        message = !StringUtils.isNotBlank(message) ? "系统内部异常" : message;
        return ResultVO.error(50000, message);
    }

    /**
     * 统一处理请求参数校验(实体对象传参)
     *
     * @param e BindException
     * @return OilResponse
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO validExceptionHandler(BindException e)
    {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors)
        {
            message.append(error.getField()).append(error.getDefaultMessage()).append(StringPool.COMMA);
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));

        return ResultVO.error(50000, message.toString());

    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResultVO handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException", e);
        return ResultVO.error(50000,e.toString());
    }

    /**
     * 统一处理请求参数校验(普通传参)
     *
     * @param e ConstraintViolationException
     * @return OilResponse
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO handleConstraintViolationException(ConstraintViolationException e)
    {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations)
        {
            Path path = violation.getPropertyPath();
            String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), StringPool.DOT);
            message.append(pathArr[1]).append(violation.getMessage()).append(StringPool.COMMA);
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return ResultVO.error(50000, message.toString());
    }

    @ExceptionHandler(value = LimitAccessException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResultVO handleLimitAccessException(LimitAccessException e)
    {
        log.warn(e.getMessage());
        return ResultVO.error(50000, e.toString());
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleUnauthorizedException(Exception e)
    {
        log.error("权限不足，{}", e.getMessage());
    }
}
