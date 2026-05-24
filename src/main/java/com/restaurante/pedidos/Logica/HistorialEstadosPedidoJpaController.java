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
import Clases.EstadosPedido;
import Clases.HistorialEstadosPedido;
import Clases.PedidosCliente;
import Clases.Personal;
import Logica.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Montaño
 */
public class HistorialEstadosPedidoJpaController implements Serializable {

    public HistorialEstadosPedidoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(HistorialEstadosPedido historialEstadosPedido) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            EstadosPedido estadoDestinoId = historialEstadosPedido.getEstadoDestinoId();
            if (estadoDestinoId != null) {
                estadoDestinoId = em.getReference(estadoDestinoId.getClass(), estadoDestinoId.getEstadoId());
                historialEstadosPedido.setEstadoDestinoId(estadoDestinoId);
            }
            EstadosPedido estadoOrigenId = historialEstadosPedido.getEstadoOrigenId();
            if (estadoOrigenId != null) {
                estadoOrigenId = em.getReference(estadoOrigenId.getClass(), estadoOrigenId.getEstadoId());
                historialEstadosPedido.setEstadoOrigenId(estadoOrigenId);
            }
            PedidosCliente pedidoId = historialEstadosPedido.getPedidoId();
            if (pedidoId != null) {
                pedidoId = em.getReference(pedidoId.getClass(), pedidoId.getPedidoId());
                historialEstadosPedido.setPedidoId(pedidoId);
            }
            Personal cambiadoPorPersonalId = historialEstadosPedido.getCambiadoPorPersonalId();
            if (cambiadoPorPersonalId != null) {
                cambiadoPorPersonalId = em.getReference(cambiadoPorPersonalId.getClass(), cambiadoPorPersonalId.getPersonalId());
                historialEstadosPedido.setCambiadoPorPersonalId(cambiadoPorPersonalId);
            }
            em.persist(historialEstadosPedido);
            if (estadoDestinoId != null) {
                estadoDestinoId.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                estadoDestinoId = em.merge(estadoDestinoId);
            }
            if (estadoOrigenId != null) {
                estadoOrigenId.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                estadoOrigenId = em.merge(estadoOrigenId);
            }
            if (pedidoId != null) {
                pedidoId.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                pedidoId = em.merge(pedidoId);
            }
            if (cambiadoPorPersonalId != null) {
                cambiadoPorPersonalId.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                cambiadoPorPersonalId = em.merge(cambiadoPorPersonalId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(HistorialEstadosPedido historialEstadosPedido) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HistorialEstadosPedido persistentHistorialEstadosPedido = em.find(HistorialEstadosPedido.class, historialEstadosPedido.getHistorialId());
            EstadosPedido estadoDestinoIdOld = persistentHistorialEstadosPedido.getEstadoDestinoId();
            EstadosPedido estadoDestinoIdNew = historialEstadosPedido.getEstadoDestinoId();
            EstadosPedido estadoOrigenIdOld = persistentHistorialEstadosPedido.getEstadoOrigenId();
            EstadosPedido estadoOrigenIdNew = historialEstadosPedido.getEstadoOrigenId();
            PedidosCliente pedidoIdOld = persistentHistorialEstadosPedido.getPedidoId();
            PedidosCliente pedidoIdNew = historialEstadosPedido.getPedidoId();
            Personal cambiadoPorPersonalIdOld = persistentHistorialEstadosPedido.getCambiadoPorPersonalId();
            Personal cambiadoPorPersonalIdNew = historialEstadosPedido.getCambiadoPorPersonalId();
            if (estadoDestinoIdNew != null) {
                estadoDestinoIdNew = em.getReference(estadoDestinoIdNew.getClass(), estadoDestinoIdNew.getEstadoId());
                historialEstadosPedido.setEstadoDestinoId(estadoDestinoIdNew);
            }
            if (estadoOrigenIdNew != null) {
                estadoOrigenIdNew = em.getReference(estadoOrigenIdNew.getClass(), estadoOrigenIdNew.getEstadoId());
                historialEstadosPedido.setEstadoOrigenId(estadoOrigenIdNew);
            }
            if (pedidoIdNew != null) {
                pedidoIdNew = em.getReference(pedidoIdNew.getClass(), pedidoIdNew.getPedidoId());
                historialEstadosPedido.setPedidoId(pedidoIdNew);
            }
            if (cambiadoPorPersonalIdNew != null) {
                cambiadoPorPersonalIdNew = em.getReference(cambiadoPorPersonalIdNew.getClass(), cambiadoPorPersonalIdNew.getPersonalId());
                historialEstadosPedido.setCambiadoPorPersonalId(cambiadoPorPersonalIdNew);
            }
            historialEstadosPedido = em.merge(historialEstadosPedido);
            if (estadoDestinoIdOld != null && !estadoDestinoIdOld.equals(estadoDestinoIdNew)) {
                estadoDestinoIdOld.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                estadoDestinoIdOld = em.merge(estadoDestinoIdOld);
            }
            if (estadoDestinoIdNew != null && !estadoDestinoIdNew.equals(estadoDestinoIdOld)) {
                estadoDestinoIdNew.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                estadoDestinoIdNew = em.merge(estadoDestinoIdNew);
            }
            if (estadoOrigenIdOld != null && !estadoOrigenIdOld.equals(estadoOrigenIdNew)) {
                estadoOrigenIdOld.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                estadoOrigenIdOld = em.merge(estadoOrigenIdOld);
            }
            if (estadoOrigenIdNew != null && !estadoOrigenIdNew.equals(estadoOrigenIdOld)) {
                estadoOrigenIdNew.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                estadoOrigenIdNew = em.merge(estadoOrigenIdNew);
            }
            if (pedidoIdOld != null && !pedidoIdOld.equals(pedidoIdNew)) {
                pedidoIdOld.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                pedidoIdOld = em.merge(pedidoIdOld);
            }
            if (pedidoIdNew != null && !pedidoIdNew.equals(pedidoIdOld)) {
                pedidoIdNew.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                pedidoIdNew = em.merge(pedidoIdNew);
            }
            if (cambiadoPorPersonalIdOld != null && !cambiadoPorPersonalIdOld.equals(cambiadoPorPersonalIdNew)) {
                cambiadoPorPersonalIdOld.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                cambiadoPorPersonalIdOld = em.merge(cambiadoPorPersonalIdOld);
            }
            if (cambiadoPorPersonalIdNew != null && !cambiadoPorPersonalIdNew.equals(cambiadoPorPersonalIdOld)) {
                cambiadoPorPersonalIdNew.getHistorialEstadosPedidoCollection().add(historialEstadosPedido);
                cambiadoPorPersonalIdNew = em.merge(cambiadoPorPersonalIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = historialEstadosPedido.getHistorialId();
                if (findHistorialEstadosPedido(id) == null) {
                    throw new NonexistentEntityException("The historialEstadosPedido with id " + id + " no longer exists.");
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
            HistorialEstadosPedido historialEstadosPedido;
            try {
                historialEstadosPedido = em.getReference(HistorialEstadosPedido.class, id);
                historialEstadosPedido.getHistorialId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The historialEstadosPedido with id " + id + " no longer exists.", enfe);
            }
            EstadosPedido estadoDestinoId = historialEstadosPedido.getEstadoDestinoId();
            if (estadoDestinoId != null) {
                estadoDestinoId.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                estadoDestinoId = em.merge(estadoDestinoId);
            }
            EstadosPedido estadoOrigenId = historialEstadosPedido.getEstadoOrigenId();
            if (estadoOrigenId != null) {
                estadoOrigenId.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                estadoOrigenId = em.merge(estadoOrigenId);
            }
            PedidosCliente pedidoId = historialEstadosPedido.getPedidoId();
            if (pedidoId != null) {
                pedidoId.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                pedidoId = em.merge(pedidoId);
            }
            Personal cambiadoPorPersonalId = historialEstadosPedido.getCambiadoPorPersonalId();
            if (cambiadoPorPersonalId != null) {
                cambiadoPorPersonalId.getHistorialEstadosPedidoCollection().remove(historialEstadosPedido);
                cambiadoPorPersonalId = em.merge(cambiadoPorPersonalId);
            }
            em.remove(historialEstadosPedido);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<HistorialEstadosPedido> findHistorialEstadosPedidoEntities() {
        return findHistorialEstadosPedidoEntities(true, -1, -1);
    }

    public List<HistorialEstadosPedido> findHistorialEstadosPedidoEntities(int maxResults, int firstResult) {
        return findHistorialEstadosPedidoEntities(false, maxResults, firstResult);
    }

    private List<HistorialEstadosPedido> findHistorialEstadosPedidoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HistorialEstadosPedido.class));
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

    public HistorialEstadosPedido findHistorialEstadosPedido(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HistorialEstadosPedido.class, id);
        } finally {
            em.close();
        }
    }

    public int getHistorialEstadosPedidoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HistorialEstadosPedido> rt = cq.from(HistorialEstadosPedido.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
