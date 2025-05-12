/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author JUAN JOSE
 */
public interface CategoryRepository extends JpaRepository<Categoria, Long>{
    
}
