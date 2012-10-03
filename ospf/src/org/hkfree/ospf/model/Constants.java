package org.hkfree.ospf.model;

/**
 * Konstanty a enumy pro aplikaci OspfVisualiser
 * @author Jan Schovánek
 */
public abstract class Constants {

    /** url k obrázkům */
    public static final String URL_IMG_GUI = "/images/";
    /** url k webovym strankam s tipy aplikace */
    public static final String URL_TIPS = "tips/";
    /** název souboru s nastavením */
    public static final String SETTINGS_FILE = "settings.properties";
    /** komentář do properties souboru s nastavením aplikace */
    public static final String SETTINGS_COMMENT = "OSPF VISUALISER SETTINGS";
    /** verze aplikace */
    public static final String APP_VERSION = "3.0.3";
    // konstanty pro vyber zdroje pro nacteni dat
    public static final int FROM_DATE_TO_DATE = 0;
    public static final int ZIP_SERVER = 1;
    public static final int ZIP_LOCAL = 2;
    public static final int LOCAL_SOURCES = 3;
    public static final int TELNET = 4;
    public static final int LOCAL = 1;
    public static final int REMOTE_SERVER = 2;
    public static final int FOLDER = 1;
    public static final int ZIP = 2;

    /**
     * Enum pro lokalizaci aplikace
     */
    public enum LANGUAGE {
	en_EN, cs_CZ;
    }

    /**
     * Layout pouzity pro rozmistovani grafu
     */
    public enum LAYOUT {
	FR, SPRING
    }

    /**
     * Enum pro nastavení pracovního režimu
     * Ord odpovídá hodnotě v lng 'mdwal.ORD'
     */
    public enum MODE {
	SHOW_NEIGHBORS,
	COST_CHANGING,
	SHORTEST_PATH,
	GPS,
	GPS_ALL,
	ZOOM,
	LOCK_ALL,
	LOCK_VERTEX,
	PICKING,
	TRANSFORMING,
	LAYOUTING_START,
	LAYOUTING_STOP,
	ADD_VERTEXES,
	ADD_EDGES,
	ASYMETRIC_LINKS,
	SHORTEST_PATH_TWO_ROUTERS,
	NONE
    }
}
