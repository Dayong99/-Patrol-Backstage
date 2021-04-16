package com.qqkj.inspection.inspection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class  PatrolSpecial extends  TPatrol implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 巡察对象名
     */
    @ApiModelProperty(value = "巡察对象名")
    private String unitName;
    @ApiModelProperty(value = "巡察任务id")
    private String patrolunitid;
    /**
     * 巡察报告
     */
    @TableField(exist = false)
    private TSpecial special;

    /**
     * 巡察材料
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "专题报告材料")
    private List<TMaterial>specialMaterials;

    @TableField(exist =false)
    @ApiModelProperty(value = "领导批示材料")
    private List<TMaterial>leaderMaterials;

    @TableField(exist = false)
    @ApiModelProperty(value = "办结材料")
    private List<TMaterial>Materials;

}
