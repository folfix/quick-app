package net.folfas.quickapp

import net.folfas.quickapp.QuickAppApplication
import net.folfas.quickapp.QuickAppProperties
import spock.lang.Specification

class QuickAppApplicationTestsUT extends Specification {

    def 'should error on empty template repo'() {
        given:
            def properties = new QuickAppProperties()
            def appApplication = new QuickAppApplication(properties)

        when:
            appApplication.verifyProperties()

        then:
            def exception = thrown(IllegalArgumentException)
            exception.message == 'Template GIT repository url must be provided'
    }

    def 'should error on both auth methods used'() {
        given:
            def properties = new QuickAppProperties()
            properties.templates.gitRepository.url = 'example'
            def appApplication = new QuickAppApplication(properties)

        when:
            properties.publish.bitbucketRestApi.bearerToken = 'example'
            properties.publish.bitbucketRestApi.username = 'example'
            properties.publish.bitbucketRestApi.password = 'example'
            appApplication.verifyProperties()

        then:
            def exception = thrown(IllegalArgumentException)
            exception.message == 'Do not use username/password and Bearer authentication for Bitbucket in the same time'
    }

    def 'should error on miss username when password provided'() {
        given:
            def properties = new QuickAppProperties()
            properties.templates.gitRepository.url = 'example'
            def appApplication = new QuickAppApplication(properties)

        when:
            properties.publish.bitbucketRestApi.username = 'example'
            appApplication.verifyProperties()

        then:
            def exception = thrown(IllegalArgumentException)
            exception.message == 'Provide publish Bitbucket password'
    }

    def 'should error on miss password when username provided'() {
        given:
            def properties = new QuickAppProperties()
            properties.templates.gitRepository.url = 'example'
            def appApplication = new QuickAppApplication(properties)

        when:
            properties.publish.bitbucketRestApi.password = 'example'
            appApplication.verifyProperties()

        then:
            def exception = thrown(IllegalArgumentException)
            exception.message == 'Provide publish Bitbucket username'
    }
}
