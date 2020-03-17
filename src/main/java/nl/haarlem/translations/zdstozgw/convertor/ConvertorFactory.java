package nl.haarlem.translations.zdstozgw.convertor;

import nl.haarlem.translations.zdstozgw.convertor.impl.CreeerZaak;

public class ConvertorFactory {

    // http://www.egem.nl/StUF/sector/zkn/0310/cancelCheckout_Di02
    // http://www.egem.nl/StUF/sector/zkn/0310/geefZaakdocumentbewerken_Di02
    // http://www.egem.nl/StUF/sector/zkn/0310/genereerDocumentIdentificatie_Di02
    // http://www.egem.nl/StUF/sector/zkn/0310/genereerZaakIdentificatie_Di02
    // http://www.egem.nl/StUF/sector/zkn/0310/updateZaakdocument_Di02	
	
	public static Convertor getConvertor(String soapAction, String application) {
		/** 
		 * Op basis van de soapaction en de applicatie wil je sturen welke implementatie er gebruikt wordt. 
		 * @TODO: later eens mooi uit een configuratie bestand zodat er eigen implementatie kunnen worden toegevoegd
		*/		
		String classname = null;
		String templatepath = null;
	
		switch(soapAction) 
        { 
    		case "genereerZaakIdentificatie_Di02": 		
        	case "http://www.egem.nl/StUF/sector/zkn/0310/genereerZaakIdentificatie_Di02":
        		classname = "nl.sudwestfryslan.translations.zdstozgw.implementation.GenereerZaakIdentificatie";
        		templatepath = "src\\main\\java\\nl\\sudwestfryslan\\translations\\zdstozgw\\implementation\\genereerZaakIdentificatie_Du02.xml";
        		break;
        	case "http://www.egem.nl/StUF/sector/zkn/0310/creeerZaak_Lk01":    			
        	case "creeerZaak_ZakLk01":
        		classname = CreeerZaak.class.getName();
        		templatepath = "--template-zit-in-de-code--";
        		break;
    		case "voegZaakdocumentToe_Lk01": 		
        	case "http://www.egem.nl/StUF/sector/zkn/0310/voegZaakdocumentToe_Lk01":
        	case "http://www.stufstandaarden.nl/koppelvlak/zds0120/voegZaakdocumentToe_Lk01":
        		classname = "nl.sudwestfryslan.translations.zdstozgw.implementation.CreeerZaak";
        		templatepath = "--template-zit-in-de-code--";
        		break;
        	default: 
            	return null;
        }
		try {
			Class<?> clazz = Class.forName(classname);
			java.lang.reflect.Constructor<?> ctor = clazz.getConstructor(String.class);
			Object object = ctor.newInstance(new Object[] { templatepath });
			return  (Convertor) object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}		
}