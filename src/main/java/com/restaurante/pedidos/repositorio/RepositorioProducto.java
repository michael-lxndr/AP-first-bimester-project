package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.CategoriaProducto;
import com.restaurante.pedidos.dominio.entidad.Producto;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositorioProducto {
	private final EntityManager administradorDeEntidad;

	public RepositorioProducto(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	public Optional<Producto> buscarPorId(Long id) {
		return Optional.ofNullable(administradorDeEntidad.find(Producto.class, id));
	}

	public Optional<Producto> buscarPorCodigo(String codigo) {
		return administradorDeEntidad
			.createQuery("SELECT p FROM Producto p WHERE p.codigoProducto = :codigo", Producto.class)
			.setParameter("codigo", codigo)
			.getResultList()
			.stream()
			.findFirst();
	}

	public List<Producto> buscarDisponibles() {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM Producto p
				WHERE p.disponible = true
				ORDER BY p.categoria ASC, p.nombreProducto ASC
			""", Producto.class)
			.getResultList();
	}

	public List<Producto> buscarDisponiblesPorCategoria(CategoriaProducto categoria) {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM Producto p
				WHERE p.disponible = true AND p.categoria = :categoria
				ORDER BY p.nombreProducto ASC
			""", Producto.class)
			.setParameter("categoria", categoria)
			.getResultList();
	}

	public Producto guardar(Producto producto) {
		administradorDeEntidad.persist(producto);
		return producto;
	}

	public Producto actualizar(Producto producto) {
		return administradorDeEntidad.merge(producto);
	}
}
