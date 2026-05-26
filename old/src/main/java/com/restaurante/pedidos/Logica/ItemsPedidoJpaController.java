/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.logica;

import com.restaurante.pedidos.clases.ItemsPedido;
import com.restaurante.pedidos.clases.PedidosCliente;
import com.restaurante.pedidos.clases.Productos;
import com.restaurante.pedidos.logica.exceptions.NonexistentEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;


public class ItemsPedidoJpaController implements Serializable {

	private EntityManagerFactory emf = null;

	public ItemsPedidoJpaController(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(ItemsPedido itemsPedido) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			PedidosCliente pedidoId = itemsPedido.getPedidoId();
			if (pedidoId != null) {
				pedidoId = em.getReference(pedidoId.getClass(), pedidoId.getPedidoId());
				itemsPedido.setPedidoId(pedidoId);
			}
			Productos productoId = itemsPedido.getProductoId();
			if (productoId != null) {
				productoId = em.getReference(productoId.getClass(), productoId.getProductoId());
				itemsPedido.setProductoId(productoId);
			}
			em.persist(itemsPedido);
			if (pedidoId != null) {
				pedidoId.getItemsPedidoCollection().add(itemsPedido);
				pedidoId = em.merge(pedidoId);
			}
			if (productoId != null) {
				productoId.getItemsPedidoCollection().add(itemsPedido);
				productoId = em.merge(productoId);
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(ItemsPedido itemsPedido) throws Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			ItemsPedido persistentItemsPedido = em.find(ItemsPedido.class, itemsPedido.getItemPedidoId());
			PedidosCliente pedidoIdOld = persistentItemsPedido.getPedidoId();
			PedidosCliente pedidoIdNew = itemsPedido.getPedidoId();
			Productos productoIdOld = persistentItemsPedido.getProductoId();
			Productos productoIdNew = itemsPedido.getProductoId();
			if (pedidoIdNew != null) {
				pedidoIdNew = em.getReference(pedidoIdNew.getClass(), pedidoIdNew.getPedidoId());
				itemsPedido.setPedidoId(pedidoIdNew);
			}
			if (productoIdNew != null) {
				productoIdNew = em.getReference(productoIdNew.getClass(), productoIdNew.getProductoId());
				itemsPedido.setProductoId(productoIdNew);
			}
			itemsPedido = em.merge(itemsPedido);
			if (pedidoIdOld != null && !pedidoIdOld.equals(pedidoIdNew)) {
				pedidoIdOld.getItemsPedidoCollection().remove(itemsPedido);
				pedidoIdOld = em.merge(pedidoIdOld);
			}
			if (pedidoIdNew != null && !pedidoIdNew.equals(pedidoIdOld)) {
				pedidoIdNew.getItemsPedidoCollection().add(itemsPedido);
				pedidoIdNew = em.merge(pedidoIdNew);
			}
			if (productoIdOld != null && !productoIdOld.equals(productoIdNew)) {
				productoIdOld.getItemsPedidoCollection().remove(itemsPedido);
				productoIdOld = em.merge(productoIdOld);
			}
			if (productoIdNew != null && !productoIdNew.equals(productoIdOld)) {
				productoIdNew.getItemsPedidoCollection().add(itemsPedido);
				productoIdNew = em.merge(productoIdNew);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Long id = itemsPedido.getItemPedidoId();
				if (findItemsPedido(id) == null) {
					throw new NonexistentEntityException("The itemsPedido with id " + id + " no longer exists.");
				}
			}
			throw ex;
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void destroy(Long id) throws NonexistentEntityException {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			ItemsPedido itemsPedido;
			try {
				itemsPedido = em.getReference(ItemsPedido.class, id);
				itemsPedido.getItemPedidoId();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The itemsPedido with id " + id + " no longer exists.", enfe);
			}
			PedidosCliente pedidoId = itemsPedido.getPedidoId();
			if (pedidoId != null) {
				pedidoId.getItemsPedidoCollection().remove(itemsPedido);
				pedidoId = em.merge(pedidoId);
			}
			Productos productoId = itemsPedido.getProductoId();
			if (productoId != null) {
				productoId.getItemsPedidoCollection().remove(itemsPedido);
				productoId = em.merge(productoId);
			}
			em.remove(itemsPedido);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<ItemsPedido> findItemsPedidoEntities() {
		return findItemsPedidoEntities(true, -1, -1);
	}

	public List<ItemsPedido> findItemsPedidoEntities(int maxResults, int firstResult) {
		return findItemsPedidoEntities(false, maxResults, firstResult);
	}

	private List<ItemsPedido> findItemsPedidoEntities(boolean all, int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(ItemsPedido.class));
			Query q = em.createQuery(cq);
			if (!all) {
				q.setMaxResults(maxResults);
				q.setFirstResult(firstResult);
			}
			return q.getResultList();
		} finally {
			em.close();
		}
	}

	public ItemsPedido findItemsPedido(Long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(ItemsPedido.class, id);
		} finally {
			em.close();
		}
	}

	public int getItemsPedidoCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<ItemsPedido> rt = cq.from(ItemsPedido.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

}
