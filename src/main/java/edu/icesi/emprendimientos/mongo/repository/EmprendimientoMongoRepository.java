package edu.icesi.emprendimientos.mongo.repository;

import edu.icesi.emprendimientos.mongo.document.EmprendimientoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmprendimientoMongoRepository extends MongoRepository<EmprendimientoDocument, String> {

    Optional<EmprendimientoDocument> findByIdEmprendimientoSql(Integer idEmprendimientoSql);

    List<EmprendimientoDocument> findByEstado(String estado);

    List<EmprendimientoDocument> findByDestacadoTrue();

    List<EmprendimientoDocument> findByCategoriaIdCategoriaSql(Integer idCategoriaSql);

    List<EmprendimientoDocument> findByEmprendedorIdUsuarioSql(Integer idUsuarioSql);

    /** Busca emprendimientos cuyo nombre o descripcion contiene el texto (case-insensitive) */
    @Query("{ $or: [ { 'nombre': { $regex: ?0, $options: 'i' } }, { 'descripcion': { $regex: ?0, $options: 'i' } } ] }")
    List<EmprendimientoDocument> buscarPorTexto(String texto);

    /** Ranking por calificacion promedio descendente */
    List<EmprendimientoDocument> findAllByOrderByMetricasCalificacionPromedioDesc();

    /** Ranking por total de pedidos descendente */
    List<EmprendimientoDocument> findAllByOrderByMetricasTotalPedidosDesc();
}
