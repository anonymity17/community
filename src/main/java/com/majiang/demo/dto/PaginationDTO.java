package com.majiang.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 希望返回的数据不止是一些问题的基本信息，还有“页码”相关新
 * 这个就是页面中的各种元素
 */
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;//"<"上一页的标识按钮，当访问第一页就不会出现这个按钮
    private boolean showFirstPage;//"<<" 能够回到第一个的按钮
    private boolean showNext;//">" 下一页的按钮
    private boolean showEndPage;//">>"能够回到最后一页的按钮
    private Integer page;//当前访问的页面编号
    private List<Integer> pages = new ArrayList<>();//页面中显示出来的所有页面编号"1 2 3 4 5"
    private Integer totalPage;//全部的页面个数


    public void setPagination(Integer totalCount, Integer page, Integer size) {
        if (totalCount % size != 0){
            totalPage = totalCount / size + 1;
        }else{
            totalPage = totalCount / size;
        }
        //对于page越界的处理
        if (page < 1){
            page = 1;
        }
        if (page > totalPage){
            page = totalPage;
        }
        this.page = page;

        //计算出pages
        pages.add(page);//添加当前的页面编号
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0){
                pages.add(0, page - i);//从头部插入，当前页page的前面
            }
            if(page + i <= totalPage){
                pages.add(page + i);//从尾部插入，当前页page的后面
            }
        }
        //是否展示上一页
        if (page == 1){
            showPrevious = false;
        }else {
            showPrevious = true;
        }
        //是否展示下一页
        if (page == totalPage){
            showNext = false;
        }else{
            showNext = true;
        }
        //是否展示回到第一页
        if (pages.contains(1)){
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }
        //是否展示回到最后一页
        if (pages.contains(totalPage)){
            showEndPage = false;
        }else{
            showEndPage = true;
        }
    }
}
