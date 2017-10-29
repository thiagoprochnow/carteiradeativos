package br.com.guiainvestimento.common;

/* Class to br used as a constants repository.
Here, we'll create static public sub-classes to properly separate
each groups of similar/related constants.
 */

public final class Constants {

    // Constants used to pass extras to Intents
    public static class Extra {
        public static final String EXTRA_PRODUCT_TYPE = "extra_product_type";
        public static final String EXTRA_PRODUCT_STATUS = "extra_product_status";
        public static final String EXTRA_PRODUCT_SYMBOL = "extra_product_symbol";
        public static final String EXTRA_INCOME_TYPE = "extra_income_type";
        public static final String EXTRA_INCOME_ID = "extra_income_id";
        public static final String EXTRA_TRANSACTION_ID = "extra_transaction_id";
    }

    // To start activitys for result
    public static class Intent{
        public static final int IMPORT_DB = 100;
        public static final int GET_DRIVE_FILE = 101;
        public static final int DRIVE_CONNECTION_RESOLUTION = 102;
    }

    // Value of stock/fii type, buy, sell, bonification, grouping, split
    public static class Type {
        public static final int INVALID = -1;
        public static final int BUY = 0;
        public static final int SELL = 1;
        public static final int BONIFICATION = 2;
        public static final int GROUPING = 3;
        public static final int SPLIT = 4;
        public static final int EDIT = 5;
        public static final int DELETE_TRANSACION = 6;
        public static final int EDIT_TRANSACION = 7;
    }

    // Value id for the clicked item in the data adapters
    public static class AdapterClickable{
        public static final int INVALID = -1;
        public static final int MAIN = 0;
        public static final int ADD = 1;
        public static final int EDIT = 2;
        public static final int SELL = 3;
        public static final int DELETE = 4;
    }

    // This should contains all product types in the portfolio
    public static class ProductType {
        public static final int INVALID = -1;
        public static final int STOCK = 0;
        public static final int FII = 1;
        public static final int CURRENCY = 2;
        public static final int FIXED = 3;
        public static final int TREASURY = 4;
        public static final int OTHERS = 5;
        public static final int PORTFOLIO = 6;
    }

    // This should contains all incomes types in the portfolio
    public static class IncomeType {
        public static final int INVALID = -1;
        public static final int DIVIDEND = 0;
        public static final int JCP = 1;
        public static final int BONIFICATION = 2;
        public static final int GROUPING = 3;
        public static final int SPLIT = 4;
        public static final int FII = 5;
        public static final int FIXED = 6;
        public static final int TREASURY = 7;
        public static final int OTHERS = 8;
    }

    // Status of the a specific investment
    // If Active, means user still is in that investment
    // If Sold, means user already sold or expired that investment
    // For Stocks and FIIs will be used to know if user still has that stock in his portfolio
    // For other the same, will be used to know if it is a past investment or current.
    public static class Status{
        public static final int INVALID = -1;
        public static final int ACTIVE = 0;
        public static final int SOLD = 1;
    }

    // Constant for calling BroadcastReceivers
    public static class Receiver{
        public static final String STOCK = "UPDATE_STOCK_PORTFOLIO";
        public static final String FII = "UPDATE_FII_PORTFOLIO";
        public static final String CURRENCY = "UPDATE_CURRENCY_PORTFOLIO";
        public static final String FIXED = "UPDATE_FIXED_PORTFOLIO";
        public static final String TREASURY = "UPDATE_TREASURY_PORTFOLIO";
        public static final String PORTFOLIO = "UPDATE_PORTFOLIO";
        public static final String OTHERS = "UPDATE_OTHERS_PORTFOLIO";
    }

    // FixedIncome Types
    public static class FixedType{
        public static final int INVALID = -1;
        public static final int CDB = 0;
        public static final int LCI = 1;
        public static final int LCA = 2;
        public static final int DEBENTURE = 3;
        public static final int LC = 4;
        public static final int CRI = 5;
        public static final int CRA = 6;
    }

