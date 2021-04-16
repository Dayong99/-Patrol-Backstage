package com.qqkj.inspection.inspection.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 巡察对象表
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty(value = "巡察对象id")
    private String id;
    /**
     * 名称
     */
    @ApiModelProperty(value = "巡察对象名称")
    private String name;

    /**
     * 简称
     */
    @ApiModelProperty(value = "巡察对象简称")
    private String shorter;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String mobile;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String user;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 巡察级别：村级/区级
     */
    @ApiModelProperty(value = "巡察级别：村级/区级")
    private String sort;


}
