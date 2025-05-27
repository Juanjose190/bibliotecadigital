package Interfaces;

import Model.Prestamo;
import Model.PrestamoCreationDTO;
import Model.PrestamoUpdateDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IPrestamo {
    
    // Métodos CRUD básicos
    Prestamo registrarPrestamo(PrestamoCreationDTO prestamoDto);
    Prestamo obtenerPrestamoPorId(Long id);
    List<Prestamo> obtenerTodosLosPrestamos();
    void eliminarPrestamo(Long id);
    Prestamo actualizarPrestamo(Long id, PrestamoUpdateDTO prestamoActualizado);
    
    // Métodos para estadísticas y reportes
    List<Object[]> obtenerLibrosMasPrestadosPorCategoria();
    List<Object[]> obtenerPromedioRetrasosPorMes(); // Mantenemos el existente
    List<Object[]> obtenerConteoRetrasosPorMes();   // Añadimos el nuevo método
}