package com.hazelcast.samples.eureka.partition.groups;

import static com.hazelcast.config.PartitionGroupConfig.MemberGroupType.ZONE_AWARE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;

/**
 * <P>Creates a Hazelcast configuration object, which Spring Boot
 * will use to create a Hazelcast instance based on that configuration.
 * </P>
 */
@Configuration
public class MyConfiguration {

	/**
	 * <P>Create a Hazelcast configuration object that differs from
	 * the default in three ways.
	 * </P>
	 * <OL>
	 * <LI><B>Name</B> 
	 * <P>Use {@link Constants} to give the cluster a name,
	 * here more for logging and diagnostics than to avoid inadvertant
	 * connections.</P>
	 * </LI>
	 * <LI><B>Networking</B>
	 * <P>Turn off the default multicast broadcasting mechanism for servers
	 * to find each other, in favor of a plug-in that will somehow provide
	 * the locations of the servers. The "<em>somehow</em>" being to find
	 * their locations in {@code Eureka}.
	 * </P>
	 * </LI>
	 * <LI><B>Partition Groups</B>
	 * <P>Activate {@code ZONE_AWARE} partitioning, where we guide Hazelcast
	 * in where to place data master and data backup copies with external
	 * meta-data (which we get from {@code Eureka}.</P>
	 * </LI>
	 * </OL>
	 * 
	 * @param discoveryServiceProvider A {@link MyDiscoveryServiceProvider} instance.
	 * @return Configuration for a Hazelcast server.
	 */
	@Bean
	public Config config(DiscoveryServiceProvider discoveryServiceProvider) {

		Config config = new Config();
		
		// Naming
		
		config.getGroupConfig().setName(Constants.CLUSTER_NAME);

		// Discovery
		
		config.setProperty("hazelcast.discovery.enabled", Boolean.TRUE.toString());
		JoinConfig joinConfig = config.getNetworkConfig().getJoin();
		joinConfig
			.getMulticastConfig()
			.setEnabled(false);
		joinConfig
			.getDiscoveryConfig()
			.setDiscoveryServiceProvider(discoveryServiceProvider);

		// Partition Groups
		
		config.getPartitionGroupConfig()
			.setEnabled(true)
			.setGroupType(ZONE_AWARE);

		return config;
	}
}
