package org.bonitasoft.migration.plugin

import static org.bonitasoft.migration.plugin.VersionUtils.dotted
import static org.bonitasoft.migration.plugin.VersionUtils.semver

import com.github.zafarkhaja.semver.Version
import org.bonitasoft.migration.plugin.db.DatabaseResourcesConfigurator
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

/**
 * @author Baptiste Mesta.
 */
class RunMigrationTask extends JavaExec {

    String bonitaVersion
    boolean isSP

    @Override
    void exec() {
        def testValues = DatabaseResourcesConfigurator.getDatabaseConnectionSystemProperties(project)
        testValues.put("target.version", String.valueOf(bonitaVersion))
        if (semver(bonitaVersion) <= Version.valueOf("7.3.0")) {
            testValues.put("bonita.home", String.valueOf(project.buildDir.absolutePath + File.separator +
                    "bonita-home-" + dotted(bonitaVersion) + File.separator + "bonita-home-to-migrate"))
        }
        setSystemProperties testValues
        logger.info "execute migration with properties $systemProperties"
        setMain "${isSP ? 'com' : 'org'}.bonitasoft.migration.core.Migration"
        logger.info "using classpath:"
        classpath(project.sourceSets.main.output, project.sourceSets.main.runtimeClasspath, project.getConfigurations().getByName("drivers"))
        setDebug System.getProperty("migration.debug") != null
        super.exec()
    }


    def configureBonita(Project project, String bonitaVersion, boolean isSP) {
        this.isSP = isSP
        this.bonitaVersion = bonitaVersion
    }


}
