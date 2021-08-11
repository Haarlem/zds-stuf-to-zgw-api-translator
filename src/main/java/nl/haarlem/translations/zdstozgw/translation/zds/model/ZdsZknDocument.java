/*
 * Copyright 2020-2021 The Open Zaakbrug Contributors
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the 
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlElement;

import nl.haarlem.translations.zdstozgw.utils.StufUtils;

abstract public class ZdsZknDocument extends ZdsObject {
	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public ZdsStuurgegevens stuurgegevens;

	protected ZdsZknDocument() {
	}

	public ZdsZknDocument(ZdsStuurgegevens fromRequest, String referentienummer) {
		this.stuurgegevens = new ZdsStuurgegevens(fromRequest, referentienummer);
		this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
	}
}
