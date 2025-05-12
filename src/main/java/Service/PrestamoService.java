package Service;

import Interfaces.IPrestamo;
import Model.Libro;
import Model.Prestamo;
import Model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PrestamoService implements IPrestamo {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Prestamo registrarPrestamo(Prestamo prestamo) {
        if (prestamo.getUsuario() != null && prestamo.getUsuario().getId() != null) {
            User usuario = entityManager.find(User.class, prestamo.getUsuario().getId());
            if (usuario == null) {
                throw new EntityNotFoundException("Usuario no encontrado con ID: " + prestamo.getUsuario().getId());
            }
            prestamo.setUsuario(usuario);
        }
        
        if (prestamo.getLibro() != null && prestamo.getLibro().getId() != null) {
            Libro libro = entityManager.find(Libro.class, prestamo.getLibro().getId());
            if (libro == null) {
                throw new EntityNotFoundException("Libro no encontrado con ID: " + prestamo.getLibro().getId());
            }
            prestamo.setLibro(libro);
        }
        
        entityManager.persist(prestamo);
        
        entityManager.flush();
        entityManager.refresh(prestamo);
        
        return prestamo;
    }
    
    @Override
    public Prestamo obtenerPrestamoPorId(Long id) {
        return entityManager.find(Prestamo.class, id);
    }
    
    @Override
    public List<Prestamo> obtenerTodosLosPrestamos() {
        TypedQuery<Prestamo> query = entityManager.createQuery("SELECT p FROM Prestamo p", Prestamo.class);
        return query.getResultList();
    }
    
    @Override
    public void eliminarPrestamo(Long id) {
        Prestamo prestamo = obtenerPrestamoPorId(id);
        if (prestamo != null) {
            entityManager.remove(prestamo);
        }
    }
    
    @Override
    public Prestamo actualizarPrestamo(Long id, Prestamo prestamoActualizado) {
        Prestamo prestamo = obtenerPrestamoPorId(id);
        if (prestamo != null) {
            if (prestamoActualizado.getUsuario() != null && prestamoActualizado.getUsuario().getId() != null) {
                User usuario = entityManager.find(User.class, prestamoActualizado.getUsuario().getId());
                if (usuario != null) {
                    prestamo.setUsuario(usuario);
                }
            }
            
            if (prestamoActualizado.getLibro() != null && prestamoActualizado.getLibro().getId() != null) {
                Libro libro = entityManager.find(Libro.class, prestamoActualizado.getLibro().getId());
                if (libro != null) {
                    prestamo.setLibro(libro);
                }
            }
            
            prestamo.setFechaPrestamo(prestamoActualizado.getFechaPrestamo());
            prestamo.setFechaDevolucionEsperada(prestamoActualizado.getFechaDevolucionEsperada());
            prestamo.setFechaDevolucionReal(prestamoActualizado.getFechaDevolucionReal());
            prestamo.setEstado(prestamoActualizado.getEstado());
            
            entityManager.merge(prestamo);
            return prestamo;
        }
        return null;
    }
    
    @Override
    public List<Object[]> obtenerLibrosMasPrestadosPorCategoria() {
        Query query = entityManager.createQuery(
            "SELECT c.nombre, l.titulo, COUNT(p.id) as cantidad " +
            "FROM Prestamo p " +
            "JOIN p.libro l " +
            "JOIN l.categoria c " +
            "GROUP BY c.nombre, l.titulo " +
            "ORDER BY c.nombre, cantidad DESC");
        return query.getResultList();
    }
    
  @Override
public List<Object[]> obtenerPromedioRetrasosPorMes() {
    Query query = entityManager.createNativeQuery(
        "SELECT MONTH(fecha_devolucion_real) AS mes, " +
        "YEAR(fecha_devolucion_real) AS anio, " +
        "AVG(DATEDIFF(fecha_devolucion_real, fecha_devolucion_esperada)) AS promedioRetraso " +
        "FROM prestamos " +
        "WHERE fecha_devolucion_real IS NOT NULL " +
        "AND fecha_devolucion_real > fecha_devolucion_esperada " +
        "GROUP BY YEAR(fecha_devolucion_real), MONTH(fecha_devolucion_real) " +
        "ORDER BY anio, mes"
    );

    return query.getResultList();
}
}
