# Integrated Discrete-Event Systems Software (IDES)

## About

IDES (Integrated Discrete-Event Systems) Software is designed to assist you with
discrete-event problem-solving and with input and output of DES models.
IDES allows you to mimic pen-and-paper drawing of state-transition diagrams,
export your drawings to EPS, PNG, JPEG, LaTeX, Grail+, or TCT formats and perform DES operations.
It was created in the [QDES Lab](https://www.ece.queensu.ca/people/K-Rudie/qdes.html)
at Queen's University, Kingston, Canada under the supervision of Prof. Karen Rudie.

Contributors:
Helen Bretzke, Chris Dragert, Kristian Edlund,
Lenko Grigorov, Chris McAloney, Axel G. Michelsen, Utsav Mital,
Philippe Nguyen, Christian Silvano, Valerie Sugarman,
Sarah-Jane Whittaker, Mike Wood.

See [this notice](NOTICE.txt) for more information about the libraries used by by IDES.

IDES has support for extensions and is bundled with the following plugins:
- [Template Design plugin](https://github.com/lenkog/IDES-TD)

## License

Starting with version 3.1, IDES is released under the [AGPL-3.0 license](agpl-3.0.txt).

## Installation

IDES is platform-independent. You must have installed Java version 8 or higher in order to run IDES.

You can download the latest IDES release from the "Releases" tab in the Github project page.
Download the **"IDES-version.zip"** file for the version you are interested in.
The "IDES-version-api.zip" is for developer who want to develop a plugin for IDES.

If you build IDES yourself, the generated package will be called "IDES-version.zip"
and can be found under the "build" directory.

After downloading or building IDES, extract the package in any location on your hard drive.
A folder, called "IDES-version", will be created which contains all program files.
Double-click on the "IDES-version.jar" file inside this folder to launch the program.
If "jar" files are not associated with Java on your machine, you may need to start a command prompt
in the IDES folder and execute `java -jar IDES-version.jar` to launch the program.

In order to use the LaTeX features of IDES, you have to install LaTeX and GhostScript on your computer.
Full instructions on how to download and install this software can be found
[here](docs/help/LaTeX%20Rendering/index.md).

## Tutorial

A complete tutorial which describes how to use IDES can be found [here](docs/help/IDES%20Tutorial/index.md).
It provides you with the overview necessary to begin drawing and manipulating finite-state machines,
performing operations and more.

## Add-on for developers

IDES has a pluggable architecture that allows you to write your own features and to tailor
IDES to your own DES needs. The developer add-on, available as "IDES-version-api.zip"
contains the API, additional documentation and a guide on writing plugins.
This package is not required for regular users, only those that wish to develop their
own IDES functionality. Some of the documentation is listed below:
- [IDES 2.1 File Format](docs/api/IDESFileFormat.md)
- [File Format for Supervisory Event Set Models](docs/api/EventSetFileFormat.md)
- [Guide to writing plugins for IDES ](docs/api/GuidePlugins.md)

## Contributing

Pull requests are welcome. Please refer to the [Guide for contributors](docs/dev/develop.md) for
information about how to build the project and for notes relevant to IDES developers.

## Known issues

* IDES fails to launch on newer macOS
* Poor automatic placement of event labels on edges
* Occasional poor automatic placement of edges
* Potential problems when using the uniform node size option
* DES operations are not formally verified
* Automatic layout of the result of operations is slow
* Pasting from the clipboard might not always work as expected

## Changelog

You can view the changelog [here](CHANGELOG.txt).
