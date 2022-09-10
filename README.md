# vavaUtils

This is a set of Java utilities that I use in several projects.

- jutils contains general tools for i/o (CIF files, Options,...), custom logging, atomic properties, and other general methods to deal with strings, system, etc...

- cellsymm contains crystallographic specific tools to deal with symmetry operations, structure factors and also includes a PD compound database that is used in [d1Dplot](https://github.com/ovallcorba/D1Dplot/) and [d2Dplot](https://github.com/ovallcorba/D2Dplot/). There is more information regarding the database in the user's guide of the d1D and d2Dplot programs and in their respective publications: 

[O. Vallcorba & J. Rius. XRD data visualization, processing and analysis with d1Dplot and d2Dplot software packages. *MDPI Proceedings* 2020, 62 (1), 9 (doi:10.3390/IOCC_2020-07311)](https://www.mdpi.com/2504-3900/62/1/9)

[O. Vallcorba & J. Rius. d2Dplot: 2D X-ray diffraction data processing and analysis for through-the-substrate microdiffraction *J. Appl. Cryst.* 2019, 52, 478–484 (doi:10.1107/S160057671900219X)](https://scripts.iucr.org/cgi-bin/paper?S160057671900219X)


### Dependencies

vavaUtils libraries are completely programmed with JavaTM (www.java.com) using OpenJDK version 11.0.9.1 (GNU General Public License, version 2, with the Classpath Exception: https://openjdk.java.net/legal/gplv2+ce.html). You may find Oracle's free, GPL-licensed, production-ready OpenJDK binaries necessary to use them at https://openjdk.java.net/.

The following 3rd party libraries have been used:
- MigLayout. http://www.miglayout.com
    BSD license: http://directory.fsf.org/wiki/License:BSD_4Clause
- Apache Commons Math. https://commons.apache.org/proper/commons-math/
    Apache License: http://www.apache.org/licenses/LICENSE-2.0

(No changes on the source codes of these libraries have been made, you can download the source codes for these libraries at their respective websites).

### Usage

Clone the project or use the jar file in releases and then import from other projects

## Authors

  - **Oriol Vallcorba**

## Disclaimer

This software is distributed WITHOUT ANY WARRANTY. The authors (or their institutions) have no liabilities in respect of errors in the software, in the documentation and in any consequence of erroneous results or damages arising out of the use or inability to use this software. Use it at your own risk.

## Acknowledgments 

Thanks are due the Spanish "Ministerio de Ciencia e Innovación", to the "Generalitat the Catalunya" and to the ALBA Synchrotron for continued financial support.

## License

This project is licensed under the [GPL-3.0 license](LICENSE.txt)

Citation of the author/program/affiliation would be greatly appreciated when this program helped to your work.