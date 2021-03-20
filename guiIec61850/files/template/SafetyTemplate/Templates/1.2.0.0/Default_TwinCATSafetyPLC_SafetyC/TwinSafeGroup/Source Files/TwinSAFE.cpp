////////////////////////////////////////////////////////////////////////////////
//! \file       [!output GROUP_NAME].cpp
//! \brief      Source file of the [!output GROUP_NAME] application module
//! \ingroup    [!output GROUP_NAME]
//! \authors    [!output WORKER]
//! \copyright  Put affiliation and copyright notice here
//! \version    Define version number here
//! \date       [!output CREATION_DATE]
//! \details    Put detailed description of your module implementation here
////////////////////////////////////////////////////////////////////////////////

///\internal////////////////////////////////////////////////////////////////////
//! XML tags <...> enclosed by multiline comment markups /*...*/ are protected
//! for static code analysis. Do NOT remove or reorder any of the protected 
//! section comments defined by source code templates as safe build process may
//! fail otherwise! For further information on how to write compliant Safety C
//! user code please refer to the provided Safety C coding guidelines document!
////////////////////////////////////////////////////////////////////////////////

/*<SafeUserApplicationCppFrontend>*/

#include "[!output GROUP_NAME].h" // Rename according to TwinSAFE group name!

SAFE_MODULE_DEF([!output GROUP_NAME]) // Rename according to TwinSAFE group name!
{

    #pragma region TcRuntimeInterfaceDef

    #pragma region TcInit
    ////////////////////////////////////////////////////////////////////////////
    //! \brief Implementation of the safe user module initialization function
    ////////////////////////////////////////////////////////////////////////////
    /*<TcInit>*/
    VOID CSafeModule::Init()
    {

        // Performs startup checks w.r.t. the build and runtime environment
        StartUpChecks();

        // Put your initialization code here!

    }
    /*</TcInit>*/
    #pragma endregion

    #pragma region TcInputUpdate
    ////////////////////////////////////////////////////////////////////////////
    //! \brief Implementation of the safe user module input update function
    ////////////////////////////////////////////////////////////////////////////
    /*<TcInputUpdate>*/
    VOID CSafeModule::InputUpdate()
    {

        // Put your input update code here!

    }
    /*</TcInputUpdate>*/
    #pragma endregion

    #pragma region TcOutputUpdate
    ////////////////////////////////////////////////////////////////////////////
    //! \brief Implementation of the safe user module output update function
    ////////////////////////////////////////////////////////////////////////////
    /*<TcOutputUpdate>*/
    VOID CSafeModule::OutputUpdate()
    {

        // Put your output update code here!

    }
    /*</TcOutputUpdate>*/
    #pragma endregion

    #pragma region TcCycleUpdate
    ////////////////////////////////////////////////////////////////////////////
    //! \brief Implementation of the safe user module cycle update function
    ////////////////////////////////////////////////////////////////////////////
    /*<TcCycleUpdate>*/
    VOID CSafeModule::CycleUpdate()
    {

        // Put your cycle update code here!

    }
    /*</TcCycleUpdate>*/
    #pragma endregion

    #pragma endregion

    #pragma region UserDefinedFunctionsDef
    /*<UserDefinedFunctionsDef>*/

    // Implement internal functions here!

    #pragma region StartUpChecks
    ////////////////////////////////////////////////////////////////////////////
    //! \brief Predefined user function checking build and runtime environment
    ////////////////////////////////////////////////////////////////////////////
    VOID CSafeModule::StartUpChecks()
    {

        ///\internal////////////////////////////////////////////////////////////
        //! These checks must be adapted manually when your safety project and
        //! application code needs to be migrated to a different release of the
        //! TwinCAT3 Safety Editor, Safety PLC Runtime or Safety C libraries.
        //! By modifying the assertions below in order to accept other release
        //! version numbers you are obliged to perform a safety impact analysis
        //! and extra iterations of your verification and validation process.
        //! REMOVING OF THIS CODE WILL DISABLE ENVIRONMENT CONFORMANCE CHECKS!
        ////////////////////////////////////////////////////////////////////////
        // Check the expected TwinCAT3 XAE version TC3.1 Release 4024
        UINT32 nTcXaeMajorVer = GetTwinCatReleaseVersionU32() & 0xFF000000U;
        DEBUG_ASSERT(TC3XAE_MIN_MAJOR_VER_CHECK, (nTcXaeMajorVer >> 24U) >= 3U);
        DEBUG_ASSERT(TC3XAE_MAX_MAJOR_VER_CHECK, (nTcXaeMajorVer >> 24U) <= 3U);

        UINT32 nTcXaeMinorVer = GetTwinCatReleaseVersionU32() & 0x00FF0000U;
        DEBUG_ASSERT(TC3XAE_MIN_MINOR_VER_CHECK, (nTcXaeMinorVer >> 16U) >= 1U);
        DEBUG_ASSERT(TC3XAE_MAX_MINOR_VER_CHECK, (nTcXaeMinorVer >> 16U) <= 1U);

        UINT32 nTcXaeRelNum = GetTwinCatReleaseVersionU32() & 0x0000FFFFU;
        DEBUG_ASSERT(TC3XAE_MIN_RELEASE_NUM_CHECK, nTcXaeRelNum >= 4024U);
        DEBUG_ASSERT(TC3XAE_MAX_RELEASE_NUM_CHECK, nTcXaeRelNum <= 4024U);

        // Check the expected TwinCAT3 Safety PLC version V1.2
        UINT32 nSafePlcMajorVer = GetSafetyPlcMajorVersionU32();
        DEBUG_ASSERT(TC3SPLC_MIN_MAJOR_VER_CHECK, nSafePlcMajorVer >= 1U);
        DEBUG_ASSERT(TC3SPLC_MAX_MAJOR_VER_CHECK, nSafePlcMajorVer <= 1U);

        UINT32 nSafePlcMinorVer = GetSafetyPlcMinorVersionU32();
        DEBUG_ASSERT(TC3SPLC_MIN_MINOR_VER_CHECK, nSafePlcMinorVer >= 2U);
        DEBUG_ASSERT(TC3SPLC_MAX_MINOR_VER_CHECK, nSafePlcMinorVer <= 2U);

        // Check the expected Safety C Safe Helper Core Library version V2
        UINT32 nCoreLibVer = GetSafeHelperCoreLibVersionU32();
        DEBUG_ASSERT(SAFELIB_MIN_MINOR_VER_CHECK, nCoreLibVer >= 2U);
        DEBUG_ASSERT(SAFELIB_MAX_MINOR_VER_CHECK, nCoreLibVer <= 2U);

        // Check the expected Safety C 32-Bit Floating Point Library version V1
        UINT32 nFloatLibVer = GetSafeHelperFloatLibVersionU32();
        DEBUG_ASSERT(FLOATLIB_MIN_MINOR_VER_CHECK, nFloatLibVer == 1U);
        DEBUG_ASSERT(FLOATLIB_MAX_MINOR_VER_CHECK, nFloatLibVer == 1U);

        // Check the expected Safety C Cephes 32-Bit Math Library version V1
        UINT32 nCephes32LibVer = GetSafeHelperCephesSingleLibVersionU32();
        DEBUG_ASSERT(CEPHES32LIB_MIN_MINOR_VER_CHECK, nCephes32LibVer == 1U);
        DEBUG_ASSERT(CEPHES32LIB_MAX_MINOR_VER_CHECK, nCephes32LibVer == 1U);

    }
    #pragma endregion

    /*</UserDefinedFunctionsDef>*/
    #pragma endregion

    //! Reference to project FCS symbol
    extern UINT32 SAFETY_PROJECT_FCS; // Do NOT read, write or remove!

};

// Rename according to TwinSAFE group name!
SAFE_MODULE_DEF_EXPORT([!output GROUP_NAME]);

/*</SafeUserApplicationCppFrontend>*/
