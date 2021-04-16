package com.qqkj.inspection.inspection.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色权限
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 权限id
     */
    private Integer menuId;

    /**
     * 角色id
     */
    private Integer roleId;


}
