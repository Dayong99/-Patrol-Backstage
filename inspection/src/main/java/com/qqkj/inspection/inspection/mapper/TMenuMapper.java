package com.qqkj.inspection.inspection.mapper;

import com.qqkj.inspection.inspection.entity.TMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
public interface TMenuMapper extends BaseMapper<TMenu> {
    List<TMenu> showMenu(@Param("parentId")Integer parentId);

    List<String> showParentId();
    Set<String> getList(Integer parentid);
    List<Integer>getParentid(String username);
}
