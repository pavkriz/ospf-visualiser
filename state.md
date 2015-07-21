**27.5.2013**
  * pridani ikony JS layout na toolbar
  * pri zmene rozvrhovani nejprve zastaveni aktualniho
  * uprava JS layoutu
  * cisteni bordel v kodech

**25.5.2013**
  * shortkey ctrl L - nacist ospf model
  * BUG - rozmistovani po zobrazeni mapy od centralniho routeru

**16.4.2013**
  * oprava tip dialogu
  * pridana moznost zobrazit nove pridany model v nastaveni
  * **nova verze 3.0.4**

**11.4.2013**
  * oprava tlacitka pro odebirani zaznamu v nacteni dat pres rozmezi datumu
  * zruseni konstantky lltd\_url, adresa lltd dat nyni ve vstupnim poli

**10.4.2013**
  * odstraneni rDNS z CGI, nacitani name a geo z obsahu stazenych dat
  * oprava BUG: pri zaskrtnuti otevreni v okne stavu a nacitani jinym zpusobem uz nevyhazuje err-neni nacteno dost modelu

**9.4.2013**
  * opravy ikonek
  * oprava zarazeni lltd modelu k routerum

**27.3.2013**
  * opravy zobrazeni LLTD modelu v grafu
  * upravy tooltipu v mape

**23-26.3.2013**
  * vytvoreni rozhrani pro vrchol a hranu, abstraktnich predku
  * vytvoreni modelu pro lltd
  * predelani aplikace tak aby pracovala s rozhranimi
  * zobrazeni/skryti lltd modelu u routeru
  * sjednoceni popisku a titulku vrcholu a hran

**18.3.2013**
  * uprava nacitani cgi - tvar nazvu routeru z rdns
  * LLTD traceroute

**27.2.2013**
  * logovani LLTD

**26.2.2013**
  * pridani LLTD modelu k routerum v OSPF modelech

**25.2.2013**
  * dialog zobrazujici LLTD graf

**24.2.2013**
  * dialog zobrazujici seznamu LLTD modelu
  * dialog pro zobrazeni mapy LLTD modely

**23.2.2013**
  * nacitani dat ze serveru, parsovani do modelu v ospf-visualiseru

**20.2.2013**
  * ziskani dat z lltd, vytvoreni xml a odeslani na server

**13.2.2013**
  * nacitani names+geo pres cgi

**12.2.2013**
  * svg mozno upravovat dle css

**5.1.2013**
  * oprava zavirani nactenych modelu
  * uprava wiki stranek
  * **nova verze 3.0.3**

**2.1.2013**
  * vlastni FR layout - je potreba vyresit priblizeni
  * moznost menit EdgeShaper v nastaveni
  * uprava net state dialogu
    * layoutu
    * edge shape
  * oprava zobrazeni routeru s IPv6 po inicializaci grafu - uz se neukazuji
  * oprava - zruseni nazvu souboru v properties a nahrazeni konstantami
  * uprava nacitani dat - primarne nacteni souboru ospfdump, k tomu geo a nazvy
    * vzdalene ZIP
    * lokalni ZIP
    * lokalni soubory

**21.12.2012**
  * oprava vyhledavani - pri nacteni nml nehleda v ospfmodelu, jen v mapamodelu
  * cistka trid

**20.12.2012**
  * oprava netstatewindow - pridani fr a spring layoutu
  * zbaveni zavislosti na prikazech pro urceni typu dat pri parsovani

**19.12.2012**
  * oprava nacitani NML souboru
  * zmenseni source data dialogu

**19.11.2012**
  * zaneseny drobne upravy
    * prohozeni sloupcu v tabulka se subnetama
    * oprava hintu ve vyhledavacim poli
    * disabled akce ktere nelze provest (GPS, IPv6)

**18.11.2012**
  * vyhledavani v podsitich dle rozsahu (masky)

**17.11.2012**
  * nacitani subnetu + external LSA
  * vyhledavani v stub + external LSA

**11.11.2012**
  * toggle tlacitko pro IPv6 zobrazit/skryt
  * zobrazeni cen zpet na puvodni zpusob, pri IPv6 info o verzi
  * port pro IPv6 data netreba vypnlit
  * uprava suffixu v nazvu routeru

**10.10.2012 - 12.10.2012**
  * nacitani IPv6 do modelu novym zpusobem

**8.10.2012**
  * cgi - RDNS nazvy routeru

**7.10.2012**
  * uprava nacitani mapModelu
  * oprava zvyrazneni asymetrickych spoju
  * nacitani z vystupu CGI skriptu

**6.10.2012**
  * nacteni dat v novem tvaru
  * uprava stavajicich kodu

**5.10.2012**
  * telnet - defaultni rDNS taky pomoci vlaken
  * telnet - ziskani dat tremi prikazy a jejich nacteni

**4.10.2012**
  * layout - FR/Spring, kazdy ma sve tlacitko, defaultne FR
  * labely spoju, posunuty popisky routeru
  * telnet nacitani dat - upraven dialog + appnastaveni

**3.10.2012**
  * layout - moznost FR/Spring pres menu

**28.9.2012**
  * telnet - zruseni Read vlakna, nyni vse v jednom vlakne

**26.9.2012**
  * pridani + - tlacitko zoomu na status bar v ospfwin a netstatewin

**25.9.2012**
  * pridani zoom do net state okna

**20.9.2012**
  * net state win - zobrazeni ceny a info pro vypadle spoje
  * **nova verze 3.0.2**

**18.9.2012**
  * oprava na nenutnost mazani app settings souboru v home pri nove verzi
  * oprava bugu net state window - mod nejkratsich cest ihned prekresli mapu
  * net state okno - pridano umistovani dle GPS

