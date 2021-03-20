////////////////////////////////////////////////////////////////////////////////
//! \file       ModuleTests.h
//! \brief      Header file containing helpers for testing safe modules
//! \authors    TwinCAT 3 Safety Editor
//! \version    V1.2
//! \copyright  Beckhoff Automation, Huelshorstweg 20, D-33415 Verl, Germany
//! \details    Used to build a non-safe module test DLL loaded by TwinCAT3
//! \attention  This file was auto-generated for testing. Do NOT edit manually!
////////////////////////////////////////////////////////////////////////////////
#pragma once

#include <string.h>
#include <assert.h>
#include <stdexcept>

//! Make module name available as a string for comparison and test output
#define STRINGIFY(x)	#x
#define TOSTRING(str)	STRINGIFY(str)
const char strModuleName[] = TOSTRING(MODULE_NAME);

//! Macro magic to get a module specific symbol name for the max branch no. variable
#define __BMAX_VAR(var, module)  var ## _ ## module
#define _BMAX_VAR(var, module) __BMAX_VAR(var, module)
#define BMAX_VAR(var) _BMAX_VAR(var, MODULE_NAME)

//! If not redirected by user, print test report to stdout
FILE *fp = stdout;
#define REDIRECT_TEST_REPORT(file) \
    setvbuf(stdout, NULL, _IONBF, 0); \
    fp = fopen(file, "w");
