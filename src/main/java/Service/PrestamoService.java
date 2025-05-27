package Service;

import Interfaces.IPrestamo;
import Model.Libro;
import Model.Prestamo;
import Model.PrestamoCreationDTO;
import Model.PrestamoUpdateDTO;
import Model.Sancion;
import Model.User;
import Repository.PrestamoRepository;
import Repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PrestamoService implements IPrestamo {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SancionService sancionService;
    
        @Autowired

        private PrestamoRepository  prestamoRepository;
    
        

 @Autowired
private UserRepository usuarioRepository;


    @Override
    public Prestamo registrarPrestamo(PrestamoCreationDTO prestamoDto) {
        if (prestamoDto.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio");
        }

        if (prestamoDto.getLibroId() == null) {
            throw new IllegalArgumentException("El ID de libro es obligatorio");
        }

        // Obtener el usuario desde la BD usando el ID del DTO
        User usuario = entityManager.find(User.class, prestamoDto.getUsuarioId());
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + prestamoDto.getUsuarioId());
        }

        // ELIMINADA LA VALIDACIÓN DE SANCIONES - Ahora se permite prestar a usuarios sancionados
        /*
        if (sancionService.usuarioTieneSancionesActivas(usuario.getId())) {
            List<Sancion> sancionesActivas = (List<Sancion>) sancionService.obtenerSancionesActivasUsuario(usuario.getId());
            LocalDateTime fechaFinSancion = sancionesActivas.stream()
                .map(Sancion::getFechaFin)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

            throw new IllegalStateException(String.format(
                "Usuario con sanciones activas. No puede realizar préstamos hasta %s",
                fechaFinSancion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));
        }
        */

        // Obtener el libro desde la BD usando el ID del DTO
        Libro libro = entityManager.find(Libro.class, prestamoDto.getLibroId());
        if (libro == null) {
            throw new EntityNotFoundException("Libro no encontrado con ID: " + prestamoDto.getLibroId());
        }

        if (!libro.isDisponible()) {
            throw new IllegalStateException("El libro no está disponible para préstamo");
        }

        // Crear la entidad Prestamo a partir del DTO y las entidades cargadas
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(prestamoDto.getFechaPrestamo() != null ? prestamoDto.getFechaPrestamo() : LocalDate.now());
        prestamo.setFechaDevolucionEsperada(prestamoDto.getFechaDevolucionEsperada());
        prestamo.setFechaDevolucionReal(prestamoDto.getFechaDevolucionReal());
        prestamo.setEstado(prestamoDto.getEstado() != null ? prestamoDto.getEstado() : "EN_CURSO");

        entityManager.persist(prestamo);
        entityManager.flush();
        entityManager.refresh(prestamo);

        return prestamo;
    }

    @Override
    public Prestamo obtenerPrestamoPorId(Long id) {
        Prestamo prestamo = entityManager.find(Prestamo.class, id);
        if (prestamo != null) {
            actualizarEstadoPrestamo(prestamo);

            // Verificación optimizada
            if (prestamo.getEstado().equals("RETRASADO") &&
                prestamo.getFechaDevolucionReal() == null &&
                !sancionService.existeSancionParaPrestamo(prestamo.getId())) {

                sancionService.sancionarUsuario(prestamo.getUsuario(), prestamo);
            }
        }
        return prestamo;
    }

    @Override
    public List<Prestamo> obtenerTodosLosPrestamos() {
        TypedQuery<Prestamo> query = entityManager.createQuery(
            "SELECT DISTINCT p FROM Prestamo p " +
            "LEFT JOIN FETCH p.libro l " +
            "LEFT JOIN FETCH p.usuario u " +
            "ORDER BY p.fechaPrestamo DESC",
            Prestamo.class);
        
        return query.getResultList();
    }

    @Override
    public void eliminarPrestamo(Long id) {
        Prestamo prestamo = obtenerPrestamoPorId(id);
        if (prestamo != null) {
            entityManager.remove(prestamo);
        }
    }
    
    
    
    
    @Override
