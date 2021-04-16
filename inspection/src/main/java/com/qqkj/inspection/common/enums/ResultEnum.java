package com.qqkj.inspection.common.enums;

/**
 * @author earthchen
 * @date 2018/8/31
 **/
public enum ResultEnum
{
    /**
     * 其他
     */
    INVLIDE_DATE_STRING(400, "输入日期格式不对"),

    /**
     * 其他
     */
    WRITE_ERROR(50000, "渲染界面错误"),

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),

    /**
     * 错误的请求
     */
    REQUEST_NULL(400, "请求有错误"),
    SERVER_ERROR(50000, "服务器异常"),

    /********************系统模块异常枚举*****************************/

    CAPTCHA_ERROR(500001, "验证码错误"),
    USERNAME_PASSWORD_NOT_NULL(500002, "用户名密码不能为空"),
    USERNAME_PASSWORD_ERROR(500002, "用户名密码不匹配"),
    USER_PASSWORD_RETRY_LIMIT_EXCEED(500004, "登录次数尝试过多"),
    USER_STATUS_ERROR(500003, "用户状态错误"),
    USER_ALREADY_EXISTS (500004,"该用户名已存在"),
    UPDATE_HAVE_TO_ID(500005, "更新操作必须含有id"),
    UPDATE_ERROR_KEY(500007, "重复了"),

    RESET_PASSWORD_ONLY_ADMIN(500006,"只有管理员可以重置密码"),

    /***************************************************************/
    PROJECT_NAME_OR_PROJECTID_ALEARLY_HAVE(60001, "项目编号或名称重复"),

    PROJECT_STARTDATE_MUST_LT_ENDDATE(60003,"项目开始时间必须小于结束时间"),
    /***************************************************************/


    ROLE_KEY_OR_NAME_ALREALY_HAVE(60002,"角色名或角色代码重复"),
    /***************************************************************/

    DEPT_EXSIT_USERS(70000,"该部门下存在用户，不可删除！"),
    /***************************************************************/
    RESOURCE_ALREADY_EXIST(8000,"资源名称或者权限标志重复")
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

}
