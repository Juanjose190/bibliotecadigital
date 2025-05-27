package Controller;

import Interfaces.ILibro;
import Model.Categoria;
import Model.Libro;
import Model.Prestamo;
import Repository.PrestamoRepository;
import Service.LibroService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class LibroController {

    @Autowired
    private ILibro libroService;
    
    @Autowired
    private LibroService libroServiceImpl; // Para acceder a métodos específicos
    
     @Autowired
    private PrestamoRepository prestamoRepository;
    
    
       @PersistenceContext
    private EntityManager entityManager;
     
     
     

 @PostMapping
public ResponseEntity<Map<String, Object>> crearLibro(@RequestBody Libro libro) {
    // Validar que el libro tenga categoría
    if (libro.getCategoria() == null || libro.getCategoria().getId() == null) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "El libro debe tener una categoría asignada");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Verificar duplicados
    List<Libro> duplicados = libroServiceImpl.verificarDuplicados(libro.getTitulo());
    
    Map<String, Object> response = new HashMap<>();
    
    if (!duplicados.isEmpty()) {
        response.put("advertencia", true);
        response.put("mensaje", "Se encontraron posibles libros duplicados");
        response.put("duplicadosPotenciales", duplicados);
    }
    
    // Asegurarse de que la categoría existe
        Categoria categoria = entityManager.find(Categoria.class, libro.getCategoria().getId());
    if (categoria == null) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "La categoría especificada no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    libro.setCategoria(categoria);
    Libro nuevoLibro = libroService.crearLibro(libro);
    
    response.put("libro", nuevoLibro);
    response.put("creado", true);
    
    HttpStatus status = duplicados.isEmpty() ? HttpStatus.CREATED : HttpStatus.OK;
    return new ResponseEntity<>(response, status);
}
    
    
    
    
    
    
    
    
    @PostMapping("/verificar-duplicados")
    public ResponseEntity<Map<String, Object>> verificarDuplicados(@RequestBody Map<String, String> request) {
        String titulo = request.get("titulo");
        
        if (titulo == null || titulo.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "El título es requerido");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        
        List<Libro> duplicados = libroServiceImpl.verificarDuplicados(titulo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tieneDuplicados", !duplicados.isEmpty());
        response.put("duplicadosPotenciales", duplicados);
        response.put("cantidad", duplicados.size());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerLibroPorId(@PathVariable Long id) {
        Libro libro = libroService.obtenerLibroPorId(id);
        if (libro != null) {
            return new ResponseEntity<>(libro, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Libro>> obtenerTodosLosLibros() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        return new ResponseEntity<>(libros, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        Libro libro = libroService.obtenerLibroPorId(id);
        if (libro != null) {
            libroService.eliminarLibro(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        // Verificar duplicados si se está cambiando el título
        Libro libroExistente = libroService.obtenerLibroPorId(id);
        Map<String, Object> response = new HashMap<>();
        
        if (libroExistente != null && !libroExistente.getTitulo().equals(libro.getTitulo())) {
            List<Libro> duplicados = libroServiceImpl.verificarDuplicados(libro.getTitulo());
            // Filtrar el libro actual de los duplicados
            duplicados.removeIf(l -> l.getId().equals(id));
            
            if (!duplicados.isEmpty()) {
                response.put("advertencia", true);
                response.put("mensaje", "Se encontraron posibles libros duplicados");
                response.put("duplicadosPotenciales", duplicados);
            }
        }
        
        Libro libroActualizado = libroService.actualizarLibro(id, libro);
        if (libroActualizado != null) {
            response.put("libro", libroActualizado);
            response.put("actualizado", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/ping")
    public String ping() {
        return "libros-controller-ok";
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Libro>> buscarPorAutorYRango(
            @RequestParam String autor,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        List<Libro> libros = libroService.buscarPorAutorYRango(autor, inicio, fin);
        return new ResponseEntity<>(libros, HttpStatus.OK);
    }
    
    
 

}