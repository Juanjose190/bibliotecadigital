/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author JUAN JOSE
 */
import Model.Sancion;
import Repository.SancionRepository;
import Service.SancionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sanciones")
public class SancionController {

    @Autowired
    private SancionRepository sancionRepository;
    
    @Autowired
    private SancionService sancionService;

    @GetMapping
    public List<Sancion> listarTodas() {
        return sancionRepository.findAll();
    }

    @GetMapping("/usuario/{id}")
    public List<Sancion> listarPorUsuario(@PathVariable Long id) {
        return sancionRepository.findByUserId(id);
    }
    
    @GetMapping("/activas/usuario/{id}")
    public List<Sancion> listarSancionesActivasPorUsuario(@PathVariable Long id) {
        return sancionService.obtenerSancionesActivasUsuario(id);
    }
    
    @PostMapping("/finalizar/{id}")
    public ResponseEntity<?> finalizarSancion(@PathVariable Long id) {
        Sancion sancion = sancionRepository.findById(id).orElse(null);
        if (sancion != null) {
            sancion.setActiva(false);
            sancionRepository.save(sancion);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}