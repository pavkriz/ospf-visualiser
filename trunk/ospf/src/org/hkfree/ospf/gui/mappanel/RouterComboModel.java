package org.hkfree.ospf.gui.mappanel;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import org.hkfree.ospf.model.ospf.Router;

/**
 * Třída představující ComboboxModel obsahující routery
 * @author Jakub Menzel
 */
public class RouterComboModel implements ComboBoxModel {

    //TODO tahle trida neni nikde pouzita
    private List<Router> routerList = null;
    private String selected = null;


    /**
     * Konstruktor
     * @param routerList
     */
    public RouterComboModel(List<Router> routerList) {
	this.routerList = routerList;
	selected = (String) this.getElementAt(0);
    }


    /**
     * Vrací vybraný objekt
     * @return Object
     */
    public Object getSelectedItem() {
	return selected;
    }


    /**
     * Nastaví vybranou položku
     */
    public void setSelectedItem(Object anItem) {
	selected = (String) anItem;
    }


    public void addListDataListener(ListDataListener arg0) {}


    /**
     * Vrací objekt na dané pozici
     */
    public Object getElementAt(int i) {
	if (routerList.get(i).getRouterName() != null && !routerList.get(i).getRouterName().isEmpty())
	    return routerList.get(i).getRouterID() + "   -   " + routerList.get(i).getRouterName();
	else
	    return routerList.get(i).getRouterID();
    }


    /**
     * Vrací počet routerů
     * @return int
     */
    public int getSize() {
	return routerList.size();
    }


    public void removeListDataListener(ListDataListener arg0) {}


    /**
     * Vrací vybraný router
     * @return Router
     */
    public Router getSelectedRouter() {
	for (Router r : routerList) {
	    if (selected.equals(r.getRouterID()) || selected.equals(r.getRouterID() + "   -   " + r.getRouterName())) {
		return r;
	    }
	}
	return null;
    }


    /**
     * Nastavuje vybraný router
     * @param r
     */
    public void setSelectedRouter(Router r) {
	for (int i = 0; i < routerList.size(); i++) {
	    if (routerList.get(i).equals(r)) {
		selected = (String) getElementAt(i);
		break;
	    }
	}
    }
}