public Prestamo actualizarPrestamo(Long id, PrestamoUpdateDTO prestamoActualizado) {
    
    
    
    
    
    // 1. Buscar el préstamo existente
    Prestamo prestamo = obtenerPrestamoPorId(id);
    if (prestamo == null) {
        throw new EntityNotFoundException("Préstamo no encontrado con ID: " + id);
    }

    // 2. Actualizar relaciones
    if (prestamoActualizado.getUsuarioId() != null) {
        User usuario = entityManager.find(User.class, prestamoActualizado.getUsuarioId());
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + prestamoActualizado.getUsuarioId());
        }
        prestamo.setUsuario(usuario);
    }

    if (prestamoActualizado.getLibroId() != null) {
        Libro libro = entityManager.find(Libro.class, prestamoActualizado.getLibroId());
        if (libro == null) {
            throw new EntityNotFoundException("Libro no encontrado con ID: " + prestamoActualizado.getLibroId());
        }
        if (!libro.isDisponible() && !libro.equals(prestamo.getLibro())) {
            throw new IllegalStateException("El libro ID " + libro.getId() + " no está disponible");
        }
        prestamo.setLibro(libro);
    }

    // 3. Actualizar campos simples
    if (prestamoActualizado.getFechaPrestamo() != null) {
        prestamo.setFechaPrestamo(prestamoActualizado.getFechaPrestamo());
    }

    if (prestamoActualizado.getFechaDevolucionEsperada() != null) {
        prestamo.setFechaDevolucionEsperada(prestamoActualizado.getFechaDevolucionEsperada());
    }

    if (prestamoActualizado.getFechaDevolucionReal() != null) {
        prestamo.setFechaDevolucionReal(prestamoActualizado.getFechaDevolucionReal());
    }

    if (prestamoActualizado.getDevuelto() != null) {
        prestamo.setDevuelto(prestamoActualizado.getDevuelto());
    }

    // 4. Lógica de estado y sanción
   // 4. Lógica de estado y sanción
if (prestamo.getFechaDevolucionReal() != null) {
    if (prestamo.getFechaDevolucionReal().isAfter(prestamo.getFechaDevolucionEsperada())) {
        prestamo.setEstado("RETRASADO");
        aplicarSancionSiCorresponde(prestamo); // ✅ Aquí se sanciona aunque ya esté devuelto
    } else {
        prestamo.setEstado("COMPLETADO");
    }
} else {
    if (LocalDate.now().isAfter(prestamo.getFechaDevolucionEsperada())) {
        prestamo.setEstado("RETRASADO");
        aplicarSancionSiCorresponde(prestamo); // ✅ También sancionas si aún no se devuelve
    } else {
        prestamo.setEstado("EN_CURSO");
    }
}

    
 


    return entityManager.merge(prestamo);
}

    private void actualizarDisponibilidadLibro(Prestamo prestamo) {
        Libro libroActual = prestamo.getLibro();
        Libro libroAnterior = entityManager.find(Libro.class, prestamo.getLibro().getId());
        
        // Si se cambió el libro, actualizar disponibilidad de ambos
        if (!libroActual.equals(libroAnterior)) {
            libroAnterior.setCopiasDisponibles(libroAnterior.getCopiasDisponibles() + 1);
            entityManager.merge(libroAnterior);
            
            libroActual.setCopiasDisponibles(libroActual.getCopiasDisponibles() - 1);
            entityManager.merge(libroActual);
        }
    }

    public Prestamo marcarComoDevuelto(Long prestamoId) {
        Prestamo prestamo = obtenerPrestamoPorId(prestamoId);
        if (prestamo != null && prestamo.getFechaDevolucionReal() == null) {
            prestamo.setFechaDevolucionReal(LocalDate.now());
            prestamo.setDevuelto(true);
            entityManager.merge(prestamo);
        }
        return prestamo;
    }
    
      @Autowired
    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    public List<Prestamo> obtenerPrestamosRetrasados() {
        LocalDate hoy = LocalDate.now();
        return prestamoRepository.findByDevueltoFalseAndFechaDevolucionRealIsNullAndFechaDevolucionEsperadaBefore(hoy);
    }
    
    

 
    public List<Prestamo> obtenerPrestamosActivos() {
        TypedQuery<Prestamo> query = entityManager.createQuery(
            "SELECT p FROM Prestamo p " +
            "WHERE p.fechaDevolucionReal IS NULL " +
            "AND p.fechaDevolucionEsperada >= :fechaActual", Prestamo.class);
        query.setParameter("fechaActual", LocalDate.now());

        List<Prestamo> prestamos = query.getResultList();

        for (Prestamo prestamo : prestamos) {
            if (!prestamo.getEstado().equals("EN_CURSO")) {
                prestamo.setEstado("EN_CURSO");
                entityManager.merge(prestamo);
            }
        }

        return prestamos;
    }

  private void actualizarEstadoPrestamo(Prestamo prestamo) {
    if (prestamo.getFechaDevolucionReal() != null) {
        // Caso: Préstamo devuelto
        if (prestamo.getFechaDevolucionReal().isAfter(prestamo.getFechaDevolucionEsperada())) {
            prestamo.setEstado("RETRASADO");
            aplicarSancionPorRetraso(prestamo); // ¡Nuevo método para sanciones!
        } else {
            prestamo.setEstado("COMPLETADO");
        }
    } else {
        // Caso: Préstamo no devuelto
        if (LocalDate.now().isAfter(prestamo.getFechaDevolucionEsperada())) {
            prestamo.setEstado("RETRASADO");
            aplicarSancionPorRetraso(prestamo); // ¡Nuevo método para sanciones!
        } else {
            prestamo.setEstado("EN_CURSO");
        }
    }
}

