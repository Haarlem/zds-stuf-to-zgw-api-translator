package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN)
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLa01GeefZaakDetails {

    @XmlElement(namespace = STUF)
    public Stuurgegevens stuurgegevens;

    @XmlElement(namespace = ZKN)
    public Antwoord antwoord;

    @XmlElement(namespace = ZKN)
    public Object object;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Antwoord {
        @XmlElement(namespace = ZKN, name = "object")
        public Object zaak;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Object extends nl.haarlem.translations.zdstozgw.translation.zds.model.Zaak {
            @XmlElement(namespace = ZKN)
            public List<Status> heeft;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Object {
        @XmlElement(namespace = ZKN)
        public Rol isVan;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Status {
        @XmlAttribute(namespace = STUF)
        public String entiteittype;

        @XmlElement(namespace = ZKN)
        public String toelichting;

        @XmlElement(namespace = ZKN)
        public String datumStatusGezet;

        @XmlElement(namespace = ZKN)
        public String indicatieLaatsteStatus;

        @XmlElement(namespace = ZKN)
        public String isGezetDoor;
    }

}