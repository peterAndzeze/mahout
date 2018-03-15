package hadoop;

import org.apache.hadoop.conf.Configuration;

public enum RUN_MODE {
    LOCAL,CLUSTER;

    /**
     * 初始化环境
     * @param mode 运行环境选择
     * @return 环境信息
     */
    public static Configuration getConf(RUN_MODE mode){
        Configuration configuration=new Configuration();
        if(mode ==RUN_MODE.LOCAL){//本机
            configuration.set("fs.defaultFS","file:///");
            configuration.set("mapred.job.tracker","local");
            configuration.set("mapreduce.framework.name","local");
        }
        return configuration;

    }
}
