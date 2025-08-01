package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenusController {

    @Resource
    private MenuService menuService;

    @GetMapping("/getRouters")
    public ResponseResult getRouters() {

        return ResponseResult.okResult(menuService.getRouters());
    }

}