#define START_TEST_REPORT(module) \
    setvbuf(fp, NULL, _IONBF, 0); \
    fprintf(fp, "===============================================================================\n"); \
    fprintf(fp, "=== START OF MODULE TEST REPORT ===============================================\n"); \
    fprintf(fp, "===============================================================================\n"); \
    fprintf(fp, "Module name : %s\n", strModuleName); \
    fprintf(fp, "Project CRC : 0x%08X\n\n", module##::PROJECT_CRC32);
#define CLOSE_TEST_REPORT() \
    fprintf(fp, "===============================================================================\n"); \
    fprintf(fp, "=== END OF MODULE TEST REPORT =================================================\n"); \
    fprintf(fp, "===============================================================================\n"); \
    fclose(fp);

//! Define is used to export test API functions called by TwinCAT3
#define DLL_EXPORT   extern "C" __declspec( dllexport )

//! Helpers to measure the module test's branch coverage w.r.t. the test bench
extern int BMAX_VAR(nBranches); //!< The highest branch ID to be defined by the DUT code
static long long int *nCounters = NULL; //!< The global branch coverage counter vector
//! This is invoked by safe module code inside a BRANCH_TRACE() statement
#pragma warning ( disable: 4100 ) // Disable unreferenced parameter warning
void BranchTrace(const char* strFile, const char* strFunc, int nLine, int nId)
{
    // Check if current callback was done by the DUT module code
    if ((strlen(strModuleName) <= strlen(strFunc)) &&
        (0 == strncmp(strFunc, strModuleName, strlen(strModuleName))))
    {
        assert(nId < BMAX_VAR(nBranches));
        if (NULL != nCounters)
            nCounters[nId]++; // Increase branch coverage counters if allocated
    }
}
//! Register BranchCoverage() as a callback function to be called by BRANCH_TRACE()
void(*__pUserCallbackFunc__)(const char*, const char*, int, int) = &BranchTrace;
//! User macro to reset the branch coverage counter vector to zero
#define START_COVERAGE_MEASUREMENT() \
    nCounters = new long long int[BMAX_VAR(nBranches)]; \
    memset(nCounters, 0, sizeof(long long int) * BMAX_VAR(nBranches)); \
    nUncovered = 0;
//! User macros to trigger computation and output of branch coverage results
#define PRINT_COVERAGE_COUNTERS() \
    PrintCoverageCounters(0, BMAX_VAR(nBranches) - 1);
#define PRINT_COVERAGE_COUNTERS_WITH_RANGE(startId, endId) \
    PrintCoverageCounters(startId, endId);
static void PrintCoverageCounters(int startId, int endId)
{
    fprintf(fp, "=== COVERAGE COUNTERS =========================================================\n");
    if (0 < BMAX_VAR(nBranches))
    {
        assert(startId <= endId);
        assert(startId >= 0);
        assert(endId < BMAX_VAR(nBranches));
        assert(NULL != nCounters); // Assumes prior call of START_COVERAGE_MEASUREMENT()
        // Print all branch counter values for the given ID range
        for (int i = startId; i <= endId; i++)
            fprintf(fp, "%d,%lld\n", i, nCounters[i]);
        fprintf(fp, "\n");
    }
    else
    {
        fprintf(fp, "No branch counters to print as no BRANCH_TRACE() statements were found!\n\n");
    }
}
#define STOP_COVERAGE_MEASUREMENT() \
    StopCoverageMeasurement(0, BMAX_VAR(nBranches) - 1);
#define STOP_COVERAGE_MEASUREMENT_WITH_RANGE(startId, endId) \
    StopCoverageMeasurement(startId, endId);
static int nUncovered = 0; //!< Global counter for uncovered branches
static void StopCoverageMeasurement(int startId, int endId)
{
    fprintf(fp, "=== COVERAGE SUMMARY ==========================================================\n");
    if (0 < BMAX_VAR(nBranches))
    {
        assert(startId <= endId);
        assert(startId >= 0);
        assert(endId < BMAX_VAR(nBranches));
        assert(NULL != nCounters); // Assumes prior call of START_COVERAGE_MEASUREMENT()
        int nRange = endId - startId + 1;

        // Check the the branch coverage counters and print results to stdout
        for (int i = startId; i <= endId; i++)
        {
            if (nCounters[i] < 1) // Each branch should be covered at least once!
            {
                nUncovered++;
                fprintf(fp, "WARNING: Branch ID %d not covered by module tests!\n", i);
            }
        }
        if (nUncovered > 0)
            fprintf(fp, "See ModuleDatabase.saxml file in Anaylsis Files folder for ID to source line mapping!\n");
        // Compute percentage of covered branches w.r.t. total no. of branches in given ID range
        float rCoverage = 100.0f * (float)(nRange - nUncovered) / (float)(nRange);
        fprintf(fp, "Range start ID     : %d\n", startId);
        fprintf(fp, "Range end ID       : %d\n", endId);
        fprintf(fp, "Total branches     : %d\n", nRange);
        fprintf(fp, "Covered branches   : %d\n", nRange - nUncovered);
        fprintf(fp, "Uncovered branches : %d\n", nUncovered);
        fprintf(fp, "Branch coverage    : %3.2f%%\n\n", rCoverage);
        
        if (NULL != nCounters)
            delete nCounters;
    }
    else
    {
        fprintf(fp, "Cannot compute test coverage as no BRANCH_TRACE() statements were found!\n\n");
    }
}

//! Global counters for passed and failed test cases
static int nPassed = 0;
static int nFailed = 0;

//! DLL export APIs used by TwinCAT3 to conduct module tests from extern
DLL_EXPORT int GetPassedTestsCounter()
{
    return nPassed;
}
DLL_EXPORT int GetFailedTestsCounter()
{
    return nFailed;
}
DLL_EXPORT int GetCoveredBranchesCounter()
{
    return BMAX_VAR(nBranches);
}
DLL_EXPORT int GetUncoveredBranchesCounter()
{
    return nUncovered;
}

DLL_EXPORT void ResetTestCounters()
{
    nPassed = 0;
    nFailed = 0;
}
DLL_EXPORT long long int *GetBranchCoverageCounters()
{
    return nCounters;
}
DLL_EXPORT void ResetBranchCoverageCounters()
{
    START_COVERAGE_MEASUREMENT();
}
bool __stdcall DllMain()
{
    // Deactivate standard output buffering for immediate output to VS output pane
    setvbuf(stdout, NULL, _IONBF, 0);
    return true;
}

//! Helper macro to set output structs to zero after cycle execution. This macro
//! is used by the test step macro to emulate behavior of the Safety PLC runtime.
#define SET_OUTPUTS_TO_ZERO(dut) \
    memset(&dut.sStandardOutputs, 0, sizeof(dut.sStandardOutputs)); \
    memset(&dut.sSafetyOutputs, 0, sizeof(dut.sSafetyOutputs));

//! Use the test step macro within test case definitions to mark start of
//! consecutive teststeps (e.g. module initialization and task cycles).
//! Resets the module's standard and safe output structs to zero.
static int nTestStep = 0;
#define MODULE_TEST_STEP(id) \
    SET_OUTPUTS_TO_ZERO(DUT); \
    nTestStep = id;

//! Use the test assert macro to evaluate if a test assumption holds. Throws
//! a TestAssertEx exception type to distinguish from failed DEBUG_ASSERT().
#define TEST_ASSERT(cond) \
    ((cond) ? true : throw(new TestAssertEx(#cond, __FUNCTION__, __FILE__, __LINE__)));
//! Exception class thrown by TEST_ASSERT(cond) to be caught by module test
class TestAssertEx : public AssertFailureEx {
public:
    TestAssertEx(const char *cond, const char *func, const char *file, int line)
        : AssertFailureEx(cond, func, file, line) { }
};

//! Use test bench define macro to define the test bench entry point
#define MODULE_TEST_BENCH_DEF() \
    DLL_EXPORT void TestBench()

//! Use the test group define macro to define test groups being composed of
//! multiple test cases. Use the test group run macro to define the test bench.
#define MODULE_TEST_GROUP_DEF(ID) \
    DLL_EXPORT void MODULE_TEST_GROUP_##ID(int id = ID)
#define MODULE_TEST_GROUP_RUN(ID) \
    nPassed = 0; nFailed = 0; \
    MODULE_TEST_GROUP_##ID(); \
    PrintTestGroupResults(#ID, nPassed + nFailed, nPassed, nFailed);

//! Use the test case define macro to define test cases being composed of one
//! ore more teststeps. Use the test case run macros to define test groups.
#pragma warning ( disable: 4700 ) // Disable uninitialized variable warning
#define MODULE_TEST_CASE_DEF(ID) \
    void TEST_CASE_##ID(MODULE_TYPE DUT, int id = ID)
#define MODULE_TEST_CASE_RUN(ID) \
    MODULE_TYPE DUT_##ID; \
    try { \
        TEST_CASE_##ID(DUT_##ID); \
        nPassed++; \
    } \
    catch (TestAssertEx *ex) \
    { \
        PrintAssertionFailure(#ID, nTestStep, ex); \
        nFailed++; \
        delete ex; \
    } \
    catch (DebugAssertEx *ex) \
    { \
        PrintAssertionFailure(#ID, nTestStep, ex); \
        nFailed++; \
        delete ex; \
    } \
    catch (FailsafeAssertEx *ex) \
    { \
        PrintAssertionFailure(#ID, nTestStep, ex); \
        nFailed++; \
        delete ex; \
    }
//! Fault injection test cases are expected to fail within a specified test
//! step by an expected fail-safe assertion in order to count being passed.
#define MODULE_FIT_CASE_RUN(ID, FAILSTEP, ASSERTION) \
    MODULE_TYPE DUT_##ID; \
    try { \
        TEST_CASE_##ID(DUT_##ID); \
        nFailed++; \
    } \
    catch (TestAssertEx *ex) \
    { \
        PrintAssertionFailure(#ID, nTestStep, ex); \
        nFailed++; \
        delete ex; \
    } \
    catch (DebugAssertEx *ex) \
    { \
        PrintAssertionFailure(#ID, nTestStep, ex); \
        nFailed++; \
        delete ex; \
    } \
    catch (FailsafeAssertEx *ex) \
    { \
        PrintAssertionFailure(#ID, nTestStep, ex); \
        if ((nTestStep == FAILSTEP) && (0 == strcmp(ex->strId, #ASSERTION))) \
            nPassed++; \
        else \
            nFailed++; \
        delete ex; \
    }

//! Helper functions used for printing test results to stdout
static inline void PrintAssertionFailure(const char *tcid, int step, NamedAssertFailureEx *ex)
{
    fprintf(fp, "Test case : %s\n", tcid);
    fprintf(fp, "Test step : %d\n", step);
    fprintf(fp, "Assertion : %s\n", ex->strId);
    fprintf(fp, "Condition : %s\n", ex->strCond);
    fprintf(fp, "Function  : %s\n", ex->strFunc);
    fprintf(fp, "Location  : %s", ex->strFile);
    fprintf(fp, ":%d\n\n", ex->nLine);
}
static inline void PrintAssertionFailure(const char *tcid, int step, TestAssertEx *ex)
{
    fprintf(fp, "=== TEST ASSERT FAILED ========================================================\n");
    fprintf(fp, "Test case : %s\n", tcid);
    fprintf(fp, "Test step : %d\n", step);
    fprintf(fp, "Condition : %s\n", ex->strCond);
    fprintf(fp, "Function  : %s\n", ex->strFunc);
    fprintf(fp, "Location  : %s", ex->strFile);
    fprintf(fp, ":%d\n\n", ex->nLine);
}
static inline void PrintAssertionFailure(const char *tcid, int step, DebugAssertEx *ex)
{
    fprintf(fp, "=== DEBUG ASSERT FAILED =======================================================\n");
    PrintAssertionFailure(tcid, step, (NamedAssertFailureEx*)ex);
}
static inline void PrintAssertionFailure(const char *tcid, int step, FailsafeAssertEx *ex)
{
    fprintf(fp, "=== FAILSAFE ASSERT FAILED ====================================================\n");
    PrintAssertionFailure(tcid, step, (NamedAssertFailureEx*)ex);
}
static inline void PrintTestGroupResults(const char *tgid, int total, int passed, int failed)
{
    fprintf(fp, "=== TEST GROUP RESULTS ========================================================\n");
    fprintf(fp, "Test group   : %s\n", tgid);
    fprintf(fp, "Total tests  : %d\n", total);
    fprintf(fp, "Passed tests : %d\n", passed);
    fprintf(fp, "Failed tests : %d\n\n", failed);
}
