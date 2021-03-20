////////////////////////////////////////////////////////////////////////////////
//! \file       [!output GROUP_NAME]IoData.h
//! \brief      Header file containing struct definitions for I/O alias devices
//! \ingroup    [!output GROUP_NAME]
//! \authors    TwinCAT 3 Safety Editor
//! \version    V1.2
//! \date       [!output CREATION_DATE]
//! \attention  This file was auto-generated for build. Do NOT edit manually!
////////////////////////////////////////////////////////////////////////////////
#pragma once

#include "SafeModuleHelper.h"

NAMESPACE([!output GROUP_NAME])
{

	#pragma region StandardInputAliasDevices
	//! Struct providing input data of the corresponding standard alias devices
	struct StandardInputs
	{
		//! ..\Alias Devices\ErrorAcknowledgement.sds
		BOOL ErrorAcknowledgement;

	};
	#pragma endregion
	


	#pragma region StandardOutputAliasDevices
	//! Struct storing output data for the corresponding standard alias devices
	struct StandardOutputs
	{

	};
	#pragma endregion
	


	#pragma region SafetyInputAliasDevices
	//! Struct providing input data of the corresponding safety alias devices
	struct SafetyInputs
	{

	};
	#pragma endregion
	


	#pragma region SafetyOutputAliasDevices
	//! Struct storing output data for the corresponding safety alias devices
	struct SafetyOutputs
	{

	};
	#pragma endregion
	


	#pragma region TwinSafeGroupExchangeData
	//! Struct storing the TwinSAFE group exchange data
	struct TSGData
	{

	};
	#pragma endregion
	


	//! Safety project FCS symbol
	STATIC UINT32 PROJECT_CRC32_FFFFFFFF = 0xFFFFFFFF;
	STATIC UINT32 &PROJECT_CRC32 = PROJECT_CRC32_FFFFFFFF;
	
};
