package com.example.lab10.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab10.client.ProductWebClient;
import com.example.lab10.model.Product;
import com.example.lab10.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ProductController — Reactive REST Controller
 *
 * ✅ @RestController, @RequestMapping, Constructor Injection ครบแล้ว
 * ✅ endpoint GET /products/{id} ทำเสร็จแล้วเป็นตัวอย่าง (30%)
 * ❌ TODO: เติม method body ของ endpoint ที่เหลือ (70%)
 *
 * Endpoints ที่ต้องทำทั้งหมด:
 *   GET    /products          → Flux<Product>   (ดึงทั้งหมด)
 *   GET    /products/{id}     → Mono<Product>   ✅ ตัวอย่างทำแล้ว
 *   POST   /products          → Mono<Product>   (บันทึก)
 *   DELETE /products/{id}     → Mono<Void>      (ลบ)
 *   GET    /products/category/{cat} → Flux<Product> (กรอง)
 *   GET    /products/{id}/price    → Mono<Double>   (ราคาหลังลด)
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    // ── Constructor Injection (DIP — SOLID) ─────────────
    private final ProductService service;
    private final ProductWebClient webClient;
    
    public ProductController(ProductService service, ProductWebClient webClient) {
        this.service = service;
        this.webClient = webClient;
    }

    // ══════════════════════════════════════════════════════
    // ✅ ตัวอย่างที่ทำเสร็จแล้ว — ศึกษาแล้วทำ endpoint ที่เหลือ
    // ══════════════════════════════════════════════════════

    /**
     * GET /products/{id}
     * คืน Mono<Product> — ค้นหา Product 1 รายการ
     *
     * ทดสอบ: GET http://localhost:8080/products/1
     */
    @GetMapping("/{id}")
    public Mono<Product> getById(@PathVariable String id) {
        return service.getById(id);
    }

    // ══════════════════════════════════════════════════════
    // ❌ TODO: เติม method body ด้านล่างนี้
    // ══════════════════════════════════════════════════════

    /**
     * GET /products
     * TODO: คืน Flux<Product> ทุกรายการ
     *
     * Hint: เรียก service.getAll()
     * ทดสอบ: GET http://localhost:8080/products
     */
     @GetMapping
    public Flux<Product> getAll() {
        return service.getAll();
    }

    /**
     * POST /products
     * TODO: รับ Product จาก request body แล้วบันทึก
     *
     * Hint: เรียก service.save(product)
     * ทดสอบ: POST http://localhost:8080/products
     *        Body: { "name": "...", "price": 999.0, ... }
     */
    @PostMapping
    public Mono<Product> save(@RequestBody Product product) {
        return service.save(product);
    }

    /**
     * DELETE /products/{id}
     * TODO: ลบ Product และคืน Mono<Void>
     *
     * Hint: เรียก service.delete(id)
     * ทดสอบ: DELETE http://localhost:8080/products/1
     */
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }

    /**
     * GET /products/category/{category}
     * TODO: คืน Flux<Product> ที่กรองตาม category
     *
     * Hint: เรียก service.getByCategory(category)
     * ทดสอบ: GET http://localhost:8080/products/category/Electronics
     */
    @GetMapping("/category/{category}")
    public Flux<Product> getByCategory(@PathVariable String category) {
        return service.getByCategory(category);
    }

    /**
     * GET /products/{id}/price
     * TODO: คืน Mono<Double> ราคาหลังส่วนลด
     *
     * Hint: เรียก service.getDiscountedPrice(id)
     * ทดสอบ: GET http://localhost:8080/products/1/price
     */
    @GetMapping("/{id}/price")
    public Mono<Double> getDiscountedPrice(@PathVariable String id) {
        return service.getDiscountedPrice(id);
    }

     // ══════════════════════════════════════════════════════
    // 🧪 Demo endpoints — เรียก ProductWebClient แล้ว chain operators
    // ══════════════════════════════════════════════════════

    /**
     * GET /products/demo/{id}
     * เรียก endpoint /products/{id} ผ่าน WebClient แล้ว chain
     * .map() → แปลงเป็นข้อความ, .defaultIfEmpty() → fallback ถ้าไม่พบ
     *
     * ทดสอบ: GET http://localhost:8080/products/demo/1
     *        GET http://localhost:8080/products/demo/999  (ไม่มีจริง)
     */
    @GetMapping("/demo/{id}")
    public Mono<String> demoGetById(@PathVariable String id) {
        return webClient.getProductById(id)
                .map(p -> "Product: " + p.getName() + " | Price: " + p.getPrice())
                .defaultIfEmpty("Not Found")
                .onErrorReturn("Error: something went wrong");
    }

    /**
     * GET /products/demo/category/{category}
     * เรียก endpoint /products/category/{cat} ผ่าน WebClient แล้ว chain
     * .filter() → เอาเฉพาะที่มี stock, .map() → format ข้อความ
     *
     * ทดสอบ: GET http://localhost:8080/products/demo/category/Electronics
     */
    @GetMapping("/demo/category/{category}")
    public Flux<String> demoGetByCategory(@PathVariable String category) {
        return webClient.getByCategory(category)
                .filter(p -> p.getStock() > 0)
                .map(p -> p.getName() + " (stock: " + p.getStock() + ")");
    }

    /**
     * GET /products/demo/{id}/price
     * เรียก endpoint /products/{id}/price ผ่าน WebClient
     * แสดงราคาหลังส่วนลด พร้อม fallback ถ้าไม่พบ
     *
     * ทดสอบ: GET http://localhost:8080/products/demo/1/price
     */
    @GetMapping("/demo/{id}/price")
    public Mono<String> demoGetPrice(@PathVariable String id) {
        return webClient.getDiscountedPrice(id)
                .map(price -> "Discounted price: " + price)
                .defaultIfEmpty("Price not available")
                .onErrorReturn("Error: product not found");
    }
}
