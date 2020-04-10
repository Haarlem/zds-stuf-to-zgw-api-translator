package nl.haarlem.translations.zdstozgw.convertor.impl;

import nl.haarlem.translations.zdstozgw.controller.SoapController;
import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class CreeerZaak extends Convertor {
    protected String templatePath;

    public CreeerZaak(String templatePath, String legacyService) {
        super(templatePath, legacyService);
    }

    @Override
    public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestBody) {
        try {
        	ZakLk01_v2 object = SoapController.getZakLka01(requestBody);
            var zaak = zaakService.creeerZaak((ZakLk01_v2) object);
            var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
            bv03.setReferentienummer(zaak.getUuid());
            return bv03.getSoapMessageAsString();

        } catch (Exception ex) {
            ex.printStackTrace();
            var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
            f03.setFaultString("Object was not saved");
            f03.setCode("StUF046");
            f03.setOmschrijving("Object niet opgeslagen");
            f03.setDetails(ex.getMessage());
            return f03.getSoapMessageAsString();
        }
    }
}