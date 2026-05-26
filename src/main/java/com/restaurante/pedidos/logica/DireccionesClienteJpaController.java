/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.logica;

import com.restaurante.pedidos.clases.Clientes;
import com.restaurante.pedidos.clases.DireccionesCliente;
import com.restaurante.pedidos.clases.PedidosCliente;
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


public class DireccionesClienteJpaController implements Serializable {

	private EntityManagerFactory emf = null;

	public DireccionesClienteJpaController(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(DireccionesCliente direccionesCliente) {
		if (direccionesCliente.getPedidosClienteCollection() == null) {
			direccionesCliente.setPedidosClienteCollection(new ArrayList<PedidosCliente>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Clientes clienteId = direccionesCliente.getClienteId();
			if (clienteId != null) {
				clienteId = em.getReference(clienteId.getClass(), clienteId.getClienteId());
				direccionesCliente.setClienteId(clienteId);
			}
			Collection<PedidosCliente> attachedPedidosClienteCollection = new ArrayList<PedidosCliente>();
			for (PedidosCliente pedidosClienteCollectionPedidosClienteToAttach : direccionesCliente.getPedidosClienteCollection()) {
				pedidosClienteCollectionPedidosClienteToAttach = em.getReference(pedidosClienteCollectionPedidosClienteToAttach.getClass(), pedidosClienteCollectionPedidosClienteToAttach.getPedidoId());
				attachedPedidosClienteCollection.add(pedidosClienteCollectionPedidosClienteToAttach);
			}
			direccionesCliente.setPedidosClienteCollection(attachedPedidosClienteCollection);
			em.persist(direccionesCliente);
			if (clienteId != null) {
				clienteId.getDireccionesClienteCollection().add(direccionesCliente);
				clienteId = em.merge(clienteId);
			}
			for (PedidosCliente pedidosClienteCollectionPedidosCliente : direccionesCliente.getPedidosClienteCollection()) {
				DireccionesCliente oldDireccionEntregaIdOfPedidosClienteCollectionPedidosCliente = pedidosClienteCollectionPedidosCliente.getDireccionEntregaId();
				pedidosClienteCollectionPedidosCliente.setDireccionEntregaId(direccionesCliente);
				pedidosClienteCollectionPedidosCliente = em.merge(pedidosClienteCollectionPedidosCliente);
				if (oldDireccionEntregaIdOfPedidosClienteCollectionPedidosCliente != null) {
					oldDireccionEntregaIdOfPedidosClienteCollectionPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionPedidosCliente);
					oldDireccionEntregaIdOfPedidosClienteCollectionPedidosCliente = em.merge(oldDireccionEntregaIdOfPedidosClienteCollectionPedidosCliente);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(DireccionesCliente direccionesCliente) throws Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			DireccionesCliente persistentDireccionesCliente = em.find(DireccionesCliente.class, direccionesCliente.getDireccionId());
			Clientes clienteIdOld = persistentDireccionesCliente.getClienteId();
			Clientes clienteIdNew = direccionesCliente.getClienteId();
			Collection<PedidosCliente> pedidosClienteCollectionOld = persistentDireccionesCliente.getPedidosClienteCollection();
			Collection<PedidosCliente> pedidosClienteCollectionNew = direccionesCliente.getPedidosClienteCollection();
			List<String> illegalOrphanMessages = null;
			for (PedidosCliente pedidosClienteCollectionOldPedidosCliente : pedidosClienteCollectionOld) {
				if (!pedidosClienteCollectionNew.contains(pedidosClienteCollectionOldPedidosCliente)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain PedidosCliente " + pedidosClienteCollectionOldPedidosCliente + " since its direccionEntregaId field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			if (clienteIdNew != null) {
				clienteIdNew = em.getReference(clienteIdNew.getClass(), clienteIdNew.getClienteId());
				direccionesCliente.setClienteId(clienteIdNew);
			}
			Collection<PedidosCliente> attachedPedidosClienteCollectionNew = new ArrayList<PedidosCliente>();
			for (PedidosCliente pedidosClienteCollectionNewPedidosClienteToAttach : pedidosClienteCollectionNew) {
				pedidosClienteCollectionNewPedidosClienteToAttach = em.getReference(pedidosClienteCollectionNewPedidosClienteToAttach.getClass(), pedidosClienteCollectionNewPedidosClienteToAttach.getPedidoId());
				attachedPedidosClienteCollectionNew.add(pedidosClienteCollectionNewPedidosClienteToAttach);
			}
			pedidosClienteCollectionNew = attachedPedidosClienteCollectionNew;
			direccionesCliente.setPedidosClienteCollection(pedidosClienteCollectionNew);
			direccionesCliente = em.merge(direccionesCliente);
			if (clienteIdOld != null && !clienteIdOld.equals(clienteIdNew)) {
				clienteIdOld.getDireccionesClienteCollection().remove(direccionesCliente);
				clienteIdOld = em.merge(clienteIdOld);
			}
			if (clienteIdNew != null && !clienteIdNew.equals(clienteIdOld)) {
				clienteIdNew.getDireccionesClienteCollection().add(direccionesCliente);
				clienteIdNew = em.merge(clienteIdNew);
			}
			for (PedidosCliente pedidosClienteCollectionNewPedidosCliente : pedidosClienteCollectionNew) {
				if (!pedidosClienteCollectionOld.contains(pedidosClienteCollectionNewPedidosCliente)) {
					DireccionesCliente oldDireccionEntregaIdOfPedidosClienteCollectionNewPedidosCliente = pedidosClienteCollectionNewPedidosCliente.getDireccionEntregaId();
					pedidosClienteCollectionNewPedidosCliente.setDireccionEntregaId(direccionesCliente);
					pedidosClienteCollectionNewPedidosCliente = em.merge(pedidosClienteCollectionNewPedidosCliente);
					if (oldDireccionEntregaIdOfPedidosClienteCollectionNewPedidosCliente != null && !oldDireccionEntregaIdOfPedidosClienteCollectionNewPedidosCliente.equals(direccionesCliente)) {
						oldDireccionEntregaIdOfPedidosClienteCollectionNewPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionNewPedidosCliente);
						oldDireccionEntregaIdOfPedidosClienteCollectionNewPedidosCliente = em.merge(oldDireccionEntregaIdOfPedidosClienteCollectionNewPedidosCliente);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Long id = direccionesCliente.getDireccionId();
				if (findDireccionesCliente(id) == null) {
					throw new NonexistentEntityException("The direccionesCliente with id " + id + " no longer exists.");
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
			DireccionesCliente direccionesCliente;
			try {
				direccionesCliente = em.getReference(DireccionesCliente.class, id);
				direccionesCliente.getDireccionId();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The direccionesCliente with id " + id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			Collection<PedidosCliente> pedidosClienteCollectionOrphanCheck = direccionesCliente.getPedidosClienteCollection();
			for (PedidosCliente pedidosClienteCollectionOrphanCheckPedidosCliente : pedidosClienteCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This DireccionesCliente (" + direccionesCliente + ") cannot be destroyed since the PedidosCliente " + pedidosClienteCollectionOrphanCheckPedidosCliente + " in its pedidosClienteCollection field has a non-nullable direccionEntregaId field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			Clientes clienteId = direccionesCliente.getClienteId();
			if (clienteId != null) {
				clienteId.getDireccionesClienteCollection().remove(direccionesCliente);
				clienteId = em.merge(clienteId);
			}
			em.remove(direccionesCliente);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<DireccionesCliente> findDireccionesClienteEntities() {
		return findDireccionesClienteEntities(true, -1, -1);
	}

	public List<DireccionesCliente> findDireccionesClienteEntities(int maxResults, int firstResult) {
		return findDireccionesClienteEntities(false, maxResults, firstResult);
	}

	private List<DireccionesCliente> findDireccionesClienteEntities(boolean all, int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(DireccionesCliente.class));
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

	public DireccionesCliente findDireccionesCliente(Long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(DireccionesCliente.class, id);
		} finally {
			em.close();
		}
	}

	public int getDireccionesClienteCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<DireccionesCliente> rt = cq.from(DireccionesCliente.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

}
