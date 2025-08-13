package com.wybbb.rainbobit.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatisticsVO {
    private Integer articleCount;
    private Integer categoryCount; 
    private Integer tagCount;
    private Long totalViews;
    private Integer todayViews;
    private Integer commentCount;
    private Integer newArticles;
    private Integer newComments;
}