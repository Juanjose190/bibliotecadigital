/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

import Model.Prestamo;
import java.util.List;

/**
 *
 * @author JUAN JOSE
 */
public interface IPrestamo {
    
    Prestamo registrarPrestamo(Prestamo prestamo);
    Prestamo obtenerPrestamoPorId(Long id);
    List<Prestamo> obtenerTodosLosPrestamos();
    void eliminarPrestamo(Long id);
    Prestamo actualizarPrestamo(Long id, Prestamo prestamoActualizado);

    // Consulta avanzada
    List<Object[]> obtenerLibrosMasPrestadosPorCategoria();

    // Consulta avanzada2
    List<Object[]> obtenerPromedioRetrasosPorMes();
}
