package com.majiang.demo.service;

import com.majiang.demo.dto.PaginationDTO;
import com.majiang.demo.dto.QuestionDTO;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import com.majiang.demo.mapper.QuestionExtMapper;
import com.majiang.demo.mapper.QuestionMapper;
import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.Question;
import com.majiang.demo.model.QuestionExample;
import com.majiang.demo.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 将User作为属性额外添加使用QuestionDTO,但是在indexController中获取数据
 * 该数据是针对question表的，并且使用的是questionMapper中的方法list
 * 该方法返回的是Question，并不是QuestionDIO
 * 因此我们需要service来组装QuestionMapper和UserMapper
 * QuestionService作为中间层，去返回QuestionDTO，即List<Question> questionList = questionService.list();
 */
@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTOS = new PaginationDTO();
        //获取页面中所有的记录条数
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
        //使用方法将所有的参数传入
        paginationDTOS.setPagination(totalCount,page,size);
        //对于page越界的处理
        if (page < 1){
            page = 1;
        }
        if (page > paginationDTOS.getTotalPage()){
            page = paginationDTOS.getTotalPage();
        }

        Integer offset = size * (page - 1);
        if (offset < 0) return paginationDTOS;
        //获取所有的question目录
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //遍历所有的question，根据每条数据中的id去查询user
        for (Question question : questions) {
            //通过id查找到user
//            User user = userMapper.findById(question.getCreator());
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            //要将Question转换成QuestionDTO
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//快速将question对象中的属性拷贝到questionDTO对象上
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
//        return questionDTOList;
        paginationDTOS.setQuestions(questionDTOList);
        return paginationDTOS;
    }

    //获取个人全部的问题列表=》“我的问题”
    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        PaginationDTO paginationDTOS = new PaginationDTO();
        //获取个人页面中所有的问题条数
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(example);

        //使用方法将所有的参数传入
        paginationDTOS.setPagination(totalCount,page,size);
        //对于page越界的处理
        if (page < 1){
            page = 1;
        }
        if (page > paginationDTOS.getTotalPage()){
            page = paginationDTOS.getTotalPage();
        }

        Integer offset = size * (page - 1);
        //获取所有的question目录,(查询我的所有问题，也就是将userId传进入)
        QuestionExample example1 = new QuestionExample();
        example1.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example1,new RowBounds(offset,size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //遍历所有的question，根据每条数据中的id去查询user
        for (Question question : questions) {
            //通过id查找到user
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            //要将Question转换成QuestionDTO
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//快速将question对象中的属性拷贝到questionDTO对象上
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

//        return questionDTOList;
        paginationDTOS.setQuestions(questionDTOList);
        return paginationDTOS;
    }

    public QuestionDTO getById(Integer id) {
//        QuestionMapper自然是处理question这个表的，返回questionDTO当然不好了
//        因此还是使用Question+User的方式(QuestionDTO就是包含这两个)
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            //给CustomizeErrorCode中的message赋值=>可以通过她的接口中的getMessage()获取该message=》CustomizeException直接调用接口获取message
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            //表示这是一个新的发布问题，直接插入数据即可
            question.setGmtCreate(System.currentTimeMillis( ));
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        }else{
            //表示这是一个编辑问题操作，更新数据库即可
//            question.setGmtModified(System.currentTimeMillis());
            Question updateQuestion = new Question();//需要更新的记录
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());

            QuestionExample example = new QuestionExample();
            example.createCriteria()
                            .andIdEqualTo(question.getId());
            int update = questionMapper.updateByExampleSelective(updateQuestion, example);
            //如果我在编辑之后点击发布之前，我在另一个页面将这个问题删除了，我这里应该是更新失败的
            //更新失败应该抛出问题找不到异常
            if (update != 1){
                //更新失败
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);//这个message是重复的，每次使用都重新写很麻烦，定义一个ErrorCode来枚举
            }
        }
    }

    public void incView(Integer id) {
        //用作更新的question，如下
        //更新需要两个值；1）待更新的问题的id；2）每次递增的大小
        Question updateQuestion = new Question();
        updateQuestion.setId(id);
        updateQuestion.setViewCount(1);//每次递增1
        questionExtMapper.incView(updateQuestion);
    }
}