    // Constants for Loaders IDs
    public static class Loaders{
        public static final int INVALID = -1;

        public static final int PORTFOLIO = 0;

        public static final int STOCK_DATA = 1;
        public static final int SOLD_STOCK_DATA = 2;
        public static final int STOCK_INCOME = 3;
        public static final int STOCK_OVERVIEW = 4;
        public static final int STOCK_DETAILS = 5;

        public static final int FII_DATA = 6;
        public static final int SOLD_FII_DATA = 7;
        public static final int FII_INCOME = 8;
        public static final int FII_OVERVIEW = 9;
        public static final int FII_DETAILS = 10;

        public static final int CURRENCY_DATA = 11;
        public static final int SOLD_CURRENCY_DATA = 12;
        public static final int CURRENCY_OVERVIEW = 13;
        public static final int CURRENCY_DETAILS = 14;

        public static final int FIXED_DATA = 15;
        public static final int FIXED_OVERVIEW = 16;
        public static final int FIXED_DETAILS = 17;

        public static final int TREASURY_DATA = 18;
        public static final int SOLD_TREASURY_DATA = 19;
        public static final int TREASURY_INCOME = 20;
        public static final int TREASURY_OVERVIEW = 21;
        public static final int TREASURY_DETAILS = 22;

        public static final int OTHERS_DATA = 23;
        public static final int OTHERS_OVERVIEW = 24;
        public static final int OTHERS_DETAILS = 25;
        public static final int OTHERS_INCOME = 26;
    }

    // Constants for Providers IDs
    public static class Provider{
        public static final int PORTFOLIO = 100;

        public static final int PORTFOLIO_GROWTH = 200;

        public static final int INCOME_GROWTH = 300;

        public static final int BUY_GROWTH = 400;

        public static final int STOCK_PORTFOLIO = 1100;

        public static final int STOCK_DATA = 1200;
        public static final int STOCK_DATA_WITH_SYMBOL = 1201;
        public static final int STOCK_DATA_BULK_UPDATE = 1202;
        public static final int STOCK_DATA_BULK_UPDATE_FOR_CURRENT = 1203;

        public static final int SOLD_STOCK_DATA = 1300;
        public static final int SOLD_STOCK_DATA_WITH_SYMBOL = 1301;

        public static final int STOCK_TRANSACTION = 1400;
        public static final int STOCK_TRANSACTION_FOR_SYMBOL = 1401;

        public static final int STOCK_INCOME = 1500;
        public static final int STOCK_INCOME_FOR_SYMBOL = 1501;

        public static final int FII_PORTFOLIO = 2100;

        public static final int FII_DATA = 2200;
        public static final int FII_DATA_WITH_SYMBOL = 2201;
        public static final int FII_DATA_BULK_UPDATE = 2202;
        public static final int FII_DATA_BULK_UPDATE_FOR_CURRENT = 2203;

        public static final int SOLD_FII_DATA = 2300;
        public static final int SOLD_FII_DATA_WITH_SYMBOL = 2301;

        public static final int FII_TRANSACTION = 2400;
        public static final int FII_TRANSACTION_FOR_SYMBOL = 2401;

        public static final int FII_INCOME = 2500;
        public static final int FII_INCOME_FOR_SYMBOL = 2501;

        public static final int CURRENCY_PORTFOLIO = 3100;

        public static final int CURRENCY_DATA = 3200;
        public static final int CURRENCY_DATA_WITH_SYMBOL = 3201;
        public static final int CURRENCY_DATA_BULK_UPDATE = 3202;
        public static final int CURRENCY_DATA_BULK_UPDATE_FOR_CURRENT = 3203;

        public static final int SOLD_CURRENCY_DATA = 3300;
        public static final int SOLD_CURRENCY_DATA_WITH_SYMBOL = 3301;

        public static final int CURRENCY_TRANSACTION = 3400;
        public static final int CURRENCY_TRANSACTION_FOR_SYMBOL = 3401;

        public static final int FIXED_PORTFOLIO = 4100;

