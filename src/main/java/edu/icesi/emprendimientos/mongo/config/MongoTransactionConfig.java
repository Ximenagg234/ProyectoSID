package edu.icesi.emprendimientos.mongo.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuración de transaction managers para dual-DB.
 *
 * Con dos BDs activas (PostgreSQL + MongoDB), Spring necesita saber
 * cuál es el transaction manager por defecto.
 *
 * - transactionManager (PRIMARY): JPA para PostgreSQL/Supabase
 *   → usado por @Transactional sin cualificador
 *   → usado por JPA repositories (usuarios, roles, permisos)
 *
 * - mongoTransactionManager: MongoDB Atlas
 *   → usado por @Transactional("mongoTransactionManager")
 *   → usado para operaciones ACID en MongoDB (stock, pedidos)
 */
@Configuration
public class MongoTransactionConfig {

    /** JPA transaction manager — PRIMARY (defecto para @Transactional sin nombre) */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    /** MongoDB transaction manager — usado con @Transactional("mongoTransactionManager") */
    @Bean(name = "mongoTransactionManager")
    public PlatformTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
