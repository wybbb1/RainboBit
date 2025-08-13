package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.vo.CategoryStatistics;
import com.wybbb.rainbobit.pojo.vo.DashboardStatisticsVO;
import com.wybbb.rainbobit.service.ArticleService;
import com.wybbb.rainbobit.service.CommentService;
import com.wybbb.rainbobit.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private StatisticsService statisticsService;
    @Resource
    private ArticleService articleService;
    @Resource
    private CommentService commentService;

    // 获取仪表盘统计数据
    @GetMapping("/statistics")
    public ResponseResult<?> getStatistics(){
        return ResponseResult.okResult(statisticsService.getDashboardStatistics());
    };
    
    // 获取最新文章
    @GetMapping("/recentArticles")
    public ResponseResult<?> getRecentArticles(@RequestParam(defaultValue = "5") int limit){
        return ResponseResult.okResult(articleService.getRecentArticles(limit));
    };

    // 获取最新评论
    @GetMapping("/recentComments")
    public ResponseResult<?> getRecentComments(@RequestParam(defaultValue = "5") int limit){
        return ResponseResult.okResult(commentService.getRecentComments(limit));
    }
}