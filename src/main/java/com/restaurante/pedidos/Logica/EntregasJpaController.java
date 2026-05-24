/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Logica;

import Clases.Entregas;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Clases.PedidosCliente;
import Clases.Personal;
import Logica.exceptions.IllegalOrphanException;
import Logica.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Montaño
 */
public class EntregasJpaController implements Serializable {

    public EntregasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Entregas entregas) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        PedidosCliente pedidoIdOrphanCheck = entregas.getPedidoId();
        if (pedidoIdOrphanCheck != null) {
            Entregas oldEntregasOfPedidoId = pedidoIdOrphanCheck.getEntregas();
            if (oldEntregasOfPedidoId != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The PedidosCliente " + pedidoIdOrphanCheck + " already has an item of type Entregas whose pedidoId column cannot be null. Please make another selection for the pedidoId field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PedidosCliente pedidoId = entregas.getPedidoId();
            if (pedidoId != null) {
                pedidoId = em.getReference(pedidoId.getClass(), pedidoId.getPedidoId());
                entregas.setPedidoId(pedidoId);
            }
            Personal repartidorPersonalId = entregas.getRepartidorPersonalId();
            if (repartidorPersonalId != null) {
                repartidorPersonalId = em.getReference(repartidorPersonalId.getClass(), repartidorPersonalId.getPersonalId());
                entregas.setRepartidorPersonalId(repartidorPersonalId);
            }
            em.persist(entregas);
            if (pedidoId != null) {
                pedidoId.setEntregas(entregas);
                pedidoId = em.merge(pedidoId);
            }
            if (repartidorPersonalId != null) {
                repartidorPersonalId.getEntregasCollection().add(entregas);
                repartidorPersonalId = em.merge(repartidorPersonalId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Entregas entregas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Entregas persistentEntregas = em.find(Entregas.class, entregas.getEntregaId());
            PedidosCliente pedidoIdOld = persistentEntregas.getPedidoId();
            PedidosCliente pedidoIdNew = entregas.getPedidoId();
            Personal repartidorPersonalIdOld = persistentEntregas.getRepartidorPersonalId();
            Personal repartidorPersonalIdNew = entregas.getRepartidorPersonalId();
            List<String> illegalOrphanMessages = null;
            if (pedidoIdNew != null && !pedidoIdNew.equals(pedidoIdOld)) {
                Entregas oldEntregasOfPedidoId = pedidoIdNew.getEntregas();
                if (oldEntregasOfPedidoId != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The PedidosCliente " + pedidoIdNew + " already has an item of type Entregas whose pedidoId column cannot be null. Please make another selection for the pedidoId field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pedidoIdNew != null) {
                pedidoIdNew = em.getReference(pedidoIdNew.getClass(), pedidoIdNew.getPedidoId());
                entregas.setPedidoId(pedidoIdNew);
            }
            if (repartidorPersonalIdNew != null) {
                repartidorPersonalIdNew = em.getReference(repartidorPersonalIdNew.getClass(), repartidorPersonalIdNew.getPersonalId());
                entregas.setRepartidorPersonalId(repartidorPersonalIdNew);
            }
            entregas = em.merge(entregas);
            if (pedidoIdOld != null && !pedidoIdOld.equals(pedidoIdNew)) {
                pedidoIdOld.setEntregas(null);
                pedidoIdOld = em.merge(pedidoIdOld);
            }
            if (pedidoIdNew != null && !pedidoIdNew.equals(pedidoIdOld)) {
                pedidoIdNew.setEntregas(entregas);
                pedidoIdNew = em.merge(pedidoIdNew);
            }
            if (repartidorPersonalIdOld != null && !repartidorPersonalIdOld.equals(repartidorPersonalIdNew)) {
                repartidorPersonalIdOld.getEntregasCollection().remove(entregas);
                repartidorPersonalIdOld = em.merge(repartidorPersonalIdOld);
            }
            if (repartidorPersonalIdNew != null && !repartidorPersonalIdNew.equals(repartidorPersonalIdOld)) {
                repartidorPersonalIdNew.getEntregasCollection().add(entregas);
                repartidorPersonalIdNew = em.merge(repartidorPersonalIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = entregas.getEntregaId();
                if (findEntregas(id) == null) {
                    throw new NonexistentEntityException("The entregas with id " + id + " no longer exists.");
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
            Entregas entregas;
            try {
                entregas = em.getReference(Entregas.class, id);
                entregas.getEntregaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The entregas with id " + id + " no longer exists.", enfe);
            }
            PedidosCliente pedidoId = entregas.getPedidoId();
            if (pedidoId != null) {
                pedidoId.setEntregas(null);
                pedidoId = em.merge(pedidoId);
            }
            Personal repartidorPersonalId = entregas.getRepartidorPersonalId();
            if (repartidorPersonalId != null) {
                repartidorPersonalId.getEntregasCollection().remove(entregas);
                repartidorPersonalId = em.merge(repartidorPersonalId);
            }
            em.remove(entregas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Entregas> findEntregasEntities() {
        return findEntregasEntities(true, -1, -1);
    }

    public List<Entregas> findEntregasEntities(int maxResults, int firstResult) {
        return findEntregasEntities(false, maxResults, firstResult);
    }

    private List<Entregas> findEntregasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Entregas.class));
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

    public Entregas findEntregas(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Entregas.class, id);
        } finally {
            em.close();
        }
    }

    public int getEntregasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Entregas> rt = cq.from(Entregas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
