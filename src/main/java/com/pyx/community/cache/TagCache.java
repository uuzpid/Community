package com.pyx.community.cache;

import com.pyx.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagCache {
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("js","php","css","java","html","node","python","js","php","css","java","html","node","python","js","php","css","java","html","node","python"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("spring","koa","struts","express"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux","nginx","apache","unix"));
        tagDTOS.add(server);

        TagDTO dataBase = new TagDTO();
        dataBase.setCategoryName("数据库");
        dataBase.setTags(Arrays.asList("mysql","sql","mongodb","oracle"));
        tagDTOS.add(dataBase);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git","github","maven","idea"));
        tagDTOS.add(tool);
        return tagDTOS;
    }

    /**
     * 校验标签是否合法
     */
    public static String filterInvalid(String tags){
        //分出所有的标签，并放在数组中
        String[] strings = StringUtils.split(tags, ",");


        List<TagDTO> tagDTOS = get();//获取主题和标签所有内容
        //根据主题获取规定的所有的标签，并装到集合中
        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());

        //将所有的标签进行判断，判断后将所有不符合规定的标签连接成string返回
        String invalid = Arrays.stream(strings).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
