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
public class Patrol extends TPatrol{
    /**
     * 多个巡察对象
     */
    private List<TUnit> units;

    @ApiModelProperty(value = "巡察对象关系")
    private List<ReportPatrolUnit> reportPatrolUnits;

}
