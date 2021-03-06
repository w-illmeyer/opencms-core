/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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

package org.opencms.ade.galleries.client.ui;

import org.opencms.ade.galleries.client.CmsCategoriesTabHandler;
import org.opencms.ade.galleries.client.Messages;
import org.opencms.ade.galleries.shared.CmsGallerySearchBean;
import org.opencms.ade.galleries.shared.I_CmsGalleryProviderConstants.GalleryTabId;
import org.opencms.ade.galleries.shared.I_CmsGalleryProviderConstants.SortParams;
import org.opencms.gwt.client.ui.CmsListItemWidget;
import org.opencms.gwt.client.ui.CmsSimpleListItem;
import org.opencms.gwt.client.ui.input.CmsCheckBox;
import org.opencms.gwt.client.ui.tree.CmsTreeItem;
import org.opencms.gwt.shared.CmsCategoryBean;
import org.opencms.gwt.shared.CmsCategoryTreeEntry;
import org.opencms.gwt.shared.CmsIconUtil;
import org.opencms.gwt.shared.CmsListInfoBean;
import org.opencms.util.CmsPair;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Label;

/**
 * Provides the widget for the categories tab.<p>
 * 
 * It displays the available categories in the given sort order.
 * 
 * @since 8.0.
 */
public class CmsCategoriesTab extends A_CmsListTab {

    /** 
     * Handles the change of the item selection.<p>
     */
    private class SelectionHandler extends A_SelectionHandler {

        /** The category path as id for the selected category. */
        private String m_categoryPath;

        /**
         * Constructor.<p>
         * 
         * @param categoryPath as id for the selected category
         * @param checkBox the reference to the checkbox
         */
        public SelectionHandler(String categoryPath, CmsCheckBox checkBox) {

            super(checkBox);
            m_categoryPath = categoryPath;
        }

        /**
         * @see org.opencms.ade.galleries.client.ui.A_CmsListTab.A_SelectionHandler#onSelectionChange()
         */
        @Override
        protected void onSelectionChange() {

            if (getCheckBox().isChecked()) {
                getTabHandler().onSelectCategory(m_categoryPath);
            } else {
                getTabHandler().onDeselectCategory(m_categoryPath);
            }

        }
    }

    /** The category icon CSS classes. */
    private static final String CATEGORY_ICON_CLASSES = CmsIconUtil.getResourceIconClasses("folder", false);

    /** Text metrics key. */
    private static final String TM_CATEGORY_TAB = "CategoryTab";

    /** Map of the categories by path. */
    private Map<String, CmsCategoryBean> m_categories;

    /** The flag to indicate when the categories are opened for the fist time. */
    private boolean m_isInitOpen;

    /** The search parameter panel for this tab. */
    private CmsSearchParamPanel m_paramPanel;

    /** The tab handler. */
    private CmsCategoriesTabHandler m_tabHandler;

    /**
     * Constructor.<p>
     * 
     * @param tabHandler the tab handler 
     */
    public CmsCategoriesTab(CmsCategoriesTabHandler tabHandler) {

        super(GalleryTabId.cms_tab_categories);
        m_scrollList.truncate(TM_CATEGORY_TAB, CmsGalleryDialog.DIALOG_WIDTH);
        m_tabHandler = tabHandler;
        m_isInitOpen = false;
    }

    /**
     * Fill the content of the categories tab panel.<p>
     * 
     * @param categoryRoot the category tree root entry 
     */
    public void fillContent(List<CmsCategoryTreeEntry> categoryRoot) {

        setInitOpen(true);

        updateContentTree(categoryRoot, null);
    }

    /**
     * Returns the content of the categories search parameter.<p>
     *  
     * @param selectedCategories the list of selected categories by the user
     * 
     * @return the selected categories
     */
    public String getCategoriesParams(List<String> selectedCategories) {

        if ((selectedCategories == null) || (selectedCategories.size() == 0)) {
            return null;
        }
        StringBuffer result = new StringBuffer(128);
        for (String categoryPath : selectedCategories) {
            CmsCategoryBean categoryItem = m_categories.get(categoryPath);
            String title = categoryItem.getTitle();
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                title = categoryItem.getPath();
            }
            result.append(title).append(", ");
        }
        result.delete(result.length() - 2, result.length());

