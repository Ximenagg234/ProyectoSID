package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import java.util.List;

public interface EmprendimientoService {
    Emprendimiento guardar(Emprendimiento emprendimiento);
    List<Emprendimiento> listar();
    Emprendimiento buscarPorId(Integer id);
    Emprendimiento actualizar(Integer id, Emprendimiento emprendimiento);
    void eliminar(Integer id);
    List<Emprendimiento> listarPorUsuario(Integer idUsuario);
    List<Emprendimiento> listarDestacados();
    void toggleDestacado(Integer id);
}
