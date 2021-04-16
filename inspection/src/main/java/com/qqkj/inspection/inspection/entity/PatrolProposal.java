package com.qqkj.inspection.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PatrolProposal extends TPatrol implements Serializable {

    /**
     * 巡察对象名
     */
    @ApiModelProperty(value = "巡察对象名")
    private String unitName;

    @ApiModelProperty(value = "巡察任务id")
    private String patrolunitid;
    /**
     * 移交问题建议
     */
    @TableField(exist = false)
    private TProposal proposal;


    /**
     * 专题报告材
     */
    @ApiModelProperty(value = "专题报告材料")
    private TMaterial material;

}
