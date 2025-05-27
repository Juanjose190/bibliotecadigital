/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.Libro;
import Model.Prestamo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author JUAN JOSE
 */
public interface LibroRepository extends JpaRepository<Libro, Long>{

    public Libro save(Libro libro);

    public List<Libro> findByAutorAndFechaPublicacionBetween(String autor, LocalDate inicio, LocalDate fin);
    
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro")
List<Prestamo> findAllConLibro();

    
}
