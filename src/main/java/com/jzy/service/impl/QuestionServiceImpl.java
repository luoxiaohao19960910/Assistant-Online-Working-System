package com.jzy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jzy.dao.QuestionMapper;
import com.jzy.manager.constant.RedisConstants;
import com.jzy.manager.exception.InvalidParameterException;
import com.jzy.manager.exception.NoMoreQuestionsException;
import com.jzy.manager.exception.QuestionNotExistException;
import com.jzy.manager.util.CodeUtils;
import com.jzy.manager.util.QuestionUtils;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.search.QuestionSearchCondition;
import com.jzy.model.dto.QuestionWithCreatorDto;
import com.jzy.model.dto.UpdateResult;
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
public class QuestionServiceImpl extends AbstractServiceImpl implements QuestionService {
    private final static Logger logger = LogManager.getLogger(QuestionServiceImpl.class);

    /**
     * 表示问题的内容重复
     */
    private final static String QUESTION_CONTENT_REPEAT = "questionContentRepeat";

    /**
     * 表示至少需要一个问题
     */
    private final static String AT_LEAST_ONE_NEEDED = "atLeastOneQuestionNeeded";

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public boolean isRepeatedQuestionContent(Question question) {
        if (question == null) {
            throw new InvalidParameterException("输入对象不能为空");
        }
        return getQuestionByContent(question.getContent()) != null;
    }

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
        String key = RedisConstants.QUESTION_KEY;
        if (redisTemplate.hasKey(key)) {
            //缓存中有
            String questionsJSON = (String) valueOps.get(key);
            return JSONArray.parseArray(questionsJSON, Question.class);
        }

        List<Question> questions = questionMapper.listAllQuestions();
        valueOps.set(key, JSON.toJSONString(questions));
        return questions;
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
    public Question getDifferentRandomQuestion(Question currentQuestion) throws NoMoreQuestionsException {
        if (currentQuestion == null) {
            return getRandomQuestion();
        }

        return getDifferentRandomQuestion(currentQuestion.getContent());
    }

    @Override
    public Question getDifferentRandomQuestion(String currentQuestionContent) throws NoMoreQuestionsException {
        if (StringUtils.isEmpty(currentQuestionContent)) {
            return getRandomQuestion();
        }

        List<Question> questions = listAllQuestions();
        if (questions.size() <= 1) {
            //总共只有一个问题（或数据库没有问题，使用了默认问题），不找不同的问题了，抛出异常待捕获
            throw new NoMoreQuestionsException("数据库中只有一个问题!");
        }

        Question newQuestion = null;
        do {
            //循环直到找到与当前问题不同的问题
            newQuestion = getRandomQuestion();
        } while (newQuestion.getContent().equals(currentQuestionContent));

        return newQuestion;
    }

    @Override
    public boolean isCorrectAnswer(String questionContent, String answerInput) throws QuestionNotExistException {
        if (StringUtils.isEmpty(answerInput)) {
            return false;
        }

        if (countAllQuestions() == 0) {
            //数据库没问题，即使用的是默认问题
            return QuestionUtils.isCorrectAnswerOfDefaultQuestion(answerInput);
        }

        Question question = getQuestionByContent(questionContent);
        if (question == null) {
            throw new QuestionNotExistException("当前问题不存在！");
        }

        if (isAlwaysTrueAnswer(answerInput)) {
            //用万能答案
            return true;
        }

        if (question.getAnswer().equals(answerInput)) {
            //与答案1匹配
            return true;
        }

        if (StringUtils.isEmpty(question.getAnswer2())) {
            //没有第二答案
            return false;
        }

        if (question.getAnswer2().equals(answerInput)) {
            //与答案2相同
            return true;
        }

        return false;
    }

    @Override
    public boolean isAlwaysTrueAnswer(String answerInput) {
        return QuestionUtils.isAlwaysTrueAnswer(answerInput);
    }

    @Override
    public PageInfo<QuestionWithCreatorDto> listQuestions(MyPage myPage, QuestionSearchCondition condition) {
        PageHelper.startPage(myPage.getPageNum(), myPage.getPageSize());
        List<QuestionWithCreatorDto> questionWithCreatorDtos = questionMapper.listQuestions(condition);
        return new PageInfo<>(questionWithCreatorDtos);
    }

    @Override
    public String updateQuestionInfo(Question question) {
        if (question == null) {
            return FAILURE;
        }
        Question originalQuestion = getQuestionById(question.getId());
        if (originalQuestion == null) {
            return FAILURE;
        }
        if (isModifiedAndRepeatedQuestionContent(originalQuestion, question)) {
            //问题内容改过了，判断是否与已存在的记录冲突
            return QUESTION_CONTENT_REPEAT;
        }

        if (question.equalsExceptBaseParamsAndTrueAnswerAndCreatorId(originalQuestion)) {
            //未修改
            return UNCHANGED;
        }
        //执行更新
        questionMapper.updateQuestionInfo(question);
        return SUCCESS;
    }

    /**
     * 判断当前要更新的问题的内容是否修改过且重复。
     * 只有相较于原来的问题修改过且与数据库中重复才返回false
     *
     * @param originalQuestion 用来比较的原来的问题
     * @param newQuestion      要更新的问题
     * @return 问题的内容是否修改过且重复
     */
    private boolean isModifiedAndRepeatedQuestionContent(Question originalQuestion, Question newQuestion) {
        if (!originalQuestion.getContent().equals(newQuestion.getContent())) {
            //问题内容改过了，判断是否与已存在的记录冲突
            if (isRepeatedQuestionContent(newQuestion)) {
                //修改后的问题已存在
                return true;
            }
        }
        return false;
    }

    @Override
    public String insertOneQuestion(Question question) {
        if (question == null) {
            return FAILURE;
        }
        if (isRepeatedQuestionContent(question)) {
            //修改后的问题已存在
            return QUESTION_CONTENT_REPEAT;
        }

        questionMapper.insertOneQuestion(question);
        return SUCCESS;
    }

    @Override
    public UpdateResult deleteOneQuestionById(Long id) {
        if (id == null) {
            return new UpdateResult(SUCCESS);
        }

        if (countAllQuestions() <= 1) {
            return new UpdateResult(AT_LEAST_ONE_NEEDED);
        }

        long count = questionMapper.deleteOneQuestionById(id);
        UpdateResult result = new UpdateResult(SUCCESS);
        result.setDeleteCount(count);
        return result;
    }

    @Override
    public UpdateResult deleteManyQuestionsByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return new UpdateResult(SUCCESS);
        }

        if (countAllQuestions() <= ids.size()) {
            return new UpdateResult(AT_LEAST_ONE_NEEDED);
        }

        long count = questionMapper.deleteManyQuestionsByIds(ids);
        UpdateResult result = new UpdateResult(SUCCESS);
        result.setDeleteCount(count);
        return result;
    }
}
