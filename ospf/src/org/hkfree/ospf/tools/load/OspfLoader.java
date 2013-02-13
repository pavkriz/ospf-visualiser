package org.hkfree.ospf.tools.load;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hkfree.ospf.model.linkfault.LinkFaultModel;
import org.hkfree.ospf.model.ospf.ExternalLSA;
import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospf.StubLink;
import org.hkfree.ospf.tools.geo.GeoCoordinatesTransformator;
import org.hkfree.ospf.tools.ip.IpCalculator;

/**
 * Třída, která slouží k načítání OspfModelu z externích souborů.
 * Načítání může probíhat dle nastavení z místního adresáře, nebo z webu.
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfLoader {

    private static String patternIP = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
    private static String patternMask = "^.*/([0-9]{1,2})";
    private static String patternCost = "^.*:\\s*([0-9]{1,})";
    private static String patternName = "^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})\\s+(.+)$";
    private static String patternGeo = "^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})\\s+([0-9]+)\\s+([0-9]+)(.*)$";
    private static String patternLog = "^([0-9]{4}/[0-9]{2}/[0-9]{2}\\s+[0-9]{2}:[0-9]{2}:[0-9]{2})\\s+.+id\\((.+)\\).+ar.+$";


    /**
     * Metoda, která načte ze zadaného umístění topologii sítě routerů
     * @throws IOException
     */
    public static void loadTopology(OspfModel model, BufferedReader input) throws IOException {
	BufferedReader vstup = null;
	String radek = "";
	Pattern ipPattern = Pattern.compile(patternIP);
	Matcher ipMatcher = null;
	Pattern maskPattern = Pattern.compile(patternMask);
	Matcher maskMatcher = null;
	vstup = input;
	while ((radek = vstup.readLine()) != null) {
	    if (radek.contains("Link State ID")) {
		String linkName = "";
		int linkMask = 0;
		ipMatcher = ipPattern.matcher(radek);
		ipMatcher.find();
		linkName = ipMatcher.group(0);
		while (!((radek = vstup.readLine()).contains("Network Mask"))) {}
		maskMatcher = maskPattern.matcher(radek);
		maskMatcher.find();
		linkMask = Integer.valueOf(maskMatcher.group(1));
		Link l = new Link();
		l.setLinkIDv4(linkName);
		l.setSubnetMask(linkMask);
		model.getLinks().add(l);
		// čtení řádků než narazí na Attached Router
		while (!((radek = vstup.readLine()).contains("Attached Router"))) {}
		// načtení první IP jdoucí do spoje
		ipMatcher = ipPattern.matcher(radek);
		ipMatcher.find();
		model.addRouter(ipMatcher.group(0));
		// načtení zbylých IP jdoucích do spoje
		while ((radek = vstup.readLine()).contains("Attached Router")) {
		    ipMatcher = ipPattern.matcher(radek);
		    ipMatcher.find();
		    model.addRouter(ipMatcher.group(0));
		}
	    }
	}
    }


    /**
     * Metoda, která načte ze zadaného umístění ceny spojů načtené topologie
     * @throws IOException
     */
    public static void loadCosts(OspfModel model, String routerIP, BufferedReader input) throws IOException {
	BufferedReader infoUzlu = null;
	Router router = null;
	String radek;
	// Pattern costPattern = Pattern.compile("^.*:\\s([0-9]{1,})");
	Pattern costPattern = Pattern.compile(patternCost);
	Matcher costMatcher = null;
	Pattern ipPattern = Pattern.compile(patternIP);
	// Pattern ipPattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
	Matcher ipMatcher = null;
	int cena;
	List<Link> act_spoje = new ArrayList<Link>();
	router = model.getRouterByIp(routerIP);
	if (router != null) {
	    for (Link s : model.getLinks()) {
		if (s.containsRouter(router))
		    act_spoje.add(s);
	    }
	    infoUzlu = input;
	    // změnit cenu a ip interface routeru ve spojích kde router figuruje
	    while ((radek = infoUzlu.readLine()) != null) {
		for (Link s : act_spoje) {
		    if (radek.contains("Link ID") && radek.endsWith(s.getLinkIDv4())) {
			String interfaceIp = "";
			while (!(radek = infoUzlu.readLine()).contains("Interface")) {}
			ipMatcher = ipPattern.matcher(radek);
			ipMatcher.find();
			interfaceIp = ipMatcher.group(0);
			while (!(radek = infoUzlu.readLine()).contains("TOS 0 Metric")) {}
			costMatcher = costPattern.matcher(radek);
			costMatcher.find();
			cena = Integer.valueOf(costMatcher.group(1));
			model.updateCost(s.getLinkIDv4(), router, interfaceIp, cena);
		    } else if (radek.contains("Stub Network")) {
			// nacitani stub spoje
			StubLink stub = new StubLink();
			while (!(radek = infoUzlu.readLine()).contains("(Link ID) Net")) {}
			ipMatcher = ipPattern.matcher(radek);
			ipMatcher.find();
			stub.setLinkID(ipMatcher.group(0));
			while (!(radek = infoUzlu.readLine()).contains("(Link Data) Network Mask")) {}
			ipMatcher = ipPattern.matcher(radek);
			ipMatcher.find();
			stub.setMask(IpCalculator.getMask(ipMatcher.group(0)));
			while (!(radek = infoUzlu.readLine()).contains("TOS 0 Metric")) {}
			costMatcher = costPattern.matcher(radek);
			costMatcher.find();
			stub.setCost(Integer.valueOf(costMatcher.group(1)));
			router.getStubs().add(stub);
		    }
		}
	    }
	    for (int i = act_spoje.size() - 1; i >= 0; i--) {
		act_spoje.remove(i);
	    }
	}
    }


    /**
     * Metoda, která načte ze zadaného umístění jména routerů a náležitě upraví model
     * @throws IOException
     */
    public static void loadRouterNames(OspfModel model, BufferedReader input) throws IOException {
	BufferedReader vstup = null;
	String radek = "", ip = "", name = "";
	Pattern namePattern = Pattern.compile(patternName);
	Matcher nameMatcher = null;
	vstup = input;
	while ((radek = vstup.readLine()) != null) {
	    nameMatcher = namePattern.matcher(radek);
	    nameMatcher.find();
	    if (nameMatcher.matches()) {
		ip = nameMatcher.group(1);
		name = nameMatcher.group(2);
		for (Router r : model.getRouters()) {
		    if (r.getId().equals(ip) && !ip.equals(name))
			r.setName(name);
		}
	    }
	}
    }


    /**
     * Metoda, která načte ze zadaného umístění logy o výpadcích
     * @param model
     * @param input
     * @throws ParseException
     * @throws IOException
     */
    public static void loadOSPFLog(LinkFaultModel model, BufferedReader input) throws IOException, ParseException {
	BufferedReader vstup = null;
	SimpleDateFormat inputDateFormater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Pattern logPattern = Pattern.compile(patternLog);
	Matcher logMatcher = null;
	vstup = input;
	String line = "";
	while ((line = vstup.readLine()) != null) {
	    logMatcher = logPattern.matcher(line);
	    logMatcher.find();
	    if (logMatcher.matches()) {
		model.addLinkFault(inputDateFormater.parse(logMatcher.group(1)), logMatcher.group(2));
	    }
	}
    }


    /**
     * Metoda, která načte ze zadaného umístění pozice routerů
     * @param model
     * @param input
     * @throws IOException
     * @throws NumberFormatException
     * @throws Exception
     */
    public static void loadRouterGeoPositions(OspfModel model, BufferedReader input) throws NumberFormatException,
	    IOException {
	BufferedReader vstup = null;
	Pattern geoPattern = Pattern.compile(patternGeo);
	Matcher geoMatcher = null;
	vstup = input;
	GeoCoordinatesTransformator geoCoorTransormator = new GeoCoordinatesTransformator();
	String line = "";
	while ((line = vstup.readLine()) != null) {
	    geoMatcher = geoPattern.matcher(line);
	    geoMatcher.find();
	    if (geoMatcher.matches()) {
		model.setGpsLoaded(true);
		Router r = model.getRouterByIp(geoMatcher.group(1));
		if (r != null) {
		    r.setGpsPosition(geoCoorTransormator.transformJTSKToWGS(Integer.valueOf(geoMatcher.group(2)),
			    Integer.valueOf(geoMatcher.group(3))));
		}
	    }
	}
    }


    public static void getTopologyFromData(OspfModel model, BufferedReader input) throws NumberFormatException,
	    IOException {
	try {
	    // zapis dat do souboru
	    // BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
	    // String s;
	    // BufferedReader input2 = new BufferedReader(input);
	    // while ((s = input2.readLine()) != null) {
	    // out.write(s + "\n");
	    // }
	    // out.close();
	    OspfModel modelIPv6 = new OspfModel();
	    String routerId = null;
	    String routerName = null;
	    String linkStateId = null;
	    String linkId = null;
	    String linkData = null;
	    String linkName = null;
	    String router = null;
	    String neighborInterface = null;
	    String neighborRouter = null;
	    String advRouter = null;
	    int metricType = -1;
	    int cost = -1;
	    int mask = -1;
	    int numberOfLinks;
	    String radek = null;
	    Pattern ipPattern = Pattern.compile(patternIP);
	    Pattern maskPattern = Pattern.compile(patternMask);
	    Pattern costPattern = Pattern.compile(patternCost);
	    Pattern namePattern = Pattern.compile(patternName);
	    Pattern geoPattern = Pattern.compile(patternGeo);
	    GeoCoordinatesTransformator geoCoorTransormator = new GeoCoordinatesTransformator();
	    Matcher matcher = null;
	    // prikazy
	    String cmd1 = "Net Link States"; // "show ip ospf database network"
	    String cmd2 = "Router Link States"; // "show ip ospf database router"
	    String cmd3 = "AS External Link States"; // "show ip ospf database external"
	    // String cmd4 = "show ipv6 ospf6 database network detail";
	    // String cmd5 = "show ipv6 ospf6 database router detail";
	    String cmd6 = "AS Scoped Link State Database"; // "show ipv6 ospf6 database as-external detail"
	    String cmd7 = "Area Scoped Link State Database"; // pozdeji bude urceno a jaka data jde
	    String cmd8 = "router names"; // nacitani nazvu routeru
	    String cmd9 = "geo positions"; // nacitani geo souradnic routeru
	    boolean isStub = false;
	    int cmd = 0;
	    while ((radek = input.readLine()) != null) {
		if (radek.contains(cmd1)) {
		    cmd = 1;
		    continue;
		}
		if (radek.contains(cmd2)) {
		    cmd = 2;
		    continue;
		}
		if (radek.contains(cmd3)) {
		    cmd = 3;
		    continue;
		}
		if (radek.contains(cmd6)) {
		    cmd = 6;
		    continue;
		}
		if (radek.contains(cmd7)) {
		    cmd = 7;
		    continue;
		}
		if (radek.contains(cmd8)) {
		    cmd = 8;
		    continue;
		}
		if (radek.contains(cmd9)) {
		    cmd = 9;
		    continue;
		}
		switch (cmd) {
		    case 1:
			// nacitani topologie pro IPv4
			if (radek.contains("Link State ID")) {
			    int linkMask = 0;
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    linkName = matcher.group(0);
			    while (!((radek = input.readLine()).contains("Network Mask")))
				;
			    matcher = maskPattern.matcher(radek);
			    matcher.find();
			    linkMask = Integer.valueOf(matcher.group(1));
			    Link l = new Link();
			    l.setLinkIDv4(linkName);
			    l.setSubnetMask(linkMask);
			    model.getLinks().add(l);
			    // čtení řádků než narazí na Attached Router
			    while (!((radek = input.readLine()).contains("Attached Router")))
				;
			    // načtení první IP jdoucí do spoje
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    model.addRouter(matcher.group(0));
			    // načtení zbylých IP jdoucích do spoje
			    while ((radek = input.readLine()).contains("Attached Router")) {
				matcher = ipPattern.matcher(radek);
				matcher.find();
				model.addRouter(matcher.group(0));
			    }
			}
			break;
		    case 2:
			// nacitani dodatecnych dat pro IPv4
			if (radek.contains("Link State ID")) {
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    linkStateId = matcher.group(0);
			    while (!((radek = input.readLine()).contains("Number of Links")))
				;
			    matcher = costPattern.matcher(radek);
			    matcher.find();
			    numberOfLinks = Integer.valueOf(matcher.group(1));
			    for (int i = 0; i < numberOfLinks; i++) {
				while (!(radek = input.readLine()).contains("Link connected to"))
				    ;
				isStub = radek.endsWith("Stub Network");
				while (!(radek = input.readLine()).contains("(Link ID)"))
				    ;
				matcher = ipPattern.matcher(radek);
				matcher.find();
				linkId = matcher.group(0);
				while (!(radek = input.readLine()).contains("(Link Data)"))
				    ;
				matcher = ipPattern.matcher(radek);
				matcher.find();
				linkData = matcher.group(0);
				while (!(radek = input.readLine()).contains("TOS 0 Metric"))
				    ;
				matcher = costPattern.matcher(radek);
				matcher.find();
				cost = Integer.valueOf(matcher.group(1));
				if (isStub) {
				    model.addStubNetwork(linkStateId, linkId, IpCalculator.getMask(linkData), cost);
				} else {
				    model.updateCost(linkId, linkStateId, linkData, cost);
				}
			    }
			}
			break;
		    case 3:
			// nacitani externich LSA a jejich masek
			if (radek.contains("Link State ID")) {
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    linkName = matcher.group(0);
			    while (!(radek = input.readLine()).contains("Advertising Router"))
				;
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    advRouter = matcher.group(0);
			    while (!(radek = input.readLine()).contains("Network Mask"))
				;
			    matcher = maskPattern.matcher(radek);
			    matcher.find();
			    mask = Integer.valueOf(matcher.group(1));
			    while (!(radek = input.readLine()).contains("Metric Type"))
				;
			    matcher = costPattern.matcher(radek);
			    matcher.find();
			    metricType = Integer.valueOf(matcher.group(1));
			    while (!(radek = input.readLine()).contains("Metric"))
				;
			    matcher = costPattern.matcher(radek);
			    matcher.find();
			    cost = Integer.valueOf(matcher.group(1));
			    ExternalLSA exLsa = new ExternalLSA();
			    exLsa.setMask(mask);
			    exLsa.setCost(cost);
			    exLsa.setMetricType(metricType);
			    exLsa.setNetwork(linkName);
			    // TODO zjistit proc se router nenajde
			    if (model.getRouterByIp(advRouter) != null) {
				model.getRouterByIp(advRouter).getExternalLsa().add(exLsa);
			    }
			}
			break;
		    case 4:
			// nacitani topologie pro IPv6
			if (radek.contains("Link State ID")) {
			    int linkMask = 0;
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    linkName = matcher.group(0);
			    Link l = new Link();
			    l.setLinkIDv6(linkName);
			    l.setSubnetMask(linkMask);
			    modelIPv6.getLinks().add(l);
			    while (!((radek = input.readLine()).contains("Attached Router")))
				;
			    // načtení první IP jdoucí do spoje
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    modelIPv6.addRouter(matcher.group(0));
			    // načtení zbylých IP jdoucích do spoje
			    while ((radek = input.readLine()).contains("Attached Router")) {
				matcher = ipPattern.matcher(radek);
				matcher.find();
				modelIPv6.addRouter(matcher.group(0));
			    }
			}
			break;
		    case 5:
			// nacitani dodatecnych dat pro IPv6
			if (radek.contains("Advertising Router")) {
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    router = matcher.group(0);
			} else if (radek.contains("Transit-Network Metric")) {
			    matcher = costPattern.matcher(radek);
			    matcher.find();
			    cost = Integer.valueOf(matcher.group(1));
			} else if (radek.contains("Neighbor Interface ID")) {
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    neighborInterface = matcher.group(0);
			} else if (radek.contains("Neighbor Router ID")) {
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    neighborRouter = matcher.group(0);
			    // linkId, router, 2.router, cena
			    modelIPv6.updateCostIPv6(neighborInterface, router, neighborRouter, cost);
			}
			break;
		    case 6:
			// nacitani externich lsa a masky pro IPv6
			if (radek.contains("Advertising Router")) {
			    matcher = ipPattern.matcher(radek);
			    matcher.find();
			    advRouter = matcher.group(0);
			} else if (radek.contains("Metric")) {
			    matcher = costPattern.matcher(radek);
			    matcher.find();
			    cost = Integer.valueOf(matcher.group(1));
			} else if (radek.contains("Prefix") && !radek.contains("Options")) {
			    linkName = radek.substring(radek.indexOf(':') + 2, radek.indexOf('/'));
			    mask = Integer.valueOf(radek.substring(radek.indexOf('/') + 1));
			    ExternalLSA exLsa = new ExternalLSA();
			    exLsa.setMask(mask);
			    exLsa.setCost(cost);
			    exLsa.setNetwork(linkName);
			    if (model.getRouterByIp(advRouter) != null) {
				model.getRouterByIp(advRouter).getExternalLsa().add(exLsa);
			    } else if (modelIPv6.getRouterByIp(advRouter) != null) {
				modelIPv6.getRouterByIp(advRouter).getExternalLsa().add(exLsa);
			    } else {
				System.err.println("OspfLoader - Router nenalezen");
			    }
			}
			break;
		    case 7:
			if (radek.contains("Type: Network")) {
			    cmd = 4;
			} else if (radek.contains("Type: Router")) {
			    cmd = 5;
			}
			break;
		    case 8:
			// nacitani nazvu routeru
			matcher = namePattern.matcher(radek);
			matcher.find();
			if (matcher.matches()) {
			    routerId = matcher.group(1);
			    routerName = matcher.group(2);
			    for (Router r : model.getRouters()) {
				if (r.getId().equals(routerId) && !routerId.equals(routerName))
				    r.setName(routerName);
			    }
			}
			break;
		    case 9:
			// nacitani geo souradnic
			matcher = geoPattern.matcher(radek);
			matcher.find();
			if (matcher.matches()) {
			    model.setGpsLoaded(true);
			    Router r = model.getRouterByIp(matcher.group(1));
			    if (r != null) {
				r.setGpsPosition(geoCoorTransormator.transformJTSKToWGS(Integer.valueOf(matcher.group(2)),
				        Integer.valueOf(matcher.group(3))));
			    }
			}
			break;
		}
	    }
	    // zacleneni IPv6 do puvodniho modelu kde je do teto doby pouze IPv4
	    // priznak zda jsou nacteny nejaky IPv6
	    model.setIpv6Loaded(!modelIPv6.getLinks().isEmpty());
	    // prochazeni spoju
	    boolean b = false;
	    for (Link l6 : modelIPv6.getLinks()) {
		b = false;
		for (Link l4 : model.getLinks()) {
		    if (l6.hasSameRouters(l4.getOspfLinkData())) {
			b = true;
			l4.setLinkIDv6(l6.getLinkIDv6());
			for (OspfLinkData old6 : l6.getOspfLinkData()) {
			    OspfLinkData old4 = l4.getOspfLinkData(old6.getRouter().getId());
			    old4.setCostIPv6(old6.getCostIPv6());
			}
			continue;
		    }
		}
		if (!b) {
		    Link l = new Link();
		    l.setLinkIDv6(l6.getLinkIDv6());
		    for (OspfLinkData old : l6.getOspfLinkData()) {
			Router r = model.getRouterByIp(old.getRouter().getId());
			if (r == null) {
			    r = new Router(old.getRouter().getId());
			    model.getRouters().add(r);
			}
			OspfLinkData o = new OspfLinkData();
			o.setCostIPv4(old.getCostIPv4());
			o.setCostIPv6(old.getCostIPv6());
			o.setRouter(model.getRouterByIp(old.getRouter().getId()));
			o.setInterfaceIp(old.getInterfaceIp());
			l.getOspfLinkData().add(o);
		    }
		    model.getLinks().add(l);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}