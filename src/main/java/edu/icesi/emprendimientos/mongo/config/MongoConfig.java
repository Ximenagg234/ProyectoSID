package edu.icesi.emprendimientos.mongo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "edu.icesi.emprendimientos.mongo.repository"
)
@EnableJpaRepositories(
    basePackages = "edu.icesi.emprendimientos.repository"
)
public class MongoConfig {
}
