/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Interfaces.ILibro;
import Model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LibroService implements ILibro {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Libro crearLibro(Libro libro) {
        entityManager.persist(libro);
        return libro;
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