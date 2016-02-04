# QBT - Command `importThirdParty`

## Usage

Run the third party import and place the packages in the `3p` repo.

>     $ qbt runArtifact --package misc1.third_party_tools.release bin/thirdPartyTools importThirdParty --logLevel=DEBUG --repo 3p

See [Common Options](qbt-common-options.html) for options shared by most or all commands.

## Description

The third party importer will import java packages from maven central according to its configuration file, which is located in the target repository and called `mc/.config`.  The new packages will be inserted into the `mc` directory of the repo, and namespaced to start with `mc` (for maven central).

Here is an exawmple config file:

>     MODULE:com.google.code.gson:gson:2.3
>     MODULE:com.google.guava:guava:15.0
>     MODULE:junit:junit:4.12
>     MODULE:log4j:apache-log4j-extras:1.2.17
>     MODULE:org.apache.commons:commons-lang3:3.3.2
>     MODULE:org.apache.httpcomponents:httpclient:4.3.5
>     MODULE:org.apache.ivy:ivy:2.4.0
>     MODULE:org.apache.maven:maven-artifact:3.3.1
>     MODULE:org.codehaus.groovy:groovy-all:2.3.6
>     MODULE:org.mockito:mockito-core:1.8.5
>     MODULE:org.objenesis:objenesis:1.3
>     MODULE:org.ow2.asm:asm-all:5.0
>     MODULE:org.slf4j:slf4j-log4j12:1.7.7
>     
>     # Apparently missing dependencies.  It's unclear which of above modules pulled
>     # in each of but they sure as hell don't link check without them.
>     ADD:net.java.dev.jna:platform:*,net.java.dev.jna:jna:0
>     ADD:org.mockito:mockito-core:*,org.apache.ant:ant:0
>     ADD:xalan:xalan:*,org.apache.bcel:bcel:5.2
>     
>     # Apparently stupid dependencies.  
>     REMOVE:org.codehaus.groovy:groovy-all:*,*:*:*
>     LINK_CHECKER_ARGS:mc.org.codehaus.groovy.groovy.all,--whitelistFrom groovy
>     LINK_CHECKER_ARGS:mc.org.codehaus.groovy.groovy.all,--whitelistFrom groovyjarjarasm
>     LINK_CHECKER_ARGS:mc.org.codehaus.groovy.groovy.all,--whitelistFrom org
>     
>     # Rather than include this and fight all of its transitive crap we break it
>     REMOVE:commons-logging:commons-logging:*,avalon-framework:avalon-framework:*
>     LINK_CHECKER_ARGS:mc.commons.logging.commons.logging,--whitelistFrom org/apache/commons/logging/impl/AvalonLogger
>     
>     # Stupid renames.  Note magic "0" version is never imported and always
>     # loses so we end up with whatever the rest of the import specifies.
>     REWRITE:*:*:*,ant:ant-launcher:*,org.apache.ant:ant-launcher:0
>     REWRITE:*:*:*,ant:ant:*,org.apache.ant:ant:0
>     REWRITE:*:*:*,xerces:xerces-impl:*,xerces:xercesImpl:0
>     
>     # Slf4j's static binding is a catastrophe from a safety perspective.  Their API
>     # jar references these and expects some other jar on the classpath to provide
>     # an "implementation" of these classes.  Probably.  We let them go and whatever
>     # happens at runtime is what you get.
>     LINK_CHECKER_ARGS:mc.org.slf4j.slf4j.api,--whitelistTo org/slf4j/impl/StaticLoggerBinder
>     LINK_CHECKER_ARGS:mc.org.slf4j.slf4j.api,--whitelistTo org/slf4j/impl/StaticMDCBinder
>     LINK_CHECKER_ARGS:mc.org.slf4j.slf4j.api,--whitelistTo org/slf4j/impl/StaticMarkerBinder
>     
>     # Looks like test garbage.
>     LINK_CHECKER_ARGS:mc.org.bouncycastle.bcprov.jdk14,--whitelistFrom org/bouncycastle/util/AllTests
>     LINK_CHECKER_ARGS:mc.org.bouncycastle.bcprov.jdk14,--whitelistFrom org/bouncycastle/util/IPTest
>     
>     # Various as-of-yet undocumented kinds of special...
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/BaseXPath
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/JaxenException
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/dom4j/DocumentNavigator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/exml/AttributesIterator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/exml/ChildrenIterator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/exml/DocumentNavigator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/exml/ElementsIterator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/exml/Namespace
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/exml/NamespaceIterator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/expr/DefaultStep
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/jdom/DocumentNavigator
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/jdom/XPathNamespace
>     LINK_CHECKER_ARGS:mc.jaxen.jaxen,--whitelistFrom org/jaxen/pattern/PatternParser
>     LINK_CHECKER_ARGS:mc.logkit.logkit,--whitelistFrom org/apache/log/format/AvalonFormatter
>     LINK_CHECKER_ARGS:mc.logkit.logkit,--whitelistFrom org/apache/log/output/ServletOutputLogTarget
>     LINK_CHECKER_ARGS:mc.logkit.logkit,--whitelistFrom org/apache/log/output/jms/JMSQueueTarget
>     LINK_CHECKER_ARGS:mc.logkit.logkit,--whitelistFrom org/apache/log/output/jms/JMSTopicTarget
>     LINK_CHECKER_ARGS:mc.logkit.logkit,--whitelistFrom org/apache/log/output/jms/ObjectMessageBuilder
>     LINK_CHECKER_ARGS:mc.logkit.logkit,--whitelistFrom org/apache/log/output/jms/TextMessageBuilder
>     
>     ################################################################################
>     # END horrible, legacy, pre-config args
>     ################################################################################
>     
>     # Add new modules below here and generally keep their wacky configuration next
>     # to them.
>     
>     # Altering dependencies is superiour to link checker whitelisting since e.g.
>     # missing dependencies can actually be relevant to the code or mistaken extra
>     # dependencies can pull in unneeded garbage.
>     
>     # Link checker examples can be found above if you must.

As you can see, you can add modules, modify their dependencies when their POM turns out to inevitably be horrifically incorrect, and pass the link-checker arguments to ignore certain "known issues" like a jar containing code that depends upon something that is not present (and the code is never used anyways, so you'd rather ignore the error than fix it).

The importer creates a "build" for each imported package that copies the jar into the artifacts, then runs the link checker to ensure its strong classpath is correct, which is *almost* as good as actually building it from source.

    vi: ft=markdown
