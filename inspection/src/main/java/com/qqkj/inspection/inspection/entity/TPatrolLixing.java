package com.qqkj.inspection.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TPatrolLixing extends TPatrol {
    @TableField(exist = false)
    private TLixing lixing;
    /**
     * 巡察对象名
     */
    @ApiModelProperty(value = "巡察任务id")
    private String patrolunitid;
    @ApiModelProperty(value = "巡察对象名")
    private String unitName;
    @ApiModelProperty(value = "移交立行立改问题材料")
    private  List<TMaterial> problemMaterials;
    @ApiModelProperty(value = "整改情况报告材料")
    private  List<TMaterial>rectreportMaterials;

}
