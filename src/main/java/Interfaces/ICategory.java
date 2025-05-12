/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

import Model.Categoria;
import java.util.List;

/**
 *
 * @author JUAN JOSE
 */
public interface ICategory {
    
    Categoria crearCategoria(Categoria categoria);
    Categoria obtenerCategoriaPorId(Long id);
    List<Categoria> obtenerTodasLasCategorias();
    void eliminarCategoria(Long id);
    Categoria actualizarCategoria(Long id, Categoria categoriaActualizada);
    
}
