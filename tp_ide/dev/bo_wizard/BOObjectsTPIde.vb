Option Strict Off

Imports System.Collections
Imports TPIdeUniverseWizard.CounterKeysTPIde


''
'  BOObjects class is a collection of BOObject classes
'
Public Class BOObjectsTPIde
    Implements IBOObjectsTPIde

    Private _objects As System.Collections.ArrayList = New System.Collections.ArrayList
    Public m_objectParse As Boolean
    Private tpUtilities As ITPUtilitiesTPIde
    Private universeProxy As IUniverseProxy
    Private databaseProxy As DBProxy

    ''
    ' Constructor to be used for testing. 
    ' This takes a universe and database proxy so they can be mocked out for unit testing.
    '@param universeProxy   A UniverseProxy object.
    '@param databaseProxy   A database proxy.
    '@param tpUtils         TPUtilitiesTPIde object.
    Public Sub New(ByVal universeProxy As IUniverseProxy, ByVal databaseProxy As DBProxy, ByVal tpUtils As ITPUtilitiesTPIde)
        Me.tpUtilities = tpUtils
        Me.databaseProxy = databaseProxy
        Me.universeProxy = universeProxy
    End Sub

    ''
    ' Creates a new instance of BOObjectsTPIde.
    '@param universeProxy   A UniverseProxy object.
    Public Sub New(ByVal universeProxy As IUniverseProxy)
        Me.tpUtilities = New TPUtilitiesTPIde()
        Me.databaseProxy = New DatabaseProxy()
        Me.universeProxy = universeProxy
    End Sub

    ''
    '  Gets count of BOObject classes in BOObjects class
    '
    ' @param Index Specifies the index in the BOObjects class
    ' @return Count of BOObject classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _objects Is Nothing) Then
                Return _objects.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOObject class from BOObjects class based on given index. - test
    '
    ' @param Index Specifies the index in the BOObjects class
    ' @return Reference to BOObject
    Public ReadOnly Property Item(ByVal Index As Integer) As BOObject
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_objects.Item(Index - 1), BOObject)
            End If
            Return Nothing
        End Get
    End Property

    Public Property ObjectParse() As Boolean Implements IBOObjectsTPIde.ObjectParse
        Get
            Return m_objectParse
        End Get
        Set(ByVal value As Boolean)
            m_objectParse = value
        End Set
    End Property

    ''
    '  Adds BOObject class to BOObjects class
    '
    ' @param ValueIn Specifies reference to BOObject
    Public Sub AddItem(ByVal ValueIn As BOObject)

        If (Not _objects Is Nothing) Then
            _objects.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOObject defines universe's objects.
    '
    Public Class BOObject
        Private m_ClassName As String
        Private m_OldClassName As String
        Private m_Name As String
        Private m_OldName As String
        Private m_Description As String
        Private m_Path As String
        Private m_Table As String
        Private m_Header As String
        Private m_Aggregation As String
        Private m_Level As Integer
        Private m_ElementBHRelated As Boolean
        Private m_ObjectBHRelated As Boolean
        Private m_UniverseExtension As String

        Public Property UniverseExtension()
            Get
                UniverseExtension = m_UniverseExtension
            End Get

            Set(ByVal Value)
                m_UniverseExtension = LCase(Value)
            End Set

        End Property
        ''
        ' Gets and sets value for ClassName parameter. 
        ' ClassName defines object's class name.
        '
        ' @param Value Specifies value of ClassName parameter
        ' @return Value of ClassName parameter
        Public Property ClassName()
            Get
                ClassName = m_ClassName
            End Get

            Set(ByVal Value)
                m_ClassName = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for OldClassName parameter. 
        ' OldClassName defines object's previous class name.
        '
        ' @param Value Specifies value of OldClassName parameter
        ' @return Value of OldClassName parameter
        Public Property OldClassName()
            Get
                OldClassName = m_OldClassName
            End Get

            Set(ByVal Value)
                m_OldClassName = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Level parameter. 
        ' Level defines object's class level.
        '
        ' @param Value Specifies value of Level parameter
        ' @return Value of Level parameter
        Public Property Level()
            Get
                Level = m_Level
            End Get

            Set(ByVal Value)
                m_Level = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Name parameter. 
        ' Name defines object's name.
        '
        ' @param Value Specifies value of Name parameter
        ' @return Value of Name parameter
        Public Property Name()
            Get
                Name = m_Name
            End Get

            Set(ByVal Value)
                m_Name = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for OldName parameter. 
        ' OldName defines object's previous name.
        '
        ' @param Value Specifies value of OldName parameter
        ' @return Value of OldName parameter
        Public Property OldName()
            Get
                OldName = m_OldName
            End Get

            Set(ByVal Value)
                m_OldName = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Description parameter. 
        ' Description defines description.
        '
        ' @param Value Specifies value of Description parameter
        ' @return Value of Description parameter
        Public Property Description()
            Get
                Description = m_Description
            End Get

            Set(ByVal Value)
                m_Description = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Path parameter. 
        ' Path defines object's path.
        '
        ' @param Value Specifies value of Path parameter
        ' @return Value of Path parameter
        Public Property Path()
            Get
                Path = m_Path
            End Get

            Set(ByVal Value)
                m_Path = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Table parameter. 
        ' Table defines object's table.
        '
        ' @param Value Specifies value of Table parameter
        ' @return Value of Table parameter
        Public Property Table()
            Get
                Table = m_Table
            End Get

            Set(ByVal Value)
                m_Table = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Header parameter. 
        ' Header defines object's header.
        '
        ' @param Value Specifies value of Header parameter
        ' @return Value of Header parameter
        Public Property Header()
            Get
                Header = m_Header
            End Get

            Set(ByVal Value)
                m_Header = Trim(Value)
            End Set

        End Property

        Public Property ElementBHRelated()
            Get
                ElementBHRelated = m_ElementBHRelated
            End Get

            Set(ByVal Value)
                If LCase(Trim(Value)) = "1" Then
                    m_ElementBHRelated = True
                Else
                    m_ElementBHRelated = False
                End If
            End Set

        End Property

        Public Property ObjectBHRelated()
            Get
                ObjectBHRelated = m_ObjectBHRelated
            End Get

            Set(ByVal Value)
                If LCase(Trim(Value)) = "1" Then
                    m_ObjectBHRelated = True
                Else
                    m_ObjectBHRelated = False
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for Aggregation parameter. 
        ' Aggregation defines object's aggregation.
        '
        ' @param Value Specifies value of Aggregation parameter
        ' @return Value of Aggregation parameter
        Public Property Aggregation()
            Get
                Aggregation = m_Aggregation
            End Get

            Set(ByVal Value)
                If Value = Designer.DsObjectAggregate.dsAggregateByAvgObject Then
                    m_Aggregation = "AVG"
                ElseIf Value = Designer.DsObjectAggregate.dsAggregateByCountObject Then
                    m_Aggregation = "COUNT"
                ElseIf Value = Designer.DsObjectAggregate.dsAggregateByMaxObject Then
                    m_Aggregation = "MAX"
                ElseIf Value = Designer.DsObjectAggregate.dsAggregateByMinObject Then
                    m_Aggregation = "MIN"
                ElseIf Value = Designer.DsObjectAggregate.dsAggregateBySumObject Then
                    m_Aggregation = "SUM"
                Else
                    m_Aggregation = ""
                End If
            End Set

        End Property
    End Class


    Private Function OlDbBlob2Variable(ByVal index As Integer, ByRef dbReader As System.Data.Odbc.OdbcDataReader) As String
        Dim b(32000) As Byte
        dbReader.GetBytes(index, 0, b, 0, b.Length)
        Return System.Text.Encoding.UTF8.GetString(b)
    End Function

    ''
    'Adds universe objects for the rank busy hours.
    '@param     mts     The measurement types.
    '@returns   True    True if adding the rank busy hour objects was successful.
    Public Function addBusyHourRankObjects(ByRef mts As MeasurementTypesTPIde) As Boolean Implements IBOObjectsTPIde.addBusyHourRankObjects
        Dim singleDigitFormat As String
        singleDigitFormat = "0;-0;0"

        Dim success As Boolean
        success = True

        'Get a list of the rank tables
        Dim rankTableList As New ArrayList()
        rankTableList = tpUtilities.getRankMeasurementTypes(mts)
        Dim rankMT As MeasurementTypesTPIde.MeasurementType

        'Go through the rank tables, and add  objects for each one
        Dim Count As Integer
        For Count = 0 To (rankTableList.Count - 1)
            rankMT = rankTableList.Item(Count)
            Dim rankTableName As String
            rankTableName = rankMT.TypeName & "_RANKBH"

            Try
                ' Add ordinary objects:
                addBusyHourRankObject(rankTableName, "Date", getSelectStatement(rankTableName, "DATE_ID"), _
                                    "Date for " & rankMT.TypeName, "dimension", "date", "")

                Dim dimTableName As String = Replace(rankMT.TypeName, "DC_", "DIM_", , 1)
                dimTableName = dimTableName & "_BHTYPE"
                addBusyHourRankObject(rankTableName, "Busy Hour Type", getSelectStatement(dimTableName, "BHTYPE"), _
                                      "Busy hour type for " & rankMT.TypeName, "dimension", "character", "")
                addBusyHourRankObject(rankTableName, "Busy Hour Description", getSelectStatement(dimTableName, "DESCRIPTION"), _
                                      "Busy hour type for " & rankMT.TypeName, "dimension", "character", "")

                addBusyHourRankObject(rankTableName, "Busy Hour Value", getSelectStatement(rankTableName, "BHVALUE"), _
                                      "Busy hour value for " & rankMT.TypeName, "measure", "number", "")

                addBusyHourRankObject(rankTableName, "Busy Hour Object", getSelectStatement(rankTableName, "BHOBJECT"), _
                                      "Busy hour object for " & rankMT.TypeName, "dimension", "character", "")

                addBusyHourRankObject(rankTableName, "Busy Hour", getSelectStatement(rankTableName, "BUSYHOUR"), _
                      "Busy hour for " & rankMT.TypeName, "dimension", "number", singleDigitFormat)

                addBusyHourRankObject(rankTableName, "Busy Hour Class", getSelectStatement(rankTableName, "BHCLASS"), _
                      "Busy hour class (day, week or month) for " & rankMT.TypeName, "dimension", "number", singleDigitFormat)

                addBusyHourRankObject(rankTableName, "Busy Hour Window Size", getSelectStatement(rankTableName, "WINDOWSIZE"), _
                                      "Busy Hour Window Size for " & rankMT.TypeName, "dimension", "number", singleDigitFormat)

                addBusyHourRankObject(rankTableName, "Busy Hour Offset", getSelectStatement(rankTableName, "OFFSET"), _
                      "Busy Hour Offset for " & rankMT.TypeName, "dimension", "number", singleDigitFormat)

                ' Add key objects:
                addBusyHourKeyObjects(rankMT, rankTableName)
            Catch e As Exception
                Console.WriteLine("Error adding busy hour ranking object to universe: " & rankTableName)
                Trace.WriteLine("Error adding busy hour ranking object to universe: " & e.ToString)
            End Try
        Next
        Return success
    End Function

    ''
    'Adds busy hour key objects.
    '@param rankMT
    '@param Univ
    Private Sub addBusyHourKeyObjects(ByVal rankMT As MeasurementTypesTPIde.MeasurementType, ByVal className As String)
        Trace.WriteLine("Adding busy hour ranking key objects to universe for " & rankMT.TypeName)
        Dim myKeys As CounterKeysTPIde = rankMT.CounterKeys
        Dim count2 As Integer
        For count2 = 1 To (myKeys.Count)
            Dim myCounterKey As CounterKeysTPIde.CounterKey
            myCounterKey = myKeys.Item(count2)
            Dim objectName As String
            objectName = myCounterKey.CounterKeyName
            Trace.WriteLine("Adding busy hour ranking key object: " & objectName & ", Rank table: " & rankMT.TypeName)

            addBusyHourRankObject(className, objectName, getSelectStatement(className, objectName), _
            objectName & " value for " & className, "dimension", "character", "")
        Next
    End Sub

    ''
    'Gets select statement.
    '@param rankTableName
    '@param rankObjectName
    '@returns
    Private Function getSelectStatement(ByVal tableName As String, ByVal objectName As String) As String
        Dim statement As String
        ' select value from RANKBH table:
        statement = "DC." & tableName & "." & objectName ' e.g. DC.DC_E_SGSN_NEBH_RANKBH.BHTYPE
        Return statement
    End Function

    '' 
    'Gets the busy hour criteria 
    '@param busyHourLevel
    '@param techPackTPIde
    '@param dbReader
    '@param conn
    '@returns 
    Private Function getBusyHourCriteria(ByVal busyHourLevel As String, ByVal techPackTPIde As String, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                                         ByRef conn As System.Data.Odbc.OdbcConnection) As String
        Dim result As String
        result = ""

        Dim sqlStatement As String
        sqlStatement = "SELECT BHCRITERIA FROM Busyhour where VERSIONID = '" & techPackTPIde & "' AND BHLEVEL = '" & busyHourLevel & "'"

        ' Set up database reader with SQL statement:
        Try
            databaseProxy.setupDatabaseReader(sqlStatement, conn)
        Catch ex As Exception
            Throw New Exception("Error setting up database reader: " & ex.ToString)
        End Try

        While (databaseProxy.read())
            If databaseProxy.getValue(0).ToString() = "" Then ' just read first value for now
                Trace.WriteLine("Found no results")
                Exit While
            Else
                result = Trim(databaseProxy.getValue(0).ToString())
            End If
        End While

        Return result
    End Function

    '' 
    'Adds a busy hour ranking object to the universe in the Busy Hour class.
    '@param Univ
    '@param rankTableName
    '@param rankObjectName
    '@param selectStatement
    '@param objectDescription
    '@param qualification
    '@param type
    Private Sub addBusyHourRankObject(ByVal rankTableName As String, ByVal rankObjectName As String, _
                                           ByVal selectStatement As String, ByVal objectDescription As String, ByVal qualification As String, _
                                           ByVal type As String, ByVal objectFormat As String)
        Dim Cls As Designer.IClass
        Dim Obj As Designer.IObject = Nothing
        Dim parentClass As String = "Busy Hour"

        ' Define parameters for the objects:
        Dim objWhere As String
        Dim aggregation As String

        ' Get the class using the rankTableName (the class name is just the rank table name):
        Cls = universeProxy.getClass(rankTableName)

        If Cls Is Nothing Then
            Trace.WriteLine("Error: failed to find universe class for rank measurement table: " & rankTableName)
            Return
        End If

        ' Add the object:
        Try
            Obj = universeProxy.getObject(Cls, rankObjectName)
        Catch e As Exception
            ' If not there already, add it in:
            Obj = universeProxy.addObject(rankObjectName, Cls)
        Finally
            ' Add object to updated objects:
            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"

            ' Set up object values
            objWhere = ""
            aggregation = ""
            setObjectParams(Obj, selectStatement, objWhere, objectDescription, type, qualification, aggregation)

            If (objectFormat <> "" And type = "number") Then
                Obj.Format.NumberFormat = objectFormat
            End If
        End Try
        ' Check the object parses ok:
        If ParseObject(Obj, Cls) = False Then
            Throw New Exception("Failed to parse object: " & Obj.Name & " in class " & Cls.Name)
        End If
    End Sub

    '''
    ' Adds an object to the universe.
    '@param className       The universe class name.
    '@param objName         The object name
    '@param TechPackTPIde   The tech pack identifier. Can be the tech pack itself or the base tech pack.
    '@param objSelect       The select statement to use for the object.
    '@param objWhere        The where statement to use for the object.
    '@param description     The description statement to use for the object.
    '@param objectType      The type of the object.
    '@param qualification   Qualification
    '@param aggregation     Aggregation
    '@returns               True if added ok.
    Private Function addObjectToUniverse(ByVal className As String, ByVal objName As String, ByRef TechPackTPIde As String, ByVal objSelect As String, _
                                        ByVal objWhere As String, ByVal description As String, ByVal objectType As String, _
                                        ByVal qualification As String, ByVal aggregation As String) As Boolean
        Dim Cls As Designer.IClass
        Dim Obj As Designer.IObject
        Trace.WriteLine("Adding object to universe: " & className & "/" & objName)

        ' Get the object's class in the universe:
        Cls = universeProxy.getClass(className)
        If Cls Is Nothing Then
            Trace.WriteLine("Couldn't find class: " & className & ". Failed to add object: " & className & "/" & objName)
            Return False
        End If

        ' Add the object to the universe:
        Try
            Obj = universeProxy.getObject(Cls, objName)
        Catch e As Exception
            Obj = universeProxy.addObject(objName, Cls)
        End Try

        Try
            ' Special case for General/TP Version, add DIM_DATE as a table:
            If objName = "TP Version" AndAlso className = "General" Then
                objSelect = "'" & objSelect & "'"
                'universeProxy.addToObjectsTables(Obj, "DC.DIM_DATE")
                Dim table_name As String
                table_name = "DC.DIM_DATE"

                description = "TP Version"
                Dim obj_table As Designer.Table
                obj_table = Obj.Tables.Add(table_name)
                obj_table = Obj.Tables.Item(obj_table)

            End If

            Trace.WriteLine("Setting object parameters for: " & className & "/" & objName)
            setObjectParams(Obj, objSelect, objWhere, description, objectType, qualification, aggregation)
            Trace.WriteLine("Added object from " & TechPackTPIde & ": " & className & "/" & objName & _
                            ", Select: " & objSelect & ", Where: " & objWhere)
        Catch ex As Exception
            Trace.WriteLine("Error adding object to universe: " & ex.ToString())
        End Try

        If ParseObject(Obj, Cls) = False Then
            Trace.WriteLine("Error parsing object : " & className & "/" & objName)
            Return False
        End If
        Return True
    End Function

    ''
    ' Adds extra object to universe. 
    ' @param tp_name Specifies name of tech pack
    ' @param Univ Specifies reference to universe
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Objects are defined in the Universe, Objects tab in the tech pack IDE.
    Public Function getObjectsFromDatabase(ByRef tp_name As String, ByRef tp_release As String, ByRef conn As System.Data.Odbc.OdbcConnection, _
                               ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                               ByRef mts As MeasurementTypesTPIde, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, _
                               ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String, ByVal addToUniverse As Boolean) As Boolean Implements IBOObjectsTPIde.getObjectsFromDatabase
        Dim Cls As Designer.IClass
        Dim Obj As Designer.IObject
        Dim objName As String
        Dim objSelect As String
        Dim objWhere As String
        Dim Count As Integer
        Dim unvobject As BOObject
        Dim addObject As Boolean

        Dim description As String
        Dim objSelectRead As String
        Dim objWhereRead As String
        Dim aggregation As String
        Dim qualification As String
        Dim objectType As String

        Dim unvobj As String
        unvobj = "SELECT CLASSNAME,UNIVERSEEXTENSION,OBJECTNAME," & _
        "SUBSTR(DESCRIPTION,1,8000),SUBSTR(DESCRIPTION,8001,8000),SUBSTR(DESCRIPTION,16001,8000),SUBSTR(DESCRIPTION,24001,8000)" & _
        ",OBJECTTYPE,QUALIFICATION,AGGREGATION," & _
        "SUBSTR(OBJSELECT,1,8000),SUBSTR(OBJSELECT,8001,8000),SUBSTR(OBJSELECT,16001,8000),SUBSTR(OBJSELECT,24001,8000)," & _
        "SUBSTR(OBJWHERE,1,8000),SUBSTR(OBJWHERE,8001,8000),SUBSTR(OBJWHERE,16001,8000),SUBSTR(OBJWHERE,24001,8000)" & _
        ",OBJ_BH_REL,ELEM_BH_REL FROM Universeobject WHERE VERSIONID='" & TechPackTPIde & "'"

        Dim updatedObjects As String = ""

        Try
            databaseProxy.setupDatabaseReader(unvobj, conn)
        Catch ex As Exception
            Console.WriteLine("Error: failed to read objects from universe : " & ex.ToString)
            Trace.WriteLine("Error: failed to read objects from universe : " & ex.ToString)
            Return False
        End Try

        Dim addedObjToUnv As Boolean = True

        While (databaseProxy.read())
            addedObjToUnv = True
            Try
                If databaseProxy.getValue(0).ToString() = "" Then
                    ' No data is read for this tech pack, exit:
                    Console.WriteLine("No data read for " & TechPackTPIde)
                    Trace.WriteLine("No data read for " & TechPackTPIde)
                    Exit While
                Else
                    If Trim(databaseProxy.getValue(1).ToString()) <> "" Then
                        Try
                            description = ""
                            objectType = ""
                            qualification = ""
                            aggregation = ""
                            objSelectRead = ""
                            objWhereRead = ""

                            unvobject = New BOObject
                            addObject = False
                            unvobject.ClassName = Trim(databaseProxy.getValue(0).ToString())
                            unvobject.UniverseExtension = Trim(databaseProxy.getValue(1).ToString())
                            unvobject.Name = Trim(databaseProxy.getValue(2).ToString())
                            If databaseProxy.isDBNull(3) = False Then
                                description = Trim(databaseProxy.getString(3) + databaseProxy.getString(4) + databaseProxy.getString(5) + databaseProxy.getString(6))
                            Else
                                description = ""
                            End If
                            objectType = Trim(databaseProxy.getValue(7).ToString())
                            qualification = Trim(databaseProxy.getValue(8).ToString())
                            aggregation = Trim(databaseProxy.getValue(9).ToString())
                            If databaseProxy.isDBNull(10) = False Then
                                objSelectRead = Trim(databaseProxy.getString(10) + databaseProxy.getString(11) + databaseProxy.getString(12) + databaseProxy.getString(13))
                            Else
                                objSelectRead = ""
                            End If
                            If databaseProxy.isDBNull(14) = False Then
                                objWhereRead = Trim(databaseProxy.getString(14) + databaseProxy.getString(15) + databaseProxy.getString(16) + databaseProxy.getString(17))
                            Else
                                objWhereRead = ""
                            End If
                            unvobject.ObjectBHRelated = Trim(databaseProxy.getValue(18).ToString())
                            unvobject.ElementBHRelated = Trim(databaseProxy.getValue(19).ToString())

                            If unvobject.UniverseExtension = "all" Then
                                addObject = True
                            ElseIf unvobject.UniverseExtension = "" AndAlso UniverseNameExtension = "" Then
                                addObject = True
                            Else
                                Dim UniverseCountList() As String
                                Dim UnvCount As Integer
                                If InStrRev(unvobject.UniverseExtension, ",") = 0 Then
                                    If unvobject.UniverseExtension = UniverseNameExtension Then
                                        addObject = True
                                    End If
                                Else
                                    UniverseCountList = Split(unvobject.UniverseExtension, ",")
                                    For UnvCount = 0 To UBound(UniverseCountList)
                                        If UniverseCountList(UnvCount) = UniverseNameExtension Then
                                            addObject = True
                                            Exit For
                                        End If
                                    Next
                                End If
                            End If
                        Catch ex As Exception
                            Trace.WriteLine("Error reading object data from database: " & ex.ToString())
                        End Try

                        Try
                            If addObject = True Then
                                If unvobject.ObjectBHRelated = ObjectBHSupport OrElse unvobject.ElementBHRelated = ElementBHSupport OrElse (unvobject.ObjectBHRelated = False AndAlso unvobject.ElementBHRelated = False) Then
                                    If InStrRev(objSelectRead, "(DIM_RANKMT)") > 0 Then
                                        For Count = 1 To mts.Count
                                            If mts.Item(Count).RankTable = True Then

                                                objName = replaceNaming(tp_name, unvobject.Name, "(TPNAME)", "")

                                                If mts.Item(Count).ElementBusyHours = True Then
                                                    objName = Replace(objName, "(BHObject)", "Element")
                                                End If
                                                If mts.Item(Count).ObjectBusyHours <> "" Then
                                                    objName = Replace(objName, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                                End If

                                                objSelect = replaceNaming(tp_name, objSelectRead, "(TPNAME)", "")
                                                objSelect = replaceNaming(mts.Item(Count).TypeName, objSelect, "(DIM_RANKMT)", "DIM_")
                                                'Code chnages for the TR HR15161
                                               
                                                objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")
                                                objWhere = replaceNaming(mts.Item(Count).TypeName, objWhere, "(DIM_RANKMT)", "DIM_")

                                                UniverseFunctionsTPIde.updatedObjects &= unvobject.ClassName & "/" & objName & ";"
                                                If (addToUniverse = True) Then
                                                    addedObjToUnv = addObjectToUniverse(unvobject.ClassName, objName, TechPackTPIde, objSelect, _
                                                                                                     objWhere, description, objectType, qualification, aggregation)
                                                End If
                                            End If
                                        Next Count
                                    ElseIf (unvobject.ObjectBHRelated = True AndAlso ObjectBHSupport = False) AndAlso (unvobject.ElementBHRelated = True AndAlso ElementBHSupport = False) Then
                                        'Do nothing
                                    ElseIf InStrRev(objSelectRead, "(ELEMENTRANKMT)") > 0 Then
                                        For Count = 1 To mts.Count
                                            If mts.Item(Count).RankTable = True AndAlso mts.Item(Count).ElementBusyHours = True Then

                                                objName = replaceNaming(tp_name, unvobject.Name, "(TPNAME)", "")

                                                If mts.Item(Count).ElementBusyHours = True Then
                                                    objName = Replace(objName, "(BHObject)", "Element")
                                                End If

                                                objSelect = replaceNaming(tp_name, objSelectRead, "(TPNAME)", "")
                                                objSelect = Replace(objSelect, "(ELEMENTRANKMT)", mts.Item(Count).TypeName & "_RANKBH")
                                               
                                                objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")
                                                objWhere = Replace(objWhere, "(ELEMENTRANKMT)", mts.Item(Count).TypeName & "_RANKBH")

                                                UniverseFunctionsTPIde.updatedObjects &= unvobject.ClassName & "/" & objName & ";"
                                                If (addToUniverse = True) Then
                                                    addedObjToUnv = addObjectToUniverse(unvobject.ClassName, objName, TechPackTPIde, objSelect, _
                                                                                                     objWhere, description, objectType, qualification, aggregation)
                                                End If
                                            End If
                                        Next Count
                                    ElseIf InStrRev(objSelectRead, "(TPRELEASE)") > 0 Then
                                        objName = replaceNaming(tp_name, unvobject.Name, "(TPNAME)", "")

                                        objSelect = Replace(objSelectRead, "(TPRELEASE)", tp_name & " " & tp_release)
                                        objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")
                                       
                                        UniverseFunctionsTPIde.updatedObjects &= unvobject.ClassName & "/" & objName & ";"
                                        If (addToUniverse = True) Then
                                            addedObjToUnv = addObjectToUniverse(unvobject.ClassName, objName, TechPackTPIde, objSelect, _
                                                                                                     objWhere, description, objectType, qualification, aggregation)
                                        End If
                                    Else
                                        objName = replaceNaming(tp_name, unvobject.Name, "(TPNAME)", "")

                                        objSelect = replaceNaming(tp_name, objSelectRead, "(TPNAME)", "")
                                        objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")

                                        UniverseFunctionsTPIde.updatedObjects &= unvobject.ClassName & "/" & objName & ";"
                                        If (addToUniverse = True) Then
                                            addedObjToUnv = addObjectToUniverse(unvobject.ClassName, objName, TechPackTPIde, objSelect, _
                                                                                                     objWhere, description, objectType, qualification, aggregation)
                                        End If
                                    End If
                                End If
                            End If
                        Catch ex As Exception
                            Trace.WriteLine("Error adding object to universe: " & ex.ToString())
                        End Try
                    End If
                End If
            Catch ex As Exception
                Trace.WriteLine("Error adding object: " & ex.ToString())
                addedObjToUnv = False
            End Try

            ' Check if the object was added ok:
            If (addedObjToUnv = False) Then
                Console.WriteLine("Failed to add object")
                Trace.WriteLine("Failed to add object")
            End If
        End While

        ' Close the database connection:
        databaseProxy.closeDatabase()
        Return True
    End Function

    Public Function replaceNaming(ByRef techpackName As String, ByRef Expression As String, ByVal Find As String, ByVal Replacement As String) As String
        Dim returnValue As String
        returnValue = ""
        If (techpackName.StartsWith("DC_")) Then
            'Code chnages for TR HR15161
            returnValue = Replace(Expression, Find, Replace(techpackName, "DC_", Replacement, , 1))
           
        ElseIf (techpackName.StartsWith("PM_")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "PM_", Replacement))
        ElseIf (techpackName.StartsWith("CM_")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "CM_", Replacement))
        ElseIf (techpackName.StartsWith("CUSTOM_DC")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "DC_", Replacement))
        ElseIf (techpackName.StartsWith("CUSTOM_")) Then
	    returnValue = Replace(Expression, Find, techpackName)
	Else
            ' Return Expression
            returnValue = Expression
        End If
        Return returnValue
    End Function


    Public Function addObject(ByRef univ_class As String, ByRef univ_object As String, ByRef objType As String, ByRef objSelect As String, _
                              ByRef description As String) As Boolean Implements IBOObjectsTPIde.addObject

        Dim Cls As Designer.IClass
        Dim Obj As Designer.IObject
        Dim objName As String
        Dim objWhere As String

        Cls = Me.universeProxy.getClass(univ_class)

        If Cls Is Nothing Then
            Trace.WriteLine("Class Failure: Class='" & univ_class & "', Object='" & univ_object & _
                "', Description='" & description & "', Type='" & objType & "'.")
            Return True
        End If

        Try
            Obj = Me.universeProxy.getObject(Cls, univ_object)
        Catch e As Exception
            Obj = Me.universeProxy.addObject(univ_object, Cls)
        Finally
            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            objWhere = ""
            If Obj Is Nothing Then
                Trace.WriteLine("Object Failure: Class='" & univ_class & "', Object='" & univ_object & _
                "', Description='" & description & "', Type='" & objType & "'.")
            Else
                setReferenceObjectParams(Obj, objSelect, objWhere, description, objType, "dimension", "")
            End If
        End Try
        If ParseObject(Obj, Cls) = False Then
            Return False
        End If
        Return True

    End Function

    ''
    'Removes counter objects from the universe for EBS tech packs.
    '@param Cls     The universe class for the measurment type.
    '@param cnts    The counters from the measurement type object (from dwhrep).
    '@remark        Only removes a counter object from the universe if there is no object in dwhrep.
    '               The universe then reflects the MOM when the EBS tech pack is updated with a new MOM.
    Public Sub removeObjectsForEBS(ByRef Cls As Designer.IClass, ByRef cnts As CountersTPIde) Implements IBOObjectsTPIde.removeObjectsForEBS
        Trace.WriteLine("BOObjectsTPIde, removeObjectsForEBS() --> entering")
        Dim removedObject As Boolean = False
        Dim x As Integer
        Dim y As Integer

        ' The "univObject" field in the counter in the tech pack:
        Dim counterUnivObject As String
        ' The object name defined in the universe:
        Dim objectNameInUniverse As String

        Dim UnvCntAmount As Integer
        Dim MeasCntAmount As Integer
        Dim Count As Integer
        Dim aggr_awareFormula As String
        UnvCntAmount = Cls.Objects.Count()
        MeasCntAmount = cnts.Count()

        ' If there are no counters defined in dwhrep, remove all of the counters from the universe class:
        If (MeasCntAmount = 0) Then
            For y = UnvCntAmount To 1 Step -1
                objectNameInUniverse = Cls.Objects(y).Name
                If ((objectNameInUniverse <> "data_coverage") And (objectNameInUniverse <> "period_duration")) Then
                    Trace.WriteLine("BOObjectsTPIde, removeObjectsForEBS(): Deleting " & Cls.Name & "/" & Cls.Objects(y).Name)
                    Cls.Objects(y).Delete()
                End If
            Next
        Else
            Dim RemoveItem As Boolean
            For y = UnvCntAmount To 1 Step -1
                RemoveItem = True
                objectNameInUniverse = Cls.Objects(y).Name()
                If ((objectNameInUniverse = "data_coverage") Or (objectNameInUniverse = "period_duration")) Then
                    RemoveItem = False
                Else
                    ' Try to find the universe object in the counters defined in the tech pack:
                    For x = 1 To MeasCntAmount
                        Dim counter As CountersTPIde.Counter
                        counter = cnts.Item(x)
                        counterUnivObject = counter.UnivObject.ToString()

                        If (counterUnivObject = objectNameInUniverse OrElse (checkForAggObject(counter, objectNameInUniverse))) Then
                            RemoveItem = False
                            Exit For
                        End If
                    Next x
                End If

                If (RemoveItem = True) Then
                    Trace.WriteLine("BOObjectsTPIde, removeObjectsForEBS(): Deleting " & Cls.Name & "/" & Cls.Objects(y).Name)
                    Cls.Objects(y).Delete()
                End If
            Next y

        End If
        Trace.WriteLine("BOObjectsTPIde, removeObjectsForEBS() <-- exiting")
    End Sub

    ''
    ''Checks if a universe object for an EBS tech pack matches a counter in the tech pack with an aggregation.
    '@param counter                 The counter from the tech pack.
    '@param objectNameInUniverse    The object name in the universe.
    '@returns foundAggObject        True if there is a counter with the same name and aggregation e.g. "counter (avg)" in the tech pack.
    '@remarks                       If found, we shouldn't remove the universe object.
    Private Function checkForAggObject(ByVal counter As CountersTPIde.Counter, ByVal objectNameInUniverse As String) As Boolean
        Dim foundAggObject As Boolean = False
        Try
            If ((counter.Aggregations Is Nothing) OrElse (counter.UnivObject Is Nothing)) Then
                foundAggObject = False
            Else
                Dim univObject As String = counter.UnivObject.ToString()
                For index As Integer = 0 To UBound(counter.Aggregations)
                    If (counter.Aggregations(index) <> "") Then
                        ' example: UnivObject + (avg)
                        If (objectNameInUniverse = univObject & " (" & LCase(counter.Aggregations(index)) & ")") Then
                            foundAggObject = True
                            Exit For
                        End If
                    End If
                Next
            End If
        Catch ex As Exception
            Trace.WriteLine("Error checking for aggregate object: " & ex.ToString())
        End Try

        Return foundAggObject
    End Function

    ''
    ' Sets object parameters.
    '@param Obj
    '@param objSelect
    '@param objWhere
    '@param description
    '@param type
    '@param qualification
    '@param aggregation
    Sub setObjectParams(ByRef Obj As Designer.IObject, ByRef objSelect As String, ByRef objWhere As String, _
    ByRef description As String, ByRef type As String, ByRef qualification As String, ByRef aggregation As String)
        Try
            If (description <> Nothing) Then
                Obj.Description = description
            Else
                Obj.Description = ""
            End If
            setObjectType(type, Obj)
            setQualificationAndAggregation(qualification, aggregation, Obj)

            If (objSelect <> Nothing) Then
                Obj.Select = objSelect
            Else
                Obj.Select = ""
            End If
            If (objWhere <> Nothing) Then
                Obj.Where = objWhere
            Else
                Obj.Where = ""
            End If
            universeProxy.formatObject(Obj)
            Obj.HasListOfValues = True
            Obj.AllowUserToEditLov = True
            Obj.AutomaticLovRefreshBeforeUse = False
            Obj.ExportLovWithUniverse = False
        Catch ex As Exception
            Trace.WriteLine("Error in BOObjectsTPIde.setObjectParams: " & ex.ToString())
        End Try

    End Sub

    ''
    ' Sets reference object parameters.
    '@param Obj
    '@param objSelect
    '@param objWhere
    '@param description
    '@param type
    '@param qualification
    '@param aggregation
    Sub setReferenceObjectParams(ByRef Obj As Designer.IObject, ByRef objSelect As String, ByRef objWhere As String, _
    ByRef description As String, ByRef type As String, ByRef qualification As String, ByRef aggregation As String)
        Try
            If (description <> Nothing) Then
                Obj.Description = description
            Else
                Obj.Description = ""
            End If
            setObjectType(type, Obj, False)
            setQualificationAndAggregation(qualification, aggregation, Obj)

            If (objSelect <> Nothing) Then
                Obj.Select = objSelect
            Else
                Obj.Select = ""
            End If
            If (objWhere <> Nothing) Then
                Obj.Where = objWhere
            Else
                Obj.Where = ""
            End If
            Obj.Format.NumberFormat = formatObject(Obj)

            Obj.HasListOfValues = True
            Obj.AllowUserToEditLov = True
            Obj.AutomaticLovRefreshBeforeUse = False
            Obj.ExportLovWithUniverse = False
        Catch ex As Exception
            Trace.WriteLine("Error in BOObjectsTPIde.setReferenceObjectParams: " & ex.ToString())
        End Try
    End Sub
    Public Function ParseCounterObject(ByRef Obj As Designer.Object, ByRef Cls As Designer.Class) As Integer
        Dim Result As MsgBoxResult
        Try
            Obj.Parse()
        Catch ex As Exception
            Trace.WriteLine("Counter Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
            Trace.WriteLine("Counter Object Parse Exception: " & ex.ToString)
        End Try
        Return 0
    End Function
    Public Function ParseReferenceObject(ByRef Obj As Designer.Object, ByRef Cls As Designer.Class) As Integer
        Dim Result As MsgBoxResult
        Try
            Obj.Parse()
        Catch ex As Exception
            Trace.WriteLine("Reference Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
            Trace.WriteLine("Reference Object Parse Exception: " & ex.ToString)
        End Try
        Return 0
    End Function

    ''
    ' Tests parsing of a universe object.
    '@param     Obj     Universe object.
    '@param     Cls     Universe class.
    '@returns   result  True if the object parsed ok.
    Public Function ParseObject(ByRef Obj As Designer.IObject, ByRef Cls As Designer.IClass) As Boolean
        Dim result As Boolean = True
        If m_objectParse = True Then
            Try
                Obj.Parse()
            Catch ex As Exception
                Trace.WriteLine("Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
                Trace.WriteLine("Object Parse Exception: " & ex.ToString)
                result = False
            End Try
        End If
        Return result
    End Function
    Function getClass(ByRef Univ As Object, ByRef classname As String) As Designer.Class

        Dim Cls As Designer.Class
        Try
            Cls = Univ.Classes.FindClass(classname)
        Catch e As Exception
            Trace.WriteLine("Class '" & classname & "' is not found. Add class to TP Definition.")
            Trace.WriteLine("Class Exception: " & e.ToString)
        End Try
        Return Cls
    End Function
    ''
    ' Sets type for universe object. 
    '
    ' @param DefinedType Specifies defined type for object
    ' @param Obj Specifies reference to object
    Public Sub setObjectType(ByRef DefinedType As String, ByRef Obj As Designer.IObject)

        If LCase(DefinedType) = "date" Then
            Obj.Type = Designer.DsObjectType.dsDateObject
        ElseIf LCase(DefinedType) = "character" Then
            Obj.Type = Designer.DsObjectType.dsCharacterObject
        ElseIf LCase(DefinedType) = "number" Then
            Obj.Type = Designer.DsObjectType.dsNumericObject
        Else
            Obj.Type = Designer.DsObjectType.dsCharacterObject
        End If

    End Sub

    ''
    ' Sets qualification function for universe object. 
    '
    ' @param Qualification Specifies defined qualification for object
    ' @param DefinedAggregation Specifies defined aggregation for object
    ' @param Obj Specifies reference to object
    Public Sub setQualificationAndAggregation(ByRef Qualification As String, ByRef DefinedAggregation As String, ByRef Obj As Designer.IObject)

        If LCase(Qualification) = "measure" Then
            Obj.Qualification = Designer.DsObjectQualification.dsMeasureObject
            setAggregateFunction(DefinedAggregation, Obj)
        ElseIf LCase(Qualification) = "dimension" Then
            Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
        ElseIf LCase(Qualification) = "detail" Then
            Obj.Qualification = Designer.DsObjectQualification.dsDetailObject
        Else
            Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
        End If

    End Sub

    ''
    ' Sets aggregation function for universe object. 
    '
    ' @param DefinedAggregation Specifies defined aggregation for object
    ' @param Obj Specifies reference to object
    Public Sub setAggregateFunction(ByRef DefinedAggregation As String, ByRef Obj As Designer.Object)

        If DefinedAggregation = "AVG" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByAvgObject
        ElseIf DefinedAggregation = "COUNT" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByCountObject
        ElseIf DefinedAggregation = "MAX" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByMaxObject
        ElseIf DefinedAggregation = "MIN" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByMinObject
        ElseIf DefinedAggregation = "NULL" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByNullObject
        ElseIf DefinedAggregation = "NONE" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByNullObject
        ElseIf DefinedAggregation = "SUM" Then
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateBySumObject
        Else
            Trace.WriteLine("Aggregation must be set for object '" & Obj.Name & "' in class ''.")
        End If

    End Sub
    ''
    ' Sets type for universe object. 
    '
    ' @param DataType Specifies data type of object
    ' @param Obj Specifies reference to object
    ' @param Counter Specifies if object is counter or reference object. If value is True, object is counter object.
    Public Sub setObjectType(ByRef DataType As String, ByRef Obj As Designer.Object, ByRef Counter As Boolean)
        Try
            If InStrRev(DataType, "date") > 0 Then
                Obj.Type = Designer.DsObjectType.dsDateObject
            ElseIf InStrRev(DataType, "char") > 0 Then
                Obj.Type = Designer.DsObjectType.dsCharacterObject
            Else
                Obj.Type = Designer.DsObjectType.dsNumericObject
            End If

            If Obj.Type = Designer.DsObjectType.dsCharacterObject OrElse Obj.Type = Designer.DsObjectType.dsDateObject Then
                If Counter = True Then
                    Obj.Qualification = Designer.DsObjectQualification.dsMeasureObject
                    Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByNullObject
                    If InStrRev(DataType, "date") > 0 Then
                        Obj.Type = Designer.DsObjectType.dsDateObject
                    Else
                        Obj.Type = Designer.DsObjectType.dsCharacterObject
                    End If
                Else
                    Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                End If
            Else
                Obj.Qualification = Designer.DsObjectQualification.dsMeasureObject
            End If
        Catch ex As Exception
            Trace.WriteLine("setObjectType: Obj='" & Obj.Name & "', Data type='" & DataType & "', Counter='" & Counter & "'.")
            Trace.WriteLine("setObjectType Exception: " & ex.ToString)
        End Try

    End Sub

    ''
    ' Sets default formatting for universe object. 
    '
    ' @param Obj Specifies reference to object
    ' @return Formatting mask
    Public Function formatObject(ByRef Obj As Designer.IObject) As String
        If Obj.Type = Designer.DsObjectType.dsNumericObject Then
            Return "0;-0;0"
        Else
            ' If not a numeric object, return empty string:
            Return ""
        End If
    End Function

    ''
    ' Adds an object to specified class. If object already exists, it is selected. 
    '
    ' @param Obj Specifies reference to object's class
    ' @param cnt Specifies reference to counter class
    ' @return Reference to object mask
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt As CountersTPIde.Counter, ByRef Counter As Boolean, ByRef AggrFunc As String) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(cnt.UnivObject)
        Catch e As Exception
            Obj = Cls.Objects.Add(cnt.UnivObject, Cls)
        Finally
            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = cnt.Description
            setObjectType(cnt.Datatype, Obj, Counter)
            setAggregateFunction(AggrFunc, Obj)
        End Try
        Return Obj

    End Function

    ''
    ' Adds an object to specified class. If object already exists, it is selected. 
    '
    ' @param Obj Specifies reference to object's class
    ' @param cnt Specifies reference to counter class
    ' @return Reference to object mask
    Public Function addObject(ByRef Cls As Designer.Class, ByRef rd As ReferenceDatasTPIde.ReferenceData, ByRef Counter As Boolean) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(rd.UnivObject)
        Catch e As Exception
            Obj = Cls.Objects.Add(rd.UnivObject, Cls)
        Finally
            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = rd.Description
            setObjectType(rd.Datatype, Obj, Counter)
        End Try
        Return Obj

    End Function

    ''
    ' Adds an object to specified class with custom name. If object already exists, it is selected. 
    '
    ' @param Obj Specifies reference to object's class
    ' @param cnt Specifies reference to counter class
    ' @param cnt_name Specifies object name
    ' @return Reference to object mask
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt As CountersTPIde.Counter, ByRef cnt_name As String, ByRef Counter As Boolean, _
                              ByRef AggrFunc As String) As Designer.Object Implements IBOObjectsTPIde.addObject
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(cnt_name)
        Catch e As Exception
            Obj = Cls.Objects.Add(cnt_name, Cls)
        Finally
            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = cnt.Description
            setObjectType(cnt.Datatype, Obj, Counter)
            setAggregateFunction(AggrFunc, Obj)
        End Try
        Return Obj

    End Function

    ''
    ' Adds an object to specified class with specified name. If object already exists, it is selected. 
    '
    ' @param Obj Specifies reference to object's class
    ' @param cnt_name Specifies object name
    ' @param objType Specifies object type
    ' @param objQual Specifies object qualification
    ' @param objAggr Specifies object aggregate function
    ' @param description Specifies object description
    ' @return Reference to object mask
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt_name As String, ByRef objType As Designer.DsObjectType, ByRef objQual As Designer.DsObjectQualification, ByRef objAggr As Designer.DsObjectAggregate, ByRef description As String) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(cnt_name)
        Catch e As Exception
            Obj = Cls.Objects.Add(cnt_name, Cls)
        Finally
            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = description
            Obj.Type = objType
            Obj.Qualification = objQual
            Obj.AggregateFunction = objAggr
            Obj.Format.NumberFormat = formatObject(Obj)
        End Try
        Return Obj

    End Function

    ''
    ' Sets object's key formatting. 
    '
    ' @param obj Specifies reference to object's class
    ' @param cnt_key Specifies reference to counter key
    Public Sub keyFormat(ByRef obj As Designer.Object, ByRef cnt_key As CounterKeysTPIde.CounterKey)
        Try
            obj.Description = cnt_key.Description
            setObjectType(cnt_key.Datatype, obj, False)
            obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
            obj.Format.NumberFormat = getNumberFormatWithDatascale(obj, cnt_key.Datascale)
            obj.HasListOfValues = True
            obj.AllowUserToEditLov = True
            obj.AutomaticLovRefreshBeforeUse = False
            obj.ExportLovWithUniverse = False
        Catch ex As Exception
            Trace.WriteLine("Key Format Exception: " & ex.Message)
        End Try
    End Sub

    ''
    ' Sets object's key formatting. 
    '
    ' @param obj Specifies reference to object's class
    ' @param CMTechPack Specifies whether technology package is CM package
    ' @return Data formatting string
    Public Function formatCounterObject(ByRef obj As Designer.Object, ByVal cnt As CountersTPIde.Counter, ByRef CMTechPack As Boolean) As String
        Dim format As String
        format = ""
        If obj.Type = Designer.DsObjectType.dsNumericObject Then
            format = getNumberFormatWithDatascale(obj, cnt.Datascale)
        End If
        Return format
    End Function

    ''
    ' Gets the number format for a counter, calculates decimal places depending on the datascale.
    '@param obj Reference to object in universe
    '@param datascale 
    '@returns format Data formatting string
    Public Function getNumberFormatWithDatascale(ByRef obj As Designer.IObject, ByVal datascaleString As String) As String
        Dim format As String
        ' Format is blank by default (standard formatting):
        format = ""

        If (obj Is Nothing OrElse datascaleString Is Nothing) Then
            format = ""
        ElseIf (obj.Type = Designer.DsObjectType.dsNumericObject) Then
            ' Get the datascale as an integer (value is 1 by default):
            Dim datascaleInt As Integer = 1
            Try
                datascaleInt = Integer.Parse(datascaleString)
            Catch ex As Exception
                Trace.WriteLine("Error parsing object's data scale. Data scale was: " & datascaleString & ". " _
                    & "Using default formatting (Standard): " & ex.ToString())
            End Try

            If (datascaleInt = 0) Then
                format = "0;-0;0"
            ElseIf (datascaleInt > 0) Then
                ' use default/blank formatting
                format = ""
            End If
        End If
        Return format
    End Function

    ''
    ' Protected creator function to create TPUtilitiesTPIde object.
    ' @return A new TPUtilitiesTPIde object
    Protected Overridable Function createTPUtilities() As ITPUtilitiesTPIde
        Return New TPUtilitiesTPIde
    End Function

    ''
    ' Protected creator function to create DatabaseProxy object.
    ' @return A new DatabaseProxy object
    Protected Overridable Function createDatabaseProxy() As DBProxy
        Return New DatabaseProxy()
    End Function

End Class

