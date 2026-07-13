package com.minimarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- Nueva importación de Jackson
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")   
    @Column(nullable = false, unique = true)
    private String nombre;

    @JsonIgnore // <-- ¡ESTA ANOTACIÓN CORTA EL BUCLE DE SERIALIZACIÓN INFINITA!
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}