package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.Patrol;
import com.qqkj.inspection.inspection.entity.PatrolSpecial;
import com.qqkj.inspection.inspection.entity.TPatrol;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qqkj.inspection.inspection.entity.TUser;

import java.util.List;

/**
 * <p>
 * 巡察表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
public interface ITPatrolService extends IService<TPatrol> {
    IPage<Patrol> queryPatrol(Page<Patrol> page, QueryWrapper<Patrol> patrolQueryWrapper);

    IPage<PatrolSpecial>getList(Page<PatrolSpecial>page,Integer session,
                                Integer year,Integer round,String groupName,String unitName,String department);

    List<PatrolSpecial>getList2(Integer session,
    Integer year,Integer round,String groupName,String unitName,String department);



    IPage<Patrol>getAll(Page<Patrol>page,Integer session,
                                Integer year,Integer round,Integer groupName,String unitName);
    List<Patrol>getAll2(Integer session,
                        Integer year,Integer round,Integer groupName,String unitName);
}
