Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Collections

<TestFixture()> _
Public Class BOConditionsTPIdeTest

    Private mocks As NMock2.Mockery

    Dim universeProxyMock As IUniverseProxy
    Dim databaseProxyMock As DBProxy
    Dim mockTPUtilities As ITPUtilitiesTPIde
    Dim fullListOfMTypes As MeasurementTypesTPIde
    Dim mockClass As IClass
    Dim mockObject As IObject
    Dim mockCondition As Designer.PredefinedCondition
    Dim testCounterKeys As CounterKeysTPIde
    Dim rankMTArrayList As ArrayList
    Dim boConditionsTPIde As BOConditionsTPIdeForTest

    <SetUp()> _
    Public Sub SetUp()
        mocks = New NMock2.Mockery()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try

        boConditionsTPIde = Nothing
        mockTPUtilities = Nothing
        fullListOfMTypes = Nothing
        mockClass = Nothing
        mockObject = Nothing
        mockCondition = Nothing
        testCounterKeys = Nothing
        rankMTArrayList = Nothing
    End Sub

    Private Sub setupMocks()
        ' Set up mocks:
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        databaseProxyMock = mocks.NewMock(Of DBProxy)()
        boConditionsTPIde = New BOConditionsTPIdeForTest(universeProxyMock, databaseProxyMock)

        mockTPUtilities = mocks.NewMock(Of ITPUtilitiesTPIde)()
        fullListOfMTypes = setupDummyMeasurementTypes()
        mockClass = mocks.NewMock(Of Designer.IClass)()
        mockCondition = mocks.NewMock(Of Designer.PredefinedCondition)()
        mockObject = mocks.NewMock(Of Designer.IObject)()

        boConditionsTPIde.ConditionParse = True
        boConditionsTPIde.TestTPUtilities = mockTPUtilities

        ' Set up counter keys:
        testCounterKeys = New CounterKeysTPIde()
        Dim key As CounterKeysTPIde.CounterKey = New CounterKeysTPIde.CounterKey()
        key.CounterKeyName = "test key"
        ' Just one element key:
        key.Element = 1
        testCounterKeys.AddItem(key)

        ' Set up rank measurement types:
        rankMTArrayList = New ArrayList()
        Dim rankMT As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMT.ExtendedUniverse = "a"
        rankMT.TypeName = "DC_E_MGW_AAL2APBH"
        rankMT.CounterKeys = testCounterKeys
        rankMTArrayList.Add(rankMT)
    End Sub

    ''
    ' Sets up the expectations for adding a single condition:
    '@param numberOfCalls       The number of calls to addCondition that we are expecting.
    '@param     newCondition    True if this is a new condition.
    Private Sub setupAddConditionExpectations(ByVal numberOfCalls As Integer, ByVal newCondition As Boolean)
        For count As Integer = 1 To numberOfCalls
            Expect.Once.On(universeProxyMock).Method("getClass").WithAnyArguments().Will(NMock2.Return.Value(mockClass))

            If (newCondition) Then
                ' Mock the case where the condition has not been added yet:
                Expect.Once.On(universeProxyMock).Method("getPredefinedCondition").WithAnyArguments() _
                                .Will(NMock2.Throw.Exception(New Exception("Could not find class")))

                Expect.Once.On(universeProxyMock).Method("addPredefinedCondition").WithAnyArguments() _
                                .Will(NMock2.Return.Value(mockCondition))
            Else
                ' Mock the case where the condition exists already:
                Expect.Once.On(universeProxyMock).Method("getPredefinedCondition").WithAnyArguments().Will(NMock2.Return.Value(mockCondition))
            End If

            ' For recording the condition that was added:
            Expect.Once.On(mockCondition).GetProperty("Name")
            Expect.Once.On(mockClass).GetProperty("Name")

            ' Getting the object the condition is selecting:
            Expect.Once.On(universeProxyMock).Method("getObject").WithAnyArguments().Will(NMock2.Return.Value(mockObject))
            ' Setting details for condition:
            Expect.Once.On(mockCondition).SetProperty("Description")
            Expect.Once.On(mockCondition).SetProperty("Where")
            Expect.Once.On(mockCondition).Method("Parse")
        Next
    End Sub

    ' Test adding a condition, when the condition already exists in the universe:
    <Test()> _
    Public Sub addConditionAlreadyExistsTest()
        ' Set up mocks:
        setupMocks()

        ' Set up expectations:
        setupAddConditionExpectations(1, False)
        Expect.AtLeastOnce.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsCharacterObject))

        Dim universeClass As String = "Test Class"
        Dim universeObject As String = "Test Object"
        Dim description As String = "Description"
        Dim returnValue = boConditionsTPIde.addCondition(universeClass, universeObject, description)
        Assert.IsTrue(returnValue, "Adding a universe condition should return true if it is successful")
    End Sub

    ' Test adding a new condition, when the condition is not already in the universe:
    <Test()> _
    Public Sub addConditionNewTest()
        ' Set up mocks:
        setupMocks()

        ' Set up expectations:
        setupAddConditionExpectations(1, True)
        Expect.AtLeastOnce.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsCharacterObject))

        Dim universeClass As String = "Test Class"
        Dim universeObject As String = "Test Object"
        Dim description As String = "Description"
        Dim returnValue = boConditionsTPIde.addCondition(universeClass, universeObject, description)
        Assert.IsTrue(returnValue, "Adding a universe condition should return true if it is successful")
    End Sub

    ' Test adding a condition, adding fails:
    <Test()> _
    Public Sub addConditionFailsTest()
        ' Set up mocks:
        setupMocks()

        ' Test situation where the universe class is not found, only needs one expectation:
        Expect.Once.On(universeProxyMock).Method("getClass").WithAnyArguments().Will(NMock2.Return.Value(Nothing))

        Dim universeClass As String = "Test Class"
        Dim universeObject As String = "Test Object"
        Dim description As String = "Description"
        Dim returnValue = boConditionsTPIde.addCondition(universeClass, universeObject, description)
        Assert.IsFalse(returnValue, "Adding a universe condition should return fail if the universe class is not found")
    End Sub

    <Test()> _
    Public Sub addBusyHourConditionsTest()
        setupMocks()
        Expect.Once.On(mockTPUtilities).Method("getRankMeasurementTypes").With(fullListOfMTypes).Will(NMock2.Return.Value(rankMTArrayList))
        ' Adding busy hour conditions will add 3 conditions:
        setupAddConditionExpectations(3, False)
        ' Also add a condition for each key
        setupAddConditionExpectations(testCounterKeys.Count, False)

        ' Get object type will be called at least once each time we add a condition.
        ' We are adding four conditions in total here (3 busy hour conditions and 1 key condition):
        Expect.AtLeast(4).On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsCharacterObject))

        Dim returnValue As Boolean = boConditionsTPIde.addBusyHourConditions(fullListOfMTypes)
        Assert.IsTrue(returnValue, "Adding busy hour conditions should return true")
    End Sub

    <Test()> _
    Public Sub addBusyHourConditionsNullArgumentsTest()
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        boConditionsTPIde = New BOConditionsTPIdeForTest(universeProxyMock, databaseProxyMock)
        Dim returnValue As Boolean = boConditionsTPIde.addBusyHourConditions(Nothing)
        Assert.IsFalse(returnValue, "Adding busy hour conditions should return False if null arguments passed")
    End Sub

    <Test()> _
    Public Sub addBusyHourConditionsNoRankMTsTest()
        mockTPUtilities = mocks.NewMock(Of ITPUtilitiesTPIde)()
        fullListOfMTypes = setupDummyMeasurementTypes()
        boConditionsTPIde = New BOConditionsTPIdeForTest(universeProxyMock, databaseProxyMock)

        ' Empty rank measurement type list:
        rankMTArrayList = New ArrayList()
        boConditionsTPIde.TestTPUtilities = mockTPUtilities

        'Set expectations:
        Expect.Once.On(mockTPUtilities).Method("getRankMeasurementTypes").With(fullListOfMTypes).Will(NMock2.Return.Value(rankMTArrayList))

        Dim returnValue As Boolean = boConditionsTPIde.addBusyHourConditions(fullListOfMTypes)
        ' mocks.VerifyAllExpectationsHaveBeenMet()
        Assert.IsTrue(returnValue, "Adding busy hour conditions should return true")
    End Sub

    <Test()> _
    Public Sub getPromptTypeNumericTest()
        ' Set up mocks:
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        mockObject = mocks.NewMock(Of Designer.IObject)()
        Dim boConditionsTPIde As BOConditionsTPIde = New BOConditionsTPIde(universeProxyMock)

        ' Set up expectations:
        Expect.AtLeastOnce.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsNumericObject))
        Dim returnValue = boConditionsTPIde.getPromptType(mockObject)
        Assert.IsTrue(returnValue = "N", "Prompt type should be 'N' for numeric object")
    End Sub

    <Test()> _
    Public Sub getPromptTypeAlphanumericTest()
        ' Set up mocks:
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        mockObject = mocks.NewMock(Of Designer.IObject)()
        Dim boConditionsTPIde As BOConditionsTPIde = New BOConditionsTPIde(universeProxyMock)

        ' Set up expectations:
        Stub.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsDateObject))
        Dim returnValue = boConditionsTPIde.getPromptType(mockObject)
        Assert.IsTrue(returnValue = "D", "Prompt type should be 'D' for character object")
    End Sub

    <Test()> _
    Public Sub getPromptTypeDateTest()
        ' Set up mocks:
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        mockObject = mocks.NewMock(Of Designer.IObject)()
        Dim boConditionsTPIde As BOConditionsTPIde = New BOConditionsTPIde(universeProxyMock)

        ' Set up expectations:
        Stub.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsCharacterObject))
        Dim returnValue = boConditionsTPIde.getPromptType(mockObject)
        Assert.IsTrue(returnValue = "A", "Prompt type should be 'A' for character object")
    End Sub

    <Test()> _
    Public Sub getPromptTypeNullArgsTest()
        ' Set up mocks:
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        Dim boConditionsTPIde As BOConditionsTPIde = New BOConditionsTPIde(universeProxyMock)

        ' Set up expectations:
        Dim returnValue = boConditionsTPIde.getPromptType(Nothing)
        Assert.IsTrue(returnValue = "A", "Prompt type should be 'A' if object is null")
    End Sub

    <Test()> _
    Public Sub getPromptTypeNullObjectTest()
        ' Set up mocks:
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        mockObject = mocks.NewMock(Of Designer.IObject)()
        Dim boConditionsTPIde As BOConditionsTPIde = New BOConditionsTPIde(universeProxyMock)

        ' Set up expectations:
        Stub.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsNullObject))
        Dim returnValue = boConditionsTPIde.getPromptType(mockObject)
        Assert.IsTrue(returnValue = "A", "Prompt type should be 'A' for null object")
    End Sub

    ' Test adding the conditions.
    ' This is the main function that is called by UniverseFunctionsTPIde.vb to add conditions to the universe.
    <Test()> _
    Public Sub addConditionsTest()
        ' Set up mocks:
        setupMocks()

        Dim connMock As System.Data.Odbc.OdbcConnection '= mocks.NewMock(Of System.Data.Odbc.OdbcConnection)()
        Dim dbCommandMock As System.Data.Odbc.OdbcCommand '= mocks.NewMock(Of System.Data.Odbc.OdbcCommand)()
        Dim dbReaderMock As System.Data.Odbc.OdbcDataReader '= mocks.NewMock(Of System.Data.Odbc.OdbcDataReader)()

        ' Set up the database reader:
        Expect.Once.On(databaseProxyMock).Method("closeDatabase")
        Expect.Once.On(databaseProxyMock).Method("closeConnection")
        Expect.Once.On(databaseProxyMock).Method("openConnection")
        Expect.Once.On(databaseProxyMock).Method("setupDatabaseReader").WithAnyArguments()

        ' Read one line from the database
        Expect.Once.On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(True))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(2).Will(NMock2.Return.Value("test"))

        Expect.Once.On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(False))
        Expect.Once.On(databaseProxyMock).Method("closeDatabase")

        Dim result As Boolean = boConditionsTPIde.addConditions("tp_name", connMock, dbCommandMock, dbReaderMock, _
                                                                fullListOfMTypes, True, True, "Standard", "DC_E_SGSN:((10))")
        Assert.IsTrue(result, "addConditions() should complete successfully if condition is set up ok")
    End Sub

    ' Test getBHPromptText for element busy hour measurement type:
    <Test()> _
    Public Sub getBHPromptTextForElemBHTest()
        ' Set up mocks:
        setupMocks()

        Dim connMock As System.Data.Odbc.OdbcConnection
        Dim dbCommandMock As System.Data.Odbc.OdbcCommand
        Dim dbReaderMock As System.Data.Odbc.OdbcDataReader

        Dim rankMT As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMT.ExtendedUniverse = "a"
        rankMT.TypeName = "DC_E_MGW_AAL2APBH"
        rankMT.CounterKeys = testCounterKeys
        rankMT.ElementBusyHours = True

        Dim actualResult As String = boConditionsTPIde.getBHPromptText(rankMT, "Busy Hour Type")
        Dim expectedResult As String = "@Prompt('Element Busy Hour Type:','"
        Assert.IsTrue(actualResult = expectedResult, "Prompt text should be correct for Element busy hour")
    End Sub

    ' Test getBHPromptText for object busy hour measurement type:
    <Test()> _
    Public Sub getBHPromptTextForObjectBHTest()
        ' Set up mocks:
        setupMocks()

        Dim connMock As System.Data.Odbc.OdbcConnection
        Dim dbCommandMock As System.Data.Odbc.OdbcCommand
        Dim dbReaderMock As System.Data.Odbc.OdbcDataReader

        Dim rankMT As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        rankMT.ExtendedUniverse = "a"
        rankMT.TypeName = "DC_E_MGW_AAL2APBH"
        rankMT.CounterKeys = testCounterKeys
        rankMT.ElementBusyHours = False
        rankMT.ObjectBusyHours = "TEST_BUSY_HOUR"

        Dim actualResult As String = boConditionsTPIde.getBHPromptText(rankMT, "Busy Hour Type")
        Dim expectedResult As String = "@Prompt('TEST_BUSY_HOUR Busy Hour Type:','"
        Assert.IsTrue(actualResult = expectedResult, "Prompt text should be correct for object busy hour")
    End Sub

    ' Test getBHPromptText for measurement type that is not a rank busy hour type.
    ' The original prompt text should be used.
    <Test()> _
    Public Sub getBHPromptTextNoBusyHourTest()
        ' Set up mocks:
        setupMocks()

        Dim connMock As System.Data.Odbc.OdbcConnection
        Dim dbCommandMock As System.Data.Odbc.OdbcCommand
        Dim dbReaderMock As System.Data.Odbc.OdbcDataReader

        Dim testMT As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        testMT.ExtendedUniverse = "a"
        testMT.TypeName = "DC_E_MGW_AAL2APBH"
        testMT.CounterKeys = testCounterKeys
        testMT.ElementBusyHours = False
        testMT.ObjectBusyHours = ""

        Dim actualResult As String = boConditionsTPIde.getBHPromptText(testMT, "Busy Hour Type")
        Dim expectedResult As String = "@Prompt('Busy Hour Type:','"
        Assert.IsTrue(actualResult = expectedResult, "getBHPromptText() should handle non busy hour measurement type correctly")
    End Sub

    Private Function setupDummyMeasurementTypes() As MeasurementTypesTPIde
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

    ''
    ' Test implementation of BOConditionsTPIde.
    ' Returns a mock version of TPUtilitiesTPIde.
    ''
    Private Class BOConditionsTPIdeForTest
        Inherits BOConditionsTPIde

        Public Sub New(ByVal proxy As IUniverseProxy, ByVal databaseProxy As DBProxy)
            MyBase.New(proxy)
            Me.databaseProxy = databaseProxy
        End Sub

        Public tpUtilitiesTPIde As ITPUtilitiesTPIde
        Public databaseProxy As DBProxy
        Private m_testClass As Designer.IClass
        Private m_testCondition As Designer.PredefinedCondition
        Private m_testObject As Designer.IObject

        ' Set test value for TPUtilities:
        Public Property TestTPUtilities() As ITPUtilitiesTPIde
            Get
                Return tpUtilitiesTPIde
            End Get

            Set(ByVal value As ITPUtilitiesTPIde)
                tpUtilitiesTPIde = value
            End Set
        End Property

        ' Override function to create 
        Protected Overrides Function createTPUtilities() As ITPUtilitiesTPIde
            Return tpUtilitiesTPIde
        End Function

        Protected Overrides Function createDatabaseProxy() As DBProxy
            Return databaseProxy
        End Function

        Protected Overrides Function setupCondition(ByVal UniverseNameExtension As String, ByVal mts As MeasurementTypesTPIde, ByVal tp_name As String, _
                                           ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean) As Boolean
            Return True
        End Function

    End Class

End Class
