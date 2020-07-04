package com.pyx.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//这个对象包裹的是页面需要的元素
//
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;//是否有向前按钮 默认是false
    private boolean showFirstPage;//是否有第一页按钮
    private boolean showNext;//是否有下一页按钮
    private boolean showEndPage;//是否有末页按钮
    private Integer totalPage;//初始化定义一个总页数，总页数由每页数量size和总共多少条数据totalCount算出

    private Integer currentPage;//当前页是第几页。收集这个需要将当前页按钮加深展示
    private List<Integer> pages = new ArrayList<>();//返回一个列表，包含了页数，例如 1，2，3，4，5

    public void setPagination(Integer totalCount, Integer page, Integer size) {

        if (totalCount % size == 0) {//两种情况
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        this.currentPage = page;

        /**
         * 列表展示规则，这里默认前后展示当前页面的前三个页面和后三个页面
         */
        pages.add(page);//当前页面肯定要放入这个展示的列表中
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }
            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        /**
         * 是否展示上一页
         */
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }

        /**
         * 是否展示下一页
         */
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }

        /**
         * 是否展示第一页
         */
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        /**
         * 是否展示最后一页
         */
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
