///////////////////////////////////////////////////////////////////////////////
//! \file       main.cpp
//! \brief      Source file implementing generic module test DLL loader
//! \copyright  Beckhoff Automation, Huelshorstweg 20, D-33415 Verl, Germany
//! \version    V1.1
//! \details    Blocks on waiting for the Visual Studio C++ debugger to attach
//! \attention  This file was auto-generated for testing. Do NOT edit manually!
///////////////////////////////////////////////////////////////////////////////
#include "Windows.h"
#include <string.h>

typedef void(*VoidFuncPtr)();

int main(int argc, char *argv[])
{
    if ((argc>1) && (0 == strncmp(argv[1], "Debug", 5)))
    {
        // Wait for the standard C++ debugger in TwinCAT3 to attach
        while (!::IsDebuggerPresent())
            ::Sleep(100);
    }

    // Load and execute the actual module test DLL
    HMODULE handle = LoadLibraryA("Debug/ModuleTests.dll");
    if (handle != NULL)
    {
        VoidFuncPtr tb = (VoidFuncPtr)GetProcAddress(handle, "TestBench");
        if (tb != NULL)
        {
            (*tb)();
        }
        if (handle != NULL)
            FreeLibrary(handle);
    }
    return 0;
}
