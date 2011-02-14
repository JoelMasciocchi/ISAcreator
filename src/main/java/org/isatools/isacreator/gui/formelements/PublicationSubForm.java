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

package org.isatools.isacreator.gui.formelements;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.*;
import org.isatools.isacreator.model.Publication;
import org.isatools.isacreator.publicationlocator.PublicationLocatorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * PublicationSubForm
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class PublicationSubForm extends SubForm {

    private static PublicationLocatorUI publicationLocator = new PublicationLocatorUI();

    static {
        publicationLocator.createGUI();
        publicationLocator.installListeners();
    }

    public PublicationSubForm(String title, FieldTypes fieldType,
                              List<SubFormField> fields, DataEntryEnvironment dep) {
        super(title, fieldType, fields, dep);
    }

    public PublicationSubForm(String title, FieldTypes fieldType,
                              List<SubFormField> fields, int initialNoFields,
                              int width, int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public void reformPreviousContent() {
        if (parent != null) {
            reformItems();
        }
    }

    public void reformItems() {
        List<Publication> publications = parent.getPublications();
        for (int i = 1; i < publications.size() + 1; i++) {
            String[] publicationInfo = {
                    publications.get(i - 1).getPubmedId(),
                    publications.get(i - 1).getPublicationDOI(),
                    publications.get(i - 1).getPublicationAuthorList(),
                    publications.get(i - 1).getPublicationTitle(),
                    publications.get(i - 1).getPublicationStatus()};

            for (int j = 0; j < publicationInfo.length; j++) {
                dtm.setValueAt(publicationInfo[j], j, i);
            }
        }
    }

    protected void removeItem(int itemToRemove) {
        // provide a publication id or a publication title depending on which is available
        if (parent != null) {

            String pubMedId = (scrollTable.getModel().getValueAt(0, itemToRemove) != null)
                    ? scrollTable.getModel().getValueAt(0, itemToRemove).toString()
                    : "";
            String publicationTitle = (scrollTable.getModel().getValueAt(3, itemToRemove) != null)
                    ? scrollTable.getModel().getValueAt(3, itemToRemove).toString()
                    : "";

            if (parent instanceof StudyDataEntry) {
                parent.getStudy().removePublication(pubMedId, publicationTitle);
            } else {
                parent.getInvestigation().removePublication(pubMedId, publicationTitle);
            }
        }
        removeColumn(itemToRemove);
    }

    public void updateItems() {
        int cols = dtm.getColumnCount();
        int rows = dtm.getRowCount();
        final List<Publication> newPublications = new ArrayList<Publication>();

        for (int i = 1; i < cols; i++) {
            String[] publicationVars = new String[5];

            for (int j = 0; j < rows; j++) {
                if (dtm.getValueAt(j, i) != null) {
                    publicationVars[j] = dtm.getValueAt(j, i).toString();
                } else {
                    publicationVars[j] = "";
                }
            }

            if (!publicationVars[0].equals("") || !publicationVars[1].equals("") || !publicationVars[2].equals("")
                    || !publicationVars[3].equals("") || !publicationVars[4].equals("")) {
                newPublications.add(new Publication(publicationVars[0], publicationVars[1],
                        publicationVars[2], publicationVars[3], publicationVars[4]));
            }
        }

        if (parent instanceof StudyDataEntry) {
            parent.getStudy().setPublications(newPublications);
        } else if (parent instanceof InvestigationDataEntry) {
            parent.getInvestigation().setPublications(newPublications);
        }
    }

    public void update() {
        if (parent != null) {
            updateItems();
        }
    }

    /**
     * Implementing this method allows for the creation of additional menu
     * elements in the options panel of the subform.
     */
    public void createCustomOptions() {
        if (parent != null && fieldType == FieldTypes.PUBLICATION) {

            final JLabel selectPublicationLabel = new JLabel(
                    "search for " + fieldType,
                    searchIcon,
                    JLabel.LEFT);

            UIHelper.renderComponent(selectPublicationLabel, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            selectPublicationLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    selectPublicationLabel.setIcon(searchIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    selectPublicationLabel.setIcon(searchIcon);
                }

                public void mousePressed(MouseEvent event) {
                    selectPublicationLabel.setIcon(searchIcon);
                    if (selectPublicationLabel.isEnabled()) {


                        publicationLocator.addPropertyChangeListener("selectedPublication",
                                new PropertyChangeListener() {
                                    public void propertyChange(
                                            PropertyChangeEvent evt) {

                                        if (evt.getNewValue() instanceof Publication) {
                                            Publication p = (Publication) evt.getNewValue();
                                            boolean added;
                                            if (parent instanceof StudyDataEntry) {
                                                added = parent.getStudy().addPublication(p);
                                            } else {
                                                //parent is instance of InvestigationDataEntry
                                                added = parent.getInvestigation().addPublication(p);
                                            }

                                            if (added) {
                                                SwingUtilities.invokeLater(new Runnable() {
                                                    public void run() {
                                                        addColumn();
                                                        updateTables();
                                                        reformItems();
                                                    }
                                                });

                                            }

                                        }
                                        selectPublicationLabel.setEnabled(true);
                                    }
                                });


                        publicationLocator.addPropertyChangeListener("noSelectedPublication",
                                new PropertyChangeListener() {
                                    public void propertyChange
                                            (PropertyChangeEvent evt) {
                                        selectPublicationLabel.setEnabled(true);
                                        publicationLocator.setVisible(false);
                                    }
                                }

                        );


                        // set up location on screen
                        int proposedX = (int) selectPublicationLabel.getLocationOnScreen()
                                .getX();
                        int proposedY = (int) selectPublicationLabel.getLocationOnScreen()
                                .getY();

                        // get the desktop bounds e.g. 1440*990, 800x600, etc.
                        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getMaximumWindowBounds();

                        if ((proposedX + HistoricalSelectionGUI.WIDTH) > desktopBounds.width)

                        {
                            int difference = (proposedX +
                                    HistoricalSelectionGUI.WIDTH) -
                                    desktopBounds.width;
                            proposedX = proposedX - difference;
                        }

                        if ((proposedY + HistoricalSelectionGUI.HEIGHT) > desktopBounds.height)

                        {
                            int difference = (proposedY +
                                    HistoricalSelectionGUI.HEIGHT) -
                                    desktopBounds.height;
                            proposedY = proposedY - difference;
                        }

                        publicationLocator.setLocation(proposedX, proposedY);
                        publicationLocator.setVisible(true);

                    }
                }
            });


            options.add(selectPublicationLabel);
        }
    }

    public boolean doAddColumn(DefaultTableModel model, TableColumn col) {
        try {
            scrollTable.addColumn(col);
            model.addColumn(fieldType);
            model.fireTableStructureChanged();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
