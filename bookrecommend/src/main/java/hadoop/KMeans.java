package hadoop;

import hadoop.util.HadoopUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.utils.clustering.ClusterDumper;

import java.io.IOException;


public class KMeans {

    public static void main(String [] args) {
        org.apache.hadoop.conf.Configuration configuration=null;
        FileSystem fileSystem=null;
        HadoopUtil.initCluster(configuration,fileSystem,"cluster");

        try {
            InputDriver.runJob( new Path("/user/newcapec/output/seq/"),new Path("/user/newcapec/output/vertor"), "org.apache.mahout.math.RandomAccessSparseVector");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        int k = 3;
        Path seqFilePath = new Path("/user/newcapec/output/seq/");
        Path clustersSeeds = new Path("/user/newcapec/output/seeds");
        DistanceMeasure measure = new EuclideanDistanceMeasure();
        try {
            clustersSeeds = RandomSeedGenerator.buildRandom(configuration, seqFilePath, clustersSeeds, k, measure);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            KMeansDriver.run(configuration,new Path("/user/newcapec/output/vertor"),clustersSeeds,new Path("/user/newcapec/output/result")
                            ,0.01,10,true,0.01,false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //KMeansDriver.run(configuration, seqFilePath, clustersSeeds, new Path("/user/newcapec/output/result"), measure, 0.01, 10, true, 0.01, false);
        Path outGlobPath = new Path("/user/newcapec/output/result", "clusters-*-final");
        Path clusteredPointsPath = new Path(      "/user/newcapec/output/result/clusteredPoints");
        System.out.printf("Dumping out clusters from clusters: %s and clusteredPoints: %s\n", outGlobPath, clusteredPointsPath);

        ClusterDumper clusterDumper = new ClusterDumper(outGlobPath, clusteredPointsPath);
        try {
            clusterDumper.printClusters(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
