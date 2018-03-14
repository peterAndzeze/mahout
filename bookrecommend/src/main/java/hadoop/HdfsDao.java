package hadoop;

import org.apache.hadoop.conf.Configuration;

public class HdfsDao {
    private static final String HDFS="hdfs://localhost:9000";
    public HdfsDao(Configuration conf) {
        this(HDFS, conf);
    }

    public HdfsDao(String hdfs, Configuration conf) {
        this.hdfsPath = hdfs;
        this.conf = conf;
    }

    private String hdfsPath;
    private Configuration conf;

    public void JobConf config(){
        JobConf conf = new JobConf(HdfsDAO.class);
        conf.setJobName("HdfsDAO");
        conf.addResource("classpath:/hadoop/core-site.xml");
        conf.addResource("classpath:/hadoop/hdfs-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");
        return conf;
    }
}
