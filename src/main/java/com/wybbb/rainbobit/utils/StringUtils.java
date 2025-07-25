package com.wybbb.rainbobit.utils;

import com.wybbb.rainbobit.constants.CategoryConstants;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class StringUtils {
    private StringUtils(){}

    public static String[] categoryCacheOut(String categoryStr){
        // 1. 检查输入字符串是否为null或空（包括只包含空格）
        if (Objects.isNull(categoryStr) || categoryStr.isBlank()) {
            return new String[]{CategoryConstants.CATEGORY_FORMAT_ERROR};
        }

        // 2. 使用下划线分割字符串
        String[] categories = categoryStr.split("_");

        // 3. 检查分割后的数组长度是否为2
        // 确保严格的 "ID_名称" 格式
        if (categories.length == 2) {
            // 进一步检查ID和名称是否为空白，虽然理论上ID和名称本身可以为空，但业务上通常不允许
            // 如果希望ID和名称不能是空字符串，可以添加以下检查：
            if (categories[0].trim().isEmpty() || categories[1].isBlank()) {
                return new String[]{"-1", CategoryConstants.CATEGORY_FORMAT_ERROR};
            }
            return new String[]{categories[0], categories[1]};
        }

        // 4. 如果长度不为2，则视为格式错误
        return new String[]{"-1", CategoryConstants.CATEGORY_FORMAT_ERROR};

    }

    public static String categoryCacheIn(CategoryVO categoryVO) {
        // 1. 检查输入对象是否为null
        if (Objects.isNull(categoryVO)) {
            return null;
        }

        // 2. 获取ID和名称
        Long id = categoryVO.getId();
        String name = categoryVO.getName();

        // 3. 检查ID和名称是否为null或空
        if (Objects.isNull(id) || id <= 0 || Objects.isNull(name) || name.isBlank()) {
            return null;
        }

        // 4. 返回格式化的字符串
        return id + "_" + name;
    }
}
