package hadoop.util;

import hadoop.RUN_MODE;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.mahout.clustering.iterator.KMeansClusteringPolicy;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * hadoop 工具类
 */
public class HadoopUtil {


    /**
     * 初始环境
     * @param mode
     * @return
     */
    public static Configuration getConf(RUN_MODE mode){
        Configuration configuration=new Configuration();
        if(mode.equals(RUN_MODE.CLUSTER)){//集群
        /*    configuration.set("fs.defaultFS","classpath:/hadoop/hdfs-site.xm");
            configuration.set("mapred.job.tracker","classpath:/hadoop/mapred-site.xml");
            configuration.set("mapreduce.framework.name","classpath:/hadoop/core-site.xml");*/
            configuration.addResource(new Path("classpath:/hadoop/hdfs-site.xml"));
            configuration.addResource(new Path("classpath:/hadoop/mapred-site.xml"));
            configuration.addResource(new Path("classpath:/hadoop/core-site.xml"));

        }else{
            configuration=RUN_MODE.getConf(mode);
        }
        return configuration;
    }

    /**
     * 初始化环境
     * @param runMode 集群还是本机
     */
    public static void initCluster(Configuration configuration,FileSystem fileSystem,String runMode){
        RUN_MODE mode=RUN_MODE.LOCAL;
        if("cluster".equals(runMode)){
            mode=RUN_MODE.CLUSTER;
        }
        configuration= HadoopUtil.getConf(mode);
        try {
            fileSystem = FileSystem.get( configuration);

        } catch (IOException e) {
            System.out.println("初始化环境异常:"+e.getMessage());
            e.printStackTrace();
        }
    }
}