        return result.toString();
    }

    /**
     * @see org.opencms.ade.galleries.client.ui.A_CmsTab#getParamPanel(org.opencms.ade.galleries.shared.CmsGallerySearchBean)
     */
    @Override
    public CmsSearchParamPanel getParamPanel(CmsGallerySearchBean searchObj) {

        if (m_paramPanel == null) {
            m_paramPanel = new CmsSearchParamPanel(Messages.get().key(Messages.GUI_PARAMS_LABEL_CATEGORIES_0), this);
        }
        String content = getCategoriesParams(searchObj.getCategories());
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(content)) {
            m_paramPanel.setContent(content);
            return m_paramPanel;
        }
        return null;
    }

    /**
     * Returns the isInitOpen.<p>
     *
     * @return the isInitOpen
     */
    public boolean isInitOpen() {

        return m_isInitOpen;
    }

    /**
     * Opens the first level in the categories tree.<p>
     */
    public void openFirstLevel() {

        if (!m_categories.isEmpty()) {
            for (int i = 0; i < m_scrollList.getWidgetCount(); i++) {
                CmsTreeItem item = (CmsTreeItem)m_scrollList.getItem(i);
                item.setOpen(true);
            }
        }
    }

    /**
     * Sets the isInitOpen.<p>
     *
     * @param isInitOpen the isInitOpen to set
     */
    public void setInitOpen(boolean isInitOpen) {

        m_isInitOpen = isInitOpen;
    }

    /**
     * Deselect the categories  in the category list.<p>
     * 
     * @param categories the categories to deselect
     */
    public void uncheckCategories(List<String> categories) {

        for (String category : categories) {
            CmsTreeItem item = searchTreeItem(m_scrollList, category);
            item.getCheckBox().setChecked(false);
        }
    }

    /**
     * Updates the content of the categories list.<p>
     * 
     * @param categoriesBeans the updates list of categories tree item beans
     * @param selectedCategories the categories to select in the list by update
     */
    public void updateContentList(List<CmsCategoryBean> categoriesBeans, List<String> selectedCategories) {

        clearList();
        if (m_categories == null) {
            m_categories = new HashMap<String, CmsCategoryBean>();
        }
        if ((categoriesBeans != null) && !categoriesBeans.isEmpty()) {
            for (CmsCategoryBean categoryBean : categoriesBeans) {
                m_categories.put(categoryBean.getPath(), categoryBean);
                // set the list item widget
                CmsListItemWidget listItemWidget = new CmsListItemWidget(new CmsListInfoBean(
                    categoryBean.getTitle(),
                    CmsStringUtil.isNotEmptyOrWhitespaceOnly(categoryBean.getDescription())
                    ? categoryBean.getDescription()
                    : categoryBean.getPath(),
                    null));
                listItemWidget.setIcon(CATEGORY_ICON_CLASSES);
                // the checkbox
                CmsCheckBox checkBox = new CmsCheckBox();
                if ((selectedCategories != null) && selectedCategories.contains(categoryBean.getPath())) {
                    checkBox.setChecked(true);
                }
                SelectionHandler selectionHandler = new SelectionHandler(categoryBean.getPath(), checkBox);
                checkBox.addClickHandler(selectionHandler);
                listItemWidget.addDoubleClickHandler(selectionHandler);
                // set the category list item and add to list 
                CmsTreeItem listItem = new CmsTreeItem(false, checkBox, listItemWidget);
                listItem.setId(categoryBean.getPath());
                addWidgetToList(listItem);
            }
        } else {
            showIsEmptyLabel();
        }
    }

    /**
     * Updates the content of th categories tree.<p>
     * 
     * @param treeEntries the root category entry
     * @param selectedCategories the categories to select after update
     */
    public void updateContentTree(List<CmsCategoryTreeEntry> treeEntries, List<String> selectedCategories) {

        clearList();
        if (m_categories == null) {
            m_categories = new HashMap<String, CmsCategoryBean>();
        }
        if ((treeEntries != null) && !treeEntries.isEmpty()) {
            // add the first level and children
            for (CmsCategoryTreeEntry category : treeEntries) {
                // set the category tree item and add to list 
                CmsTreeItem treeItem = buildTreeItem(category, selectedCategories);
                addChildren(treeItem, category.getChildren(), selectedCategories);
                addWidgetToList(treeItem);
                treeItem.setOpen(true);
            }
        } else {
            showIsEmptyLabel();
        }
    }

    /**
     * @see org.opencms.ade.galleries.client.ui.A_CmsListTab#getSortList()
     */
    @Override
    protected List<CmsPair<String, String>> getSortList() {

        List<CmsPair<String, String>> list = new ArrayList<CmsPair<String, String>>();
        list.add(new CmsPair<String, String>(SortParams.tree.name(), Messages.get().key(
            Messages.GUI_SORT_LABEL_HIERARCHIC_0)));
        list.add(new CmsPair<String, String>(SortParams.title_asc.name(), Messages.get().key(
            Messages.GUI_SORT_LABEL_TITLE_ASC_0)));
        list.add(new CmsPair<String, String>(SortParams.title_desc.name(), Messages.get().key(
            Messages.GUI_SORT_LABEL_TITLE_DECS_0)));

        return list;
    }

    /**
     * @see org.opencms.ade.galleries.client.ui.A_CmsListTab#getTabHandler()
     */
    @Override
    protected CmsCategoriesTabHandler getTabHandler() {

        return m_tabHandler;
    }

    /**
     * @see org.opencms.ade.galleries.client.ui.A_CmsListTab#hasQuickFilter()
     */
    @Override
    protected boolean hasQuickFilter() {

        // allow filter if not in tree mode
        return SortParams.tree != SortParams.valueOf(m_sortSelectBox.getFormValueAsString());
    }

    /**
     * Adds children item to the category tree and select the categories.<p>
     * 
     * @param parent the parent item 
     * @param children the list of children
     * @param selectedCategories the list of categories to select
     */
    private void addChildren(CmsTreeItem parent, List<CmsCategoryTreeEntry> children, List<String> selectedCategories) {

        if (children != null) {
            for (CmsCategoryTreeEntry child : children) {
                // set the category tree item and add to parent tree item
                CmsTreeItem treeItem = buildTreeItem(child, selectedCategories);
                if ((selectedCategories != null) && selectedCategories.contains(child.getPath())) {
                    parent.setOpen(true);
                    openParents(parent);
                }
                parent.addChild(treeItem);
                addChildren(treeItem, child.getChildren(), selectedCategories);
            }
        }
    }

    /**
     * Builds a tree item for the given category.<p>
     * 
     * @param category the category
     * @param selectedCategories the selected categories
     * 
     * @return the tree item widget
     */
    private CmsTreeItem buildTreeItem(CmsCategoryTreeEntry category, List<String> selectedCategories) {

        CmsListInfoBean categoryBean = new CmsListInfoBean(
            category.getTitle(),
            CmsStringUtil.isNotEmptyOrWhitespaceOnly(category.getDescription())
            ? category.getDescription()
            : category.getPath(),
            null);
        m_categories.put(category.getPath(), category);
        // set the list item widget
        CmsListItemWidget listItemWidget = new CmsListItemWidget(categoryBean);
        listItemWidget.setIcon(CATEGORY_ICON_CLASSES);
        // the checkbox
        CmsCheckBox checkBox = new CmsCheckBox();
        if ((selectedCategories != null) && selectedCategories.contains(category.getPath())) {
            checkBox.setChecked(true);
        }
        SelectionHandler selectionHandler = new SelectionHandler(category.getPath(), checkBox);
        checkBox.addClickHandler(selectionHandler);
        listItemWidget.addDoubleClickHandler(selectionHandler);
        // set the category tree item and add to list 
        CmsTreeItem treeItem = new CmsTreeItem(true, checkBox, listItemWidget);
        treeItem.setId(category.getPath());
        return treeItem;
    }

    /**
     * Goes up the tree and opens the parents of the item.<p>
     * 
     * @param item the child item to start from
     */
    private void openParents(CmsTreeItem item) {

        if (item != null) {
            item.setOpen(true);
            openParents(item.getParentItem());
        }
    }

    /**
     * Shows the tab list is empty label.<p>
     */
    private void showIsEmptyLabel() {

        CmsSimpleListItem item = new CmsSimpleListItem();
        Label isEmptyLabel = new Label(Messages.get().key(Messages.GUI_TAB_CATEGORIES_IS_EMPTY_0));
        item.add(isEmptyLabel);
        m_scrollList.add(item);
    }
}