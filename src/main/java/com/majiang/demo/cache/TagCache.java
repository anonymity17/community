package com.majiang.demo.cache;

import com.majiang.demo.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/* 所有的tag存储位置*/
public class TagCache {
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("java","js","php","cpp","c","go","python","css","html","h5","ruby","c#"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("spring","springboot","express","flask"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux","docker","apache","tomcat"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql","redis","oracle","mongodb"));
        tagDTOS.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git","vim","sublime","idea","eclipse"));
        tagDTOS.add(tool);
        return tagDTOS;
    }

    /**
     * 返回无效的标签
     * @param tags
     * @return
     */
    public static String filterInvalid(String tags){
        String[] split = StringUtils.split(tags, ",");
        //获取我们缓存中所有的标签
        List<TagDTO> tagDTOS = get();
        //flatMap 可以将二维铺平成为一个
        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        //找到split中不存在于缓存中 （tagList）的标签
        String inValid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return inValid;
    }

}
