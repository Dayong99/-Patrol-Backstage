package com.qqkj.inspection.inspection.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门成员表
 * </p>
 *
 * @author qqkj
 * @since 2020-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TDepartmentUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    private Integer departmentId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "成员id")
    private Integer userId;


}
