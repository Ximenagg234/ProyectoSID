package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            throw new RuntimeException("El usuario debe tener al menos un rol");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public Usuario actualizar(Integer id, Usuario usuarioActualizado) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setNombreCompleto(usuarioActualizado.getNombreCompleto());
        existente.setClave(usuarioActualizado.getClave());

        return usuarioRepository.save(existente);
    }
}