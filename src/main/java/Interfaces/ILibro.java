/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

import Model.Libro;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author JUAN JOSE
 */
public interface ILibro {
    
    Libro crearLibro(Libro libro);
    Libro obtenerLibroPorId(Long id);
    List<Libro> obtenerTodosLosLibros();
    void eliminarLibro(Long id);
    Libro actualizarLibro(Long id, Libro libroActualizado);

  
    List<Libro> buscarPorAutorYRango(String autor, LocalDate inicio, LocalDate fin);
}
