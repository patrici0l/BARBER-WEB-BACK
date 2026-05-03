package com.barberia.barberia_backend.product;

import com.barberia.barberia_backend.product.dto.ProductRequest;
import com.barberia.barberia_backend.product.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // --- Métodos de Lectura (Read) ---

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProductsForAdmin() {
        return productRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getActiveProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no disponible con id: " + id));

        return toResponse(product);
    }

    // --- Métodos de Escritura (Write) ---

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }

        Product product = new Product();
        mapRequestToEntity(request, product);
        product.setActive(request.getActive() != null ? request.getActive() : true);

        Product savedProduct = productRepository.save(product);
        return toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductById(id);

        // Validación: Que no exista otro producto (ID diferente) con el mismo nombre
        if (productRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre");
        }

        mapRequestToEntity(request, product);

        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse updateStock(Long id, Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        Product product = findProductById(id);
        product.setStock(stock);

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse activateProduct(Long id) {
        Product product = findProductById(id);
        product.setActive(true);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse deactivateProduct(Long id) {
        Product product = findProductById(id);
        product.setActive(false);
        return toResponse(productRepository.save(product));
    }

    // --- Métodos Auxiliares y Mapeo ---

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }

    /**
     * Mapea los datos comunes del DTO a la Entidad para evitar repetición de código.
     */
    private void mapRequestToEntity(ProductRequest request, Product product) {
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
    }

    private ProductResponse toResponse(Product product) {
        // Un producto está "disponible" si está activo y tiene stock mayor a cero
        boolean available = Boolean.TRUE.equals(product.getActive()) 
                && product.getStock() != null 
                && product.getStock() > 0;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getActive(),
                available);
    }
}