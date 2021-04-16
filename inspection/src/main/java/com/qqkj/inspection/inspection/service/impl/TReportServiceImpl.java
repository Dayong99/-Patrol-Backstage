package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TReport;
import com.qqkj.inspection.inspection.mapper.TMaterialMapper;
import com.qqkj.inspection.inspection.mapper.TPatrolUnitMapper;
import com.qqkj.inspection.inspection.mapper.TReportMapper;
import com.qqkj.inspection.inspection.service.ITReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 巡察报告表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@Service
public class TReportServiceImpl extends ServiceImpl<TReportMapper, TReport> implements ITReportService {

}
