package cz.matysekxx.aftermathserver.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:metro-service-config.xml")
public class MetroSpringConfiguration {}