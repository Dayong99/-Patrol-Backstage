package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.PatrolSpecial;
import com.qqkj.inspection.inspection.entity.ReportPatrolUnit;
import com.qqkj.inspection.inspection.entity.TPatrolUnit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-12-17
 */
public interface TPatrolUnitMapper extends BaseMapper<TPatrolUnit> {
    IPage<ReportPatrolUnit> getList(Page<ReportPatrolUnit> page, @Param("year") Integer year,
                                    @Param("session") Integer session, @Param("round") Integer round,
                                    @Param("groupName") String groupName, @Param("unitName") String unitName);
}