        public static final int FIXED_DATA = 4200;
        public static final int FIXED_DATA_WITH_SYMBOL = 4201;
        public static final int FIXED_DATA_BULK_UPDATE = 4202;
        public static final int FIXED_DATA_BULK_UPDATE_FOR_CURRENT = 4203;

        public static final int FIXED_TRANSACTION = 4400;
        public static final int FIXED_TRANSACTION_FOR_SYMBOL = 4401;

        public static final int TREASURY_PORTFOLIO = 5100;

        public static final int TREASURY_DATA = 5200;
        public static final int TREASURY_DATA_WITH_SYMBOL = 5201;
        public static final int TREASURY_DATA_BULK_UPDATE = 5202;
        public static final int TREASURY_DATA_BULK_UPDATE_FOR_CURRENT = 5203;

        public static final int SOLD_TREASURY_DATA = 5300;
        public static final int SOLD_TREASURY_DATA_WITH_SYMBOL = 5301;

        public static final int TREASURY_TRANSACTION = 5400;
        public static final int TREASURY_TRANSACTION_FOR_SYMBOL = 5401;

        public static final int TREASURY_INCOME = 5500;
        public static final int TREASURY_INCOME_FOR_SYMBOL = 5501;

        public static final int OTHERS_PORTFOLIO = 6100;

        public static final int OTHERS_DATA = 6200;
        public static final int OTHERS_DATA_WITH_SYMBOL = 6201;
        public static final int OTHERS_DATA_BULK_UPDATE = 6202;
        public static final int OTHERS_DATA_BULK_UPDATE_FOR_CURRENT = 6203;

        public static final int OTHERS_TRANSACTION = 6400;
        public static final int OTHERS_TRANSACTION_FOR_SYMBOL = 6401;

        public static final int OTHERS_INCOME = 6500;
        public static final int OTHERS_INCOME_FOR_SYMBOL = 6501;
    }

