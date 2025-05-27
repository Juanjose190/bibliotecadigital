package Service;

import Model.Libro;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class DeteccionDuplicadoService {
    
    private static final double UMBRAL_SIMILITUD = 0.85;
  
    public List<Libro> detectarDuplicados(String nuevoTitulo, List<Libro> librosExistentes) {
        List<Libro> duplicadosPotenciales = new ArrayList<>();
        
        for (Libro libro : librosExistentes) {
            double similitud = calcularSimilitudJaccard(
                normalizarTexto(nuevoTitulo), 
                normalizarTexto(libro.getTitulo())
            );
            
            if (similitud >= UMBRAL_SIMILITUD) {
                duplicadosPotenciales.add(libro);
            }
        }
        
        return duplicadosPotenciales;
    }
    
 
    private double calcularSimilitudJaccard(String texto1, String texto2) {
        if (texto1 == null || texto2 == null) {
            return 0.0;
        }
        
        if (texto1.equals(texto2)) {
            return 1.0;
        }
        
        List<String> ngramas1 = crearNGramas(texto1, 3);
        List<String> ngramas2 = crearNGramas(texto2, 3);
        
        if (ngramas1.isEmpty() && ngramas2.isEmpty()) {
            return 1.0;
        }
        
        if (ngramas1.isEmpty() || ngramas2.isEmpty()) {
            return 0.0;
        }

        List<String> interseccion = new ArrayList<>(ngramas1);
        interseccion.retainAll(ngramas2);
 
        List<String> union = new ArrayList<>(ngramas1);
        for (String ngrama : ngramas2) {
            if (!union.contains(ngrama)) {
                union.add(ngrama);
            }
        }
        
        return (double) interseccion.size() / union.size();
    }
    
  
    private List<String> crearNGramas(String texto, int n) {
        List<String> ngramas = new ArrayList<>();
        
        if (texto.length() < n) {
            ngramas.add(texto);
            return ngramas;
        }
        
        for (int i = 0; i <= texto.length() - n; i++) {
            ngramas.add(texto.substring(i, i + n));
        }
        
        return ngramas;
    }
    
   
    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        
        return texto.toLowerCase()
                   .replaceAll("[^a-záéíóúüñ0-9\\s]", "") 
                   .replaceAll("\\s+", " ") 
                   .trim();
    }
  
    public double calcularSimilitudLevenshtein(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }
        
        if (s1.equals(s2)) {
            return 1.0;
        }
        
        int distancia = calcularDistanciaLevenshtein(s1, s2);
        int maxLongitud = Math.max(s1.length(), s2.length());
        
        return 1.0 - (double) distancia / maxLongitud;
    }
    
    private int calcularDistanciaLevenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1]
                    );
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
}