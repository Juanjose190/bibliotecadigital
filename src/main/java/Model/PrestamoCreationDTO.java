package Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class PrestamoCreationDTO {
    private Long usuarioId;
    private Long libroId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPrestamo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDevolucionEsperada;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDevolucionReal;

    private String estado;
    
    private Boolean devuelto; // Añade este campo si es necesario

    // Constructor vacío requerido por Jackson
    public PrestamoCreationDTO() {
    }

    // Constructor con todos los campos (opcional pero útil)
    public PrestamoCreationDTO(Long usuarioId, Long libroId, LocalDate fechaPrestamo,
                               LocalDate fechaDevolucionEsperada, LocalDate fechaDevolucionReal,
                               String estado) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
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
}
