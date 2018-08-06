# GS16_64bit
Description:
- Control of General Standards 16AO64C with 64bit drivers
- Bindings created using JNAerator.
- SDK files were tools.h and AO64eintface.h
- This project works through all examples provided with the SDK under 16AO64Example.c

Dependencies:
- Java Native Access (JNA) version 4.5.1
- JDK 1.8

File Structure:
- JNAerator generated bindings are under "bindings"
- useful program constants are in "constants"
- "lib.win64" is JNAerator generated and contains mandatory .dlls
- "win32-x86-64" contains mandatory device .dll
- "operations" has an individual class file for each of the example tests provided by General Standards
- "scripts" contains several main methods, some for testing specific modules and one ("main", which calls "example") for executing the examples in "operations" folder.

TODO:
test if changing binding for "write_local_32" from NativeLong to Int reproduces all outputs.
- this could be useful as the binding itself defaults to int for these Instance values.
- we can write hex to long or int, but NativeLong is a (possibly unnecessary) JNA object and needs to be created for EVERY value.
