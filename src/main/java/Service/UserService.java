package Service;

import Interfaces.IUser;
import Model.User;
import Repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserService implements IUser {

    @Autowired
    private UserRepository userRepository;

  public User registrarUsuario(User usuario) {
        
        if (userRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }

     
        if (usuario.getCedula() == null || usuario.getCedula().isEmpty()) {
            throw new RuntimeException("La cédula es obligatoria");
        }

        if (userRepository.existsByCedula(usuario.getCedula())) {
            throw new RuntimeException("Ya existe un usuario con esa cédula");
        }

        return userRepository.save(usuario);
    }

    @Override
    public User obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> obtenerTodosLosUsuarios() {
        return userRepository.findAll();
    }

    @Override
    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User actualizarUsuario(Long id, User usuarioActualizado) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existente = optionalUser.get();
            existente.setNombre(usuarioActualizado.getNombre());
            existente.setEmail(usuarioActualizado.getEmail());
            existente.setTipoUsuario(usuarioActualizado.getTipoUsuario());
            existente.setSanciones(usuarioActualizado.getSanciones());
            return userRepository.save(existente);
        } else {
            return null;
        }
    }

    @Override
    public List<User> obtenerUsuariosConMasSanciones() {
        return userRepository.findTop5ByOrderBySancionesDesc();
    }
}
