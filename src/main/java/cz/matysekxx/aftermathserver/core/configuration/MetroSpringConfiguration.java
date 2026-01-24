package cz.matysekxx.aftermathserver.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/// Java configuration class for the Metro system.
///
/// Bridges the Spring configuration with XML definitions for metro lines and services.
@Configuration
@ImportResource("classpath:metro-service-config.xml")
public class MetroSpringConfiguration {
}