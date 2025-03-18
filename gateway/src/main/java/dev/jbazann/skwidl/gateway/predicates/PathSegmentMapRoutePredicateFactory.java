package dev.jbazann.skwidl.gateway.predicates;

import com.netflix.discovery.shared.Application;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

public class PathSegmentMapRoutePredicateFactory extends AbstractRoutePredicateFactory<PathSegmentMapRoutePredicateFactory.Config> {

    private final DiscoveryClient discoveryClient;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public PathSegmentMapRoutePredicateFactory(DiscoveryClient discoveryClient) {
        super(Config.class);
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            logger.info("Matching predicate for URI " + exchange.getRequest().getURI());// TODO get these out of here.

            String segment = getUriSegment(exchange.getRequest().getURI(), config);
            List<ServiceInstance> services = discoveryClient.getInstances(config.serviceId);
            if (services.isEmpty()) return false; // TODO Exception?

            String[] keys = services.getFirst().getMetadata()
                    // defaultValue should be an invalid path segment, or be replaced by throwing an exception.
                    .getOrDefault(config.metadata_key, ";)")
                    .toLowerCase().split(",");

            logger.info("Matching keys = " + asList(keys) + " with segment = " + segment);// TODO get these out of here.
            return asList(keys).contains(segment);
        };
    }

    @Setter @Getter @ToString
    public static class Config {
        private Integer segment_index = 0;
        private String metadata_key = "collections";
        private String serviceId = "";
    }

    private String getUriSegment(URI uri, int index) {
        if (index < 0) index = 0;
        String[] segments = uri.getPath().split("/");
        if (uri.getPath().startsWith("/")) index += 1;
        if (segments.length <= index) return "";
        return (segments[index] == null ? "" : segments[index]).toLowerCase();
    }

    private String getUriSegment(URI uri, PathSegmentMapRoutePredicateFactory.Config config) {
        return getUriSegment(uri,
                (config == null || config.segment_index == null) ?
                        0 : config.segment_index
        );
    }

}
