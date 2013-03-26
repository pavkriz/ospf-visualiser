package org.hkfree.ospf.model.map;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * Rozhraní pro vrcholu grafu (router nebo zařízení z LLTD modelu)
 * @author Jan Schovánek
 */
public interface IVertex {

    public boolean isEnabled();


    public void setEnabled(boolean enabled);


    public boolean isVisible();


    public void setVisible(boolean visible);


    /**
     * Vrací název vrcholu
     * @return
     */
    public String getLabel();


    /**
     * Vrací popisek vrcholu
     * @return
     */
    public String getDescription();


    /**
     * Vrací barvu výplně vrcholu
     * @return
     */
    public Color getColorFill();


    /**
     * Vrací barvu ohraničení vrcholu
     * @return
     */
    public Color getColorStroke();


    /**
     * Vrací štětec (jeho sílu, může být čárkovaný)
     * @return štětec
     */
    public Stroke getStroker();


    /**
     * Vrací tvar vrcholu
     * @return
     */
    public Shape getShaper();
}