// Método auxiliar para aplicar sanciones
private void aplicarSancionPorRetraso(Prestamo prestamo) {
    // Verificar si ya existe una sanción activa para este préstamo
    if (!sancionService.existeSancionParaPrestamo(prestamo.getId())) {
        Sancion sancion = new Sancion();
        sancion.setDescripcion("Devolución atrasada del libro " + prestamo.getLibro().getTitulo());
        sancion.setFechaInicio(LocalDateTime.now());
        sancion.setFechaFin(LocalDateTime.now().plusDays(7)); // 7 días de sanción
        sancion.setActiva(true);
        sancion.setUser(prestamo.getUsuario());
        sancion.setPrestamo(prestamo);
        
        entityManager.persist(sancion);
        
        // Incrementar contador de sanciones del usuario
        User usuario = prestamo.getUsuario();
        usuario.setSanciones(usuario.getSanciones() + 1);
        entityManager.merge(usuario);
    }
}

    // MÉTODOS PROGRAMADOS PARA SANCIONES AUTOMÁTICAS

    @Scheduled(cron = "0 1 0 * * ?") // Se ejecuta diariamente a las 00:01
    public void aplicarSancionesAutomaticas() {
        System.out.println("Ejecutando aplicación automática de sanciones...");
        
        // Obtener todos los préstamos retrasados sin sanción
        TypedQuery<Prestamo> query = entityManager.createQuery(
            "SELECT p FROM Prestamo p " +
            "WHERE p.fechaDevolucionReal IS NULL " +
            "AND p.fechaDevolucionEsperada < :fechaActual " +
            "AND p.estado = :estadoRetrasado",
            Prestamo.class);
        
        query.setParameter("fechaActual", LocalDate.now());
        query.setParameter("estadoRetrasado", "RETRASADO");
        
        List<Prestamo> prestamosRetrasados = query.getResultList();
        
        int sancionesAplicadas = 0;
        
        for (Prestamo prestamo : prestamosRetrasados) {
            // Verificar si ya existe una sanción para este préstamo
            if (!sancionService.existeSancionParaPrestamo(prestamo.getId())) {
                try {
                    sancionService.sancionarUsuario(prestamo.getUsuario(), prestamo);
                    sancionesAplicadas++;
                    System.out.println("Sanción aplicada al usuario " + 
                        prestamo.getUsuario().getNombre() + 
                        " por préstamo ID " + prestamo.getId());
                } catch (Exception e) {
                    System.err.println("Error al aplicar sanción para préstamo ID " + 
                        prestamo.getId() + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("Total de sanciones aplicadas: " + sancionesAplicadas);
        entityManager.flush();
    }

    @Scheduled(cron = "0 1 0 * * ?") // Se ejecuta diariamente a las 00:01
    public void actualizarEstadosAutomaticamente() {
        System.out.println("Ejecutando actualización automática de estados de préstamos...");

        Query queryRetrasados = entityManager.createQuery(
            "UPDATE Prestamo p SET p.estado = :estadoRetrasado " +
            "WHERE p.fechaDevolucionReal IS NULL " +
            "AND p.fechaDevolucionEsperada < :fechaActual " +
            "AND p.estado != :estadoRetrasado");
        queryRetrasados.setParameter("estadoRetrasado", "RETRASADO");
        queryRetrasados.setParameter("fechaActual", LocalDate.now());

        int prestamosActualizados = queryRetrasados.executeUpdate();
        System.out.println("Préstamos marcados como retrasados: " + prestamosActualizados);

        entityManager.flush();
    }

    // Método mejorado que combina actualización de estados Y aplicación de sanciones
    @Scheduled(cron = "0 1 0 * * ?") // Se ejecuta diariamente a las 00:01
    public void procesarPrestamosAutomaticamente() {
        System.out.println("Ejecutando procesamiento automático de préstamos...");
        
        // 1. Primero actualizar estados
        actualizarEstadosAutomaticamente();
        
        // 2. Luego aplicar sanciones
        aplicarSancionesAutomaticas();
        
        // 3. Desactivar sanciones expiradas
        desactivarSancionesExpiradas();
        
        System.out.println("Procesamiento automático completado.");
    }

    // Método adicional para desactivar sanciones que ya expiraron
    @Transactional
    public void desactivarSancionesExpiradas() {
        System.out.println("Desactivando sanciones expiradas...");
        
        Query query = entityManager.createQuery(
            "UPDATE Sancion s SET s.activa = false " +
            "WHERE s.activa = true AND s.fechaFin < :fechaActual");
        
        query.setParameter("fechaActual", LocalDateTime.now());
        
        int sancionesDesactivadas = query.executeUpdate();
        System.out.println("Sanciones desactivadas: " + sancionesDesactivadas);
        
        entityManager.flush();
    }

    // Método para forzar revisión de sanciones (útil para testing)
    @Transactional
    public Map<String, Object> revisarYAplicarSanciones() {
        Map<String, Object> resultado = new HashMap<>();
        
        // Obtener préstamos retrasados sin sanción
        List<Prestamo> prestamosRetrasados = obtenerPrestamosRetrasadosSinSancion();
        
        int sancionesAplicadas = 0;
        List<String> errores = new ArrayList<>();
        
        for (Prestamo prestamo : prestamosRetrasados) {
            try {
                sancionService.sancionarUsuario(prestamo.getUsuario(), prestamo);
                sancionesAplicadas++;
            } catch (Exception e) {
                errores.add("Error en préstamo ID " + prestamo.getId() + ": " + e.getMessage());
            }
        }
        
        resultado.put("prestamosRevisados", prestamosRetrasados.size());
        resultado.put("sancionesAplicadas", sancionesAplicadas);
        resultado.put("errores", errores);
        
        return resultado;
    }
    
    
    
    
    
    
    
    
    

    // Método auxiliar para obtener préstamos retrasados sin sanción
    private List<Prestamo> obtenerPrestamosRetrasadosSinSancion() {
        TypedQuery<Prestamo> query = entityManager.createQuery(
            "SELECT p FROM Prestamo p " +
            "WHERE p.fechaDevolucionReal IS NULL " +
            "AND p.fechaDevolucionEsperada < :fechaActual " +
            "AND NOT EXISTS (SELECT s FROM Sancion s WHERE s.prestamo.id = p.id)",
            Prestamo.class);
        
        query.setParameter("fechaActual", LocalDate.now());
        
        return query.getResultList();
    }

 @Override
    public List<Object[]> obtenerLibrosMasPrestadosPorCategoria() {
        try {
            // Consulta JPQL optimizada
            String jpql = """
                SELECT c.nombre AS categoria, 
                       l.titulo AS libro, 
                       COUNT(p.id) AS totalPrestamos,
                       l.id AS libroId
                FROM Prestamo p
                JOIN p.libro l
                JOIN l.categoria c
                WHERE p.devuelto = true
                GROUP BY c.nombre, l.titulo, l.id
                ORDER BY c.nombre, totalPrestamos DESC
                """;

            Query query = entityManager.createQuery(jpql);
            
            // Ejecutar consulta y obtener resultados
            List<Object[]> resultados = query.getResultList();
            
            if (resultados.isEmpty()) {
                return Collections.emptyList();
            }

            // Procesamiento para obtener el libro más prestado por categoría
            Map<String, Object[]> topLibrosPorCategoria = new LinkedHashMap<>();
            
            for (Object[] fila : resultados) {
                String categoria = (String) fila[0];
                Long prestamosActual = (Long) fila[2];
                
                if (!topLibrosPorCategoria.containsKey(categoria) || 
                    prestamosActual > (Long) topLibrosPorCategoria.get(categoria)[2]) {
                    
                    // Creamos un nuevo array con los datos relevantes
                    Object[] datosLibro = {
                        fila[0], // categoria
                        fila[1], // titulo libro
                        fila[2], // cantidad prestamos
                        fila[3]  // id libro (útil para enlaces)
                    };
                    topLibrosPorCategoria.put(categoria, datosLibro);
                }
            }

            return new ArrayList<>(topLibrosPorCategoria.values());
            
        } catch (Exception e) {
            // Loggear el error (usar un logger real en producción)
            System.err.println("Error al obtener libros más prestados por categoría: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Object[]> obtenerPromedioRetrasosPorMes() {
        Query query = entityManager.createNativeQuery(
            "SELECT MONTH(fecha_devolucion_real) AS mes, " +
            "YEAR(fecha_devolucion_real) AS anio, " +
            "AVG(DATEDIFF(fecha_devolucion_real, fecha_devolucion_esperada)) AS promedioRetraso " +
            "FROM prestamos " +
            "WHERE fecha_devolucion_real IS NOT NULL " +
            "AND fecha_devolucion_real > fecha_devolucion_esperada " +
            "GROUP BY YEAR(fecha_devolucion_real), MONTH(fecha_devolucion_real) " +
            "ORDER BY anio, mes"
        );

        return query.getResultList();
    }
    
    
    
    @Override
public List<Object[]> obtenerConteoRetrasosPorMes() {
    Query query = entityManager.createNativeQuery(
    "SELECT MONTH(fecha_devolucion_real) AS mes, " +
    "YEAR(fecha_devolucion_real) AS anio, " +
    "COUNT(*) AS conteoRetrasos " +
    "FROM prestamo " +  // ← tabla correcta
    "WHERE fecha_devolucion_real IS NOT NULL " +
    "AND fecha_devolucion_real > fecha_devolucion_esperada " +
    "GROUP BY YEAR(fecha_devolucion_real), MONTH(fecha_devolucion_real) " +
    "ORDER BY anio, mes"
);
    return query.getResultList();
}
    

 

  private void aplicarSancionSiCorresponde(Prestamo prestamo) {
    if (
        prestamo.getFechaDevolucionReal() != null &&
        prestamo.getFechaDevolucionEsperada() != null &&
        prestamo.getFechaDevolucionReal().isAfter(prestamo.getFechaDevolucionEsperada())
    ) {
        User usuario = prestamo.getUsuario();
        if (usuario != null) {
            usuario.setSanciones(usuario.getSanciones() + 1);
            usuarioRepository.save(usuario);
        }
    }
}


    
}