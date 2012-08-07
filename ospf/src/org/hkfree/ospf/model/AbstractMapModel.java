package org.hkfree.ospf.model;

import java.util.List;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.tools.NeighbourCostAndLink;

/**
 * Rozhraní modelu grafu (mapy)
 * @author Jakub Menzel
 */
public interface AbstractMapModel {

    public List<RouterVertex> getRouterVertices();


    public List<LinkEdge> getLinkEdges();


    public List<NeighbourCostAndLink> getNeighboursWithCosts(RouterVertex r);


    public LinkEdge getMultilinkEdge(RouterVertex router, RouterVertex multilinkVertex);
}
