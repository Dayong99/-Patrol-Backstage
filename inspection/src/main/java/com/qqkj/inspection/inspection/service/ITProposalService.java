package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.PatrolProposal;
import com.qqkj.inspection.inspection.entity.TProposal;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 移交问题建议表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
public interface ITProposalService extends IService<TProposal> {
    IPage<PatrolProposal>getProposal(Page<PatrolProposal>page,  Integer year,
                                     Integer session, Integer round,
                                     Integer groupName,String unitName,
                                     String message, String information);

    List<PatrolProposal> getProposal2(Integer year,
                                      Integer session, Integer round,
                                      Integer groupName, String unitName,
                                      String message, String information);
}
