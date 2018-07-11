# GS16_64bit
Control of General Standards 16AO64C with 64bit drivers

Bindings created using JNAerator.

SDK files were tools.h and AO64eintface.h

This project works through all examples provided with the SDK under 16AO64Example.c

output tests are under the operations package.

TODO:
test if changing binding for "write_local_32" from NativeLong to Int reproduces all outputs.
- this could be useful as the binding itself defaults to int for these Instance values.
- we can write hex to long or int, but NativeLong is a (possibly unnecessary) JNA object and needs to be created for EVERY value.
