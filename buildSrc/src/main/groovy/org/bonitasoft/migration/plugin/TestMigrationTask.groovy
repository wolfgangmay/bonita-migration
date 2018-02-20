package org.bonitasoft.migration.plugin

import com.github.zafarkhaja.semver.Version
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

import static org.bonitasoft.migration.plugin.VersionUtils.dotted
import static org.bonitasoft.migration.plugin.VersionUtils.underscored

/**
 * @author Baptiste Mesta.
 */
class TestMigrationTask extends Test {

    private String bonitaVersion
    private boolean isSP


    @Override
    void executeTests() {
        def testValues = [
                "db.vendor"     : String.valueOf(project.database.dbvendor),
                "db.url"        : String.valueOf(project.database.dburl),
                "db.user"       : String.valueOf(project.database.dbuser),
                "db.password"   : String.valueOf(project.database.dbpassword),
                "db.driverClass": String.valueOf(project.database.dbdriverClass),
                "auto.accept"   : "true"
        ]
        if (isSP) {
            // From 7.3.0, 'bonita.client.home' is the default key used by EngineStarterSP to retrieve licenses:
            testValues["bonita.client.home"] = String.valueOf(project.buildDir) + "/licenses"
        }
        if (Version.valueOf(bonitaVersion) <= Version.valueOf("7.3.0")) {
            testValues.put("bonita.home", String.valueOf(project.buildDir.absolutePath + File.separator +
                    "bonita-home-" + dotted(bonitaVersion) + File.separator + "bonita-home-to-migrate"))
        }
        setSystemProperties testValues
        super.executeTests()
    }


    def configureBonita(Project project, String bonitaVersion, boolean isSP) {
        this.isSP = isSP
        this.bonitaVersion = bonitaVersion
        testClassesDirs = project.sourceSets.enginetest.output.classesDirs
        classpath = project.files(project.sourceSets.enginetest.runtimeClasspath,
                project.getConfigurations().getByName(underscored(bonitaVersion)),
                project.getConfigurations().getByName("drivers"))

        if (Version.valueOf(bonitaVersion) < Version.valueOf("7.2.0")) {
            include "**/*Before7_2_0DefaultTest*"
        }else {
            include "**/*After7_2_0DefaultTest*"
        }
        include "**/*To" + underscored(bonitaVersion) + (isSP ? "SP" : "") +"*"
    }

}
