package Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class PrestamoUpdateDTO {
    private Long usuarioId;
    private Long libroId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPrestamo;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDevolucionEsperada;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDevolucionReal;
    
    private String estado;
    private Boolean devuelto;

    // Constructor vacío (requerido para Jackson)
    public PrestamoUpdateDTO() {
    }

    // Constructor completo
    public PrestamoUpdateDTO(Long usuarioId, Long libroId, LocalDate fechaPrestamo, 
                           LocalDate fechaDevolucionEsperada, LocalDate fechaDevolucionReal,
                           String estado, Boolean devuelto) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
        this.devuelto = devuelto;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getDevuelto() {
        return devuelto;
    }

    public void setDevuelto(Boolean devuelto) {
        this.devuelto = devuelto;
    }

    // Método toString() para logging/debugging
    @Override
    public String toString() {
        return "PrestamoUpdateDTO{" +
                "usuarioId=" + usuarioId +
                ", libroId=" + libroId +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucionEsperada=" + fechaDevolucionEsperada +
                ", fechaDevolucionReal=" + fechaDevolucionReal +
                ", estado='" + estado + '\'' +
                ", devuelto=" + devuelto +
                '}';
    }
}