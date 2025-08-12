package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/tag")
@RestController
public class BlogTagController {

    @Resource
    private TagService tagService;

    @GetMapping("/getAllTag")
    public ResponseResult<?> getAllTag() {
        return ResponseResult.okResult(tagService.listAllTags());
    }
}
