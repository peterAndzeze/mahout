package hadoop;

import hadoop.util.HadoopUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.classifier.sequencelearning.hmm.RandomSequenceGenerator;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;

import java.io.IOException;
import java.net.URI;


/**
 * KMeans聚类
 */
public class KMeansCluster {
    private static final String HDFS="hdfs://localhost:9000/user/newcapec/";

    Configuration configuration=null;
    FileSystem fileSystem=null;
    static boolean runSequential=true;

    /**
     *
     * @param inDir 输入文件
     * @param outDir 输出文件
     * @param nkluster 预先分类个数
     */
    public void kMeansCluster(String inDir,String outDir,int nkluster) throws Exception {
        /**
         * 1.文件数据验证
         * 2.初始化hadoop环境
         *
         *
         * */
        System.out.println("step 0 ,init environment");
        initCluster("cluster");
        System.out.println("step1 ,local file send hdfs");
        txtToSequenceFile(HDFS+"testdata/test.txt",HDFS+"/output/seq");
        writeToSequenceFile(HDFS+"/output/seq/",HDFS+"/output/tfidf");
        //将文本生成向量
        System.out.println(("step 2,sequenceFile to vertors"));
        seqToSparse(HDFS+"/output/seq",HDFS+"output/seqToSparse-tfidf");
        //使用算法进行聚类
        System.out.println(("step 3,use k-means cluster"));

        kmeansCluster(HDFS+"output/seqToSparse-tfidf/",nkluster);

       // HDFS+"output/seqToSparse-tfidf/



    }

    /**
     * 初始化环境
     * @param runMode 集群还是本机
     */
    public void initCluster(String runMode){
        RUN_MODE mode=RUN_MODE.LOCAL;
        if("cluster".equals(runMode)){
            mode=RUN_MODE.CLUSTER;
            runSequential=false;
        }
        configuration= HadoopUtil.getConf(mode);
        try {
            fileSystem = FileSystem.get( configuration);

        } catch (IOException e) {
            System.out.println("初始化环境异常:"+e.getMessage());
            e.printStackTrace();
        }
    }




    /**
     * 普通文件写成SequenceFile(TF-IDF)计算的TEXT文件必须是SequenceFile 格式的
     * @param inDir 输入文件(文件夹下的所有文件进行seq压缩)
     * @param original 输出文件
     */
    public  void writeToSequenceFile(String inDir, String original){
        // TODO Auto-generated method stub
        Path seqFile = new Path(inDir);
        try {
            // Writer内部类用于文件的写操作,假设Key和Value都为Text类型
            SequenceFile.Writer writer = SequenceFile.createWriter(
                    configuration,
                    new SequenceFile.Writer.Option[] {
                            SequenceFile.Writer.file(seqFile),
                            SequenceFile.Writer.keyClass(Text.class),
                            SequenceFile.Writer.valueClass(Text.class),
                    }
            );

            // 通过reader从文档中读取记录
            SequenceFile.Reader reader = new SequenceFile.Reader(fileSystem,seqFile,configuration);
            Text key = new Text();
            Text value = new Text();
            while (reader.next(key, value)) {
                System.out.println(key);
                System.out.println(value);
                writer.append(new Text(key),new Text(value));
            }
            IOUtils.closeStream(reader);// 关闭read流
            IOUtils.closeStream(writer);//关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param inDir
     * @param outDir
     */
    public void seqToSparse(String inDir,String outDir) throws Exception {
        String [] args={
          "-i",inDir,"-o",outDir,"-lnorm","-nv","-ow","-wt","tfidf"
         // ,"-a","org.wltea.analyzer.lucene.IKAnalyzer"


        };
        ToolRunner.run(configuration,new SparseVectorsFromSequenceFiles(),args);
    }

    /**
     * 执行kmeans聚类
     * @param vertorDir
     * @param nCluter
     * @throws Exception
     */
    public  void kmeansCluster(String vertorDir,int nCluter) throws Exception {
        String [] args ={
            "-i",vertorDir,"-c",vertorDir+"/clusters",
            "-o",vertorDir+"/kmeans-output",
            "-dm","org.apache.mahout.common.distance.CosineDistanceMeasure",//余弦距离算法 默认欧式距离算法
            "-x","10",//指定最大迭代次数
            "-k",nCluter+"",//由 RandomSequenceGenerator 生成
            "-cl",//是否执行聚类
            "-ow","-xm","sequential"//执行方法
        };
        ToolRunner.run(configuration,new KMeansDriver(),args);
    }

    /**
     * 导出聚类后的文件
     * @param dir
     */
    public void clusterDumper(String dir){
        String args [] ={
                "-i",HDFS+"output/seqToSparse-tfidf/",
                "-o","clusterdump.json",

        };
    }

    /**
     * 文本文件转为SquenceFile
     * @param inDir
     * @param outDir
     * @return
     */
    public void txtToSequenceFile(String inDir,String outDir) throws Exception {
        String [] args ={
                "-i",inDir,
                "-o",outDir,
                "-ow"
        };
        ToolRunner.run(configuration,new SequenceFilesFromDirectory(),args);
    }



    public static void main(String [] args) throws Exception {
        KMeansCluster kMeansCluster=new KMeansCluster();
/*
        kMeansCluster.kMeansCluster("testdata/test.txt","origin/",4);

*/
        kMeansCluster.seqToSparse("/user/newcapec/testdata/part-m-00000","/user/newcapec/testdata/vecfile");
    }

}
