package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 巡察表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Mapper
public interface TPatrolMapper extends BaseMapper<TPatrol> {
    IPage<Patrol> queryPatrol(Page<Patrol> page, @Param(Constants.WRAPPER) QueryWrapper<Patrol> patrolQueryWrapper);

    IPage<PatrolSpecial> getList(Page<PatrolSpecial> page, @Param("year") Integer year,
                                 @Param("session") Integer session, @Param("round") Integer round,
                                 @Param("groupName") String groupName, @Param("unitName") String unitName, @Param("department") String department);

    List<PatrolSpecial> getList2(@Param("year") Integer year,
                                 @Param("session") Integer session, @Param("round") Integer round,
                                 @Param("groupName") String groupName, @Param("unitName") String unitName, @Param("department") String department);


    IPage<Patrol> getAll(Page<Patrol> page, @Param("year") Integer year,
                         @Param("session") Integer session, @Param("round") Integer round,
                         @Param("groupName") Integer groupName, @Param("unitName") String unitName);

    List<Patrol> getAll2(@Param("year") Integer year,
                         @Param("session") Integer session, @Param("round") Integer round,
                         @Param("groupName") Integer groupName, @Param("unitName") String unitName);

    IPage<TPatrolLixing> getLixing(Page<TPatrolLixing> page, @Param("year") Integer year,
                                   @Param("session") Integer session, @Param("round") Integer round,
                                   @Param("groupName") Integer groupName, @Param("unitName") String unitName, @Param("lixingType") Integer lixingType);

    List<TPatrolLixing> getLixing2(@Param("year") Integer year,
                                   @Param("session") Integer session, @Param("round") Integer round,
                                   @Param("groupName") Integer groupName, @Param("unitName") String unitName, @Param("lixingType") Integer lixingType);

    IPage<PatrolClue> getClue(Page<PatrolClue> page, @Param("year") Integer year,
                              @Param("session") Integer session, @Param("round") Integer round,
                              @Param("groupName") String groupName, @Param("unitName") String unitNam
            , @Param("discipline") String discipline, @Param("end") Integer end,@Param("problem")String problem);

    List<PatrolClue> getClue2(@Param("year") Integer year, @Param("session") Integer session, @Param("round") Integer round,
                              @Param("groupName") String groupName, @Param("unitName") String unitNam, @Param("discipline") String discipline, @Param("end") Integer end);

    List<PatrolClue> getClues(@Param("year") Integer year,
                              @Param("session") Integer session, @Param("round") Integer round,
                              @Param("groupName") String groupName, @Param("unitName") String unitName);

}
