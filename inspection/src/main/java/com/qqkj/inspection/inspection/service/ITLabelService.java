package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TLabel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
public interface ITLabelService extends IService<TLabel> {
    IPage<TLabel> query(Page<TLabel> page, QueryWrapper<TLabel> labelQueryWrapper,int labletype);
}
