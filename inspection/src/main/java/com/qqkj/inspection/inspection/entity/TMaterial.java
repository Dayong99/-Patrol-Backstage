package com.qqkj.inspection.inspection.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 专题报告材料表
 * </p>
 *
 * @author qqkj
 * @since 2020-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 父id
     */
    @ApiModelProperty(value = "父类id")
    private String parentId;

    /**
     * 附件表id
     */
    @ApiModelProperty(value = "附件表attachid")
    private String attachId;

    @TableField(value ="filename")
    @ApiModelProperty(value ="文件名")
    private String fileName;

    @ApiModelProperty(value = "文件类型 1、领导小组会报告2、书记专题会报告3、反馈报告4、专题报告5、领导批示6、办结材料7、移交立行立改问题8、整改情况报告9、移交具体问题建议10、情况报告11、线索报告")
    private Integer filetype;

    @TableField("up_time")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime uptime;

}
