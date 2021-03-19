package com.comm.community.cache;

import com.comm.community.dto.HotTagDTO;
import lombok.Data;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class HotTagCache {//还可以附加关注量和回复量
    //private Map<String,Integer> tags = new HashMap<>();
    private List<String> hots = new ArrayList<>();//定义list是因为需要对别的地方输出

    public void updateTags(Map<String,Integer> tags){
        int max = 5;
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>();//构建一个优先队列

        tags.forEach((name, priority)->{
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if (priorityQueue.size() < max){//>max
                priorityQueue.add(hotTagDTO);
            }else {//<max
                HotTagDTO minHot = priorityQueue.peek();
                if (hotTagDTO.compareTo(minHot)>0){//当前的标签priority大于最小热度的标签，就放入优先队列
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });

        List<String> sortedTags = new ArrayList<>();

        HotTagDTO poll = priorityQueue.poll();
        //hots.add(poll.getName());
        while (poll != null){
            sortedTags.add(0,poll.getName());
            poll = priorityQueue.poll();
        }
        hots = sortedTags;
    }
}