**17.9.2012**
  * upravy textu

**16.9.2012**
  * IPv6
  * oprava tips
  * lng - preklady

**11.9.2012**
  * IPv6 - telnet, nacitani

**10.9.2012**
  * sjednoceni popisku net state window a menu map panelu

**9.9.2012**
  * dodelan state dialog
  * opraveno nacitani logu
  * opraven net state window proti null exception

**7.9.2012**
  * upraven state dialog a pridan k ostatnimu nacitani dat

**5.9.2012**
  * net state okno - hover
  * preklad tipu

**2.9.2012**
  * statusbar v netstate okne

**29.8.2012**
  * oprava rozdilu darum od - do pro vyber dat

**28.8.2012**
  * hlavni okno upraveno do noveho vzhledu

**27.8.2012**
  * tipy aplikace
  * preklad tipu aplikace

**26.8.2012**
  * log okno pro prave probihajici operace
  * opraveno nastaveni modu zobrazovani sousedu
  * zobrazeni info v property okne po kliku na router v seznamu

**25.8.2012**
  * opraveny popisky pro zobrazeni nejkratsi cesty (jsou dve varianty)
  * dialog pro informace o prave provadene operaci

**24.8.2012**
  * nacitani od-do - nacitani do tabu nebo net state okne

**23.8.2012**
  * pouziti defaultniho DNS serveru pokud neni zadan jiny
  * telnet IPv6
  * uprava status bar
  * nacitani od-do uprava na moznost nacteni do zalozek/otevreni v net state okne

**22.8.2012**
  * zrychleni a osetreni telnet - preklad hostname
  * telnet IPv6

**21.8.2012**
  * stub, properties okno

**7.8.2012**
  * opetovne nahrani zdrojovych kodu (svn neustalo hromadu zmen)

**6.8.2012**
  * export do SVG (tvary, barvy)

**5.8.2012**
  * export do SVG (zakladni - vrcholy, hrany)

**2.8.2012**
  * nacitani seznamu zip souboru z jednoho files.txt.gz

**1.8.2012**
  * vyber zdroje dat z casoveho rozmezi po intervalech
  * ukladani a nacitani do properties (pocet dni je pocitan a nastavovan aktualne)
  * upraveno vyhledavani (non-case-sensitive, vyhledava v nazvu i popisu soucasne)
  * nacitani seznamu zip souboru z jednoho files.txt.gz

**31.7.2012**
  * vyber zdroje dat z casoveho rozmezi po intervalech

**25.7.2012**
  * properties okno obsahujici strom routeru a info o vybranem routeru

**24.7.2012**
  * zvyrazneni hrany pod kurzorem

**22.7.2012**
  * uprava GUI

**20.7.2012**
  * oprava dialogu pro export do xgmml
  * oprava prehledu s daty ospf modelu
  * uprava GUI

**19.7.2012**
  * uprava GUI

**18.7.2012**
  * uprava GUI

**17.7.2012**
  * uprava GUI

**14.7.2012**
  * upraveno vyhledavani, pridano tlacitko

**26.6.2012**
  * layout dialogu s vypadky spoju
  * layout importance link
  * layout importance router
  * **nova verze 3.0.1**

**25.6.2012**
  * oprava stahovani dat pres telnet
  * nalezeni suffixu pro data prijata telnetem (nejcastejsi vyskyt)
  * map design predelan na FR layout
  * vyber logu - zaskrtnout/odskrtnout vse
  * oprava dialogu pro otevreni nml souboru
  * oprava dialogu pro ulozeni nml souboru
  * oprava okna pro sledovani stavu v case
    * predelani na FR layout
    * opraven postranni panel

**20.6.2012**
  * preklady
  * uprava wiki, zkouska FR layoutu
  * **nova verze 3.0.0**

**19.6.2012**
  * preklady
  * oprava vyhledavani, mozne vyhledavat vice nazvu - oddeleni svislitkem

**18.6.2012**
  * preklady

**11.6.2012**
  * vyhledavani routeru a jeho zvyrazneni

**9.6.2012**
  * preklady
  * opravy layoutu
  * sjednoceni usporadani tlacitek
  * ukladani info, jakym zpusobem byla data naposledy ziskana
  * oprava nacitani obr pro about

**7.6.2012**
  * predelani na dynamicky layout (popisky jsou viditelne i v en)
  * eport modelu do xgmml souboru vhodeho pro import do Cytoscape
  * nacitani nastaveni z properties souboru, pokud neexistuje, tak vytvori defaultni v domovskem adresri uzivatele (jazyk nastavi dle OS)
  * moznost nastavit timeout u telnetu

**1.6.2012**
  * kompletni predelani na nacitani textu z lng

**28.4.2012**
  * pri 1. spusteni zjisteni lokalizace, nasledne nacteni popisku dle toho a ulozeni do nastaveni
  * preklady do en

**27.4.2012**
  * soubor properties s nastavenim aplikace a stahovanim dat
  * nacitani a ukladani nastaveni
  * preklady do en

**26.4.2012**
  * novy dialog pro nastaveni
  * zacatek lokalizace

**24.4.2012**
  * nacitani nazvu routeru pres knihovnu fastreversedns.jar

**22.4.2012**
  * nacitani pres telnet

**7.3.2012**
  * reset zdrojovych kodu
  * nahrani novych kodu s kodovanim UTF-8
  * baliky doplneny o org.hkfree.ospf


**22.2.2012**
  * zalozen ucet na code.google.com
  * nahrany zdrojove kody
  * vytvorena wiki stranka