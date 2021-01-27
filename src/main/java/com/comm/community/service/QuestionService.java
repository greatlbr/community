package com.comm.community.service;

import com.comm.community.dto.PaginationDTO;
import com.comm.community.dto.QuestionDTO;
import com.comm.community.mapper.QuestionMapper;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.Question;
import com.comm.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//使用QuestionService的目的是在里面可以不仅仅使用QuestionMapper还可以同时使用UserMapper，起到组装的作用
// 当一个请求需要组装user和question的时候，就需要引入中间层来做这件事情，习惯性的把中间层叫service
//（多表查询影响速度,最好sql做最简单的增删查改，逻辑部分交给Java处理？）

@Service
public class QuestionService {
    //需要依赖的是index依赖的
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired(required=false)
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;//////////////////////////////

        Integer totalCount = questionMapper.count();

        if (totalCount % size ==0){////////////////////////////////
            totalPage = totalCount /size;
        }else totalPage = totalCount /size +1;/////////////////////////

        //paginationDTO.setPagination(totalCount, page, size);/////////////////////////////////

        if (page<1){
            page = 1;
        }

        //if (page > paginationDTO.getTotalPage()){/////////////////////
        //    page = paginationDTO.getTotalPage();/////////////////////
        //}/////////////////////

        if (page > totalPage){//////////////////////////////////////
            page = totalPage;///////////////////////////////////////
        }

        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);

        List<Question> questions =  questionMapper.list(offset, size);//通过questionMapper.list()查到所有的question对象
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question:questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            //此时需要把question转换成dto
            QuestionDTO questionDTO = new QuestionDTO();
            //古老方法是questionDTO.setId(question.getId());
            BeanUtils.copyProperties(question, questionDTO);//快速把question对象上的所有属性copy到questionDTO,(把Question和User再共同封装到一个类里面)
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;//////////////////////////////

        Integer totalCount = questionMapper.countByUserId(userId);

        if (totalCount % size ==0){////////////////////////////////
            totalPage = totalCount /size;
        }else totalPage = totalCount /size +1;/////////////////////////

        //paginationDTO.setPagination(totalCount, page, size);/////////////////////////////////

        if (page<1){
            page = 1;
        }

        //if (page > paginationDTO.getTotalPage()){/////////////////////
        //    page = paginationDTO.getTotalPage();/////////////////////
        //}/////////////////////

        if (page > totalPage){//////////////////////////////////////
            page = totalPage;///////////////////////////////////////
        }

        paginationDTO.setPagination(totalPage, page);////////////////////////////

        //size*(page - 1)
        Integer offset = size * (page - 1);

        List<Question> questionList =  questionMapper.listByUserId(userId, offset, size);//通过questionMapper.list()查到所有的question对象
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question:questionList){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            //此时需要把question转换成dto
            QuestionDTO questionDTO = new QuestionDTO();
            //古老方法是questionDTO.setId(question.getId());
            BeanUtils.copyProperties(question, questionDTO);//快速把question对象上的所有属性copy到questionDTO,(把Question和User再共同封装到一个类里面)
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);//把question赋值到questiondto
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null){
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        }else {
            //更新
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }
}
