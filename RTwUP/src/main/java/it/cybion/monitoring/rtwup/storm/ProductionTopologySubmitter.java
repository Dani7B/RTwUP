package it.cybion.monitoring.rtwup.storm;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ProductionTopologySubmitter implements StormTopologySubmitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionTopologySubmitter.class);

    private int numWorkers;

    private int maxSpoutPending;

    public ProductionTopologySubmitter(final int numWorkers, final int maxSpoutPending) {

        this.numWorkers = numWorkers;

        this.maxSpoutPending = maxSpoutPending;
    }

    @Override
    public void submit(final String topologyName, final Config topologyConfiguration,
                       final StormTopology topology) {

        runProductionTopology(topologyName, topologyConfiguration, topology);

    }

    private void runProductionTopology(final String topologyName,
                                       final Config topologyConfiguration,
                                       final StormTopology topology) {

        topologyConfiguration.setNumWorkers(this.numWorkers);
        topologyConfiguration.setMaxSpoutPending(this.maxSpoutPending);

        try {
            StormSubmitter.submitTopology(topologyName, topologyConfiguration, topology);
        } catch (AlreadyAliveException e) {
            LOGGER.error("already alive " + e.getMessage());
            e.printStackTrace();
        } catch (InvalidTopologyException e) {
            LOGGER.error("invalid topology " + e.getMessage());
            e.printStackTrace();
        }
    }

}
