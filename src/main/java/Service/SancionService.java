/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

/**
 *
 * @author JUAN JOSE
 */
import Model.Sancion;
import Model.Prestamo;
import Model.User;
import Repository.SancionRepository;
import Repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SancionService {

    @Autowired
    private SancionRepository sancionRepository;
    
    @Autowired
private UserRepository userRepository;

    public boolean existeSancionParaPrestamo(Long prestamoId) {
        return sancionRepository.existsByPrestamoId(prestamoId);
    }

    public boolean usuarioTieneSancionesActivas(Long usuarioId) {
        return sancionRepository.existsByUsuarioIdAndActivaTrue(usuarioId);
    }

    public List<Sancion> obtenerSancionesActivasUsuario(Long usuarioId) {
        return sancionRepository.findByUsuarioIdAndActivaTrue(usuarioId);
    }

@Transactional
public void sancionarUsuario(User usuario, Prestamo prestamo) {
    if (existeSancionParaPrestamo(prestamo.getId())) {
        return;
    }

    Sancion sancion = new Sancion();
    sancion.setUser(usuario);
    sancion.setPrestamo(prestamo);
    sancion.setFechaInicio(LocalDateTime.now());
    sancion.setFechaFin(LocalDateTime.now().plusDays(7));
    sancion.setActiva(true);
    sancion.setDescripcion("Retraso en devolución: " + prestamo.getLibro().getTitulo());

    sancionRepository.save(sancion);

    usuario.setSanciones(usuario.getSanciones() + 1); 
    userRepository.save(usuario); 
}
}