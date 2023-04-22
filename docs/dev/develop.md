# Guide for contributors to IDES

## Building IDES

IDES is built with Gradle 8, via the `gradle build` command. Later versions of Gradle may work but are not tested.

The output of the build task produces two files: `IDES-version.zip` and `IDES-version-api.zip` directly under the `build` folder.
The version string is taken from the `gradle.properties` file.

The compiled code targets Java 8 for greater compatibility.

## Code style

The code style follows closely that of the default Eclipse formatter, with the notable exception of using spaces for indentation.
The following is a brief summary:
- Indentation is via 4 spaces (no tabs)
- Max line width is 120 characters
- Curly braces open on the same line
- Single-line **if** statements must be enclosed in curly braces

## Architecture

The main goal of the architecture is to provide the infrastructure to support plugins.
In fact, there should be as few "core" features as possible. The plugin architecture should allow for the following entities:
- Models - any sort of interface-defined objects that can be manipulated or used in computations.
  Examples are: FSAs, event sets, Petri nets, general diagrams, online DES systems, etc.
- Operations - any operation that can be performed on models. Examples are: meet, parallel composition, construct supervisor,
  run a system for N steps, etc.
- Presentations - the actual interface that visualizes and (optionally) allows the modification of models.

The entities should interact through a message channel (with message publishers and subscribers).
A multiple of presentations should be able to concurrently represent a single model.
There is a workspace which keeps track of all models and their presentations which are used by a user at a given time.
The workspace is be model-independent. If a set of DES models needs to have some superstructure
(e.g., a global event set for a bunch of FSAs), this should be implemented as a layer independent of the main workspace.

### Architecture for plugins

The philosophy of the plugin architecture in IDES is: openness over security, ease of plugin implementation over scalability.
The plugins may come in a variety of flavors:
- models
- operations
- presentations
- sub-workspaces (unifying a set of models)
- importers/exporters

The IDES infrastructure provides services to the plugins (such as screen real estate) and the plugins are the active components
(as opposed to being passively driven by IDES). The central point for access to IDES services is the `Hub` class which
provides methods to get handles to the main window, to the workspace, to the IO, etc.

## Implementation

The implementation is done in Java, and the GUI uses Swing/AWT. These technologies may be reconsidered for future IDES
releases, however, at the moment using alternatives should be avoided where possible. The use of external libraries should be minimal
and reliance on native code should be avoided unless absolutely necessary (e.g., we don't feel like implementing a LaTeX compiler).
If an external library is to be used, the license must be compatible with the IDES license; for example,
the GPL, LGPL, Apache, MIT, Mozilla and BSD licenses are compatible with AGPL 3.

### Implementation specifics

- In order to alleviate future improvements to the interface or possible localization, any text strings that will be displayed
  to the user should be put in the `strings.properties` file and the `Hub.string(key)` method should be used to retrieve the string.
- Settings can be automatically managed by the `Hub.persistentData` structure.
- Stuff that needs to be done when IDES is closing should go in `Main.onClose()`.
- The `util.EscapeDialog` implements a Swing dialog box which reacts to the user pressing the Escape key.

## User interface

The paradigm to be used in the user interface is as follows:
- Localized (contextual) interaction.
- Mouse-centric.
- Standard GUI elements with improved integration.
- Consistency.

The user should be able to interact with visual elements locally, in a context-dependent way.
E.g., if there is a transition with an event, the user should be able to do whatever is doable to an event just by interacting
with that event locally, at the transition. They should not be required to go to an "event-editing window/view" to achieve this
(as it is in previous versions of IDES). Of course, this does not mean that such "event-editing windows/views"
should be disposed of: such general views are useful for other purposes.

The UI should be designed so that the user has to touch the keyboard only when absolutely necessary.
Do not spend time on creating keyboard shortcuts if this time can be spent on improving the mouse interaction.

The interface should be built as much as possible with standard GUI elements which behave as expected,
are named using standard monikers and are arranged in a standard way. Some novel interactions will require unusual
(i.e., not common) UI, however, most probably there is a way to implement it using the above constraints.
The trick is not to invent new UI elements, but to find the right integration of the available ones.
Users should feel confident they know what is going on and how their actions will modify the system.

The Windows UI conventions should be followed (as opposed to macOS, GNOME, etc.) The rationale is that the majority of users are acquainted
with the Windows UI even if they are not running this OS. Be consistent with the design choices you and other developers make.

Users should be given reasonable feedback. The interface should support fast interaction. If something is expected to take a long time,
let the user know - by changing the cursor to an hourglass or by displaying a progress bar. When messages are to be displayed to the user,
avoid cryptic jargon. On the other hand, avoid treating the users as kids who should be shielded from the software.
Just use plain language to describe what's going on. See some of the entries in `strings.properties` as examples.

If more than one design option seems viable, create a prototype for each alternative and test with users.

## Testing

Where possible, unit tests and integration test should be included with code changes.

In addition, there is a [Manual test protocol](manual-test.md).

## Release process

When a release is getting readied, it necessary to ensure that:
- The license and readme files are up to date with any new contributors and contain the current year.
- The NOTICE.txt file is up to date with information about the licenses of all external libraries.
- The help files and tutorials are up to date.
- The Javadoc for the API is complete and up to date (i.e., for all classes in the `ides.*` package)
  and the plugin developers guide is up to date.
- The testing documentation is up to date.
- All new code follows the style guide for the project.
- Where possible, there are unit tests for the code changes.

After confirming all items in the above checklist, follow these steps to release a new version of IDES:
1.  Make sure that all outstanding code changes are committed. Modified files with changes that do not belong in the release
    should be reverted to their original version (e.g., `settings.ini` can be reverted with `git checkout settings.ini`).
2.  Update `gradle.properties` with the version for the release.
3.  Update the release notes in `CHANGELOG.txt`, e.g. using the conventions from https://keepachangelog.com .
4.  Make sure that the all IDES plugins listed under `ext.idesPlugins` in `build.gradle` have the correct API compatibility information.
    If the release introduces API changes which are not backward-compatible, refer to the section below for more details.
5.  Commit the changes above with the commit message `IDES Version` (where "Version" stands for the release version, e.g., `IDES 3.1.2`).
6.  Run `gradle clean build` to generate the IDES release.
7.  Perform the manual test and any other relevant testing with the build output.
8.  If all tests succeed, push the commit.
9.  In Github, create a new release with the tag `vVersion` (where "Version" stands for the release version, e.g., `v3.1.2`)
    and upload the `IDES-version.zip` and `IDES-version-api.zip` files from the `build` folder.
10. Increment the version in `gradle.properties` and mark it as SNAPSHOT, e.g., `3.1.3-SNAPSHOT`.
11. Commit the change to `gradle.properties` and push it.

### Releasing API with breaking changes

If the release will introduce API changes which are not backward-compatible, it is necessary to review the
IDES plugins listed under `ext.idesPlugins` in `build.gradle`. Make sure that all listed plugins have the correct
API compatibility information. Specifically, the `apiVerBelow` field has to be set to the version you are releasing
(or lower) for the plugins which do not yet support the new API.

After the new API release, plugin authors can update their plugins. Subsequently, the corresponding entries
under `ext.idesPlugins` can be updated and the newly-compatible plugins can be included in future IDES releases.
