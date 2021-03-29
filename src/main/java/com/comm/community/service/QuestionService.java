package com.comm.community.service;

import com.comm.community.dto.PaginationDTO;
import com.comm.community.dto.QuestionDTO;
import com.comm.community.dto.QuestionQueryDTO;
import com.comm.community.exception.CustomizeErrorCode;
import com.comm.community.exception.CustomizeException;
import com.comm.community.mapper.QuestionExtMapper;
import com.comm.community.mapper.QuestionMapper;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.Question;
import com.comm.community.model.QuestionExample;
import com.comm.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//使用QuestionService的目的是在里面可以不仅仅使用QuestionMapper还可以同时使用UserMapper，起到组装的作用
// 当一个请求需要组装user和question的时候，就需要引入中间层来做这件事情，习惯性的把中间层叫service
//（多表查询影响速度,最好sql做最简单的增删查改，逻辑部分交给Java处理？）

@Service
public class QuestionService {
    //需要依赖的是index依赖的
    @Autowired(required=false)
    private QuestionMapper questionMapper;

    @Autowired(required=false)
    private UserMapper userMapper;

    @Autowired(required=false)
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(String search, String tag, Integer page, Integer size) {

        if(StringUtils.isNotBlank(search)){//这种情况不存在，只是加一个额外的验证
            String[] tags = StringUtils.split(search," ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }


        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;


        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setTag(tag);//热门话题标签

        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);//p34:56.17

        if (totalCount % size ==0){
            totalPage = totalCount /size;
        }else totalPage = totalCount /size +1;

        if (page<1){
            page = 1;
        }

        if (page > totalPage){
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        Integer offset = page < 1 ? 0 : size * (page - 1);//page小于1就让page=1

        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");//把首页话题按时间倒叙排序
        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions =  questionExtMapper.selectBySearch(questionQueryDTO);//通过questionMapper.list()查到所有的question对象
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
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;//////////////////////////////

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

        if (totalCount % size ==0){
            totalPage = totalCount /size;
        }else totalPage = totalCount /size +1;

        if (page<1){
            page = 1;
        }

        if (page > totalPage){
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        //size*(page - 1)
        Integer offset = size * (page - 1);

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions =  questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
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
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
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
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        }else {
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){//这种情况不存在，只是加一个额外的验证
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(),",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);//拿到question列表
        List<QuestionDTO> questionDTOS = questions.stream().map(q->{
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);//需要有这步前端thymleaf才能解析到变量的值（展示链接和标题），否则内容会为空（id等）
            return questionDTO;
        }).collect(Collectors.toList());//把question列表变成DTO
        return questionDTOS;
    }
}
