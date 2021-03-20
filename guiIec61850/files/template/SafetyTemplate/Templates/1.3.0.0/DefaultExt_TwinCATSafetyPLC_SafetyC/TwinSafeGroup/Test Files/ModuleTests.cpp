///////////////////////////////////////////////////////////////////////////////
//! \file       ModuleTests.cpp
//! \brief      Source file with module test definitions for [!output GROUP_NAME]
//! \authors    [!output WORKER]
//! \copyright  Put affiliation and copyright notice here
//! \version    Define version number here
//! \date       [!output CREATION_DATE]
//! \details    Put detailed description of your module tests here
///////////////////////////////////////////////////////////////////////////////

//! Define name of safe module under test (according to TwinSafe group name)
#define MODULE_NAME     [!output GROUP_NAME]
#define MODULE_TYPE     [!output GROUP_NAME]::CSafeModule

#include "[!output GROUP_NAME].h"
#include "ModuleTests.h"

//! Definition of test group IDs
#define TG_ID_0     0

//! Definition of test case IDs
#define TC_ID_0     0

///////////////////////////////////////////////////////////////////////////////
//! \brief   TC_ID_0
//! \test    Example of a module test case sequence with 1000 runtime cycles.
//!          This is the default test case sequence triggered when clicking on
//!          'Test Files -> Module Tests -> Build/Debug' in TC3 Safety Editor.
///////////////////////////////////////////////////////////////////////////////
MODULE_TEST_CASE_DEF(TC_ID_0)
{

    // Test case sequence starts with a test step 0 to prepare preconditions
    MODULE_TEST_STEP(0);

    // Call initilazation interface to set module variables to default values
    DUT.Init();

    // Add TEST_ASSERT() post initialization checks for module variables here

    // Execute a test sequence with 1000 execution cycles
    for (int nCycle = 1; nCycle <= 1000; nCycle++)
    {
        // Increase test step ID according to task cycle counter
        MODULE_TEST_STEP(nCycle);

        // Set test step data for safe and non-safe module inputs here

        // Increase safe timer w.r.t. assumed task period (in ms)
        DUT.u16SafeTimer = (nCycle * 5) % 65536U; 

        // Trigger cyclic task interface like the safe runtime would do
        DUT.InputUpdate();
        DUT.CycleUpdate();
        DUT.OutputUpdate();

        // Add TEST_ASSERT() invariant checks on module variables here
        // Add TEST_ASSERT() checks on test output data w.r.t. input data here

    }

}

///////////////////////////////////////////////////////////////////////////////
//! \brief Test group TG_ID_0 definition with default test case TC_ID_0
///////////////////////////////////////////////////////////////////////////////
MODULE_TEST_GROUP_DEF(TG_ID_0)
{

    // Run default test case TC_ID_0
    MODULE_TEST_CASE_RUN(TC_ID_0);

}

///////////////////////////////////////////////////////////////////////////////
//! \brief Top-level test bench definition with default test group TG_ID_0
///////////////////////////////////////////////////////////////////////////////
MODULE_TEST_BENCH_DEF()
{

	// Start test report printing module name and project CRC
	START_TEST_REPORT(MODULE_NAME);

	// Reset branch counters for branch coverage measurement
	START_COVERAGE_MEASUREMENT();

	// Run default test group TG_ID_0
	MODULE_TEST_GROUP_RUN(TG_ID_0);

	// Print coverage counters and summary with list of uncovered branches
	PRINT_COVERAGE_COUNTERS();
	STOP_COVERAGE_MEASUREMENT();

	// Close the test report 
	CLOSE_TEST_REPORT();

}