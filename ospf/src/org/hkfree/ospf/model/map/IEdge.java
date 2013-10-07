package org.hkfree.ospf.model.map;

import java.awt.Paint;
import java.awt.Stroke;

public interface IEdge {

    public boolean isEnabled();


    public void setEnabled(boolean enabled);


    public boolean isHover();


    public void setHover(boolean hover);


    /**
     * Vrací první vrchol hrany
     * @return
     */
    public IVertex getVertex1();


    /**
     * Vrací druhý vrchol hrany
     * @return
     */
    public IVertex getVertex2();


    /**
     * Vrací popisek hrany
     * @return
     */
    public String getLabel();


    /**
     * Vrací popisek zobrazený v tooltipu
     * @return
     */
    public String getDescription();


    /**
     * Vrací barvu hrany
     * @return
     */
    public Paint getLineColor();


    /**
     * Vrací sílu štětce - jeho tloušťku
     * @return
     */
    public Stroke getStroker();


    /**
     * Vrací váhu spoje (používá se u SpringLayout pro preferovanou vzdálenost od mezi uzly)
     * @return
     */
	public int getWeight();
}
