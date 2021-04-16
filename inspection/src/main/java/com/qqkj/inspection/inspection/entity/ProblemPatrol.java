package com.qqkj.inspection.inspection.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.qqkj.inspection.common.easyExcel.util.GroupConverter;
import com.qqkj.inspection.common.easyExcel.util.RoundConverter;
import com.qqkj.inspection.common.easyExcel.util.SessionConverter;
import com.qqkj.inspection.common.easyExcel.util.YearConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
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
public class ProblemPatrol extends TProblem {

    @ApiModelProperty(value = "巡察表id")
    @ExcelIgnore
    private String patrolId;

    @ApiModelProperty(value = "届次")
    @ExcelProperty(value = "届次", index = 0,converter = SessionConverter.class)
    private Integer session;

    @ApiModelProperty(value = "年度")
    @ExcelProperty(value = "年度", index = 1,converter = YearConverter.class)
    private Integer year;

    @ApiModelProperty(value = "轮次")
    @ExcelProperty(value = "轮次", index = 2,converter = RoundConverter.class)
    private Integer round;

    @ApiModelProperty(value = "巡察对象id")
    @ExcelIgnore
    private String unitId;

    @ApiModelProperty(value = "巡察任务和巡察对象关系id")
    @ExcelIgnore
    private String patrolUnitId;

    @ApiModelProperty(value = "巡察对象名称")
    @ExcelProperty(value = "巡察对象名称", index = 3)
    private String name;

    @ApiModelProperty(value = "巡察级别：村级/区级")
    @ExcelProperty(value = "巡察级别", index = 4)
    private String sort;

    @ApiModelProperty(value = "巡察分组")
    @TableField("group_name")
    @ExcelProperty(value = "巡察分组", index = 5,converter = GroupConverter.class)
    private Integer  groupName;

    @ApiModelProperty(value = "一级标签")
    @ExcelProperty(value = "一级标签", index = 8)
    private String label1;

    @ApiModelProperty(value = "二级标签")
    @ExcelProperty(value = "二级标签", index = 9)
    private String label2;

    @ApiModelProperty(value = "其他标签")
    @ExcelProperty(value = "其他标签", index = 10)
    private String labelOther;
}
