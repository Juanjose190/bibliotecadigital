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

    @Override
    public User crearUsuario(User user) {
        return userRepository.save(user);
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
