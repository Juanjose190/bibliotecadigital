package Service;

import Interfaces.ICategory;
import Model.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoriaService implements ICategory {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Categoria crearCategoria(Categoria categoria) {
        entityManager.persist(categoria);
        return categoria;
    }
    
    @Override
    public Categoria obtenerCategoriaPorId(Long id) {
        return entityManager.find(Categoria.class, id);
    }
    
    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        TypedQuery<Categoria> query = entityManager.createQuery("SELECT c FROM Categoria c", Categoria.class);
        return query.getResultList();
    }
    
    @Override
    public void eliminarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        if (categoria != null) {
            entityManager.remove(categoria);
        }
    }
    
    @Override
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Categoria categoria = obtenerCategoriaPorId(id);
        if (categoria != null) {
            categoria.setNombre(categoriaActualizada.getNombre());
            categoria.setDescripcion(categoriaActualizada.getDescripcion());
            entityManager.merge(categoria);
            return categoria;
        }
        return null;
    }
}