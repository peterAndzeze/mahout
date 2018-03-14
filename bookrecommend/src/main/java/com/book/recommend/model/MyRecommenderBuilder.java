package com.book.recommend.model;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class MyRecommenderBuilder implements RecommenderBuilder {
    @Override
    public Recommender buildRecommender(DataModel dataModel) throws TasteException {
        UserSimilarity similarity=new PearsonCorrelationSimilarity(dataModel);
        //定义相似的用户，超过0.1的可能性则认为是相似的：
        UserNeighborhood neighborhood=new ThresholdUserNeighborhood(0.1,similarity,dataModel);
        //根据数据模型、相似信息、相似用户来创建推荐：
        UserBasedRecommender userBasedRecommender=new GenericUserBasedRecommender(dataModel,neighborhood,similarity);
       return  userBasedRecommender;
    }
}
