/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Logica;

import com.restaurante.pedidos.Clases.EstadosPedido;
import com.restaurante.pedidos.Clases.ReglasTransicionEstadoPedido;
import com.restaurante.pedidos.Clases.Roles;
import com.restaurante.pedidos.Logica.exceptions.NonexistentEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;


public class ReglasTransicionEstadoPedidoJpaController implements Serializable {

	private EntityManagerFactory emf = null;

	public ReglasTransicionEstadoPedidoJpaController(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(ReglasTransicionEstadoPedido reglasTransicionEstadoPedido) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			EstadosPedido estadoDestinoId = reglasTransicionEstadoPedido.getEstadoDestinoId();
			if (estadoDestinoId != null) {
				estadoDestinoId = em.getReference(estadoDestinoId.getClass(), estadoDestinoId.getEstadoId());
				reglasTransicionEstadoPedido.setEstadoDestinoId(estadoDestinoId);
			}
			EstadosPedido estadoOrigenId = reglasTransicionEstadoPedido.getEstadoOrigenId();
			if (estadoOrigenId != null) {
				estadoOrigenId = em.getReference(estadoOrigenId.getClass(), estadoOrigenId.getEstadoId());
				reglasTransicionEstadoPedido.setEstadoOrigenId(estadoOrigenId);
			}
			Roles rolId = reglasTransicionEstadoPedido.getRolId();
			if (rolId != null) {
				rolId = em.getReference(rolId.getClass(), rolId.getRolId());
				reglasTransicionEstadoPedido.setRolId(rolId);
			}
			em.persist(reglasTransicionEstadoPedido);
			if (estadoDestinoId != null) {
				estadoDestinoId.getReglasTransicionEstadoPedidoCollection().add(reglasTransicionEstadoPedido);
				estadoDestinoId = em.merge(estadoDestinoId);
			}
			if (estadoOrigenId != null) {
				estadoOrigenId.getReglasTransicionEstadoPedidoCollection().add(reglasTransicionEstadoPedido);
				estadoOrigenId = em.merge(estadoOrigenId);
			}
			if (rolId != null) {
				rolId.getReglasTransicionEstadoPedidoCollection().add(reglasTransicionEstadoPedido);
				rolId = em.merge(rolId);
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(ReglasTransicionEstadoPedido reglasTransicionEstadoPedido) throws Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			ReglasTransicionEstadoPedido persistentReglasTransicionEstadoPedido = em.find(ReglasTransicionEstadoPedido.class, reglasTransicionEstadoPedido.getReglaTransicionId());
			EstadosPedido estadoDestinoIdOld = persistentReglasTransicionEstadoPedido.getEstadoDestinoId();
			EstadosPedido estadoDestinoIdNew = reglasTransicionEstadoPedido.getEstadoDestinoId();
			EstadosPedido estadoOrigenIdOld = persistentReglasTransicionEstadoPedido.getEstadoOrigenId();
			EstadosPedido estadoOrigenIdNew = reglasTransicionEstadoPedido.getEstadoOrigenId();
			Roles rolIdOld = persistentReglasTransicionEstadoPedido.getRolId();
			Roles rolIdNew = reglasTransicionEstadoPedido.getRolId();
			if (estadoDestinoIdNew != null) {
				estadoDestinoIdNew = em.getReference(estadoDestinoIdNew.getClass(), estadoDestinoIdNew.getEstadoId());
				reglasTransicionEstadoPedido.setEstadoDestinoId(estadoDestinoIdNew);
			}
			if (estadoOrigenIdNew != null) {
				estadoOrigenIdNew = em.getReference(estadoOrigenIdNew.getClass(), estadoOrigenIdNew.getEstadoId());
				reglasTransicionEstadoPedido.setEstadoOrigenId(estadoOrigenIdNew);
			}
			if (rolIdNew != null) {
				rolIdNew = em.getReference(rolIdNew.getClass(), rolIdNew.getRolId());
				reglasTransicionEstadoPedido.setRolId(rolIdNew);
			}
			reglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedido);
			if (estadoDestinoIdOld != null && !estadoDestinoIdOld.equals(estadoDestinoIdNew)) {
				estadoDestinoIdOld.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedido);
				estadoDestinoIdOld = em.merge(estadoDestinoIdOld);
			}
			if (estadoDestinoIdNew != null && !estadoDestinoIdNew.equals(estadoDestinoIdOld)) {
				estadoDestinoIdNew.getReglasTransicionEstadoPedidoCollection().add(reglasTransicionEstadoPedido);
				estadoDestinoIdNew = em.merge(estadoDestinoIdNew);
			}
			if (estadoOrigenIdOld != null && !estadoOrigenIdOld.equals(estadoOrigenIdNew)) {
				estadoOrigenIdOld.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedido);
				estadoOrigenIdOld = em.merge(estadoOrigenIdOld);
			}
			if (estadoOrigenIdNew != null && !estadoOrigenIdNew.equals(estadoOrigenIdOld)) {
				estadoOrigenIdNew.getReglasTransicionEstadoPedidoCollection().add(reglasTransicionEstadoPedido);
				estadoOrigenIdNew = em.merge(estadoOrigenIdNew);
			}
			if (rolIdOld != null && !rolIdOld.equals(rolIdNew)) {
				rolIdOld.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedido);
				rolIdOld = em.merge(rolIdOld);
			}
			if (rolIdNew != null && !rolIdNew.equals(rolIdOld)) {
				rolIdNew.getReglasTransicionEstadoPedidoCollection().add(reglasTransicionEstadoPedido);
				rolIdNew = em.merge(rolIdNew);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Long id = reglasTransicionEstadoPedido.getReglaTransicionId();
				if (findReglasTransicionEstadoPedido(id) == null) {
					throw new NonexistentEntityException("The reglasTransicionEstadoPedido with id " + id + " no longer exists.");
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
			ReglasTransicionEstadoPedido reglasTransicionEstadoPedido;
			try {
				reglasTransicionEstadoPedido = em.getReference(ReglasTransicionEstadoPedido.class, id);
				reglasTransicionEstadoPedido.getReglaTransicionId();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The reglasTransicionEstadoPedido with id " + id + " no longer exists.", enfe);
			}
			EstadosPedido estadoDestinoId = reglasTransicionEstadoPedido.getEstadoDestinoId();
			if (estadoDestinoId != null) {
				estadoDestinoId.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedido);
				estadoDestinoId = em.merge(estadoDestinoId);
			}
			EstadosPedido estadoOrigenId = reglasTransicionEstadoPedido.getEstadoOrigenId();
			if (estadoOrigenId != null) {
				estadoOrigenId.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedido);
				estadoOrigenId = em.merge(estadoOrigenId);
			}
			Roles rolId = reglasTransicionEstadoPedido.getRolId();
			if (rolId != null) {
				rolId.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedido);
				rolId = em.merge(rolId);
			}
			em.remove(reglasTransicionEstadoPedido);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<ReglasTransicionEstadoPedido> findReglasTransicionEstadoPedidoEntities() {
		return findReglasTransicionEstadoPedidoEntities(true, -1, -1);
	}

	public List<ReglasTransicionEstadoPedido> findReglasTransicionEstadoPedidoEntities(int maxResults, int firstResult) {
		return findReglasTransicionEstadoPedidoEntities(false, maxResults, firstResult);
	}

	private List<ReglasTransicionEstadoPedido> findReglasTransicionEstadoPedidoEntities(boolean all, int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(ReglasTransicionEstadoPedido.class));
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

	public ReglasTransicionEstadoPedido findReglasTransicionEstadoPedido(Long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(ReglasTransicionEstadoPedido.class, id);
		} finally {
			em.close();
		}
	}

	public int getReglasTransicionEstadoPedidoCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<ReglasTransicionEstadoPedido> rt = cq.from(ReglasTransicionEstadoPedido.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

}
