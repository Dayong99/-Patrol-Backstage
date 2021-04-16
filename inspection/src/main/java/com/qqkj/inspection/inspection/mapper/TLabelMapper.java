package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TLabel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
public interface TLabelMapper extends BaseMapper<TLabel> {

    IPage<TLabel> query(Page<TLabel> page, @Param(Constants.WRAPPER) QueryWrapper<TLabel> labelQueryWrapper,int labletype);

}
