/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author JUAN JOSE
 */
public interface UserRepository extends JpaRepository<User, Long>{

    public List<User> findByOrderBySancionesDesc();

    public List<User> findTop5ByOrderBySancionesDesc();

    public boolean existsByCedula(String cedula);

    public boolean existsByEmail(String email);
    
}
