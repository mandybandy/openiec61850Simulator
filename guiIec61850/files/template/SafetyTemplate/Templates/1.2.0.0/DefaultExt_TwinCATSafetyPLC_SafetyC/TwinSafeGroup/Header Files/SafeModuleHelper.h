////////////////////////////////////////////////////////////////////////////////
//! \file       SafeModuleHelper.h
//! \brief      Header file with Safety C built-in types, macros and libraries
//! \authors    TwinCAT 3 Safety Editor
//! \version    V1.2
//! \copyright  Beckhoff Automation, Huelshorstweg 20, D-33415 Verl, Germany
//! \details    Some helper macros can be custom-defined by preprocessor flags
//!             in order to provide integration with module testing frameworks.
//!             In any case, module test results need to be validated against
//!             the final software release being executed on target hardware.
//! \attention  This file was auto-generated for build. DO NOT EDIT manually!
////////////////////////////////////////////////////////////////////////////////
#pragma once

#pragma region SafeIntegerDataTypes
////////////////////////////////////////////////////////////////////////////////
//! \brief      Definition of safe integer data types with min/max constants
//! \attention  Safety C code targets systems with C++ data models ILP32/LLP64.
//!             Be aware that small 8/16 bit integer types are subject to C/C++
//!             standard INTEGRAL PROMOTION RULES when used as operands for the
//!             built-in C/C++ operators, i.e., a WIDENING CONVERSION to signed
//!             32 bit integer type is applied in advance! As such, the yielded
//!             expression of a built-in operation with small integer operands
//!             is always of a signed 32 bit type, even though all operands are
//!             small integers. This applies also to small unsigned integers!
//! \attention  Be aware that INTEGRAL PROMOTION RULES are the same for alias
//!             type names. Though, this might be in conflict with conversion
//!             rules defined by IEC61131-3. For instance, a SINTxSINT operator
//!             yields a DINT/INT32 type with no SINT/INT8 overflow detection!
//! \attention  Be aware that BUILT-IN C/C++ OPERATORS MIGHT CAUSE OVERFLOWS to
//!             32 bit integer data types. For SIGNED 32 BIT INTEGER TYPES, THIS
//!             IS UNDEFINED BEHAVIOR! So, unlike for unsigned 32 bit integers,
//!             there is no wrap around/modulo behavior implemented! Propagated
//!             effects of UNDEFINED C/C++ BEHAVIOR WILL LIKELY CAUSE FAIL-SAFE
//!             REACTION at runtime. DO CHECK THE RIGHTHAND-SIDE OPERANDS OF ALL
//!             DIVISION/MODULO AND SHIFT OPERATORS for valid range. When in
//!             doubt, CONSIDER USING A SAFE HELPER FUNCTION as they do catch
//!             undefined behavior by performing checks on operands in advance.
////////////////////////////////////////////////////////////////////////////////
//! @name Primary aliases for supported functional primitive types
//! @{
typedef void            VOID;
typedef bool            BOOL;       //!< Range: false, true
typedef signed char     INT8;       //!< Range: -128..127
typedef unsigned char   UINT8;      //!< Range: 0..255
typedef signed short    INT16;      //!< Range: -32768..32767
typedef unsigned short  UINT16;     //!< Range: 0..65535
typedef signed int      INT32;      //!< Range: -2147483648..2147483647
typedef unsigned int    UINT32;     //!< Range: 0..4294967295
//! @}
//! @name Safe data aliases for supported functional primitive types
//! @{
typedef BOOL         safeBOOL;      //!< Range: false, true
typedef INT8         safeINT8;      //!< Range: -128..127
typedef UINT8        safeUINT8;     //!< Range: 0..255
typedef INT16        safeINT16;     //!< Range: -32768..32767
typedef UINT16       safeUINT16;    //!< Range: 0..65535
typedef INT32        safeINT32;     //!< Range: -2147483648..2147483647
typedef UINT32       safeUINT32;    //!< Range: 0..4294967295
//! @}
//! @name IEC 61131-3 aliases for supported functional primitive types
//! @{
typedef INT8         SINT;          //!< Range: -128..127
typedef UINT8        USINT;         //!< Range: 0..255
typedef INT16        INT;           //!< Range: -32768..32767
typedef UINT16       UINT;          //!< Range: 0..65535
typedef INT32        DINT;          //!< Range: -2147483648..2147483647
typedef UINT32       UDINT;         //!< Range: 0..4294967295
//! @}
//! @name IEC 61131-3 safe data aliases for supported functional primitive types
//! @{
typedef SINT         safeSINT;      //!< Range: -128..127
typedef USINT        safeUSINT;     //!< Range: 0..255
typedef INT          safeINT;       //!< Range: -32768..32767
typedef UINT         safeUINT;      //!< Range: 0..65535
typedef DINT         safeDINT;      //!< Range: -2147483648..2147483647
typedef UDINT        safeUDINT;     //!< Range: 0..4294967295
//! @}
//! @name Preprocessor defines for typed integer and boolean constants
//! @{
#define I8_ZERO      ((INT8)        0               )   //!< 0x0
#define I8_MIN       ((INT8)        -128            )   //!< 0x80
#define I8_MAX       ((INT8)        127             )   //!< 0x7F
#define I16_ZERO     ((INT16)       0               )   //!< 0x0
#define I16_MIN      ((INT16)       -32768          )   //!< 0x8000
#define I16_MAX      ((INT16)       32767           )   //!< 0x7FFF
#define I32_MIN      (              -2147483647-1   )   //!< 0x80000000
#define I32_MAX                     2147483647          //!< 0x7FFFFFFF
#define U8_ZERO      ((UINT8)       0U              )   //!< 0x0U
#define U8_MAX       ((UINT8)       255U            )   //!< 0xFFU
#define U16_ZERO     ((UINT16)      0U              )   //!< 0x0U
#define U16_MAX      ((UINT16)      65535U          )   //!< 0xFFFFU
#define U32_MAX                     4294967295U         //!< 0xFFFFFFFFU
#define FALSE        ((BOOL)        0               )   //!< false
#define TRUE         ((BOOL)        1               )   //!< true
//! @}
#pragma endregion Basic safe data type definitions and predefined constants

