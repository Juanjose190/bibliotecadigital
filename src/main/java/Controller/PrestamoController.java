package Controller;

import Interfaces.IPrestamo;
import Model.Prestamo;
import Model.PrestamoCreationDTO;
import Model.PrestamoUpdateDTO;
import Service.PrestamoService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestamos")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class PrestamoController {

    @Autowired
    private IPrestamo prestamoService;

    @Autowired
    private PrestamoService prestamoServiceImpl;
    

    @PostMapping
    public ResponseEntity<?> registrarPrestamo(@RequestBody PrestamoCreationDTO prestamoDto) { // ¡CAMBIO AQUÍ!
        try {
            // El controlador pasa el DTO al servicio, que se encargará de buscar las entidades
            Prestamo nuevoPrestamo = prestamoService.registrarPrestamo(prestamoDto); // ¡CAMBIO AQUÍ!
            return new ResponseEntity<>(nuevoPrestamo, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", e.getMessage())
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    
     @GetMapping("/conteo-retrasos")
public ResponseEntity<List<Map<String, Object>>> obtenerConteoRetrasosPorMes() {
    List<Object[]> resultados = prestamoService.obtenerConteoRetrasosPorMes();

    List<Map<String, Object>> response = resultados.stream().map(resultado -> {
        Map<String, Object> map = new HashMap<>();
        map.put("mes", resultado[0]);
        map.put("año", resultado[1]);
        map.put("conteoRetrasos", resultado[2]);
        return map;
    }).collect(Collectors.toList());

    return new ResponseEntity<>(response, HttpStatus.OK);
}


    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> obtenerPrestamoPorId(@PathVariable Long id) {
        Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);
        if (prestamo != null) {
            return new ResponseEntity<>(prestamo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Prestamo>> obtenerTodosLosPrestamos() {
        List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
        return new ResponseEntity<>(prestamos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);
        if (prestamo != null) {
            prestamoService.eliminarPrestamo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

 @PutMapping("/{id}")
    public ResponseEntity<Prestamo> actualizarPrestamo(
        @PathVariable Long id,
        @RequestBody PrestamoUpdateDTO prestamoDto) {

        Prestamo prestamoActualizado = prestamoService.actualizarPrestamo(id, prestamoDto);
        return ResponseEntity.ok(prestamoActualizado);
    }
   
    @PutMapping("/{id}/devolver")
    public ResponseEntity<Map<String, Object>> marcarComoDevuelto(@PathVariable Long id) {
        Prestamo prestamo = prestamoServiceImpl.marcarComoDevuelto(id);
        Map<String, Object> response = new HashMap<>();
        
        if (prestamo != null) {
            response.put("prestamo", prestamo);
            response.put("mensaje", "Préstamo marcado como devuelto exitosamente");
            response.put("devuelto", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Préstamo no encontrado o ya devuelto");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    
  
    @GetMapping("/retrasados")
    public ResponseEntity<List<Prestamo>> obtenerPrestamosRetrasados() {
        List<Prestamo> prestamosRetrasados = prestamoServiceImpl.obtenerPrestamosRetrasados();
        return new ResponseEntity<>(prestamosRetrasados, HttpStatus.OK);
    }
    
    
    @GetMapping("/activos")
    public ResponseEntity<List<Prestamo>> obtenerPrestamosActivos() {
        List<Prestamo> prestamosActivos = prestamoServiceImpl.obtenerPrestamosActivos();
        return new ResponseEntity<>(prestamosActivos, HttpStatus.OK);
    }
    
  
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        List<Prestamo> todosPrestamos = prestamoService.obtenerTodosLosPrestamos();
        List<Prestamo> prestamosActivos = prestamoServiceImpl.obtenerPrestamosActivos();
        List<Prestamo> prestamosRetrasados = prestamoServiceImpl.obtenerPrestamosRetrasados();
        
        long prestamosCompletados = todosPrestamos.stream()
            .filter(p -> p.getFechaDevolucionReal() != null)
            .count();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalPrestamos", todosPrestamos.size());
        estadisticas.put("prestamosActivos", prestamosActivos.size());
        estadisticas.put("prestamosRetrasados", prestamosRetrasados.size());
        estadisticas.put("prestamosCompletados", prestamosCompletados);
        
        return new ResponseEntity<>(estadisticas, HttpStatus.OK);
    }
    
  
    @PostMapping("/actualizar-estados")
    public ResponseEntity<Map<String, Object>> actualizarEstados() {
        prestamoServiceImpl.actualizarEstadosAutomaticamente();
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Estados actualizados exitosamente");
        response.put("actualizado", true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/libros-mas-prestados")
    public ResponseEntity<List<Map<String, Object>>> obtenerLibrosMasPrestadosPorCategoria() {
        List<Object[]> resultados = prestamoService.obtenerLibrosMasPrestadosPorCategoria();

        List<Map<String, Object>> response = resultados.stream().map(resultado -> Map.of(
                "categoria", resultado[0],
                "titulo", resultado[1],
                "cantidadPrestamos", resultado[2]
        )).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/promedio-retrasos")
    public ResponseEntity<List<Map<String, Object>>> obtenerPromedioRetrasosPorMes() {
        List<Object[]> resultados = prestamoService.obtenerPromedioRetrasosPorMes();

        List<Map<String, Object>> response = resultados.stream().map(resultado -> Map.of(
                "mes", resultado[0],
                "año", resultado[1],
                "promedioRetraso", resultado[2]
        )).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @PostMapping("/revisar-sanciones")
public ResponseEntity<Map<String, Object>> revisarSanciones() {
    try {
        Map<String, Object> resultado = prestamoServiceImpl.revisarYAplicarSanciones();
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error al revisar sanciones: " + e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@PostMapping("/procesar-automatico")
public ResponseEntity<Map<String, Object>> procesarAutomatico() {
    try {
        prestamoServiceImpl.procesarPrestamosAutomaticamente();
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Procesamiento automático ejecutado exitosamente");
        response.put("ejecutado", true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error en procesamiento automático: " + e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    

    
}

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PrestamoService servicioPrestamo;

    public AdminController(PrestamoService servicioPrestamo) {
        this.servicioPrestamo = servicioPrestamo;
    }

    @GetMapping("/aplicar-sanciones")
    public ResponseEntity<?> aplicarSancionesManualmente() {
        Map<String, Object> resultado = servicioPrestamo.revisarYAplicarSanciones();
        return ResponseEntity.ok(resultado);
    }
    
    
    


    
    
    
}





}