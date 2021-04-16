package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.ProblemPatrol;
import com.qqkj.inspection.inspection.entity.TProblem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 巡察问题表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
public interface TProblemMapper extends BaseMapper<TProblem> {
    IPage<ProblemPatrol> query(Page<ProblemPatrol> page,
                               @Param(Constants.WRAPPER)QueryWrapper<ProblemPatrol> patrolQueryWrapper);

    List<ProblemPatrol>query2(@Param(Constants.WRAPPER)QueryWrapper<ProblemPatrol> patrolQueryWrapper);
    List<HashMap<String, Object>> getFirstCategoryNum(@Param("year") Integer year,@Param("session") Integer session,
                                                @Param("round") Integer round, @Param("groupName") Integer groupName,
                                                @Param("unitName") String unitName, @Param("first_category") Integer first_category,
                                                @Param("sort") String sort);

    List<ProblemPatrol> listAll(@Param("sort")String sort);

    List<HashMap<String, Object>> getFirstCount(@Param("year") Integer year, @Param("session") Integer session,
                                           @Param("round") Integer round, @Param("groupName") Integer groupName,
                                           @Param("unitName") String unitName, @Param("first_category") Integer first_category,
                                           @Param("sort") String sort);

    List<HashMap<String, Object>> getTwoCategoryNum(@Param("year") Integer year, @Param("session") Integer session,
                                              @Param("round") Integer round, @Param("groupName") Integer groupName,
                                              @Param("unitName") String unitName, @Param("first_category") Integer first_category,
                                              @Param("sort") String sort,boolean b);

    List<HashMap<String, Object>> getTwoCount(@Param("year") Integer year, @Param("session") Integer session,
                                              @Param("round") Integer round, @Param("groupName") Integer groupName,
                                              @Param("unitName") String unitName, @Param("first_category") Integer first_category,
                                              @Param("sort") String sort,boolean b);

    List<HashMap<String,Object>>getMap(@Param("year") Integer year,
                                       @Param("session") Integer session, @Param("round") Integer round,
                                       @Param("groupName") Integer groupName, @Param("unitName") String unitName,
                                       @Param("firstId") Integer firstId,@Param("sort")String sort);

    HashMap<String,Object> getNums(@Param("session") Integer session, @Param("round") Integer round,@Param("firstId") Integer firstId,@Param("sort")String sort);


}
