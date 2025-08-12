package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.entity.Link;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员友链管理", description = "管理员友链管理相关接口")
@RestController
@RequestMapping("/content/link")
public class AdminLinkController {

    @Resource
    private LinkService linkService;

    @Operation(summary = "获取友链列表", description = "分页获取友链列表，支持按名称和状态搜索")
    @GetMapping("/list")
    public ResponseResult<?> getLink(
            @Parameter(description = "分页参数") PageQuery pageQuery, 
            @Parameter(description = "友链名称") String name, 
            @Parameter(description = "友链状态") String status) {
        return ResponseResult.okResult(linkService.getLinkList(pageQuery, name, status));
    }

    @Operation(summary = "添加友链", description = "添加新的友链")
    @PostMapping
    public ResponseResult<?> addLink(
            @Parameter(description = "友链信息") @RequestBody Link link) {
        linkService.save(link);
        return ResponseResult.okResult();
    }

    @Operation(summary = "根据ID获取友链", description = "根据友链ID获取友链详细信息")
    @GetMapping("/{id}")
    public ResponseResult<?> getLinkById(
            @Parameter(description = "友链ID") @PathVariable Long id) {
        Link link = linkService.getById(id);
        return ResponseResult.okResult(link);
    }

    @Operation(summary = "更新友链", description = "更新友链信息")
    @PutMapping
    public ResponseResult<?> updateLink(
            @Parameter(description = "友链信息") @RequestBody Link link) {
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除友链", description = "根据友链ID删除友链")
    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteLink(
            @Parameter(description = "友链ID") @PathVariable Long id) {
        linkService.delete(id);
        return ResponseResult.okResult();
    }
}
