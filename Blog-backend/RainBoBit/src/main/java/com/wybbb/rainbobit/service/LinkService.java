package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.entity.Link;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.LinkVO;

import java.util.List;


/**
 * 友链(Link)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-25 17:51:14
 */
public interface LinkService extends IService<Link> {

    List<LinkVO> getAllLink();

    PageResult<LinkVO> getLinkList(PageQuery pageQuery, String name, String status);

    void delete(Long id);
}

