package org.bonitasoft.migration.core

import com.github.zafarkhaja.semver.Version
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files

/**
 * @author Baptiste Mesta
 */
class MigrationTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    private def infos = []
    private def logger = [info: { String message -> infos.add(message) }] as Logger
    private Sql sql = Mock(Sql)
    private MigrationContext migrationContext = Mock(MigrationContext)
    private DisplayUtil displayUtil = Mock(DisplayUtil)
    private File bonitaHome
    private Migration migration
    private MigrationRunner runner = Mock(MigrationRunner)

    def setup() {
        migrationContext.sql >> sql
        migrationContext.logger >> logger
        bonitaHome = temporaryFolder.newFolder()
        migration = Spy(Migration, constructorArgs: [migrationContext, displayUtil])
        migration.createRunner() >> runner
    }

    def "migrate with different version in bonita home and database should throw exception"() {
        given:
        versionInDatabase("7.2.0")
        versionInBonitaHome("7.2.1")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "Versions are not consistent, see logs"
    }

    def "migrate with different version in bonita home and database after 7.3.0 should not throw exception"() {
        given:
        versionInDatabase("7.3.0")
        versionInBonitaHome("7.2.1")
        targetVersion("7.3.1")
        when:
        migration.run(false)
        then:
        noExceptionThrown()
    }

    def "no bonita home set after 7.3.0 should not throw exception"() {
        given:
        versionInDatabase("7.3.0")
        targetVersion("7.3.1")
        when:
        migration.run(false)
        then:
        noExceptionThrown()
    }

    def "no bonita home set before 7.3.0 should throw exception"() {
        given:
        versionInDatabase("7.2.9")
        targetVersion("7.3.1")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "The property bonita.home is neither set in system property nor in the configuration file"
    }

    def "migrate with an unhandled source version should throw exception"() {
        given:
        versionInDatabase("7.0.9")
        versionInBonitaHome("7.0.9")
        targetVersion("7.2.0")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "Sorry the version 7.0.9 can not be migrated using this migration tool"
    }

    def "migrate with an unhandled target version should throw exception"() {
        given:
        versionInDatabase("7.3.0")
        targetVersion("7.10.10")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "7.10.10 is not yet handled by this version of the migration tool"
    }

    def "migrate with a version before 7.0 should throw exception"() {
        given:
        versionInDatabase("6.5.0")
        versionInBonitaHome("6.5.0")
        targetVersion("7.2.0")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "Sorry, but this tool can't manage version under 7.0.0"
    }

    def "same target and source version should throw exception"() {
        given:
        versionInDatabase("7.3.1")
        targetVersion("7.3.1")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "The version is already in 7.3.1"
    }


    def "target before source version should throw exception"() {
        given:
        versionInDatabase("7.3.1")
        targetVersion("7.2.9")
        when:
        migration.run(false)
        then:
        IllegalStateException throwable = thrown()
        throwable.message == "The target version 7.2.9 can not be before source version 7.3.1"
    }

    @Unroll
    def "should migration from #source to #target execute steps #migrationVersions"() {
        given:
        versionInDatabase(source)
        versionInBonitaHome(source)
        targetVersion(target)
        when:
        migration.run(false)
        then:
        1 * runner.setProperty('migrationVersions', ({
            it.collect { it.class.name.split('\\.').last() } == migrationVersions
        }))

        where:
        source  | target  || migrationVersions
        "7.3.0" | "7.3.1" || ["MigrateTo7_3_1"]
        "7.2.9" | "7.3.1" || ["MigrateTo7_3_0", "MigrateTo7_3_1"]
        "7.2.2" | "7.3.0" || ["MigrateTo7_2_9", "MigrateTo7_3_0"]
        "7.2.0" | "7.2.9" || ["MigrateTo7_2_1", "MigrateTo7_2_2", "MigrateTo7_2_9"]
        "7.6.3" | "7.7.1" || ["MigrateTo7_7_0", "MigrateTo7_7_1"]
        "7.7.5" | "7.8.3" || ["MigrateTo7_8_0", "MigrateTo7_8_1", "MigrateTo7_8_2", "MigrateTo7_8_3"]
    }

    def "should throw exception if trying to migrate to 7.7.0"() {
        given:
        versionInDatabase("7.6.3")
        targetVersion("7.7.0")

        when:
        migration.run(false)

        then:
        IllegalStateException throwable = thrown()
        throwable.message == "Migrating to version 7.7.0 is forbidden. Please choose a more recent version"
    }

    def "should NOT throw exception if trying to migrate to 7.7.0 and SysProp \"ignore.invalid.target.version\" provided"() {
        given:
        versionInDatabase("7.6.3")
        targetVersion("7.7.0")
        System.setProperty("ignore.invalid.target.version", "value")

        when:
        migration.run(false)

        then:
        noExceptionThrown()

        cleanup:
        System.clearProperty("ignore.invalid.target.version")
    }

    def "should INDEED throw exception if trying to migrate to 7.6.9 and SysProp \"ignore.invalid.target.version\" provided but 7.6.9 not in invisible TRANSITION VERSION list"() {
        given:
        versionInDatabase("7.6.3")
        targetVersion("7.6.9")
        System.setProperty("ignore.invalid.target.version", "value")

        when:
        migration.run(false)

        then:
        IllegalStateException throwable = thrown()
        throwable.message == "7.6.9 is not yet handled by this version of the migration tool"

        cleanup:
        System.clearProperty("ignore.invalid.target.version")
    }

    def "should allow to migrate from 7.7.0 to 7.7.1"() {
        given:
        versionInDatabase("7.7.0")
        targetVersion("7.7.1")

        when:
        migration.run(true)

        then:
        noExceptionThrown()
    }

    def "should throw exception if trying to migrate to 7.8.2"() {
        given:
        versionInDatabase("7.7.5")
        targetVersion("7.8.2")

        when:
        migration.run(false)

        then:
        IllegalStateException throwable = thrown()
        throwable.message == "Migrating to version 7.8.2 is forbidden. Please choose a more recent version"
    }

    def "should allow to migrate from 7.8.1 to 7.8.3"() {
        given:
        versionInDatabase("7.8.1")
        targetVersion("7.8.3")

        when:
        migration.run(true)

        then:
        noExceptionThrown()
    }

    protected void targetVersion(String s) {
        migrationContext.targetVersion >> Version.valueOf(s)
    }

    protected void versionInDatabase(String s) {
        sql.firstRow(_ as String) >> new GroovyRowResult([version: s])
        migrationContext.sourceVersion >> Version.valueOf(s)
    }

    protected void versionInBonitaHome(String versionInBonitaHome) {
        migrationContext.bonitaHome >> bonitaHome
        def versionFile = bonitaHome.toPath().resolve("engine-server/work/platform/VERSION")
        Files.createDirectories(versionFile.getParent())
        Files.createFile(versionFile)
        versionFile.write(versionInBonitaHome)
    }
}
