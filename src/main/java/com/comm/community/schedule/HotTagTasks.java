package com.comm.community.schedule;

import com.comm.community.cache.HotTagCache;
import com.comm.community.mapper.QuestionMapper;
import com.comm.community.model.Question;
import com.comm.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {
    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired
    private HotTagCache hotTagCache;

    @Scheduled(fixedRate = 20000)
    //@Scheduled(cron = "0 0 1 * * *")
    public void hotTagSchedule(){//获取话题的列表，然后查tag
        int offset = 0;
        int limit = 20;
        log.info("hotTagSchedule {}", new Date());
        List<Question> list = new ArrayList<>();
        Map<String,Integer> priorities = new HashMap<>();
        while(offset ==0 || list.size() == limit){
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset,limit));
            for (Question question : list){

                String[] tags = StringUtils.split(question.getTag(),",");
                for (String tag : tags){
                    Integer priority = priorities.get(tag);
                    if (priority != null){
                        priorities.put(tag, priority + 5 + question.getCommentCount());
                    }else {
                        priorities.put(tag, 5 + question.getCommentCount());
                    }
                }

                log.info("the time is now {}", new Date());
            }
            offset += limit;
        }
        //hotTagCache.setTags(priorities);

        /*priorities.forEach(
                (k, v) ->{
                    System.out.print(k);
                    System.out.print(":");
                    System.out.println(v);
                    //System.out.println();
                }
        );*/

        hotTagCache.updateTags(priorities);
    }
}
