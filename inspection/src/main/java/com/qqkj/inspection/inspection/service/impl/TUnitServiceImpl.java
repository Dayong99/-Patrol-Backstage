package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TUnit;
import com.qqkj.inspection.inspection.mapper.TUnitMapper;
import com.qqkj.inspection.inspection.service.ITUnitService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 巡察对象表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Service
public class TUnitServiceImpl extends ServiceImpl<TUnitMapper, TUnit> implements ITUnitService {

}
