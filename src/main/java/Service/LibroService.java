package Service;

import Interfaces.ILibro;
import Model.Categoria;
import Model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class LibroService implements ILibro {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private DeteccionDuplicadoService deteccionDuplicadosService;
    
    @Override
public Libro crearLibro(Libro libro) {
    // Validar categoría
    if (libro.getCategoria() == null || libro.getCategoria().getId() == null) {
        throw new IllegalArgumentException("El libro debe tener una categoría válida");
    }

    // Verificar que la categoría existe
Categoria categoria = entityManager.find(Categoria.class, libro.getCategoria().getId());
if (categoria == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría con ID " + libro.getCategoria().getId() + " no existe");
}

libro.setCategoria(categoria);

        
    
        if (categoria == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría especificada no existe");
}

    
    libro.setCategoria(categoria);

    // Resto de la lógica de duplicados...
    List<Libro> librosExistentes = obtenerTodosLosLibros();
    List<Libro> duplicadosPotenciales = deteccionDuplicadosService.detectarDuplicados(
        libro.getTitulo(), librosExistentes
    );
    
    if (!duplicadosPotenciales.isEmpty()) {
        StringBuilder mensaje = new StringBuilder("ADVERTENCIA: Posibles duplicados encontrados:\n");
        for (Libro duplicado : duplicadosPotenciales) {
            mensaje.append("- ").append(duplicado.getTitulo())
                   .append(" por ").append(duplicado.getAutor()).append("\n");
        }
        System.out.println(mensaje.toString()); 
    }
    
    entityManager.persist(libro);
    return libro;
}
    
   
    public List<Libro> verificarDuplicados(String titulo) {
        List<Libro> librosExistentes = obtenerTodosLosLibros();
        return deteccionDuplicadosService.detectarDuplicados(titulo, librosExistentes);
    }
    
    @Override
    public Libro obtenerLibroPorId(Long id) {
        return entityManager.find(Libro.class, id);
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        TypedQuery<Libro> query = entityManager.createQuery("SELECT l FROM Libro l", Libro.class);
        return query.getResultList();
    }

    @Override
    public void eliminarLibro(Long id) {
        Libro libro = obtenerLibroPorId(id);
        if (libro != null) {
            entityManager.remove(libro);
        }
    }
  
@Override
public Libro actualizarLibro(Long id, Libro libroActualizado) {
    Libro libro = obtenerLibroPorId(id);
    if (libro != null) {
        if (!libro.getTitulo().equals(libroActualizado.getTitulo())) {
            List<Libro> librosExistentes = obtenerTodosLosLibros();

            librosExistentes.removeIf(l -> l.getId().equals(id));

            List<Libro> duplicadosPotenciales = deteccionDuplicadosService.detectarDuplicados(
                libroActualizado.getTitulo(), librosExistentes
            );

            if (!duplicadosPotenciales.isEmpty()) {
                StringBuilder mensaje = new StringBuilder("ADVERTENCIA: Posibles duplicados encontrados al actualizar:\n");
                for (Libro duplicado : duplicadosPotenciales) {
                    mensaje.append("- ").append(duplicado.getTitulo())
                           .append(" por ").append(duplicado.getAutor()).append("\n");
                }
                System.out.println(mensaje.toString());
            }
        }

        libro.setTitulo(libroActualizado.getTitulo());
        libro.setAutor(libroActualizado.getAutor());
        libro.setFechaPublicacion(libroActualizado.getFechaPublicacion());
        libro.setCopiasDisponibles(libroActualizado.getCopiasDisponibles());
        libro.setCategoria(libroActualizado.getCategoria());

   

        entityManager.merge(libro);
        return libro;
    }
    return null;
}

    
    @Override
    public List<Libro> buscarPorAutorYRango(String autor, LocalDate inicio, LocalDate fin) {
        TypedQuery<Libro> query = entityManager.createQuery(
                "SELECT l FROM Libro l WHERE l.autor = :autor " +
                "AND l.fechaPublicacion BETWEEN :inicio AND :fin", Libro.class);
        query.setParameter("autor", autor);
        query.setParameter("inicio", inicio);
        query.setParameter("fin", fin);
        return query.getResultList();
    }
}