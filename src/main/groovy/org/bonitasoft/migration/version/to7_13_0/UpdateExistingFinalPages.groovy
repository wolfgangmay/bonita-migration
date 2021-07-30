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
 * Update the 'removable' and 'editable' flogs to false
 * for all existing pages that are now final.
 *
 * @author Emmanuel Duchastenier
 * @author Dumitru Corini
 */
class UpdateExistingFinalPages extends MigrationStep {

    @Override
    def execute(MigrationContext context) {
        context.with {
            sql.executeUpdate("""UPDATE page SET removable = ${false}, editable = ${false}
WHERE name in ('custompage_themeBonita', 'custompage_caseoverview', 'custompage_layoutBonita', 'custompage_adminApplicationDetailsBonita',
'custompage_adminApplicationListBonita', 'custompage_adminBDMBonita', 'custompage_adminCaseListBonita', 'custompage_adminCaseVisuBonita',
'custompage_adminInstallExportOrganizationBonita', 'custompage_adminLicenseBonita', 'custompage_adminMonitoringBonita',
'custompage_adminProcessDetailsBonita', 'custompage_adminProcessVisuBonita', 'custompage_adminUserDetailsBonita', 'custompage_home',
'custompage_tenantStatusBonita', 'custompage_processAutogeneratedForm', 'custompage_taskAutogeneratedForm')
""")
        }
    }

    @Override
    String getDescription() {
        return "Updating pre-7.13 pages that are now final (removable & editable set to false)"
    }
}
