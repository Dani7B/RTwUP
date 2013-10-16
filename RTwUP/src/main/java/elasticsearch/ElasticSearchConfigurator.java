package elasticsearch;

import it.cybion.commons.FileHelper;

import java.io.File;
import java.io.IOException;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexMissingException;

/**
 * Simple class to configure ElasticSearch started as a service
 */
public class ElasticSearchConfigurator 
{
    public static void main( String[] args ) throws InterruptedException{
    	
    	final String host = "localhost";
        final int transportPort = 9300;
        
        
        final String clusterName = "profileRepository";
        final String index = "profiles";
        final String type = "snapshot";
        final String mappingFileSource = "/mappings/twitter_user_snapshot-mapping.json";
        /*
        final String clusterName = args[0];
        final String index = args[1];
        final String type = args[2];
        final String mappingFileSource = args[3];*/
        
        
     // Create a TransportClient
        final Settings transportClientSettings = ImmutableSettings.settingsBuilder().put(
                "cluster.name", clusterName).build();
        Client transportClient = new TransportClient(transportClientSettings).addTransportAddress(
                new InetSocketTransportAddress(host, transportPort));
        
        transportClient.admin()
        			   .cluster()
        			   .prepareHealth()
        			   .setWaitForYellowStatus()
        			   .execute()
        			   .actionGet();
                
        try {
        	DeleteIndexResponse deleteIndexResponse = transportClient.admin()
            									 .indices()
            									 .delete(new DeleteIndexRequest(index))
            									 .actionGet();
        } catch (IndexMissingException e) {
            System.out.println("index does not exist - continue");
        }

		finally {
	    	transportClient.admin()
	    				   .indices()
	    				   .prepareCreate(index)
	    				   .execute()
	    				   .actionGet();
	
	    	// create mapping
	        final File mappingFile = FileHelper.readFromClasspath(mappingFileSource);
	
	        String mappingContent = "";
	
	        try {
	            mappingContent = FileHelper.readFile(mappingFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	
	        PutMappingResponse putResponse = transportClient.admin()
										        			.indices()
										        			.preparePutMapping(index)
										        			.setType(type)
										        			.setSource(mappingContent)
										        			.execute()
										        			.actionGet();
	        
	        Thread.sleep(2000);
	        System.out.println("Created index " + index + " in cluster " + clusterName + " and put mapping " + mappingFileSource);
		}
    }
    
    public static Settings buildNodeSettings(final String host, final String port,
            final String transportPort, final String clusterName, final String elasticSearchPath) {

        return ImmutableSettings.settingsBuilder().put("network.host", host).put("http.port", port)
                .put("transport.tcp.port", transportPort).put("cluster.name", clusterName)
                .put("path.data", elasticSearchPath + "data")
                .put("path.logs", elasticSearchPath + "logs")
                .put("path.work", elasticSearchPath + "work").build();
    }

}
