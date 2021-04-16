package com.qqkj.inspection.inspection.service.impl;

import com.qqkj.inspection.inspection.entity.TAttach;
import com.qqkj.inspection.inspection.mapper.TAttachMapper;
import com.qqkj.inspection.inspection.service.ITAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 附件表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@Service
public class TAttachServiceImpl extends ServiceImpl<TAttachMapper, TAttach> implements ITAttachService {
 @Autowired
    TAttachMapper attachMapper;


    @Override
    public String getName(String id) {
        TAttach attach=attachMapper.selectById(id);
        return attach.getFilename();
    }
}
