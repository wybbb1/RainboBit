package com.wybbb.rainbobit.pojo.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "分页查询参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageQuery {
    @Schema(description = "页码", example = "1")
    Integer page = 1;
    @Schema(description = "每页大小", example = "10")
    Integer pageSize = 10;

}
