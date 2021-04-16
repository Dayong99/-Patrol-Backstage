package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TLabel;
import com.qqkj.inspection.inspection.mapper.TLabelMapper;
import com.qqkj.inspection.inspection.service.ITLabelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Service
public class TLabelServiceImpl extends ServiceImpl<TLabelMapper, TLabel> implements ITLabelService {

    @Autowired
    TLabelMapper labelMapper;
    @Override
    public IPage<TLabel> query(Page<TLabel> page,QueryWrapper<TLabel> labelQueryWrapper, int labletype) {
        return labelMapper.query(page,labelQueryWrapper,labletype);
    }

}
