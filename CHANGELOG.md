
Changelog
=========

### 3.0.0

 - Minimum supported version of Java is now Java 8
   ([#213](https://github.com/ultraq/thymeleaf-layout-dialect/issues/213))
 - Deprecated `layout:decorator` processor has been deleted
   ([#95](https://github.com/ultraq/thymeleaf-layout-dialect/issues/95))
 - Deprecated `$DECORATOR_TITLE` constant has been deleted
   ([#95](https://github.com/ultraq/thymeleaf-layout-dialect/issues/95))
 - `<head>` merging strategies now respect the `<title>` position by default
   ([#177](https://github.com/ultraq/thymeleaf-layout-dialect/issues/177))
 - Deprecated `layout:collect`
 - [Java] Added module-info with name `nz.net.ultraq.thymeleaf.layoutdialect`
   instead of `Automatic-Module-Name`. Also see
   ([#171](https://github.com/ultraq/thymeleaf-layout-dialect/issues/171))

Version 3.0 of the layout dialect written in java is a full Java module.
synchronize the module name from upstream as it is a replacement library.

Check the migration guide out on the 
[original documentation site](https://ultraq.github.io/thymeleaf-layout-dialect/migrating-to-3.0/) 
to help make the assessment of what an upgrade to 3.0 would entail for you.


### 2.5.3
 - [Java] A version bump without change anything, for upstream fixes a groovy security issue.


### 2.5.2

 - [Java] Fix MethodHandles.publicLookup() issue on Java 8
 - Fix from [@silkentrance](https://github.com/silkentrance) for re-declared
   layout fragments in deep hierarchies, where the layout dialect would return
   the wrong fragment from the template hierarchy
   ([#200](https://github.com/ultraq/thymeleaf-layout-dialect/issues/200))


### 2.5.1

 - [Java] Added an `Automatic-Module-Name` of `nz.net.ultraq.thymeleaf`.
 - [Java] Call groovy metaprogramming on method `IContext.getPrefixForDialect` if available.
 - Fixed the fragment cache not being cleared when dispatching/forwarding to the
   error page
   ([#189](https://github.com/ultraq/thymeleaf-layout-dialect/issues/189))
 - Revamped the documentation website on https://ultraq.github.io/thymeleaf-layout-dialect/
   ([#204](https://github.com/ultraq/thymeleaf-layout-dialect/issues/204))
 - Rolled the benchmark project into this repo so that performance testing can
   be done right next to the code
   ([#192](https://github.com/ultraq/thymeleaf-layout-dialect/issues/192))
 - Migrated unit tests from JUnit to Spock
   ([#193](https://github.com/ultraq/thymeleaf-layout-dialect/issues/193))


### 2.5.0
 - [Java] It was not released due to an upstream bug.


### 2.4.1

 - Allow passing of values up to layout templates using fragment parameters
   ([#157](https://github.com/ultraq/thymeleaf-layout-dialect/issues/157))
 - Updated how `<head>` element sorting was done so that the `<title>` can now
   optionally be put as the first element, instead of always making it first.
   Also created 2 new sorting strategies that reflect this respecting of the
   `<title>` and other element positions.
   ([#176](https://github.com/ultraq/thymeleaf-layout-dialect/issues/176))
 - Fixed a bug around nested elements which arose in 2.3.0
   ([#178](https://github.com/ultraq/thymeleaf-layout-dialect/issues/178))
 - Fixed a bug when self-closing `<html>` tags are used as a root element
   ([#173](https://github.com/ultraq/thymeleaf-layout-dialect/issues/173))
 - Added an experimental option to allow developers to opt-out of the automatic
   `<head>` merging that normally occurs, useful if wanted to manage that
   section using other Thymeleaf processors
   ([#165](https://github.com/ultraq/thymeleaf-layout-dialect/issues/165))


### 2.4.0
- [Java] It was not released due to an upstream bug.


### 2.3.0

 - Verification that the layout dialect plays well with Java 9 and Spring 5,
   involved a patch upgrade of Groovy to 2.4.13
   ([#161](https://github.com/ultraq/thymeleaf-layout-dialect/issues/161))
 - Contribution from [@Vineg](https://github.com/Vineg) to add a
   `layout:collect`/`data-layout-collect` processor that accumulates encountered
   fragments of the same name.  Documentation is still pending, but eager devs
   can take a look at the PR for more details
   ([#166](https://github.com/ultraq/thymeleaf-layout-dialect/pull/166))
 - A few tool updates for linting, automated testing, and code coverage w/
   Travis CI (included dropping the JDK7 build as I could no longer get a
   supported configuration working on Travis, but the layout dialect still aims
   to support Java 7 for the remainder of the 2.x releases).


### 2.2.2

 - Another decorate processor root element check fix for deep hierarchies that
   include a `th:with` attribute processor (which gets mutated by the various)
   decoration processes)
   ([#127](https://github.com/ultraq/thymeleaf-layout-dialect/issues/127))
 - Updated [thymeleaf-expression-processor](https://github.com/ultraq/thymeleaf-expression-processor),
   which includes a `null` check for parsing fragment expressions, a potential
   fix for ([#151](https://github.com/ultraq/thymeleaf-layout-dialect/issues/151))


### 2.2.1

 - Fix decorate processor root element check when interacting with high-priority
   custom dialects
   ([#127](https://github.com/ultraq/thymeleaf-layout-dialect/issues/127))


### 2.2.0

 - Rework how titles are handled to support inline expressions in Thymeleaf 3
   ([#145](https://github.com/ultraq/thymeleaf-layout-dialect/issues/145))
 - Now that the complete title cannot be known during execution of the title
   pattern processor, remove the exposed "layout context" object which contained
   the title values.
   ([#147](https://github.com/ultraq/thymeleaf-layout-dialect/issues/147))
 - Implement a more accurate way to check if the decorate processor is in the
   root element of a template
   ([#127](https://github.com/ultraq/thymeleaf-layout-dialect/issues/127))


### 2.1.2

 - Fix insertion of elements into a `<head>` section that is empty, ie: `<head></head>`
   ([#144](https://github.com/ultraq/thymeleaf-layout-dialect/issues/144))


### 2.1.1

 - Simplify and fix the "model level" counting algorithm after a better
   understanding of attoparser and how it works
   ([#142](https://github.com/ultraq/thymeleaf-layout-dialect/issues/142),
   [#143](https://github.com/ultraq/thymeleaf-layout-dialect/issues/143))


### 2.1.0

 - Be less strict with HTML templates that are auto-balanced by Attoparser
   (usually a result of not knowing which HTML elements cause auto-closing
   behaviours), instead only using tags that are in the original templates to
   influence the "model level".  While this was a great tool for learning more
   about the HTML spec when it errors, it is more in line with how Thymeleaf
   behaves
   ([#138](https://github.com/ultraq/thymeleaf-layout-dialect/issues/138))
 - Reveal the processed content and layout title values on the `layout` object
   ([#137](https://github.com/ultraq/thymeleaf-layout-dialect/issues/137))
 - **Huge** improvements to the memory profile of the layout dialect
   ([#102](https://github.com/ultraq/thymeleaf-layout-dialect/issues/102),
   [#139](https://github.com/ultraq/thymeleaf-layout-dialect/issues/139))

What follows is a summary of the performance improvements in 2.1.0.  For details
such as the test methodology and changes made, see the full release notes at:
https://github.com/ultraq/thymeleaf-layout-dialect/releases/tag/2.1.0

#### Thymeleaf Layout Dialect 2.0.4

![memory usage 2 0 4](https://cloud.githubusercontent.com/assets/1686920/20034461/c17b6eb8-a423-11e6-8fe0-d2a5572f3b8c.png)

Main takeaways:
 - The JMeter test took about 3 minutes to complete (started around the 30
   second mark), with requests taking an average of 1.674 seconds each
 - Old generation space at 99MB
 - 35 garbage collections
 - 27 million object allocations
 - 4 seconds spent in GC
 - Several items taking over 10MB of retained memory (none of them appearing as
   dominators however, so are potentially GC'able, but don't seem to have been collected)

   ![screen shot 2016-11-06 at 1 33 32 pm](https://cloud.githubusercontent.com/assets/1686920/20034519/aaf7123a-a425-11e6-9a87-857bd3960dc1.png)

 - Majority of the object allocations taking place in the `IModelExtensions.findModel`
   closure, which uses a Groovy feature of dynamic metaclass creation

   ![screen shot 2016-11-06 at 1 36 02 pm](https://cloud.githubusercontent.com/assets/1686920/20034527/152d07b8-a426-11e6-9a1c-78143ed44895.png)

#### Thymeleaf Layout Dialect 2.1.0

![memory usage 2 1 0-snapshot](https://cloud.githubusercontent.com/assets/1686920/20034539/9449f510-a426-11e6-86cc-0fa2ab52e8fe.png)

Differences:
 - The JMeter test took about 1 minute to complete (also started around the 30
   second mark), with requests taking an average of 452ms to complete **(at
   least 3x faster)**
 - Old generation space at 22MB **(memory footprint 1/5th the size)**
 - 21 garbage collections **(40% less GCs)**
 - 7.1 million object allocations **(74% less objects created)**
 - 1 second spent in GC **(75% less time spent in GC)**
 - Only 1 item taking over 10MB of retained memory (dominator profile looking
   mostly the same however)

   ![screen shot 2016-11-06 at 1 51 37 pm](https://cloud.githubusercontent.com/assets/1686920/20034609/6496e1c8-a428-11e6-9678-4544303b5d97.png)

 - Majority of the object allocations no longer in a Groovy dynamic meta class
   method, but in one of Thymeleaf's utility projects, [unbescape](http://www.unbescape.org/)

   ![screen shot 2016-11-06 at 1 54 42 pm](https://cloud.githubusercontent.com/assets/1686920/20034622/cf95185a-a428-11e6-8999-894cabde6cde.png)


### 2.0.5

 - Upgrade thymeleaf-expression-processor to 1.1.2, which includes a fix for
   multiline fragment expressions
   ([#140](https://github.com/ultraq/thymeleaf-layout-dialect/issues/140))
 - Use Thymeleaf's `AssignationUtils` class for parsing variable declarations so
   that variable declarations behave the same way they do in Thymeleaf
   ([#126](https://github.com/ultraq/thymeleaf-layout-dialect/issues/126))


### 2.0.4

 - Have the `layout:title-pattern` processor work when using `th:utext`/`data-th-utext`
   on the `<title>` tag as well
   ([#136](https://github.com/ultraq/thymeleaf-layout-dialect/issues/136))


### 2.0.3

 - Kill the rise in memory usage found by
   ([#134](https://github.com/ultraq/thymeleaf-layout-dialect/issues/134))


### 2.0.2

 - Convert `FragmentProcessor` from a model processor to a tag processor to get
   around model level problems when inserting fragments that may alter the model
   level
   ([#129](https://github.com/ultraq/thymeleaf-layout-dialect/issues/129))
 - Fix the merging of the `<head>` section in multi-level layouts
   ([#130](https://github.com/ultraq/thymeleaf-layout-dialect/issues/130))
 - Respect the runtime-configured prefix
   ([#103](https://github.com/ultraq/thymeleaf-layout-dialect/issues/103))


### 2.0.1

 - Fix `NullPointerException` when using any of the `include`/`insert`/`replace`
   processors with a full template fragment expression
   ([#120](https://github.com/ultraq/thymeleaf-layout-dialect/issues/120))
 - Restored support for multi-level layouts
   ([#121](https://github.com/ultraq/thymeleaf-layout-dialect/issues/121))
 - Reduced logging of backwards compatibility warnings to log just the first
   time the compatibility was encountered
   ([#124](https://github.com/ultraq/thymeleaf-layout-dialect/issues/124))
 - Improved memory usage by not being so wasteful of objects in a few parts of
   the codebase, which in turn fixed a potential memory leak
   ([#122](https://github.com/ultraq/thymeleaf-layout-dialect/issues/122))
 - Fixed a backwards compatibility problem when using `layout:decorator`
   ([#125](https://github.com/ultraq/thymeleaf-layout-dialect/issues/125))


### 2.0.0

 - [Java] Minimum supported version of Java is now Java 1.7
 - Layout dialect rewritten to support Thymeleaf 3
   ([#68](https://github.com/ultraq/thymeleaf-layout-dialect/issues/68),
   [Layout Dialect 2.0 milestone](https://github.com/ultraq/thymeleaf-layout-dialect/milestone/6?closed=1))
 - `layout:decorator` processor renamed to `layout:decorate`, `$DECORATOR_TITLE`
   renamed to `$LAYOUT_TITLE`
   ([#95](https://github.com/ultraq/thymeleaf-layout-dialect/issues/95))
 - Deprecated `layout:include` processor for the new `layout:insert` processor
   ([#107](https://github.com/ultraq/thymeleaf-layout-dialect/issues/107))
 - New documentation site created to hold what was turning into a gigantic
   readme!
   ([#115](https://github.com/ultraq/thymeleaf-layout-dialect/issues/115))

Upgrading to 2.0?  I've written a migration guide to help make the transition
easier.  Check it out on the new documentation pages site, here:
https://ultraq.github.io/thymeleaf-layout-dialect/MigrationGuide.html

As of release there are still some tests that have been disabled and may
cause regressions for these use cases.  They are listed here:
https://github.com/ultraq/thymeleaf-layout-dialect/blob/d4f57d08cbf5c70a33cfa45283015811c90a3765/Tests/nz/net/ultraq/thymeleaf/tests/LayoutDialectTestExecutor.groovy#L55-L61
One is a very specific and rare case, another is an undocumented use of the
layout dialect, and the commented one I don't feel I should fix as it feels like
the incorrect use of an element.  For those first 2 cases, I'll work on them in
upcoming patches so they don't hold up the release.


### 1.4.0
 - [Java] Initial release, support JDK8+ only.
