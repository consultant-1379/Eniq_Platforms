Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Collections

<TestFixture()> _
Public Class TPUtilitiesTPIdeTest

    Private tpUtils As TPUtilitiesTPIde
    Private mocks As NMock2.Mockery

    <SetUp()> _
    Public Sub SetUp()
        tpUtils = New TPUtilitiesTPIde()
        mocks = New NMock2.Mockery()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        tpUtils = Nothing
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try
    End Sub

    <Test()> _
    Public Sub getMeasurementTypeByNameTest()
        Dim measTypes As MeasurementTypesTPIde
        measTypes = getTestMeasurementTypes()

        Dim mTypeRetured As MeasurementTypesTPIde.MeasurementType = tpUtils.getMeasurementTypeByName("incorrect name", measTypes)
        Assert.IsNull(mTypeRetured, "Incorrect measurement type name should return Nothing")

        mTypeRetured = tpUtils.getMeasurementTypeByName("Measurement Type 1", measTypes)
        Assert.IsTrue(mTypeRetured.TypeName = "Measurement Type 1", "Correct measurement type name should return a value")

        mTypeRetured = tpUtils.getMeasurementTypeByName("Measurement Type 2", measTypes)
        Assert.IsTrue(mTypeRetured.TypeName = "Measurement Type 2", "Correct measurement type name should return a value")

        mTypeRetured = tpUtils.getMeasurementTypeByName("Measurement Type 3", measTypes)
        Assert.IsTrue(mTypeRetured.TypeName = "Measurement Type 3", "Correct measurement type name should return a value")

        mTypeRetured = tpUtils.getMeasurementTypeByName("Measurement Type 4", measTypes)
        Assert.IsTrue(mTypeRetured.TypeName = "Measurement Type 4", "Correct measurement type name should return a value")
    End Sub

    <Test()> _
    Public Sub getMeasurementTypeByNameInvalidMeasurementTypesTest()
        ' Tests the case where the list of measurements types has invalid elements (measurement types with null values):
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.TypeName = Nothing
        measTypeA.ExtendedUniverse = Nothing

        Dim fullListOfMTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMTypes.AddItem(measTypeA)

        Dim mTypeRetured As MeasurementTypesTPIde.MeasurementType = tpUtils.getMeasurementTypeByName("Measurement Type 1", fullListOfMTypes)
        Assert.IsNull(mTypeRetured, "Null arguments should return Nothing")
    End Sub

    <Test()> _
    Public Sub getMeasurementTypeByNameNullArgsTest()
        Dim measTypes As MeasurementTypesTPIde
        measTypes = getTestMeasurementTypes()

        Dim mTypeRetured As MeasurementTypesTPIde.MeasurementType = tpUtils.getMeasurementTypeByName(Nothing, Nothing)
        Assert.IsNull(mTypeRetured, "Null arguments should return Nothing")
    End Sub

    <Test()> _
    Public Sub isVectorRangePresentTest()

        Dim testList As New ArrayList
        testList.Add("test1")
        testList.Add("test2")

        Dim mockDbProxy As DBProxy
        mockDbProxy = mocks.NewMock(Of DBProxy)()

        Dim dummyTpConn As New System.Data.Odbc.OdbcConnection
        Dim instance As New TPUtilitiesTPIde

        instance.DataBaseProxy = mockDbProxy

        Expect.Once.On(mockDbProxy).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Once.On(mockDbProxy).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(testList))
        Dim result As Boolean
        result = instance.isVectorRangePresent("measTypeId", "dataName", dummyTpConn)
        Assert.IsTrue(result, "Expected True")

        testList.Clear()
        Expect.Once.On(mockDbProxy).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Once.On(mockDbProxy).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(testList))
        result = instance.isVectorRangePresent("measTypeId", "dataName", dummyTpConn)
        Assert.IsFalse(result, "Expected false")
    End Sub

    <Test()> _
    Public Sub isVectorRangePresentNullArgumentsTest()

        Dim testList As New ArrayList
        testList.Add("test1")
        testList.Add("test2")

        Dim mockDbProxy As DBProxy
        mockDbProxy = mocks.NewMock(Of DBProxy)()

        Dim dummyTpConn As New System.Data.Odbc.OdbcConnection
        Dim instance As New TPUtilitiesTPIde

        instance.DataBaseProxy = mockDbProxy
        Expect.Once.On(mockDbProxy).Method("setupDatabaseReader").WithAnyArguments()
        Expect.Once.On(mockDbProxy).Method("readSingleColumnFromDB").WithAnyArguments().Will(NMock2.Return.Value(Nothing))

        Dim result As Boolean
        result = instance.isVectorRangePresent(Nothing, Nothing, dummyTpConn)

        Assert.IsFalse(result, "Expected True")
    End Sub


    ''
    'Sets up a test list of measurement types.
    '@returns MeasurementTypesTPIde A new MeasurementTypesTPIde object with MeasurementTypes.
    Private Function getTestMeasurementTypes() As MeasurementTypesTPIde
        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.TypeName = "Measurement Type 1"
        measTypeA.ExtendedUniverse = "a"

        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.TypeName = "Measurement Type 2"
        measTypeB.ExtendedUniverse = "b"

        Dim measTypeALL As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeALL.TypeName = "Measurement Type 3"
        measTypeALL.ExtendedUniverse = "all"

        Dim measTypeEMPTY As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeEMPTY.TypeName = "Measurement Type 4"
        measTypeEMPTY.ExtendedUniverse = ""

        Dim fullListOfMTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMTypes.AddItem(measTypeA)
        fullListOfMTypes.AddItem(measTypeB)
        fullListOfMTypes.AddItem(measTypeALL)
        fullListOfMTypes.AddItem(measTypeEMPTY)
        Return fullListOfMTypes
    End Function

    ''
    'A test instance of UniverseFunctionsTPIde. 
    'This test class overrides displayMessageBox() so that it is not displayed when the unit tests are run.
    Private Class TPUtilitiesTPIdeForTest
        Inherits TPUtilitiesTPIde

        ' The user's choice when they click Yes or No for the message box.
        Private m_usersChoice As MsgBoxResult
        ' Universe
        Private m_Universe As Designer.IUniverse
        ' DesignerApp
        Private m_DesignerApp As Designer.IApplication

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

        ' Property method to get and set the m_usersChoice variable.
        ' Will be used by tests to set up 
        Public Property Universe() As Designer.IUniverse
            Get
                Universe = m_Universe
            End Get

            Set(ByVal universe As Designer.IUniverse)
                m_Universe = universe
            End Set
        End Property

        ' Property method to get and set the m_usersChoice variable.
        ' Will be used by tests to set up 
        Public Property DesignerApp() As Designer.IApplication
            Get
                DesignerApp = m_DesignerApp
            End Get

            Set(ByVal designerApp As Designer.IApplication)
                m_DesignerApp = designerApp
            End Set
        End Property

        ' Overridden version of displayMessageBox() to return m_usersChoice.
        ' Avoids displaying the message box when tests are run.
        Public Overrides Function displayMessageBox(ByVal message As String, ByVal msgBoxStyle As MsgBoxStyle, _
                                                       ByVal msgBoxTitle As String) As MsgBoxResult
            Return m_usersChoice
        End Function

        Protected Overrides Function doUniverseOpen(ByRef DesignerApp As Designer.IApplication) As Designer.IUniverse
            Return m_Universe
        End Function

        Protected Overrides Function createDesignerApp() As Designer.IApplication
            Return m_DesignerApp
        End Function
    End Class

    ' Positive case:
    Private Sub expectCheckUniverseName(ByRef mockUniverse As Designer.IUniverse, ByVal expectedName As String)
        Expect.Exactly(3).On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value(expectedName))
        Expect.Exactly(2).On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value(expectedName))
    End Sub

    '' Tests the situation where the universe name is correct.
    '' checkUniverseName should return True.
    <Test()> _
    Public Sub checkUniverseNameNameCorrectTest()
        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()

        'tests situation where short names doesn't match, but universe name does:
        Dim shortName As String = "DCE1"
        Dim expectedName As String = "Test universe name"
        Dim expectedExtension As String = "Extension"
        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        expectCheckUniverseName(mockUniverse, expectedName)

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, shortName, expectedName, expectedExtension)
        Assert.IsTrue(nameIsCorrect, "checkUniverseName() should return True if universe name is correct")
    End Sub

    '' Tests the situation where the universe's name is not the one we expect.
    '' User(clicks) 'Yes' to say the name is actually ok.
    '' Function should return True.
    <Test()> _
    Public Sub checkUniverseNameIncorrectUserClicksYesTest()

        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        univFunctionsTPIde.UsersChoice = MsgBoxResult.Yes

        Dim shortName As String = "DCE1"
        Dim nameOfOpenedUniverse As String = "Test universe name"
        Dim expectedName As String = "Test universe name"
        Dim expectedExtension As String = "Extension"

        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Expect.Exactly(5).On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value("A different universe name"))
        Expect.Exactly(4).On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value("A different universe name"))

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, shortName, expectedName, expectedExtension)
        Assert.IsTrue(nameIsCorrect, "checkUniverseName() should return True if universe name is correct")
    End Sub

    '' Tests the situation where the universe's name is not the one we expect.
    '' User(clicks) 'No' to confirm that it's incorrect.
    '' Function should return False.
    <Test()> _
    Public Sub checkUniverseNameIncorrectUserClicksNoTest()

        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        univFunctionsTPIde.UsersChoice = MsgBoxResult.No

        Dim shortName As String = "DCE1"
        Dim nameOfOpenedUniverse As String = "Test universe name"
        Dim expectedName As String = "Test universe name"
        Dim expectedExtension As String = "Extension"

        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Expect.Exactly(5).On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value("A different universe name"))
        Expect.Exactly(4).On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value("A different universe name"))

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, shortName, expectedName, expectedExtension)
        Assert.IsFalse(nameIsCorrect, "checkUniverseName() should return False if universe name is incorrect")
    End Sub

    '' Tests the situation where the universe's name is not defined.
    '' User(clicks) 'Yes' to say the name is actually ok.
    '' Function should return True.
    <Test()> _
    Public Sub checkUniverseNameNameEmptyUserClicksYesTest()
        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        univFunctionsTPIde.UsersChoice = MsgBoxResult.Yes

        Dim shortName As String = ""
        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Expect.Once.On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value(""))
        Expect.Once.On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value(""))

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, shortName, "UniverseName", "Extension")
        Assert.IsTrue(nameIsCorrect, "checkUniverseName() should return True if universe name is empty but user clicks 'Yes'")
    End Sub

    '' Tests the situation where the universe's name is not defined.
    '' User(clicks) 'No' to confirm that it's incorrect.
    '' Function should return False.
    <Test()> _
    Public Sub checkUniverseNameNameEmptyUserClicksNoTest()
        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        univFunctionsTPIde.UsersChoice = MsgBoxResult.No

        Dim shortName As String = ""
        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Expect.Once.On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value(""))
        Expect.Once.On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value(""))

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, shortName, "UniverseName", "Extension")
        Assert.IsFalse(nameIsCorrect, "checkUniverseName() should return False if universe name is empty but user clicks 'No'")
    End Sub

    '' Test for when the universe name is set to the short name.
    '' checkUniverseName() should return True if the universe name matches the short name.
    <Test()> _
    Public Sub checkUniverseNameShortNameTest()
        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        Dim shortName As String = "DCE1"

        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Expect.Exactly(2).On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value(shortName))
        Expect.Once.On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value(shortName))

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, shortName, "UniverseName", "Extension")
        Assert.IsTrue(nameIsCorrect, "checkUniverseName() should return True if universe name matches the short name")
    End Sub

    '' Test for when null arguments are passed to the function.
    '' checkUniverseName should return false when the name comparison fails. Assumes the user will press 'No' so they can open another universe.
    <Test()> _
    Public Sub checkUniverseNameExceptionTest()
        Dim univFunctionsTPIde As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        univFunctionsTPIde.UsersChoice = MsgBoxResult.No

        Dim shortName As String = "DCE1"
        Dim nameOfOpenedUniverse As String = "Test universe name"
        Dim expectedName As String = "Test universe name"
        Dim expectedExtension As String = "Extension"

        Dim mockUniverse As Designer.IUniverse
        mockUniverse = mocks.NewMock(Of Designer.IUniverse)()

        Expect.AtLeastOnce.On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value(Nothing))
        Expect.AtLeastOnce.On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value(Nothing))

        Dim nameIsCorrect As Boolean
        nameIsCorrect = univFunctionsTPIde.checkUniverseName(mockUniverse, Nothing, Nothing, Nothing)
        Assert.IsFalse(nameIsCorrect, "checkUniverseName() should return False if an exception is thrown.")
    End Sub

    '================================================================================
    <Test()> _
    Public Sub promptToOpenUniverseTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        Dim mockUniverse As Designer.IUniverse = mocks.NewMock(Of Designer.IUniverse)()
        tpUtilities.Universe = mockUniverse
        tpUtilities.UsersChoice = MsgBoxResult.Yes

        ' The name of the mock universe:
        Dim nameOfOpenedUniverse As String = "UniverseName"
        expectCheckUniverseName(mockUniverse, nameOfOpenedUniverse)

        Expect.Exactly(2).On(mockDesignerApp).SetProperty("Visible").To(False)
        Expect.Exactly(2).On(mockDesignerApp).SetProperty("Interactive").To(False)

        Dim newUniverse As Designer.IUniverse = tpUtilities.promptToOpenUniverse("Standard", "a", "XI", mockDesignerApp, "UniverseName", "DCE1", "folder")
        Assert.NotNull(newUniverse, "Universe opened should not be null")
        Assert.AreEqual(mockUniverse, newUniverse)
    End Sub

    <Test()> _
    Public Sub promptToOpenUniverseWrongNameTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        Dim mockUniverse As Designer.IUniverse = mocks.NewMock(Of Designer.IUniverse)()
        tpUtilities.Universe = mockUniverse
        tpUtilities.UsersChoice = MsgBoxResult.Yes

        ' The name of the mock universe has the wrong name:
        Dim nameOfOpenedUniverse As String = "WrongName"
        Expect.Exactly(5).On(mockUniverse).GetProperty("Name").Will(NMock2.Return.Value(nameOfOpenedUniverse))
        Expect.Exactly(4).On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value(nameOfOpenedUniverse))

        Expect.Exactly(2).On(mockDesignerApp).SetProperty("Visible").To(False)
        Expect.Exactly(2).On(mockDesignerApp).SetProperty("Interactive").To(False)

        Dim newUniverse As Designer.IUniverse = tpUtilities.promptToOpenUniverse("Standard", "a", "XI", mockDesignerApp, "UniverseName", "DCE1", "Folder")
        Assert.NotNull(newUniverse, "Universe opened should not be null")
        Assert.AreEqual(mockUniverse, newUniverse)
    End Sub

    <Test()> _
    Public Sub setupDesignerAppTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        tpUtilities.DesignerApp = mockDesignerApp

        ' Set up expectations:
        Expect.Once.On(mockDesignerApp).Method("GetInstallDirectory").With(Designer.DsDirectoryID.dsDesignerDirectory)
        Expect.Once.On(mockDesignerApp).GetProperty("Version").Will(NMock2.Return.Value("test version"))
        Expect.Once.On(mockDesignerApp).Method("Logon").With("user", "password", "atrcx886vm4:6400", "Enterprise")

        Expect.Exactly(1).On(mockDesignerApp).SetProperty("Visible").To(False)

        Dim designerApp As Designer.IApplication = tpUtilities.setupDesignerApp("XI", "user", "password", "atrcx886vm4:6400", "Enterprise")
        Assert.NotNull(designerApp, "DesignerApp should not be null after it is set up")
    End Sub

    ' Removing this test due to a problem in nant with ExpectedException
    ' <Test(), ExpectedException(GetType(System.Exception))> _
    Public Sub setupDesignerAppTestInvalidVersionTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        tpUtilities.DesignerApp = mockDesignerApp

        ' Set up expectations:
        Expect.Once.On(mockDesignerApp).SetProperty("Visible").To(False)

        Dim designerApp As Designer.IApplication = tpUtilities.setupDesignerApp("Invalid Version", "user", "password", "atrcx886vm4:6400", "Enterprise")
        Assert.NotNull(designerApp, "DesignerApp should not be null after it is set up")
    End Sub

    <Test()> _
    Public Sub setupDesignerAppTestErrorTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        tpUtilities.DesignerApp = mockDesignerApp

        ' Set up expectations:
        Expect.Once.On(mockDesignerApp).SetProperty("Visible").To(False)
        Expect.Once.On(mockDesignerApp).Method("GetInstallDirectory").With(Designer.DsDirectoryID.dsDesignerDirectory)
        Expect.Once.On(mockDesignerApp).GetProperty("Version").Will(NMock2.Return.Value("test version"))
        Expect.Once.On(mockDesignerApp).Method("Logon").With("user", "password", "atrcx886vm4:6400", _
                                                             "Enterprise").Will(NMock2.Throw.Exception(New Exception("Test exception")))

        ' Expect a call to Designer.LogonDialog() because the user will be propmted to open the universe manually:
        Expect.Once.On(mockDesignerApp).Method("LogonDialog")

        Dim designerApp As Designer.IApplication = tpUtilities.setupDesignerApp("XI", "user", "password", "atrcx886vm4:6400", "Enterprise")
        Assert.NotNull(designerApp, "DesignerApp should not be null after it is set up")
    End Sub

    ' Removing this test due to a problem in nant with ExpectedException
    ' <Test()> _
    ' <ExpectedException(GetType(System.Exception))> _
    Public Sub setupDesignerAppTestManualErrorTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        tpUtilities.DesignerApp = mockDesignerApp

        ' Set up expectations:
        Expect.Once.On(mockDesignerApp).SetProperty("Visible").To(False)
        Expect.Once.On(mockDesignerApp).Method("GetInstallDirectory").With(Designer.DsDirectoryID.dsDesignerDirectory)
        Expect.Once.On(mockDesignerApp).GetProperty("Version").Will(NMock2.Return.Value("test version"))
        Expect.Once.On(mockDesignerApp).Method("Logon").With("user", "password", "atrcx886vm4:6400", _
                                                             "Enterprise").Will(NMock2.Throw.Exception(New Exception("Test exception")))

        ' Expect a call to Designer.LogonDialog(), this will also throw an exception:
        Expect.Once.On(mockDesignerApp).Method("LogonDialog").Will(NMock2.Throw.Exception(New Exception("Test exception")))
        Expect.Once.On(mockDesignerApp).Method("Quit")
        Dim designerApp As Designer.IApplication = tpUtilities.setupDesignerApp("XI", "user", "password", "atrcx886vm4:6400", "Enterprise")
        Assert.NotNull(designerApp, "DesignerApp should not be null after it is set up")
    End Sub

End Class
