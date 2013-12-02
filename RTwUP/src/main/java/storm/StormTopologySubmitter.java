package storm;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public interface StormTopologySubmitter {

    void submit(String topologyName, Config topologyConfiguration, StormTopology topology);
}
