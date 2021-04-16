package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.ProblemPatrol;
import com.qqkj.inspection.inspection.entity.TProblem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 巡察问题表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
public interface ITProblemService extends IService<TProblem> {
    IPage<ProblemPatrol> query(Page<ProblemPatrol> page,
                               QueryWrapper<ProblemPatrol>patrolQueryWrapper);
    List<ProblemPatrol>query2(QueryWrapper<ProblemPatrol>patrolQueryWrapper);

    List<HashMap<String, Object>> getFirstCategoryNum(Integer year,Integer session,
                                                Integer round, Integer groupName,
                                                String unitName, Integer first_category,
                                                      String sort);

    List<ProblemPatrol> listAll(String sort);

    List<HashMap<String, Object>> getFirstCount(Integer year, Integer session,
                                            Integer round, Integer groupName,
                                            String unitName, Integer first_category,
                                           String sort);

    List<HashMap<String, Object>> getTwoCategoryNum(Integer year, Integer session,
                                              Integer round, Integer groupName,
                                              String unitName, Integer first_category,
                                              String sort,boolean b);

    List<HashMap<String, Object>> getTwoCount(Integer year, Integer session,
                                                    Integer round, Integer groupName,
                                                    String unitName, Integer first_category,
                                                    String sort,boolean b);

    List<HashMap<String,Object>>getMap(Integer year,Integer session,
                                       Integer round,Integer groupName,
                                       String unitName,Integer firstId,String sort);

    HashMap<String,Object> getNums(Integer session, Integer round, Integer firstId,String sort);

}
