VERSION_NUMBER = '1.0-SNAPSHOT'

JAVAEE = 'javax:javaee-api:jar:7.0'
OSGI = group('osgi_R4_core', 'osgi_R4_compendium', :under => 'org.osgi', :version => '1.0')
LOG4J2 = group('log4j-api', 'log4j-core', :under => 'org.apache.logging.log4j', :version => '2.3')
COMMONS = 'org.apache.commons:commons-lang3:jar:3.4'

TESTNG = 'org.testng:testng:jar:6.9.4'
MOCKITO = transitive('org.mockito:mockito-core:jar:2.0.26-beta')
ASSERTJ = 'org.assertj:assertj-core:jar:3.1.0'

repositories.remote << 'http://uk.maven.org/maven2'

define 'identity' do
	project.group = 'onl.identitas'
	project.version = VERSION_NUMBER

	define 'identity-ejb-api' do
		compile.with JAVAEE, OSGI, LOG4J2, COMMONS

		test.using :testng
		test.with TESTNG, MOCKITO, ASSERTJ

		package :jar
		package_with_sources
		package_with_javadoc
	end
	
	define 'identity-ejb' do
		compile.with JAVAEE, OSGI, LOG4J2, COMMONS

		test.using :testng
		test.with TESTNG, MOCKITO, ASSERTJ

		package :jar
		package_with_sources
		package_with_javadoc
	end

	define 'identity-web' do
		compile.with JAVAEE, OSGI, LOG4J2, project('identity-ejb')

		test.using :testng
		test.with TESTNG

		package :war
		package_with_sources
		package_with_javadoc
	end

	package(:ear).add :ejb => project('identity-ejb'), :war => project('identity-web')
end