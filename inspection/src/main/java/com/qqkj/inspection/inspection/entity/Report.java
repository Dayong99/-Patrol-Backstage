package com.qqkj.inspection.inspection.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Report extends TReport{

    @ApiModelProperty(value = "领导小组会报告")
    private TMaterial leadershipReport;

    @ApiModelProperty(value = "书记专题会报告")
    private TMaterial secretaryReport;

    @ApiModelProperty(value = "反馈报告")
    private TMaterial feedbackReport;

    @ApiModelProperty(value = "情况报告")
    private TMaterial situationReport;

    @ApiModelProperty(value = "线索报告")
    private TMaterial leadReport;

    @ApiModelProperty(value = "班子反馈报告")
    private  TMaterial TeamReport;

}
