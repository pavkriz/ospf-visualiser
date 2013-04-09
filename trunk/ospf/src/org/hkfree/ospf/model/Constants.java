package org.hkfree.ospf.model;

import java.awt.Dimension;

/**
 * Konstanty a enumy pro aplikaci OspfVisualiser
 * @author Jan Schovánek
 */
public abstract class Constants {

    /** verze aplikace */
    public static final String APP_VERSION = "3.0.4";
    /** url k obrázkům */
    public static final String URL_IMG_GUI = "/images/";
    /** url k webovym strankam s tipy aplikace */
    public static final String URL_TIPS = "tips/";
    /** název souboru s nastavením */
    public static final String SETTINGS_FILE = "settings.properties";
    /** komentář do properties souboru s nastavením aplikace */
    public static final String SETTINGS_COMMENT = "OSPF VISUALISER SETTINGS";
    /** multilink, multispoj */
    public static final String MULTILINK = "MULTILINK";
    /** symetricky spoj */
    public static final String SYMETRIC = "SYMETRIC";
    /** název souboru v zip archivu určující název souboru s daty pro načtení OSPF modelu */
    public static final String FILENAME_OSPF_DUMP = "ospfdump";
    /** název souboru v zip archivu určující název souboru se jmény routerů */
    public static final String FILENAME_ROUTER_NAMES = "router_names";
    /** název souboru v zip archivu určující název souboru informacemi o síti */
    public static final String FILENAME_TOPOLOGY = "ospf_database_network";
    /** název souboru v zip archivu určující název souboru s geo souřadnicemi */
    public static final String FILENAME_GEO_POSITIONS = "geo";
    /** adresa serveru odkud se stahuji xml retezce s daty z LLTD aplikace */
    public static final String URL_LLTD_DATA = "http://ospf-visualiser.wz.cz/";
    /** FR LAYOUT - maximalni pocet iteraci pro FR layout */
    public static int LAYOUT_FR_MAX_ITERATIONS = 1500;
    /** FR LAYOUT - vzdalenosti vrcholu od sebe */
    public static double LAYOUT_ATTRACTION = 0.55; // vzdalenosti vrcholu od sebe  60  45  55
    /** FR LAYOUT - vzdalenosti vrcholu na spoji od sebe */
    public static double LAYOUT_REPULSION = 0.18; // vzdalenosti vrcholu na spoji od sobe   28  15  18
    /** SPRING LAYOUT - */
    public static double LAYOUT_STRETCH = 0.7;
    /** SPRING LAYOUT - */
    public static int LAYOUT_REPULSION_RANGE = 120;
    /** SPRING LAYOUT - */
    public static double LAYOUT_FORCE_MULTIPLIER = 0.85;
    /** velikost layoutu 2000x2000 */
    public static Dimension LAYOUT_SIZE = new Dimension(2000, 2000);
    // konstanty pro vyber zdroje pro nacteni dat
    public static final int FROM_DATE_TO_DATE = 0;
    public static final int ZIP_SERVER = 1;
    public static final int ZIP_LOCAL = 2;
    public static final int LOCAL_SOURCES = 3;
    public static final int TELNET = 4;
    public static final int CGI = 5;
    public static final int LOCAL = 1;
    public static final int REMOTE_SERVER = 2;
    public static final int FOLDER = 1;
    public static final int ZIP = 2;

    /**
     * Výčet lokalizace aplikace
     */
    public enum LANGUAGE {
	en_EN,
	cs_CZ;
    }

    /**
     * Výčet nastavení pracovního režimu
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
	ADD_VERTEXES,
	ADD_EDGES,
	ASYMETRIC_LINKS,
	SHORTEST_PATH_TWO_ROUTERS,
	IPV6,
	NONE,
    }

    public enum LAYOUT {
	LAYOUT_FR,
	LAYOUT_FR_LLTD,
	LAYOUT_SPRING_START,
	LAYOUT_SPRING_STOP,
	LAYOUT_JS_START,
    }

    /**
     * Výčet tvarů spoje
     */
    public enum EDGE_SHAPER {
	QUAD_CURVE,
	LINE,
	BENT_LINE,
	CUBIC_CURVE
    }
}
