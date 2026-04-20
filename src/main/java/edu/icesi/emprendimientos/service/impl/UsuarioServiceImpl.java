package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.entity.UsuarioRol;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder; // NUEVO

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario guardar(Usuario usuario) {

        usuario.setIdUsuario(null);

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            throw new RuntimeException("El usuario debe tener al menos un rol");
        }
        if (usuario.getClave() == null || usuario.getClave().isEmpty()) {
            throw new RuntimeException("El usuario debe tener una contraseña");
        }

        // ENCRIPTAR CONTRASEÑA
        if (passwordEncoder != null) {
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));
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

        // SOLO SI CAMBIA LA CLAVE
        if (usuarioActualizado.getClave() != null &&
                !usuarioActualizado.getClave().isEmpty()) {

            if (passwordEncoder != null) {
                existente.setClave(
                        passwordEncoder.encode(usuarioActualizado.getClave())
                );
            } else {
                existente.setClave(usuarioActualizado.getClave());
            }
        }

        return usuarioRepository.save(existente);
    }

    @Override
    public void asignarRol(Integer idUsuario, Integer idRol) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        if (usuario.getRoles() != null) {
            usuario.getRoles().add(usuarioRol);
        } else {
            usuario.setRoles(new java.util.ArrayList<>());
            usuario.getRoles().add(usuarioRol);
        }

        usuarioRepository.save(usuario);
    }

    @Override
    public void quitarRol(Integer idUsuario, Integer idRol) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            usuario.getRoles().removeIf(
                    ur -> ur.getRol() != null && ur.getRol().getIdRol().equals(idRol)
            );
        }

        usuarioRepository.save(usuario);
    }
}