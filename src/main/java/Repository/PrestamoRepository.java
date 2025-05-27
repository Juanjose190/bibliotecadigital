/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Interfaces.IPrestamo;
import Model.Prestamo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author JUAN JOSE
 */
public interface PrestamoRepository extends JpaRepository<Prestamo, Long>{

    public  List<Prestamo> findByDevueltoFalseAndFechaDevolucionRealIsNullAndFechaDevolucionEsperadaBefore(LocalDate hoy);


    

     @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro")
    List<Prestamo> findAllConLibro();



    
}