    // List of Symbols to use as overall and autocomplete fields
    public static class Symbols{
        public static final String[] STOCKS = {"BOVA11","BRAX11","CSMO11","DIVO11","ECOO11","FIND11","GOVE11","ISUS11","MATB11","MILA11","MOBI11","PIBB11","SMAL11","UTIP11","XBOV11","ADHM3","AELP3","TIET11","TIET3","TIET4","AFLU3","AFLU5","AFLU6","AFLT3","RPAD3","RPAD5","RPAD6","ALSC3","ALPA3","ALPA4","ALTS11","ALTS3","ALUP11","ALUP3","ALUP4","ABEV3","CBEE3","ARZZ3","ATOM3","AZEV3","AZEV4","AZUL4","BTOW3","BAHI3","BEES3","BEES4","BDLL3","BDLL4","BTTL3","BTTL4","BALM3","BALM4","BBSE3","ABCB4","BRIV3","BRIV4","BAZA3","BBDC3","BBDC4","BBAS11","BBAS12","BBAS3","BPAC11","BPAC3","BPAC5","BGIP3","BGIP4","BPAR3","BRSR3","BRSR5","BRSR6","IDVL3","IDVL4","BMIN3","BMIN4","BMEB3","BMEB4","BNBR3","BPAN4","BPAT33","PINE3","PINE4","SANB11","SANB3","SANB4","BSAN33","BMKS3","BIOM3","BSEV3","BVMF3","BOBR3","BOBR4","HCBR3","BRIN3","BRML3","BRPR3","BRAP3","BRAP4","BBRK3","BPHA3","AGRO3","BRKM3","BRKM5","BRKM6","BFRE11","BFRE12","BSLI3","BSLI4","BRFS3","BRQB3","BBTG11","BBTG12","BBTG35","BBTG36","CAMB3","CAMB4","CCRO3","CCXC3","RANI3","RANI4","MAPT3","MAPT4","ELET3","ELET5","ELET6","CLSC3","CLSC4","CELP3","CELP5","CELP6","CELP7","AALR3","CESP3","CESP5","CESP6","CABB3","PCAR3","PCAR4","CASN3","CASN4","GPAR3","CEGR3","CEEB3","CEEB5","CEEB6","CEBR3","CEBR5","CEBR6","CMIG3","CMIG4","CEPE3","CEPE5","CEPE6","COCE3","COCE5","COCE6","ENMA3B","ENMA5B","ENMA6B","CSRN3","CSRN5","CSRN6","CEED3","CEED4","EEEL3","EEEL4","FESA3","FESA4","CEDO3","CEDO4","CGAS3","CGAS5","HBTS3","HBTS5","HBTS6","HGTX3","CATA3","CATA4","LCAM3","MSPA3","MSPA4","CPLE3","CPLE5","CPLE6","PEAB3","PEAB4","SBSP3","CSMG3","SAPR3","SAPR4","CSAB3","CSAB4","CSNA3","CTNM3","CTNM4","CTSA3","CTSA4","CTSA8","CIEL3","CMSA3","CMSA4","CNSY3","ODER3","ODER4","BRGE11","BRGE12","BRGE3","BRGE5","BRGE6","BRGE7","BRGE8","CALI3","CALI4","LIXC3","LIXC4","TEND3","CTAX3","CORR3","CORR4","CZLT33","RLOG3","CSAN3","CPFE3","CPRE3","CRDE3","CREM3","CRPG3","CRPG5","CRPG6","CARD3","CTCA3","TRPL3","TRPL4","CVCB3","CYRE3","CCPR3","DASA3","PNVL3","PNVL4","DIRR3","DOHL3","DOHL4","DTCY3","DTCY4","DAGB33","DTEX3","ECOR3","ENBR3","EALT3","EALT4","ELEK3","ELEK4","EKTR3","EKTR4","LIPR3","ELPL3","ELPL4","EMAE3","EMAE4","EMBR3","ECPR3","ECPR4","ENMT3","ENMT4","ENGI11","ENGI3","ENGI4","ENEV3","EGIE3","EQTL3","ESTC3","ETER3","EUCA3","EUCA4","EVEN3","BAUH3","BAUH4","EZTC3","VSPT3","VSPT4","FHER3","FBMC3","FBMC4","FIBR3","CRIV3","CRIV4","FNCN3","FLRY3","FJTA3","FJTA4","FOMS3","FRAS3","ANIM3","GFSA3","GSHP3","GGBR3","GGBR4","GOLL4","GPIV33","GPCP3","GPCP4","CGRA3","CGRA4","GRND3","GRUC3","GRUC6","GUAR3","GUAR4","HAGA3","HAGA4","HBOR3","HETA3","HETA4","HOOT3","HOOT4","HYPE3","IDNT3","IGBR3","IGTA3","JBDU3","JBDU4","ROMI3","INEP3","INEP4","PARD3","MEAL3","FIGE3","FIGE4","MYPK3","SQRM11","SQRM3","ITUB3","ITUB4","ITSA3","ITSA4","ITEC3","JBSS3","MLFT3","MLFT4","JHSF3","JFEN3","JOPA3","JOPA4","LFFE3","LFFE4","JSLG3","CTKA3","CTKA4","KEPL3","KLBN11","KLBN3","KLBN4","KROT3","LIGT3","LINX3","RENT3","LOGN3","LAME3","LAME4","LHER3","LHER4","LREN3","LPSB3","LUPA3","MDIA3","MSRO3","MGLU3","MAGG3","LEVE3","MGEL3","MGEL4","ESTR3","ESTR4","POMO3","POMO4","MRFG3","AMAR3","MEND3","MEND5","MEND6","MERC3","MERC4","FRIO3","MTIG3","MTIG4","GOAU3","GOAU4","RSUL3","RSUL4","MTSA3","MTSA4","MILS3","MMAQ3","MMAQ4","BEEF3","MNPR3","MMXM3","MOAR3","MOVI3","MRVE3","MULT3","MPLU3","MNDL3","NAFG3","NAFG4","NATU3","NORD3","NRTQ3","NUTR3","ODPV3","OGSA3","OIBR3","OIBR4","OGXP3","OSXB3","OFSA3","PATI3","PATI4","PRBC4","PMAM3","PTBL3","PDGR3","PRIO3","PETR3","PETR4","PTNT3","PTNT4","PLAS3","PPAR3","FRTA3","PSSA3","POSI3","PRCA11","PRCA12","PRCA3","PFRM3","PRML3","QGEP3","QUAL3","QUSW3","RADL3","RAPT3","RAPT4","RCSL3","RCSL4","REDE3","REDE4","RPMG3","RNEW11","RNEW3","RNEW4","LLIS3","GEPA3","GEPA4","RDNI3","RSID3","RAIL3","SNSY3","SNSY5","SNSY6","STBP3","SCAR3","SMTO3","AHEB3","AHEB5","AHEB6","SLED3","SLED4","PSEG3","PSEG4","SHUL3","SHUL4","SNSL3","SEER3","APTI3","APTI4","SLCE3","SMLE3","SEDU3","SSBR3","SOND3","SOND5","SOND6","SPRI3","SPRI5","SPRI6","SGPS3","STKF3","SULA11","SULA3","SULA4","NEMO3","NEMO5","NEMO6","SUZB5","SUZB6","SHOW3","TRPN3","TOYB3","TOYB4","TECN3","TCSA3","TCNO3","TCNO4","TGMA3","TEKA3","TEKA4","TKNO3","TKNO4","TELB3","TELB4","VIVT3","VIVT4","TESA3","TXRX3","TXRX4","TIMP3","TOTS3","TPIS3","TAEE11","TAEE3","TAEE4","LUXM3","LUXM4","TRIS3","TUPY3","UGPA3","UCAS3","UNIP3","UNIP5","UNIP6","USIM3","USIM5","USIM6","VALE3","VALE5","VLID3","VVAR11","VVAR3","VVAR4","VIVR3","VULC3","WEGE3","MWET3","MWET4","WHRL3","WHRL4","WSON33","WIZS3","SGAS3","SGAS4","IRBR3"};
        public static final String[] FII = {"AEFI11","ATCR11","BCRI11","BNFS11","BBFI11B","BBPO11","BBIM11","BBRC11","RNDP11","BCIA11","CARE11","CXRI11","CPTS11B","CBOP11","ATSA11B","HGBS11","GRLV11","HGJH11","HGLG11","HGRE11","HGCR11","FOFT11","TFOF11","DOMC11","DOVL11B","FIXX11","VRTA11","BMII11","ANCR11B","FAED11","BRCR11","FEXC11","BCFF11B","FCFL11B","CNES11","CEOC11B","THRA11B","FAMB11B","FCAS11","EDGA11","ELDO11B","FLRP11","HCRI11","NSLU11","HTMX11","MAXR11","NVHO11","PQDP11","PRSV11","JRDM11","SHDP11B","WPLZ11B","SAIC11B","TBOF11","ALMI11","TRNT11","VLOL11","AGCX11","BBVJ11","BMLC11B","BPFF11","BVAR11","CXCE11B","CXTL11","CTXT11","FLMA11","EDFO11B","EURO11","FIGS11","ABCP11","GWIR11","FIIB11","FMOF11","MBRF11","PABY11","FPNG11","VPSI11","FPAB11","FFCI11","RNGO11","SFND11","SCPF11","SHPH11","ONEF11","FVBI11","VERE11","FVPQ11","RBBV11","JPPC11","JSRE11","KNRE11","KNIP11","KNRI11","KNCR11","LATR11B","MXRF11","MFII11","DRIT11B","FTCE11B","PRSN11B","PLRI11","PORD11","RBDS11","RBGS11","FIIP11B","RBRD11","REIT11B","RDES11","RBCB11","RBVO11","SAAG11","SDIL11","SPTW11","SPAF11","TSNC11","XTED11","TRXL11","VLJS12","XPCM11","XPGA11"};
    }
}
