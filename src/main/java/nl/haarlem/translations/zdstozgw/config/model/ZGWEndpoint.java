package nl.haarlem.translations.zdstozgw.config.model;

import lombok.Data;

@Data
public class ZGWEndpoint {
    private String roltype = "/catalogi/api/v1/roltypen";
    private String rol = "/zaken/api/v1/rollen";
    private String zaaktype = "/catalogi/api/v1/zaaktypen";
    private String status = "/zaken/api/v1/statussen";
    private String resultaat = "/zaken/api/v1/resultaten";
    private String statustype = "/catalogi/api/v1/statustypen";
    private String resultaattype = "/catalogi/api/v1/resultaattypen";
    private String zaakinformatieobject = "/zaken/api/v1/zaakinformatieobjecten";
    private String enkelvoudiginformatieobject = "/documenten/api/v1/enkelvoudiginformatieobjecten";
    private String objectinformatieobject = "/documenten/api/v1/objectinformatieobjecten";
    private String zaak = "/zaken/api/v1/zaken";
    private String informatieobjecttype = "/catalogi/api/v1/informatieobjecttypen";
    private String zaakobject = "/zaken/api/v1/zaakobjecten";
}
