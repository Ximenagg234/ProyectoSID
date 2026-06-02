package edu.icesi.emprendimientos.mongo.repository;

import edu.icesi.emprendimientos.mongo.document.PedidoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoMongoRepository extends MongoRepository<PedidoDocument, String> {

    Optional<PedidoDocument> findByIdPedidoSql(Integer idPedidoSql);

    List<PedidoDocument> findByCompradorIdUsuarioSql(Integer idUsuarioSql);

    List<PedidoDocument> findByEmprendimientoId(String emprendimientoId);

    List<PedidoDocument> findByIdEmprendimientoSql(Integer idEmprendimientoSql);

    List<PedidoDocument> findByEstado(String estado);

    List<PedidoDocument> findByIdEmprendimientoSqlAndEstado(Integer idEmprendimientoSql, String estado);
}
