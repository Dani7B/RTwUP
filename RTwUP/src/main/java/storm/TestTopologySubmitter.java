package storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class TestTopologySubmitter implements StormTopologySubmitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTopologySubmitter.class);

    private LocalCluster localCluster;

    private long executionTime;

    public TestTopologySubmitter(long executionTime) {

        this.localCluster = new LocalCluster();

        this.executionTime = executionTime;

    }

    @Override
    public void submit(String topologyName, Config topologyConfiguration, StormTopology topology) {

        LOGGER.info("test local cluster " + this.localCluster);
        runLocalTopology(topologyName, topologyConfiguration, topology);

    }

    private void runLocalTopology(final String topologyName, final Config topologyConfiguration,
                                  final StormTopology topology) {

        try {
            this.localCluster.submitTopology(topologyName, topologyConfiguration, topology);
            Utils.sleep(this.executionTime);
            this.localCluster.killTopology(topologyName);
            this.localCluster.shutdown();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
