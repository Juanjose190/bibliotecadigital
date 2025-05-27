/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

import Model.User;
import java.util.List;

/**
 *
 * @author JUAN JOSE
 */
public interface IUser {
    
    User registrarUsuario(User user);
    User obtenerUsuarioPorId(Long id);
    List<User> obtenerTodosLosUsuarios();
    void eliminarUsuario(Long id);
    User actualizarUsuario(Long id, User usuarioActualizado);

    // Consulta avanzada
    List<User> obtenerUsuariosConMasSanciones();
    
}
