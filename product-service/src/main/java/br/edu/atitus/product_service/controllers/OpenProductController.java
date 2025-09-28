package br.edu.atitus.product_service.controllers;

import br.edu.atitus.product_service.entities.ProductEntry;
import br.edu.atitus.product_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class OpenProductController {

    @Value("${server.port}")
    private int serverPort;

    private final ProductRepository repository;

    public OpenProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{idProduct}/{targetCurrency}")
    public ResponseEntity<ProductEntry> getProduct(
            @PathVariable("idProduct") Long idProduct,
            @PathVariable("targetCurrency") String targetCurrency
    ) throws Exception {

        ProductEntry product = repository.findById(idProduct)
                .orElseThrow(() -> new Exception("Produto não encontrado"));

        product.setConvertedPrice(product.getPrice());

        product.setEnvironment("Product Service running on port: " + serverPort);

        return ResponseEntity.ok(product);
    }
}