#pragma region SafeHelperCoreLibrary
////////////////////////////////////////////////////////////////////////////////
//! \class  CBeckhoffSafeHelperLib (CoreLib)
//! \brief  The safe helper library provides pure helper functions for Safety C
//!         that can be used permissively also in expressions with side effect
//!         restrictions, such as in control flow and assertion statements.
//!         Safe helper functions include fail-safe checks for invalid input
//!         data in order to prevent or at least safely reveal the effects
//!         of either unexpected behavior due to user misconceptions of the
//!         C/C++ language or due to the propagation of inconsistent data
//!         from undefined C/C++ behavior (which can be difficult to debug).
//!         If in doubt, PLEASE REFER TO THE SAFETY C CODING GUIDELINES to get
//!         an unambiguous functional specification of the helper functions!
////////////////////////////////////////////////////////////////////////////////
#ifndef HELPER_LIB_MODULE
namespace BeckhoffSafeHelperLib
{
    class CBeckhoffSafeHelperLib
    {
    public:
    //! @name Safe logic helpers with eager evaluation of operands
    //! @{
        BOOL AND(BOOL a, BOOL b);
        BOOL AND3(BOOL a, BOOL b, BOOL c);
        BOOL AND4(BOOL a, BOOL b, BOOL c, BOOL d);
        BOOL OR(BOOL a, BOOL b);
        BOOL OR3(BOOL a, BOOL b, BOOL c);
        BOOL OR4(BOOL a, BOOL b, BOOL c, BOOL d);
    //! @}
    //! @name Safe arithmetic helpers catching undefined C/C++ behavior
    //! @{
        INT32 ADDI32(INT32 a, INT32 b);
        INT32 SUBI32(INT32 a, INT32 b);
        INT32 MULI32(INT32 a, INT32 b);
        UINT16 MULU16(UINT16 a, UINT16 b);
        INT8 DIVI8(INT8 a, INT8 b);
        UINT8 DIVU8(UINT8 a, UINT8 b);
        INT16 DIVI16(INT16 a, INT16 b);
        UINT16 DIVU16(UINT16 a, UINT16 b);
        INT32 DIVI32(INT32 a, INT32 b);
        UINT32 DIVU32(UINT32 a, UINT32 b);
        INT8 MODI8(INT8 a, INT8 b);
        UINT8 MODU8(UINT8 a, UINT8 b);
        INT16 MODI16(INT16 a, INT16 b);
        UINT16 MODU16(UINT16 a, UINT16 b);
        INT32 MODI32(INT32 a, INT32 b);
        UINT32 MODU32(UINT32 a, UINT32 b);
        INT8 NEGI8(INT8 v);
        INT16 NEGI16(INT16 v);
        INT32 NEGI32(INT32 v);
        INT8 ABSI8(INT8 v);
        INT16 ABSI16(INT16 v);
        INT32 ABSI32(INT32 v);
    //! @}
    //! @name Safe logic bit shift helpers catching undefined C/C++ behavior
    //! @{
        UINT8 SHLU8(UINT8 a, UINT32 b);
        UINT16 SHLU16(UINT16 a, UINT32 b);
        UINT32 SHLU32(UINT32 a, UINT32 b);
        UINT8 SHRU8(UINT8 a, UINT32 b);
        UINT16 SHRU16(UINT16 a, UINT32 b);
        UINT32 SHRU32(UINT32 a, UINT32 b);
    //! @}
    //! @name Safe boolean to integer conversion helpers
    //! @{
        INT8 BTOI8(BOOL v);
        INT16 BTOI16(BOOL v);
        INT32 BTOI32(BOOL v);
        UINT8 BTOU8(BOOL v);
        UINT16 BTOU16(BOOL v);
        UINT32 BTOU32(BOOL v);
    //! @}
    //! @name Safe integer to integer conversion helpers catching data/sign loss
    //! @{
        UINT8 I8TOU8(INT8 v);
        UINT16 I8TOU16(INT8 v);
        UINT32 I8TOU32(INT8 v);
        INT8 U8TOI8(UINT8 v);
        INT8 I16TOI8(INT16 v);
        UINT8 I16TOU8(INT16 v);
        UINT16 I16TOU16(INT16 v);
        UINT32 I16TOU32(INT16 v);
        INT8 U16TOI8(UINT16 v);
        UINT8 U16TOU8(UINT16 v);
        INT16 U16TOI16(UINT16 v);
        INT8 I32TOI8(INT32 v);
        UINT8 I32TOU8(INT32 v);
        INT16 I32TOI16(INT32 v);
        UINT16 I32TOU16(INT32 v);
        UINT32 I32TOU32(INT32 v);
        INT8 U32TOI8(UINT32 v);
        UINT8 U32TOU8(UINT32 v);
        INT16 U32TOI16(UINT32 v);
        UINT16 U32TOU16(UINT32 v);
        INT32 U32TOI32(UINT32 v);
    //! @}
    //! @name Safe helper library version info query functions
    //! @{
        UINT32 GetSafetyPlcMajorVersionU32();
        UINT32 GetSafetyPlcMinorVersionU32();
        UINT32 GetSafeHelperCoreLibVersionU32();
        UINT32 GetTwinCatReleaseVersionU32();
    //! @}
    };
}
static BeckhoffSafeHelperLib::CBeckhoffSafeHelperLib CoreLib;
//! @name Static wrapper functions for access to CoreLib like a built-in C API
//! @{
namespace CORE {

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe boolean AND avoiding short circuit evaluation of operands
//! \param a First boolean operand
//! \param b Second boolean operand
//! \return a && b
////////////////////////////////////////////////////////////////////////////////
inline static BOOL AND(BOOL a, BOOL b)
{
    return CoreLib.AND(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe boolean AND3 avoiding short circuit evaluation of operands
//! \param a First boolean operand
//! \param b Second boolean operand
//! \param c Third boolean operand
//! \return a && b && c
////////////////////////////////////////////////////////////////////////////////
inline static BOOL AND3(BOOL a, BOOL b, BOOL c)
{
    return CoreLib.AND3(a, b, c);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe boolean AND4 avoiding short circuit evaluation of operands
//! \param a First boolean operand
//! \param b Second boolean operand
//! \param c Third boolean operand
//! \param d Fourth boolean operand
//! \return a && b && c && d
////////////////////////////////////////////////////////////////////////////////
inline static BOOL AND4(BOOL a, BOOL b, BOOL c, BOOL d)
{
    return CoreLib.AND4(a, b, c, d);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe boolean OR avoiding short circuit evaluation of operands
//! \param a First boolean operand
//! \param b Second boolean operand
//! \return a || b
////////////////////////////////////////////////////////////////////////////////
inline static BOOL OR(BOOL a, BOOL b)
{
    return CoreLib.OR(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe boolean OR3 avoiding short circuit evaluation of operands
//! \param a First boolean operand
//! \param b Second boolean operand
//! \param c Third boolean operand
//! \return a || b || c
////////////////////////////////////////////////////////////////////////////////
inline static BOOL OR3(BOOL a, BOOL b, BOOL c)
{
    return CoreLib.OR3(a, b, c);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe boolean OR4 avoiding short circuit evaluation of operands
//! \param a First boolean operand
//! \param b Second boolean operand
//! \param c Third boolean operand
//! \param d Fourth boolean operand
//! \return a || b || c || d
////////////////////////////////////////////////////////////////////////////////
inline static BOOL OR4(BOOL a, BOOL b, BOOL c, BOOL d)
{
    return CoreLib.OR4(a, b, c, d);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely adds values a and b catching INT32 overflow
//! \param a Left-hand side integer operand
//! \param b Right-hand side integer operand
//! \return a + b
////////////////////////////////////////////////////////////////////////////////
inline static INT32 ADDI32(INT32 a, INT32 b)
{
    return CoreLib.ADDI32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely subtracts values b from a catching INT32 overflow
//! \param a Left-hand side integer operand
//! \param b Right-hand side integer operand
//! \return a - b
////////////////////////////////////////////////////////////////////////////////
inline static INT32 SUBI32(INT32 a, INT32 b)
{
    return CoreLib.SUBI32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely multiplies a and b catching INT32 overflow
//! \param a Left-hand side integer operand
//! \param b Right-hand side integer operand
//! \return a * b
////////////////////////////////////////////////////////////////////////////////
inline static INT32 MULI32(INT32 a, INT32 b)
{
    return CoreLib.MULI32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely multiplies UINT16 a and b catching UINT16 overflow
//! \param a Left-hand side integer operand
//! \param b Right-hand side integer operand
//! \return (UINT16)(a * b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 MULU16(UINT16 a, UINT16 b)
{
    return CoreLib.MULU16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely divides INT32 a by b catching overflow and division-by-zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return a / b
////////////////////////////////////////////////////////////////////////////////
inline static INT32 DIVI32(INT32 a, INT32 b)
{
    return CoreLib.DIVI32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely divides UINT32 a by b catching division-by-zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return a / b
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 DIVU32(UINT32 a, UINT32 b)
{
    return CoreLib.DIVU32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely computes remainder of INT32 a divided by b catching division
//!        by zero and negative operands
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return a % b
////////////////////////////////////////////////////////////////////////////////
inline static INT32 MODI32(INT32 a, INT32 b)
{
    return CoreLib.MODI32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely computes remainder of UINT32 a divided by b catching division
//!        by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return a % b
///////////////////////////////////////////////////////////////////////////////
inline static UINT32 MODU32(UINT32 a, UINT32 b)
{
    return CoreLib.MODU32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely divides INT16 a by b catching overflow and division by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (INT32)(a / b)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 DIVI16(INT16 a, INT16 b)
{
    return CoreLib.DIVI16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely divides UINT16 a by b catching division by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (UINT16)(a / b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 DIVU16(UINT16 a, UINT16 b)
{
    return CoreLib.DIVU16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely computes remainder of INT16 a divided by b catching division
//!        by zero and negative operands
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (INT16)(a % b)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 MODI16(INT16 a, INT16 b)
{
    return CoreLib.MODI16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely computes remainder of UINT16 a divided by b catching division
//!        by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (UINT16)(a % b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 MODU16(UINT16 a, UINT16 b)
{
    return CoreLib.MODU16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely divides INT8 a and b catching overflow and division by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (INT8)(a / b)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 DIVI8(INT8 a, INT8 b)
{
    return CoreLib.DIVI8(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely divides UINT8 a and b catching division by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (UINT8)(a / b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 DIVU8(UINT8 a, UINT8 b)
{
    return CoreLib.DIVU8(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely computes remainder of INT8 a divided by b catching division
//!        by zero and negative operands
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (INT8)(a % b)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 MODI8(INT8 a, INT8 b)
{
    return CoreLib.MODI8(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely computes remainder of UINT8 a divided by b catching division
//!        by zero
//! \param a Left-hand side integer operand (dividend)
//! \param b Right-hand side integer operand (divisor)
//! \return (UINT8)(a % b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 MODU8(UINT8 a, UINT8 b)
{
    return CoreLib.MODU8(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely negates INT8 value catching overflow at I8_MIN input
//! \param a Integer operand to be negated
//! \return (INT8)(-a)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 NEGI8(INT8 a)
{
    return CoreLib.NEGI8(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely negates INT16 value catching overflow at I16_MIN input
//! \param a Integer operand to be negated
//! \return (UNT16)(-a)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 NEGI16(INT16 a)
{
    return CoreLib.NEGI16(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely negates INT32 value catching overflow at I32_MIN input
//! \param a Integer operand to be negated
//! \return -a
////////////////////////////////////////////////////////////////////////////////
inline static INT32 NEGI32(INT32 a)
{
    return CoreLib.NEGI32(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe absolute value function for INT8 catching overflow
//! \param a Integer operand used for absolute value computation
//! \return (INT8)((a < 0) ? -a : a)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 ABSI8(INT8 v)
{
    return CoreLib.ABSI8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe absolute value function for INT16 catching overflow
//! \param a Integer operand used for absolute value computation
//! \return (INT16)(a < 0) ? -a : a
////////////////////////////////////////////////////////////////////////////////
inline static INT16 ABSI16(INT16 v)
{
    return CoreLib.ABSI16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe absolute value function for INT32 catching overflow
//! \param a Integer operand used for absolute value computation
//! \return (a < 0) ? -a : a
////////////////////////////////////////////////////////////////////////////////
inline static INT32 ABSI32(INT32 v)
{
    return CoreLib.ABSI32(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe shift left for UINT8 bitstring catching overlong shift length
//! \param a Bitstring operand to be shifted
//! \param b Integer shift length operand
//! \return (UINT8)(a << b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 SHLU8(UINT8 a, UINT32 b)
{
    return CoreLib.SHLU8(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe shift left for UINT16 bitstring catching overlong shift length
//! \param a Bitstring operand to be shifted
//! \param b Integer shift length operand
//! \return (UINT16)(a << b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 SHLU16(UINT16 a, UINT32 b)
{
    return CoreLib.SHLU16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe shift left for UINT32 bitstring catching overlong shift length
//! \param a Bitstring operand to be shifted
//! \param b Integer shift length operand
//! \return a << b
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 SHLU32(UINT32 a, UINT32 b)
{
    return CoreLib.SHLU32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe shift right for UINT8 bitstring catching overlong shift length
//! \param a Bitstring operand to be shifted
//! \param b Integer shift length operand
//! \return (UINT8)(a >> b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 SHRU8(UINT8 a, UINT32 b)
{
    return CoreLib.SHRU8(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe shift right for UINT16 bitstring catching overlong shift length
//! \param a Bitstring operand to be shifted
//! \param b Integer shift length operand
//! \return (UINT16)(a >> b)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 SHRU16(UINT16 a, UINT32 b)
{
    return CoreLib.SHRU16(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safe shift right for UINT32 bitstring catching overlong shift length
//! \param a Bitstring operand to be shifted
//! \param b Integer shift length operand
//! \return a >> b
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 SHRU32(UINT32 a, UINT32 b)
{
    return CoreLib.SHRU32(a, b);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts BOOL value to INT8 value mapping true to 1 and false to 0
//! \param a Boolean operand to be casted to integer
//! \return (INT8)(a ? 1 : 0)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 BTOI8(BOOL a)
{
    return CoreLib.BTOI8(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts BOOL value to INT16 value mapping true to 1 and false to 0
//! \param a Boolean operand to be casted to integer
//! \return (INT16)(a ? 1 : 0)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 BTOI16(BOOL a)
{
    return CoreLib.BTOI16(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts BOOL value to INT32 value mapping true to 1 and false to 0
//! \param a Boolean operand to be casted to integer
//! \return a ? 1 : 0
////////////////////////////////////////////////////////////////////////////////
inline static INT32 BTOI32(BOOL a)
{
    return CoreLib.BTOI32(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts BOOL value to UINT8 value mapping true to 1 and false to 0
//! \param a Boolean source operand to be casted to integer target type
//! \return (UINT8)(a ? 1 : 0)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 BTOU8(BOOL a)
{
    return CoreLib.BTOU8(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts BOOL value to UINT16 value mapping true to 1 and false to 0
//! \param a Boolean source operand to be casted to integer target type
//! \return (UINT16)(a ? 1 : 0)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 BTOU16(BOOL a)
{
    return CoreLib.BTOU16(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts BOOL value to UINT32 value mapping true to 1 and false to 0
//! \param a Boolean source operand to be casted to integer target type
//! \return (UINT32)(a ? 1 : 0)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 BTOU32(BOOL a)
{
    return CoreLib.BTOU32(a);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT8 value to UINT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 I8TOU8(INT8 v)
{
    return CoreLib.I8TOU8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT8 value to UINT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 I8TOU16(INT8 v)
{
    return CoreLib.I8TOU16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT8 value to UINT32 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT32)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 I8TOU32(INT8 v)
{
    return CoreLib.I8TOU32(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT8 value to INT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 U8TOI8(UINT8 v)
{
    return CoreLib.U8TOI8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT16 value to UINT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 I16TOU8(INT16 v)
{
    return CoreLib.I16TOU8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT16 value to INT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 I16TOI8(INT16 v)
{
    return CoreLib.I16TOI8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT16 value to UINT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 I16TOU16(INT16 v)
{
    return CoreLib.I16TOU16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT16 value to UINT32 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT32)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 I16TOU32(INT16 v)
{
    return CoreLib.I16TOU32(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT16 value to INT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 U16TOI8(UINT16 v)
{
    return CoreLib.U16TOI8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT16 value to UINT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 U16TOU8(UINT16 v)
{
    return CoreLib.U16TOU8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT16 value to INT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 U16TOI16(UINT16 v)
{
    return CoreLib.U16TOI16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT32 value to INT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 I32TOI8(INT32 v)
{
    return CoreLib.I32TOI8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT32 value to UINT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 I32TOU8(INT32 v)
{
    return CoreLib.I32TOU8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT32 value to INT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 I32TOI16(INT32 v)
{
    return CoreLib.I32TOI16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT32 value to UINT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 I32TOU16(INT32 v)
{
    return CoreLib.I32TOU16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts INT32 value to UINT32 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT32)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 I32TOU32(INT32 v)
{
    return CoreLib.I32TOU32(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT32 value to INT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT8 U32TOI8(UINT32 v)
{
    return CoreLib.U32TOI8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT32 value to UINT8 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT8)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT8 U32TOU8(UINT32 v)
{
    return CoreLib.U32TOU8(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT32 value to INT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT16 U32TOI16(UINT32 v)
{
    return CoreLib.U32TOI16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT32 value to UINT16 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (UINT16)(v)
////////////////////////////////////////////////////////////////////////////////
inline static UINT16 U32TOU16(UINT32 v)
{
    return CoreLib.U32TOU16(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Safely converts UINT32 value to INT32 value catching data/sign loss
//! \param v Integer source operand to be casted to integer target type
//! \return (INT32)(v)
////////////////////////////////////////////////////////////////////////////////
inline static INT32 U32TOI32(UINT32 v)
{
    return CoreLib.U32TOI32(v);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Return the according TwinCAT Safety PLC major version number
//! \return Integer version number (e.g., 1 for 1.x)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetSafetyPlcMajorVersionU32()
{
    return CoreLib.GetSafetyPlcMajorVersionU32();
}
////////////////////////////////////////////////////////////////////////////////
//! \brief Return the according TwinCAT Safety PLC minor version number
//! \return Integer version number (e.g., 1 for x.1)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetSafetyPlcMinorVersionU32()
{
    return CoreLib.GetSafetyPlcMinorVersionU32();
}
////////////////////////////////////////////////////////////////////////////////
//! \brief Return release version number of the SafeHelperCoreLib
//! \return Integer version number
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetSafeHelperCoreLibVersionU32()
{
    return CoreLib.GetSafeHelperCoreLibVersionU32();
}
////////////////////////////////////////////////////////////////////////////////
//! \brief Return the current TwinCAT version for which this library is built
//! \return Integer version numbers coded into a UINT32 bitstring as follows:
//!         AAAAAAAABBBBBBBBCCCCCCCCCCCCCCCC
//!         A: TwinCAT major version number (e.g. 3)
//!         B: TwinCAT minor version number (e.g. 1)
//!         C: TwinCAT release number       (e.g. 4024)
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetTwinCatReleaseVersionU32()
{
    return CoreLib.GetTwinCatReleaseVersionU32();
}

} // Close namespace CORE
using namespace CORE; // Set as default namespace
//! @}
#endif // HELPER_LIB_MODULE
#pragma endregion Programming interface of the safe helper core library

#pragma region SafeFloatingPointLibrary
////////////////////////////////////////////////////////////////////////////////
//! \class      CFloat
//! \brief      Substitute softfloat type for the native C/C++ type 'float'
//! \details    This type provides an interface for the software-implemented
//!             IEEE754 single precision floating point arithmetic. Operators
//!             +, -, *, / are overloaded, e.g., a+b invokes ADDF32(a,b).
//!             Compare operators ==, != and relational operators <, >, <=, >=
//!             are also overloaded, e.g., a==b invokes CEQF32(a,b). Note that
//!             conversions from/to integers are NOT overloaded by arithmetic
//!             conversion as defined by IEEE754 convertToIntegerTowardZero/
//!             convertFromInt or as defined by the C/C++ standard conversions.
//!             Instead, use strongly typed helper
//!             functions, such as HEXTOF32, U32TOF32, F32TOU32 etc.
//!             Literals must be explicitly typed by suffix f or F. Otherwise,
//!             they are interpreted as integer or double precision literals.
//! \attention  Uninitialized floating point variables have undefined values,
//!             i.e., they are not 0.0f or signaling/quiet NaN by default!
////////////////////////////////////////////////////////////////////////////////
#pragma warning ( disable: 4710 )
union CFloat
{
    //! C/C++ native single precision 32 bit type 'float'
    float f32;

    //! Unsigned 32 bit data word representation
    UINT32 u32;

    //! Encoding of mantissa, exponent and sign according to IEEE754
    struct
    {
        //! Bitstring representation [31-0]: SEEEEEEEEMMMMMMMMMMMMMMMMMMMMMMM
        UINT32 m : 23; //!< Mantissa bits 0-22
        UINT32 e : 8;  //!< Exponent bits 23-30
        UINT32 s : 1;  //!< Sign bit 31
    } ieee754;

    //! Default constructor without initialization
    CFloat() { }

    //! Constructor for initialization with float literals
    CFloat(const float &_f32)
    {
        f32 = _f32;
    }

    //! Constructor for initialization with unsigned int bitstring
    explicit CFloat(const UINT32 &_u32)
    {
        u32 = _u32;
    }

    //! Overload combined arithmetic operators with assignment
    inline CFloat& operator += (const CFloat &rhs);
    inline CFloat& operator -= (const CFloat &rhs);
    inline CFloat& operator *= (const CFloat &rhs);
    inline CFloat& operator /= (const CFloat &rhs);

#ifndef SAFE_BUILD
    //! Overload streaming operator for non-safe DEBUG_TRACE output
    class ostream;
    friend ostream& operator << (ostream &os, const CFloat &rhs);
#endif
};

//! @name Alias names for the functional single precision floating point data type
//! @{
typedef union CFloat FLOAT32;       //!< Range: -3.4028235E+38f..3.4028235E+38f
typedef FLOAT32      safeFLOAT32;   //!< Range: -3.4028235E-38f..3.4028235E+38f
typedef FLOAT32      REAL;          //!< Range: -3.4028235E-38f..3.4028235E+38f
typedef REAL         safeREAL;      //!< Range: -3.4028235E-38f..3.4028235E+38f
//! @}
//! @name Predefined single precision floating point constants
//! @{
#define F32_NAN      ((FLOAT32)0x7FC00000U)  //!< Not a Number (NaN)
#define F32_ZERO     ((FLOAT32)0x00000000U)  //!< 0.0f
#define F32_ONE      ((FLOAT32)0x3F800000U)  //!< 1.0f
#define F32_MIN      ((FLOAT32)0x00800000U)  //!< 1.17549435E-38f
#define F32_MAX      ((FLOAT32)0x7F7FFFFFU)  //!< 3.4028235E+38f
#define F32_INF      ((FLOAT32)0x7F800000U)  //!< Infinity (x > F32_MAX)
#define F32_PI       ((FLOAT32)0x40490FDBU)  //!< 3.1415927410125732421875f
#define F32_PI_2     ((FLOAT32)0x3FC90FDBU)  //!< 1.57079637050628662109375f
#define F32_PI_4     ((FLOAT32)0x3F490FDBU)  //!< 0.785398185253143310546875f
#define F32_1_PI     ((FLOAT32)0x3EA2F983U)  //!< 0.3183098733425140380859375f
#define F32_2_PI     ((FLOAT32)0x3F22F983U)  //!< 0.636619746685028076171875f
#define F32_4_PI     ((FLOAT32)0x3FA2F983U)  //!< 1.27323949337005615234375f
#define F32_E        ((FLOAT32)0x402DF854U)  //!< 2.71828174591064453125f
#define F32_LOG2E    ((FLOAT32)0x3FB8AA3BU)  //!< 1.44269502162933349609375f
#define F32_LOG10E   ((FLOAT32)0x3EDE5BD9U)  //!< 0.4342944920063018798828125f
#define F32_SQRT2    ((FLOAT32)0x3F3504F3U)  //!< 0.707106769084930419921875f
#define F32_2_SQRTPI ((FLOAT32)0x3F906EBBU)  //!< 1.12837922573089599609375f
#define F32_SQRT1_2  ((FLOAT32)0x3FB504F3U)  //!< 1.41421353816986083984375f
#define F32_LN2      ((FLOAT32)0x3F317218U)  //!< 0.693147182464599609375f
#define F32_LN10     ((FLOAT32)0x40135D8EU)  //!< 2.302585124969482421875f
//! @}
//! @name Predefined floating point type ID defines returned by CLASSF32(x)
//! @{
#define I32_FP_NOT_A_NUMBER         (1) //!< Identifier for a (quiet) NaN
#define I32_FP_NEGATIVE_INFINITE    (2) //!< Identifier for +Infinity
#define I32_FP_NEGATIVE_NORMAL      (3) //!< Identifier for a positive normal
#define I32_FP_NEGATIVE_SUBNORMAL   (4) //!< Identifier for a positive subnormal
#define I32_FP_NEGATIVE_ZERO        (5) //!< Identifier for +0.0f
#define I32_FP_POSITIVE_ZERO        (6) //!< Identifier for -0.0f
#define I32_FP_POSITIVE_SUBNORMAL   (7) //!< Identifier for a negative normal
#define I32_FP_POSITIVE_NORMAL      (8) //!< Identifier for a negative subnormal
#define I32_FP_POSITIVE_INFINITE    (9) //!< Identifier for -Infinity
//! @}

////////////////////////////////////////////////////////////////////////////////
//! \class     BeckhoffSafeFloatLib (FloatLib)
//! \brief     The safe float library provides Safety C pure helper functions
//!            that can be used permissively also in expressions with side
//!            effect restrictions, such as in control flow and assertion
//!            statements. These helper functions provide single precision
//!            binary floating point arithmetic based on a subset of the
//!            IEEE754-2008 standard with some safety-related modifications:
//!            - Default rounding attribute 'to nearest even' is supported for
//!              internal target format rounding only (not to be confused with
//!              integral rounding for which all rounding modes are supported)
//!            - Default exception handling with status flags is not supported
//!            - Additional fail-safe checks are included for potentially
//!              dangerous operations masking NaN inputs and IEEE754 exceptions
//!              that cannot be detected due to unavailable exception flags
//!            If in doubt, PLEASE REFER TO THE SAFETY C CODING GUIDELINES for
//!            an unambiguous specification w.r.t. the IEEE754-2008 standard.
//! \attention Be aware that most arithmetic and conversion operations are
//!            subject to target format rounding, i.e., results are accurate
//!            within 0.5 units in the last place (ULP) only. This is the
//!            inherent inaccuracy of limited precision floating point formats.
//!            Accumulated errors should be bounded by static error analysis.
//!            However, there are no means of detecting target format rounding
//!            at runtime as there is no INEXACT flag raised and naturally no
//!            fail-safe reaction, as rounding is considered the default case.
//! \attention Be aware that floating point literals can be already subject to
//!            rounding if not representable by target format either due to
//!            radix conversion or truncation/rounding of too many significant
//!            digits. Consider converting unambiguous bitstring representations
//!            using HEXTOF32 helper function, as the rounding error of literals
//!            can accumulate significantly, e.g., bias of literal coefficients.
//! \attention Using floating point arithmetic requires deep numerical analysis
//!            skills and, therefore, must be applied with special care in the
//!            context of safety application development. A good practice is to
//!            not relying on accuracy of the least bits. Safety C developers
//!            should read the paper 'What Every Computer Scientist Should Know
//!            About Floating-Point Arithmetic' by D. Goldberg (ACM Computing
//!            Surveys, Vol. 23, No. 1, March 1991) summarizing common pitfalls
//!            and best practices w.r.t. floating point arithmetic.
////////////////////////////////////////////////////////////////////////////////
#ifndef FLOAT_LIB_MODULE
namespace BeckhoffSafeFloatLib
{
    class CBeckhoffSafeFloatLib
    {
    public:
    //! @name Single precision floating point conversion functions
    //! @{
        UINT32 HEXTOF32(UINT32 x);          //!< no IEEE754 pendant
        UINT32 F32TOHEX(UINT32 x);          //!< no IEEE754 pendant
        UINT32 I32TOF32(INT32 x);           //!< convertFromInt
        UINT32 U32TOF32(UINT32 x);          //!< convertFromInt
        INT32 F32TOI32(UINT32 x);           //!< convertToIntegralTowardZero
        UINT32 F32TOU32(UINT32 x);          //!< convertToIntegralTowardZero
    //! @}
    //! @name Single precision floating point rounding functions
    //! @{
        UINT32 ROUNDF32(UINT32 x);          //!< roundToIntegralTiesToAway
        UINT32 FLOORF32(UINT32 x);          //!< roundToIntegralTowardNegative
        UINT32 CEILF32(UINT32 x);           //!< roundToIntegralTowardPositive
        UINT32 TRUNCF32(UINT32 x);          //!< roundToIntegralTowardZero
        UINT32 NEARBYINTF32(UINT32 x);      //!< roundToIntegralTiesToEven
    //! @}
    //! @name Single precision floating point test functions
    //! @{
        BOOL ISNANF32(UINT32 x);                  //!< isNaN
        BOOL ISINFINITEF32(UINT32 x);             //!< isInfinite
        BOOL ISFINITEF32(UINT32 x);               //!< isFinite
        BOOL ISNORMALF32(UINT32 x);               //!< isNormal
        BOOL ISSUBNORMALF32(UINT32 x);            //!< isSubnormal
        BOOL ISSIGNMINUSF32(UINT32 x);            //!< isSignMinus
        BOOL ISZEROF32(UINT32 x);                 //!< isZero
        INT32 CLASSF32(UINT32 x);                 //!< class
    //! @}
    //! @name Single precision floating point compare/relational functions
    //! @{
        BOOL CEQF32(UINT32 xa, UINT32 xb);        //!< compareQuietEqual
        BOOL CNEF32(UINT32 xa, UINT32 xb);        //!< compareQuietNotEqual
        BOOL CGTF32(UINT32 xa, UINT32 xb);        //!< compareQuietGreater
        BOOL CLTF32(UINT32 xa, UINT32 xb);        //!< compareQuietLess
        BOOL CGEF32(UINT32 xa, UINT32 xb);        //!< compareQuietGreaterEqual
        BOOL CLEF32(UINT32 xa, UINT32 xb);        //!< compareQuietLessEqual
    //! @}
    //! @name Single precision floating point arithmetic functions
    //! @{
        UINT32 ADDF32(UINT32 xa, UINT32 xb);            //!< addition
        UINT32 SUBF32(UINT32 xa, UINT32 xb);            //!< subtraction
        UINT32 MULF32(UINT32 xa, UINT32 xb);            //!< multiplication
        UINT32 DIVF32(UINT32 xa, UINT32 xb);            //!< division
        UINT32 REMF32(UINT32 xa, UINT32 xb);            //!< remainder
        UINT32 FMAF32(UINT32 xa, UINT32 xb, UINT32 xc); //!< fuseMultiplyAdd
        UINT32 SQRTF32(UINT32 x);                       //!< squareRoot
    //! @}
    //! @name Single precision floating point order functions
    //! @{
        UINT32 MAXF32(UINT32 xa, UINT32 xb);      //!< maxNum
        UINT32 MINF32(UINT32 xa, UINT32 xb);      //!< minNum
        UINT32 MAXABSF32(UINT32 xa, UINT32 xb);   //!< maxNumMag
        UINT32 MINABSF32(UINT32 xa, UINT32 xb);   //!< minNumMag
        UINT32 NEXTUPF32(UINT32 x);               //!< nextUp
        UINT32 NEXTDOWNF32(UINT32 x);             //!< nextDown
    //! @}
    //! @name Single precision floating point sign bit functions
    //! @{
        UINT32 NEGF32(UINT32 x);                  //!< negate
        UINT32 ABSF32(UINT32 x);                  //!< abs
        UINT32 COPYSIGNF32(UINT32 xa, UINT32 xb); //!< copySign
    //! @}
    //! @name Single precision floating point binary format functions
    //! @{
        UINT32 SCALBNF32(UINT32 x, INT32 e);      //!< scaleB
        INT32 ILOGBF32(UINT32 x);                 //!< logB
    //! @name Single precision helper library version info functions
    //! @{
        UINT32 GetSafeHelperFloatLibConfU32();
        UINT32 GetSafeHelperFloatLibVersionU32();
    //! @}
    };
}
static BeckhoffSafeFloatLib::CBeckhoffSafeFloatLib FloatLib;
//! @name Static wrapper functions for accessing FloatLib like a built-in C API
//! @{
namespace IEEE754 {
////////////////////////////////////////////////////////////////////////////////
//! \brief Converts an IEEE754 single precision format encoded UINT32 bitstring
//!        to FLOAT32 number
//! \param x UINT32 bitstring to be converted
//! \return FLOAT32 conversion result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 HEXTOF32(UINT32 x)
{
    return FLOAT32(FloatLib.HEXTOF32(x));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts FLOAT32 number to a UINT32 bitstring with IEEE754 single
//!        precision format encoding
//! \param x FLOAT32 value to be converted
//! \return UINT32 conversion result
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 F32TOHEX(FLOAT32 x)
{
    return FloatLib.F32TOHEX(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts an INT32 integer number to a FLOAT32 floating point number
//! \param x INT32 value to be converted
//! \return FLOAT32 conversion result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 I32TOF32(INT32 x)
{
    return FLOAT32(FloatLib.I32TOF32(x));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts a UINT32 integer number to a FLOAT32 floating point number
//! \param x UINT32 value to be converted
//! \return FLOAT32 conversion result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 U32TOF32(UINT32 x)
{
    return FLOAT32(FloatLib.U32TOF32(x));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts a FLOAT32 floating point number to an INT32 integer number
//! \param x FLOAT32 value to be converted
//! \return INT32 conversion result
////////////////////////////////////////////////////////////////////////////////
inline static INT32 F32TOI32(FLOAT32 x)
{
    return FloatLib.F32TOI32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Converts a FLOAT32 floating point number to a UINT32 integer number
//! \param x FLOAT32 value to be converted
//! \return UINT32 conversion result
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 F32TOU32(FLOAT32 x)
{
    return FloatLib.F32TOU32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Rounds a FLOAT32 floating point number to next integral floating
//!        point number using rounding mode ties to away
//! \param x FLOAT32 value to be rounded
//! \return FLOAT32 rounded result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 ROUNDF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.ROUNDF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Rounds a FLOAT32 floating point number to next integral floating
//!        point number using rounding mode toward negative
//! \param x FLOAT32 value to be rounded
//! \return FLOAT32 rounded result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 FLOORF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.FLOORF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Rounds a FLOAT32 floating point number to next integral floating
//!        point number using rounding mode toward positive
//! \param x FLOAT32 value to be rounded
//! \return FLOAT32 rounded result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 CEILF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.CEILF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Rounds a FLOAT32 floating point number to next integral floating
//!        point number using rounding mode toward zero
//! \param x FLOAT32 value to be rounded
//! \return FLOAT32 rounded result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 TRUNCF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.TRUNCF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Rounds a FLOAT32 floating point number to next integral floating
//!        point number using rounding mode ties to even
//! \param x FLOAT32 value to be rounded
//! \return FLOAT32 rounded result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 NEARBYINTF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.NEARBYINTF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if a FLOAT32 floating point value is Not a Number (NaN)
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISNANF32(FLOAT32 x)
{
    return FloatLib.ISNANF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if a FLOAT32 floating point value is +Infinity or -Infinity
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISINFINITEF32(FLOAT32 x)
{
    return FloatLib.ISINFINITEF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if a FLOAT32 floating point value is neither +/-Infinity nor
//!        a NaN
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISFINITEF32(FLOAT32 x)
{
    return FloatLib.ISFINITEF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if a FLOAT32 floating point value is a normalized number
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISNORMALF32(FLOAT32 x)
{
    return FloatLib.ISNORMALF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if a FLOAT32 floating point value is a subnormal
//!        (denormalized) number close to zero
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISSUBNORMALF32(FLOAT32 x)
{
    return FloatLib.ISSUBNORMALF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if the sign bit of a FLOAT32 floating point value is set
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISSIGNMINUSF32(FLOAT32 x)
{
    return FloatLib.ISSIGNMINUSF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if the provided FLOAT32 floating point number is +/-0
//! \param x FLOAT32 value to be tested
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL ISZEROF32(FLOAT32 x)
{
    return FloatLib.ISZEROF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Classifies the provided FLOAT32 floating point value
//! \param x FLOAT32 value to be tested
//! \return INT32 result storing the FP class identifier
////////////////////////////////////////////////////////////////////////////////
inline static INT32 CLASSF32(FLOAT32 x)
{
    return FloatLib.CLASSF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if two floating point numbers are equal
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL CEQF32(FLOAT32 xa, FLOAT32 xb)
{
    return FloatLib.CEQF32(xa.u32, xb.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if two floating point numbers are not equal
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL CNEF32(FLOAT32 xa, FLOAT32 xb)
{
    return FloatLib.CNEF32(xa.u32, xb.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if left floating point number is greater than the right
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL CGTF32(FLOAT32 xa, FLOAT32 xb)
{
    return FloatLib.CGTF32(xa.u32, xb.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if left floating point number is less than the right
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL CLTF32(FLOAT32 xa, FLOAT32 xb)
{
    return FloatLib.CLTF32(xa.u32, xb.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if left floating point number is greater than/equals the right
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL CGEF32(FLOAT32 xa, FLOAT32 xb)
{
    return FloatLib.CGEF32(xa.u32, xb.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Tests if left floating point number is less than/equals the right
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return BOOL test result
////////////////////////////////////////////////////////////////////////////////
inline static BOOL CLEF32(FLOAT32 xa, FLOAT32 xb)
{
    return FloatLib.CLEF32(xa.u32, xb.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs addition of two single precision floating point numbers
//! \param xa Left-hand side single precision floating point summand
//! \param xb Right-hand side single precision floating point summand
//! \return FLOAT32 addition result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 ADDF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.ADDF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs subtraction of two single precision floating point numbers
//! \param xa Left-hand side single precision floating point minuend
//! \param xb Right-hand side single precision floating point subtrahend
//! \return FLOAT32 subtraction result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 SUBF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.SUBF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs multiplication of two single precision floating point
//!        numbers
//! \param xa Left-hand side single precision floating point multiplicand
//! \param xb Right-hand side single precision floating point multiplicand
//! \return FLOAT32 multiplication result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 MULF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.MULF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs division of two single precision floating point numbers
//! \param xa Left-hand side single precision floating point dividend
//! \param xb Right-hand side single precision floating point divisor
//! \return FLOAT32 division result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 DIVF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.DIVF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs floating point remainder computation of two single
//!        precision format numbers (not to be confused with standard C's fmod)
//! \param xa Left-hand side single precision floating point dividend
//! \param xb Right-hand side single precision floating point divisor
//! \return FLOAT32 remainder result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 REMF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.REMF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs fused multiplication with addition of thee single precision
//!        floating point numbers
//! \param xa Left-hand side single precision floating point multiplicand
//! \param xb Right-hand side single precision floating point multiplicand
//! \param xc Right-hand side single precision floating point summand
//! \return FLOAT32 result of multiplication with subsequent addition
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 FMAF32(FLOAT32 xa, FLOAT32 xb, FLOAT32 xc)
{
    return FLOAT32(FloatLib.FMAF32(xa.u32, xb.u32, xc.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Performs the square root computation of a single precision floating
//!        point number x
//! \param x Single precision floating point input
//! \return sqrt(x)
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 SQRTF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.SQRTF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Selects the maximum of two single precision floating point numbers
//!        considering signed values
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return FLOAT32 maximum number
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 MAXF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.MAXF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Selects the minimum of two single precision floating point numbers
//!        considering signed values
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return FLOAT32 minimum number
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 MINF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.MINF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Selects the maximum of two single precision floating point numbers
//!        considering absolute values
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return FLOAT32 maximum absolute number
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 MAXABSF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.MAXABSF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Selects the minimum of two single precision floating point numbers
//!        considering absolute values
//! \param xa Left-hand side single precision floating point operand
//! \param xb Right-hand side single precision floating point operand
//! \return FLOAT32 minimum absolute number
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 MINABSF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.MINABSF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Returns the next floating point number that compares greater w.r.t.
//!        the given floating point number
//! \param x Single precision floating point input value
//! \return FLOAT32 next higher number
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 NEXTUPF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.NEXTUPF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Returns the next floating point number that compares less w.r.t.
//!        the given floating point number
//! \param x Single precision floating point input value
//! \return FLOAT32 next lower number
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 NEXTDOWNF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.NEXTDOWNF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Negates a floating point number by inverting the sign bit
//! \param x Single precision floating point operand to be negated
//! \return FLOAT32 negation result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 NEGF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.NEGF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Computes the absolute value of a floating point number by clearing
//!        the sign bit
//! \param x Single precision floating point operand
//! \return FLOAT32 absolute result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 ABSF32(FLOAT32 x)
{
    return FLOAT32(FloatLib.ABSF32(x.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Copies the sign bit of a source floating point number to a
//!        destination floating point number
//! \param xa Single precision floating point target operand
//! \param xb Single precision floating point source operand
//! \return FLOAT32 merged result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 COPYSIGNF32(FLOAT32 xa, FLOAT32 xb)
{
    return FLOAT32(FloatLib.COPYSIGNF32(xa.u32, xb.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Scales the base 2 exponent of a floating point number by a signed
//!        integer value
//! \param xa FLOAT32 floating point number to be scaled
//! \param xb INT32 integer scaling operand
//! \return FLOAT32 scaled result
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 SCALBNF32(FLOAT32 x, INT32 e)
{
    return FLOAT32(FloatLib.SCALBNF32(x.u32, e));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Extracts the signed integer base 2 exponent of a floating point
//!        number
//! \param x FLOAT32 floating point operand
//! \return INT32 extracted exponent
////////////////////////////////////////////////////////////////////////////////
inline static INT32 ILOGBF32(FLOAT32 x)
{
    return FloatLib.ILOGBF32(x.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Determines the configuration of this floating point library coded
//!        into a UINT32 bitstring
//! \return UINT32 configuration flags bitstring
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetSafeHelperFloatLibConfU32()
{
    return FloatLib.GetSafeHelperFloatLibConfU32();
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Determines the version number of the floating point library release
//! \return UINT32 version number
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetSafeHelperFloatLibVersionU32()
{
    return FloatLib.GetSafeHelperFloatLibVersionU32();
}

} // Close namespace IEEE754
using namespace IEEE754; // Set as default namespace
//! @}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the unary - operator of the CFloat type with IEEE754
//!        library function call NEGF32
//! \param rhs Right-hand side single precision floating point operand
//! \return NEGF32(rhs)
////////////////////////////////////////////////////////////////////////////////
static inline CFloat operator - (const CFloat &rhs)
{
    return FLOAT32(FloatLib.NEGF32(rhs.u32));
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary + operator of the CFloat type with IEEE754
//!        library function call ADDF32
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return ADDF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline CFloat operator + (const CFloat &lhs, const CFloat &rhs)
{
    return FLOAT32(FloatLib.ADDF32(lhs.u32, rhs.u32));
}
inline CFloat& CFloat::operator += (const CFloat &rhs)
{
    u32 = FloatLib.ADDF32(u32, rhs.u32);
    return *this;
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary - operator of the CFloat type with IEEE754
//!        library function call SUBF32
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return SUBF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline CFloat operator - (const CFloat &lhs, const CFloat &rhs)
{
    return FLOAT32(FloatLib.SUBF32(lhs.u32, rhs.u32));
}
inline CFloat& CFloat::operator -= (const CFloat &rhs)
{
    u32 = FloatLib.SUBF32(u32, rhs.u32);
    return *this;
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary * operator of the CFloat type with IEEE754
//!        library function call MULF32
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return MULF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline CFloat operator * (const CFloat &lhs, const CFloat &rhs)
{
    return FLOAT32(FloatLib.MULF32(lhs.u32, rhs.u32));
}
inline CFloat& CFloat::operator *= (const CFloat &rhs)
{
    u32 = FloatLib.MULF32(u32, rhs.u32);
    return *this;
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary / operator of the CFloat type with IEEE754
//!        library function call DIVF32
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return DIVF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline CFloat operator / (const CFloat &lhs, const CFloat &rhs)
{
    return FLOAT32(FloatLib.DIVF32(lhs.u32, rhs.u32));
}
inline CFloat& CFloat::operator /= (const CFloat &rhs)
{
    u32 = FloatLib.DIVF32(u32, rhs.u32);
    return *this;
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary == operator of the CFloat type with IEEE754
//!        library function call CEQF32.
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return CEQF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
__declspec (deprecated)
static inline BOOL operator == (const CFloat &lhs, const CFloat &rhs)
{
    return FloatLib.CEQF32(lhs.u32, rhs.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary != operator of the CFloat type with IEEE754
//!        library function call CNEF32.
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return CNEF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
__declspec (deprecated)
static inline BOOL operator != (const CFloat &lhs, const CFloat &rhs)
{
    return FloatLib.CNEF32(lhs.u32, rhs.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary < operator of the CFloat type with IEEE754
//!        library function call CLTF32.
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return CLTF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline BOOL operator <  (const CFloat &lhs, const CFloat &rhs)
{
    return FloatLib.CLTF32(lhs.u32, rhs.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary <= operator of the CFloat type with IEEE754
//!        library function call CLEF32.
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return CLEF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline BOOL operator <= (const CFloat &lhs, const CFloat &rhs)
{
    return FloatLib.CLEF32(lhs.u32, rhs.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary > operator of the CFloat type with IEEE754
//!        library function call CGTF32.
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return CGTF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline BOOL operator >  (const CFloat &lhs, const CFloat &rhs)
{
    return FloatLib.CGTF32(lhs.u32, rhs.u32);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Overloads the binary >= operator of the CFloat type with IEEE754
//!        library function call CGEF32.
//! \param lhs Left-hand side single precision floating point operand
//! \param rhs Right-hand side single precision floating point operand
//! \return CGEF32(lhs, rhs)
////////////////////////////////////////////////////////////////////////////////
static inline BOOL operator >= (const CFloat &lhs, const CFloat &rhs)
{
    return FloatLib.CGEF32(lhs.u32, rhs.u32);
}
#endif // !FLOAT_LIB_MODULE
#pragma endregion Programming interface of the floating point library

#pragma region SafeFloatMathLibrary
////////////////////////////////////////////////////////////////////////////////
//! \class  CBeckhoffCephesSingleLib (CephesSingleLib)
//! \brief  The CephesSingleLib provides pure helper functions for Safety C
//!         that can be used permissively also in expressions with side effect
//!         restrictions, such as in control flow and assertion statements.
//!         The CephesSingleLib math library is a ported subset of the Cephes
//!         single precision math library by Stephen L. Moshier that can
//!         be downloaded from Netlib repository: http://www.netlib.org/cephes/
//!         Though, Cephes library is known for good numerical accuracy, there
//!         is no guarantee for correct rounding to destination format unlike
//!         mandated for math functions of the IEEE754 'recommended functions'.
//!         Numerical methods of the Cephes library have been ported without
//!         modifications; however, some minor source code refactorings were
//!         needed to achieve pure single precision arithmetic with C/C++ type
//!         float. As such, source code literals were explicitly typed, which
//!         slightly affects numerical accuracy of the least bits. The Cephes'
//!         internal signals PLOSS, TLOSS, DOMAIN and SING trigger a fail-safe
//!         reaction. The Cephes OVERFLOW signal returns yielding +Infinite
//!         or -Infinite. The UNDERFLOW signal returns yielding a +0 or a -0.
//!         Handling of subnormal numbers and NaN is implemented on top of the
//!         arithmetic provided by the IEEE754 library (FloatLib). Input domain
//!         is further restricted by fail-safe checks in order to bound the
//!         approximation inaccuracy induced by range reduction of large inputs.
//!         If in doubt, PLEASE REFER TO THE SAFETY C CODING GUIDELINES to get
//!         an unambiguous specification of modifications w.r.t. the original
//!         Cephes single precision library with worst-case error bounds.
////////////////////////////////////////////////////////////////////////////////
#ifndef CEPHES_SINGLE_LIB_MODULE
#ifndef FLOAT_LIB_MODULE
namespace BeckhoffCephesSingleLib
{
    class CBeckhoffCephesSingleLib
    {
    public:
    //! @name Logarithmic and exponentiation functions
    //! @{
        FLOAT32 LOGF32(FLOAT32 x);
    //! @}
    //! @name Trigonometric functions
    //! @{
        FLOAT32 SINF32(FLOAT32 x);
        FLOAT32 COSF32(FLOAT32 x);
        FLOAT32 TANF32(FLOAT32 x);
    //! @}
    //! @name Cephes single precision library version info query function
    //! @{
        UINT32 GetSafeHelperCephesSingleLibVersionU32();
    //! @}
    };
}
static BeckhoffCephesSingleLib::CBeckhoffCephesSingleLib CephesSingleLib;
//! @name Static wrapper functions for access to CephesLib like a built-in C API
//! @{
namespace CEPHES {

////////////////////////////////////////////////////////////////////////////////
//! \brief Approximates the natural logarithm of a single precision floating
//!        point value that must be greater zero
//! \param x Single precision floating point input value
//! \return log(x)
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 LOGF32(FLOAT32 x)
{
    return CephesSingleLib.LOGF32(x);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Approximates the periodic sine of a single precision floating point
//!        angle x that must be given in radians with |x| <= 4096.0f
//! \param x Single precision floating point input value in radians
//! \return sin(x)
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 SINF32(FLOAT32 x)
{
    return CephesSingleLib.SINF32(x);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Approximates the periodic cosine of a single precision floating
//!        point angle x that must be given in radians with |x| <= 4096.0f
//! \param x Single precision floating point input value in radians
//! \return cos(x)
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 COSF32(FLOAT32 x)
{
    return CephesSingleLib.COSF32(x);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Approximates the periodic tangent of a single precision floating
//!        point angle x that must be given in radians with |x| <= 4096.0f
//! \param x Single precision floating point input value in radians
//! \return tan(x)
////////////////////////////////////////////////////////////////////////////////
inline static FLOAT32 TANF32(FLOAT32 x)
{
    return CephesSingleLib.TANF32(x);
}

////////////////////////////////////////////////////////////////////////////////
//! \brief Returns the version number of the CephesSingleLib math library
//! \return U32_VERSION_NO
////////////////////////////////////////////////////////////////////////////////
inline static UINT32 GetSafeHelperCephesSingleLibVersionU32()
{
    return CephesSingleLib.GetSafeHelperCephesSingleLibVersionU32();
}

} // Close namespace CEPHES
using namespace CEPHES; // Set as default namespace
//! @}
#endif // !FLOAT_LIB_MODULE
#endif // !CEPHES_SINGLE_LIB_MODULE
#pragma endregion Programming interface of the floating point math library

#pragma region SafeNonSafeBuildMacros
////////////////////////////////////////////////////////////////////////////////
//! \def    SAFE_BUILD
//! \brief  Used to toggle between non-safe C++ build (for module testing) and
//!         extended safe build (targeting TwinCAT Safety PLC). MUST BE DEFINED
//!         BY SAFETY BUILD PROCESS ONLY as non-safe build will fail otherwise!
////////////////////////////////////////////////////////////////////////////////
#ifdef SAFE_BUILD
#include "SafeModuleBase.h" // Safe build details are opaque for sake of safety
#else
#define SAFE_BUILD
#undef SAFE_BUILD

//! @name Preprocessor helper macros required for defining safe modules
//! @{
#ifndef STATIC
#define STATIC  //! Define 'static' module testing
#endif
#ifdef NAMESPACE
#error "NAMESPACE macro must not be custom-defined!"
#endif
#define NAMESPACE(name) \
    namespace name
#ifdef  SAFE_MODULE
#error "SAFE_MODULE macro must not be custom-defined!"
#endif  //! Define class C<name> and typedef alias 'CSafeModule'
#define SAFE_MODULE(name) \
    STATIC UINT32 SAFETY_PROJECT_FCS; \
    class C##name; \
    typedef C##name CSafeModule; \
    class C##name
#ifdef SAFE_MODULE_DEF
#error "SAFE_MODULE_DEF macro must not be custom-defined!"
#endif
#define SAFE_MODULE_DEF(name) \
    namespace name
//! Macro not defined for non-safe build
#define SAFE_MODULE_EXPORT()
#ifdef PUBLIC
#error "PUBLIC define must not be custom-defined!"
#endif
#define PUBLIC \
    public
#ifndef PRIVATE     //! Define 'public' to provide access for module testing
#define PRIVATE \
    private
#endif
# ifdef SAFE_MODULE_DEF_EXPORT
#error "SAFE_MODULE_DEF_EXPORT macro must not be custom-defined!"
#endif
    //! Store number of branches for coverage measurement (non-safe build only!)
#define SAFE_MODULE_DEF_EXPORT(name) \
    INT32 nBranches_##name = __COUNTER__;
//! @}

//! @name Preprocessor macros providing assertions and tracing facilities
//! @{
#include <stdexcept>
#ifdef _DEBUG
#ifndef DEBUG_ASSERT
//! Throws an exception on assertion failure (with _DEBUG and !SAFE_BUILD).
//! Catch DebugAssertEx exception to detect failures during module testing.
#define DEBUG_ASSERT(id, cond) \
    (cond) ? true : \
    throw(new DebugAssertEx(#id, #cond, __FUNCTION__, __FILE__, __LINE__));
#endif // DEBUG_ASSERT
#ifndef DEBUG_TRACE
#include <iostream>
//! Dumps the expression and its value to stdout (with _DEBUG defined only).
//! Redirect stdout for your module testing interfaces and debugging needs.
#define DEBUG_TRACE(expr) \
    cout << "DEBUG_TRACE("#expr"): " << (expr) << endl;
using namespace std;
static ostream& operator << (ostream &os, const CFloat &rhs)
{
    return (os << rhs.f32);
}
#else
#endif // _DEBUG_TRACE
#ifndef BRANCH_TRACE
#include <iostream>
//! Branch callback function pointer
extern void(* __pUserCallbackFunc__)(const char*, const char*, int, int);
//! Traces branch coverage by calling function (with _DEBUG and !SAFE_BUILD).
//! Define callback function and assign it to __pUserCallbackFunc__ pointer.
#define BRANCH_TRACE() \
    if (0 != __pUserCallbackFunc__) \
        (* __pUserCallbackFunc__)(__FILE__, __FUNCTION__, __LINE__, __COUNTER__);
#endif // BRANCH_TRACE
#else // _DEBUG
//! Not in use when SAFE_BUILD defined and _DEBUG not defined!
#define DEBUG_ASSERT(id, cond)
//! Not in use when SAFE_BUILD defined and _DEBUG not defined!
#define DEBUG_TRACE(expr)
//! Not in use when SAFE_BUILD defined and _DEBUG not defined!
#define BRANCH_TRACE()
#endif // _DEBUG
#ifdef FAILSAFE_ASSERT
#error "FAILSAFE_ASSERT macro must not be custom-defined!"
//! Real-time monitor event logging is used when SAFE_BUILD is defined only!
#ifndef RTM_EVENT_LOG
#define RTM_EVENT_LOG(context_id, group_id, event_id, param0, param1)
#endif
#endif
#ifdef _MSC_VER
#pragma warning ( disable: 4996 ) // Disable strcpy deprecated warning for MSVC++
#endif
//! Always results in fail-safe mode on assertion failure (in any build mode!).
//! Catch FailsafeAssertEx exceptions to detect failures during module testing.
#define FAILSAFE_ASSERT(id, cond) \
    (cond) ? true : \
    throw(new FailsafeAssertEx(#id, #cond, __FUNCTION__, __FILE__, __LINE__));
//! @}
//! @name Exception types thrown by assertion failures in non-safe build only
//! @{
//! Exception base type for assertion failures storing condition and location
    class AssertFailureEx : public std::exception {
public:
    static const int maxLen = 4096;
    char strCond[maxLen]; char strFunc[maxLen]; char strFile[maxLen]; int nLine;
    AssertFailureEx(const char* cond, const char* func, const char* file, int line)
    {
        strcpy(strCond, cond);  // Store condition expression
        strcpy(strFunc, func);  // Store member function name
        strcpy(strFile, file);  // Store source code file path
        nLine = line;           // Store related source code line
    }
    };
    //! Exception base type for assertion failures with unique identifiers
    class NamedAssertFailureEx : public AssertFailureEx {
public:
    char strId[maxLen];
    NamedAssertFailureEx(const char* id, const char* cond, const char* func,
                         const char* file, int line)
        :AssertFailureEx(cond, func, file, line)
    {
        strcpy(strId, id); // Store unique identifier of the assertion
    }
    };
    //! Exception type thrown by DEBUG_ASSERT(id, cond) to be caught by module tests
    class DebugAssertEx : public NamedAssertFailureEx {
public:
    DebugAssertEx(const char* id, const char* cond, const char* func,
        const char* file, int line)
        :NamedAssertFailureEx(id, cond, func, file, line) { }
};
//! Exception type thrown by FAILSAFE_ASSERT(id ,cond) to be caught by module tests
class FailsafeAssertEx : public NamedAssertFailureEx {
public:
    FailsafeAssertEx(const char* id, const char* cond, const char* func,
        const char* file, int line)
        :NamedAssertFailureEx(id, cond, func, file, line) { }
};
//! @}
#endif //SAFE_BUILD

////////////////////////////////////////////////////////////////////////////////
//! \def    NO_COMPILER_CHECKS
//! \brief  Used to toggle off static compile-time assertions. This SHOULD ONLY
//!         BE DONE for non-safe build environments WHEN compilation fails due
//!         to these checks and THE EFFECT of a failed compiler check IS KNOWN!
////////////////////////////////////////////////////////////////////////////////
#ifndef  NO_COMPILER_CHECKS
#define NO_COMPILER_CHECKS
#undef NO_COMPILER_CHECKS
// Used for static compile-time assertions without presuming C++11 type traits
template<bool> struct CompileTime { };
template<> struct CompileTime<true> { static void Assert() { } };
//! Must not be called as this is just considered for static compile-time checks!
#pragma warning ( disable: 4505 ) // Disable unreferenced function removed warning
static void STATIC_COMPILER_CONFORMANCE_CHECKS()
{
#ifdef SAFE_BUILD // Safe build presumes the Microsoft Visual C++ Compiler
#ifdef _MSC_VER
#ifndef __INTEL_COMPILER
#if (_MSC_VER < 1600) // MSVC++ (>= VS 2010) is required
#error "Safe build requires Microsoft C++ Compiler (Visual Studio 2010 or later)"
#else
#if !(_MSC_VER == 1900) // MSVC++ (VS 2015) is recommended for availability reasons
#pragma message("Safe build is recommended using Visual C++ Build Tools 2015 only")
#endif
    // Safe build fails if data types are not compiled with 1 byte alignment
    struct s { INT8 c; INT32 i; };
CompileTime<sizeof(s) == (sizeof(char) + sizeof(int))>::Assert();
#endif
#else
#error "Safe build requires the Microsoft Visual C++ Compiler"
#endif
#else
#error "Safe build requires the Microsoft Visual C++ Compiler"
#endif
#else // Non-safe test environments/tools may also use other C++ compilers;
#if __cplusplus < 199711L // however, ISO/IEC C++ 98 compliance is expected
#error "C++ compiler does not support ISO/IEC C++ 98 standard"
#endif
#endif
// ATTENTION: For reasonable test results w.r.t. the target system, these basic
// checks must pass for C++ compilers used by any non-safe build environment!
CompileTime<sizeof(char) == 1>::Assert();
CompileTime<sizeof(short) == 2>::Assert();
CompileTime<sizeof(int) == 4>::Assert(); //!< Assume ILP32/LP64/LLP64 data models
CompileTime<sizeof(long long) == 8>::Assert();
CompileTime<sizeof(float) == 4>::Assert();
CompileTime<sizeof(double) == 8>::Assert();
CompileTime<sizeof(CFloat) == 4>::Assert(); //!< Assume bit layout of 'float'
CompileTime<~(-1 ^ 0U) == 0U>::Assert(); //!< Assume 2's complement signed integers
}
#endif // NO_COMPILER_CHECKS

#pragma endregion Macros and checks for the safe and non-safe build process

#pragma region SafeArrayDataType
////////////////////////////////////////////////////////////////////////////////
//! \class      CArray
//! \brief      Template array class as a substitute for built-in C/C++ arrays
//! \details    Safe array class implementation based on a C++ template class.
//!             The subscript operator [] is overloaded for referencing single
//!             elements for read/write access using integer index types only.
//!             Accesses with indices out of array bounds are detected at run-
//!             time using fail-safe assertion checks.
//!             In contrast to built-in C/C++ array types, all array element's
//!             data is contained as member data of the CArray object (not as
//!             pointer to data). This is similar to the semantics of the STL
//!             template class std::array being introduced with C++11. As
//!             such, CArray objects of equal type can be also copied/assigned.
//! \attention  Be aware that CArray objects ARE ALWAYS PASSED AS A COPY AND
//!             NEVER PASSED BY REFERENCE! As such, there is a difference in
//!             the semantics compared with built-in C/C++ arrays behaving
//!             like pointer arguments, i.e., data is referenced by default.
//! \attention  Be also aware that passing CArray objects as a parameter needs
//!             to allocate the whole array object data on the stack which
//!             introduces a significant performance overhead for large arrays.
//! \attention  Be aware that the indexing is always zero-based, thus, the last
//!             valid index is N-1 with N being the statically allocated
//!             number of array elements.
//! \attention  Be aware that array objects are not initialized by default.
//!             Thus, all array elements have an undefined value like any other
//!             uninitialized variable and need to be assigned before reading.
////////////////////////////////////////////////////////////////////////////////
template<typename T1, INT32 N, INT32 K = 1, INT32 L = 1, BOOL LUT = false>
struct CArray
{
    //!< Struct member holding space for data of N elements with type T1
    T1 data[N];

    //! Static array member to query number of elements as signed integer value
    static constexpr INT32 COUNTI32() { return N * K * L; }

    //! Static array members to query size of dimensions as signed integer value
    static constexpr INT32 DIM1I32() { return N; }
    static constexpr INT32 DIM2I32() { return K; }
    static constexpr INT32 DIM3I32() { return L; }

    //! C-like const and non-const access with native subscript operator
    template <typename T2> T1 & operator [] (const T2 idx)
    {
        // Check for a valid index inside array bounds
        FAILSAFE_ASSERT(ARRAY_INDEX_NEGATIVE, idx >= 0);
        FAILSAFE_ASSERT(ARRAY_INDEX_EXCEEDED, idx < N);

        // Return reference to data element
        if ((idx >= 0) && (idx < N)) // Required to avoid C6835
            return data[idx]; // With valid index
        else
            return data[0]; // Invalid element, but error state is triggered!
    }
    template <typename T2> const T1 & operator [] (const T2 idx) const
    {
        // Check for a valid index inside array bounds
        FAILSAFE_ASSERT(ARRAY_INDEX_NEGATIVE, idx >= 0);
        FAILSAFE_ASSERT(ARRAY_INDEX_EXCEEDED, idx < N);

        // Return reference to data element
        if ((idx >= 0) && (idx < N)) // Required to avoid C6835
            return data[idx]; // With valid index
        else
            return data[0]; // Invalid element, but error state is triggered!
    }
};

//! @name Macros for custom user type definitions
//! @{
//! 1-dimensional array type definition
#define DEF_ARRAY_TYPE(name, d1, type) \
    typedef CArray<type, d1> name;
//! 2-dimensional array type definition
#define DEF_ARRAY_TYPE_2D(name, d1, d2, type) \
    typedef CArray<CArray<type, d2>, d1, d2> name;
//! 3-dimensional array type definition
#define DEF_ARRAY_TYPE_3D(name, d1, d2, d3, type) \
    typedef CArray<CArray<CArray<type, d3>, d2, d3>, d1, d2, d3> name;
//! 1-dimensional lookup table type definition
#define DEF_LUT_TYPE(name, d1, type) \
    typedef CArray<type, d1, 1, 1, true> name;
//! 2-dimensional lookup table type definition
#define DEF_LUT_TYPE_2D(name, d1, d2, type) \
    typedef CArray<CArray<type, d2, 1, 1, true>, d1, d2, 1, true> name;
//! 3-dimensional lookup table type definition
#define DEF_LUT_TYPE_3D(name, d1, d2, d3, type) \
    typedef CArray<CArray<CArray<type, d3, 1, 1, true>, d2, d3, 1, true>, \
                                                       d1, d2, d3, true> name;
//! Compound structure type (C struct) definition
#define DEF_STRUCT_TYPE(name) \
    struct name
//! @}

#pragma endregion Definition and programming interface of safe array data types
