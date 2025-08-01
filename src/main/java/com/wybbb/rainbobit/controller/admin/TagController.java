package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.PageQuery;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.pojo.entity.Tag;
import com.wybbb.rainbobit.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult list(TagDTO tagDTO, PageQuery pageQuery) {
        return ResponseResult.okResult(tagService.getTag(tagDTO, pageQuery));
    }
}
