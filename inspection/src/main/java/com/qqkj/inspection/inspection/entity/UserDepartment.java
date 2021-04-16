package com.qqkj.inspection.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDepartment extends TUser{
    /**
     *成员所处部门
     */
    @TableField(exist = false)
    private TDepartment department;

    /**
     * 成员的角色
     */
    private List<TRole> roles;

}
