package com.qqkj.inspection.inspection.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PatrolClue extends Patrol{

    /**
     * 巡察对象名
     */
    @ApiModelProperty(value = "巡察对象名")
    private String unitName;
    private List<TClue> clues;
    @ApiModelProperty(value = "巡察任务id")
    private String patrolunitid;
}
