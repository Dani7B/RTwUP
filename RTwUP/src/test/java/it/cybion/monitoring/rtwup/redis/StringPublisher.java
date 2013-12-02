package it.cybion.monitoring.rtwup.redis;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public interface StringPublisher {

    void publish(String channel, String message);

}
