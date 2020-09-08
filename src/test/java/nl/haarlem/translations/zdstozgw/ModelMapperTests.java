package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.config.ModelMapperConfig;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsHeeft;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import javax.validation.constraints.AssertTrue;
import java.time.ZoneId;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ModelMapperConfig.class)
public class ModelMapperTests {

    @Autowired
    ModelMapper modelMapper;

    @Test
    public void zgwEnkelvoudigInformatieObjectToZdsZaakDocument_shouldMapCorrectly(){
        //assign
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = new ZgwEnkelvoudigInformatieObject()
                .setBestandsnaam("bestandsnaam")
                .setInhoud("inhoud")
                .setAuteur("auteur")
                .setBeschrijving("beschrijving")
                .setBronorganisatie("bronorganisatie")
                .setCreatiedatum("2020-02-30")
                .setFormaat("formaat")
                .setIdentificatie("identificatie")
                .setInformatieobjecttype("informatieobjecttype")
                .setOntvangstdatum("2020-06-20")
                .setStatus("status")
                .setTaal("taal")
                .setTitel("titel")
                .setUrl("url")
                .setVersie("versie")
                .setVertrouwelijkheidaanduiding("vertrouwelijkheidaanduiding")
                .setVerzenddatum("2020-05-09");
        String expectedCreatieDatum = "20200230";

        //act
        ZdsZaakDocument zdsZaakDocument = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);

        //assert
        Assert.assertEquals(expectedCreatieDatum, zdsZaakDocument.getCreatiedatum());
    }

    @Test
    public void convertStufDateTimeToZgwDateTime_shouldAddTwoHoursInUTCWhenDayInSummer(){
        //assign
        ZdsHeeft zdsHeeft = new ZdsHeeft().setDatumStatusGezet("20200904103404929");
        String expectedDatum = "2020-09-04T10:34:04+02:00";

        //act
        ZgwStatus zgwStatus =  modelMapper.map(zdsHeeft, ZgwStatus.class);

        //assert
        Assert.assertEquals(expectedDatum, zgwStatus.getDatumStatusGezet());
    }

    @Test
    public void convertStufDateTimeToZgwDateTime_shouldAddOneHourInUTCWhenDayInWinter(){
        //assign
        ZdsHeeft zdsHeeft = new ZdsHeeft().setDatumStatusGezet("20200101103404929");
        String expectedDatum = "2020-01-01T10:34:04+01:00";

        //act
        ZgwStatus zgwStatus =  modelMapper.map(zdsHeeft, ZgwStatus.class);

        //assert
        Assert.assertEquals(expectedDatum, zgwStatus.getDatumStatusGezet());
    }
}