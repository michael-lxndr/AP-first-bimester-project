/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.logica;

import com.restaurante.pedidos.clases.*;
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


public class PedidosClienteJpaController implements Serializable {

	private EntityManagerFactory emf = null;

	public PedidosClienteJpaController(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(PedidosCliente pedidosCliente) {
		if (pedidosCliente.getHistorialEstadosPedidoCollection() == null) {
			pedidosCliente.setHistorialEstadosPedidoCollection(new ArrayList<HistorialEstadosPedido>());
		}
		if (pedidosCliente.getItemsPedidoCollection() == null) {
			pedidosCliente.setItemsPedidoCollection(new ArrayList<ItemsPedido>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Entregas entregas = pedidosCliente.getEntregas();
			if (entregas != null && entregas.getEntregaId() != null && entregas.getEntregaId() > 0) {
				try {
					entregas = em.getReference(entregas.getClass(), entregas.getEntregaId());
					pedidosCliente.setEntregas(entregas);
				} catch (javax.persistence.EntityNotFoundException e) {}
			}
			Clientes clienteId = pedidosCliente.getClienteId();
			if (clienteId != null && clienteId.getClienteId() != null && clienteId.getClienteId() > 0) {
				try {
					clienteId = em.getReference(clienteId.getClass(), clienteId.getClienteId());
					pedidosCliente.setClienteId(clienteId);
				} catch (javax.persistence.EntityNotFoundException e) {}
			}
			DireccionesCliente direccionEntregaId = pedidosCliente.getDireccionEntregaId();
			if (direccionEntregaId != null && direccionEntregaId.getDireccionId() != null && direccionEntregaId.getDireccionId() > 0) {
				try {
					direccionEntregaId = em.getReference(direccionEntregaId.getClass(), direccionEntregaId.getDireccionId());
					pedidosCliente.setDireccionEntregaId(direccionEntregaId);
				} catch (javax.persistence.EntityNotFoundException e) {}
			}
			EstadosPedido estadoActualId = pedidosCliente.getEstadoActualId();
			if (estadoActualId != null && estadoActualId.getEstadoId() != null && estadoActualId.getEstadoId() > 0) {
				try {
					estadoActualId = em.getReference(estadoActualId.getClass(), estadoActualId.getEstadoId());
					pedidosCliente.setEstadoActualId(estadoActualId);
				} catch (javax.persistence.EntityNotFoundException e) {}
			}
			Personal registradoPorPersonalId = pedidosCliente.getRegistradoPorPersonalId();
			if (registradoPorPersonalId != null && registradoPorPersonalId.getPersonalId() != null && registradoPorPersonalId.getPersonalId() > 0) {
				try {
					registradoPorPersonalId = em.getReference(registradoPorPersonalId.getClass(), registradoPorPersonalId.getPersonalId());
					pedidosCliente.setRegistradoPorPersonalId(registradoPorPersonalId);
				} catch (javax.persistence.EntityNotFoundException e) {}
			}
			Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollection = new ArrayList<HistorialEstadosPedido>();
			for (HistorialEstadosPedido historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach : pedidosCliente.getHistorialEstadosPedidoCollection()) {
				if (historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getHistorialId() != null && historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getHistorialId() > 0) {
					try {
						historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getHistorialId());
					} catch (javax.persistence.EntityNotFoundException e) {}
				}
				attachedHistorialEstadosPedidoCollection.add(historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach);
			}
			pedidosCliente.setHistorialEstadosPedidoCollection(attachedHistorialEstadosPedidoCollection);
			Collection<ItemsPedido> attachedItemsPedidoCollection = new ArrayList<ItemsPedido>();
			for (ItemsPedido itemsPedidoCollectionItemsPedidoToAttach : pedidosCliente.getItemsPedidoCollection()) {
				if (itemsPedidoCollectionItemsPedidoToAttach.getItemPedidoId() != null && itemsPedidoCollectionItemsPedidoToAttach.getItemPedidoId() > 0) {
					try {
						itemsPedidoCollectionItemsPedidoToAttach = em.getReference(itemsPedidoCollectionItemsPedidoToAttach.getClass(), itemsPedidoCollectionItemsPedidoToAttach.getItemPedidoId());
					} catch (javax.persistence.EntityNotFoundException e) {}
				}
				attachedItemsPedidoCollection.add(itemsPedidoCollectionItemsPedidoToAttach);
			}
			pedidosCliente.setItemsPedidoCollection(attachedItemsPedidoCollection);
			em.persist(pedidosCliente);
			if (entregas != null) {
				PedidosCliente oldPedidoIdOfEntregas = entregas.getPedidoId();
				if (oldPedidoIdOfEntregas != null) {
					oldPedidoIdOfEntregas.setEntregas(null);
					oldPedidoIdOfEntregas = em.merge(oldPedidoIdOfEntregas);
				}
				entregas.setPedidoId(pedidosCliente);
				entregas = em.merge(entregas);
			}
			if (clienteId != null) {
				clienteId.getPedidosClienteCollection().add(pedidosCliente);
				clienteId = em.merge(clienteId);
			}
			if (direccionEntregaId != null) {
				direccionEntregaId.getPedidosClienteCollection().add(pedidosCliente);
				direccionEntregaId = em.merge(direccionEntregaId);
			}
			if (estadoActualId != null) {
				estadoActualId.getPedidosClienteCollection().add(pedidosCliente);
				estadoActualId = em.merge(estadoActualId);
			}
			if (registradoPorPersonalId != null) {
				registradoPorPersonalId.getPedidosClienteCollection().add(pedidosCliente);
				registradoPorPersonalId = em.merge(registradoPorPersonalId);
			}
			for (HistorialEstadosPedido historialEstadosPedidoCollectionHistorialEstadosPedido : pedidosCliente.getHistorialEstadosPedidoCollection()) {
				PedidosCliente oldPedidoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido = historialEstadosPedidoCollectionHistorialEstadosPedido.getPedidoId();
				historialEstadosPedidoCollectionHistorialEstadosPedido.setPedidoId(pedidosCliente);
				historialEstadosPedidoCollectionHistorialEstadosPedido = em.merge(historialEstadosPedidoCollectionHistorialEstadosPedido);
				if (oldPedidoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido != null) {
					oldPedidoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido.getHistorialEstadosPedidoCollection().remove(historialEstadosPedidoCollectionHistorialEstadosPedido);
					oldPedidoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido = em.merge(oldPedidoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido);
				}
			}
			for (ItemsPedido itemsPedidoCollectionItemsPedido : pedidosCliente.getItemsPedidoCollection()) {
				PedidosCliente oldPedidoIdOfItemsPedidoCollectionItemsPedido = itemsPedidoCollectionItemsPedido.getPedidoId();
				itemsPedidoCollectionItemsPedido.setPedidoId(pedidosCliente);
				itemsPedidoCollectionItemsPedido = em.merge(itemsPedidoCollectionItemsPedido);
				if (oldPedidoIdOfItemsPedidoCollectionItemsPedido != null) {
					oldPedidoIdOfItemsPedidoCollectionItemsPedido.getItemsPedidoCollection().remove(itemsPedidoCollectionItemsPedido);
					oldPedidoIdOfItemsPedidoCollectionItemsPedido = em.merge(oldPedidoIdOfItemsPedidoCollectionItemsPedido);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(PedidosCliente pedidosCliente) throws Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			PedidosCliente persistentPedidosCliente = em.find(PedidosCliente.class, pedidosCliente.getPedidoId());
			Entregas entregasOld = persistentPedidosCliente.getEntregas();
			Entregas entregasNew = pedidosCliente.getEntregas();
			Clientes clienteIdOld = persistentPedidosCliente.getClienteId();
			Clientes clienteIdNew = pedidosCliente.getClienteId();
			DireccionesCliente direccionEntregaIdOld = persistentPedidosCliente.getDireccionEntregaId();
			DireccionesCliente direccionEntregaIdNew = pedidosCliente.getDireccionEntregaId();
			EstadosPedido estadoActualIdOld = persistentPedidosCliente.getEstadoActualId();
			EstadosPedido estadoActualIdNew = pedidosCliente.getEstadoActualId();
			Personal registradoPorPersonalIdOld = persistentPedidosCliente.getRegistradoPorPersonalId();
			Personal registradoPorPersonalIdNew = pedidosCliente.getRegistradoPorPersonalId();
			Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionOld = persistentPedidosCliente.getHistorialEstadosPedidoCollection();
			Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionNew = pedidosCliente.getHistorialEstadosPedidoCollection();
			Collection<ItemsPedido> itemsPedidoCollectionOld = persistentPedidosCliente.getItemsPedidoCollection();
			Collection<ItemsPedido> itemsPedidoCollectionNew = pedidosCliente.getItemsPedidoCollection();
			List<String> illegalOrphanMessages = null;
			if (entregasOld != null && !entregasOld.equals(entregasNew)) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("You must retain Entregas " + entregasOld + " since its pedidoId field is not nullable.");
			}
			for (HistorialEstadosPedido historialEstadosPedidoCollectionOldHistorialEstadosPedido : historialEstadosPedidoCollectionOld) {
				if (!historialEstadosPedidoCollectionNew.contains(historialEstadosPedidoCollectionOldHistorialEstadosPedido)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain HistorialEstadosPedido " + historialEstadosPedidoCollectionOldHistorialEstadosPedido + " since its pedidoId field is not nullable.");
				}
			}
			for (ItemsPedido itemsPedidoCollectionOldItemsPedido : itemsPedidoCollectionOld) {
				if (!itemsPedidoCollectionNew.contains(itemsPedidoCollectionOldItemsPedido)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain ItemsPedido " + itemsPedidoCollectionOldItemsPedido + " since its pedidoId field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			if (entregasNew != null) {
				entregasNew = em.getReference(entregasNew.getClass(), entregasNew.getEntregaId());
				pedidosCliente.setEntregas(entregasNew);
			}
			if (clienteIdNew != null) {
				clienteIdNew = em.getReference(clienteIdNew.getClass(), clienteIdNew.getClienteId());
				pedidosCliente.setClienteId(clienteIdNew);
			}
			if (direccionEntregaIdNew != null) {
				direccionEntregaIdNew = em.getReference(direccionEntregaIdNew.getClass(), direccionEntregaIdNew.getDireccionId());
				pedidosCliente.setDireccionEntregaId(direccionEntregaIdNew);
			}
			if (estadoActualIdNew != null) {
				estadoActualIdNew = em.getReference(estadoActualIdNew.getClass(), estadoActualIdNew.getEstadoId());
				pedidosCliente.setEstadoActualId(estadoActualIdNew);
			}
			if (registradoPorPersonalIdNew != null) {
				registradoPorPersonalIdNew = em.getReference(registradoPorPersonalIdNew.getClass(), registradoPorPersonalIdNew.getPersonalId());
				pedidosCliente.setRegistradoPorPersonalId(registradoPorPersonalIdNew);
			}
			Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollectionNew = new ArrayList<HistorialEstadosPedido>();
			for (HistorialEstadosPedido historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach : historialEstadosPedidoCollectionNew) {
				historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach.getHistorialId());
				attachedHistorialEstadosPedidoCollectionNew.add(historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach);
			}
			historialEstadosPedidoCollectionNew = attachedHistorialEstadosPedidoCollectionNew;
			pedidosCliente.setHistorialEstadosPedidoCollection(historialEstadosPedidoCollectionNew);
			Collection<ItemsPedido> attachedItemsPedidoCollectionNew = new ArrayList<ItemsPedido>();
			for (ItemsPedido itemsPedidoCollectionNewItemsPedidoToAttach : itemsPedidoCollectionNew) {
				itemsPedidoCollectionNewItemsPedidoToAttach = em.getReference(itemsPedidoCollectionNewItemsPedidoToAttach.getClass(), itemsPedidoCollectionNewItemsPedidoToAttach.getItemPedidoId());
				attachedItemsPedidoCollectionNew.add(itemsPedidoCollectionNewItemsPedidoToAttach);
			}
			itemsPedidoCollectionNew = attachedItemsPedidoCollectionNew;
			pedidosCliente.setItemsPedidoCollection(itemsPedidoCollectionNew);
			pedidosCliente = em.merge(pedidosCliente);
			if (entregasNew != null && !entregasNew.equals(entregasOld)) {
				PedidosCliente oldPedidoIdOfEntregas = entregasNew.getPedidoId();
				if (oldPedidoIdOfEntregas != null) {
					oldPedidoIdOfEntregas.setEntregas(null);
					oldPedidoIdOfEntregas = em.merge(oldPedidoIdOfEntregas);
				}
				entregasNew.setPedidoId(pedidosCliente);
				entregasNew = em.merge(entregasNew);
			}
			if (clienteIdOld != null && !clienteIdOld.equals(clienteIdNew)) {
				clienteIdOld.getPedidosClienteCollection().remove(pedidosCliente);
				clienteIdOld = em.merge(clienteIdOld);
			}
			if (clienteIdNew != null && !clienteIdNew.equals(clienteIdOld)) {
				clienteIdNew.getPedidosClienteCollection().add(pedidosCliente);
				clienteIdNew = em.merge(clienteIdNew);
			}
			if (direccionEntregaIdOld != null && !direccionEntregaIdOld.equals(direccionEntregaIdNew)) {
				direccionEntregaIdOld.getPedidosClienteCollection().remove(pedidosCliente);
				direccionEntregaIdOld = em.merge(direccionEntregaIdOld);
			}
			if (direccionEntregaIdNew != null && !direccionEntregaIdNew.equals(direccionEntregaIdOld)) {
				direccionEntregaIdNew.getPedidosClienteCollection().add(pedidosCliente);
				direccionEntregaIdNew = em.merge(direccionEntregaIdNew);
			}
			if (estadoActualIdOld != null && !estadoActualIdOld.equals(estadoActualIdNew)) {
				estadoActualIdOld.getPedidosClienteCollection().remove(pedidosCliente);
				estadoActualIdOld = em.merge(estadoActualIdOld);
			}
			if (estadoActualIdNew != null && !estadoActualIdNew.equals(estadoActualIdOld)) {
				estadoActualIdNew.getPedidosClienteCollection().add(pedidosCliente);
				estadoActualIdNew = em.merge(estadoActualIdNew);
			}
			if (registradoPorPersonalIdOld != null && !registradoPorPersonalIdOld.equals(registradoPorPersonalIdNew)) {
				registradoPorPersonalIdOld.getPedidosClienteCollection().remove(pedidosCliente);
				registradoPorPersonalIdOld = em.merge(registradoPorPersonalIdOld);
			}
			if (registradoPorPersonalIdNew != null && !registradoPorPersonalIdNew.equals(registradoPorPersonalIdOld)) {
				registradoPorPersonalIdNew.getPedidosClienteCollection().add(pedidosCliente);
				registradoPorPersonalIdNew = em.merge(registradoPorPersonalIdNew);
			}
			for (HistorialEstadosPedido historialEstadosPedidoCollectionNewHistorialEstadosPedido : historialEstadosPedidoCollectionNew) {
				if (!historialEstadosPedidoCollectionOld.contains(historialEstadosPedidoCollectionNewHistorialEstadosPedido)) {
					PedidosCliente oldPedidoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido = historialEstadosPedidoCollectionNewHistorialEstadosPedido.getPedidoId();
					historialEstadosPedidoCollectionNewHistorialEstadosPedido.setPedidoId(pedidosCliente);
					historialEstadosPedidoCollectionNewHistorialEstadosPedido = em.merge(historialEstadosPedidoCollectionNewHistorialEstadosPedido);
					if (oldPedidoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido != null && !oldPedidoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido.equals(pedidosCliente)) {
						oldPedidoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido.getHistorialEstadosPedidoCollection().remove(historialEstadosPedidoCollectionNewHistorialEstadosPedido);
						oldPedidoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido = em.merge(oldPedidoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido);
					}
				}
			}
			for (ItemsPedido itemsPedidoCollectionNewItemsPedido : itemsPedidoCollectionNew) {
				if (!itemsPedidoCollectionOld.contains(itemsPedidoCollectionNewItemsPedido)) {
					PedidosCliente oldPedidoIdOfItemsPedidoCollectionNewItemsPedido = itemsPedidoCollectionNewItemsPedido.getPedidoId();
					itemsPedidoCollectionNewItemsPedido.setPedidoId(pedidosCliente);
					itemsPedidoCollectionNewItemsPedido = em.merge(itemsPedidoCollectionNewItemsPedido);
					if (oldPedidoIdOfItemsPedidoCollectionNewItemsPedido != null && !oldPedidoIdOfItemsPedidoCollectionNewItemsPedido.equals(pedidosCliente)) {
						oldPedidoIdOfItemsPedidoCollectionNewItemsPedido.getItemsPedidoCollection().remove(itemsPedidoCollectionNewItemsPedido);
						oldPedidoIdOfItemsPedidoCollectionNewItemsPedido = em.merge(oldPedidoIdOfItemsPedidoCollectionNewItemsPedido);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Long id = pedidosCliente.getPedidoId();
				if (em.find(PedidosCliente.class, id) == null) {
					throw new NonexistentEntityException("The pedidosCliente with id " + id + " no longer exists.");
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
			PedidosCliente pedidosCliente;
			try {
				pedidosCliente = em.getReference(PedidosCliente.class, id);
				pedidosCliente.getPedidoId();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The pedidosCliente with id " + id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			Entregas entregasOrphanCheck = pedidosCliente.getEntregas();
			if (entregasOrphanCheck != null) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This PedidosCliente (" + pedidosCliente + ") cannot be destroyed since the Entregas " + entregasOrphanCheck + " in its entregas field has a non-nullable pedidoId field.");
			}
			Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionOrphanCheck = pedidosCliente.getHistorialEstadosPedidoCollection();
			for (HistorialEstadosPedido historialEstadosPedidoCollectionOrphanCheckHistorialEstadosPedido : historialEstadosPedidoCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This PedidosCliente (" + pedidosCliente + ") cannot be destroyed since the HistorialEstadosPedido " + historialEstadosPedidoCollectionOrphanCheckHistorialEstadosPedido + " in its historialEstadosPedidoCollection field has a non-nullable pedidoId field.");
			}
			Collection<ItemsPedido> itemsPedidoCollectionOrphanCheck = pedidosCliente.getItemsPedidoCollection();
			for (ItemsPedido itemsPedidoCollectionOrphanCheckItemsPedido : itemsPedidoCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages.add("This PedidosCliente (" + pedidosCliente + ") cannot be destroyed since the ItemsPedido " + itemsPedidoCollectionOrphanCheckItemsPedido + " in its itemsPedidoCollection field has a non-nullable pedidoId field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			Clientes clienteId = pedidosCliente.getClienteId();
			if (clienteId != null) {
				clienteId.getPedidosClienteCollection().remove(pedidosCliente);
				clienteId = em.merge(clienteId);
			}
			DireccionesCliente direccionEntregaId = pedidosCliente.getDireccionEntregaId();
			if (direccionEntregaId != null) {
				direccionEntregaId.getPedidosClienteCollection().remove(pedidosCliente);
				direccionEntregaId = em.merge(direccionEntregaId);
			}
			EstadosPedido estadoActualId = pedidosCliente.getEstadoActualId();
			if (estadoActualId != null) {
				estadoActualId.getPedidosClienteCollection().remove(pedidosCliente);
				estadoActualId = em.merge(estadoActualId);
			}
			Personal registradoPorPersonalId = pedidosCliente.getRegistradoPorPersonalId();
			if (registradoPorPersonalId != null) {
				registradoPorPersonalId.getPedidosClienteCollection().remove(pedidosCliente);
				registradoPorPersonalId = em.merge(registradoPorPersonalId);
			}
			em.remove(pedidosCliente);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<PedidosCliente> findPedidosClienteEntities() {
		return findPedidosClienteEntities(true, -1, -1);
	}

	public List<PedidosCliente> findPedidosClienteEntities(int maxResults, int firstResult) {
		return findPedidosClienteEntities(false, maxResults, firstResult);
	}

	private List<PedidosCliente> findPedidosClienteEntities(boolean all, int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(PedidosCliente.class));
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

	public PedidosCliente findByCodigo(String codigo) {

		EntityManager em = getEntityManager();

		try {
			return em.createQuery(
					"SELECT p FROM PedidosCliente p WHERE p.codigoPedido = :codigo",
					PedidosCliente.class)
				.setParameter("codigo", codigo)
				.getSingleResult();

		} catch (Exception e) {
			return null;

		} finally {
			em.close();
		}
	}

	public int getPedidosClienteCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<PedidosCliente> rt = cq.from(PedidosCliente.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}
}
