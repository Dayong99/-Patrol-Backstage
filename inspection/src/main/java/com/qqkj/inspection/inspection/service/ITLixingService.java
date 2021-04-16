package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TLixing;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qqkj.inspection.inspection.entity.TPatrolLixing;

import java.util.List;

/**
 * <p>
 * 立行立改及查立处表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
public interface ITLixingService extends IService<TLixing> {

    IPage<TPatrolLixing> getList(Page<TPatrolLixing> page, Integer session,
                                 Integer year, Integer round, Integer groupName, String unitName,Integer lixingType);

    List<TPatrolLixing>getList2(Integer session,
                                Integer year, Integer round, Integer groupName, String unitName,Integer lixingType);
}
