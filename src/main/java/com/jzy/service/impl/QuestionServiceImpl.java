package com.jzy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jzy.dao.QuestionMapper;
import com.jzy.manager.constant.Constants;
import com.jzy.manager.exception.InvalidParameterException;
import com.jzy.manager.exception.NoMoreQuestionsException;
import com.jzy.manager.util.CodeUtils;
import com.jzy.manager.util.QuestionUtils;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.QuestionSearchCondition;
import com.jzy.model.dto.QuestionWithCreatorDto;
import com.jzy.model.entity.Question;
import com.jzy.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName QuestionServiceImpl
 * @description 游客登录问题业务实现
 * @date 2019/12/3 14:51
 **/
@Service
public class QuestionServiceImpl implements QuestionService {
    private final static Logger logger = LogManager.getLogger(QuestionServiceImpl.class);

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question getQuestionById(Long id) {
        return id == null ? null : questionMapper.getQuestionById(id);
    }

    @Override
    public Question getQuestionByContent(String content) {
        return StringUtils.isEmpty(content) ? null : questionMapper.getQuestionByContent(content);
    }

    @Override
    public List<Question> listAllQuestions() {
        return questionMapper.listAllQuestions();
    }

    @Override
    public long countAllQuestions() {
        return questionMapper.countAllQuestions();
    }

    @Override
    public Question getDefaultQuestion() {
        return QuestionUtils.getDefaultQuestion();
    }

    @Override
    public Question getRandomQuestion() {
        List<Question> questions = listAllQuestions();

        if (questions.size() > 0) {
            //数据库中有问题
            //获得随机问题id
            int randIndex = CodeUtils.oneRandomNumber(0, questions.size() - 1);
            return questions.get(randIndex);
        } else {
            //使用默认问题
            return getDefaultQuestion();
        }
    }

    @Override
    public Question getRandomDifferentQuestion(Question currentQuestion) throws NoMoreQuestionsException {
        if (currentQuestion == null){
            return getRandomQuestion();
        }

        return getRandomDifferentQuestion(currentQuestion.getContent());
    }

    @Override
    public Question getRandomDifferentQuestion(String currentQuestionContent) throws NoMoreQuestionsException {
        if (StringUtils.isEmpty(currentQuestionContent)){
            return getRandomQuestion();
        }

        List<Question> questions=listAllQuestions();
        if (questions.size() <= 1){
            //总共只有一个问题（或数据库没有问题，使用了默认问题），不找不同的问题了，抛出异常待捕获
            throw new NoMoreQuestionsException("数据库中只有一个问题!");
        }

        Question newQuestion;
        do {
            //循环直到找到与当前问题不同的问题
            newQuestion = getRandomQuestion();
        } while (newQuestion.getContent().equals(currentQuestionContent));

        return newQuestion;
    }

    @Override
    public boolean isCorrectAnswer(String questionContent, String answerInput) throws InvalidParameterException {
        if (listAllQuestions().size() == 0){
            //数据库没问题，即使用的是默认问题
            return QuestionUtils.isCorrectDefaultQuestionAnswer(answerInput);
        }

        Question question=getQuestionByContent(questionContent);
        if (question==null){
            throw new InvalidParameterException();
        }

        if (QuestionUtils.isAlwaysTrueAnswer(answerInput)) {
            //用万能答案
            return true;
        }

        if (question.getAnswer().equals(answerInput)){
            //与答案1匹配
            return true;
        }

        if (StringUtils.isEmpty(question.getAnswer2())){
            //没有第二答案
            return false;
        }

        if (question.getAnswer2().equals(answerInput)){
            //与答案2相同
            return true;
        }

        return false;
    }

    @Override
    public PageInfo<QuestionWithCreatorDto> listQuestions(MyPage myPage, QuestionSearchCondition condition) {
        PageHelper.startPage(myPage.getPageNum(), myPage.getPageSize());
        List<QuestionWithCreatorDto> questionWithCreatorDtos = questionMapper.listQuestions(condition);
        return new PageInfo<>(questionWithCreatorDtos);
    }

    @Override
    public String updateQuestionInfo(Question question) {
        Question originalQuestion = getQuestionById(question.getId());
        if (!originalQuestion.getContent().equals(question.getContent())) {
            //问题内容改过了，判断是否与已存在的记录冲突
            if (getQuestionByContent(question.getContent()) != null) {
                //修改后的问题已存在
                return "questionContentRepeat";
            }
        }

        //执行更新
        questionMapper.updateQuestionInfo(question);
        return Constants.SUCCESS;
    }

    @Override
    public String insertQuestion(Question question) {
        if (getQuestionByContent(question.getContent()) !=null) {
            //修改后的问题已存在
            return "questionContentRepeat";
        }

        questionMapper.insertQuestion(question);
        return Constants.SUCCESS;
    }

    @Override
    public String deleteOneQuestionById(Long id) {
        if (id == null) {
            return Constants.SUCCESS;
        }

        if (countAllQuestions() <= 1){
            return "atLeastOneQuestionNeeded";
        }

        questionMapper.deleteOneQuestionById(id);
        return Constants.SUCCESS;
    }

    @Override
    public String deleteManyQuestionsByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return Constants.SUCCESS;
        }

        if (countAllQuestions() <= ids.size()){
            return "atLeastOneQuestionNeeded";
        }

        questionMapper.deleteManyQuestionsByIds(ids);
        return Constants.SUCCESS;
    }
}
