package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.PatrolProposal;
import com.qqkj.inspection.inspection.entity.TProposal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 移交问题建议表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
public interface TProposalMapper extends BaseMapper<TProposal> {
    IPage<PatrolProposal>getProposal(Page<PatrolProposal>page, @Param("year") Integer year,
                                     @Param("session") Integer session, @Param("round") Integer round,
                                     @Param("groupName") Integer groupName, @Param("unitName") String unitName,
                                     @Param("message")String message,@Param("information") String information);

    List<PatrolProposal> getProposal2(@Param("year") Integer year,
                                      @Param("session") Integer session, @Param("round") Integer round,
                                      @Param("groupName") Integer groupName, @Param("unitName") String unitName,
                                      @Param("message")String message, @Param("information") String information);

   }
