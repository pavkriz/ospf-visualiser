package org.hkfree.ospf.tools;

/**
 * Třída sloužící k výpočtu intenzity spoje na základě jeho počtu výpadků
 * @author Jakub Menzel
 */
public class FaultIntensityCalculator {

    private float minimalIntensityValue = 25;
    private int maximalFaults = 0;
    private int minimalFaults = 0;
    private double power = 0.2;


    /**
     * Konstruktor
     */
    public FaultIntensityCalculator() {}


    /**
     * Vrací maximální počet výpadků
     * @return count
     */
    public int getMaximalFaults() {
	return maximalFaults;
    }


    /**
     * Nastaví maximální počet výpadků
     * @param maximalFaults
     */
    public void setMaximalFaults(int maximalFaults) {
	this.maximalFaults = (int) Math.pow(maximalFaults, power);
    }


    /**
     * Vrací minimální počet výpadků
     * @return count
     */
    public int getMinimalFaults() {
	return minimalFaults;
    }


    /**
     * Nastaví minimální počet výpadků
     * @param minimalFaults
     */
    public void setMinimalFaults(int minimalFaults) {
	this.minimalFaults = (int) Math.pow(minimalFaults, power);
    }


    /**
     * Vrací intenzitu výpdaků spoje s danným počtem výpadků
     * @param faultsCount
     * @return intensity
     */
    public float getMyIntensity(int faultsCount) {
	faultsCount = (int) Math.pow(faultsCount, power);
	float f = (float) (faultsCount / (maximalFaults - minimalFaults));
	return f * (255 - minimalIntensityValue) + minimalIntensityValue;
    }


    /**
     * Vrací minimální možnou intenzitu
     * @return value
     */
    public float getMinimalIntensityValue() {
	return minimalIntensityValue;
    }


    /**
     * Nastavuje minimální možnou intenzitu
     * @param minimalIntensityValue
     */
    public void setMinimalIntensityValue(float minimalIntensityValue) {
	this.minimalIntensityValue = minimalIntensityValue;
    }
}
