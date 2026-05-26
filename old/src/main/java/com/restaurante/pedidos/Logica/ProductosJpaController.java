/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.logica;

import com.restaurante.pedidos.clases.ItemsPedido;
import com.restaurante.pedidos.clases.Productos;
import com.restaurante.pedidos.logica.exceptions.IllegalOrphanException;
import com.restaurante.pedidos.logica.exceptions.NonexistentEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ProductosJpaController implements Serializable {

	private EntityManagerFactory emf = null;

	public ProductosJpaController(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(Productos productos) {
		if (productos.getItemsPedidoCollection() == null) {
			productos.setItemsPedidoCollection(new ArrayList<ItemsPedido>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Collection<ItemsPedido> attachedItemsPedidoCollection = new ArrayList<ItemsPedido>();
			for (ItemsPedido itemsPedidoCollectionItemsPedidoToAttach : productos.getItemsPedidoCollection()) {
				itemsPedidoCollectionItemsPedidoToAttach = em.getReference(itemsPedidoCollectionItemsPedidoToAttach.getClass(), itemsPedidoCollectionItemsPedidoToAttach.getItemPedidoId());
				attachedItemsPedidoCollection.add(itemsPedidoCollectionItemsPedidoToAttach);
			}
			productos.setItemsPedidoCollection(attachedItemsPedidoCollection);
			em.persist(productos);
			for (ItemsPedido itemsPedidoCollectionItemsPedido : productos.getItemsPedidoCollection()) {
				Productos oldProductoIdOfItemsPedidoCollectionItemsPedido = itemsPedidoCollectionItemsPedido.getProductoId();
				itemsPedidoCollectionItemsPedido.setProductoId(productos);
				itemsPedidoCollectionItemsPedido = em.merge(itemsPedidoCollectionItemsPedido);
				if (oldProductoIdOfItemsPedidoCollectionItemsPedido != null) {
					oldProductoIdOfItemsPedidoCollectionItemsPedido.getItemsPedidoCollection().remove(itemsPedidoCollectionItemsPedido);
					oldProductoIdOfItemsPedidoCollectionItemsPedido = em.merge(oldProductoIdOfItemsPedidoCollectionItemsPedido);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(Productos productos) throws Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Productos persistentProductos = em.find(Productos.class, productos.getProductoId());
			Collection<ItemsPedido> itemsPedidoCollectionOld = persistentProductos.getItemsPedidoCollection();
			Collection<ItemsPedido> itemsPedidoCollectionNew = productos.getItemsPedidoCollection();
			List<String> illegalOrphanMessages = null;
			for (ItemsPedido itemsPedidoCollectionOldItemsPedido : itemsPedidoCollectionOld) {
				if (!itemsPedidoCollectionNew.contains(itemsPedidoCollectionOldItemsPedido)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain ItemsPedido " + itemsPedidoCollectionOldItemsPedido + " since its productoId field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			Collection<ItemsPedido> attachedItemsPedidoCollectionNew = new ArrayList<ItemsPedido>();
			for (ItemsPedido itemsPedidoCollectionNewItemsPedidoToAttach : itemsPedidoCollectionNew) {
				itemsPedidoCollectionNewItemsPedidoToAttach = em.getReference(itemsPedidoCollectionNewItemsPedidoToAttach.getClass(), itemsPedidoCollectionNewItemsPedidoToAttach.getItemPedidoId());
				attachedItemsPedidoCollectionNew.add(itemsPedidoCollectionNewItemsPedidoToAttach);
			}
			itemsPedidoCollectionNew = attachedItemsPedidoCollectionNew;
			productos.setItemsPedidoCollection(itemsPedidoCollectionNew);
			productos = em.merge(productos);
			for (ItemsPedido itemsPedidoCollectionNewItemsPedido : itemsPedidoCollectionNew) {
				if (!itemsPedidoCollectionOld.contains(itemsPedidoCollectionNewItemsPedido)) {
					Productos oldProductoIdOfItemsPedidoCollectionNewItemsPedido = itemsPedidoCollectionNewItemsPedido.getProductoId();
					itemsPedidoCollectionNewItemsPedido.setProductoId(productos);
					itemsPedidoCollectionNewItemsPedido = em.merge(itemsPedidoCollectionNewItemsPedido);
					if (oldProductoIdOfItemsPedidoCollectionNewItemsPedido != null && !oldProductoIdOfItemsPedidoCollectionNewItemsPedido.equals(productos)) {
						oldProductoIdOfItemsPedidoCollectionNewItemsPedido.getItemsPedidoCollection().remove(itemsPedidoCollectionNewItemsPedido);
						oldProductoIdOfItemsPedidoCollectionNewItemsPedido = em.merge(oldProductoIdOfItemsPedidoCollectionNewItemsPedido);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Long id = productos.getProductoId();
				if (findProductos(id) == null) {
					throw new NonexistentEntityException("The productos with id " + id + " no longer exists.");
				}
			}
			throw ex;
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Productos productos;
			try {
				productos = em.getReference(Productos.class, id);
				productos.getProductoId();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The productos with id " + id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			Collection<ItemsPedido> itemsPedidoCollectionOrphanCheck = productos.getItemsPedidoCollection();
			for (ItemsPedido itemsPedidoCollectionOrphanCheckItemsPedido : itemsPedidoCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This Productos (" + productos + ") cannot be destroyed since the ItemsPedido " + itemsPedidoCollectionOrphanCheckItemsPedido + " in its itemsPedidoCollection field has a non-nullable productoId field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			em.remove(productos);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<Productos> findProductosEntities() {
		return findProductosEntities(true, -1, -1);
	}

	public List<Productos> findProductosEntities(int maxResults, int firstResult) {
		return findProductosEntities(false, maxResults, firstResult);
	}

	private List<Productos> findProductosEntities(boolean all, int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(Productos.class));
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

	public Productos findProductos(Long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Productos.class, id);
		} finally {
			em.close();
		}
	}

	public int getProductosCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<Productos> rt = cq.from(Productos.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

}
