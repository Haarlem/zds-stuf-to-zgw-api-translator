package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwZaakType {

	@SerializedName("omschrijving")
	@Expose
	public String omschrijving;
	@SerializedName("identificatie")
	@Expose
	public String identificatie;
	@SerializedName("url")
	@Expose
	public String url;
	@SerializedName("beginGeldigheid")
	@Expose
	public Date beginGeldigheid;
	@SerializedName("eindeGeldigheid")
	@Expose
	public Date eindeGeldigheid;	
}
