package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
public interface TRoleMapper extends BaseMapper<TRole> {
    <T> IPage<TRole> findRolePage(Page<T> page, @Param("role") TRole role);

    List<TRole> selectRole();
}
