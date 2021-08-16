/**
 * Copyright (C) 2021 Bonitasoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.migration.version.to7_13_0

import org.bonitasoft.migration.core.MigrationContext
import org.bonitasoft.migration.core.MigrationStep

/**
 * Create new removable pages with empty content
 * The content will be automatically updated during the platform startup
 * Because provided === true
 *
 * @author Emmanuel Duchastenier
 * @author Baptiste Mesta
 * @author Dumitru Corini
 */
class CreateNewPages extends MigrationStep {

    @Override
    def execute(MigrationContext context) {
        final long currentTimeMillis = System.currentTimeMillis()

        context.with {
            databaseHelper.getAllTenants().each {
                tenant ->
                    def tenantId = tenant.id as long
                    insertPageIfMissing(it, currentTimeMillis, "custompage_themeBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_caseoverview", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_layoutBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminApplicationDetailsBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminApplicationListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminBDMBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminCaseDetailsBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminCaseListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminCaseVisuBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminGroupListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminInstallExportOrganizationBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminLicenseBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminMonitoringBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminProcessDetailsBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminProcessListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminProcessVisuBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminProfileListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminResourceListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminRoleListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminTaskDetailsBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminTaskListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminUserDetailsBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_adminUserListBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_home", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_tenantStatusBonita", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_processAutogeneratedForm", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_taskAutogeneratedForm", false, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_error403Bonita", true, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_error404Bonita", true, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_error500Bonita", true, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_layoutWithoutMenuBonita", true, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_applicationDirectoryBonita", true, false, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_userCaseDetailsBonita", true, true, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_userCaseListBonita", true, true, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_processlistBonita", true, true, tenantId)
                    insertPageIfMissing(it, currentTimeMillis, "custompage_tasklist", true, true, tenantId)
            }
        }
    }

    @Override
    String getDescription() {
        return "Create new provided pages with empty content"
    }

    void insertPageIfMissing(MigrationContext migrationContext, long currentTimeMillis, String pageName, boolean editable, boolean removable, long tenantId) {
        migrationContext.with {
            if(sql.firstRow("SELECT count(id) FROM page WHERE tenantId = $tenantId AND name = $pageName")[0] > 0){
                logger.info("A page name $pageName already exists for tenant $tenantId, it will not be replaced by the one provided by the platform.")
                sql.executeUpdate("UPDATE page SET removable = $removable, editable = $editable WHERE name = $pageName AND tenantId = $tenantId")
                return
            }
            sql.executeInsert("""INSERT INTO page(tenantId , id, name, displayName, description, installationDate, 
installedBy, provided, lastModificationDate, lastUpdatedBy, contentName, content, contentType, processDefinitionId, hidden, editable, removable)
VALUES ($tenantId, ${databaseHelper.getAndUpdateNextSequenceId(10120L, tenantId)}, $pageName, 'will be updated', '', $currentTimeMillis, 
-1, ${true/* This is the provided flag that MUST be true in order to let pages be updated at engine startup*/}, $currentTimeMillis, -1, ${pageName + '.zip'}, ${''.getBytes()}, '', 0, ${false}, $editable, $removable)""")
        }
    }
}
