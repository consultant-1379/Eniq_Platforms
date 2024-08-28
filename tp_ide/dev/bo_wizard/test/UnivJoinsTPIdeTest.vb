Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Collections

<TestFixture()> _
Public Class UnivJoinsTPIdeTest

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

    ' Test instance:
    Dim univJoins As UnivJoinsTPIde

    <SetUp()> _
    Public Sub SetUp()
        mocks = New NMock2.Mockery()
        univJoins = New UnivJoinsTPIde()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try

        univJoins = Nothing
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

    End Sub

    <Test()> _
    Public Sub checkExtensionTest()
        Dim checkExtension As Boolean = univJoins.checkExtension("A", "A")
        Assert.IsTrue(checkExtension, "checkExtension should return true if the extension matches the current universe")
    End Sub

    <Test()> _
    Public Sub checkExtensionNotMatchingTest()
        Dim checkExtension As Boolean = univJoins.checkExtension("A", "B")
        Assert.IsFalse(checkExtension, "checkExtension should return false if the extension doesn't match the current universe")

        checkExtension = univJoins.checkExtension("rubbish value", "B")
        Assert.IsFalse(checkExtension, "checkExtension should return false if the extension doesn't match the current universe")
    End Sub


    <Test()> _
    Public Sub checkExtensionNotMatchingSeveralValuesTest()
        Dim checkExtension As Boolean = univJoins.checkExtension("A,B,c,D", "E")
        Assert.IsFalse(checkExtension, "checkExtension should return false if the extension doesn't match the current universe")
    End Sub

    <Test()> _
    Public Sub checkExtensionTestNullValue()
        Dim checkExtension As Boolean = univJoins.checkExtension(Nothing, "A")
        Assert.IsTrue(checkExtension, "checkExtension should return true if the database value is null")
    End Sub

    <Test()> _
    Public Sub checkExtensionTestSeveralUniverses()
        Dim checkExtension As Boolean = univJoins.checkExtension("A,B,C,D", "A")
        Assert.IsTrue(checkExtension, "checkExtension should return true if one of the database extensions matches the current universe")
    End Sub

    ' Universe generator will add a join by default if the value entered by the user is in an incorrect format.
    <Test()> _
    Public Sub checkExtensionTestMalformedValue()
        Dim checkExtension As Boolean = univJoins.checkExtension(",D", "A")
        Assert.IsTrue(checkExtension, "checkExtension should return false if the database value is not in the correct format")
    End Sub

    ' Universe generator will add a join by default if no universe extensions are defined in the tech pack.
    <Test()> _
    Public Sub checkExtensionTestCurrentUniverseNotDefined()
        Dim checkExtension As Boolean = univJoins.checkExtension("A", Nothing)
        Assert.IsTrue(checkExtension, "checkExtension should return true if the current universe is not defined")
    End Sub

    <Test()> _
    Public Sub checkExtensionTestCurrentUniverseEmptyString()
        Dim checkExtension As Boolean = univJoins.checkExtension("A", "")
        Assert.IsTrue(checkExtension, "checkExtension should return true if the current universe is an empty string")
    End Sub

    <Test()> _
    Public Sub checkExtensionTestSpacesInString()
        Dim checkExtension As Boolean = univJoins.checkExtension("A    ", "A")
        Assert.IsTrue(checkExtension, "checkExtension should return true if the string entered by user has spaces")
    End Sub

    <Test()> _
    Public Sub checkExtensionTestRubbishUserValue()
        Dim checkExtension As Boolean = univJoins.checkExtension(",", "A")
        Assert.IsTrue(checkExtension, "checkExtension should return true if the string entered by user has spaces")
    End Sub

End Class

