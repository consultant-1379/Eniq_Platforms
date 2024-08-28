Option Strict Off
Option Explicit On

Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Collections
Imports busobj

<TestFixture()> _
Public Class UniverseFunctionsTPIdeTest

    Private univFunctionsTPIde As UniverseFunctionsTPIdeForTest
    Private mocks As NMock2.Mockery

    ''
    'Private class for test.
    '@remarks Overrides functions that should not be executed in tests.
    Private Class UniverseFunctionsTPIdeForTest
        Inherits UniverseFunctionsTPIde

        Public tables As Designer.ITables
        Public table As Designer.ITable
        Public joins As Designer.IJoins
        Public join As Designer.IJoin
        Public m_boBlockStructure As busobj.BlockStructure

        Public Sub New()

        End Sub

        Public Sub New(ByVal newTables As Designer.ITables, ByVal newTable As Designer.ITable)
            tables = newTables
            table = newTable
        End Sub

        Public Sub New(ByVal newTables As Designer.ITables, ByVal newTable As Designer.ITable, ByVal newJoins As Designer.IJoins, _
                       ByVal newJoin As Designer.IJoin)
            tables = newTables
            table = newTable
            joins = newJoins
            join = newJoin
        End Sub

        Public Property BoBlockStructure() As busobj.BlockStructure
            Get
                Return m_boBlockStructure
            End Get
            Set(ByVal value As busobj.BlockStructure)
                m_boBlockStructure = value
            End Set
        End Property

        Public Overrides Sub universe_BuildRankBHContexts(ByVal universeProxy As IUniverseProxy, ByRef mts As MeasurementTypesTPIde, _
                                    ByRef univ_joins As UnivJoinsTPIde)
            ' dummy method
        End Sub

        Protected Overrides Sub Universe_BuildContexts(ByRef Univ As Designer.IUniverse, ByRef mts As MeasurementTypesTPIde, _
                                                         ByRef univ_joins As UnivJoinsTPIde)
            ' dummy method
        End Sub

        Protected Overrides Function getJoins(ByRef Univ As Designer.IUniverse) As Designer.IJoins
            Return joins
        End Function

        Protected Overrides Function getJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin
            Return join
        End Function

        Protected Overrides Function addJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin
            Return join
        End Function

        Protected Overrides Function convertReportStructureItem(ByVal boRepStrucItem As busobj.ReportStructureItem) As busobj.BlockStructure
            Return m_boBlockStructure
        End Function

    End Class

    <SetUp()> _
    Public Sub SetUp()
        univFunctionsTPIde = New UniverseFunctionsTPIdeForTest()
        mocks = New NMock2.Mockery()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try
        univFunctionsTPIde = Nothing
    End Sub


    Private Function createDummyMeasTypes() As MeasurementTypesTPIde
        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"

        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.ExtendedUniverse = "b"

        Dim measTypeALL As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeALL.ExtendedUniverse = "all"

        Dim measTypeEMPTY As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeEMPTY.ExtendedUniverse = ""

        Dim fullListOfMTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMTypes.AddItem(measTypeA)
        fullListOfMTypes.AddItem(measTypeB)
        fullListOfMTypes.AddItem(measTypeALL)
        fullListOfMTypes.AddItem(measTypeEMPTY)
        Return fullListOfMTypes
    End Function

    <Test()> _
    Public Sub addJoinsTest()
        ' Set up universe joins:
        Dim univJoins As UnivJoinsTPIde = New UnivJoinsTPIde()
        Dim univJoin As UnivJoinsTPIde.UnivJoin = New UnivJoinsTPIde.UnivJoin()
        univJoin.Expression = "DC.DC_E_MGW_AAL1TPVCCTP_DAY.OSS_ID = DC.DIM_E_TDRAN_TDRBS.RNC"
        univJoin.Cardinality = "1_to_1"
        univJoins.AddItem(univJoin)

        ' Set up extra joins:
        Dim extraJoins As UnivJoinsTPIde = New UnivJoinsTPIde()
        Dim extraJoin As UnivJoinsTPIde.UnivJoin = New UnivJoinsTPIde.UnivJoin()
        extraJoin.Expression = "DC.DC_E_MGW_AAL1TPVCCTP_DAY.OSS_ID = DC.DIM_E_TDRAN_TDRBS.RNC"
        extraJoin.Cardinality = "1_to_1"
        extraJoins.AddItem(univJoin)

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"

        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.ExtendedUniverse = "b"

        Dim measTypeALL As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeALL.ExtendedUniverse = "all"

        Dim measTypeEMPTY As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeEMPTY.ExtendedUniverse = ""

        Dim fullListOfMTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMTypes.AddItem(measTypeA)
        fullListOfMTypes.AddItem(measTypeB)
        fullListOfMTypes.AddItem(measTypeALL)
        fullListOfMTypes.AddItem(measTypeEMPTY)

        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Dim mockTables As ITables
        mockTables = mocks.NewMock(Of ITables)()

        Dim mockTable As ITable
        mockTable = mocks.NewMock(Of ITable)()

        Dim mockJoins As IJoins
        mockJoins = mocks.NewMock(Of IJoins)()

        Dim mockJoin As IJoin
        mockJoin = mocks.NewMock(Of IJoin)()

        univFunctionsTPIde = New UniverseFunctionsTPIdeForTest(mockTables, mockTable, mockJoins, mockJoin)
        univFunctionsTPIde.updatedJoins = ""

        Expect.Once.On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value("Universe Name"))
        Expect.Once.On(mockJoin).GetProperty("Expression").Will(NMock2.Return.Value("DC.DC_E_MGW_AAL1TPVCCTP_DAY.OSS_ID = DC.DIM_E_TDRAN_TDRBS.RNC"))
        Expect.Exactly(2).On(mockJoin).SetProperty("Cardinality").To(Designer.DsCardinality.dsOneToOneCardinality)

        Dim returnValue As Boolean = univFunctionsTPIde.Universe_AddJoins(mockUniverse, fullListOfMTypes, univJoins, extraJoins)
        Dim expectedValue As Boolean = True
        Assert.AreEqual(expectedValue, returnValue, "Universe_AddJoins() should return true")
    End Sub

    <Test()> _
    Public Sub getRankBHReportNameTest()
        ' Test the situation where a valid report number is given (happy cases):
        Dim reportname As String = univFunctionsTPIde.getRankBHReportName(1, 4, "BHTYPE", "TARGETMEASTYPE", "EXTNTEST")
        Dim expectedReportname As String = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE_EXTNTEST_1"
        Assert.AreEqual(expectedReportname, reportname, _
                        "Rank busy hour report name should include busy hour type, target type, correct report number and extension")

        reportname = univFunctionsTPIde.getRankBHReportName(1, 4, "BHTYPE", "TARGETMEASTYPE", "")
        expectedReportname = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE_1"
        Assert.AreEqual(expectedReportname, reportname, _
                        "Rank busy hour report name should not include extension if it is blank")

        reportname = univFunctionsTPIde.getRankBHReportName(1, 1, "BHTYPE", "TARGETMEASTYPE", "")
        expectedReportname = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE"
        Assert.AreEqual(expectedReportname, reportname, _
                        "Rank busy hour report name should not include number if only one report")
    End Sub

    <Test()> _
    Public Sub getRankBHReportNameOutOfBoundsTest()
        ' Test the situation where the report number is out of bounds (5 when the total number of reports is 4):
        Dim reportname As String = univFunctionsTPIde.getRankBHReportName(5, 4, "BHTYPE", "TARGETMEASTYPE", "EXTNTEST")
        Dim expectedReportname As String = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE_EXTNTEST"
        Assert.IsFalse(reportname.Contains("5"), "Out of bounds report number should return report name with no report number")
        Assert.AreEqual(expectedReportname, reportname, "Out of bounds report number should return report name with no report number")

        reportname = univFunctionsTPIde.getRankBHReportName(5, 4, "BHTYPE", "TARGETMEASTYPE", "")
        expectedReportname = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE"
        Assert.IsFalse(reportname.Contains("5"), "Out of bounds report number should return report name with no report number")
        Assert.AreEqual(expectedReportname, reportname, "Out of bounds report number should return report name with no report number")
    End Sub

    <Test()> _
    Public Sub getRankBHReportNameInvalidNumberTest()
        Dim reportname As String = univFunctionsTPIde.getRankBHReportName(-1, 4, "BHTYPE", "TARGETMEASTYPE", "EXTNTEST")
        Dim expectedReportname As String = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE_EXTNTEST"
        Assert.AreEqual(expectedReportname, reportname, "Invalid report number should return report name with no report number")
    End Sub

    <Test()> _
    Public Sub getRankBHReportNameInvalidTotalTest()
        Dim reportname As String = univFunctionsTPIde.getRankBHReportName(10, 0, "BHTYPE", "TARGETMEASTYPE", "EXTNTEST")
        Dim expectedReportname As String = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE_EXTNTEST"
        Assert.AreEqual(expectedReportname, reportname, "Invalid total reports number should return report name with no report number")
    End Sub

    <Test()> _
    Public Sub getRankBHReportNameNullArgsTest()
        Dim reportname As String = univFunctionsTPIde.getRankBHReportName(1, 0, Nothing, Nothing, Nothing)
        Dim expectedReportname As String = ""
        Assert.AreEqual(reportname, expectedReportname, "Invalid arguments should return empty string")
    End Sub

    <Test()> _
    Public Sub getRankBHReportNameEmptyExtensionTest()
        Dim reportname As String = univFunctionsTPIde.getRankBHReportName(1, 4, "BHTYPE", "TARGETMEASTYPE", "")
        Dim expectedReportname As String = "Verification_BHTYPE_RANKBH_TARGETMEASTYPE_1"
        Assert.AreEqual(reportname, expectedReportname, "Invalid arguments should return empty string")
    End Sub

    ''
    'A test instance of UniverseFunctionsTPIde. 
    'This test class overrides displayMessageBox() so that it is not displayed when the unit tests are run.
    Private Class UniverseFunctionsTPIdeCheckUnvNameTest
        Inherits UniverseFunctionsTPIde

        ' The user's choice when they click Yes or No for the message box.
        Private m_usersChoice As MsgBoxResult

        ' Property method to get and set the m_usersChoice variable.
        ' Will be used by tests to set up 
        Public Property UsersChoice() As MsgBoxResult
            Get
                m_usersChoice = m_usersChoice
            End Get

            Set(ByVal choice As MsgBoxResult)
                m_usersChoice = choice
            End Set
        End Property

        ' Overridden version of displayMessageBox() to return m_usersChoice.
        ' Avoids displaying the message box when tests are run.
        'Protected Overrides Function displayMessageBox(ByVal message As String, ByVal msgBoxStyle As MsgBoxStyle, _
        'ByVal msgBoxTitle As String) As MsgBoxResult
        'Return m_usersChoice
        'End Function
    End Class
    ' Test for getUniverseFilename(), positive test case where BO version is "XI" and extension is defined.
    ' This function gets the filename a universe will be saved to.
    ' Should return the fullName appended with the full extension: "TP Ericsson CPP PM Standard".
    <Test()> _
    Public Sub getUniverseFilenameXIWithExtensionTest()
        ' Test instance of UniverseFunctionsTPIde:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeCheckUnvNameTest = New UniverseFunctionsTPIdeCheckUnvNameTest()
        Dim dceName As String = "DCE1"
        Dim dceExtension As String = "a"
        Dim fullName As String = "TP Ericsson CPP PM"
        Dim fullExtension As String = "Standard"
        Dim BoVersion As String = "XI"

        Dim expectedFilename As String = "TP Ericsson CPP PM Standard"
        Dim result As String = univFunctionsTPIde.getUniverseFilename(dceName, dceExtension, fullName, fullExtension, BoVersion)
        Assert.AreEqual(result, expectedFilename, "Filename should have the full universe name with extension")
    End Sub

    ' Test for getUniverseFilename(), positive test case where BO version is "XI" and extension is defined.
    ' This function gets the filename a universe will be saved to.
    ' Should return the fullName appended with the full extension: "TP Ericsson CPP PM Standard".
    <Test()> _
    Public Sub getUniverseFilenameXINoExtensionTest()
        ' Test instance of UniverseFunctionsTPIde:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeCheckUnvNameTest = New UniverseFunctionsTPIdeCheckUnvNameTest()
        Dim dceName As String = "DCE1"
        Dim dceExtension As String = "a"
        Dim fullName As String = "TP Ericsson BSS PM"
        Dim fullExtension As String = ""
        Dim BoVersion As String = "XI"

        Dim expectedFilename As String = "TP Ericsson BSS PM"
        Dim result As String = univFunctionsTPIde.getUniverseFilename(dceName, dceExtension, fullName, fullExtension, BoVersion)
        Assert.AreEqual(result, expectedFilename, "Filename should have the full universe name with no extension")
    End Sub

    ' Test for getUniverseFilename(), positive test case where BO version is "6.5" and extension is defined.
    ' This function gets the filename a universe will be saved to.
    ' Should return the fullName appended with the full extension: "TP Ericsson CPP PM Standard".
    <Test()> _
    Public Sub getUniverseFilename65WithExtensionTest()
        ' Test instance of UniverseFunctionsTPIde:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeCheckUnvNameTest = New UniverseFunctionsTPIdeCheckUnvNameTest()
        Dim dceName As String = "DCE1"
        Dim dceExtension As String = "a"
        Dim fullName As String = "TP Ericsson CPP PM"
        Dim fullExtension As String = "Standard"
        Dim BoVersion As String = "6.5"

        Dim expectedFilename As String = "DCE1a"
        Dim result As String = univFunctionsTPIde.getUniverseFilename(dceName, dceExtension, fullName, fullExtension, BoVersion)
        Assert.AreEqual(result, expectedFilename, "Filename should have the short universe name with short extension")
    End Sub

    ' Test for getUniverseFilename(), positive test case where BO version is "6.5" and extension is defined.
    ' This function gets the filename a universe will be saved to.
    ' Should return the fullName appended with the full extension: "TP Ericsson CPP PM Standard".
    <Test()> _
    Public Sub getUniverseFilename65NoExtensionTest()
        ' Test instance of UniverseFunctionsTPIde:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeCheckUnvNameTest = New UniverseFunctionsTPIdeCheckUnvNameTest()
        Dim dceName As String = "DCE1"
        ' No extension is defined here:
        Dim dceExtension As String = ""
        Dim fullName As String = "TP Ericsson BSS PM"
        Dim fullExtension As String = ""
        Dim BoVersion As String = "6.5"

        Dim expectedFilename As String = "DCE1"
        Dim result As String = univFunctionsTPIde.getUniverseFilename(dceName, dceExtension, fullName, fullExtension, BoVersion)
        Assert.AreEqual(result, expectedFilename, "Filename should have the short universe name")
    End Sub

    ' Test for getUniverseFilename(), positive test case where BO version is "6.5" and extension is defined.
    ' This function gets the filename a universe will be saved to.
    ' Should return the fullName appended with the full extension: "TP Ericsson CPP PM Standard".
    <Test()> _
    Public Sub getUniverseFilenameInvalidBOVersionTest()
        ' Test instance of UniverseFunctionsTPIde:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeCheckUnvNameTest = New UniverseFunctionsTPIdeCheckUnvNameTest()
        Dim dceName As String = "DCE1"
        ' No extension is defined here:
        Dim dceExtension As String = ""
        Dim fullName As String = "TP Ericsson BSS PM"
        Dim fullExtension As String = ""
        Dim BoVersion As String = "WRONG"

        Dim expectedFilename As String = "Universe"
        Dim result As String = univFunctionsTPIde.getUniverseFilename(dceName, dceExtension, fullName, fullExtension, BoVersion)
        Assert.AreEqual(result, expectedFilename, "Filename should have default value if BO version is incorrect")
    End Sub

    ' Test for getUniverseFilename(), positive test case where BO version is "6.5" and extension is defined.
    ' This function gets the filename a universe will be saved to.
    ' Should return the fullName appended with the full extension: "TP Ericsson CPP PM Standard".
    <Test()> _
    Public Sub getUniverseFilenameNullArgsTest()
        ' Test instance of UniverseFunctionsTPIde:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeCheckUnvNameTest = New UniverseFunctionsTPIdeCheckUnvNameTest()
        ' Arguments are null:
        Dim dceName As String = Nothing
        Dim dceExtension As String = Nothing
        Dim fullName As String = Nothing
        Dim fullExtension As String = Nothing
        Dim BoVersion As String = Nothing

        Dim expectedFilename As String = "Universe"
        Dim result As String = univFunctionsTPIde.getUniverseFilename(dceName, dceExtension, fullName, fullExtension, BoVersion)
        Assert.AreEqual(result, expectedFilename, "Filename should have default value if arguments are null")
    End Sub

    <Test()> _
   Public Sub getRankBHTargetTypesTest()
        Dim targetTypes As ArrayList = New ArrayList()

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.RankTable = True

        Dim fullListOfMTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMTypes.AddItem(measTypeA)

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Expect.Once.On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Once.On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(targetTypes))

        Dim universeFunctions As UniverseFunctionsTPIde = New UniverseFunctionsTPIde(dbProxyMock, tpUtilsMock)

        Dim returnValue As ArrayList = universeFunctions.getRankBHTargetTypes("techpackTPIde", "DC_E_AAL2APBH")
        Assert.IsNotNull(returnValue, "getRankBHTargetTypes() should return a value")
    End Sub

    ''
    'Private class for test.
    '@remarks Overrides functions that should not be executed in tests.
    Private Class UniverseFunctionsTPIdeRankBHReportTest
        Inherits UniverseFunctionsTPIde

        Public Sub New(ByVal databaseProxy As DBProxy, ByRef newTPUtilities As ITPUtilitiesTPIde)
            MyBase.New(DatabaseProxy, newTPUtilities)
        End Sub

        Public Overrides Sub createRankBusyHourReport(ByRef BoApp As busobj.IApplication, ByRef BoVersion As String, ByRef rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                                        ByRef targetMType As MeasurementTypesTPIde.MeasurementType, ByVal targetType As String, _
                                        ByVal OutputDir As String, ByRef mts As MeasurementTypesTPIde, ByVal UniverseExtension As String)
            ' overridden in test
        End Sub
    End Class

    <Test()> _
   Public Sub makeVerificationReport_RankBHTest()
        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)

        ' Set up a rank measurement type:
        Dim rankMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMType.ExtendedUniverse = "a"
        rankMType.RankTable = True
        rankMType.TypeName = "DC_E_CPP_AAL2APBH"
        rankMType.MeasurementTypeID = "DC_E_CPP_AAL2APBH"

        ' Set up a target measurement type:
        Dim targetMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        targetMType.ExtendedUniverse = "a"
        targetMType.RankTable = False
        targetMType.TypeName = "DC_E_CPP_AAL2AP"
        targetMType.MeasurementTypeID = "DC_E_CPP_AAL2AP"

        ' Set up measurement types:
        Dim mTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde()
        mTypes.AddItem(rankMType)
        mTypes.AddItem(targetMType)

        ' Set up the target types array (target types is defined as a list of Strings in the busy hour):
        Dim mappedTypesForBusyHour As ArrayList = New ArrayList()
        mappedTypesForBusyHour.Add("DC_E_CPP_AAL2AP")

        ' Set up the target types array:
        Dim rankTypes As ArrayList = New ArrayList()
        rankTypes.Add(rankMType)

        Expect.Once.On(tpUtilsMock).Method("getRankMeasurementTypes").WithAnyArguments().Will(NMock2.Return.Value(rankTypes))
        ' Called when getting target types:
        Expect.Exactly(2).On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Exactly(2).On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(mappedTypesForBusyHour))
        Expect.Once.On(tpUtilsMock).Method("getMeasurementTypeByName").WithAnyArguments().Will(NMock2.Return.Value(targetMType))
        Expect.Once.On(tpUtilsMock).Method("displayMessageBox").WithAnyArguments().Will(NMock2.Return.Value(MsgBoxResult.Yes))

        univFunctionsTPIde.VerifReports_makeVerificationReport_RankBH(busobjAppMock, "outputDirectory", True, mTypes, "Standard", "XI")
    End Sub

    <Test()> _
    Public Sub makeVerificationReport_RankBH_TypeNotFoundTest()

        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)

        ' Set up a rank measurement type:
        Dim rankMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMType.ExtendedUniverse = "a"
        rankMType.RankTable = True
        rankMType.TypeName = "DC_E_CPP_AAL2APBH"
        rankMType.MeasurementTypeID = "DC_E_CPP_AAL2APBH"

        ' Set up a target measurement type:
        Dim targetMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        targetMType.ExtendedUniverse = "a"
        targetMType.RankTable = True
        targetMType.TypeName = "DC_E_CPP_AAL2AP"
        targetMType.MeasurementTypeID = "DC_E_CPP_AAL2AP"

        ' Set up measurement types:
        Dim mTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde()
        ' Measurement types have one rank measurement type, and one ordinary type:
        mTypes.AddItem(rankMType)
        mTypes.AddItem(targetMType)

        ' Set up the mapped types array (mapped/target types is defined as a list of Strings in the busy hour):
        Dim mappedTypesForBusyHour As ArrayList = New ArrayList()
        ' Mapped types has a measurement type that is not in the list of types for current universe:
        mappedTypesForBusyHour.Add("DC_E_CPP_MT_FROM_DIFFERENT_UNIVERSE")

        ' Set up the target types array:
        Dim rankTypes As ArrayList = New ArrayList()
        rankTypes.Add(rankMType)

        Expect.Once.On(tpUtilsMock).Method("getRankMeasurementTypes").WithAnyArguments().Will(NMock2.Return.Value(rankTypes))

        ' Called when getting target types:
        Expect.Once.On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Once.On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(mappedTypesForBusyHour))

        ' Called when getting the source types for the busy hour:
        Expect.Once.On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Once.On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(mappedTypesForBusyHour))

        Expect.Once.On(tpUtilsMock).Method("getMeasurementTypeByName").WithAnyArguments().Will(NMock2.Return.Value(Nothing))

        univFunctionsTPIde.VerifReports_makeVerificationReport_RankBH(busobjAppMock, "outputDirectory", True, mTypes, "Standard", "XI")
    End Sub

    ''' 
    ''Test for buildRankBHReportTables when busy hour is an element busy hour.
    ''
    <Test()> _
    Public Sub buildRankBHReportTables_ElemBHTest()

        Dim documentMock As busobj.Document
        documentMock = mocks.NewMock(Of busobj.Document)()

        Dim reportProxyMock As IReportProxy
        reportProxyMock = mocks.NewMock(Of IReportProxy)()

        ' Report mocks:
        Dim reportMock As busobj.Report
        reportMock = mocks.NewMock(Of busobj.Report)()

        Dim structureMock As busobj.SectionStructure
        structureMock = mocks.NewMock(Of busobj.SectionStructure)()

        Dim boRepStrucItemsMock As busobj.ReportStructureItems
        boRepStrucItemsMock = mocks.NewMock(Of busobj.ReportStructureItems)()

        Dim boRepStrucItemMock As busobj.ReportStructureItem
        boRepStrucItemMock = mocks.NewMock(Of busobj.ReportStructureItem)()

        Dim boBlockStrucMock As busobj.BlockStructure
        boBlockStrucMock = mocks.NewMock(Of busobj.BlockStructure)()

        ' Data provider mocks:
        Dim rankDPMock As busobj.DataProvider
        rankDPMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim rawDPMock As busobj.DataProvider
        rawDPMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim daybhDPMock As busobj.DataProvider
        daybhDPMock = mocks.NewMock(Of busobj.DataProvider)()

        ' This is for an element busy hour report:
        Dim elemBusyHour As Boolean = True

        Dim numberOfRepStructItems As Integer = 1

        ' Create test instance:
        univFunctionsTPIde = New UniverseFunctionsTPIdeForTest()
        univFunctionsTPIde.BoBlockStructure = boBlockStrucMock

        Expect.Once.On(reportMock).GetProperty("GeneralSectionStructure").Will(NMock2.Return.Value(structureMock))
        Expect.Once.On(structureMock).GetProperty("Body").Will(NMock2.Return.Value(boRepStrucItemsMock))
        Expect.Once.On(boRepStrucItemsMock).GetProperty("Count").Will(NMock2.Return.Value(numberOfRepStructItems))
        Expect.Once.On(boRepStrucItemsMock).Method("get_Item").Will(NMock2.Return.Value(boRepStrucItemMock))
        Expect.Once.On(boRepStrucItemMock).GetProperty("Type").Will(NMock2.Return.Value(busobj.BoReportItemType.boTable))
        Expect.AtLeastOnce.On(boBlockStrucMock).GetProperty("Name").Will(NMock2.Return.Value("Day Table"))

        ' If the table is the Day Table, and the busy hour is an element busy hour 
        ' then the table should be deleted:
        Expect.Once.On(boBlockStrucMock).Method("Delete")

        univFunctionsTPIde.buildRankBHReportTables(documentMock, reportMock, rankDPMock, rawDPMock, daybhDPMock, elemBusyHour, reportProxyMock)
    End Sub

    ''' 
    ''Test for buildRankBHReportTables.
    ''
    <Test()> _
    Public Sub buildRankBHReportTablesTest()

        Dim documentMock As busobj.Document
        documentMock = mocks.NewMock(Of busobj.Document)()

        Dim reportProxyMock As IReportProxy
        reportProxyMock = mocks.NewMock(Of IReportProxy)()

        ' Report mocks:
        Dim reportMock As busobj.Report
        reportMock = mocks.NewMock(Of busobj.Report)()

        Dim structureMock As busobj.SectionStructure
        structureMock = mocks.NewMock(Of busobj.SectionStructure)()

        Dim boRepStrucItemsMock As busobj.ReportStructureItems
        boRepStrucItemsMock = mocks.NewMock(Of busobj.ReportStructureItems)()

        Dim boRepStrucItemMock As busobj.ReportStructureItem
        boRepStrucItemMock = mocks.NewMock(Of busobj.ReportStructureItem)()

        Dim boBlockStrucMock As busobj.BlockStructure
        boBlockStrucMock = mocks.NewMock(Of busobj.BlockStructure)()

        Dim pivotMock As busobj.Pivot
        pivotMock = mocks.NewMock(Of busobj.Pivot)()

        Dim columnsMock As busobj.Columns
        columnsMock = mocks.NewMock(Of busobj.Columns)()

        ' Data provider mocks:
        Dim rankDPMock As busobj.DataProvider
        rankDPMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim rawDPMock As busobj.DataProvider
        rawDPMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim daybhDPMock As busobj.DataProvider
        daybhDPMock = mocks.NewMock(Of busobj.DataProvider)()

        ' This is for an element busy hour report:
        Dim elemBusyHour As Boolean = False

        Expect.Once.On(reportMock).GetProperty("GeneralSectionStructure").Will(NMock2.Return.Value(structureMock))
        Expect.Once.On(structureMock).GetProperty("Body").Will(NMock2.Return.Value(boRepStrucItemsMock))

        Dim numberOfRepStructItems As Integer = 3
        Expect.Once.On(boRepStrucItemsMock).GetProperty("Count").Will(NMock2.Return.Value(numberOfRepStructItems))

        ' Create test instance:
        univFunctionsTPIde = New UniverseFunctionsTPIdeForTest()
        univFunctionsTPIde.BoBlockStructure = boBlockStrucMock

        Stub.On(boRepStrucItemsMock).Method("get_Item").Will(NMock2.Return.Value(boRepStrucItemMock))
        Stub.On(boRepStrucItemMock).GetProperty("Type").Will(NMock2.Return.Value(busobj.BoReportItemType.boTable))

        ' Set up expectations for the name:
        Expect.Once.On(boBlockStrucMock).GetProperty("Name").Will(NMock2.Return.Value("Rank Table"))
        Expect.Once.On(reportProxyMock).Method("buildRankBHTable").With(documentMock, boBlockStrucMock, _
                                                                  rankDPMock, "Rank Table", "(RANKBH)", "dummy_rank")

        Expect.Exactly(2).On(boBlockStrucMock).GetProperty("Name").Will(NMock2.Return.Value("Raw Table"))
        Expect.Once.On(reportProxyMock).Method("buildRankBHTable").With(documentMock, boBlockStrucMock, _
                                                                  rawDPMock, "Raw Table", "(RAW)", "dummy_raw")
        Expect.Once.On(reportProxyMock).Method("addVariableToTable").With(rawDPMock, boBlockStrucMock, documentMock)

        Expect.Exactly(3).On(boBlockStrucMock).GetProperty("Name").Will(NMock2.Return.Value("Day Table"))
        Expect.Once.On(reportProxyMock).Method("buildRankBHTable").With(documentMock, boBlockStrucMock, _
                                                                  daybhDPMock, "Day Table", "(DayBH)", "dummy_day")

        univFunctionsTPIde.buildRankBHReportTables(documentMock, reportMock, rankDPMock, rawDPMock, daybhDPMock, elemBusyHour, reportProxyMock)
    End Sub

    <Test()> _
    Public Sub universe_BuildRankBHContextsTest()
        Dim tpUtilsMock As ITPUtilitiesTPIde = mocks.NewMock(Of ITPUtilitiesTPIde)()
        Dim dbProxyMock As DBProxy = mocks.NewMock(Of DBProxy)()
        Dim busobjAppMock As busobj.IApplication = mocks.NewMock(Of busobj.IApplication)()
        Dim contextMock As Designer.Context = mocks.NewMock(Of Designer.Context)()
        Dim contextsMock As Designer.Contexts = mocks.NewMock(Of Designer.Contexts)()
        Dim joinsMock As Designer.Joins = mocks.NewMock(Of Designer.Joins)()
        Dim joinMock As Designer.Join = mocks.NewMock(Of Designer.Join)()
        Dim universeProxyMock As IUniverseProxy = mocks.NewMock(Of IUniverseProxy)()
        Dim enumeratorMock As IEnumerator = mocks.NewMock(Of IEnumerator)()

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)

        ' Set up a rank measurement type:
        Dim rankMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMType.ExtendedUniverse = "a"
        rankMType.RankTable = True
        rankMType.TypeName = "DC_E_CPP_AAL2APBH"
        rankMType.MeasurementTypeID = "DC_E_CPP_AAL2APBH"

        ' Set up the rank measurement types arraylist:
        Dim rankTypes As ArrayList = New ArrayList()
        rankTypes.Add(rankMType)

        ' Set up universe joins:
        Dim univJoins As UnivJoinsTPIde = New UnivJoinsTPIde()
        Dim univJoin As UnivJoinsTPIde.UnivJoin = New UnivJoinsTPIde.UnivJoin()
        univJoin.Expression = "DC.DC_E_CPP_AAL2AP_DAY.OSS_ID = DC.DC_E_CPP_AAL2APBH_RANKBH.OSS_ID"
        univJoin.Cardinality = "1_to_1"
        univJoin.Contexts = "RANKBH"
        univJoins.AddItem(univJoin)

        ' Set up rankbh joins:
        Dim rankbhJoins As ArrayList = New ArrayList()
        Dim rankbhJoin As UnivJoinsTPIde.UnivJoin = New UnivJoinsTPIde.UnivJoin()
        rankbhJoin.Expression = "DC.DC_E_CPP_AAL2AP_DAY.OSS_ID = DC.DC_E_CPP_AAL2APBH_RANKBH.OSS_ID"
        rankbhJoin.Cardinality = "1_to_1"
        rankbhJoin.Contexts = "RANKBH"
        rankbhJoins.Add(rankbhJoin)
        univJoins.RankBHJoins = rankbhJoins

        ' Set up measurement types:
        Dim mTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde()
        mTypes.AddItem(rankMType)

        Dim expectedRankBHContextName As String = rankMType.TypeName & "_RANKBH"

        ' These expectations should be run if busy hour is not for ELEMBH:
        Expect.Once.On(tpUtilsMock).Method("getRankMeasurementTypes").With(mTypes).Will(NMock2.Return.Value(rankTypes))
        Expect.Once.On(universeProxyMock).Method("addContext").With(expectedRankBHContextName).Will(NMock2.Return.Value(contextMock))

        ' TODO: improve expectations for the Name property:
        Stub.On(contextMock).GetProperty("Name").Will(NMock2.Return.Value(expectedRankBHContextName))
        ' Expect a join to be added:
        expectAddJoinToContext(contextMock, joinsMock, enumeratorMock, joinMock, univJoin.Expression)

        ' Search for contexts:
        Expect.Once.On(universeProxyMock).Method("getContexts").Will(NMock2.Return.Value(contextsMock))
        Expect.Once.On(contextsMock).Method("GetEnumerator").Will(NMock2.Return.Value(enumeratorMock))
        Expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(True))
        Expect.Once.On(enumeratorMock).GetProperty("Current").Will(NMock2.Return.Value(contextMock))

        ' Expect a rank busy hour join to be added:
        expectAddJoinToContext(contextMock, joinsMock, enumeratorMock, joinMock, univJoin.Expression)

        UniverseFunctionsTPIde.updatedContexts = ""
        univFunctionsTPIde.universe_BuildRankBHContexts(universeProxyMock, mTypes, univJoins)
    End Sub

    Private Sub expectAddJoinToContext(ByVal contextMock As Designer.Context, ByVal joinsMock As Designer.Joins, _
                                       ByVal enumeratorMock As IEnumerator, ByVal joinMock As Designer.Join, ByVal joinExpression As String)
        Expect.Once.On(contextMock).GetProperty("Joins").Will(NMock2.Return.Value(joinsMock))
        Expect.Once.On(joinsMock).Method("GetEnumerator").Will(NMock2.Return.Value(enumeratorMock))
        Expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(True))
        Expect.Once.On(enumeratorMock).GetProperty("Current").Will(NMock2.Return.Value(joinMock))
        Expect.Once.On(joinMock).GetProperty("Expression").Will(NMock2.Return.Value(joinExpression))
    End Sub

    <Test()> _
    Public Sub removeObjectsForEBSTest()
        Dim ebsTechPack As Boolean = True
        Dim measTypeName As String = "DC_E_BSS_ATERTRANS"
        Dim classMock As Designer.Class = mocks.NewMock(Of Designer.Class)()
        Dim objectMock As Designer.Object = mocks.NewMock(Of Designer.Object)()

        Dim universeProxyMock As IUniverseProxy = mocks.NewMock(Of IUniverseProxy)()
        Dim boObjectsMock As IBOObjectsTPIde = mocks.NewMock(Of IBOObjectsTPIde)()
        Dim cnts As CountersTPIde = New CountersTPIde()

        Expect.Once.On(boObjectsMock).Method("removeObjectsForEBS")

        univFunctionsTPIde.removeObjectsForEBS(ebsTechPack, classMock, measTypeName, cnts, universeProxyMock, boObjectsMock)
    End Sub

    <Test()> _
    Public Sub removeObjectsForEBS_ClassNotFoundTest()
        Dim ebsTechPack As Boolean = True
        Dim measTypeName As String = "DC_E_BSS_ATERTRANS"
        Dim objectMock As Designer.Object = mocks.NewMock(Of Designer.Object)()
        Dim classMock As Designer.Class = mocks.NewMock(Of Designer.Class)()
        Dim universeProxyMock As IUniverseProxy = mocks.NewMock(Of IUniverseProxy)()
        Dim boObjectsMock As IBOObjectsTPIde = mocks.NewMock(Of IBOObjectsTPIde)()
        Dim cnts As CountersTPIde = New CountersTPIde()

        Expect.Once.On(universeProxyMock).Method("getClass").WithAnyArguments().Will(NMock2.Return.Value(classMock))
        Expect.Once.On(boObjectsMock).Method("removeObjectsForEBS")

        univFunctionsTPIde.removeObjectsForEBS(ebsTechPack, Nothing, measTypeName, cnts, universeProxyMock, boObjectsMock)
    End Sub

    ' <Test()> _
    Public Sub createRankBusyHourReportTest()
        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        ' Set up a rank measurement type:
        Dim rankMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMType.ExtendedUniverse = "a"
        rankMType.RankTable = True
        rankMType.TypeName = "DC_E_CPP_AAL2APBH"
        rankMType.MeasurementTypeID = "DC_E_CPP_AAL2APBH"

        ' Set up a dummy counter:
        Dim counter As CountersTPIde.Counter = New CountersTPIde.Counter()
        counter.CounterName = "dummyCounter"

        ' Set up a target measurement type:
        Dim targetMType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        targetMType.ExtendedUniverse = "a"
        targetMType.RankTable = True
        targetMType.TypeName = "DC_E_CPP_AAL2AP"
        targetMType.MeasurementTypeID = "DC_E_CPP_AAL2AP"
        targetMType.Counters = New CountersTPIde()
        targetMType.Counters.AddItem(counter)

        ' Set up measurement types:
        Dim mTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde()
        ' Measurement types have one rank measurement type, and one ordinary type:
        mTypes.AddItem(rankMType)
        mTypes.AddItem(targetMType)

        Dim boVersion As String = "XI"
        Dim universeExtension As String = ""

        ' univFunctionsTPIde.CountersPerVerificationReport = 100
        univFunctionsTPIde.createRankBusyHourReport(busobjAppMock, boVersion, rankMType, targetMType, targetMType.TypeName, _
                                                    "example\output\dir\", mTypes, universeExtension)
    End Sub

    ' Tests adding a class key object to a data provider.
    ' The key object is not hidden.
    <Test()> _
    Public Sub VerifReports_AddClassKeyObjects_Test()
        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        Dim dataProviderMock As busobj.DataProvider
        dataProviderMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim classMock As Designer.Class
        Dim objectMock As Designer.Object
        Dim mockBusobjObjects As Designer.Objects
        Dim mockBusobjQueries As busobj.Queries
        Dim mockBusobjResults As busobj.Results
        Dim mockBusobjQuery As busobj.Query

        classMock = mocks.NewMock(Of Designer.Class)()
        objectMock = mocks.NewMock(Of Designer.Object)()
        mockBusobjObjects = mocks.NewMock(Of Designer.Objects)()
        mockBusobjQueries = mocks.NewMock(Of busobj.Queries)()
        mockBusobjQuery = mocks.NewMock(Of busobj.Query)()
        mockBusobjResults = mocks.NewMock(Of busobj.Results)()

        Dim excludedKeyObjects As String() = New String() {"hours from now", "datetime (raw)", "Datetime (UTC)", "Busy Hour Type", "Min"}

        Dim counterKeys As CounterKeysTPIde = New CounterKeysTPIde
        Dim counterKey As CounterKeysTPIde.CounterKey = New CounterKeysTPIde.CounterKey()
        counterKey.UnivObject = "FUNCTION_NAME"
        counterKeys.AddItem(counterKey)

        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.CounterKeys = counterKeys

        expect.Exactly(2).On(classMock).GetProperty("Objects").Will(NMock2.Return.Value(mockBusobjObjects))
        expect.Once.On(mockBusobjObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        Stub.On(mockBusobjObjects).Method("get_Item").Will(NMock2.Return.Value(objectMock))
        expect.Once.On(objectMock).GetProperty("Name").Will(NMock2.Return.Value("Object name"))

        expect.Exactly(2).On(classMock).GetProperty("Name").Will(NMock2.Return.Value("Class name"))

        ' Expect call to addResultObject():
        expect.Once.On(dataProviderMock).GetProperty("Name").Will(NMock2.Return.Value("Data provider name"))
        expect.Once.On(dataProviderMock).GetProperty("Queries").Will(NMock2.Return.Value(mockBusobjQueries))
        Stub.On(mockBusobjQueries).Method("get_Item").Will(NMock2.Return.Value(mockBusobjQuery))
        expect.Once.On(mockBusobjQuery).GetProperty("Results").Will(NMock2.Return.Value(mockBusobjResults))
        expect.Once.On(mockBusobjResults).Method("Add")

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)

        Dim hiddenObject As String = "DummyClass/DummyObject;"
        Dim hiddenObjects As ArrayList = New ArrayList()
        hiddenObjects.Add(hiddenObject)

        univFunctionsTPIde.VerifReports_AddClassKeyObjects(dataProviderMock, classMock, excludedKeyObjects, measTypeA, _
                                                           hiddenObjects, "DummyClass/DummyObject;")
    End Sub

    ' Tests adding a class key object to a data provider.
    ' The key object is hidden but it is not defined in the counter keys or the universe objects
    ' so it should not be added.
    <Test()> _
    Public Sub VerifReports_AddClassKeyObjects_HiddenObjectAndNotDefined_Test()
        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        Dim dataProviderMock As busobj.DataProvider
        dataProviderMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim classMock As Designer.Class
        Dim objectMock As Designer.Object
        Dim mockBusobjObjects As Designer.Objects
        Dim mockBusobjQueries As busobj.Queries
        Dim mockBusobjResults As busobj.Results
        Dim mockBusobjQuery As busobj.Query

        classMock = mocks.NewMock(Of Designer.Class)()
        objectMock = mocks.NewMock(Of Designer.Object)()
        mockBusobjObjects = mocks.NewMock(Of Designer.Objects)()
        mockBusobjQueries = mocks.NewMock(Of busobj.Queries)()
        mockBusobjQuery = mocks.NewMock(Of busobj.Query)()
        mockBusobjResults = mocks.NewMock(Of busobj.Results)()

        Dim excludedKeyObjects As String() = New String() {"hours from now", "datetime (raw)", "Datetime (UTC)", "Busy Hour Type", "Min"}

        Dim counterKeys As CounterKeysTPIde = New CounterKeysTPIde
        Dim counterKey As CounterKeysTPIde.CounterKey = New CounterKeysTPIde.CounterKey()
        counterKey.UnivObject = "FUNCTION_NAME"
        counterKeys.AddItem(counterKey)

        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.CounterKeys = counterKeys

        expect.Exactly(2).On(classMock).GetProperty("Objects").Will(NMock2.Return.Value(mockBusobjObjects))
        expect.Once.On(mockBusobjObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        Stub.On(mockBusobjObjects).Method("get_Item").Will(NMock2.Return.Value(objectMock))
        expect.Once.On(objectMock).GetProperty("Name").Will(NMock2.Return.Value("Object name"))

        expect.Exactly(2).On(classMock).GetProperty("Name").Will(NMock2.Return.Value("Class name"))

        ' Expect no call to addResultObject() because the object is hidden and won't be added.

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)
        Dim hiddenObject As String = "Class name/Object name;"
        Dim hiddenObjects As ArrayList = New ArrayList()
        hiddenObjects.Add(hiddenObject)

        Dim universeObjects As String = "DummyClass/DummyObject;"
        univFunctionsTPIde.VerifReports_AddClassKeyObjects(dataProviderMock, classMock, excludedKeyObjects, measTypeA, _
                                                           hiddenObjects, universeObjects)
    End Sub

    ' Tests adding a class key object to a data provider.
    ' The key object is hidden. Not in universe objects, but it is in the counter keys defined in the measurement type.
    <Test()> _
    Public Sub VerifReports_AddClassKeyObjects_HiddenObjectButCounterKey_Test()
        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        Dim dataProviderMock As busobj.DataProvider
        dataProviderMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim classMock As Designer.Class
        Dim objectMock As Designer.Object
        Dim mockBusobjObjects As Designer.Objects
        Dim mockBusobjQueries As busobj.Queries
        Dim mockBusobjResults As busobj.Results
        Dim mockBusobjQuery As busobj.Query

        classMock = mocks.NewMock(Of Designer.Class)()
        objectMock = mocks.NewMock(Of Designer.Object)()
        mockBusobjObjects = mocks.NewMock(Of Designer.Objects)()
        mockBusobjQueries = mocks.NewMock(Of busobj.Queries)()
        mockBusobjQuery = mocks.NewMock(Of busobj.Query)()
        mockBusobjResults = mocks.NewMock(Of busobj.Results)()

        Dim excludedKeyObjects As String() = New String() {"hours from now", "datetime (raw)", "Datetime (UTC)", "Busy Hour Type", "Min"}

        Dim counterKeys As CounterKeysTPIde = New CounterKeysTPIde
        Dim counterKey As CounterKeysTPIde.CounterKey = New CounterKeysTPIde.CounterKey()
        counterKey.UnivObject = "FUNCTION_NAME"
        counterKeys.AddItem(counterKey)

        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.CounterKeys = counterKeys

        expect.Exactly(2).On(classMock).GetProperty("Objects").Will(NMock2.Return.Value(mockBusobjObjects))
        expect.Once.On(mockBusobjObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        Stub.On(mockBusobjObjects).Method("get_Item").Will(NMock2.Return.Value(objectMock))
        expect.Once.On(objectMock).GetProperty("Name").Will(NMock2.Return.Value("Object name"))

        expect.Exactly(2).On(classMock).GetProperty("Name").Will(NMock2.Return.Value("Class name"))

        ' Expect no call to addResultObject() because the object is hidden and won't be added.
        expect.Once.On(dataProviderMock).GetProperty("Name").Will(NMock2.Return.Value("Data provider name"))
        expect.Once.On(dataProviderMock).GetProperty("Queries").Will(NMock2.Return.Value(mockBusobjQueries))
        Stub.On(mockBusobjQueries).Method("get_Item").Will(NMock2.Return.Value(mockBusobjQuery))
        expect.Once.On(mockBusobjQuery).GetProperty("Results").Will(NMock2.Return.Value(mockBusobjResults))
        expect.Once.On(mockBusobjResults).Method("Add")

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)

        Dim hiddenObject As String = "Class name/FUNCTION_NAME;"
        Dim hiddenObjects As ArrayList = New ArrayList()
        hiddenObjects.Add(hiddenObject)

        Dim universeObjects As String = "DummyClass/DummyObject;"
        univFunctionsTPIde.VerifReports_AddClassKeyObjects(dataProviderMock, classMock, excludedKeyObjects, measTypeA, _
                                                           hiddenObjects, universeObjects)
    End Sub

    ' Tests adding a class key object to a data provider.
    ' The object is hidden. It is not in the counter keys defined in the measurement type,
    ' but it is defined in the Universe -> Objects tab.
    <Test()> _
    Public Sub VerifReports_AddClassKeyObjects_HiddenObjectButUniverseObject_Test()
        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim busobjAppMock As busobj.IApplication
        busobjAppMock = mocks.NewMock(Of busobj.IApplication)()

        Dim dataProviderMock As busobj.DataProvider
        dataProviderMock = mocks.NewMock(Of busobj.DataProvider)()

        Dim classMock As Designer.Class
        Dim objectMock As Designer.Object
        Dim mockBusobjObjects As Designer.Objects
        Dim mockBusobjQueries As busobj.Queries
        Dim mockBusobjResults As busobj.Results
        Dim mockBusobjQuery As busobj.Query

        classMock = mocks.NewMock(Of Designer.Class)()
        objectMock = mocks.NewMock(Of Designer.Object)()
        mockBusobjObjects = mocks.NewMock(Of Designer.Objects)()
        mockBusobjQueries = mocks.NewMock(Of busobj.Queries)()
        mockBusobjQuery = mocks.NewMock(Of busobj.Query)()
        mockBusobjResults = mocks.NewMock(Of busobj.Results)()

        Dim excludedKeyObjects As String() = New String() {"hours from now", "datetime (raw)", "Datetime (UTC)", "Busy Hour Type", "Min"}

        Dim counterKeys As CounterKeysTPIde = New CounterKeysTPIde
        Dim counterKey As CounterKeysTPIde.CounterKey = New CounterKeysTPIde.CounterKey()
        counterKey.UnivObject = "Dummy name"
        counterKeys.AddItem(counterKey)

        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.CounterKeys = counterKeys

        expect.Exactly(2).On(classMock).GetProperty("Objects").Will(NMock2.Return.Value(mockBusobjObjects))
        expect.Once.On(mockBusobjObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        Stub.On(mockBusobjObjects).Method("get_Item").Will(NMock2.Return.Value(objectMock))
        expect.Once.On(objectMock).GetProperty("Name").Will(NMock2.Return.Value("Object name"))

        expect.Exactly(2).On(classMock).GetProperty("Name").Will(NMock2.Return.Value("Class name"))

        ' Expect no call to addResultObject() because the object is hidden and won't be added.
        expect.Once.On(dataProviderMock).GetProperty("Name").Will(NMock2.Return.Value("Data provider name"))
        expect.Once.On(dataProviderMock).GetProperty("Queries").Will(NMock2.Return.Value(mockBusobjQueries))
        Stub.On(mockBusobjQueries).Method("get_Item").Will(NMock2.Return.Value(mockBusobjQuery))
        expect.Once.On(mockBusobjQuery).GetProperty("Results").Will(NMock2.Return.Value(mockBusobjResults))
        expect.Once.On(mockBusobjResults).Method("Add")

        ' Set up test object:
        Dim univFunctionsTPIde As UniverseFunctionsTPIdeRankBHReportTest = New UniverseFunctionsTPIdeRankBHReportTest(dbProxyMock, tpUtilsMock)

        Dim hiddenObject As String = "Class name/FUNCTION_NAME;"
        Dim hiddenObjects As ArrayList = New ArrayList()
        hiddenObjects.Add(hiddenObject)

        Dim universeObjects As String = "Class name/FUNCTION_NAME;"
        univFunctionsTPIde.VerifReports_AddClassKeyObjects(dataProviderMock, classMock, excludedKeyObjects, measTypeA, _
                                                           hiddenObjects, universeObjects)
    End Sub

    <Test()> _
    Public Sub getHiddenObjectsTest()
        Dim classesMock As Designer.Classes = mocks.NewMock(Of Designer.Classes)()
        Dim classMock As Designer.Class = mocks.NewMock(Of Designer.Class)()
        Dim objectsMock As Designer.Objects = mocks.NewMock(Of Designer.Objects)()
        Dim objectMock As Designer.Object = mocks.NewMock(Of Designer.Object)()

        Dim enumeratorMock As IEnumerator = mocks.NewMock(Of IEnumerator)()
        Dim occurrences As Designer.Class() = New Designer.Class() {classMock}

        expectGetHiddenObjects(classesMock, enumeratorMock, classMock, objectsMock, objectMock, 0)

        Dim hiddenObjects As ArrayList = New ArrayList()
        Dim returnValue As ArrayList = univFunctionsTPIde.getHiddenObjects(classesMock, hiddenObjects)
        Assert.IsTrue(returnValue.Item(0) = "Class name/Object name;", "getHiddenObjects() should return 'Class name/Object name;'")
    End Sub

    <Test()> _
    Public Sub getHiddenObjects_RecursiveTest()
        Dim classesMock As Designer.Classes = mocks.NewMock(Of Designer.Classes)()
        Dim classMock As Designer.Class = mocks.NewMock(Of Designer.Class)()
        Dim objectsMock As Designer.Objects = mocks.NewMock(Of Designer.Objects)()
        Dim objectMock As Designer.Object = mocks.NewMock(Of Designer.Object)()

        Dim enumeratorMock As IEnumerator = mocks.NewMock(Of IEnumerator)()
        Dim occurrences As Designer.Class() = New Designer.Class() {classMock}

        expectGetHiddenObjects(classesMock, enumeratorMock, classMock, objectsMock, objectMock, 1)

        expectGetHiddenObjects(classesMock, enumeratorMock, classMock, objectsMock, objectMock, 0)

        Dim hiddenObjects As ArrayList = New ArrayList()
        Dim returnValue As ArrayList = univFunctionsTPIde.getHiddenObjects(classesMock, hiddenObjects)

        Dim result As Boolean = returnValue.Item(0) = "Class name/Object name;" And returnValue.Item(1) = "Class name/Object name;"
        Assert.IsTrue(result = True, "getHiddenObjects() should return 'Class name/Object name;Class name/Object name;'")
        Assert.IsTrue(returnValue.Count = 2, "getHiddenObjects() should return two elements")
    End Sub

    <Test()> _
    Public Sub getHiddenObjectsNoClassesTest()
        Dim classesMock As Designer.Classes = mocks.NewMock(Of Designer.Classes)()
        Dim classMock As Designer.Class = mocks.NewMock(Of Designer.Class)()
        Dim objectsMock As Designer.Objects = mocks.NewMock(Of Designer.Objects)()
        Dim objectMock As Designer.Object = mocks.NewMock(Of Designer.Object)()

        Dim enumeratorMock As IEnumerator = mocks.NewMock(Of IEnumerator)()
        Dim occurrences As Designer.Class() = New Designer.Class() {classMock}

        ' Expectations for the For Each loop for Classes:
        expect.Once.On(classesMock).Method("GetEnumerator").Will(NMock2.Return.Value(enumeratorMock))
        expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(False))

        Dim hiddenObjects As ArrayList = New ArrayList()
        Dim returnValue As ArrayList = univFunctionsTPIde.getHiddenObjects(classesMock, hiddenObjects)
        Assert.IsTrue(returnValue.Count = 0, "getHiddenObjects() should an empty list if there are no classes")
    End Sub

    <Test()> _
    Public Sub getRankBHSourceTypesTest()
        Dim sourceTables As ArrayList = New ArrayList()
        sourceTables.Add("DC_E_RAN_UCELL_RAW")
        sourceTables.Add("DC_E_RAN_UERC_COUNT")
        sourceTables.Add("DIM_E_RAN")

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        expect.Once.On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        expect.Once.On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(sourceTables))

        Dim universeFunctions As UniverseFunctionsTPIde = New UniverseFunctionsTPIde(dbProxyMock, tpUtilsMock)
        Dim dummyTechPackId As String = "DC_E_RAN:((130))"
        Dim rankBusyHourMT As String = "DC_E_RAN_ELEMBH"

        Dim returnValue As ArrayList = universeFunctions.getRankBHSourceTypes(dummyTechPackId, rankBusyHourMT)

        Assert.IsTrue(returnValue.Contains("DC_E_RAN_UCELL"))
        Assert.IsTrue(returnValue.Contains("DC_E_RAN_UERC"))
        Assert.IsTrue(returnValue.Contains("DIM_E_RAN"))
    End Sub

    <Test()> _
    Public Sub getRankBHSourceTypes_NoDataReadTest()
        ' Database query will return an empty list:
        Dim sourceTables As ArrayList = New ArrayList()

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        expect.Once.On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        expect.Once.On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(sourceTables))

        Dim universeFunctions As UniverseFunctionsTPIde = New UniverseFunctionsTPIde(dbProxyMock, tpUtilsMock)
        Dim dummyTechPackId As String = "DC_E_RAN:((130))"
        Dim rankBusyHourMT As String = "DC_E_RAN_ELEMBH"

        Dim returnValue As ArrayList = universeFunctions.getRankBHSourceTypes(dummyTechPackId, rankBusyHourMT)

        Assert.IsTrue(returnValue.Count = 0, "getRankBHSourceTypes() should return an empty list if no source types are found in the database")
    End Sub

    <Test()> _
    Public Sub getRankBHSourceTypes_IncorrectDataTest()
        ' Database query will return a list with unexpected data:
        Dim sourceTables As ArrayList = New ArrayList()
        sourceTables.Add("DC_E_RAN_UCELL_DAY")
        sourceTables.Add("DC_E_RAN_UERC_DAYBH")
        ' Test empty string:
        sourceTables.Add("")

        Dim dbProxyMock As DBProxy
        dbProxyMock = mocks.NewMock(Of DBProxy)()

        Dim tpUtilsMock As ITPUtilitiesTPIde
        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()

        expect.Once.On(dbProxyMock).Method("setupDatabaseReader").WithAnyArguments()
        expect.Once.On(dbProxyMock).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(sourceTables))

        Dim universeFunctions As UniverseFunctionsTPIde = New UniverseFunctionsTPIde(dbProxyMock, tpUtilsMock)
        Dim dummyTechPackId As String = "DC_E_RAN:((130))"
        Dim rankBusyHourMT As String = "DC_E_RAN_ELEMBH"

        Dim returnValue As ArrayList = universeFunctions.getRankBHSourceTypes(dummyTechPackId, rankBusyHourMT)

        Assert.IsTrue(returnValue.Count = 0, "getRankBHSourceTypes() should return an empty list if no source types are found in the database")
    End Sub

    Private Sub expectGetHiddenObjects(ByVal classesMock As Designer.Classes, ByVal enumeratorMock As IEnumerator, _
                                       ByVal classMock As Designer.Class, ByVal objectsMock As Designer.Objects, _
                                       ByVal objectMock As Designer.Object, ByVal classCount As Integer)
        ' Expectations for the For Each loop for Classes:
        expect.Once.On(classesMock).Method("GetEnumerator").Will(NMock2.Return.Value(enumeratorMock))
        expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(True))
        expect.Once.On(enumeratorMock).GetProperty("Current").Will(NMock2.Return.Value(classMock))

        expect.Once.On(classMock).GetProperty("Objects").Will(NMock2.Return.Value(objectsMock))
        ' Expectations for the For Each loop for the class objects:
        expect.Once.On(objectsMock).Method("GetEnumerator").Will(NMock2.Return.Value(enumeratorMock))
        expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(True))
        expect.Once.On(enumeratorMock).GetProperty("Current").Will(NMock2.Return.Value(objectMock))
        expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(False))

        ' Find a hidden object:
        expect.Once.On(objectMock).GetProperty("Show").Will(NMock2.Return.Value(False))

        Stub.On(classMock).GetProperty("Name").Will(NMock2.Return.Value("Class name"))
        Stub.On(objectMock).GetProperty("Name").Will(NMock2.Return.Value("Object name"))

        expect.Once.On(classMock).GetProperty("Classes").Will(NMock2.Return.Value(classesMock))
        expect.Once.On(classesMock).GetProperty("Count").Will(NMock2.Return.Value(classCount))

        If (classCount > 0) Then
            expect.Once.On(classMock).GetProperty("Classes").Will(NMock2.Return.Value(classesMock))
        End If

        expect.Once.On(enumeratorMock).Method("MoveNext").Will(NMock2.Return.Value(False))
    End Sub

End Class
