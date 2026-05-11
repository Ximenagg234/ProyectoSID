package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.entity.UsuarioRol;
import edu.icesi.emprendimientos.entity.keys.UsuarioRolId;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.repository.UsuarioRolRepository;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              UsuarioRolRepository usuarioRolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        usuario.setIdUsuario(null);

        if (usuario.getClave() == null || usuario.getClave().isEmpty()) {
            throw new RuntimeException("El usuario debe tener una contraseña");
        }

        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        if (usuario.getRoles() == null) {
            usuario.setRoles(new ArrayList<>());
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

        if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().isEmpty()) {
            existente.setClave(passwordEncoder.encode(usuarioActualizado.getClave()));
        }

        return usuarioRepository.save(existente);
    }

    @Override
    public void asignarRol(Integer idUsuario, Integer idRol) {
        UsuarioRolId id = new UsuarioRolId(idUsuario, idRol);

        // Si ya existe no hacer nada
        if (usuarioRolRepository.existsById(id)) return;

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(id);
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        // Guardar directamente en la tabla junction, sin cascade
        usuarioRolRepository.save(usuarioRol);
    }

    @Override
    public void quitarRol(Integer idUsuario, Integer idRol) {
        UsuarioRolId id = new UsuarioRolId(idUsuario, idRol);
        usuarioRolRepository.deleteById(id);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoInstitucional(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
