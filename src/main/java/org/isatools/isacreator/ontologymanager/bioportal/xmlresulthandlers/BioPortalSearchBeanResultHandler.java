/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */


package org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers;

import bioontology.bioportal.searchBean.schema.SearchBeanDocument;
import bioontology.bioportal.searchBean.schema.SearchResultListDocument;
import bioontology.bioportal.searchBean.schema.SuccessDocument;
import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalOntology;
import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalSearchResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * BioPortalSearchBeanHandler
 *
 * @author eamonnmaguire
 * @date Feb 25, 2010
 */


public class BioPortalSearchBeanResultHandler {

    public SuccessDocument getDocument(String fileLocation) {
        SuccessDocument resultDocument = null;
        try {
            resultDocument = SuccessDocument.Factory.parse(new File(fileLocation));
        } catch (org.apache.xmlbeans.XmlException e) {
            System.err.println("XML Exception encountered");
            e.printStackTrace();
        } catch (java.io.IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        return resultDocument;
    }

    public Map<String, BioPortalOntology> getSearchResults(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);

        BioPortalSearchResult result = new BioPortalSearchResult();

        if (resultDocument != null) {
            if (resultDocument.getSuccess().getData().getPage() != null) {
                SearchResultListDocument.SearchResultList[] searchResults = resultDocument.getSuccess().getData().getPage()
                        .getContents().getSearchResultListArray();

                for (SearchResultListDocument.SearchResultList searchResult : searchResults) {
                    for (SearchBeanDocument.SearchBean searchBean : searchResult.getSearchBeanArray()) {
                        BioPortalOntology ontology = createBioPortalOntologyFromSearchResult(searchBean);
                        if (ontology != null) {
                            result.addToResult(ontology, false);
                        }
                    }
                }
            }
        }

        return result.getResult() == null ? new HashMap<String, BioPortalOntology>() : result.getResult();
    }

    private BioPortalOntology createBioPortalOntologyFromSearchResult(SearchBeanDocument.SearchBean searchResult) {
        BioPortalOntology ontology = new BioPortalOntology();

        if (AcceptedOntologies.getOntologyAbbreviationFromId(searchResult.getOntologyId()) != null) {
            ontology.setOntologyVersionId(searchResult.getOntologyVersionId());
            ontology.setOntologyTermName(searchResult.getPreferredName());
            ontology.setOntologySource(AcceptedOntologies.getOntologyAbbreviationFromId(searchResult.getOntologyId()));
            ontology.setOntologySourceAccession(processSourceAccession(searchResult.getConceptIdShort(), ontology.getOntologySource()));
            ontology.setOntologyPurl(searchResult.getConceptId());
            return ontology;
        }

        return null;
    }

    /**
     * Removes some elements from the conceptIds on some terms so they function properly.
     *
     * @param sourceAccession something like UO#UO:0000129
     * @return cleaned version e.g. UO:0000129
     */
    private String processSourceAccession(String sourceAccession, String source) {
        if (sourceAccession.contains("#")) {
            sourceAccession = sourceAccession.substring(sourceAccession.lastIndexOf("#") + 1);
        }

        if (!sourceAccession.toLowerCase().contains(":")) {
            sourceAccession = source + ":" + sourceAccession;
        }
        return sourceAccession;
    }

}
