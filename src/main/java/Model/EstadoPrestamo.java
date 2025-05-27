/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author JUAN JOSE
 */
public enum EstadoPrestamo {
    ACTIVO("ACTIVO"),
    RETRASADO("RETRASADO"),
    COMPLETADO("COMPLETADO");
    
    private final String valor;
    
    EstadoPrestamo(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public String toString() {
        return valor;
    }
}