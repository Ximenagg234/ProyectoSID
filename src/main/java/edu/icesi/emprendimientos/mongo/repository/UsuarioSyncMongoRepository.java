package edu.icesi.emprendimientos.mongo.repository;

import edu.icesi.emprendimientos.mongo.document.UsuarioSyncDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioSyncMongoRepository extends MongoRepository<UsuarioSyncDocument, String> {

    Optional<UsuarioSyncDocument> findByIdUsuarioSql(Integer idUsuarioSql);

    Optional<UsuarioSyncDocument> findByCorreoInstitucional(String correoInstitucional);

    boolean existsByCorreoInstitucional(String correoInstitucional);
}
