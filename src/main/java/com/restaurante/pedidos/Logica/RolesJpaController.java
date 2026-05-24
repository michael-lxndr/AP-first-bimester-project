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
import Clases.Personal;
import java.util.ArrayList;
import java.util.Collection;
import Clases.ReglasTransicionEstadoPedido;
import Clases.Roles;
import Logica.exceptions.IllegalOrphanException;
import Logica.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Montaño
 */
public class RolesJpaController implements Serializable {

    public RolesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Roles roles) {
        if (roles.getPersonalCollection() == null) {
            roles.setPersonalCollection(new ArrayList<Personal>());
        }
        if (roles.getReglasTransicionEstadoPedidoCollection() == null) {
            roles.setReglasTransicionEstadoPedidoCollection(new ArrayList<ReglasTransicionEstadoPedido>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Personal> attachedPersonalCollection = new ArrayList<Personal>();
            for (Personal personalCollectionPersonalToAttach : roles.getPersonalCollection()) {
                personalCollectionPersonalToAttach = em.getReference(personalCollectionPersonalToAttach.getClass(), personalCollectionPersonalToAttach.getPersonalId());
                attachedPersonalCollection.add(personalCollectionPersonalToAttach);
            }
            roles.setPersonalCollection(attachedPersonalCollection);
            Collection<ReglasTransicionEstadoPedido> attachedReglasTransicionEstadoPedidoCollection = new ArrayList<ReglasTransicionEstadoPedido>();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach : roles.getReglasTransicionEstadoPedidoCollection()) {
                reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach = em.getReference(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach.getClass(), reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach.getReglaTransicionId());
                attachedReglasTransicionEstadoPedidoCollection.add(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach);
            }
            roles.setReglasTransicionEstadoPedidoCollection(attachedReglasTransicionEstadoPedidoCollection);
            em.persist(roles);
            for (Personal personalCollectionPersonal : roles.getPersonalCollection()) {
                Roles oldRolIdOfPersonalCollectionPersonal = personalCollectionPersonal.getRolId();
                personalCollectionPersonal.setRolId(roles);
                personalCollectionPersonal = em.merge(personalCollectionPersonal);
                if (oldRolIdOfPersonalCollectionPersonal != null) {
                    oldRolIdOfPersonalCollectionPersonal.getPersonalCollection().remove(personalCollectionPersonal);
                    oldRolIdOfPersonalCollectionPersonal = em.merge(oldRolIdOfPersonalCollectionPersonal);
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido : roles.getReglasTransicionEstadoPedidoCollection()) {
                Roles oldRolIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido = reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido.getRolId();
                reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido.setRolId(roles);
                reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido);
                if (oldRolIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido != null) {
                    oldRolIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido);
                    oldRolIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido = em.merge(oldRolIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Roles roles) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Roles persistentRoles = em.find(Roles.class, roles.getRolId());
            Collection<Personal> personalCollectionOld = persistentRoles.getPersonalCollection();
            Collection<Personal> personalCollectionNew = roles.getPersonalCollection();
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollectionOld = persistentRoles.getReglasTransicionEstadoPedidoCollection();
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollectionNew = roles.getReglasTransicionEstadoPedidoCollection();
            List<String> illegalOrphanMessages = null;
            for (Personal personalCollectionOldPersonal : personalCollectionOld) {
                if (!personalCollectionNew.contains(personalCollectionOldPersonal)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Personal " + personalCollectionOldPersonal + " since its rolId field is not nullable.");
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionOldReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollectionOld) {
                if (!reglasTransicionEstadoPedidoCollectionNew.contains(reglasTransicionEstadoPedidoCollectionOldReglasTransicionEstadoPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ReglasTransicionEstadoPedido " + reglasTransicionEstadoPedidoCollectionOldReglasTransicionEstadoPedido + " since its rolId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Personal> attachedPersonalCollectionNew = new ArrayList<Personal>();
            for (Personal personalCollectionNewPersonalToAttach : personalCollectionNew) {
                personalCollectionNewPersonalToAttach = em.getReference(personalCollectionNewPersonalToAttach.getClass(), personalCollectionNewPersonalToAttach.getPersonalId());
                attachedPersonalCollectionNew.add(personalCollectionNewPersonalToAttach);
            }
            personalCollectionNew = attachedPersonalCollectionNew;
            roles.setPersonalCollection(personalCollectionNew);
            Collection<ReglasTransicionEstadoPedido> attachedReglasTransicionEstadoPedidoCollectionNew = new ArrayList<ReglasTransicionEstadoPedido>();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach : reglasTransicionEstadoPedidoCollectionNew) {
                reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach = em.getReference(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach.getClass(), reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach.getReglaTransicionId());
                attachedReglasTransicionEstadoPedidoCollectionNew.add(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach);
            }
            reglasTransicionEstadoPedidoCollectionNew = attachedReglasTransicionEstadoPedidoCollectionNew;
            roles.setReglasTransicionEstadoPedidoCollection(reglasTransicionEstadoPedidoCollectionNew);
            roles = em.merge(roles);
            for (Personal personalCollectionNewPersonal : personalCollectionNew) {
                if (!personalCollectionOld.contains(personalCollectionNewPersonal)) {
                    Roles oldRolIdOfPersonalCollectionNewPersonal = personalCollectionNewPersonal.getRolId();
                    personalCollectionNewPersonal.setRolId(roles);
                    personalCollectionNewPersonal = em.merge(personalCollectionNewPersonal);
                    if (oldRolIdOfPersonalCollectionNewPersonal != null && !oldRolIdOfPersonalCollectionNewPersonal.equals(roles)) {
                        oldRolIdOfPersonalCollectionNewPersonal.getPersonalCollection().remove(personalCollectionNewPersonal);
                        oldRolIdOfPersonalCollectionNewPersonal = em.merge(oldRolIdOfPersonalCollectionNewPersonal);
                    }
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollectionNew) {
                if (!reglasTransicionEstadoPedidoCollectionOld.contains(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido)) {
                    Roles oldRolIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido = reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.getRolId();
                    reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.setRolId(roles);
                    reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido);
                    if (oldRolIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido != null && !oldRolIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.equals(roles)) {
                        oldRolIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido);
                        oldRolIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido = em.merge(oldRolIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = roles.getRolId();
                if (findRoles(id) == null) {
                    throw new NonexistentEntityException("The roles with id " + id + " no longer exists.");
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
            Roles roles;
            try {
                roles = em.getReference(Roles.class, id);
                roles.getRolId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The roles with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Personal> personalCollectionOrphanCheck = roles.getPersonalCollection();
            for (Personal personalCollectionOrphanCheckPersonal : personalCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Roles (" + roles + ") cannot be destroyed since the Personal " + personalCollectionOrphanCheckPersonal + " in its personalCollection field has a non-nullable rolId field.");
            }
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollectionOrphanCheck = roles.getReglasTransicionEstadoPedidoCollection();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionOrphanCheckReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Roles (" + roles + ") cannot be destroyed since the ReglasTransicionEstadoPedido " + reglasTransicionEstadoPedidoCollectionOrphanCheckReglasTransicionEstadoPedido + " in its reglasTransicionEstadoPedidoCollection field has a non-nullable rolId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(roles);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Roles> findRolesEntities() {
        return findRolesEntities(true, -1, -1);
    }

    public List<Roles> findRolesEntities(int maxResults, int firstResult) {
        return findRolesEntities(false, maxResults, firstResult);
    }

    private List<Roles> findRolesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Roles.class));
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

    public Roles findRoles(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Roles.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Roles> rt = cq.from(Roles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
