/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/galleries/client/preview/ui/Attic/CmsPropertiesTab.java,v $
 * Date   : $Date: 2010/08/26 13:34:11 $
 * Version: $Revision: 1.7 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.galleries.client.preview.ui;

import org.opencms.ade.galleries.client.preview.I_CmsPreviewHandler;
import org.opencms.ade.galleries.client.ui.Messages;
import org.opencms.ade.galleries.client.ui.css.I_CmsLayoutBundle;
import org.opencms.ade.galleries.shared.I_CmsGalleryProviderConstants.GalleryMode;
import org.opencms.gwt.client.ui.CmsPushButton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The widget to display the properties of the selected resource.<p>
 * 
 * @author Polina Smagina
 * 
 * @version $Revision: 1.7 $
 * 
 * @since 8.0.
 */
public class CmsPropertiesTab extends A_CmsPreviewDetailTab implements ValueChangeHandler<String> {

    /** Text metrics key. */
    private static final String TM_PREVIEW_TAB_PROPERTIES = "PropertiesTab";

    /** The save button. */
    private CmsPushButton m_saveButton;

    /** The tab handler. */
    private I_CmsPreviewHandler<?> m_handler;

    private FlowPanel m_content;

    /**
     * The constructor.<p>
     * 
     * @param dialogMode the dialog mode
     * @param height the properties tab height
     * @param width the properties tab width
     * @param handler tha tab handler to set
     */
    public CmsPropertiesTab(GalleryMode dialogMode, int height, int width, I_CmsPreviewHandler<?> handler) {

        super(dialogMode, height, width);
        m_handler = handler;
        m_content = new FlowPanel();
        m_content.addStyleName(org.opencms.ade.galleries.client.ui.css.I_CmsLayoutBundle.INSTANCE.previewDialogCss().propertiesList());
        m_content.addStyleName(org.opencms.ade.galleries.client.ui.css.I_CmsLayoutBundle.INSTANCE.previewDialogCss().clearFix());
        m_main.insert(m_content, 0);
        // buttons
        m_saveButton = new CmsPushButton();
        m_saveButton.addStyleName(org.opencms.ade.galleries.client.ui.css.I_CmsLayoutBundle.INSTANCE.previewDialogCss().previewButton());
        m_saveButton.setText(Messages.get().key(Messages.GUI_PREVIEW_BUTTON_SAVE_0));
        m_saveButton.disable("nothing changed");
        m_saveButton.addClickHandler(new ClickHandler() {

            /**
             * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent event) {

                onSaveClick(event);
            }
        });

        m_buttonBar.add(m_saveButton);
    }

    /**
     * The generic function to display the resource properties.<p>
     * 
     * @param properties the properties values
     */
    public void fillProperties(Map<String, String> properties) {

        // width of a property form
        int pannelWidth = calculateWidth(m_tabWidth);
        m_content.clear();
        Iterator<Entry<String, String>> it = properties.entrySet().iterator();
        boolean isLeft = true;
        while (it.hasNext()) {

            Entry<String, String> entry = it.next();
            CmsPropertyForm property = new CmsPropertyForm(
                entry.getKey(),
                pannelWidth,
                entry.getValue(),
                TM_PREVIEW_TAB_PROPERTIES);
            if (isLeft) {
                property.setFormStyle(I_CmsLayoutBundle.INSTANCE.previewDialogCss().propertyLeft());
                isLeft = false;
            } else {
                property.setFormStyle(I_CmsLayoutBundle.INSTANCE.previewDialogCss().propertyRight());
                isLeft = true;
            }
            property.addValueChangeHandler(this);
            m_content.add(property);

            // TODO: set the calculated height of the scrolled panel with properties
        }
        setChanged(false);
        m_saveButton.disable("nothing changed");
    }

    /**
     * Returns the tab name.<p>
     * 
     * @return the tab name
     */
    public String getTabName() {

        return Messages.get().key(Messages.GUI_PREVIEW_TAB_PROPERTIES_0);
    }

    /**
     * Will be triggered, when the save button is clicked.<p>
     * 
     * @param event the click event
     */
    public void onSaveClick(ClickEvent event) {

        Map<String, String> properties = new HashMap<String, String>();
        for (Widget property : m_content) {
            CmsPropertyForm form = ((CmsPropertyForm)property);
            if (form.isChanged()) {
                properties.put(form.getId(), form.getValue());
            }
        }
        m_handler.saveProperties(properties);
    }

    /**
     * Updates the size of the dialog after the window was resized.<p>
     * 
     * @param width the new width
     * @param height the new height
     */
    public void updateSize(int width, int height) {

        m_tabHeight = height;
        m_tabWidth = width;
        // TODO: implement
    }

    /**
     * Calculates the width of the properties panel without border, margin and padding.<p>
     * 
     * '- 13px:'  2px - border, 2px - outer margin, 2px - inner margin, 2px border, 5px padding
     * '/ 2': two colums
     * '-18': some offset (The input field needs more place because of the border) 
     * 
     * @param width the width of the preview dialog containing all decorations
     * @return the width of the properties panel
     */
    private int calculateWidth(int width) {

        return ((width - 13) / 2) - 18;
    }

    /**
     * @see org.opencms.ade.galleries.client.preview.ui.A_CmsPreviewDetailTab#getHandler()
     */
    @Override
    protected I_CmsPreviewHandler<?> getHandler() {

        return m_handler;
    }

    /**
     * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
     */
    public void onValueChange(ValueChangeEvent<String> event) {

        setChanged(true);
        m_saveButton.enable();
    }
}