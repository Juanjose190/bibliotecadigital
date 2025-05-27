/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.Prestamo;
import Model.Sancion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author JUAN JOSE
 */

public interface SancionRepository extends JpaRepository<Sancion, Long> {
    boolean existsByPrestamo(Prestamo prestamo);
    List<Sancion> findByUserId(Long userId);
    List<Sancion> findByUserIdAndActivaTrue(Long userId);

    public boolean existsByUserIdAndActivaTrue(Long usuarioId);
    
     @Query("SELECT COUNT(s) > 0 FROM Sancion s WHERE s.prestamo.id = :prestamoId")
    boolean existsByPrestamoId(@Param("prestamoId") Long prestamoId);
    
    // Método para verificar sanciones activas por usuario
    @Query("SELECT COUNT(s) > 0 FROM Sancion s WHERE s.user.id = :usuarioId AND s.activa = true")
    boolean existsByUsuarioIdAndActivaTrue(@Param("usuarioId") Long usuarioId);
    
    // Método para obtener sanciones activas de un usuario
    @Query("SELECT s FROM Sancion s WHERE s.user.id = :usuarioId AND s.activa = true")
    List<Sancion> findByUsuarioIdAndActivaTrue(@Param("usuarioId") Long usuarioId);
    
    
    boolean existsByPrestamoAndActivaTrue(Prestamo prestamo);

}