package com.wybbb.rainbobit.pojo.entity;

import java.util.Date;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 友链(Link)表实体类
 *
 * @author Ra1nbot
 * @since 2025-07-25 17:47:17
 */
@Schema(description = "友链实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_link")
public class Link  {
    @Schema(description = "友链ID")
    @TableId
    private Long id;

    @Schema(description = "友链名称")
    private String name;

    @Schema(description = "友链logo")
    private String logo;

    @Schema(description = "友链描述")
    private String description;

    @Schema(description = "网站地址")
    private String address;

    @Schema(description = "审核状态（0代表审核通过，1代表审核未通过，2代表未审核）", example = "0")
    private String status;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "删除标志（0代表未删除，1代表已删除）", example = "0")
    private Integer delFlag;

}

