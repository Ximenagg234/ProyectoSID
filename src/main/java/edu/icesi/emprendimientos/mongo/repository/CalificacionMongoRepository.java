package edu.icesi.emprendimientos.mongo.repository;

import edu.icesi.emprendimientos.mongo.document.CalificacionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CalificacionMongoRepository extends MongoRepository<CalificacionDocument, String> {

    List<CalificacionDocument> findByEmprendimientoId(String emprendimientoId);

    List<CalificacionDocument> findByIdEmprendimientoSql(Integer idEmprendimientoSql);

    Optional<CalificacionDocument> findByIdCalificacionSql(Integer idCalificacionSql);

    boolean existsByIdPedidoSqlAndUsuarioIdUsuarioSql(Integer idPedidoSql, Integer idUsuarioSql);

    List<CalificacionDocument> findByUsuarioIdUsuarioSql(Integer idUsuarioSql);
}
