package ais.gui.common;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import ais.bo.SluzbaBD;
import ais.bo.pw.PWVlastnostOJ;
import ais.bo.ss.sc.SCDenVTyzdni;
import ais.bo.ss.sc.SCJazyk;
import ais.bo.ss.sc.SCMesiac;
import ais.bo.ss.sc.SCObec;
import ais.bo.ss.sc.SCOkres;
import ais.bo.ss.sc.SCParamSystem;
import ais.bo.ss.sc.SCStat;
import ais.bo.ss.sc.SCSviatokKalendar;
import ais.bo.ss.sc.SCTypDatumAkcia;
import ais.bo.ss.sc.SCTypParamSystem;
import ais.bo.ss.sc.crsc_.CRSC_Obec;
import ais.bo.ss.sc.crsc_.CRSC_Staty;
import ais.bo.ss.sc.pw.SCPWOrgJednotka;
import ais.bo.ss.sc.st.SCSTCinnostStudPrg;
import ais.bo.ss.sc.st.SCSTDruhStudia;
import ais.bo.ss.sc.st.SCSTFormaStudia;
import ais.bo.ss.sc.st.SCSTMetodaStudia;
import ais.bo.ss.sc.st.SCSTSposVyucby;
import ais.bo.ss.sc.st.SCSTStudProgram;
import ais.bo.ss.sc.st.SCSTStupenStudia;
import ais.bo.ss.sc.st.SCSTTypStudia;
import ais.bo.ss.so.ms.MSMessage;
import ais.bo.ss.so.ms.MSMessagePouzivatela;
import ais.bo.ss.so.ps.PSParamSysOJ;
import ais.bo.ss.so.pz.PZZostava;
import ais.bo.ss.so.pz.TemplatePZ;
import ais.bo.ss.sp.SPProfilPouzivatela;
import ais.bo.vs.es.ESMnozinaStudii;
import ais.bo.vs.es.ESStudium;
import ais.bo.vs.es.ESZapisnyList;
import ais.bo.vs.st.STCinnostSPOJ;
import ais.bo.vs.st.STJazykPredmet;
import ais.bo.vs.st.STPredmetRok;
import ais.gui.common.print.PrintUtils;
import ais.gui.common.print.TransformationService;
import ais.sys.AisAppException;
import ais.sys.AisApplicationContext;
import ais.sys.Globals;
import ais.utils.DateTimeFormater;
import avc.ui.AVCException;
import avc.ui.list.AVCAbstractArrayListModel;
import base.Columns;
import base.Filter;
import base.JoinTree;
import base.NestedException;
import base.Ordering;
import base.mm.Attribute;
import base.ps.BarSpec;
import base.ps.EmptySpec;
import base.ps.JoinTreeBuilder;
import base.ps.PSException;
import base.ps.ReadOnlyPersistentService;
import base.so.AppException;
import base.util.ArrayUtils;
import base.util.DateUtils;
import base.util.StringUtils;
public class DlgMdl {
    /** 30.126 */
    public static final double KURZ_EURA = 30.126;
    public static final Date DATUM_UKONCENIA_DUALNEHO_ZOBRAZOVANIA = base.util.DateUtils.date(2010, Calendar.JANUARY, 1 );
    public static final Date DATUM_ZAVEDENIA_EURA = base.util.DateUtils.date(2009, Calendar.JANUARY, 1);
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat( "#,##0.00" );
    public static final String maskaPredcislieUctu = "999999;0; ";
    public static final String maskaCisloUctu = "999999999999999999;0; ";
    public static final String maskaKS = "9999;0; ";
    public static final String maskaKodBanky = "9999;0; ";
    public static final String maskaVSSS = "9999999999;0; ";
    /**
     * @author obrinm
     * @deprecated !pouzi public static DisplayString.get( AisApp, ESStudium ) metodu
     */
    public static String getDisplayStringESStuium( AisApp app, ESStudium s ) {
        try {
            return DisplayString.get( app, s );
        } catch (NestedException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * Metoda by chcela ponukat display string ESZapisnyList
     * spravne prelozeny aj do inej jazykovej mutacie. Ak nema
     * vyplnene objekty, sama si ich dotiahne a tiez vypocita
     * rok studia.
     *
     * @author obrinm
     * @throws NestedException
     */
    public static String getDisplayString( AisApp app, ESZapisnyList zl ) throws NestedException
    {
        String spec = "";
        StringBuffer ds = new StringBuffer();
        if ( zl.getRocnik() == null )
            spec += "rocnik();";
        if ( zl.getStudProgram() == null )
            spec += "studProgram();";
        if ( !spec.isEmpty() )
            app.getPS().retrieveAssocObjects( zl, new BarSpec( spec ) );
        int rokStudia =
                app.getPS().selectCount( ESZapisnyList.class, null,
                        new BarSpec( "[idStudium = ${idStudium} AND popisAkadRok <= ${akRok}]",
                                new Object[][] { { "idStudium", zl.getIdStudium() }, { "akRok", zl.getPopisAkadRok() } } ) );
        ds.append( zl.getPopisAkadRok() );
        ds.append( ", " ).append( zl.getRocnik().getPopis() );
        ds.append( ", " ).append( app.translation( "ROK_STUDIA", "rok štúdia" ) ).append( ": " ).append( rokStudia );
        SCSTStudProgram sp = zl.getStudProgram();
        if ( sp != null )
            ds.append( ", " ).append( sp.getSkratka() + " - " + sp.getPopis() + ", " + sp.getDoplnUdaje() );
        return ds.toString();
    }
    /**
     * Vrati prelozeny html display string pre mnozinu studii.
     * @author obrinm
     */
    public static String getHtmlDisplayString( AisApp app, ESMnozinaStudii m ) {
        return
                m.getHtmlDispplayString()
                        .replace( "${poznamka}", app.translation( "POZNAMKA", "Poznámka" ) )
                        .replace( "${metoda}", app.translation( "METODA", "Metóda" ) )
                        .replace( "${typFinancovania}", app.translation( "TYP_FINANCOVANIA", "Typ financovania" ) )
                        .replace( "${stupen}", app.translation( "STUPEN_STUDIA", "Stupeň štúdia" ) )
                        .replace( "${druh}", app.translation( "DRUH_STUDIA", "Druh štúdia" ) )
                        .replace( "${typStudia}", app.translation( "TYP_STUDIA", "Typ štúdia" ) )
                        .replace( "${forma}", app.translation( "FORMA_STUDIA", "Forma štúdia" ) )
                        .replace( "${rocnik}", app.translation( "ROCNIK", "Ročník" ) )
                        .replace( "${kruzok}", app.translation( "KRUZOK", "Krúžok" ) )
                        .replace( "${studijneProgramy}", app.translation( "STUDIJNE_PROGRAMY", "Študijné programy" ) );
    }
    private static String getOracleString( Date d ) {
        return "to_date( '" + d + "', 'yyyy-mm-dd' )";
    }
    /**
     * Z profilu prihlaseneho pouzivatela  vyberie organizacne jednotky, kde ma
     * prihlaseny pouzivatel danu rolu.
     * @param app
     * @param idRola
     * @return Set<String> mnozina pristupnych oj
     */
    public static Set<String> ziskajPristupneOJs( AisApp app, int idRola )
    {
        return ziskajPristupneOJs( app, new int[] { idRola } );
    }
    /**
     * specifikacia pre vyber mnoziny dat, potrebnej pre elektronicky podpis hodnotenia.<br>
     * !!! prosim nemenit bez konzultacie s autorom.
     * @autor mobrin
     */
    public static final String HODNOTENIE_SPEC =
            "!;kodKlasifikacnyStupen;datum;kodFaza;kodTermin;uznane;idUdelil;idPredmetZL;" +
                    "elPodpisyHod([TypPodpisu='H']);"+
                    "udelil(!;meno;priezvisko;rodneCislo);" +
                    "predmetZL(!;kredit;kodSemester;kodSposUkon;predmet(!;skratka;nazov;stredisko;popisRokVzniku);" +
                    "zapisnyList(!;popisAkadRok;rokRocnik;studProgram(!;skratka);" +
                    "studium(!;student(!;meno;priezvisko;rodneCislo;" +
                    "identifKarta(!;prefix;sufix;cisloKarty;kodTypIDCisla;" +
                    "[TRUNC(sysdate) BETWEEN TRUNC( COALESCE( platnostOd, sysdate ) )" +
                    " AND TRUNC( COALESCE( platnostDo, sysdate) )];{kodTypIDCisla,prefix,cisloKarty,sufix})))))";
    /**
     * Ziskanie mnoziny skratiek organizacnych jednotiek, na ktorych ma pouzivatel dane roly.
     * @param app
     * @param ideckaRoli
     * @return Set<String>
     */
    public static Set<String> ziskajPristupneOJs( AisApp app, int[] ideckaRoli )
    {
        SPProfilPouzivatela[] profily = app.getPouzivatel().getProfil( ideckaRoli );
        Set<String> pristupneOJs = new HashSet<String>();
        if ( profily != null )
        {
            for ( SPProfilPouzivatela p: profily ) pristupneOJs .add( p.getSkratkaOrganizacnaJednotka() );
        }
        return pristupneOJs;
    }
    //	----------------------------------------------------------------------------------------------------------------------------
    //	----------------------------------------------------------------------------------------------------------------------------
    //	----------------------------------------------------------------------------------------------------------------------------
    /**
     * Ziskanie zoznamu fakult a univerzity instalacie. Zoradene podla skratky, kde na prvom
     * mieste je univerzita a potom nasleduju fakulty.
     */
    public static SCPWOrgJednotka[] ziskajFakultyUniverzituInstalacie( AisApp app ) throws AppException
    {
        return ziskajFakultyUniverzituInstalacie( app, ( Set<String> )null );
    }
    /**
     * Ziskanie univerzity, resp. fakult instalacie, ktore maju prienik so zadanou mnozinou skratiek
     * organizacnych jednotiek.
     */
    public static SCPWOrgJednotka[] ziskajFakultyUniverzituInstalacie( AisApp app, SCPWOrgJednotka[] ojs ) throws AppException
    {
        if ( ( ojs == null ) || ( ojs.length == 0 ) )
        {
            return new SCPWOrgJednotka[ 0 ];
        }
        Set<String> set = new HashSet<String>();
        for ( int i = 0; i < ojs.length; i++ ) set.add( ojs[ i].getSkratka() );
        return ziskajFakultyUniverzituInstalacie( app, set );
    }
    /**
     * Ziskanie zoznamu fakult a univerzity instalacie, ktorych skratky su v retazci "in".
     * Zoradene podla skratky, kde na prvom mieste je univerzita a potom nasleduju fakulty.
     * TODO duplicita metody je aj v triede InformacnyListAK_Mdl
     */
    public static SCPWOrgJednotka[] ziskajFakultyUniverzituInstalacie( AisApp app, Set<String> ojs ) throws AppException
    {
        try
        {
            String ioj = app.getAK().pcs.sys.getSystemParam( SCParamSystem.INSTALL_OGRJEDNOTKA_SKRATKA );
            Filter f = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
            f.addEqCond( "skratka", ioj );
            if ( ojs != null && ojs.size() > 0 )
                f.addListCond( 0, false, "skratka", ojs.toArray() );
            SCPWOrgJednotka[] data1 = ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f, null ) );
            Ordering o = new Ordering();
            o.addColumnOrder( "skratka", false );
            Filter f2 = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
            f2.addEqCond( "skratkaNadriadena", ioj );
            f2.addEqCond( "skratkaTypOrgJednotky", "Fakul" );
            if ( ojs != null && ojs.size() > 0 )
                f2.addListCond( 0, false, "skratka", ojs.toArray() );
            SCPWOrgJednotka[] data2 = ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f2, o ) );
            return ( SCPWOrgJednotka[] )ArrayUtils.concat( data1, data2 );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri získavaní organizačných jednotiek inštalácie.", e );
        }
    }
    /**
     * ziskanie organizacnych jednotiek, so skratkami v poli "ojs"
     *
     * @sqli - ok
     *
     * @param app - objekt aplikacie
     * @param ojs - pole skratiek organizacnych jednotiek ( do klausuly IN )
     * @param platne - ci sa maju vratit len platne oj, alebo secky
     * @return - pole vyhovujucich oj SCPWOrgJednotka[]
     *
     */
    public static SCPWOrgJednotka[] ziskajOrganizacneJednotky( AisApp app, String[] ojs, boolean platne ) throws NestedException
    {
        Filter f = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
        if ( ojs != null && ojs.length > 0 )
        {
            f.addListCond( 0, false, "skratka", ojs );
        }
        if ( platne )
        {
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
        }
        try
        {
            return ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 0, "Chyba pri ziskavani organizacnych jednotiek.", e );
        }
    }
    /**
     * ziskanie organizacnych jednotiek, ktorych skratky su vo vstupujucej mnozine
     * @param app
     * @param ojs - mnozina skratiek organizacnych jednotiek
     * @param platne - ci zobrazovat len platne
     * @return
     * @throws AppException
     */
    public static SCPWOrgJednotka[] ziskajOrganizacneJednotky( AisApp app, Set<String> ojs, boolean platne ) throws NestedException
    {
        return ziskajOrganizacneJednotky( app, ojs.toArray( new String[ ojs.size() ] ), platne );
    }
    /**
     * sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOrganizacneJednotky( AisApp app, SPProfilPouzivatela[] profily, boolean platne ) throws NestedException
    {
        if ( ( profily == null ) || ( profily.length == 0  ) )
        {
            return new SCPWOrgJednotka[ 0 ];
        }
        Set<String> ojs = new HashSet<String>();
        for ( SPProfilPouzivatela p: profily ) ojs.add( p.getSkratkaOrganizacnaJednotka() );
        return ziskajOrganizacneJednotky( app, ojs, platne );
    }
    /**
     * Ziskanie organizacnych jednotiek podla skratiek, typov a platnosti. Ak su null, ignoruju sa.
     * @param app
     * @param skratky
     * @param typy
     * @param platne
     * @return
     * @throws AppException
     */
    public static SCPWOrgJednotka[] ziskajOrganizacneJednotky( AisApp app, String[] skratky, String[] typy, boolean platne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
            if ( skratky != null && skratky.length > 0 )
            {
                f.addListCond( 0, false, "skratka", skratky );
            }
            if ( typy != null && typy.length > 0 )
            {
                f.addListCond( 0, false, "skratkaTypOrgJednotky", typy );
            }
            if ( platne )
            {
                f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            }
            Ordering o = new Ordering();
            o.addColumnOrder( "skratka", false );
            return ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 0, "Chyba pri ziskavani organizacnych jednotiek.", e );
        }
    }
    /**
     *
     * Ziskanie organizacnych jednotiek podla skratiek, typov a platnosti. Ak su null, ignoruju sa.
     * @param app
     * @param skratky
     * @param typy
     * @param platne
     * @return
     * @throws AppException
     */
    public static SCPWOrgJednotka[] ziskajOrganizacneJednotky( AisApp app, Set<String> skratky, Set<String> typy, boolean platne ) throws AppException
    {
        return ziskajOrganizacneJednotky( app, skratky.toArray( new String[ skratky.size() ] ), typy.toArray( new String[ typy.size() ] ), platne );
    }
    /**
     * vytvori z pola stringov, sql string pre klausulu "IN".
     * napr.: 'oj1', ..., 'ojX'
     * @param ojs
     * @return
     */
    public static String in( Object[] ojs ) {
        if ( ( ojs == null ) || ( ojs.length == 0 ) )
            return "''";
        String in = "";
        for ( int i = 0; i < ojs.length; i++ )
            in += "'" + ojs[ i ] + "',";
        if ( in.length() == 0 )
            return "''";
        in = in.substring( 0, in.length() -1 );
        return in;
    }
    /**
     * vytvori z pola intov, sql string pre klausulu "IN".
     * napr.: 1, 2, ..., X
     */
    public static String in( int[] ints ) {
        if ( ( ints == null ) || ( ints.length == 0 ) )
            return "";
        String in = "";
        for ( int i = 0; i < ints.length; i++ )
            in += + ints[ i ] + ",";
        if ( in.length() == 0 )
            return "";
        in = in.substring( 0, in.length() - 1 );
        return in;
    }

    /**
     * vytvori z pola intov, sql string pre klausulu "IN".
     * napr.: 1, 2, ..., X
     */
    public static String in( Integer[] ints ) {
        if ( ( ints == null ) || ( ints.length == 0 ) )
            return "";
        String in = "";
        for ( int i = 0; i < ints.length; i++ )
            in += + ints[ i ] + ",";
        if ( in.length() == 0 )
            return "";
        in = in.substring( 0, in.length() - 1 );
        return in;
    }
    /**
     * vytvori zo Set-u stringov, sql string pre klausulu "IN".
     * napr.: 'oj1', ..., 'ojX'
     * @param ojs
     * @return
     */
    public static String in( Set<String> ojs ) {
        if ( ( ojs == null ) || ( ojs.size() == 0 ) )
            return "''";
        String in = "";
        for ( Iterator<String> i = ojs.iterator(); i.hasNext(); )
            in += "'" + i.next() + "',";
        if ( in.length() == 0 )
            return "''";
        in = in.substring( 0, in.length() -1 );
        return in;
    }
    /**
     * vytvori zo Set-u numberov, sql string pre klausulu "IN".
     * napr.: id1, ..., idN
     * @param ojs
     * @return
     */
    public static String inIds( Set<Integer> ids ) {
        if ( ( ids == null ) || ( ids.size() == 0 ) )
            return "";
        String in = "";
        for ( Iterator<Integer> i = ids.iterator(); i.hasNext(); )
            in += i.next() + ",";
        if ( in.length() == 0 )
            return "";
        in = in.substring( 0, in.length() -1 );
        return in;
    }
    /**
     * Ziskanie organizacnych jednotiek, ktore vzhladom k zadanej <code>oj</code> maju
     * uvedene vlastnosti.
     * @param oj - skratka organizacnej jednotky
     * @param vlastnosti - pole konstant triedy PWVlastnostOJ
     * @return vracia pole vyhovujucich organizacnych jednotiek. v pripde, ze niektory z parametrov
     * nie je vyplneny, vracia pole dlzky 0.
     * @author mobrin
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String oj, int[] vlastnosti ) throws AppException
    {
        if ( ( oj == null ) || ( oj.trim().length() == 0 ) )
        {
            app.log( "DlgMdl.ziskajOJPreOJ( app, oj, vlastnosti ): parameter String oj nie je vyplneny", "warning" );
            return new SCPWOrgJednotka[ 0 ];
        }
        return ziskajOJPreOJ( app, new String[] { oj }, vlastnosti, null, null );
    }
    /**
     * ziskanie organizacnych jednotiek, krore vzhladom k zadanej <code>oj</code> maju
     * uvedenu vlastnost.
     * @param oj - skratka organizacnej jednotky
     * @param vlastnosti - konstanta z triedy PWVlastnostOJ
     * @return vracia pole vyhovujucich organizacnych jednotiek. v pripde, ze niektory z parametrov
     * nie je vyplneny, vracia pole dlzky 0.
     * @author mobrin
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String oj, int vlastnost ) throws AppException
    {
        if ( ( oj == null ) || ( oj.trim().length() == 0 ) )
        {
            app.log( "DlgMdl.ziskajOJPreOJ( app, oj, vlastnosti ): parameter String oj nie je vyplneny", "warning" );
            return new SCPWOrgJednotka[ 0 ];
        }
        return ziskajOJPreOJ( app, new String[] { oj }, new int[] { vlastnost }, null, null );
    }
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String oj, int vlastnost, Date odDatumu, Date doDatumu ) throws AppException
    {
        return ziskajOJPreOJ( app, new String[] { oj }, new int[] { vlastnost }, null, null, odDatumu, doDatumu );
    }
    /**
     * ziskanie organizacnych jednotiek, krore vzhladom k zadanej <code>oj</code> maju
     * uvedenu vlastnost. organizacne jednotky sa vyberaju len take, ktorych skratka je v
     * mnozine <code>in</code>. ak je in == null alebo in.size() == 0 tak sa tato podmienka
     * ignoruje a vratia sa vsetky vyhovujuce OJ.
     * @param app
     * @param oj - skratka oj ku ktorej hladame ojs so specifikou vlastnstou
     * @param vlastnost - konstanta z triedy PWVlastnostOJ
     * @param in - mnozina stringov, skratiek organizacnych jednotiek
     * @return
     * @throws AppException
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String oj, int vlastnost, Set<String> in ) throws AppException
    {
        if ( ( oj == null ) || ( oj.trim().length() == 0 ) )
        {
            return new SCPWOrgJednotka[ 0 ];
        }
        return ziskajOJPreOJ( app, new String[] { oj }, new int[] { vlastnost }, in, null );
    }
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String oj, int vlastnost, Set<String> in, Date odDatumu, Date doDatumu ) throws AppException
    {
        if ( ( oj == null ) || ( oj.trim().length() == 0 ) )
        {
            return new SCPWOrgJednotka[ 0 ];
        }
        return ziskajOJPreOJ( app, new String[] { oj }, new Integer[] { vlastnost }, in, null, odDatumu, doDatumu );
    }
    /**
     * Ziskanie organizacnych jednotiek, ktore vzhladom k zadanym organizacnym jednotkam
     * disponuju zadanymi vlastnostami.
     * @param app
     * @param ojs - pole objektov organizacnych jednotiek
     * @param vlastnosti - pole konstant triedy PWVlastnostOJ
     * @return - pole vyhovujucich organizacnych jednotiek
     * @throws AppException
     * @author mobrin
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, SCPWOrgJednotka[] ojs, int[] vlastnosti ) throws AppException
    {
        if ( ( ojs == null ) || ( ojs.length == 0 ) )
        {
            return new SCPWOrgJednotka[ 0 ];
        }
        Set<String> skratky = new HashSet<String>();
        for ( int i = 0; i < ojs.length; i++ ) skratky.add( ojs[ i ].getSkratka() );
        return ziskajOJPreOJ( app, skratky.toArray( new String[ 0 ] ), vlastnosti, null, null );
    }
    /**
     * Ziskanie organizacnych jednotiek, ktore vhladom k zadanym organziacnym jednotkam <code>Set ojs</code>,
     * maju vlastnost <code>int vlastnost</code>. Parameter <code>String skratka</code> je filter pre vhladavane
     * organizacne jednotky. Ak je null alebo retazec dlzky 0, ignoruje sa.
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, Set<String> ojs, int vlastnost, String skratka ) throws NestedException
    {
        return ziskajOJPreOJ( app, ojs.toArray( new String[ ojs.size() ] ), new int[] { vlastnost}, null, skratka );
    }
    /**
     * Ziskanie organizacnych jednotiek, ktore vhladom k zadanej organziacnej jednotke <code>String oj</code>,
     * maju vlastnost <code>int vlastnost</code>. Parameter <code>String skratka</code> je filter pre vhladavane
     * organizacne jednotky. Ak je null alebo retazec dlzky 0, ignoruje sa.
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String oj, int vlastnost, String skratka ) throws NestedException
    {
        return ziskajOJPreOJ( app, ( oj == null || oj.trim().length() == 0 ) ? null : new String[]{ oj }, new int[]{ vlastnost}, null, skratka );
    }
    /**
     * Ziskanie organizacnych jednotiek, ktore vzhladom k zadanym organizacnym jednotkam
     * disponuju zadanymi vlastnostami. Ziskaju sa iba organizacne jednotky, ktorych skratky su
     * v mnozine in. Ak je mnozina in null alebo size() == 0 neberie sa v uvahu a vratia sa vsetky
     * vyhovujuce organizacne jednotky.
     * @param app
     * @param ojs - pole skratiek organizacnych jednotiek
     * @param vlastnosti - pole konstant triedy PWVlastnostOJ
     * @return - pole vyhovujucich organizacnych jednotiek
     * @throws AppException
     * @author mobrin
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String[] ojs, int[] vlastnosti, Set<String> in, String skratka ) throws AppException
    {
        Set<Integer> v = new HashSet<Integer>();
        if ( vlastnosti != null )
        {
            for ( int i: vlastnosti ) v.add( i );
        }
        return ziskajOJPreOJ( app, ojs, v.toArray( new Integer[ v.size() ] ), in, skratka, null, null );
    }
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String[] ojs, int[] vlastnosti, Set<String> in, String skratka, Date odDatumu, Date doDatumu ) throws AppException
    {
        Set<Integer> v = new HashSet<Integer>();
        if ( vlastnosti != null )
        {
            for ( int i: vlastnosti ) v.add( i );
        }
        return ziskajOJPreOJ( app, ojs, v.toArray( new Integer[ v.size() ] ), in, skratka, odDatumu, doDatumu );
    }
    /**
     * Ziskanie organizacnych jednotiek, ktore vzhladom k zadanym organizacnym
     * jednotkam ( String[] ojs - skratky SCPWOrgJednotka ) disponuju zadanymi
     * vlastnostami ( Integer[] vlastnosti - idecka PWVlastnostOJ ). Ak je mnozina
     * Set<String> in != null a size() > 0, tak sa do vysledku beru len tie
     * organizacne jednotky, ktorych skratky sa v tejto mnozine nachadzaju.
     * String skratka sluzi ako filter a umoznuje pouzitie * a ? ako wildcard.
     * Date odDatumu a Data doDatumu ak su rozne od null ( zistenie vztahu medzi
     * organizacnymi jednotkami v nejakom casovom obdobi, napr. akademickom roku),
     * znamenaju podmienku na PWVlastnostOJ.odDatumu resp. PWVlastnostOJ.doDatumu.
     * Ak su obidva tieto datumy null, potom sa pridava podmienka na aktualnu platnost
     * vyslednych orgnizacnych jednotiek.
     *
     * sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOJPreOJ( AisApp app, String[] ojs, Integer[] vlastnosti, Set<String> in, String skratka, Date odDatumu, Date doDatumu ) throws AppException
    {
        if ( ( vlastnosti == null ) || ( vlastnosti.length == 0 ) )
        {
            throw new IllegalArgumentException( "int[] vlastnosti musia byt vyplnene." );
        }
        try
        {
            Filter f = new Filter( app.getCip().getColumns( PWVlastnostOJ.class ) );
            f.addListCond( 0, false, "idTypVlastnostiOJ", vlastnosti );
            if ( ojs != null && ojs.length > 0 )
            {
                f.addListCond( 0, false, "skratkaPoskytujeOJ", ojs );
            }
            if ( ( in != null ) && ( in.size() > 0 ) )
            {
                f.addListCond( 0, false, "skratkaJePreOJ", in.toArray( new String[ in.size() ] ) );
            }
            String s = "";
            if ( odDatumu != null )
            {
                s += "( ( doDatumu IS NULL ) OR ( doDatumu >= ? ) )".replace( "?", getOracleString( odDatumu ) );
            }
            if ( doDatumu != null )
            {
                s += ( s.trim().isEmpty() ? "" : " AND ( ( odDatumu IS NULL ) OR ( odDatumu <= ? ) )".replace( "?", getOracleString( doDatumu ) ) );
            }
            String spec = "";
            if ( ! s.trim().isEmpty() )
            {
                spec = "[ ( odDatumu IS NULL AND doDatumu IS NULL ) OR ( " + s + " ) ]";
            }
            PWVlastnostOJ[] res = ( PWVlastnostOJ[] )app.getPS().retrieve( PWVlastnostOJ.class, new BarSpec( spec, f, null ) );
            if ( res == null || res.length == 0 )
            {
                return new SCPWOrgJednotka[ 0 ];
            }
            Set<String> skratky = new HashSet<String>();
            for ( PWVlastnostOJ v: res ) skratky.add( v.getSkratkaJePreOJ() );
            Filter f2 = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
            f2.addListCond( 0, false, "skratka", skratky.toArray( new String[ skratky.size() ] ) );
			/*
			 * ak pouzivatel nezadal obdobie, v ktorom prislusne organizacne
			 * jednotky disponuju uvedenymi vlastnostami, zaujimaju nas
			 * aktualne platne organizacne jednotky
			 */
            if ( odDatumu == null && doDatumu == null )
            {
                f2.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            }
			/*
			 * ak je filter na skratku
			 */
            if ( ( skratka != null ) && ( skratka.trim().length() > 0 ) )
            {
                Filter f3 = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
                skratka = StringUtils.delisp( skratka.trim().replace( '?', '_' ).replace( '*', '%' ).toUpperCase() ) + "%";
                f3.setNlsToAscii( true );
                f3.addEqCond( "skratka", skratka );
                SCPWOrgJednotka[] data3 = ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f3, null ) );
                if ( data3 != null && data3.length > 0 )
                {
                    Set<String> sk = new HashSet<String>();
                    for ( SCPWOrgJednotka o: data3 ) sk.add( o.getSkratka() );
                    f2.addListCond( 0, false, "skratka", sk.toArray( new String[ sk.size() ] ) );
                }
            }
            Ordering o = new Ordering();
            o.addColumnOrder( "skratka", false );
            return( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f2, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať organizačné jednotky.", e );
        }
    }
    //	----------------------------------------------------------------------------------------------------------------------------
    //	----------------------------------------------------------------------------------------------------------------------------
    //	----------------------------------------------------------------------------------------------------------------------------
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v Set ojs ( skratky ojs ) maju
     * vlastnosti v int[] vlastnosti
     *
     *  sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOJSPreKtoreMameVlastnosti( AisApp app, Set<String> ojs, Integer[] vlastnosti ) throws NestedException
    {
        return ziskajOJSPreKtoreMameVlastnosti( app, ojs.toArray( new String[ ojs.size() ] ), vlastnosti );
    }
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v String[] ojs ( skratky ojs ) maju
     * vlastnosti v int[] vlastnosti
     *
     * sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOJSPreKtoreMameVlastnosti( AisApp app, String[] ojs, Integer[] vlastnosti ) throws NestedException
    {
        return ziskajOJSPreKtoreMameVlastnosti(app.getAK(), ojs, vlastnosti);
    }
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v String[] ojs ( skratky ojs ) maju vlastnosti v int[] vlastnosti.
     */
    public static SCPWOrgJednotka[] ziskajOJSPreKtoreMameVlastnosti(AisApplicationContext ctx, String[] ojs, Integer[] vlastnosti) throws NestedException
    {
        Filter f = new Filter(ctx.pci.getColumns(PWVlastnostOJ.class));

        if (ojs != null && ojs.length > 0) {
            f.addListCond( 0, false, "skratkaJePreOJ", ojs );
        }

        if (vlastnosti != null && vlastnosti.length > 0) {
            f.addListCond(0, false, "idTypVlastnostiOJ", vlastnosti);
        }

        PWVlastnostOJ[] vlastnostiOJ = (PWVlastnostOJ[])ctx.ps.retrieve( PWVlastnostOJ.class, new EmptySpec(f, null));
        if (vlastnostiOJ == null || vlastnostiOJ.length == 0) {
            return new SCPWOrgJednotka[0];
        }
        Set<String> oj = new HashSet<String>();

        for(PWVlastnostOJ vlastnost: vlastnostiOJ) {
            oj.add(vlastnost.getSkratkaPoskytujeOJ());
        }
        f = new Filter(ctx.pci.getColumns( SCPWOrgJednotka.class));
        f.addListCond(0, false, "skratka", oj.toArray(new String[oj.size()]));
        return (SCPWOrgJednotka[])ctx.ps.retrieve(SCPWOrgJednotka.class, new EmptySpec(f, null));
    }
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v String[] ojs ( skratky ojs ) maju
     * vlastnost v int vlastnost
     */
    public static SCPWOrgJednotka[] ziskajOJSPreKtoreMameVlastnost( AisApp app, Set<String> ojs, int vlastnost ) throws NestedException
    {
        return ziskajOJSPreKtoreMameVlastnosti( app, ojs.toArray( new String[ ojs.size() ] ), new Integer[] { vlastnost } );
    }
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v String[] ojs ( skratky ojs ) maju
     * vlastnost v int vlastnost
     */
    public static SCPWOrgJednotka[] ziskajOJSPreKtoreMameVlastnost(AisApplicationContext ctx, Set<String> ojs, int vlastnost ) throws NestedException
    {
        return ziskajOJSPreKtoreMameVlastnosti(ctx, ojs.toArray( new String[ ojs.size() ] ), new Integer[] { vlastnost } );
    }

    /**
     * ziskanie organizacnych jednotiek, pre ktore nasa zadana v String oj ( skratka oj ) ma
     * vlastnost v int vlastnost
     *
     *  sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOJSPreKtoreMamVlastnost( AisApp app, String oj, int vlastnost ) throws NestedException
    {
        return ziskajOJSPreKtoreMameVlastnosti( app, new String[] { oj }, new Integer[] { vlastnost } );
    }
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v String[] ojs ( skratky ojs ) maju
     * vlastnosti v int[] vlastnosti, a su typu z pola <code>skratkyTypovOJ<code>
     *
     * sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOJSPodlaTypovSVlastnostami( AisApp app, String[] ojs,
                                                                      Integer[] vlastnosti, String[] skratkyTypovOJ ) throws NestedException  {

        Filter f = new Filter( app.getCip().getColumns( PWVlastnostOJ.class ) );
        if ( ojs != null && ojs.length > 0 )
        {
            f.addListCond( 0, false, "skratkaJePreOJ", ojs );
        }
        if ( vlastnosti != null && vlastnosti.length > 0 ) {
            f.addListCond( 0, false, "idTypVlastnostiOJ", vlastnosti );
        }

        PWVlastnostOJ[] vlastnostiOJ = ( PWVlastnostOJ[] )app.getPS().retrieve( PWVlastnostOJ.class, new EmptySpec( f, null ) );

        if ( vlastnostiOJ == null || vlastnostiOJ.length == 0 ) {
            return new SCPWOrgJednotka[ 0 ];
        }

        Set<String> oj = new HashSet<String>();
        for( PWVlastnostOJ vlastnost: vlastnostiOJ ) {
            oj.add( vlastnost.getSkratkaPoskytujeOJ() );
        }

        f = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
        f.addListCond( 0, false, "skratka", oj.toArray( new String[ oj.size() ] ) );
        if ( skratkyTypovOJ != null && skratkyTypovOJ.length > 0 ) {
            f.addListCond( 0, false, "skratkaTypOrgJednotky", skratkyTypovOJ );
        }


        return ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f, null ) );
    }
    /**
     * ziskanie organizacnych jednotiek, pre ktore nase zadane v String[] ojs ( skratky ojs ) maju
     * vlastnost v int vlastnost, a su typu z pola <code>skratkyTypovOJ<code>
     */
    public static SCPWOrgJednotka[] ziskajOJSPodlaTypovSVlastnostami( AisApp app, Set<String> ojs, int vlastnost, String[] skratkyTypovOJ ) throws NestedException
    {
        return ziskajOJSPodlaTypovSVlastnostami( app, ojs.toArray( new String[ ojs.size() ] ), new Integer[] { vlastnost }, skratkyTypovOJ );
    }


    //	----------------------------------------------------------------------------------------------------------------------------
    //	----------------------------------------------------------------------------------------------------------------------------
    //	----------------------------------------------------------------------------------------------------------------------------
    /**
     * ziskanie takych typov studia, ktore sa na danej OJ pouzivaju v ramci platnych studijnych programov
     */
    public static SCSTTypStudia[] ziskajTypyStudia( AisApp app, String orgJednotka ) throws AppException
    {
        return ziskajTypyStudia(app, orgJednotka, SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM, true );
    }
    /**
     * vyberie len take druhy studia, ktore su pouzite v platnych studijnych programoch organizacnej jednotky
     */
    public static SCSTDruhStudia[] ziskajDruhyStudia( AisApp app, String orgJednotka) throws AppException
    {
        return ziskajDruhyStudia(app, orgJednotka, SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM, true );
    }
    /**
     * vyberie cinnostiSPOJ, ktore su pouzite v platnych studijnych programoch organizacnych jednotiek
     */
    public static STCinnostSPOJ[] ziskajCinnostiSPOJ( AisApp app, Set<String> orgJednotky) throws AppException
    {
        return ziskajCinnostiSPOJ(app, orgJednotky, SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM, true );
    }
    /**
     * ziskanie takych foriem studia, ktore su pouzite v platnych studijnych programoch organizacnej jednotky
     */
    public static SCSTFormaStudia[] ziskajFormyStudia( AisApp app, String orgJednotka ) throws AppException
    {
        return ziskajFormyStudia( app, orgJednotka, SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM, true  );
    }
    /**
     * ziskanie stupnov studia pre zvolenu organizacnu jednotku
     */
    public static SCSTStupenStudia[] ziskajStupneStudia( AisApp app, String oj ) throws AppException
    {
        return ziskajStupneStudia( app, oj, SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM, true  );
    }
    /**
     * ziskanie metod studia pre zvolenu organizacnu jednotku
     */
    public static SCSTMetodaStudia[] ziskajMetodyStudia( AisApp app, String oj ) throws AppException
    {
        return ziskajMetodyStudia( app, oj, SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM, true );
    }
    /**
     * ziskanie stupnov studia pre zvolenu organizacnu jednotku
     * @sqli ok
     */
    public static SCSTStupenStudia[] ziskajStupneStudia( AisApp app, String oj, int cinnost, boolean platne ) throws AppException
    {
        if ( oj == null || oj.trim().isEmpty() ) return DlgMdl.ziskajStupneStudia( app );
        try
        {
            String s =
                    "[kod in (" +
                            " SELECT DISTINCT( kodStupenStudia )" +
                            " FROM SCSTStudProgram sp " +
                            " JOIN STCinnostSPOJ c ON ( SP.ID = c.IdStudProgram AND c.IdCinnostST = ${cinnost} " +
                            " AND c.SkratkaOrganizacnaJednotka = ${oj} )";
            if ( platne )
            {
                s += " WHERE ( sysdate BETWEEN sp.OdDatumu AND COALESCE( sp.DoDatumu, sysdate ) )";
            }
            s += ")];{kod}";
            Object[][] params = new Object[][] { { "oj", oj },{ "cinnost", cinnost } };
            return ( SCSTStupenStudia[] )app.getPS().retrieve(	SCSTStupenStudia.class,	new BarSpec( s, params ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať stupne štúdia.", e );
        }
    }
    /**
     * ziskanie stupnov studia. vsetky, platne.
     */
    public static SCSTStupenStudia[] ziskajStupneStudia( AisApp app ) throws AppException
    {
        return ziskajStupneStudia( app, true );
    }
    public static SCSTStupenStudia[] ziskajStupneStudia( AisApp app, boolean platne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCSTStupenStudia.class ) );
            if ( platne )
            {
                f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            }
            Ordering o = new Ordering();
            o.addColumnOrder( "kod", false );
            return ( SCSTStupenStudia[] )app.getPS().retrieve( SCSTStupenStudia.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať stupne štúdia.", e );
        }
    }
    // --------------------------------------------------------------------------------------------
    // DRUHY STUDIA
    /**
     * Ziska druhy studia, ktore su pouzite v platnych studijnych programoch
     * zadanej organizacnej jednotky. V pripade ze ta je null, tak vrati vsetky platne zanznamy
     * ciselnika SCSTDruhStudia.
     * @author mobrin
     */
    public static SCSTDruhStudia[] ziskajDruhyStudia( AisApp app, String oj, int cinnost, boolean platne ) throws AppException
    {
        if ( ( oj == null ) || ( oj.trim().length() == 0 ) )
            return ziskajDruhyStudia( app );
        String spec =
                "[id in (" +
                        " SELECT DISTINCT( idDruhStudia ) " +
                        " FROM SCSTStudProgram sp " +
                        " JOIN STCinnostSPOJ c ON (" +
                        " SP.ID = c.IdStudProgram " +
                        " AND c.IdCinnostST = ${cinnost} " +
                        " AND c.SkratkaOrganizacnaJednotka = ${oj} )";
        if ( platne )
            spec +=	" WHERE ( sysdate BETWEEN sp.OdDatumu AND COALESCE( sp.DoDatumu, sysdate ) )";
        spec += ")];{id}";
        try
        {
            Object[][] params = new Object[][] { { "oj", oj },{ "cinnost", cinnost } };
            return ( SCSTDruhStudia[] )app.getPS().retrieve( SCSTDruhStudia.class, new BarSpec( spec, params )	);
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať druhy štúdia.", e );
        }
    }




    // --------------------------------------------------------------------------------------------
    // CINNOSTISPOJ podla mnoziny OJ
    /**
     * Ziska cinnostiSPOJ (podla mnoziny skratiek org. jednotiek), ktore su pouzite v platnych studijnych programoch
     * zadanej organizacnej jednotky. V pripade ze tam je null, tak ..zatial nic..
     * @author m(j)obrin
     */
    public static STCinnostSPOJ[] ziskajCinnostiSPOJ( AisApp app, Set<String> oj, int cinnost, boolean platne ) throws AppException
    {
//		if ( ( oj == null ) || ( oj.isEmpty() ) )
//			return ziskajDruhyStudia( app );
        String spec =
                "studProgram(formaStudia;stupenStudia;druhStudia)" ;
//		spec += ")];{id}";
        try
        {
            Filter f = new Filter( new Columns( new Columns[] {app.getCip().getColumns( SCSTStudProgram.class ),
                    app.getCip().getColumns( STCinnostSPOJ.class )}, false ) );

            f.addListCond( 0, false, "skratkaOrganizacnaJednotka", oj.toArray( new String[ oj.size() ] ) );
            f.addEqCond("idCinnostST", cinnost);
            if ( platne )
            {
                f.addIntersectConds( 1, "OdDatumu", "DoDatumu", DateUtils.today(), DateUtils.today() );
            }
            return ( STCinnostSPOJ[] )app.getPS().retrieve( STCinnostSPOJ.class, new BarSpec(spec, f, null)	);
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať druhy štúdia.", e );
        }
    }
    /**
     * Vrati vsetky platne zaznamy ciselnika SCSTDruhStudia.
     * @author mobrin
     */
    public static SCSTDruhStudia[] ziskajDruhyStudia( AisApp app ) throws AppException
    {
        return ziskajDruhyStudia( app, true );
    }
    /**
     * Vrati vsetky zaznamy ciselnika SCSTDruhStudia.
     * @author mobrin
     */
    public static SCSTDruhStudia[] ziskajDruhyStudia( AisApp app, boolean platne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCSTDruhStudia.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "popis", false );
            return ( SCSTDruhStudia[] )app.getPS().retrieve( SCSTDruhStudia.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať druhy štúdia.", e );
        }
    }
    //--^^ DRUHY STUDIA
    // --------------------------------------------------------------------------------------------
    /**
     * Ziska typy studia, ktore sa na danej OJ pouzivaju v ramci platnych studijnych programov.
     */
    public static SCSTTypStudia[] ziskajTypyStudia( AisApp app, String oj, int cinnost, boolean platne ) throws AppException
    {
        if ( oj == null || oj.trim().isEmpty() ) return ziskajTypyStudia( app );
        try
        {
            String spec =
                    "!;kod;popis;" +
                            "[kod in ( " +
                            " SELECT DISTINCT( kodTypStudia ) " +
                            " FROM SCSTStudProgram sp " +
                            " JOIN STCinnostSPOJ c ON (" +
                            " SP.ID = c.IdStudProgram " +
                            " AND c.IdCinnostST = ${cinnost} " +
                            " AND c.SkratkaOrganizacnaJednotka = ${oj} )";
            if ( platne )
                spec +=	" WHERE ( sysdate BETWEEN sp.OdDatumu AND COALESCE( sp.DoDatumu, sysdate ) )";
            spec += ")];{kod}";
            Object[][] params = new Object[][] { { "oj", oj },{ "cinnost", cinnost } };
            return ( SCSTTypStudia[] )app.getPS().retrieve( SCSTTypStudia.class, new BarSpec( spec, params ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať typy štúdia.", e );
        }
    }
    /**
     * Ziska typy studia. Vsetky Platne
     */
    public static SCSTTypStudia[] ziskajTypyStudia( AisApp app ) throws AppException
    {
        return ziskajTypyStudia( app, true );
    }
    /**
     * Ziska typy studia. Vsetky Platne
     */
    public static SCSTTypStudia[] ziskajTypyStudia( AisApp app, boolean platne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCSTTypStudia.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "kod", false );
            return ( SCSTTypStudia[] )app.getPS().retrieve(	SCSTTypStudia.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať typy štúdia.", e );
        }
    }
    /**
     * Ziska formy studia, ktore su pouzite v platnych studijnych programoch
     * organizacnej jednotky.
     */
    public static SCSTFormaStudia[] ziskajFormyStudia( AisApp app, String oj, int cinnost, boolean platne ) throws AppException
    {
        if ( oj == null || oj.trim().isEmpty() ) return ziskajFormyStudia( app );
        String spec =
                "!;id;popis;" +
                        "[id in (" +
                        " SELECT DISTINCT( idFormaStudia ) " +
                        " FROM SCSTStudProgram sp " +
                        " JOIN STCinnostSPOJ c ON (" +
                        " SP.ID = c.IdStudProgram " +
                        " AND c.IdCinnostST = ${cinnost} " +
                        " AND c.SkratkaOrganizacnaJednotka = ${oj} )";
        if ( platne )
            spec +=	" WHERE ( sysdate BETWEEN sp.OdDatumu AND COALESCE( sp.DoDatumu, sysdate ) )";
        spec += ")];{id}";
        try
        {
            Object[][] params = new Object[][] { { "oj", oj },{ "cinnost", cinnost } };
            return ( SCSTFormaStudia[] )app.getPS().retrieve( SCSTFormaStudia.class, new BarSpec( spec, params ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať formy štúdia.", e );
        }
    }
    /**
     * Ziska formy studia. Vsetky platne.
     */
    public static SCSTFormaStudia[] ziskajFormyStudia( AisApp app ) throws AppException
    {
        return ziskajFormyStudia( app, true );
    }
    /**
     * Ziska formy studia. Vsetky, platne.
     */
    public static SCSTFormaStudia[] ziskajFormyStudia( AisApp app, boolean platne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCSTFormaStudia.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "popis",  false );
            return ( SCSTFormaStudia[] )app.getPS().retrieve( SCSTFormaStudia.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať formy štúdia.", e );
        }
    }
    /**
     * Ziskanie platnych metod studia, ktore sa pouzivaju na zadanej organizacnej jednotke.
     *
     * @param app
     * @param oj -
     *          skratka organizacnej jednotky, ku ktorej hladame pouzivane metody studia
     * @return
     * @throws AppException
     */
    public static SCSTMetodaStudia[] ziskajMetodyStudia( AisApp app, String oj, int cinnost, boolean platne ) throws AppException
    {
        String spec =
                "!;id;popis;" +
                        "[id in (" +
                        " SELECT DISTINCT( idMetodaStudia ) " +
                        " FROM SCSTStudProgram sp " +
                        " JOIN STCinnostSPOJ c ON (" +
                        " SP.ID = c.IdStudProgram " +
                        " AND c.IdCinnostST = ${cinnost} " +
                        " AND c.SkratkaOrganizacnaJednotka = ${oj} )";
        if ( platne )
            spec +=	" WHERE ( sysdate BETWEEN sp.OdDatumu AND COALESCE( sp.DoDatumu, sysdate ) )";
        spec += ")];{popis}";
        try
        {
            Object[][] params = new Object[][] { { "oj", oj },{ "cinnost", cinnost } };
            return ( SCSTMetodaStudia[] )app.getPS().retrieve( SCSTMetodaStudia.class, new BarSpec( spec, params ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa metódy výučby.", e );
        }
    }
    /**
     * Ziskanie <strong>platnych</strong> metod studia z ciselnika. Vsetkych, bez obmedzenia.
     * @author mobrin
     */
    public static SCSTMetodaStudia[] ziskajMetodyStudia(AisApp app) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCSTMetodaStudia.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "popis", false );
            return ( SCSTMetodaStudia[] )app.getPS().retrieve( SCSTMetodaStudia.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa metódy výučby.", e );
        }
    }
    /**
     * Zikanie aktualnych sposobov vyucby pouivanych na organizacnej jednotke.
     */
    public static SCSTSposVyucby[] ziskajSposobyVyucby( AisApp app, String oj ) throws AppException
    {
        String spec =
                "!;kod;popis;" +
                        "[kod IN (" +
                        " SELECT DISTINCT( kodSposVyucby ) " +
                        " FROM SCSTSposVyucbyOJ " +
                        " WHERE skratkaOrganizacnaJednotka = ${oj} )" +
                        " AND ( sysdate BETWEEN odDatumu AND COALESCE( doDatumu, sysdate ) ) ];" +
                        "{kod}";
        try
        {
            Object[][] params = new Object[][] { { "oj", oj } };
            return ( SCSTSposVyucby[] )app.getPS().retrieve( SCSTSposVyucby.class, new BarSpec( spec, params ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať spôsoby výučby.", e );
        }
    }
    public static SCSTSposVyucby[] ziskajSposobyVyucby( AisApp app, String[] kody ) throws AppException
    {
        try
        {
            if ( kody == null || kody.length == 0 ) return null;
            Filter f = new Filter( app.getCip().getColumns( SCSTSposVyucby.class ) );
            f.addListCond( 0, false, "kod", kody );
            return 	( SCSTSposVyucby[] )app.getPS().retrieve( SCSTSposVyucby.class, new EmptySpec( f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať spôsoby výučby.", e );
        }
    }
    /**
     * @sqli ok
     */
    public static SCPWOrgJednotka[] ziskajOJKtoreSpravujuProgramyZaradeneNa( AisApp app, Set<String> ojs ) throws AppException
    {
        try
        {
			/*
			 * btw. metoda robi cary mary aby nemala problem pri in podmienke s viac ako 1000
			 * zaznamami. pretoze toto v nitre urcite bude, ze na oj je zaradnych viac ako 1000
			 * studijnych programov. cize ak je ich viac ako 1000, tak sa in neurobi, ale vyberie
			 * sa fsetko a potom nasledne sa to oddeluje co nepatri.
			 */
            if ( ( ojs == null ) || ( ojs.size() == 0 ) ) return new SCPWOrgJednotka[ 0 ];
            Filter f1 = new Filter( app.getCip().getColumns( STCinnostSPOJ.class ) );
            f1.addEqCond( "idCinnostST", SCSTCinnostStudPrg.CSP_ZARADENE_NA_ODD );
            f1.addListCond( 0, false, "skratkaOrganizacnaJednotka", ojs.toArray( new String[ ojs.size() ] ) );
            STCinnostSPOJ[] data1 = ( STCinnostSPOJ[] )app.getPS().retrieve( STCinnostSPOJ.class, new EmptySpec( f1, null ) );
            if ( data1 == null || data1.length == 0 ) return new SCPWOrgJednotka[ 0 ];
            Set<Integer> ids = new HashSet<Integer>();
            for( STCinnostSPOJ c: data1 ) ids.add( c.getIdStudProgram() );
            Filter f2 = new Filter( app.getCip().getColumns( STCinnostSPOJ.class ) );
            f2.addEqCond( "idCinnostST", SCSTCinnostStudPrg.CSP_SPRAVUJE_STUDIUM );
            if ( ids.size() < 1000 )
            {
                f2.addListCond( 0, false, "idStudProgram", ids.toArray( new Integer[ ids.size() ] ) );
            }
            STCinnostSPOJ[] data2 = ( STCinnostSPOJ[] )app.getPS().retrieve( STCinnostSPOJ.class, new EmptySpec( f2, null ) );
            if ( data2 == null || data2.length == 0 ) return new SCPWOrgJednotka[ 0 ];
            Set<String> skratky = new HashSet<String>();
            for( STCinnostSPOJ c: data2 )
            {
                if ( ids.size() >= 1000 && ! ids.contains( c.getIdStudProgram() ) ) continue;
                skratky.add( c.getSkratkaOrganizacnaJednotka() );
            }
            Filter f = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
            f.addListCond( 0, false, "skratka", skratky.toArray( new String[ skratky.size() ] ) );
            return ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, new EmptySpec( f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri získavaní organizačných jednotiek.", e );
        }
    }
    /**
     * @sqli - ok
     */
    public static SCPWOrgJednotka[] ziskajOJKtoreAktualneZabezpecujuVyucbuPreOJ( AisApp app, String oj ) throws AppException
    {
        try
        {
            String spec =
                    "[skratkaPoskytujeOJ=${oj} AND idTypVlastnostiOJ=142000 " +
                            "AND ( odDatumu IS NULL OR odDatumu <= sysdate ) " +
                            "AND ( doDatumu IS NULL OR doDatumu >= sysdate ) ]";
            PWVlastnostOJ[] data =
                    ( PWVlastnostOJ[] )app.getPS().retrieve(
                            PWVlastnostOJ.class, new BarSpec( spec, new Object[][] { { "oj", oj } } ) );
            if ( data == null || data.length == 0 ) {
                return new SCPWOrgJednotka[ 0 ];
            }
            Set<String> ojs = new HashSet<String>();
            for ( PWVlastnostOJ v: data ) {
                ojs.add( v.getSkratkaJePreOJ() );
            }
            Filter f = new Filter( app.getCip().getColumns( SCPWOrgJednotka.class ) );
            f.addListCond( 0, false, "skratka", ojs.toArray( new String[ ojs.size() ] ) );
            Ordering o = new Ordering();
            o.addColumnOrder( "skratka", false );
            return
                    ( SCPWOrgJednotka[] )app.getPS().retrieve(
                            SCPWOrgJednotka.class, new BarSpec( "", f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri získavaní organizačných jednotiek.", e );
        }
    }
    /**
     * @sqli ok
     */
    public static SCSviatokKalendar[] ziskajSviatky( AisApp app ) throws AppException
    {
        try
        {
            String spec = "sviatok(typDna([pracDen = 'N']);[kodStat=703]);[datum > sysdate]";
            return ( SCSviatokKalendar[] )app.getPS().retrieve( SCSviatokKalendar.class, new BarSpec( spec ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri sviatkov.", e );
        }
    }
    // =============================================================================================
    //  * SCStat
    // =============================================================================================
    /**
     * @sqli ok
     */
    public static SCStat ziskajStat( AisApp app, String kodStat ) throws AppException
    {
        try {
            return ( SCStat )app.getPS().retrieve( SCStat.class, kodStat, new EmptySpec() );
        } catch ( PSException e ) {
            throw new AppException( 1, "Chyba pri získavaní statu z DB.", e );
        }
    }
    /**
     * @sqli ok
     */
    public static SCStat ziskajStat( AisApp app, String kodStat, String kodJazyk ) throws AppException
    {
        try
        {
            if ( kodJazyk == null || kodJazyk.trim().isEmpty() )
            {
                return ziskajStat( app, kodStat );
            }
            Object[][] p = new Object[][]{ { "j", kodJazyk } };
            return ( SCStat )app.getPS().retrieve( SCStat.class, kodStat, new BarSpec( "jazyk([kodJazyk=${j}])", p ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri získavaní statu z DB.", e );
        }
    }
    /**
     * @sqli ok
     */
    public static SCStat[] ziskajStaty( AisApp app, String kod ) throws AppException
    {
        try
        {
            Filter f =  new Filter( app.getCip().getColumns( SCStat.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            if ( ( kod != null ) && ( kod.trim().length() > 0 ) )
            {
                f.addEqCond( "kod", kod + "%" );
            }
            Ordering o = new Ordering();
            o.addColumnOrder( "kod", false );
            return ( SCStat[] )app.getPS().retrieve( SCStat.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať číselník štátov.", e );
        }
    }
    /*
     * 14.6.2001 mobrin - prepisal som coalesce
     */
    public static SCStat[] ziskajStatyPrislusnostiOJ( AisApp app ) throws AppException
    {
        try
        {
            String spec =
                    "[EXISTS( SELECT o.KodStat FROM LZOsoba o " +
                            " JOIN ESStudium s ON s.IdStudent = o.id WHERE o.KodStat = kod )" +
                            " AND ( TRUNC( sysdate ) >= odDatumu AND ( doDatumu IS NULL OR TRUNC( sysdate ) <= doDatumu  ) ) ]" +
                            ";{acronym2}";
            return ( SCStat[] )app.getPS().retrieve( SCStat.class, new BarSpec( spec ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať číselník štátov.", e );
        }
    }
    public static SCStat[] ziskajStatyUchadzacov( AisApp app ) throws AppException
    {
        try
        {
            String spec =
                    "[EXISTS( SELECT o.KodStat FROM LZOsoba o " +
                            " JOIN PKPrihlaska p ON p.IdUchadzac = o.id WHERE o.KodStat = kod )" +
                            " AND TRUNC( sysdate ) BETWEEN TRUNC( COALESCE( odDatumu, sysdate ) )" +
                            " AND TRUNC( COALESCE( doDatumu, sysdate ) )]" +
                            ";{acronym2}";
            return ( SCStat[] )app.getPS().retrieve( SCStat.class, new BarSpec( spec ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať číselník štátov.", e );
        }
    }
    public static SCStat[] ziskajStatyAdriesUchadzacov( AisApp app ) throws AppException
    {
        try
        {
            String spec =
                    "[EXISTS( SELECT a.KodStat FROM LZAdresaOsoby a " +
                            " JOIN LZOsoba o ON o.Id = a.idOsoba"+
                            " JOIN PKPrihlaska p ON p.IdUchadzac = o.id WHERE o.KodStat = kod )" +
                            " AND TRUNC( sysdate ) BETWEEN TRUNC( COALESCE( odDatumu, sysdate ) )" +
                            " AND TRUNC( COALESCE( doDatumu, sysdate ) )]" +
                            ";{acronym2}";
            return ( SCStat[] )app.getPS().retrieve( SCStat.class, new BarSpec( spec ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať číselník štátov.", e );
        }
    }
    /**
     * Ziskanie platnych zaznamov statov, ktore navyse vyhovuju zadanemu filtru,
     * ktory sa uplatnuje na kod, popis, skratPopis, acronym2 a acronym3 ako OR podmienka.
     * Vo filtri sa pocita s wildcards. Vhodne a primarne pripravene pre vyber
     * statu z ciselnika.
     * vs
     * @author obrin
     */
    public static SCStat[] ziskajStaty2(AisApp app, String search) throws AppException
    {
        try {
            String spec = "{popis};[odDatumu <= ${toDay} AND ( doDatumu IS NULL OR doDatumu >= ${toDay})";
            if ( ( search != null ) && ( !search.trim().isEmpty() ) ) {
                search = search.trim().replace( '?', '_' ).replace( '*', '%' );
                search = StringUtils.delisp( search ).toUpperCase();
                spec +=
                        " AND NLS_TOASCII(kod) LIKE ${kod} OR " +
                                "NLS_TOASCII(popis) LIKE ${popis} OR " +
                                "NLS_TOASCII(skratPopis) LIKE ${skratPopis} OR " +
                                "NLS_TOASCII(acronym2) LIKE ${acronym2} OR " +
                                "NLS_TOASCII(acronym3) LIKE ${acronym3}];";
            }
            spec += "]";
            return ( SCStat[] )app.getPS().retrieve(
                    SCStat.class, new BarSpec( spec, new Object[][] {
                            {"toDay", DateUtils.today() },
                            {"kod", search },
                            {"popis", search },
                            {"skratPopis", search },
                            {"acronym2", search },
                            {"acronym3", search } } ) );
        }
        catch (PSException e) {
            throw new AppException( 1, "Chyba pri získavaní štátov.", e  );
        }
    }
    // kopia metody vyssie s vyuzitim aplikacneho kontextu
    public static SCStat[] ziskajStaty2(AisApplicationContext aac, String search) throws AppException
    {
        try {

            String spec = "{popis};[odDatumu <= ${toDay} AND ( doDatumu IS NULL OR doDatumu >= ${toDay})";

            if ( ( search != null ) && ( !search.trim().isEmpty() ) ) {
                search = search.trim().replace( '?', '_' ).replace( '*', '%' );
                search = StringUtils.delisp( search ).toUpperCase();
                spec +=
                        " AND NLS_TOASCII(kod) LIKE ${kod} OR " +
                                "NLS_TOASCII(popis) LIKE ${popis} OR " +
                                "NLS_TOASCII(skratPopis) LIKE ${skratPopis} OR " +
                                "NLS_TOASCII(acronym2) LIKE ${acronym2} OR " +
                                "NLS_TOASCII(acronym3) LIKE ${acronym3}];";
            }

            spec += "]";

            return aac.ps.retrieve(
                    SCStat.class, new BarSpec( spec, new Object[][] {
                            {"toDay", DateUtils.today() },
                            {"kod", search },
                            {"popis", search },
                            {"skratPopis", search },
                            {"acronym2", search },
                            {"acronym3", search } } ) );

        }
        catch (PSException e) {
            throw new AppException( 1, "Chyba pri získavaní štátov.", e  );
        }
    }
    //=========================================================================
    // JAZYKY
    //=========================================================================
    public static SCJazyk ziskajJazyk( AisApp app, String kod ) throws NestedException
    {
        try
        {
            return ( SCJazyk )app.getPS().retrieve(	SCJazyk.class, kod, new EmptySpec() );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať jazyk.", e );
        }
    }
    /**
     * @sqli - ok
     */
    public static SCJazyk[] ziskajJazyky( AisApp app, boolean zostupne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCJazyk.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "kod", zostupne );
            return ( SCJazyk[] )app.getPS().retrieve( SCJazyk.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať jazyky.", e );
        }
    }

    /**
     * @sqli - ok
     */
    public static SCJazyk[] ziskajJazyky( AisApp app ) throws AppException 	{
        return ziskajJazyky( app, false );
    }
    /**
     * @sqli - ok
     */
    public static SCJazyk[] ziskajJazyky( AisApp app, Set<String> jazyky ) throws AppException
    {
        return ziskajJazyky( app, jazyky, false );
    }
    /**
     * @sqli - ok
     */
    public static SCJazyk[] ziskajJazyky( AisApp app, Set<String> jazyky, boolean nepatria )  throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCJazyk.class ) );
            f.addListCond( 0, nepatria, "kod", jazyky.toArray( new String[ jazyky.size() ] ) );
            Ordering o = new  Ordering();
            o.addColumnOrder( "kod", false );
            return ( SCJazyk[] )app.getPS().retrieve( SCJazyk.class, new EmptySpec( f, o ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri ziskavani jazykov.", e );
        }
    }
    /**
     * Získanie KÓDOV jazykov, v ktorých sa vyučuje daný predmet.
     * @author engel
     * @param AisApp app
     * @param int idPredmet
     * @param String popisAkadRok
     * @return Set<String> kodyJazykov
     * @throws AppException
     */
    public static Set<String> ziskajJazykyVKtorychSaVyucujePredmet(AisApp app, int idPredmet, String popisAkadRok) throws AppException {
        try {
            JoinTreeBuilder jtb = new JoinTreeBuilder( app.getCip() );
            JoinTreeBuilder.JTBJoin root = jtb.addRootJoin( STJazykPredmet.class );
            root.addJoin( JoinTree.INNER , "predmetRok" );
            base.Filter filter = new base.Filter(new base.Columns(new base.Columns[] {
                    app.getCip().getColumns( STPredmetRok.class )
            }, false));
            filter.addEqCond("STPredmetRok.popisAkademickyRok", popisAkadRok );
            filter.addEqCond("STPredmetRok.idPredmet", new Integer (idPredmet));
            STJazykPredmet[] jazykyPredmetu = (STJazykPredmet[])app.getPS().retrieve(
                    STJazykPredmet.class,
                    jtb.getJoinTree(),
                    0,
                    new EmptySpec(filter, null)
            );
            Set<String> kodyJazykov = new HashSet<String>();
            for (STJazykPredmet jp : jazykyPredmetu)
                kodyJazykov.add(jp.getKodJazyk());
            return kodyJazykov;
        } catch (PSException e) {
            throw new AppException(1, "Chyba pri získavaní jazykov.");
        }
    }
    /**
     * Získanie všetkých jazykov, v ktorých sa vyučuje na danej inštalácii.
     * @author engel
     * @param AisApp app
     * @return SCJazyk[]
     * @throws AppException
     */
    public static SCJazyk[] ziskajVsetkyJazykyVKtorychSaVyucujeAleboSuTexty ( AisApp app, String oj) throws AppException {
        try 	{
            String s =
                    "[kod in ( " +
                            "SELECT DISTINCT pp.KODJAZYK " +
                            "FROM  STPOPISPREDMETU pp " +
                            "JOIN STZABEZPECUJEPREDM zp on zp.IDPREDMETZABEZPECUJE = pp.IDPREDMET " +
                            "JOIN PWVLASTNOSTOJ voj ON (zp.SKRATKAZABEZPECUJE = voj.SKRATKAJEPREOJ AND voj.SKRATKAPOSKYTUJEOJ = ${oj} AND voj.IDTYPVLASTNOSTIOJ = 142000) " +
                            "UNION " +
                            "SELECT DISTINCT jp.KODJAZYK " +
                            "FROM STJAZYKPREDMET jp " +
                            "JOIN STPREDMETROK pr ON pr.id = jp.IDPREDMETROK " +
                            "JOIN STPREDMET p ON p.id = pr.IDPREDMET " +
                            "JOIN STZABEZPECUJEPREDM zp on zp.IDPREDMETZABEZPECUJE = p.ID " +
                            "JOIN PWVLASTNOSTOJ voj ON (zp.SKRATKAZABEZPECUJE = voj.SKRATKAJEPREOJ AND voj.SKRATKAPOSKYTUJEOJ = ${oj} AND voj.IDTYPVLASTNOSTIOJ = 142000) ";
            s += ")];{kod}";
            return ( SCJazyk[] )app.getPS().retrieve(SCJazyk.class,	new BarSpec( s, new Object[][] { { "oj", oj } } ) );
        }
        catch ( PSException e ) {
            throw new AppException(1, "Chyba pri získavaní jazykov.");
        }
    }


    /**
     * Získanie kódov jazykov, v ktorých existujú texty predmetu.
     * @author engel
     * @param AisApp app
     * @param int idPredmet
     * @return Set<String> kodyJazykov
     * @throws AppException
     */
    public static Set<String> ziskajJazykyVKtorychSuTextyPredmetu ( AisApp app, int idPredmet ) throws AppException {
        try 	{
            String s =
                    "[kod in (" +
                            " SELECT DISTINCT kodjazyk" +
                            " FROM STPOPISPREDMETU where idPredmet = ${idPredmet}" ;
            s += ")];{kod}";
            SCJazyk[] jazykyTextovPredmetu =
                    ( SCJazyk[] )app.getPS().retrieve(SCJazyk.class,	new BarSpec( s, new Object[][] { { "idPredmet", idPredmet } } ) );

            Set<String> kodyJazykov = new HashSet<String>();
            for (SCJazyk j : jazykyTextovPredmetu)
                kodyJazykov.add(j.getKod());
            return kodyJazykov;
        }
        catch ( PSException e ) {
            throw new AppException(1, "Chyba pri získavaní jazykov.");
        }
    }

    public static SCJazyk[] ziskajJazykyZoStProgramu( AisApp app, Integer idSP, boolean zostupne ) throws AppException {
        try {
            String spec = "";
            Object[][] params = null;

            if ( idSP != null ) {
                spec += "[kod IN ( SELECT DISTINCT kodJazykyVyucby FROM SCSTJazykVyucbyStProg WHERE idStudProgramy = ${idSP})]";
                params = new Object[][] { { "idSP", idSP } };
            }
            if ( zostupne ) {
                spec += "{-kod}";
            } else {
                spec += "{kod}";
            }

            return (SCJazyk[]) app.getPS().retrieve( SCJazyk.class, new BarSpec( spec, params ) );
        } catch ( PSException e ) {
            throw new AppException( 1, "Nepodarilo sa načítať jazyky.", e );
        }
    }

    public static SCJazyk[] ziskajJazykyZoStProgramuStudii( AisApp app, Integer[] idStudii, boolean zostupne ) throws AppException {
        try {
            String spec = "";

            if ( idStudii != null && idStudii.length > 0) {
                spec += "[kod IN ( SELECT DISTINCT jvsp.kodJazykyVyucby "
                        + "FROM SCSTJazykVyucbyStProg jvsp "
                        + "JOIN ESStudium s ON s.idStudijnyProgram = jvsp.idStudProgramy "
                        + "WHERE s.id IN ( " + in( idStudii )+ " ) )]";
            }

            if ( zostupne ) {
                spec += "{-kod}";
            } else {
                spec += "{kod}";
            }

            return (SCJazyk[]) app.getPS().retrieve( SCJazyk.class, new BarSpec( spec ) );
        } catch ( PSException e ) {
            throw new AppException( 1, "Nepodarilo sa načítať jazyky.", e );
        }
    }
    // ----------------------------------------------------------------------------------
    // PARAMETRE SYSTEMU
    // ----------------------------------------------------------------------------------
    public static PSParamSysOJ[] ziskajParametre( AisApp app, String oj, String kodAplikacia ) throws AppException
    {
        return ziskajParametre( app, new String[] { oj }, null, kodAplikacia );
    }
    public static PSParamSysOJ[] ziskajParametre( AisApp app, String[] ojs, Integer idParamater ) throws AppException
    {
        return ziskajParametre( app, ojs, idParamater, null );
    };
    public static PSParamSysOJ[] ziskajParametre( AisApp app, String[] ojs, Integer idParamater, String kodAplikacia ) throws AppException
    {
        try
        {
            String spec = "paramSys()";
            Filter f =
                    new Filter( new Columns( new Columns[] {
                            app.getCip().getColumns( PSParamSysOJ.class ),
                            app.getCip().getColumns( SCParamSystem.class )}, false ) );
            if ( ( ojs != null ) && ( ojs.length > 0 ) )
                f.addListCond( 0, false, "skratkaOrganizacnaJednotka", ojs );
            if ( idParamater != null )
                f.addEqCond( "idParamSys", idParamater );
            if ( kodAplikacia != null )
                f.addEqCond( "kodAplikacia", kodAplikacia );
            return ( PSParamSysOJ[] )app.getPS().retrieve( PSParamSysOJ.class,	new BarSpec( spec, f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať parametre.", e );
        }
    }
    public static PSParamSysOJ[] ziskajParametre( AisApp app, String oj, Integer idParamater, String typParametra, boolean lenPlatne ) throws AppException
    {
        try
        {
            String spec = "paramSys(typParam());organizacnaJednotka()";
            Filter f =
                    new Filter( new Columns( new Columns[] {
                            app.getCip().getColumns( PSParamSysOJ.class ),
                            app.getCip().getColumns( SCParamSystem.class )}, false ) );
            if ( oj != null )
                f.addEqCond( "skratkaOrganizacnaJednotka", oj );
            if ( idParamater != null )
                f.addEqCond( "idParamSys", idParamater );
            if ( typParametra != null && typParametra.trim().length() > 0 )
                f.addEqCond( "idTypParam", typParametra );

            if ( lenPlatne )
                f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            return ( PSParamSysOJ[] )app.getPS().retrieve( PSParamSysOJ.class,	new BarSpec( spec, f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať parametre.", e );
        }
    }
    public static SCParamSystem[] ziskajParametre( AisApp app, Integer filterId, boolean lenPlatne ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCParamSystem.class ) );
            if ( lenPlatne )
            {
                f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            }
            if ( filterId != null )
            {
                f.addEqCond( "id", filterId );
            }
            return ( SCParamSystem[] )app.getPS().retrieve( SCParamSystem.class, new BarSpec( "typParam()", f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa načítať parametre.", e );
        }
    }
    public static MSMessage[] ziskajSpravyPouzivatela(AisApp app, int idPouzivatel) throws AppException
    {
        return ziskajSpravyPouzivatela(app.getPS(), idPouzivatel);
    }
    public static MSMessage[] ziskajSpravyPouzivatela(ReadOnlyPersistentService ps, int idPouzivatel ) throws AppException
    {
        return ziskajSpravyPouzivatela(ps, idPouzivatel, false );		// vyberieme vsetky spravy, precitane aj neprecitane
    }

    /**
     *
     * @param ps
     * @param idPouzivatel
     * @param ibaNeprecitane		pre mobilnu apku - ak true, tak vyberieme len neprecitane spravy
     * @return
     * @throws AppException
     */
    public static MSMessage[] ziskajSpravyPouzivatela(ReadOnlyPersistentService ps, int idPouzivatel, boolean ibaNeprecitane ) throws AppException
    {
        try
        {
            String s =
                    "aplikacia();parametre();" +
                            "pouzivatelia([idPouzivatelia = ${logged_id}]);" +
                            "[id IN (SELECT idSpravy FROM MSMessagePouzivatela WHERE idPouzivatelia = ${idPouzivatel}" + (ibaNeprecitane ? " AND zobrazena = 'N'" : "") + ")" +
                            "AND (TRUNC(sysdate) BETWEEN TRUNC(COALESCE(platnostOd, sysdate)) AND TRUNC(COALESCE(platnostDo, sysdate)))" +
                            "]" +
                            ";{-pridane}";
            Object[][] p = new Object[][] {{"logged_id", idPouzivatel}, {"idPouzivatel", idPouzivatel}};
            return (MSMessage[])ps.retrieve(MSMessage.class, new BarSpec(s, p, true));
        }
        catch (PSException e) {
            throw new AppException(1, "Nepodarilo sa získať správy používateľa.", e);
        }
    }

    /**
     *
     * @param ps
     * @param idPouzivatel
     * @param idSprava
     * @return
     * @throws AppException
     */
    public static MSMessage ziskajSpravuPouzivatela(ReadOnlyPersistentService ps, int idPouzivatel, int idSprava) throws AppException
    {
        try {
            String spec = "[id IN (SELECT idSpravy FROM MSMessagePouzivatela WHERE idPouzivatelia = ${idPouzivatel})]";
            Object[][] param = new Object[][] {{"idPouzivatel", idPouzivatel}};

            return (MSMessage)ps.retrieve(MSMessage.class, idSprava, new BarSpec(spec, param, true));
        }
        catch (PSException e) {
            throw new AppException(1, "Nepodarilo sa získať správu používateľa.", e);
        }
    }
    public static MSMessagePouzivatela[] ziskajSpravyPouzivatela(AisApp app, int idPouzivatel, String zobrazena, String kodAplikacia, String spec ) throws AppException {
        return ziskajSpravyPouzivatela(app.getPS(), idPouzivatel, zobrazena, kodAplikacia, spec );
    }


    public static MSMessagePouzivatela[] ziskajSpravyPouzivatela(ReadOnlyPersistentService ps, int idPouzivatel, String zobrazena, String kodAplikacia, String spec ) throws AppException
    {
        try
        {

            Filter f =
                    new Filter( new Columns( new Columns[] {
                            ps.getCiProvider().getColumns( MSMessagePouzivatela.class ),
                            ps.getCiProvider().getColumns( MSMessage.class ) }, false ) );
            f.addIntersectConds( 1, "platnostOd", "platnostDo", DateUtils.today(), DateUtils.today() );
            f.addEqCond("idPouzivatelia", idPouzivatel);

            if (zobrazena != null && zobrazena.length() > 0 ) {
                f.addEqCond("zobrazena", zobrazena);
            }
            if (kodAplikacia != null && kodAplikacia.length() > 0) {
                f.addEqCond("kodAplikacia", kodAplikacia);
            }

            return (MSMessagePouzivatela[])ps.retrieve(MSMessagePouzivatela.class, new BarSpec(spec, f, null));
        }
        catch (PSException e) {
            throw new AppException(1, "Nepodarilo sa získať správy používateľa.", e);
        }
    }
    public static SCObec[] ziskajObce( AisApp app, String maska ) throws NestedException
    {
        try
        {
            String spec = "okres();stat();";
            Filter f = new Filter( app.getCip().getColumns( SCObec.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "popis", false );
            if ( maska != null && ! maska.trim().isEmpty() )
            {
                Set<SCObec> obce = new TreeSet<SCObec>( new Comparator<SCObec>()
                {
                    @Override
                    public int compare(SCObec o1, SCObec o2)
                    {
                        if ( o1 == null || o2 == null || o1.getPopis() ==  null || o2.getPopis() == null )
                        {
                            return 0;
                        }
                        //uloha 13907 - tie + o?.getId() preto, lebo obce s rovnakym nazvom povazoval za tu istu a vratil mi len jednu.
                        return ( o1.getPopis() + o1.getId() ).compareTo( o2.getPopis() + o2.getId() );
                    }
                });
                Filter f2 = new Filter( app.getCip().getColumns( SCObec.class ) );
                f2.setNlsToAscii( true );
                String psc = maska.replace( " ", "" ).toUpperCase() + '%';
                f2.addEqCond( "psc", psc );
                f2.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
                SCObec[] data2 = ( SCObec[] )app.getPS().retrieve( SCObec.class, new BarSpec( spec, f2, o )	);
                if ( data2 != null ) for( SCObec ob: data2 ) obce.add( ob );
                Filter f3 = new Filter( app.getCip().getColumns( SCObec.class ) );
                f3.setNlsToAscii( true );
                String popis = maska.trim() + "%";
                f3.addEqCond( "popis", popis );
                f3.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
                SCObec[] data3 = ( SCObec[] )app.getPS().retrieve( SCObec.class, new BarSpec( spec, f3, o )	);
                if ( data3 != null ) for( SCObec ob: data3 ) obce.add( ob );
                return obce.toArray( new SCObec[ obce.size() ] );
            }
            else
            {
                return ( SCObec[] )app.getPS().retrieve( SCObec.class, new BarSpec( spec, f, o ) );
            }
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať obce.", e );
        }
    }
    public static SCOkres[] ziskajOkresy( AisApp app ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCOkres.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            return ( SCOkres[] )app.getPS().retrieve( SCOkres.class, new EmptySpec( f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri získavaní okresov.", e  );
        }
    }
    public static SCObec ziskajObec( AisApp app, int idObec ) throws NestedException
    {
        return ziskajObec(app.getAK(), idObec);
    }
    public static SCObec ziskajObec( AisApplicationContext aac, int idObec ) throws NestedException
    {
        try
        {
            return ( SCObec ) aac.ps.retrieve( SCObec.class, idObec, new BarSpec( "okres()" ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získavať obec z DB.", e );
        }
    }
    public static CRSC_Obec ziskajObecCRS( AisApplicationContext aac, String kod, String spec, String kodJazyk ) throws NestedException {

        AisApplicationContext ak = aac;
        if ( kodJazyk != null) {
            ak = new ais.sys.AisApplicationContext( aac, kodJazyk );
        }

        try {
            return ( CRSC_Obec ) ak.ps.retrieve( CRSC_Obec.class, kod, new BarSpec(spec) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať CRŠ obec z DB.", e );
        }
    }
    /**
     * Ziskanie mesiacov. Len platnych mesiacov :)
     */
    public static SCMesiac[] ziskajMesiace( AisApp app ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( SCMesiac.class ) );
            f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );
            Ordering o = new Ordering();
            o.addColumnOrder( "cisloMesiaca", false );
            return ( SCMesiac[] )app.getPS().retrieve( SCMesiac.class, new EmptySpec( f, o ) );
        }
        catch( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa ziskat mesiace.", e );
        }
    }
    /**
     * Konverzia SK<=>EUR
     * @author tino
     */
    public static BigDecimal konvertujMenu( AisApp app, BigDecimal suma, boolean naEuro )
    {
        if ( suma == null )	return null;
        if ( naEuro ) return new BigDecimal ( suma.doubleValue() / KURZ_EURA );
        return new BigDecimal ( suma.doubleValue() * KURZ_EURA );
    }
    /**
     * sqli ok
     */
    public static SCPWOrgJednotka[] ziskajStrediskaVykonu( AisApp app, String oj, Date odDatum, Date doDatum ) throws AppException
    {
        try
        {
			/*
			 * hladam prienik
			 */
            String spec =
                    "[skratka IN(" +
                            "\nSELECT distinct v.SKRATKAPOSKYTUJEOJ" +
                            "\nFROM PWVLASTNOSTOJ v" +
                            "\nJOIN PWVLASTNOSTOJ v1 on v.SKRATKAPOSKYTUJEOJ = v1.SKRATKAJEPREOJ" +
                            "\nWHERE v.IDTYPVLASTNOSTIOJ = 142007" +
                            "\nAND v1.IDTYPVLASTNOSTIOJ = 142006" +
                            "\nAND v1.SKRATKAPOSKYTUJEOJ  = ${oj}" +
                            "\nAND NOT ( v.OdDatumu IS NOT NULL AND TO_CHAR( v.OdDatumu, 'YYYY-MM-DD' ) > ${doDatum} )" +
                            "\nAND NOT ( v.DoDatumu IS NOT NULL AND TO_CHAR( v.DoDatumu, 'YYYY-MM-DD' ) < ${odDatum} )" +
                            "\nAND NOT ( v1.OdDatumu IS NOT NULL AND TO_CHAR( v1.OdDatumu, 'YYYY-MM-DD' ) > ${doDatum} )" +
                            "\nAND NOT ( v1.DoDatumu IS NOT NULL AND TO_CHAR( v1.DoDatumu, 'YYYY-MM-DD' ) < ${odDatum} )" +
                            ")];" +
                            "{skratka}";

            BarSpec bs =
                    new BarSpec( spec, new Object[][] {
                            { "oj", oj },
                            { "odDatum", DateTimeFormater.getDateEn( odDatum ) },
                            { "doDatum", DateTimeFormater.getDateEn( doDatum ) }
                    } );
            return ( SCPWOrgJednotka[] )app.getPS().retrieve( SCPWOrgJednotka.class, bs );
        }
        catch( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať strediská výkonu.", e );
        }
    }
    public static SCTypParamSystem[] ziskajTypParametra( AisApp app ) throws PSException
    {
        return ( SCTypParamSystem[] )app.getPS().retrieve( SCTypParamSystem.class, new EmptySpec() );
    }

    public static double skk2eur( double value )
    {
        return value / KURZ_EURA;
    }
    /**
     * Pokusi sa premenit string na Integer. Ak sa nepodari, vrati null.
     */
    public static Integer toInteger( String s )
    {
        try
        {
            return Integer.parseInt( s );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }
    /**
     * Ak je retazec null alebo prazdny vrati null, inak vrati povodny retazec.
     */
    public static String nullIfEmpty( String s )
    {
        if ( s == null || s.trim().length() == 0 )
            return null;
        return s;
    }
    /**
     * Ak je retazec null vrati prazdny retazec, inac povodny.
     */
    public static  String emptyIfNull ( String s ) {
        return s == null?"":s.trim();
    }
    /**
     * sqli ok
     */
    public static SCDenVTyzdni[] ziskajDniVTyzdni( AisApp app ) throws AppException
    {
        try
        {
            return ( SCDenVTyzdni[] )app.getPS().retrieve( SCDenVTyzdni.class, new EmptySpec() );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Chyba pri získavaní dni v týždni." );
        }
    }
    /**
     * @sqli - ok
     */
    public static SCTypDatumAkcia[] ziskajTypyDatumovychAkcii( AisApp app ) throws AppException
    {
        try
        {
            return ( SCTypDatumAkcia[] )app.getPS().retrieve( SCTypDatumAkcia.class, new EmptySpec() );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať typy dátumových akcií." );
        }
    }
    /**
     * @sqli ok
     */
    public static SCPWOrgJednotka ziskajAkreditujucuOJ( AisApp app, int idStudijnyProgram ) throws NestedException
    {
        return ziskajAkreditujucuOJ( app, idStudijnyProgram, null );
    }
    public static SCPWOrgJednotka ziskajAkreditujucuOJ( AisApplicationContext aac, int idStudijnyProgram ) throws NestedException
    {
        return ziskajAkreditujucuOJ( aac, idStudijnyProgram, null );
    }
    public static SCPWOrgJednotka ziskajRiadiacuOJ( AisApp app, int idStudijnyProgram ) throws NestedException
    {
        return ziskajAkreditujucuOJ( app, idStudijnyProgram, null );
    }
    public static SCPWOrgJednotka ziskajAkreditujucuOJ( AisApp app, int idStudijnyProgram, String spec ) throws NestedException
    {
        return ziskajAkreditujucuOJ( app, idStudijnyProgram, spec, null );
    }
    public static SCPWOrgJednotka ziskajAkreditujucuOJ( AisApplicationContext aac, int idStudijnyProgram, String spec ) throws NestedException
    {
        return ziskajAkreditujucuOJ( aac, idStudijnyProgram, spec, null );
    }
    /**
     * sqli - ok
     */
    public static SCPWOrgJednotka ziskajAkreditujucuOJ( AisApp app, int idStudijnyProgram, String spec, String kodJazyk ) throws NestedException
    {
        return ziskajAkreditujucuOJ(app.getAK(), idStudijnyProgram, spec, kodJazyk);
    }
    public static SCPWOrgJednotka ziskajAkreditujucuOJ( AisApplicationContext aac, int idStudijnyProgram, String spec, String kodJazyk ) throws NestedException
    {
        SCPWOrgJednotka[] data = ziskajAkreditujuceOJ( aac, idStudijnyProgram, spec, kodJazyk );
        if ( ( data == null ) || ( data.length == 0 ) ) {
            throw new AisAppException(
                    AisAppException.NEDEFINOVANA_AKREDITUJUCA_OJ,
                    "K študijnému programu sa nenašla riadiaca organizačná jednotka." );
        }
        return data[ 0 ];
    }
    public static SCPWOrgJednotka ziskajMiestoRealizacieOJ( AisApplicationContext aac, int idStudijnyProgram, String spec, String kodJazyk ) throws NestedException
    {
        SCPWOrgJednotka[] data = ziskajOJPodlaCinnosti( aac, idStudijnyProgram, SCSTCinnostStudPrg.CSP_MIESTO_REALIZACIE_SP, spec, kodJazyk );
        if ( ( data == null ) || ( data.length == 0 ) ) {
            throw new AisAppException(1, "K študijnému programu sa nenašlo miesto realizácie." );
        }
        return data[0];
    }
    public static SCPWOrgJednotka[] ziskajAkreditujuceOJ( AisApp app, int idStudijnyProgram, String spec ) throws AppException
    {
        return ziskajAkreditujuceOJ( app.getAKwithTempDir(), idStudijnyProgram, spec, null );
    }

    public static SCPWOrgJednotka ziskajAkreditujucuOj(AisApplicationContext aac, int idStudijnyProgram) throws AppException
    {
        SCPWOrgJednotka[] ojs = ziskajAkreditujuceOJ(aac, idStudijnyProgram, null, null);
        if (ojs != null && ojs.length > 0) {
            return ojs[0];
        }
        return null;
    }

    public static SCPWOrgJednotka[] ziskajAkreditujuceOJ( AisApplicationContext aac, int idStudijnyProgram, String spec, String kodJazyk ) throws AppException
    {
        return ziskajOJPodlaCinnosti(aac, idStudijnyProgram, SCSTCinnostStudPrg.CSP_AKREDITUJE, spec, kodJazyk);
    }
    public static SCPWOrgJednotka[] ziskajOJPodlaCinnosti( AisApplicationContext aac, int idStudijnyProgram, int idCinnost, String spec, String kodJazyk ) throws AppException
    {
        AisApplicationContext ak = aac;
        if ( kodJazyk != null) {
            ak = new ais.sys.AisApplicationContext( aac, kodJazyk );
        }

        ReadOnlyPersistentService ps = ak.ps;

        try
        {
            Filter f = new Filter( aac.pci.getColumns( STCinnostSPOJ.class ) );
            f.addEqCond( "idCinnostST", idCinnost );
            f.addEqCond( "idStudProgram", idStudijnyProgram );

            STCinnostSPOJ[] data =
                    ( STCinnostSPOJ[] ) ps.retrieve(
                            STCinnostSPOJ.class, new EmptySpec( f, null ) );

            if ( data == null || data.length == 0 )
                return new SCPWOrgJednotka[ 0 ];

            Set<String> set = new HashSet<String>();
            for ( STCinnostSPOJ c: data )
                set.add( c.getSkratkaOrganizacnaJednotka() );

            f = new Filter( ak.pci.getColumns( SCPWOrgJednotka.class ) );
            f.addListCond( 0, false, "skratka", set.toArray( new String[ set.size() ] ) );
            //f.addIntersectConds( 1, "odDatumu", "doDatumu", DateUtils.today(), DateUtils.today() );

            return
                    ( SCPWOrgJednotka[] ) ps.retrieve(
                            SCPWOrgJednotka.class, new BarSpec( spec == null ? "" : spec, f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať akreditujúce organizačné jednotky.", e );
        }
    }
    /**
     * @sqli ok
     */
    public static Attribute[] ziskajAtributy( AisApp app, Integer[] ids ) throws AppException
    {
        try
        {
            Filter f = new Filter( app.getCip().getColumns( Attribute.class ) );
            f.addListCond( 0, false, "id", ids );
            return ( Attribute[] )app.getPS().retrieve(	Attribute.class, new EmptySpec( f, null ) );
        }
        catch ( PSException e )
        {
            throw new AppException( 1, "Nepodarilo sa získať atributy." );
        }
    }
    public static CRSC_Staty ziskajStatCRS( AisApp app, int kodStat ) throws AppException {
        try {
            return ( CRSC_Staty )app.getPS().retrieve( CRSC_Staty.class, kodStat, new EmptySpec()  );
        } catch ( PSException e ) {
            throw new AppException( 1, "Chyba pri získavaní štátov.", e  );
        }
    }
    public static CRSC_Staty[] ziskajStatyCRS( AisApp app, String skratkaAleboNazov ) throws AppException {
        try {
            String spec = "{nazov};[platnost_Od <= ${toDay} AND ( platnost_Do IS NULL OR platnost_Do >= ${toDay})";
            if ( ( skratkaAleboNazov != null ) && ( !skratkaAleboNazov.trim().isEmpty() ) ) {
                skratkaAleboNazov = skratkaAleboNazov.trim().replace( '?', '_' ).replace( '*', '%' );
                skratkaAleboNazov = StringUtils.delisp( skratkaAleboNazov ).toUpperCase() + "%";
                spec +=
                        " AND ( NLS_TOASCII( ISO2 ) LIKE ${skratka} OR " +
                                "NLS_TOASCII( nazov ) LIKE ${nazov} )];";
            }
            spec += "]";
            return ( CRSC_Staty[] )app.getPS().retrieve(
                    CRSC_Staty.class, new BarSpec( spec, new Object[][] {
                            { "toDay", DateUtils.today() },
                            { "skratka", skratkaAleboNazov },
                            { "nazov", skratkaAleboNazov } } ) );
        } catch ( PSException e ) {
            throw new AppException( 1, "Chyba pri získavaní štátov.", e  );
        }
    }
    // kopia metody vyssie s vyuzitim aplikacneho kontextu
    public static CRSC_Staty[] ziskajStatyCRS( AisApplicationContext aac, String skratkaAleboNazov ) throws AppException {
        try {

            String spec = "{nazov};[platnost_Od <= ${toDay} AND ( platnost_Do IS NULL OR platnost_Do >= ${toDay})";

            if ( ( skratkaAleboNazov != null ) && ( !skratkaAleboNazov.trim().isEmpty() ) ) {
                skratkaAleboNazov = skratkaAleboNazov.trim().replace( '?', '_' ).replace( '*', '%' );
                skratkaAleboNazov = StringUtils.delisp( skratkaAleboNazov ).toUpperCase() + "%";
                spec +=
                        " AND ( NLS_TOASCII( ISO2 ) LIKE ${skratka} OR " +
                                "NLS_TOASCII( nazov ) LIKE ${nazov} )];";
            }

            spec += "]";

            return aac.ps.retrieve(
                    CRSC_Staty.class, new BarSpec( spec, new Object[][] {
                            { "toDay", DateUtils.today() },
                            { "skratka", skratkaAleboNazov },
                            { "nazov", skratkaAleboNazov } } ) );

        } catch ( PSException e ) {
            throw new AppException( 1, "Chyba pri získavaní štátov.", e  );
        }
    }
    public static CRSC_Obec[] ziskajObceCRS( AisApp app, String kodAleboNazov, String kodStat ) throws AppException {
        try {
            String spec = "{nazov};[platneOd <= ${toDay} AND ( platneDo IS NULL OR platneDo >= ${toDay})";

//TODO nemozem si byt isty, ze su vsade doplnene nuly, tak trimujem
            if ( ( kodStat != null ) && ( !kodStat.trim().isEmpty() ) ) {
                spec +=	" AND trim(leading '0' from kodStat) = trim(leading '0' from ${kodStat})";
            }

            if ( ( kodAleboNazov != null ) && ( !kodAleboNazov.trim().isEmpty() ) ) {
                kodAleboNazov = kodAleboNazov.trim().replace( '?', '_' ).replace( '*', '%' );
                kodAleboNazov = StringUtils.delisp( kodAleboNazov ).toUpperCase() + "%";
                spec +=
                        " AND ( NLS_TOASCII( kod ) LIKE ${ko" +
                                "d} OR " +
                                "NLS_TOASCII( nazov ) LIKE ${nazov} ) ";
            }
            spec += "]";
            return ( CRSC_Obec[] )app.getPS().retrieve(
                    CRSC_Obec.class, new BarSpec( spec, new Object[][] {
                            { "toDay", DateUtils.today() },
                            { "kodStat", kodStat },
                            { "kod", kodAleboNazov },
                            { "nazov", kodAleboNazov } } ) );
        } catch ( PSException e ) {
            throw new AppException( 1, "Chyba pri získavaní obci.", e  );
        }
    }


    public static PZZostava ziskajZostavu( AisApp app, int idZostava ) throws AppException {
        try {
            return ( PZZostava )app.getPS().retrieve( PZZostava.class, idZostava, new EmptySpec()  );
        } catch ( PSException e ) {
            throw new AppException( 1, "Chyba pri získavaní zostáv.", e  );
        }
    }

    public static String ziskajJazykZPodporovanychJazykov( AisDlg dlg ) throws NestedException {

        String kodJazyk = "";

        Set<String> s = Globals.getSupportedLangauages();
        if ( s.size() > 0 ) {
            SCJazyk[] data = DlgMdl.ziskajJazyky( dlg.getAisApp(), s );
            AVCAbstractArrayListModel listModel = JazykyListModel.getInstance( data, false );
            String selectedId =
                    dlg.getAisApp().getLang() != null ? dlg.getAisApp().getLang() :
                            Globals.getAisProperty( "db.defaultLanguage", "SK" );
            CM009_VyberMoznostiDlg dialogJazykov =
                    new CM009_VyberMoznostiDlg(
                            dlg, dlg.getAisApp().translation( "JAZYK", "Jazyk" ), dlg.getAisApp().translation( "JAZYK", "Jazyk" ),
                            listModel, selectedId );
            try {
                if ( dialogJazykov.initialize() ) {
                    dialogJazykov.showModal();
                    if ( dialogJazykov.getModalResult() == AisDlg.MR_OK ) {
                        kodJazyk = dialogJazykov.getSelectedId();
                        return kodJazyk;
                    } else
                        return null;
                }
            } finally {
                dialogJazykov.cleanUp();
            }
        }

        return ( kodJazyk != null ? kodJazyk.trim() : null );
    }


    /**
     * metoda pre tlac do zadaneho formatu
     *
     * @param templateName
     * @param xml - xml v stringovom formate
     * @param format - html, pdf, ...
     * @param kodJazyk
     * @return vracia relativnu cestu k temporarnemu suboru vysledku transformacie
     * @throws NestedException
     */
    public static String print( String templateName, String xml, String format, String kodJazyk ) throws NestedException {
        try {

            String menoSabl = templateName + "_" + kodJazyk;
            String outputFileName = PrintUtils.createFileName( System.currentTimeMillis() + "." + format );;
            String templateFileName = "ais/print/" + format + "/" + menoSabl+ "_" + format + ".xsl";
            TransformationService ts = new TransformationService( xml, outputFileName, templateFileName, null, null );
            ts.transform();
            return outputFileName;
        } catch ( Exception e ) {
            if ( e instanceof RuntimeException )
                throw (RuntimeException) e;
            if ( e instanceof AVCException
                    && ( (AVCException) e ).getCode() == AVCException.ERR_ABORT )
                throw ( (AVCException) e );
            throw new AVCException( 0, "Chyba pri tlači", "Chyba pri tlači", e );
        }
    }

    /**
     *
     * @param kodZostavy - primarny kluc z tabulky PZZostava
     * @param formatSuboru - format vysledneho suboru(html, pdf, ...)
     *
     *
     * metoda vracia nazov pdf suboru vytvoreneho s vyuzitim pouzivatelskej sablony k prikazu na uhradu,
     * samozrejme len ak existuje a je prave jedna.
     * Inak sa pouzije na transformaciu vzorova sablona prikazu na uhradu
     *
     *
     *
     */

    public static String saveToFileAndGetFileNameUsingSelectedTemplate( AisApplicationContext aac, String xml,
                                                                        String kodZostavy, String formatSuboru, String lang ) throws NestedException {
        if ( xml == null || xml.trim().length() == 0
                || kodZostavy == null|| kodZostavy.trim().length() == 0 ) {
            return null;
        }
        SluzbaBD sbd = new SluzbaBD(aac);
        TemplatePZ[] pristupneSablony = null;
        try {
            pristupneSablony = sbd.ziskajSablony(kodZostavy,
                    new ais.bo.SluzbaBD.TemplateChooserBezPravSpravcuAPrezeraca(), lang, true);
        } catch (AppException ae) {
            if ( ae.getId() == 1250106 )
                pristupneSablony = sbd.ziskajSablony(kodZostavy,
                        new ais.bo.SluzbaBD.TemplateChooserBezPravSpravcuAPrezeraca(), "SK", true);
            else
                throw new IllegalStateException("Nenašiel som vyhovujúcu šablónu pre tlač.");

        }
        if (pristupneSablony == null || pristupneSablony.length == 0) {
            throw new IllegalStateException("Nenašiel som vyhovujúcu šablónu pre tlač.");
        }
        if (pristupneSablony != null && pristupneSablony.length > 1) {
            throw new IllegalStateException("K dispozícii je viac používateľských šablón, nutné je mať práve jednu.");
        }
        TemplatePZ templatePZ = null;
        // ked pristupneSablony.length == 1, znamena to, ze je to OK, teda mame bud len vzorovu sablonu,
        // alebo 1 pouzivatelsku - aprave tuto pouzijeme
        if (pristupneSablony != null && pristupneSablony.length == 1 ) {
            templatePZ = pristupneSablony[0];
        }
        if (templatePZ == null) {
            // prelozi klient
            throw new IllegalStateException("Nenašiel som vyhovujúcu šablónu pre tlač.");
        }
        String fileName = System.currentTimeMillis() + formatSuboru;
        String absolutFileName = PrintUtils.createFileName(fileName);
        TransformationService ts = new TransformationService(xml, absolutFileName, templatePZ.getKodZostava(), templatePZ.getTemplateStream(), null);
        try {
            ts.transform();
        }
        catch (Exception e) {
            throw new AppException(0, "Transformácia dat zlyhala.", e);
        }
        return absolutFileName;
    }




}
