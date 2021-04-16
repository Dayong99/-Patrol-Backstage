package com.qqkj.inspection.inspection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 附件表
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String filename;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    private Integer filesize;

    /**
     * 路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filepath;

    /**
     * contentType
     */
    @TableField("contentType")
    @ApiModelProperty(value = "contentType")
    private String contentType;


}
