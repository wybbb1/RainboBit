package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员标签管理", description = "管理员标签管理相关接口")
@RestController
@RequestMapping("/content/tag")
public class AdminTagController {

    @Resource
    private TagService tagService;

    @Operation(summary = "分页获取标签", description = "分页获取标签列表，支持搜索")
    @GetMapping("/list")
    public ResponseResult page(
            @Parameter(description = "标签搜索条件") TagDTO tagDTO, 
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        return ResponseResult.okResult(tagService.getTag(tagDTO, pageQuery));
    }

    @Operation(summary = "获取所有标签", description = "获取所有标签列表")
    @GetMapping("/listAllTag")
    public ResponseResult list(){
        return ResponseResult.okResult(tagService.listAllTags());
    }

    @Operation(summary = "添加标签", description = "添加新的标签")
    @PostMapping
    public ResponseResult add(
            @Parameter(description = "标签信息") @RequestBody TagDTO tagDTO) {
        tagService.addTag(tagDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除标签", description = "根据标签ID删除标签")
    @DeleteMapping("/{id}")
    public ResponseResult delete(
            @Parameter(description = "标签ID") @PathVariable Long id){
        tagService.removeTagById(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "根据ID获取标签", description = "根据标签ID获取标签详细信息")
    @GetMapping("/{id}")
    public ResponseResult geyById(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return ResponseResult.okResult(tagService.getTagById(id));
    }

    @Operation(summary = "更新标签", description = "更新标签信息")
    @PutMapping
    public ResponseResult update(
            @Parameter(description = "标签信息") @RequestBody TagDTO tagDTO){
        tagService.updateTag(tagDTO);
        return ResponseResult.okResult();
    }
}
