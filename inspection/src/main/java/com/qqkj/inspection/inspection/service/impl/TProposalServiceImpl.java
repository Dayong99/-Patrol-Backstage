package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.PatrolProposal;
import com.qqkj.inspection.inspection.entity.TProposal;
import com.qqkj.inspection.inspection.mapper.TProposalMapper;
import com.qqkj.inspection.inspection.service.ITProposalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 移交问题建议表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@Service
public class TProposalServiceImpl extends ServiceImpl<TProposalMapper, TProposal> implements ITProposalService {

    @Autowired
    private TProposalMapper proposalMapper;

    @Override
    public IPage<PatrolProposal> getProposal(Page<PatrolProposal>page,  Integer year,
                                       Integer session, Integer round,
                                       Integer groupName,String unitName,
                                             String message, String information) {
        return proposalMapper.getProposal(page,year,session,round,groupName,unitName,message,information);
    }

    @Override
    public List<PatrolProposal> getProposal2(Integer year, Integer session, Integer round, Integer groupName, String unitName, String message, String information) {
        return proposalMapper.getProposal2(year,session,round,groupName,unitName,message,information);
    }


}
