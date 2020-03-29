# Running FoxTelem w/o a GUI interface

## Prerequisites

First, run the FoxTelem with GUI, to make sure that the settings are
initialized and that one can track the targeted satellite. Alternatively, one
can copy the settings files (`~/.FoxTelem/FoxTelem.properties`) from a working
configuration.

## Command line

Use this command:

`java -jar build/jar/FoxTelem.jar -no-gui -sat HuskySat-1`

This will run FoxTelem in headless mode and it will track the HuskySat-1
satellite. The position of the satellite is dumped in a hardcoded named pipe
`foxtelem-direction-pipe` on Linux and `\\.\pipe\foxtelem-direction-pipe` on
Windows. The pipe must exist before launching foxtelem.
