package org.hkfree.ospf.model;

import java.util.List;

import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.tools.NeighbourCostAndLink;

/**
 * Rozhraní modelu grafu (mapy)
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public interface IMapModel {

    /**
     * Vraci vsechny vrcholy map modelu
     * @return
     */
    public List<IVertex> getVertices();


    /**
     * Vraci vsechny hrany map modelu
     * @return
     */
    public List<IEdge> getEdges();


    /**
     * Vrací seznam sousedních routerů s cenami daného routeru
     * @param routerVertex
     * @return NeighbourCostAndLink
     */
    public List<NeighbourCostAndLink> getNeighboursWithCosts(RouterVertex r);


    /**
     * Vrací hranu multispoje mezi danými vrcholy
     * @param router
     * @param multilinkvertex
     * @return le
     */
    public LinkEdge getMultilinkEdge(RouterVertex router, RouterVertex multilinkVertex);
}
