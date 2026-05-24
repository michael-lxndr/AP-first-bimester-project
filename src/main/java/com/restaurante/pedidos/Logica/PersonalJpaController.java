/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Logica;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Clases.Roles;
import Clases.HistorialEstadosPedido;
import java.util.ArrayList;
import java.util.Collection;
import Clases.Entregas;
import Clases.PedidosCliente;
import Clases.Personal;
import Logica.exceptions.IllegalOrphanException;
import Logica.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Montaño
 */
public class PersonalJpaController implements Serializable {

    public PersonalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Personal personal) {
        if (personal.getHistorialEstadosPedidoCollection() == null) {
            personal.setHistorialEstadosPedidoCollection(new ArrayList<HistorialEstadosPedido>());
        }
        if (personal.getEntregasCollection() == null) {
            personal.setEntregasCollection(new ArrayList<Entregas>());
        }
        if (personal.getPedidosClienteCollection() == null) {
            personal.setPedidosClienteCollection(new ArrayList<PedidosCliente>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Roles rolId = personal.getRolId();
            if (rolId != null) {
                rolId = em.getReference(rolId.getClass(), rolId.getRolId());
                personal.setRolId(rolId);
            }
            Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollection = new ArrayList<HistorialEstadosPedido>();
            for (HistorialEstadosPedido historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach : personal.getHistorialEstadosPedidoCollection()) {
                historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getHistorialId());
                attachedHistorialEstadosPedidoCollection.add(historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach);
            }
            personal.setHistorialEstadosPedidoCollection(attachedHistorialEstadosPedidoCollection);
            Collection<Entregas> attachedEntregasCollection = new ArrayList<Entregas>();
            for (Entregas entregasCollectionEntregasToAttach : personal.getEntregasCollection()) {
                entregasCollectionEntregasToAttach = em.getReference(entregasCollectionEntregasToAttach.getClass(), entregasCollectionEntregasToAttach.getEntregaId());
                attachedEntregasCollection.add(entregasCollectionEntregasToAttach);
            }
            personal.setEntregasCollection(attachedEntregasCollection);
            Collection<PedidosCliente> attachedPedidosClienteCollection = new ArrayList<PedidosCliente>();
            for (PedidosCliente pedidosClienteCollectionPedidosClienteToAttach : personal.getPedidosClienteCollection()) {
                pedidosClienteCollectionPedidosClienteToAttach = em.getReference(pedidosClienteCollectionPedidosClienteToAttach.getClass(), pedidosClienteCollectionPedidosClienteToAttach.getPedidoId());
                attachedPedidosClienteCollection.add(pedidosClienteCollectionPedidosClienteToAttach);
            }
            personal.setPedidosClienteCollection(attachedPedidosClienteCollection);
            em.persist(personal);
            if (rolId != null) {
                rolId.getPersonalCollection().add(personal);
                rolId = em.merge(rolId);
            }
            for (HistorialEstadosPedido historialEstadosPedidoCollectionHistorialEstadosPedido : personal.getHistorialEstadosPedidoCollection()) {
                Personal oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido = historialEstadosPedidoCollectionHistorialEstadosPedido.getCambiadoPorPersonalId();
                historialEstadosPedidoCollectionHistorialEstadosPedido.setCambiadoPorPersonalId(personal);
                historialEstadosPedidoCollectionHistorialEstadosPedido = em.merge(historialEstadosPedidoCollectionHistorialEstadosPedido);
                if (oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido != null) {
                    oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido.getHistorialEstadosPedidoCollection().remove(historialEstadosPedidoCollectionHistorialEstadosPedido);
                    oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido = em.merge(oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido);
                }
            }
            for (Entregas entregasCollectionEntregas : personal.getEntregasCollection()) {
                Personal oldRepartidorPersonalIdOfEntregasCollectionEntregas = entregasCollectionEntregas.getRepartidorPersonalId();
                entregasCollectionEntregas.setRepartidorPersonalId(personal);
                entregasCollectionEntregas = em.merge(entregasCollectionEntregas);
                if (oldRepartidorPersonalIdOfEntregasCollectionEntregas != null) {
                    oldRepartidorPersonalIdOfEntregasCollectionEntregas.getEntregasCollection().remove(entregasCollectionEntregas);
                    oldRepartidorPersonalIdOfEntregasCollectionEntregas = em.merge(oldRepartidorPersonalIdOfEntregasCollectionEntregas);
                }
            }
            for (PedidosCliente pedidosClienteCollectionPedidosCliente : personal.getPedidosClienteCollection()) {
                Personal oldRegistradoPorPersonalIdOfPedidosClienteCollectionPedidosCliente = pedidosClienteCollectionPedidosCliente.getRegistradoPorPersonalId();
                pedidosClienteCollectionPedidosCliente.setRegistradoPorPersonalId(personal);
                pedidosClienteCollectionPedidosCliente = em.merge(pedidosClienteCollectionPedidosCliente);
                if (oldRegistradoPorPersonalIdOfPedidosClienteCollectionPedidosCliente != null) {
                    oldRegistradoPorPersonalIdOfPedidosClienteCollectionPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionPedidosCliente);
                    oldRegistradoPorPersonalIdOfPedidosClienteCollectionPedidosCliente = em.merge(oldRegistradoPorPersonalIdOfPedidosClienteCollectionPedidosCliente);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Personal personal) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Personal persistentPersonal = em.find(Personal.class, personal.getPersonalId());
            Roles rolIdOld = persistentPersonal.getRolId();
            Roles rolIdNew = personal.getRolId();
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionOld = persistentPersonal.getHistorialEstadosPedidoCollection();
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionNew = personal.getHistorialEstadosPedidoCollection();
            Collection<Entregas> entregasCollectionOld = persistentPersonal.getEntregasCollection();
            Collection<Entregas> entregasCollectionNew = personal.getEntregasCollection();
            Collection<PedidosCliente> pedidosClienteCollectionOld = persistentPersonal.getPedidosClienteCollection();
            Collection<PedidosCliente> pedidosClienteCollectionNew = personal.getPedidosClienteCollection();
            List<String> illegalOrphanMessages = null;
            for (HistorialEstadosPedido historialEstadosPedidoCollectionOldHistorialEstadosPedido : historialEstadosPedidoCollectionOld) {
                if (!historialEstadosPedidoCollectionNew.contains(historialEstadosPedidoCollectionOldHistorialEstadosPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain HistorialEstadosPedido " + historialEstadosPedidoCollectionOldHistorialEstadosPedido + " since its cambiadoPorPersonalId field is not nullable.");
                }
            }
            for (Entregas entregasCollectionOldEntregas : entregasCollectionOld) {
                if (!entregasCollectionNew.contains(entregasCollectionOldEntregas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Entregas " + entregasCollectionOldEntregas + " since its repartidorPersonalId field is not nullable.");
                }
            }
            for (PedidosCliente pedidosClienteCollectionOldPedidosCliente : pedidosClienteCollectionOld) {
                if (!pedidosClienteCollectionNew.contains(pedidosClienteCollectionOldPedidosCliente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PedidosCliente " + pedidosClienteCollectionOldPedidosCliente + " since its registradoPorPersonalId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (rolIdNew != null) {
                rolIdNew = em.getReference(rolIdNew.getClass(), rolIdNew.getRolId());
                personal.setRolId(rolIdNew);
            }
            Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollectionNew = new ArrayList<HistorialEstadosPedido>();
            for (HistorialEstadosPedido historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach : historialEstadosPedidoCollectionNew) {
                historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach.getHistorialId());
                attachedHistorialEstadosPedidoCollectionNew.add(historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach);
            }
            historialEstadosPedidoCollectionNew = attachedHistorialEstadosPedidoCollectionNew;
            personal.setHistorialEstadosPedidoCollection(historialEstadosPedidoCollectionNew);
            Collection<Entregas> attachedEntregasCollectionNew = new ArrayList<Entregas>();
            for (Entregas entregasCollectionNewEntregasToAttach : entregasCollectionNew) {
                entregasCollectionNewEntregasToAttach = em.getReference(entregasCollectionNewEntregasToAttach.getClass(), entregasCollectionNewEntregasToAttach.getEntregaId());
                attachedEntregasCollectionNew.add(entregasCollectionNewEntregasToAttach);
            }
            entregasCollectionNew = attachedEntregasCollectionNew;
            personal.setEntregasCollection(entregasCollectionNew);
            Collection<PedidosCliente> attachedPedidosClienteCollectionNew = new ArrayList<PedidosCliente>();
            for (PedidosCliente pedidosClienteCollectionNewPedidosClienteToAttach : pedidosClienteCollectionNew) {
                pedidosClienteCollectionNewPedidosClienteToAttach = em.getReference(pedidosClienteCollectionNewPedidosClienteToAttach.getClass(), pedidosClienteCollectionNewPedidosClienteToAttach.getPedidoId());
                attachedPedidosClienteCollectionNew.add(pedidosClienteCollectionNewPedidosClienteToAttach);
            }
            pedidosClienteCollectionNew = attachedPedidosClienteCollectionNew;
            personal.setPedidosClienteCollection(pedidosClienteCollectionNew);
            personal = em.merge(personal);
            if (rolIdOld != null && !rolIdOld.equals(rolIdNew)) {
                rolIdOld.getPersonalCollection().remove(personal);
                rolIdOld = em.merge(rolIdOld);
            }
            if (rolIdNew != null && !rolIdNew.equals(rolIdOld)) {
                rolIdNew.getPersonalCollection().add(personal);
                rolIdNew = em.merge(rolIdNew);
            }
            for (HistorialEstadosPedido historialEstadosPedidoCollectionNewHistorialEstadosPedido : historialEstadosPedidoCollectionNew) {
                if (!historialEstadosPedidoCollectionOld.contains(historialEstadosPedidoCollectionNewHistorialEstadosPedido)) {
                    Personal oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido = historialEstadosPedidoCollectionNewHistorialEstadosPedido.getCambiadoPorPersonalId();
                    historialEstadosPedidoCollectionNewHistorialEstadosPedido.setCambiadoPorPersonalId(personal);
                    historialEstadosPedidoCollectionNewHistorialEstadosPedido = em.merge(historialEstadosPedidoCollectionNewHistorialEstadosPedido);
                    if (oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido != null && !oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido.equals(personal)) {
                        oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido.getHistorialEstadosPedidoCollection().remove(historialEstadosPedidoCollectionNewHistorialEstadosPedido);
                        oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido = em.merge(oldCambiadoPorPersonalIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido);
                    }
                }
            }
            for (Entregas entregasCollectionNewEntregas : entregasCollectionNew) {
                if (!entregasCollectionOld.contains(entregasCollectionNewEntregas)) {
                    Personal oldRepartidorPersonalIdOfEntregasCollectionNewEntregas = entregasCollectionNewEntregas.getRepartidorPersonalId();
                    entregasCollectionNewEntregas.setRepartidorPersonalId(personal);
                    entregasCollectionNewEntregas = em.merge(entregasCollectionNewEntregas);
                    if (oldRepartidorPersonalIdOfEntregasCollectionNewEntregas != null && !oldRepartidorPersonalIdOfEntregasCollectionNewEntregas.equals(personal)) {
                        oldRepartidorPersonalIdOfEntregasCollectionNewEntregas.getEntregasCollection().remove(entregasCollectionNewEntregas);
                        oldRepartidorPersonalIdOfEntregasCollectionNewEntregas = em.merge(oldRepartidorPersonalIdOfEntregasCollectionNewEntregas);
                    }
                }
            }
            for (PedidosCliente pedidosClienteCollectionNewPedidosCliente : pedidosClienteCollectionNew) {
                if (!pedidosClienteCollectionOld.contains(pedidosClienteCollectionNewPedidosCliente)) {
                    Personal oldRegistradoPorPersonalIdOfPedidosClienteCollectionNewPedidosCliente = pedidosClienteCollectionNewPedidosCliente.getRegistradoPorPersonalId();
                    pedidosClienteCollectionNewPedidosCliente.setRegistradoPorPersonalId(personal);
                    pedidosClienteCollectionNewPedidosCliente = em.merge(pedidosClienteCollectionNewPedidosCliente);
                    if (oldRegistradoPorPersonalIdOfPedidosClienteCollectionNewPedidosCliente != null && !oldRegistradoPorPersonalIdOfPedidosClienteCollectionNewPedidosCliente.equals(personal)) {
                        oldRegistradoPorPersonalIdOfPedidosClienteCollectionNewPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionNewPedidosCliente);
                        oldRegistradoPorPersonalIdOfPedidosClienteCollectionNewPedidosCliente = em.merge(oldRegistradoPorPersonalIdOfPedidosClienteCollectionNewPedidosCliente);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = personal.getPersonalId();
                if (findPersonal(id) == null) {
                    throw new NonexistentEntityException("The personal with id " + id + " no longer exists.");
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
            Personal personal;
            try {
                personal = em.getReference(Personal.class, id);
                personal.getPersonalId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The personal with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionOrphanCheck = personal.getHistorialEstadosPedidoCollection();
            for (HistorialEstadosPedido historialEstadosPedidoCollectionOrphanCheckHistorialEstadosPedido : historialEstadosPedidoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Personal (" + personal + ") cannot be destroyed since the HistorialEstadosPedido " + historialEstadosPedidoCollectionOrphanCheckHistorialEstadosPedido + " in its historialEstadosPedidoCollection field has a non-nullable cambiadoPorPersonalId field.");
            }
            Collection<Entregas> entregasCollectionOrphanCheck = personal.getEntregasCollection();
            for (Entregas entregasCollectionOrphanCheckEntregas : entregasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Personal (" + personal + ") cannot be destroyed since the Entregas " + entregasCollectionOrphanCheckEntregas + " in its entregasCollection field has a non-nullable repartidorPersonalId field.");
            }
            Collection<PedidosCliente> pedidosClienteCollectionOrphanCheck = personal.getPedidosClienteCollection();
            for (PedidosCliente pedidosClienteCollectionOrphanCheckPedidosCliente : pedidosClienteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Personal (" + personal + ") cannot be destroyed since the PedidosCliente " + pedidosClienteCollectionOrphanCheckPedidosCliente + " in its pedidosClienteCollection field has a non-nullable registradoPorPersonalId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Roles rolId = personal.getRolId();
            if (rolId != null) {
                rolId.getPersonalCollection().remove(personal);
                rolId = em.merge(rolId);
            }
            em.remove(personal);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Personal> findPersonalEntities() {
        return findPersonalEntities(true, -1, -1);
    }

    public List<Personal> findPersonalEntities(int maxResults, int firstResult) {
        return findPersonalEntities(false, maxResults, firstResult);
    }

    private List<Personal> findPersonalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Personal.class));
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

    public Personal findPersonal(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Personal.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Personal> rt = cq.from(Personal.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
