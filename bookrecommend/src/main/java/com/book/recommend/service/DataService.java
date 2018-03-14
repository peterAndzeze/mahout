package com.book.recommend.service;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import java.io.File;
import java.io.IOException;

/**
 * 数据服务
 */
public class DataService {

    public DataModel getDataFromFile(){
        try {
            DataModel dataModel=new FileDataModel(new File("src/data/dataset.csv"));
            return dataModel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
