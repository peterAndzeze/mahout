package hadoop;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;

import java.io.IOException;
import java.net.URI;

public class HdfsDao {
    private static final String HDFS="hdfs://localhost:9000";
    public HdfsDao(Configuration conf) {
        this(HDFS, conf);
    }

    public HdfsDao(String hdfs, Configuration conf) {
        this.hdfsPath = hdfs;
        this.conf = conf;
    }

    public HdfsDao() {
    }

    private String hdfsPath;
    private Configuration conf;





    public static JobConf config(){
        JobConf jobConf = new JobConf();
        jobConf.setJobName("HdfsDAO");
        jobConf.addResource("classpath:/hadoop/core-site.xml");
        jobConf.addResource("classpath:/hadoop/hdfs-site.xml");
        jobConf.addResource("classpath:/hadoop/mapred-site.xml");
        return jobConf;
    }


    public static void main(String [] args){
        JobConf jobConf= HdfsDao.config();
        HdfsDao hdfsDao=new HdfsDao(jobConf);
        Path path = new Path("/user/newcapec/");
        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(hdfsDao.hdfsPath), jobConf);

        FileStatus[] list = fs.listStatus(path);
        for (FileStatus f : list) {
            System.out.printf("name: %s, folder: %s, size: %d\n", f.getPath(), f.isDir(), f.getLen());
        }
        System.out.println("==========================================================");
        fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
