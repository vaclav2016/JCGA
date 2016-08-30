# CGA/VGA-graphic game framework for Java

Do You remember old MS-DOS games? You can write Your own game in modern language - Java! This is Java framework for development desktop games in CGA/VGA-style graphic with 320*200 resolution.

Framework work via Swing and support:
1. Transparent upscale.
2. PCX file decoding (one bitplane - use GIMP)
3. Sprites
4. 8*8 bitmap fonts
5. Both CGA pallette
6. Pallette size is up to 256 colors - VGA 320*200*256 is possible.

## Examples

I had implement one example - Tetris game in CGA-style. Please, check https://github.com/vaclav2016/JCGA-Tetris

![Tetris Video Game](screenshot-tetris.png)

## Build

You need Maven to build:

    $ mvn install

## Licensing

Code is licensed under the Boost License, Version 1.0. See each
repository's licensing information for details and exceptions.

http://www.boost.org/LICENSE_1_0.txt
