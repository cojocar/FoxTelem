# Running FoxTelem w/o a GUI interface

`java -jar build/jar/FoxTelem.jar -no-gui -sat HuskySat-1`

This will disable any GUI interface and it will track the HuskySat-1 satellite. The
position of the satellite is dumped in a hardcoded named pipe
`foxtelem-direction-pipe` on Linux and `\\.\pipe\foxtelem-direction-pipe` on
Windows. The pipe must exist before launching foxtelem.
