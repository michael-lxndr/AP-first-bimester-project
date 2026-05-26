/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Logica;

import com.restaurante.pedidos.Clases.Clientes;
import com.restaurante.pedidos.Clases.DireccionesCliente;
import com.restaurante.pedidos.Clases.PedidosCliente;
import com.restaurante.pedidos.Logica.exceptions.IllegalOrphanException;
import com.restaurante.pedidos.Logica.exceptions.NonexistentEntityException;

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


public class ClientesJpaController implements Serializable {

	private EntityManagerFactory emf = null;

	public ClientesJpaController(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(Clientes clientes) {
		if (clientes.getPedidosClienteCollection() == null) {
			clientes.setPedidosClienteCollection(new ArrayList<PedidosCliente>());
		}
		if (clientes.getDireccionesClienteCollection() == null) {
			clientes.setDireccionesClienteCollection(new ArrayList<DireccionesCliente>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Collection<PedidosCliente> attachedPedidosClienteCollection = new ArrayList<PedidosCliente>();
			for (PedidosCliente pedidosClienteCollectionPedidosClienteToAttach : clientes.getPedidosClienteCollection()) {
				pedidosClienteCollectionPedidosClienteToAttach = em.getReference(pedidosClienteCollectionPedidosClienteToAttach.getClass(), pedidosClienteCollectionPedidosClienteToAttach.getPedidoId());
				attachedPedidosClienteCollection.add(pedidosClienteCollectionPedidosClienteToAttach);
			}
			clientes.setPedidosClienteCollection(attachedPedidosClienteCollection);
			Collection<DireccionesCliente> attachedDireccionesClienteCollection = new ArrayList<DireccionesCliente>();
			for (DireccionesCliente direccionesClienteCollectionDireccionesClienteToAttach : clientes.getDireccionesClienteCollection()) {
				direccionesClienteCollectionDireccionesClienteToAttach = em.getReference(direccionesClienteCollectionDireccionesClienteToAttach.getClass(), direccionesClienteCollectionDireccionesClienteToAttach.getDireccionId());
				attachedDireccionesClienteCollection.add(direccionesClienteCollectionDireccionesClienteToAttach);
			}
			clientes.setDireccionesClienteCollection(attachedDireccionesClienteCollection);
			em.persist(clientes);
			for (PedidosCliente pedidosClienteCollectionPedidosCliente : clientes.getPedidosClienteCollection()) {
				Clientes oldClienteIdOfPedidosClienteCollectionPedidosCliente = pedidosClienteCollectionPedidosCliente.getClienteId();
				pedidosClienteCollectionPedidosCliente.setClienteId(clientes);
				pedidosClienteCollectionPedidosCliente = em.merge(pedidosClienteCollectionPedidosCliente);
				if (oldClienteIdOfPedidosClienteCollectionPedidosCliente != null) {
					oldClienteIdOfPedidosClienteCollectionPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionPedidosCliente);
					oldClienteIdOfPedidosClienteCollectionPedidosCliente = em.merge(oldClienteIdOfPedidosClienteCollectionPedidosCliente);
				}
			}
			for (DireccionesCliente direccionesClienteCollectionDireccionesCliente : clientes.getDireccionesClienteCollection()) {
				Clientes oldClienteIdOfDireccionesClienteCollectionDireccionesCliente = direccionesClienteCollectionDireccionesCliente.getClienteId();
				direccionesClienteCollectionDireccionesCliente.setClienteId(clientes);
				direccionesClienteCollectionDireccionesCliente = em.merge(direccionesClienteCollectionDireccionesCliente);
				if (oldClienteIdOfDireccionesClienteCollectionDireccionesCliente != null) {
					oldClienteIdOfDireccionesClienteCollectionDireccionesCliente.getDireccionesClienteCollection().remove(direccionesClienteCollectionDireccionesCliente);
					oldClienteIdOfDireccionesClienteCollectionDireccionesCliente = em.merge(oldClienteIdOfDireccionesClienteCollectionDireccionesCliente);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(Clientes clientes) throws Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Clientes persistentClientes = em.find(Clientes.class, clientes.getClienteId());
			Collection<PedidosCliente> pedidosClienteCollectionOld = persistentClientes.getPedidosClienteCollection();
			Collection<PedidosCliente> pedidosClienteCollectionNew = clientes.getPedidosClienteCollection();
			Collection<DireccionesCliente> direccionesClienteCollectionOld = persistentClientes.getDireccionesClienteCollection();
			Collection<DireccionesCliente> direccionesClienteCollectionNew = clientes.getDireccionesClienteCollection();
			List<String> illegalOrphanMessages = null;
			for (PedidosCliente pedidosClienteCollectionOldPedidosCliente : pedidosClienteCollectionOld) {
				if (!pedidosClienteCollectionNew.contains(pedidosClienteCollectionOldPedidosCliente)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain PedidosCliente " + pedidosClienteCollectionOldPedidosCliente + " since its clienteId field is not nullable.");
				}
			}
			for (DireccionesCliente direccionesClienteCollectionOldDireccionesCliente : direccionesClienteCollectionOld) {
				if (!direccionesClienteCollectionNew.contains(direccionesClienteCollectionOldDireccionesCliente)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain DireccionesCliente " + direccionesClienteCollectionOldDireccionesCliente + " since its clienteId field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			Collection<PedidosCliente> attachedPedidosClienteCollectionNew = new ArrayList<PedidosCliente>();
			for (PedidosCliente pedidosClienteCollectionNewPedidosClienteToAttach : pedidosClienteCollectionNew) {
				pedidosClienteCollectionNewPedidosClienteToAttach = em.getReference(pedidosClienteCollectionNewPedidosClienteToAttach.getClass(), pedidosClienteCollectionNewPedidosClienteToAttach.getPedidoId());
				attachedPedidosClienteCollectionNew.add(pedidosClienteCollectionNewPedidosClienteToAttach);
			}
			pedidosClienteCollectionNew = attachedPedidosClienteCollectionNew;
			clientes.setPedidosClienteCollection(pedidosClienteCollectionNew);
			Collection<DireccionesCliente> attachedDireccionesClienteCollectionNew = new ArrayList<DireccionesCliente>();
			for (DireccionesCliente direccionesClienteCollectionNewDireccionesClienteToAttach : direccionesClienteCollectionNew) {
				direccionesClienteCollectionNewDireccionesClienteToAttach = em.getReference(direccionesClienteCollectionNewDireccionesClienteToAttach.getClass(), direccionesClienteCollectionNewDireccionesClienteToAttach.getDireccionId());
				attachedDireccionesClienteCollectionNew.add(direccionesClienteCollectionNewDireccionesClienteToAttach);
			}
			direccionesClienteCollectionNew = attachedDireccionesClienteCollectionNew;
			clientes.setDireccionesClienteCollection(direccionesClienteCollectionNew);
			clientes = em.merge(clientes);
			for (PedidosCliente pedidosClienteCollectionNewPedidosCliente : pedidosClienteCollectionNew) {
				if (!pedidosClienteCollectionOld.contains(pedidosClienteCollectionNewPedidosCliente)) {
					Clientes oldClienteIdOfPedidosClienteCollectionNewPedidosCliente = pedidosClienteCollectionNewPedidosCliente.getClienteId();
					pedidosClienteCollectionNewPedidosCliente.setClienteId(clientes);
					pedidosClienteCollectionNewPedidosCliente = em.merge(pedidosClienteCollectionNewPedidosCliente);
					if (oldClienteIdOfPedidosClienteCollectionNewPedidosCliente != null && !oldClienteIdOfPedidosClienteCollectionNewPedidosCliente.equals(clientes)) {
						oldClienteIdOfPedidosClienteCollectionNewPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionNewPedidosCliente);
						oldClienteIdOfPedidosClienteCollectionNewPedidosCliente = em.merge(oldClienteIdOfPedidosClienteCollectionNewPedidosCliente);
					}
				}
			}
			for (DireccionesCliente direccionesClienteCollectionNewDireccionesCliente : direccionesClienteCollectionNew) {
				if (!direccionesClienteCollectionOld.contains(direccionesClienteCollectionNewDireccionesCliente)) {
					Clientes oldClienteIdOfDireccionesClienteCollectionNewDireccionesCliente = direccionesClienteCollectionNewDireccionesCliente.getClienteId();
					direccionesClienteCollectionNewDireccionesCliente.setClienteId(clientes);
					direccionesClienteCollectionNewDireccionesCliente = em.merge(direccionesClienteCollectionNewDireccionesCliente);
					if (oldClienteIdOfDireccionesClienteCollectionNewDireccionesCliente != null && !oldClienteIdOfDireccionesClienteCollectionNewDireccionesCliente.equals(clientes)) {
						oldClienteIdOfDireccionesClienteCollectionNewDireccionesCliente.getDireccionesClienteCollection().remove(direccionesClienteCollectionNewDireccionesCliente);
						oldClienteIdOfDireccionesClienteCollectionNewDireccionesCliente = em.merge(oldClienteIdOfDireccionesClienteCollectionNewDireccionesCliente);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Long id = clientes.getClienteId();
				if (findClientes(id) == null) {
					throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.");
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
			Clientes clientes;
			try {
				clientes = em.getReference(Clientes.class, id);
				clientes.getClienteId();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			Collection<PedidosCliente> pedidosClienteCollectionOrphanCheck = clientes.getPedidosClienteCollection();
			for (PedidosCliente pedidosClienteCollectionOrphanCheckPedidosCliente : pedidosClienteCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This Clientes (" + clientes + ") cannot be destroyed since the PedidosCliente " + pedidosClienteCollectionOrphanCheckPedidosCliente + " in its pedidosClienteCollection field has a non-nullable clienteId field.");
			}
			Collection<DireccionesCliente> direccionesClienteCollectionOrphanCheck = clientes.getDireccionesClienteCollection();
			for (DireccionesCliente direccionesClienteCollectionOrphanCheckDireccionesCliente : direccionesClienteCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This Clientes (" + clientes + ") cannot be destroyed since the DireccionesCliente " + direccionesClienteCollectionOrphanCheckDireccionesCliente + " in its direccionesClienteCollection field has a non-nullable clienteId field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			em.remove(clientes);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<Clientes> findClientesEntities() {
		return findClientesEntities(true, -1, -1);
	}

	public List<Clientes> findClientesEntities(int maxResults, int firstResult) {
		return findClientesEntities(false, maxResults, firstResult);
	}

	private List<Clientes> findClientesEntities(boolean all, int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(Clientes.class));
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

	public Clientes findClientes(Long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Clientes.class, id);
		} finally {
			em.close();
		}
	}

	public int getClientesCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<Clientes> rt = cq.from(Clientes.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

}
