Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Collections

<TestFixture()> _
Public Class BOObjectsTPIdeTest

    Private mocks As NMock2.Mockery

    ' Test instance:
    Dim boObjectsTPIde As BOObjectsTPIde

    Dim tpUtilsMock As ITPUtilitiesTPIde
    Dim universeProxyMock As IUniverseProxy
    Dim databaseProxyMock As DBProxy
    Dim classMock As IClass
    Dim objectMock As IObject
    Dim tablesMock As ITables

    Dim mockClass As Designer.IClass
    Dim mockObjects As Designer.Objects
    Dim mockObject As Designer.Object

    <SetUp()> _
    Public Sub SetUp()
        mocks = New NMock2.Mockery()

        tpUtilsMock = mocks.NewMock(Of ITPUtilitiesTPIde)()
        universeProxyMock = mocks.NewMock(Of IUniverseProxy)()
        databaseProxyMock = mocks.NewMock(Of DBProxy)()
        classMock = mocks.NewMock(Of IClass)()
        objectMock = mocks.NewMock(Of IObject)()
        tablesMock = mocks.NewMock(Of ITables)()

        mockClass = mocks.NewMock(Of Designer.IClass)()
        mockObjects = mocks.NewMock(Of Designer.Objects)()
        mockObject = mocks.NewMock(Of Designer.Object)()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try

        boObjectsTPIde = Nothing
        tpUtilsMock = Nothing
        universeProxyMock = Nothing
        databaseProxyMock = Nothing
        classMock = Nothing
        objectMock = Nothing        
        UniverseFunctionsTPIde.updatedObjects = ""
    End Sub

    ''
    ' Tests adding a "normal" object, not substituting any strings.
    ' Very basic test to check that adding an object works.
    <Test()> _
    Public Sub getObjectsFromDatabaseTest()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "className", "objectName", "select")

        ' Expect adding the object to the unvierse. We will only add a single object here:
        expectAddObjectToUniverse(classMock, objectMock, databaseProxyMock, "className", "objectName")

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "updatedObjects should have the value: 'className/objectName;'")
    End Sub

    ''
    ' Database reader cannot be set up. 
    ' getObjectsFromDatabase() should return false.
    <Test()> _
    Public Sub getObjectsFromDatabase_DatabaseReaderError_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        ' expectAddObjects(databaseProxyMock, dbCommand, 1, "className", "objectName", "select")

        Expect.Once.On(databaseProxyMock).Method("setupDatabaseReader").WithAnyArguments() _
            .Will(NMock2.Throw.Exception(New System.Exception("Error setting up database reader.")))

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = False, "If there is an error setting up the database reader, getObjectsFromDatabase() should return false.")
    End Sub

    ''
    ' No data read for a tech pack.
    ' getObjectsFromDatabase() should log the error but finish normally and return True.
    <Test()> _
    Public Sub getObjectsFromDatabase_NoDataReadForTechPack_Test()
        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.Once.On(databaseProxyMock).Method("setupDatabaseReader").WithAnyArguments()

        Expect.Once.On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(True))

        'Getting the first value from the database will return an empty string:
        Expect.Once.On(databaseProxyMock).Method("getValue").With(0).Will(NMock2.Return.Value(""))

        ' Database connection should be closed:
        Expect.Once.On(databaseProxyMock).Method("closeDatabase")

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "If no data is read for a tech pack, getObjectsFromDatabase() should return true.")
    End Sub

    ''
    ' Tests situation where an object is added, but the universe class doesn't exist.    
    <Test()> _
    Public Sub getObjectsFromDatabase_NoUniverseClass_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "className", "objectName", "select")

        ' Getting the class from the universe will return Nothing here:
        Expect.Once.On(universeProxyMock).Method("getClass").With("className").Will(NMock2.Return.Value(Nothing))

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "If no class is found for an object, getObjectsFromDatabase() should complete normally")
    End Sub

    ''
    ' Tests situation where an object is added, but formatting the object fails.
    <Test()> _
    Public Sub getObjectsFromDatabase_ErrorFormattingObject_Test()
        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "className", "objectName", "select")

        Expect.Once.On(universeProxyMock).Method("getClass").With("className").Will(NMock2.Return.Value(classMock))
        Expect.Once.On(universeProxyMock).Method("getObject").With(classMock, "objectName").Will(NMock2.Return.Value(objectMock))

        Expect.Once.On(universeProxyMock).Method("formatObject").With((objectMock)).Will(NMock2.Throw.Exception(New System.Exception("Error formatting object.")))

        ' Stub out other calls to the universe class and object completely:
        Stub.On(classMock)
        Stub.On(objectMock)

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "If there is an error formatting an object, getObjectsFromDatabase() should complete normally")
    End Sub

    ''
    ' Tests adding a "normal" object, but database error occurs.
    ' getObjectsFromDatabase() should catch the exception and continue with the next object.
    <Test()> _
    Public Sub getObjectsFromDatabase_DatabaseErrorTest()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.Once.On(databaseProxyMock).Method("setupDatabaseReader").WithAnyArguments()

        'Expect reading from the database for the objects:
        Expect.Once.On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(True))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(0).Will(NMock2.Return.Value("className"))

        ' Throw an error for one of the columns:
        Expect.Once.On(databaseProxyMock).Method("getValue").With(1).Will(NMock2.Throw.Exception(New System.Exception("Error reading from database!")))

        ' Adding the objects should continue if there is a database error for one object.
        ' Expect read() to be called another time:
        Expect.Once.On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(False))

        ' When while loop ends, database connection is closed:
        Expect.Once.On(databaseProxyMock).Method("closeDatabase")

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "getObjectsFromDatabase should return True if reading from database fails for one object")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "", "updatedObjects should have an empty value if reading from database fails")
    End Sub

    ''
    ' Tests adding a "normal" object, but an error occurs when you add the object to the universe.
    ' getObjectsFromDatabase() should catch the exception and continue with the next object.
    <Test()> _
    Public Sub getObjectsFromDatabase_UniverseErrorTest()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        ' Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "className", "objectName", "select")

        ' Adding a universe object throws an exception:
        Expect.Once.On(universeProxyMock).Method("getClass").With("className").Will(NMock2.Throw.Exception(New System.Exception("Error adding to the universe!")))

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "getObjectsFromDatabase should return True if writing to the universe fails for one object")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "className/objectName;", _
                      "updatedObjects should have a value 'className/objectName;' if writing to the universe fails")
    End Sub

    ''
    ' Tests adding a "normal" object, substituting (TPNAME) in the object name.
    ' Example: DC_E_SGSN is replaced by E_SGSN.
    <Test()> _
    Public Sub getObjectsFromDatabase_TPNAME_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        Dim mts As MeasurementTypesTPIde = setupDummyMeasurementTypes()
        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = True
        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "className", "objectName(TPNAME)", "select")

        ' Expect adding the object to the unvierse. We will only add a single object here:
        expectAddObjectToUniverse(classMock, objectMock, databaseProxyMock, "className", "objectNameE_SGSN")

        Dim returnValue As Boolean = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "updatedObjects should return True")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "className/objectNameE_SGSN;", _
                      "updatedObjects should have the value: 'className/objectNameE_SGSN;")
    End Sub

    ''
    ' Tests adding an object from the base tech pack.
    ' "Busy Hour/(BHObject) Busy Hour Type" with select statement: DC.(DIM_RANKMT)_BHTYPE.DESCRIPTION,
    ' for an object busy hour.
    <Test()> _
    Public Sub getObjectsFromDatabase_ObjectBH_DIM_Table_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.TypeName = "DC_E_SGSN_SGSN"
        measTypeA.RankTable = True
        ' NE busy hour for SGSN will have 'NE' as the value:
        measTypeA.ObjectBusyHours = "NE"
        Dim mts As MeasurementTypesTPIde = New MeasurementTypesTPIde
        mts.AddItem(measTypeA)

        Dim ObjectBHSupport As Boolean = True
        Dim ElementBHSupport As Boolean = False

        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "Busy Hour", "(BHObject) Busy Hour Type", "DC.(DIM_RANKMT)_BHTYPE.DESCRIPTION")

        ' Expect adding the object to the universe. We will only add a single object here:
        expectAddObjectToUniverse(classMock, objectMock, databaseProxyMock, "Busy Hour", "NE Busy Hour Type")

        Dim returnValue As String = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "updatedObjects should return True")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "Busy Hour/NE Busy Hour Type;", _
                      "updatedObjects should have the value: 'Busy Hour/NE Busy Hour Type;'")
    End Sub

    ''
    ' Tests adding an object from the base tech pack.
    ' "Busy Hour/(BHObject) Busy Hour Type" with select statement: DC.(DIM_RANKMT)_BHTYPE.DESCRIPTION,
    ' for an element busy hour.
    <Test()> _
    Public Sub getObjectsFromDatabase_ElementBH_DIM_Table_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.TypeName = "DC_E_SGSN_SGSN"
        measTypeA.RankTable = True
        measTypeA.ObjectBusyHours = ""
        measTypeA.ElementBusyHours = True

        Dim mts As MeasurementTypesTPIde = New MeasurementTypesTPIde
        mts = New MeasurementTypesTPIde
        mts.AddItem(measTypeA)

        Dim ObjectBHSupport As Boolean = False
        Dim ElementBHSupport As Boolean = True

        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "Busy Hour", "(BHObject) Busy Hour Type", "DC.(DIM_RANKMT)_BHTYPE.DESCRIPTION")

        ' Expect adding the object to the unvierse. We will only add a single object here:
        expectAddObjectToUniverse(classMock, objectMock, databaseProxyMock, "Busy Hour", "Element Busy Hour Type")

        Dim returnValue As String = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "updatedObjects should return True")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "Busy Hour/Element Busy Hour Type;", _
                      "updatedObjects should have the value: 'Busy Hour/Element Busy Hour Type;'")
    End Sub

    '
    ' Element (Busy Hour)/Element Name to DC.(ELEMENTRANKMT).ELEMENT_NAME
    <Test()> _
    Public Sub getObjectsFromDatabase_ElementName_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.TypeName = "DC_E_SGSN_SGSN"
        measTypeA.RankTable = True
        measTypeA.ObjectBusyHours = ""
        measTypeA.ElementBusyHours = True

        Dim mts As MeasurementTypesTPIde = New MeasurementTypesTPIde
        mts = New MeasurementTypesTPIde
        mts.AddItem(measTypeA)

        Dim ObjectBHSupport As Boolean = False
        Dim ElementBHSupport As Boolean = True

        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "Element (Busy Hour)", "Element Name", "DC.(ELEMENTRANKMT).ELEMENT_NAME")

        ' Expect adding the object to the unvierse. We will only add a single object here:
        expectAddObjectToUniverse(classMock, objectMock, databaseProxyMock, "Element (Busy Hour)", "Element Name")

        Dim returnValue As String = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "updatedObjects should return True")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "Element (Busy Hour)/Element Name;", _
                      "updatedObjects should have the value: 'Element (Busy Hour)/Element Name;'")
    End Sub

    '
    ' Element (Busy Hour)/Element Name to DC.(ELEMENTRANKMT).ELEMENT_NAME
    <Test()> _
    Public Sub getObjectsFromDatabase_TPRELEASE_Test()

        Dim tp_name As String = "DC_E_SGSN"
        Dim tp_release As String = "release"

        Dim connMock As System.Data.Odbc.OdbcConnection = New System.Data.Odbc.OdbcConnection()
        Dim dbCommand As System.Data.Odbc.OdbcCommand = New System.Data.Odbc.OdbcCommand
        Dim dbReader As System.Data.Odbc.OdbcDataReader = Nothing

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.TypeName = "DC_E_SGSN_SGSN"
        measTypeA.RankTable = True
        measTypeA.ObjectBusyHours = ""
        measTypeA.ElementBusyHours = True

        Dim mts As MeasurementTypesTPIde = New MeasurementTypesTPIde
        mts = New MeasurementTypesTPIde
        mts.AddItem(measTypeA)

        Dim ObjectBHSupport As Boolean = False
        Dim ElementBHSupport As Boolean = True

        Dim UniverseNameExtension As String = "a"
        Dim TechPackTPIde As String = "DC_E_SGSN:((109))"
        Dim addToUniverse As Boolean = True

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        'Expect reading from the database for the objects:
        expectAddObjects(databaseProxyMock, dbCommand, 1, "General", "TP Version", "(TPRELEASE)")

        ' Expect adding the object to the unvierse. We will only add a single object here:
        expectAddObjectToUniverse(classMock, objectMock, databaseProxyMock, "General", "TP Version")

        ' Expect special case for General/TP Version object:
        Expect.Once.On(universeProxyMock).Method("addToObjectsTables").With(objectMock, "DC.DIM_DATE")

        Dim returnValue As String = boObjectsTPIde.getObjectsFromDatabase(tp_name, tp_release, connMock, dbCommand, dbReader, _
                               mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, addToUniverse)

        Assert.IsTrue(returnValue = True, "updatedObjects should return True")
        Assert.IsTrue(UniverseFunctionsTPIde.updatedObjects = "General/TP Version;", _
                      "updatedObjects should have the value: 'General/TP Version;'")
    End Sub

    Private Sub expectAddObjectToUniverse(ByVal classMock As IClass, ByVal objectMock As IObject, ByVal databaseProxyMock As DBProxy, _
                                          ByVal className As String, ByVal objectName As String)
        Expect.Once.On(universeProxyMock).Method("getClass").With(className).Will(NMock2.Return.Value(classMock))
        Expect.Once.On(universeProxyMock).Method("getObject").With(classMock, objectName).Will(NMock2.Return.Value(objectMock))

        Expect.Once.On(universeProxyMock).Method("formatObject").With((objectMock))

        ' Allow calls to the universe class and object:
        Stub.On(classMock)
        Stub.On(objectMock)
    End Sub

    ' Expect a call to addObjects()
    Private Sub expectAddObjects(ByVal databaseProxyMock As DBProxy, ByVal dbCommand As System.Data.Odbc.OdbcCommand, _
                                 ByVal numberOfObjects As Integer, ByVal className As String, ByVal objectName As String, _
                                 ByVal selectValue As String)
        ' Expectations for addObjects:
        Expect.Once.On(databaseProxyMock).Method("setupDatabaseReader").WithAnyArguments()

        'Read from the database:
        Expect.Exactly(numberOfObjects).On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(True))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(0).Will(NMock2.Return.Value(className))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(1).Will(NMock2.Return.Value("a"))

        'Read values from the database:
        Expect.Once.On(databaseProxyMock).Method("getValue").With(0).Will(NMock2.Return.Value(className))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(1).Will(NMock2.Return.Value("a"))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(2).Will(NMock2.Return.Value(objectName))

        Expect.Once.On(databaseProxyMock).Method("isDBNull").With(3).Will(NMock2.Return.Value(False))

        ' Description is read in from 4 different fields:
        Expect.Once.On(databaseProxyMock).Method("getString").With(3).Will(NMock2.Return.Value("description1"))
        Expect.Once.On(databaseProxyMock).Method("getString").With(4).Will(NMock2.Return.Value("description2"))
        Expect.Once.On(databaseProxyMock).Method("getString").With(5).Will(NMock2.Return.Value("description3"))
        Expect.Once.On(databaseProxyMock).Method("getString").With(6).Will(NMock2.Return.Value("description4"))

        Expect.Once.On(databaseProxyMock).Method("getValue").With(7).Will(NMock2.Return.Value("objectType"))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(8).Will(NMock2.Return.Value("qualification"))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(9).Will(NMock2.Return.Value("aggregation"))

        Expect.Once.On(databaseProxyMock).Method("isDBNull").With(10).Will(NMock2.Return.Value(False))

        ' Select statement:
        Expect.Once.On(databaseProxyMock).Method("getString").With(10).Will(NMock2.Return.Value(selectValue))
        Expect.Once.On(databaseProxyMock).Method("getString").With(11).Will(NMock2.Return.Value(""))
        Expect.Once.On(databaseProxyMock).Method("getString").With(12).Will(NMock2.Return.Value(""))
        Expect.Once.On(databaseProxyMock).Method("getString").With(13).Will(NMock2.Return.Value(""))

        Expect.Once.On(databaseProxyMock).Method("isDBNull").With(14).Will(NMock2.Return.Value(False))
        ' Where statement:
        Expect.Once.On(databaseProxyMock).Method("getString").With(14).Will(NMock2.Return.Value("where1"))
        Expect.Once.On(databaseProxyMock).Method("getString").With(15).Will(NMock2.Return.Value("where2"))
        Expect.Once.On(databaseProxyMock).Method("getString").With(16).Will(NMock2.Return.Value("where3"))
        Expect.Once.On(databaseProxyMock).Method("getString").With(17).Will(NMock2.Return.Value("where4"))

        Expect.Once.On(databaseProxyMock).Method("getValue").With(18).Will(NMock2.Return.Value(False))
        Expect.Once.On(databaseProxyMock).Method("getValue").With(19).Will(NMock2.Return.Value(False))

        ' Second call to read should return false, only one object being read:
        Expect.Once.On(databaseProxyMock).Method("read").Will(NMock2.Return.Value(False))
        Expect.Once.On(databaseProxyMock).Method("closeDatabase")
    End Sub

    Private Function setupDummyMeasurementTypes() As MeasurementTypesTPIde
        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"
        measTypeA.TypeName = "DC_E_SGSN_SGSN"
        ' measTypeA.RankTable = True

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
    Public Sub getNumberFormatWithDatascaleTest()
        Dim mockObject As Designer.IObject
        Dim datascale As String

        ' Set up test designer object:
        mockObject = mocks.NewMock(Of Designer.IObject)()
        ' Set up expectations:
        Expect.Once.On(mockObject).SetProperty("Type")
        Expect.Once.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsNumericObject))

        mockObject.Type = Designer.DsObjectType.dsNumericObject
        ' Set up datascale:
        datascale = "0"

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Dim expectedFormat As String
        expectedFormat = boObjectsTPIde.getNumberFormatWithDatascale(mockObject, datascale)
        Assert.IsTrue(expectedFormat = "0;-0;0", "Format should be '0;-0;0' if datascale is 0")

        ' Set datascale higher than 0:
        datascale = "12"
        Expect.Once.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsNumericObject))
        expectedFormat = boObjectsTPIde.getNumberFormatWithDatascale(mockObject, datascale)
        Assert.IsTrue(expectedFormat = "", "Format should be standard formatting if datascale is greater than 0")
    End Sub

    <Test()> _
    Public Sub getNumberFormatWithDatascaleInvalidArgsTest()
        Dim mockObject As Designer.IObject
        Dim datascale As String

        ' Set up test designer object:
        mockObject = mocks.NewMock(Of Designer.IObject)()
        ' Set up expectations:
        Expect.Once.On(mockObject).SetProperty("Type") ' .With(Designer.DsObjectType.dsNumericObject)
        Expect.Once.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsNumericObject))

        mockObject.Type = Designer.DsObjectType.dsNumericObject

        ' Set up datascale:
        datascale = "String not convertible to number"

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Dim format As String
        format = boObjectsTPIde.getNumberFormatWithDatascale(mockObject, datascale)
        Assert.IsTrue(format = "", "Format should be standard formatting if datascale argument has invalid value")

        ' Set up datascale:
        datascale = "-1"
        Expect.Once.On(mockObject).GetProperty("Type").Will(NMock2.Return.Value(Designer.DsObjectType.dsNumericObject))
        format = boObjectsTPIde.getNumberFormatWithDatascale(mockObject, datascale)
        Assert.IsTrue(format = "", "Format should be standard formatting if datascale argument has negative value")

        format = boObjectsTPIde.getNumberFormatWithDatascale(mockObject, Nothing)
        Assert.IsTrue(format = "", "Format should be standard formatting if datascale argument has null value")

        format = boObjectsTPIde.getNumberFormatWithDatascale(Nothing, "12")
        Assert.IsTrue(format = "", "Format should be standard formatting if Object argument has null value")

        format = boObjectsTPIde.getNumberFormatWithDatascale(Nothing, Nothing)
        Assert.IsTrue(format = "", "Format should be standard formatting if arguments have null values")
    End Sub

    ' Negative test where the object name in the universe matches one of the counter object names from the tech pack.
    ' The universe object should not be deleted in this case.
    <Test()> _
    Public Sub removeObjectTest()
        Dim UnivObjNameFromTP As String = "objectsHaveSameName"
        Dim existingUnivObj As String = "objectsHaveSameName"
        Dim invalidAggValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByNullObject

        'CountersTPIde
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = UnivObjNameFromTP
        counters.AddItem(counter)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))
        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object in the universe will have the same name as a counter in the tech pack, so don't delete it:
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value(existingUnivObj))

        ' Object should not be deleted if the universe counter name matches the counter name from the tech pack:
        Expect.Never.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' Positive test where the object name in the universe doesn't match any of the counter object names from the tech pack.
    ' e.g. An old universe counter from a previous MOM.
    ' Universe object should be removed.
    <Test()> _
    Public Sub removeOldObjectTest()
        Dim UnivObjNameFromTP As String = "techPackObject"
        Dim existingUnivObj As String = "oldUniverseObject" ' An old universe counter from a previous MOM
        Dim invalidAggValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByNullObject

        'CountersTPIde
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = UnivObjNameFromTP
        counters.AddItem(counter)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        ' Name of the universe object:
        Expect.AtLeastOnce.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value(existingUnivObj))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack.
        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack:
        Expect.AtLeastOnce.On(mockClass).GetProperty("Name").Will(NMock2.Return.Value("Test class"))
        Expect.Once.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' Test with several counters in the tech pack measurement type, one counter in universe.
    ' Universe object should be removed when not found in tech pack.
    <Test()> _
    Public Sub removeOldObject_SeveralObjectsTest()
        Dim existingUnivObj As String = "oldUniverseObject" ' An old universe counter from a previous MOM
        Dim invalidAggValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByNullObject

        ' CountersTPIde:
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = "techPackObject1"
        counters.AddItem(counter)

        Dim counter2 As New CountersTPIde.Counter
        counter2.UnivObject = "techPackObject2"
        counters.AddItem(counter2)

        Dim counter3 As New CountersTPIde.Counter
        counter3.UnivObject = "techPackObject3"
        counters.AddItem(counter3)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        ' Name of the universe object:
        Expect.AtLeastOnce.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value(existingUnivObj))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack.
        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack:
        Expect.AtLeastOnce.On(mockClass).GetProperty("Name").Will(NMock2.Return.Value("Test class"))
        Expect.Once.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' data_coverage and period_duration should not be removed.
    <Test()> _
    Public Sub removeObject_data_coverageTest()
        Dim UnivObjNameFromTP As String = "techPackObject"
        Dim existingUnivObj As String = "data_coverage" ' An old universe counter from a previous MOM
        Dim invalidAggValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByNullObject

        'CountersTPIde
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = UnivObjNameFromTP
        counters.AddItem(counter)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(2))

        ' Name of the universe object:
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("data_coverage"))
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("period_duration"))

        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should not be deleted:
        Expect.Never.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' data_coverage and period_duration should not be removed, 
    ' if there are no counters in the measurement type in the tech pack.
    <Test()> _
    Public Sub removeObject_data_coverage_period_duration_NoCountersTest()
        Dim invalidAggValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByNullObject

        ' CountersTPIde with no counters:
        Dim counters As New CountersTPIde()

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(2))

        ' Name of the universe object:
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("data_coverage"))
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("period_duration"))

        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should not be deleted:        
        Expect.Never.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' data_coverage and period_duration should not be removed, 
    ' if there are no counters in the measurement type in the tech pack.
    <Test()> _
    Public Sub removeObject_NoCountersTest()
        Dim invalidAggValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByNullObject

        ' CountersTPIde with no counters:
        Dim counters As New CountersTPIde()

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(3))

        ' Name of the universe object:
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("data_coverage"))
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("period_duration"))
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("AnotherObject"))
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value("AnotherObject2"))

        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should be deleted straight away:
        Expect.AtLeastOnce.On(mockClass).GetProperty("Name").Will(NMock2.Return.Value("Test class")) 'logging
        Expect.Once.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' Test if an object in the universe has an aggregation (like (avg) or (sum)).
    ' If it does, don't remove the object if the counter's UnivObject value + (avg) or (sum) matches it.
    ' Universe object should not be removed.
    <Test()> _
    Public Sub removeObjectValidWithAggregationTest()

        Dim UnivObjNameFromTP As String = "techPackObject"
        Dim existingUnivObj As String = "techPackObject (avg)"
        Dim aggregationValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByAvgObject

        ' CountersTPIde:
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = UnivObjNameFromTP
        counter.Aggregations = Split("SUM,AVG", ",")
        counters.AddItem(counter)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        ' Name of the universe object:
        Expect.Once.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value(existingUnivObj))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack.
        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should not be deleted if the universe counter name matches the counter name from the tech pack.
        Expect.Never.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' Test if an object in the universe has an aggregation (like (avg) or (sum)).
    ' If it does, remove the object if no counter's UnivObject value + (avg) or (sum) matches it.
    ' Universe object should be removed.
    <Test()> _
    Public Sub removeObjectInvalidWithAggregationTest()

        Dim UnivObjNameFromTP As String = "techPackObject"
        Dim existingUnivObj As String = "techPackObject2 (avg)"
        Dim aggregationValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByAvgObject

        ' CountersTPIde:
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = UnivObjNameFromTP
        counter.Aggregations = Split("SUM,AVG", ",")
        counters.AddItem(counter)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        ' Name of the universe object:
        Expect.AtLeastOnce.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value(existingUnivObj))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack.
        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should not be deleted if the universe counter name matches the counter name from the tech pack:
        Expect.AtLeastOnce.On(mockClass).GetProperty("Name").Will(NMock2.Return.Value("Test class"))
        Expect.Once.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub

    ' Test situation where there is a universe object with aggregation, and a corresponding counter is defined for it in new EBS MOM.
    ' But there are no aggregations defined in the counter.
    ' Universe object should be removed.
    <Test()> _
    Public Sub removeObjectWithNoAggsDefinedTest()

        Dim UnivObjNameFromTP As String = "techPackObject"
        Dim existingUnivObj As String = "techPackObject (avg)"
        Dim aggregationValue As Designer.DsObjectAggregate = Designer.DsObjectAggregate.dsAggregateByAvgObject

        ' CountersTPIde:
        Dim counters As New CountersTPIde()
        Dim counter As New CountersTPIde.Counter
        counter.UnivObject = UnivObjNameFromTP
        counter.Aggregations = New String() {}
        counters.AddItem(counter)

        ' Set up test instance:
        boObjectsTPIde = New BOObjectsTPIde(universeProxyMock, databaseProxyMock, tpUtilsMock)

        Expect.AtLeastOnce.On(mockClass).GetProperty("Objects").Will(NMock2.Return.Value(mockObjects))

        Expect.Once.On(mockObjects).GetProperty("Count").Will(NMock2.Return.Value(1))

        ' Name of the universe object:
        Expect.AtLeastOnce.On(mockObject).GetProperty("Name").Will(NMock2.Return.Value(existingUnivObj))

        ' Object should be deleted if the universe counter name matches the counter name from the tech pack.
        Expect.AtLeastOnce.On(mockObjects).Method("get__Item").WithAnyArguments().Will(NMock2.Return.Value(mockObject))

        ' Object should not be deleted if the universe counter name matches the counter name from the tech pack:
        Expect.AtLeastOnce.On(mockClass).GetProperty("Name").Will(NMock2.Return.Value("Test class"))
        Expect.Once.On(mockObject).Method("Delete")

        boObjectsTPIde.removeObjectsForEBS(mockClass, counters)
    End Sub
End Class
