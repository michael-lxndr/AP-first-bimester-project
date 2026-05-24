/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Logica;

import com.restaurante.pedidos.Clases.EstadosPedido;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.restaurante.pedidos.Clases.HistorialEstadosPedido;
import java.util.ArrayList;
import java.util.Collection;
import com.restaurante.pedidos.Clases.PedidosCliente;
import com.restaurante.pedidos.Clases.ReglasTransicionEstadoPedido;
import com.restaurante.pedidos.Logica.exceptions.IllegalOrphanException;
import com.restaurante.pedidos.Logica.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class EstadosPedidoJpaController implements Serializable {

    public EstadosPedidoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(EstadosPedido estadosPedido) {
        if (estadosPedido.getHistorialEstadosPedidoCollection() == null) {
            estadosPedido.setHistorialEstadosPedidoCollection(new ArrayList<HistorialEstadosPedido>());
        }
        if (estadosPedido.getHistorialEstadosPedidoCollection1() == null) {
            estadosPedido.setHistorialEstadosPedidoCollection1(new ArrayList<HistorialEstadosPedido>());
        }
        if (estadosPedido.getPedidosClienteCollection() == null) {
            estadosPedido.setPedidosClienteCollection(new ArrayList<PedidosCliente>());
        }
        if (estadosPedido.getReglasTransicionEstadoPedidoCollection() == null) {
            estadosPedido.setReglasTransicionEstadoPedidoCollection(new ArrayList<ReglasTransicionEstadoPedido>());
        }
        if (estadosPedido.getReglasTransicionEstadoPedidoCollection1() == null) {
            estadosPedido.setReglasTransicionEstadoPedidoCollection1(new ArrayList<ReglasTransicionEstadoPedido>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollection = new ArrayList<HistorialEstadosPedido>();
            for (HistorialEstadosPedido historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach : estadosPedido.getHistorialEstadosPedidoCollection()) {
                historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach.getHistorialId());
                attachedHistorialEstadosPedidoCollection.add(historialEstadosPedidoCollectionHistorialEstadosPedidoToAttach);
            }
            estadosPedido.setHistorialEstadosPedidoCollection(attachedHistorialEstadosPedidoCollection);
            Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollection1 = new ArrayList<HistorialEstadosPedido>();
            for (HistorialEstadosPedido historialEstadosPedidoCollection1HistorialEstadosPedidoToAttach : estadosPedido.getHistorialEstadosPedidoCollection1()) {
                historialEstadosPedidoCollection1HistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollection1HistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollection1HistorialEstadosPedidoToAttach.getHistorialId());
                attachedHistorialEstadosPedidoCollection1.add(historialEstadosPedidoCollection1HistorialEstadosPedidoToAttach);
            }
            estadosPedido.setHistorialEstadosPedidoCollection1(attachedHistorialEstadosPedidoCollection1);
            Collection<PedidosCliente> attachedPedidosClienteCollection = new ArrayList<PedidosCliente>();
            for (PedidosCliente pedidosClienteCollectionPedidosClienteToAttach : estadosPedido.getPedidosClienteCollection()) {
                pedidosClienteCollectionPedidosClienteToAttach = em.getReference(pedidosClienteCollectionPedidosClienteToAttach.getClass(), pedidosClienteCollectionPedidosClienteToAttach.getPedidoId());
                attachedPedidosClienteCollection.add(pedidosClienteCollectionPedidosClienteToAttach);
            }
            estadosPedido.setPedidosClienteCollection(attachedPedidosClienteCollection);
            Collection<ReglasTransicionEstadoPedido> attachedReglasTransicionEstadoPedidoCollection = new ArrayList<ReglasTransicionEstadoPedido>();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach : estadosPedido.getReglasTransicionEstadoPedidoCollection()) {
                reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach = em.getReference(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach.getClass(), reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach.getReglaTransicionId());
                attachedReglasTransicionEstadoPedidoCollection.add(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedidoToAttach);
            }
            estadosPedido.setReglasTransicionEstadoPedidoCollection(attachedReglasTransicionEstadoPedidoCollection);
            Collection<ReglasTransicionEstadoPedido> attachedReglasTransicionEstadoPedidoCollection1 = new ArrayList<ReglasTransicionEstadoPedido>();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedidoToAttach : estadosPedido.getReglasTransicionEstadoPedidoCollection1()) {
                reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedidoToAttach = em.getReference(reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedidoToAttach.getClass(), reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedidoToAttach.getReglaTransicionId());
                attachedReglasTransicionEstadoPedidoCollection1.add(reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedidoToAttach);
            }
            estadosPedido.setReglasTransicionEstadoPedidoCollection1(attachedReglasTransicionEstadoPedidoCollection1);
            em.persist(estadosPedido);
            for (HistorialEstadosPedido historialEstadosPedidoCollectionHistorialEstadosPedido : estadosPedido.getHistorialEstadosPedidoCollection()) {
                EstadosPedido oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido = historialEstadosPedidoCollectionHistorialEstadosPedido.getEstadoDestinoId();
                historialEstadosPedidoCollectionHistorialEstadosPedido.setEstadoDestinoId(estadosPedido);
                historialEstadosPedidoCollectionHistorialEstadosPedido = em.merge(historialEstadosPedidoCollectionHistorialEstadosPedido);
                if (oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido != null) {
                    oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido.getHistorialEstadosPedidoCollection().remove(historialEstadosPedidoCollectionHistorialEstadosPedido);
                    oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido = em.merge(oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionHistorialEstadosPedido);
                }
            }
            for (HistorialEstadosPedido historialEstadosPedidoCollection1HistorialEstadosPedido : estadosPedido.getHistorialEstadosPedidoCollection1()) {
                EstadosPedido oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1HistorialEstadosPedido = historialEstadosPedidoCollection1HistorialEstadosPedido.getEstadoOrigenId();
                historialEstadosPedidoCollection1HistorialEstadosPedido.setEstadoOrigenId(estadosPedido);
                historialEstadosPedidoCollection1HistorialEstadosPedido = em.merge(historialEstadosPedidoCollection1HistorialEstadosPedido);
                if (oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1HistorialEstadosPedido != null) {
                    oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1HistorialEstadosPedido.getHistorialEstadosPedidoCollection1().remove(historialEstadosPedidoCollection1HistorialEstadosPedido);
                    oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1HistorialEstadosPedido = em.merge(oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1HistorialEstadosPedido);
                }
            }
            for (PedidosCliente pedidosClienteCollectionPedidosCliente : estadosPedido.getPedidosClienteCollection()) {
                EstadosPedido oldEstadoActualIdOfPedidosClienteCollectionPedidosCliente = pedidosClienteCollectionPedidosCliente.getEstadoActualId();
                pedidosClienteCollectionPedidosCliente.setEstadoActualId(estadosPedido);
                pedidosClienteCollectionPedidosCliente = em.merge(pedidosClienteCollectionPedidosCliente);
                if (oldEstadoActualIdOfPedidosClienteCollectionPedidosCliente != null) {
                    oldEstadoActualIdOfPedidosClienteCollectionPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionPedidosCliente);
                    oldEstadoActualIdOfPedidosClienteCollectionPedidosCliente = em.merge(oldEstadoActualIdOfPedidosClienteCollectionPedidosCliente);
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido : estadosPedido.getReglasTransicionEstadoPedidoCollection()) {
                EstadosPedido oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido = reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido.getEstadoDestinoId();
                reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido.setEstadoDestinoId(estadosPedido);
                reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido);
                if (oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido != null) {
                    oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido);
                    oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido = em.merge(oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionReglasTransicionEstadoPedido);
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido : estadosPedido.getReglasTransicionEstadoPedidoCollection1()) {
                EstadosPedido oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido = reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido.getEstadoOrigenId();
                reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido.setEstadoOrigenId(estadosPedido);
                reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido);
                if (oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido != null) {
                    oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido.getReglasTransicionEstadoPedidoCollection1().remove(reglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido);
                    oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido = em.merge(oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1ReglasTransicionEstadoPedido);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(EstadosPedido estadosPedido) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            EstadosPedido persistentEstadosPedido = em.find(EstadosPedido.class, estadosPedido.getEstadoId());
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionOld = persistentEstadosPedido.getHistorialEstadosPedidoCollection();
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionNew = estadosPedido.getHistorialEstadosPedidoCollection();
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollection1Old = persistentEstadosPedido.getHistorialEstadosPedidoCollection1();
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollection1New = estadosPedido.getHistorialEstadosPedidoCollection1();
            Collection<PedidosCliente> pedidosClienteCollectionOld = persistentEstadosPedido.getPedidosClienteCollection();
            Collection<PedidosCliente> pedidosClienteCollectionNew = estadosPedido.getPedidosClienteCollection();
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollectionOld = persistentEstadosPedido.getReglasTransicionEstadoPedidoCollection();
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollectionNew = estadosPedido.getReglasTransicionEstadoPedidoCollection();
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection1Old = persistentEstadosPedido.getReglasTransicionEstadoPedidoCollection1();
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection1New = estadosPedido.getReglasTransicionEstadoPedidoCollection1();
            List<String> illegalOrphanMessages = null;
            for (HistorialEstadosPedido historialEstadosPedidoCollectionOldHistorialEstadosPedido : historialEstadosPedidoCollectionOld) {
                if (!historialEstadosPedidoCollectionNew.contains(historialEstadosPedidoCollectionOldHistorialEstadosPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain HistorialEstadosPedido " + historialEstadosPedidoCollectionOldHistorialEstadosPedido + " since its estadoDestinoId field is not nullable.");
                }
            }
            for (HistorialEstadosPedido historialEstadosPedidoCollection1OldHistorialEstadosPedido : historialEstadosPedidoCollection1Old) {
                if (!historialEstadosPedidoCollection1New.contains(historialEstadosPedidoCollection1OldHistorialEstadosPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain HistorialEstadosPedido " + historialEstadosPedidoCollection1OldHistorialEstadosPedido + " since its estadoOrigenId field is not nullable.");
                }
            }
            for (PedidosCliente pedidosClienteCollectionOldPedidosCliente : pedidosClienteCollectionOld) {
                if (!pedidosClienteCollectionNew.contains(pedidosClienteCollectionOldPedidosCliente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PedidosCliente " + pedidosClienteCollectionOldPedidosCliente + " since its estadoActualId field is not nullable.");
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionOldReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollectionOld) {
                if (!reglasTransicionEstadoPedidoCollectionNew.contains(reglasTransicionEstadoPedidoCollectionOldReglasTransicionEstadoPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ReglasTransicionEstadoPedido " + reglasTransicionEstadoPedidoCollectionOldReglasTransicionEstadoPedido + " since its estadoDestinoId field is not nullable.");
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollection1OldReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollection1Old) {
                if (!reglasTransicionEstadoPedidoCollection1New.contains(reglasTransicionEstadoPedidoCollection1OldReglasTransicionEstadoPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ReglasTransicionEstadoPedido " + reglasTransicionEstadoPedidoCollection1OldReglasTransicionEstadoPedido + " since its estadoOrigenId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollectionNew = new ArrayList<HistorialEstadosPedido>();
            for (HistorialEstadosPedido historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach : historialEstadosPedidoCollectionNew) {
                historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach.getHistorialId());
                attachedHistorialEstadosPedidoCollectionNew.add(historialEstadosPedidoCollectionNewHistorialEstadosPedidoToAttach);
            }
            historialEstadosPedidoCollectionNew = attachedHistorialEstadosPedidoCollectionNew;
            estadosPedido.setHistorialEstadosPedidoCollection(historialEstadosPedidoCollectionNew);
            Collection<HistorialEstadosPedido> attachedHistorialEstadosPedidoCollection1New = new ArrayList<HistorialEstadosPedido>();
            for (HistorialEstadosPedido historialEstadosPedidoCollection1NewHistorialEstadosPedidoToAttach : historialEstadosPedidoCollection1New) {
                historialEstadosPedidoCollection1NewHistorialEstadosPedidoToAttach = em.getReference(historialEstadosPedidoCollection1NewHistorialEstadosPedidoToAttach.getClass(), historialEstadosPedidoCollection1NewHistorialEstadosPedidoToAttach.getHistorialId());
                attachedHistorialEstadosPedidoCollection1New.add(historialEstadosPedidoCollection1NewHistorialEstadosPedidoToAttach);
            }
            historialEstadosPedidoCollection1New = attachedHistorialEstadosPedidoCollection1New;
            estadosPedido.setHistorialEstadosPedidoCollection1(historialEstadosPedidoCollection1New);
            Collection<PedidosCliente> attachedPedidosClienteCollectionNew = new ArrayList<PedidosCliente>();
            for (PedidosCliente pedidosClienteCollectionNewPedidosClienteToAttach : pedidosClienteCollectionNew) {
                pedidosClienteCollectionNewPedidosClienteToAttach = em.getReference(pedidosClienteCollectionNewPedidosClienteToAttach.getClass(), pedidosClienteCollectionNewPedidosClienteToAttach.getPedidoId());
                attachedPedidosClienteCollectionNew.add(pedidosClienteCollectionNewPedidosClienteToAttach);
            }
            pedidosClienteCollectionNew = attachedPedidosClienteCollectionNew;
            estadosPedido.setPedidosClienteCollection(pedidosClienteCollectionNew);
            Collection<ReglasTransicionEstadoPedido> attachedReglasTransicionEstadoPedidoCollectionNew = new ArrayList<ReglasTransicionEstadoPedido>();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach : reglasTransicionEstadoPedidoCollectionNew) {
                reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach = em.getReference(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach.getClass(), reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach.getReglaTransicionId());
                attachedReglasTransicionEstadoPedidoCollectionNew.add(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedidoToAttach);
            }
            reglasTransicionEstadoPedidoCollectionNew = attachedReglasTransicionEstadoPedidoCollectionNew;
            estadosPedido.setReglasTransicionEstadoPedidoCollection(reglasTransicionEstadoPedidoCollectionNew);
            Collection<ReglasTransicionEstadoPedido> attachedReglasTransicionEstadoPedidoCollection1New = new ArrayList<ReglasTransicionEstadoPedido>();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedidoToAttach : reglasTransicionEstadoPedidoCollection1New) {
                reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedidoToAttach = em.getReference(reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedidoToAttach.getClass(), reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedidoToAttach.getReglaTransicionId());
                attachedReglasTransicionEstadoPedidoCollection1New.add(reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedidoToAttach);
            }
            reglasTransicionEstadoPedidoCollection1New = attachedReglasTransicionEstadoPedidoCollection1New;
            estadosPedido.setReglasTransicionEstadoPedidoCollection1(reglasTransicionEstadoPedidoCollection1New);
            estadosPedido = em.merge(estadosPedido);
            for (HistorialEstadosPedido historialEstadosPedidoCollectionNewHistorialEstadosPedido : historialEstadosPedidoCollectionNew) {
                if (!historialEstadosPedidoCollectionOld.contains(historialEstadosPedidoCollectionNewHistorialEstadosPedido)) {
                    EstadosPedido oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido = historialEstadosPedidoCollectionNewHistorialEstadosPedido.getEstadoDestinoId();
                    historialEstadosPedidoCollectionNewHistorialEstadosPedido.setEstadoDestinoId(estadosPedido);
                    historialEstadosPedidoCollectionNewHistorialEstadosPedido = em.merge(historialEstadosPedidoCollectionNewHistorialEstadosPedido);
                    if (oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido != null && !oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido.equals(estadosPedido)) {
                        oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido.getHistorialEstadosPedidoCollection().remove(historialEstadosPedidoCollectionNewHistorialEstadosPedido);
                        oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido = em.merge(oldEstadoDestinoIdOfHistorialEstadosPedidoCollectionNewHistorialEstadosPedido);
                    }
                }
            }
            for (HistorialEstadosPedido historialEstadosPedidoCollection1NewHistorialEstadosPedido : historialEstadosPedidoCollection1New) {
                if (!historialEstadosPedidoCollection1Old.contains(historialEstadosPedidoCollection1NewHistorialEstadosPedido)) {
                    EstadosPedido oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1NewHistorialEstadosPedido = historialEstadosPedidoCollection1NewHistorialEstadosPedido.getEstadoOrigenId();
                    historialEstadosPedidoCollection1NewHistorialEstadosPedido.setEstadoOrigenId(estadosPedido);
                    historialEstadosPedidoCollection1NewHistorialEstadosPedido = em.merge(historialEstadosPedidoCollection1NewHistorialEstadosPedido);
                    if (oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1NewHistorialEstadosPedido != null && !oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1NewHistorialEstadosPedido.equals(estadosPedido)) {
                        oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1NewHistorialEstadosPedido.getHistorialEstadosPedidoCollection1().remove(historialEstadosPedidoCollection1NewHistorialEstadosPedido);
                        oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1NewHistorialEstadosPedido = em.merge(oldEstadoOrigenIdOfHistorialEstadosPedidoCollection1NewHistorialEstadosPedido);
                    }
                }
            }
            for (PedidosCliente pedidosClienteCollectionNewPedidosCliente : pedidosClienteCollectionNew) {
                if (!pedidosClienteCollectionOld.contains(pedidosClienteCollectionNewPedidosCliente)) {
                    EstadosPedido oldEstadoActualIdOfPedidosClienteCollectionNewPedidosCliente = pedidosClienteCollectionNewPedidosCliente.getEstadoActualId();
                    pedidosClienteCollectionNewPedidosCliente.setEstadoActualId(estadosPedido);
                    pedidosClienteCollectionNewPedidosCliente = em.merge(pedidosClienteCollectionNewPedidosCliente);
                    if (oldEstadoActualIdOfPedidosClienteCollectionNewPedidosCliente != null && !oldEstadoActualIdOfPedidosClienteCollectionNewPedidosCliente.equals(estadosPedido)) {
                        oldEstadoActualIdOfPedidosClienteCollectionNewPedidosCliente.getPedidosClienteCollection().remove(pedidosClienteCollectionNewPedidosCliente);
                        oldEstadoActualIdOfPedidosClienteCollectionNewPedidosCliente = em.merge(oldEstadoActualIdOfPedidosClienteCollectionNewPedidosCliente);
                    }
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollectionNew) {
                if (!reglasTransicionEstadoPedidoCollectionOld.contains(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido)) {
                    EstadosPedido oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido = reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.getEstadoDestinoId();
                    reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.setEstadoDestinoId(estadosPedido);
                    reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido);
                    if (oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido != null && !oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.equals(estadosPedido)) {
                        oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido.getReglasTransicionEstadoPedidoCollection().remove(reglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido);
                        oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido = em.merge(oldEstadoDestinoIdOfReglasTransicionEstadoPedidoCollectionNewReglasTransicionEstadoPedido);
                    }
                }
            }
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollection1New) {
                if (!reglasTransicionEstadoPedidoCollection1Old.contains(reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido)) {
                    EstadosPedido oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido = reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido.getEstadoOrigenId();
                    reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido.setEstadoOrigenId(estadosPedido);
                    reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido = em.merge(reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido);
                    if (oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido != null && !oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido.equals(estadosPedido)) {
                        oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido.getReglasTransicionEstadoPedidoCollection1().remove(reglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido);
                        oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido = em.merge(oldEstadoOrigenIdOfReglasTransicionEstadoPedidoCollection1NewReglasTransicionEstadoPedido);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = estadosPedido.getEstadoId();
                if (findEstadosPedido(id) == null) {
                    throw new NonexistentEntityException("The estadosPedido with id " + id + " no longer exists.");
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
            EstadosPedido estadosPedido;
            try {
                estadosPedido = em.getReference(EstadosPedido.class, id);
                estadosPedido.getEstadoId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estadosPedido with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollectionOrphanCheck = estadosPedido.getHistorialEstadosPedidoCollection();
            for (HistorialEstadosPedido historialEstadosPedidoCollectionOrphanCheckHistorialEstadosPedido : historialEstadosPedidoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EstadosPedido (" + estadosPedido + ") cannot be destroyed since the HistorialEstadosPedido " + historialEstadosPedidoCollectionOrphanCheckHistorialEstadosPedido + " in its historialEstadosPedidoCollection field has a non-nullable estadoDestinoId field.");
            }
            Collection<HistorialEstadosPedido> historialEstadosPedidoCollection1OrphanCheck = estadosPedido.getHistorialEstadosPedidoCollection1();
            for (HistorialEstadosPedido historialEstadosPedidoCollection1OrphanCheckHistorialEstadosPedido : historialEstadosPedidoCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EstadosPedido (" + estadosPedido + ") cannot be destroyed since the HistorialEstadosPedido " + historialEstadosPedidoCollection1OrphanCheckHistorialEstadosPedido + " in its historialEstadosPedidoCollection1 field has a non-nullable estadoOrigenId field.");
            }
            Collection<PedidosCliente> pedidosClienteCollectionOrphanCheck = estadosPedido.getPedidosClienteCollection();
            for (PedidosCliente pedidosClienteCollectionOrphanCheckPedidosCliente : pedidosClienteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EstadosPedido (" + estadosPedido + ") cannot be destroyed since the PedidosCliente " + pedidosClienteCollectionOrphanCheckPedidosCliente + " in its pedidosClienteCollection field has a non-nullable estadoActualId field.");
            }
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollectionOrphanCheck = estadosPedido.getReglasTransicionEstadoPedidoCollection();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollectionOrphanCheckReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EstadosPedido (" + estadosPedido + ") cannot be destroyed since the ReglasTransicionEstadoPedido " + reglasTransicionEstadoPedidoCollectionOrphanCheckReglasTransicionEstadoPedido + " in its reglasTransicionEstadoPedidoCollection field has a non-nullable estadoDestinoId field.");
            }
            Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection1OrphanCheck = estadosPedido.getReglasTransicionEstadoPedidoCollection1();
            for (ReglasTransicionEstadoPedido reglasTransicionEstadoPedidoCollection1OrphanCheckReglasTransicionEstadoPedido : reglasTransicionEstadoPedidoCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EstadosPedido (" + estadosPedido + ") cannot be destroyed since the ReglasTransicionEstadoPedido " + reglasTransicionEstadoPedidoCollection1OrphanCheckReglasTransicionEstadoPedido + " in its reglasTransicionEstadoPedidoCollection1 field has a non-nullable estadoOrigenId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estadosPedido);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<EstadosPedido> findEstadosPedidoEntities() {
        return findEstadosPedidoEntities(true, -1, -1);
    }

    public List<EstadosPedido> findEstadosPedidoEntities(int maxResults, int firstResult) {
        return findEstadosPedidoEntities(false, maxResults, firstResult);
    }

    private List<EstadosPedido> findEstadosPedidoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(EstadosPedido.class));
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

    public EstadosPedido findEstadosPedido(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(EstadosPedido.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadosPedidoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<EstadosPedido> rt = cq.from(EstadosPedido.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
