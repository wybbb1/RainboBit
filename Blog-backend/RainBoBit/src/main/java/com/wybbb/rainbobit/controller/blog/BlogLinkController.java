package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "友情链接", description = "友情链接相关接口")
@RestController
@RequestMapping("/link")
public class BlogLinkController {

    @Resource
    private LinkService linkService;

    @Operation(summary = "获取所有友链", description = "获取所有可用的友情链接列表")
    @GetMapping("/getAllLink")
    public ResponseResult getAllLink(){
        return ResponseResult.okResult(linkService.getAllLink());
    }

}
