# Instalace a spuštění #
Spuštění vyžaduje Javu JRE >= 1.6.
Postup:
  * stáhnout soubor ospfVERZE.jar a spustit jej
  * spuštění z příkazové řadky: ```
java -jar ospfVERZE.jar```
  * připomínky je možné psát v záložce Issues

# Verze #

## ospf3.0.5 (30.5.2013) ##
  * klávesová zkratka CTRL+L pro načítání modelů
  * přidání ikony JS layout na toolbar
  * odstraněny BUGy
    * rozmísťování po zobrazení mapy od centrálního routeru
    * při změně typu rozvrhování zastavení dřívějšího
    * NULL pointer při prvním spuštění nové aplikace

## ospf3.0.4 (16.4.2013) ##
  * SVG export připůsoben tak, aby jej bylo možné měnit přes CSS
  * úpravy popisků v mapě, tooltipů
  * přidána možnost zobrazit posledně přidaný model ihned po jeho načtení
  * načítání dle časového rozmezí
    * oprava mazání, nyní maže všechny označené záznamy
  * načítání CGI
    * zrušena adresa rDNS serveru
    * načítání názvů a geo souřadnic přímo z CGI vstupu
  * LLTD
    * aplikace umožňuje zobrazit data získaná v koncové síti pomocí LLTD protokolu, tyto data dokáže přiřadit k routeru, který podsíť propaguje
    * v mapě lze po kliknutí na routeru zobrazit podsítě, které propaguje


## ospf3.0.3 (5.1.2013) ##
<font color='red'><b>AKTUALIZACE:</b> Pokud při prvním spuštění nastane chyba, pomůže smazání složky ".ospf-visualiser" z domovského adresáře</font>
  * přidání tlačítek zoom +/- do okna na status bar
  * úprava telnetu
    * zrychlení přijetí dat
    * zrychlení načítání názvů routerů při použití defaultní rDNS (použití vláken)
    * port IPv6 nepovinný, pokud je nevyplněn, nepřipojuje se
  * možnosti rozmístění routerů v mapě
    * FR layout - ukončení po několika krocích iterace, defaultní po načtení modelu
    * SpringLayout - neustálé rozmísťování, možnost spustit/vypnout
    * JS layout - dostupné přes menu, pouze vývojová verze
  * úprava načítání dat
    * možnost načítat data z výstupu CGI skriptu
    * načítání subnet + external LSA
  * vyhledávání i v rozsahu podsítích (pro IPv4)
  * postranní panel zobrazuje také external LSA
  * toggle tlačítko na zobrazení/skrytí IPv6
  * tlačítka s nedostupnými funkcemi disablována
  * opravy
    * zvýraznění asymetrických spojů
    * nalezení suffixu v názvech routerů a oříznutí
    * hint ve vyhledávacím poli
    * načítání NML souboru
  * možnost změnit tvar spoje
    * v nastavení výběrem z roletky


## ospf3.0.2 (20.9.2012) ##
  * celková změna GUI na jedno okno + okno pro sledování stavů sítě v čase
  * úprava vyhledávání v mapě (dle ID nebo názvu routeru - non-case senzitivní)
  * dialog pro informaci o právě probíhající operaci
  * oprava dialogu pro XGMML export
  * export SVG
  * zvýraznění spoje pod kurzorem
  * informační postranní panel s:
    * načtenými modely - roletka pro výběr modelu
    * seznamem routerů v modelu
    * informacemi o vybraném routeru/spoji
  * výběr dat dle časového intervalu v zadaném čase
    * možnost načtení modelů do seznamu
    * nebo do okna pro sledování stavů
  * výběr dat ze zip souboru obsahujícího všechny dostupné modely
  * zrychlení telnet (překlad hostname)
  * přidána podpora získání dat přes telnet pro IPv6
  * použití defaultního DNS serveru v případě nezadání explicitního
  * opravy popisků (úprava, sjednocení...)
  * změna statusbaru
  * tipy pro práci s aplikací v menu nápověda
  * opravy bugů okna sledování stavů:
    * mód nejkratších cest
    * přidána možnost zarovnat dle GPS
    * u vypadlých spojů zůstává zobrazení poslední známé ceny

## ospf3.0.1 (26.6.2012) ##
  * opravy layoutů (výpadky spojů, následky výpadku spojů/routerů)
  * opravy popisků
  * oprava stahování dat přes telnet
  * oprava dialogů pro otevření/uložení NML souboru
  * opraven postranní panel v okně sledování stavů v čase
  * možnost zaškrtnout/odškrtnout vše při výběru logů
  * použití FR layoutu pro rozvhrhování mapy
    * rychlejší, nevytěžuje stále CPU
  * zjištění suffixu a jeho odmazání (vyhledá se nejvyskytovanější suffix názvu) - pouze u načítání dat přes telnet

## ospf3.0.0 (20.6.2012) ##
  * cs/en dokončen překlad
  * možnost vyhledávat v mapě routerů
    * CTRL+f předá focus poli pro zadání hledaného řetězce
    * pro vyhledání více řetězců lze řetězce oddělit symbolem |
    * hledá se název routeru obsahující hledaný řetězec
    * nalezené routery jsou vyznačeny zelenou barvou a popisek modrou

## ospf3.0.0rc (9.6.2012) ##
  * nastavení jazyku
  * cs/en částečný překlad
  * připojení k routeru přes telnet, možnosti nastavení:
    * adresa routeru, port, heslo
    * timeout (čas po který se bude aplikace pokoušet navázat spojení)
    * rDNS server - adresa serveru pro reverzní DNS překlad z IP na název routeru
  * export do souboru xgmml, který lze importovat do programu Cytoscape a pracovat s ním
  * oprava na dynamický layout GUI - správné zobrazení na Linux/Windows
  * ukládání souboru s nastavením aplikace do domovského adresáře
    * při prvním spuštění se v domovském adresáři vytvoří složka .ospf-visualiser a v ní soubor settings.properties s nastavením aplikace (jazyk se nastaví dle systémového)
    * při potvrzení načítání logů/dat modelu se uloží vyplněné hodnoty
    * při potvrzení načítání dat modelu se uloží také způsob načítání dat

Snímek z programu Cytoscape s načteným modelem:
<img src='http://ospf-visualiser.googlecode.com/svn/wiki/scn_cytoscape.png' />


## starší verze ##
Starší verze byly popisovány na adrese http://lab.hkfree.org/ospfmap/