/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author JUAN JOSE
 */
public interface PrestamoRepository extends JpaRepository<Prestamo, Long>{
    
}
