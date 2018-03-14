package com.book.recommend.model;

import com.book.recommend.service.DataService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.List;

/**
 * 基于用户
 */
public class UserRecommend {
    /**
     * 我们根据其他相似品味用户的信息来推断特定用户的推荐，用相互作用关系来推断彼此之间的联系：
     * @param dataModel
     */
    public  UserBasedRecommender userRecommned(DataModel dataModel){
        try {
            UserSimilarity similarity=new PearsonCorrelationSimilarity(dataModel);
            //定义相似的用户，超过0.1的可能性则认为是相似的：
            UserNeighborhood neighborhood=new ThresholdUserNeighborhood(0.1,similarity,dataModel);
            //根据数据模型、相似信息、相似用户来创建推荐：
            UserBasedRecommender userBasedRecommender=new GenericUserBasedRecommender(dataModel,neighborhood,similarity);

            List<RecommendedItem> recommendedItems=userBasedRecommender.recommend(2,4);

            for (RecommendedItem recommendedItem:recommendedItems){
                System.out.println(recommendedItem);
            }
            return userBasedRecommender;
        } catch (TasteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 评估 进行测试(每次运行结果不同，值越低越好)：
     * @param myDataModel
     */
    public RecommenderBuilder evaluateRecommender(DataModel myDataModel){
        RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
        try {
            RecommenderBuilder builder=new MyRecommenderBuilder();
            double result=evaluator.evaluate(builder,null,myDataModel,0.9,1.0);
            RecommenderIRStatsEvaluator evaluator1 = new GenericRecommenderIRStatsEvaluator();
            IRStatistics stats = evaluator1.evaluate(builder, null, myDataModel, null, 2, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
            System.out.println("result："+result+",stats:"+stats);
            return builder;
        } catch (TasteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getData(int userId,RecommenderBuilder recommenderBuilder,DataModel dataModel){
        try {
            List<RecommendedItem> recommendedItems=recommenderBuilder.buildRecommender(dataModel).recommend(userId,2);
            recommendedItems.forEach(e -> System.out.println(e.getItemID()+"--->"+e.getValue()));
        } catch (TasteException e) {
            e.printStackTrace();
        }
    }



    public static void main(String [] args){
      DataModel dataModel=new DataService().getDataFromFile();
        UserRecommend userRecommend=new UserRecommend();
        userRecommend.userRecommned(dataModel);
       RecommenderBuilder recommenderBuilder=userRecommend.evaluateRecommender(dataModel);
       userRecommend.getData(2,recommenderBuilder,dataModel);
    }
}
