package Controller;

import Interfaces.IPrestamo;
import Model.Prestamo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private IPrestamo prestamoService;

    @PostMapping
    public ResponseEntity<Prestamo> registrarPrestamo(@RequestBody Prestamo prestamo) {
        Prestamo nuevoPrestamo = prestamoService.registrarPrestamo(prestamo);
        return new ResponseEntity<>(nuevoPrestamo, HttpStatus.CREATED);
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
    public ResponseEntity<Prestamo> actualizarPrestamo(@PathVariable Long id, @RequestBody Prestamo prestamo) {
        Prestamo prestamoActualizado = prestamoService.actualizarPrestamo(id, prestamo);
        if (prestamoActualizado != null) {
            return new ResponseEntity<>(prestamoActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/libros-mas-prestados")
    public ResponseEntity<List<Map<String, Object>>> obtenerLibrosMasPrestadosPorCategoria() {
        List<Object[]> resultados = prestamoService.obtenerLibrosMasPrestadosPorCategoria();
        
        List<Map<String, Object>> response = resultados.stream().map(resultado -> {
            Map<String, Object> item = Map.of(
                "categoria", resultado[0],
                "titulo", resultado[1],
                "cantidadPrestamos", resultado[2]
            );
            return item;
        }).collect(Collectors.toList());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/promedio-retrasos")
    public ResponseEntity<List<Map<String, Object>>> obtenerPromedioRetrasosPorMes() {
        List<Object[]> resultados = prestamoService.obtenerPromedioRetrasosPorMes();
        
        List<Map<String, Object>> response = resultados.stream().map(resultado -> {
            Map<String, Object> item = Map.of(
                "mes", resultado[0],
                "anio", resultado[1],
                "promedioRetraso", resultado[2]
            );
            return item;
        }).collect(Collectors.toList());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}