Option Strict Off


''
'  BOComputedObjects class is a collection of BOComputedObject classes
'
Public Class BOComputedObjectTPIde
    Private _objects As System.Collections.ArrayList = New System.Collections.ArrayList
    Public ObjectParse As Boolean
    Private objectname As String = ""

    ''
    '  Gets count of BOComputedObject classes in BOComputedObjects class
    '
    ' @param Index Specifies the index in the BOComputedObjects class
    ' @return Count of BOComputedObject classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _objects Is Nothing) Then
                Return _objects.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOComputedObject class from BOComputedObjects class based on given index.
    '
    ' @param Index Specifies the index in the BOComputedObjects class
    ' @return Reference to BOComputedObject
    Public ReadOnly Property Item(ByVal Index As Integer) As BOComputedObject
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_objects.Item(Index - 1), BOComputedObject)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOComputedObject class to BOComputedObjects class
    '
    ' @param ValueIn Specifies reference to BOComputedObject
    Public Sub AddItem(ByVal ValueIn As BOComputedObject)

        If (Not _objects Is Nothing) Then
            _objects.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOComputedObject defines universe's objects.
    '
    Public Class BOComputedObject
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
    ' Adds extra object to universe. 
    '
    ' @param tp_name Specifies name of tech pack
    ' @param Univ Specifies reference to universe
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Objects are defined in TP definition's sheet 'Universe objects'.
    Public Function addObjects(ByRef tp_name As String, ByRef tp_release As String, ByRef Univ As Object, ByRef conn As System.Data.Odbc.OdbcConnection, _
                               ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                               ByRef mts As MeasurementTypesTPIde, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, _
                               ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String, ByRef BaseTechPackTPIde As String) As Boolean

        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim objName As String
        Dim objSelect As String
        Dim objWhere As String
        Dim Count As Integer
        Dim unvcomputedobject As BOComputedObject
        Dim addObject As Boolean
        Dim tmpdbCommand As System.Data.Odbc.OdbcCommand
        Dim tmpconn As System.Data.Odbc.OdbcConnection
        Dim tmpdbReader As System.Data.Odbc.OdbcDataReader

        Dim description As String
        Dim objSelectRead As String
        Dim objWhereRead As String
        Dim aggregation As String
        Dim qualification As String
        Dim objectType As String
        Dim ordernro As String
        Dim arrayList As New System.Collections.ArrayList
        Dim FormulaName As String
        Dim Formula As String
        Dim params As String
        Dim ObjSelectList() As String
        Dim tmp As String
        Dim tmp1 As String
        Dim a As Integer
        Dim Found As Boolean
        Dim Already As Boolean
        Dim len As Integer
        Dim formulaitem As String

        tmpconn = conn

        Dim unvobj As String

        unvobj = "SELECT DISTINCT a.CLASSNAME," & _
        "a.OBJECTNAME," & _
        "c.ORDERNRO," & _
        "c.TYPENAME, " & _
        "e.UNIVCLASS," & _
        "e.UNIVOBJECT," & _
        "e.TIMEAGGREGATION," & _
        "e.GROUPAGGREGATION " & _
        "FROM Universecomputedobject a, Universeformulas b ,Universeparameters c, MeasurementType d, MeasurementCounter e " & _
        "WHERE a.VERSIONID='" & TechPackTPIde & "' And (a.CLASSNAME = c.CLASSNAME) And (a.OBJECTNAME = c.OBJECTNAME) " & _
        "And a.Versionid = c.Versionid AND ((b.VERSIONID = a.Versionid or b.VERSIONID='" & BaseTechPackTPIde & "') and a.OBJSELECT = b.NAME) " & _
        "AND (d.VERSIONID = a.Versionid and d.TYPENAME = c.TYPENAME) " & _
        "AND (e.TYPEID = d.TYPEID and e.DATANAME = c.NAME) and e.UNIVCLASS is not null order by 1,2,3"

        dbCommand = New System.Data.Odbc.OdbcCommand(unvobj, conn)

        Try
            If dbReader.IsClosed = False Then
                dbReader.Close()
            End If
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            Trace.WriteLine("Database Exception: " & ex.ToString)
            Return False
        End Try

        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Try
                    Dim CLASSNAME As String = Trim(dbReader.GetValue(0).ToString())
                    Dim OBJECTNAME As String = Trim(dbReader.GetValue(1).ToString())
                    Dim ORDERNBR As String = Trim(dbReader.GetValue(2).ToString())
                    Dim TYPENAME As String = Trim(dbReader.GetValue(3).ToString())
                    Dim UNIVCLASS As String = Trim(dbReader.GetValue(4).ToString())
                    Dim UNIVOBJECT As String = Trim(dbReader.GetValue(5).ToString())
                    Dim TIMEAGGREGATION As String = Trim(dbReader.GetValue(6).ToString())
                    Dim GROUPAGGREGATION As String = Trim(dbReader.GetValue(7).ToString())

                    Dim UsedClass As String
                    Dim ClassTree() As String
                    Dim UsedObject As String

                    If UNIVOBJECT <> "" Then
                        UsedClass = TYPENAME
                        If UNIVCLASS <> "" Then
                            UsedClass = UNIVCLASS
                            ClassTree = Split(UNIVCLASS, "//")
                            For Count = 0 To UBound(ClassTree)
                                UsedClass = ClassTree(Count)
                            Next Count
                        End If

                        If (TIMEAGGREGATION = GROUPAGGREGATION OrElse GROUPAGGREGATION = "") AndAlso TIMEAGGREGATION <> "NONE" AndAlso TIMEAGGREGATION <> "" Then
                            If TIMEAGGREGATION <> "SUM" Then
                                UsedObject = UNIVOBJECT & " (" & LCase(TIMEAGGREGATION) & ")"
                            Else
                                UsedObject = UNIVOBJECT
                            End If
                        ElseIf (TIMEAGGREGATION = GROUPAGGREGATION OrElse GROUPAGGREGATION = "") AndAlso TIMEAGGREGATION = "NONE" Then
                            UsedObject = UNIVOBJECT & " (" & LCase(TIMEAGGREGATION) & ")"
                        ElseIf TIMEAGGREGATION <> GROUPAGGREGATION AndAlso GROUPAGGREGATION <> "" Then 'If Time Aggregation and Group Aggregation are different
                            Trace.WriteLine("Universe Object '" & UNIVOBJECT & "' in Class '" & UsedClass & "' contains multiple instances (due different aggregation formulas). Using time aggregation formula for Computed Counters")
                            UsedObject = UNIVOBJECT & " (" & LCase(TIMEAGGREGATION) & ")"
                        Else
                        End If
                    Else
                    End If

                    'CLASSNAME::OBJECTNAME::ORDERNRO::TYPENAME::NAME
                    formulaitem = CLASSNAME & "::" & OBJECTNAME & "::" & ORDERNBR & "::" & UsedClass & "::" & UsedObject
                    arrayList.Add(formulaitem)
                Catch ex As Exception
                    Trace.WriteLine("Error reading computed object data from database: " & ex.ToString())
                End Try

            End If
        End While
        dbReader.Close()

        dbCommand.Dispose()

        unvobj = "SELECT DISTINCT a.CLASSNAME," & _
        "a.UNIVERSEEXTENSION," & _
        "a.OBJECTNAME," & _
        "SUBSTR(a.DESCRIPTION,1,8000),SUBSTR(a.DESCRIPTION,8001,8000),SUBSTR(a.DESCRIPTION,16001,8000),SUBSTR(a.DESCRIPTION,24001,8000)" & _
        ",b.OBJECTTYPE," & _
        "b.QUALIFICATION," & _
        "b.AGGREGATION," & _
        "SUBSTR(a.OBJWHERE,1,8000),SUBSTR(a.OBJWHERE,8001,8000),SUBSTR(a.OBJWHERE,16001,8000),SUBSTR(a.OBJWHERE,24001,8000)" & _
        ",a.OBJ_BH_REL," & _
        "a.ELEM_BH_REL," & _
        "b.FORMULA " & _
        "FROM Universecomputedobject a, Universeformulas b ,Universeparameters c " & _
        "WHERE a.VERSIONID='" & TechPackTPIde & "' And (a.CLASSNAME = c.CLASSNAME) And (a.OBJECTNAME = c.OBJECTNAME) " & _
        "And a.Versionid = c.Versionid AND ((b.VERSIONID = a.Versionid or b.VERSIONID='" & BaseTechPackTPIde & "') and a.OBJSELECT = b.NAME)"

        dbCommand = New System.Data.Odbc.OdbcCommand(unvobj, conn)
        dbReader = dbCommand.ExecuteReader()

        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                If Trim(dbReader.GetValue(2).ToString()) <> "" Then
                    Try
                        description = ""
                        objectType = ""
                        qualification = ""
                        aggregation = ""
                        objSelectRead = ""
                        objWhereRead = ""

                        unvcomputedobject = New BOComputedObject
                        addObject = False

                        unvcomputedobject.ClassName = Trim(dbReader.GetValue(0).ToString())
                        unvcomputedobject.UniverseExtension = Trim(dbReader.GetValue(1).ToString())
                        unvcomputedobject.Name = Trim(dbReader.GetValue(2).ToString())
                        If dbReader.IsDBNull(3) = False Then
                            description = Trim(dbReader.GetString(3) + dbReader.GetString(4) + dbReader.GetString(5) + dbReader.GetString(6))
                        Else
                            description = ""
                        End If
                        objectType = LCase(Trim(dbReader.GetValue(7).ToString()))
                        qualification = LCase(Trim(dbReader.GetValue(8).ToString()))
                        aggregation = UCase(Trim(dbReader.GetValue(9).ToString()))

                        If dbReader.IsDBNull(10) = False Then
                            objWhereRead = Trim(dbReader.GetString(10) + dbReader.GetString(11) + dbReader.GetString(12) + dbReader.GetString(13))
                        Else
                            objWhereRead = ""
                        End If
                        unvcomputedobject.ObjectBHRelated = Trim(dbReader.GetValue(14).ToString())
                        unvcomputedobject.ElementBHRelated = Trim(dbReader.GetValue(15).ToString())
                        Formula = dbReader.GetValue(16).ToString()

                        tmp = ""
                    Catch ex As Exception
                        Trace.WriteLine("Error reading computed object data from database: " & ex.ToString())
                    End Try

                    Try
                        For a = 0 To arrayList.Count() - 1
                            formulaitem = arrayList.Item(a)
                            Dim formulaitemList() As String
                            Dim searchNumber As String
                            Dim formulaObject As String
                            Dim c As Integer
                            formulaitemList = Split(formulaitem, "::")
                            'CLASSNAME::OBJECTNAME::ORDERNRO::TYPENAME::NAME
                            If unvcomputedobject.ClassName = formulaitemList(0) AndAlso unvcomputedobject.Name = formulaitemList(1) Then
                                searchNumber = formulaitemList(2)
                                formulaObject = "@Select(" & formulaitemList(3) & "\" & formulaitemList(4) & ")"
                                Formula = Replace(Formula, ":" & searchNumber & ":", formulaObject)
                                If (Formula <> Nothing) Then
                                  If Formula.StartsWith(":0") Then
                                    Formula = formulaObject
                                  End If
                                End If

                            End If
                        Next

                        objSelectRead = Replace(Formula, Chr(34), "'")

                        If unvcomputedobject.UniverseExtension = "all" Then
                            addObject = True
                        ElseIf unvcomputedobject.UniverseExtension = "" AndAlso UniverseNameExtension = "" Then
                            addObject = True
                        Else
                            Dim UniverseCountList() As String
                            Dim UnvCount As Integer
                            If InStrRev(unvcomputedobject.UniverseExtension, ",") = 0 Then
                                If unvcomputedobject.UniverseExtension = UniverseNameExtension Then
                                    addObject = True
                                End If
                            Else
                                UniverseCountList = Split(unvcomputedobject.UniverseExtension, ",")
                                For UnvCount = 0 To UBound(UniverseCountList)
                                    If UniverseCountList(UnvCount) = UniverseNameExtension Then
                                        addObject = True
                                        Exit For
                                    End If
                                Next
                            End If
                        End If

                        If addObject = True Then
                            If unvcomputedobject.ObjectBHRelated = ObjectBHSupport OrElse unvcomputedobject.ElementBHRelated = ElementBHSupport OrElse (unvcomputedobject.ObjectBHRelated = False AndAlso unvcomputedobject.ElementBHRelated = False) Then
                                If InStrRev(objSelectRead, "(DIM_RANKMT)") > 0 Then
                                    For Count = 1 To mts.Count
                                        If mts.Item(Count).RankTable = True Then

                                            Cls = getClass(Univ, unvcomputedobject.ClassName)
                                            If Cls Is Nothing Then
                                                Return True
                                            End If
                                            objName = replaceNaming(tp_name, unvcomputedobject.Name, "(TPNAME)", "")

                                            If mts.Item(Count).ElementBusyHours = True Then
                                                objName = Replace(objName, "(BHObject)", "Element")
                                            End If
                                            If mts.Item(Count).ObjectBusyHours <> "" Then
                                                objName = Replace(objName, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                            End If

                                            Try
                                                Obj = Cls.Objects.Item(objName)
                                            Catch e As Exception
                                                Obj = Cls.Objects.Add(objName, Cls)
                                            Finally
                                                UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"

                                                objSelect = replaceNaming(tp_name, objSelectRead, "(TPNAME)", "")
                                                objSelect = replaceNaming(mts.Item(Count).TypeName, objSelect, "(DIM_RANKMT)", "DIM_")

                                                objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")
                                                objWhere = replaceNaming(mts.Item(Count).TypeName, objWhere, "(DIM_RANKMT)", "DIM_")

                                                setObjectParams(Obj, objSelect, objWhere, description, objectType, qualification, aggregation)
                                            End Try
                                            If ParseObject(Obj, Cls) = False Then
                                                Return False
                                            End If
                                        End If
                                    Next Count
                                ElseIf (unvcomputedobject.ObjectBHRelated = True AndAlso ObjectBHSupport = False) AndAlso (unvcomputedobject.ElementBHRelated = True AndAlso ElementBHSupport = False) Then
                                    'Do nothing
                                ElseIf InStrRev(objSelectRead, "(ELEMENTRANKMT)") > 0 Then
                                    For Count = 1 To mts.Count
                                        If mts.Item(Count).RankTable = True AndAlso mts.Item(Count).ElementBusyHours = True Then

                                            Cls = getClass(Univ, unvcomputedobject.ClassName)
                                            If Cls Is Nothing Then
                                                Return True
                                            End If
                                            objName = replaceNaming(tp_name, unvcomputedobject.Name, "(TPNAME)", "")

                                            If mts.Item(Count).ElementBusyHours = True Then
                                                objName = Replace(objName, "(BHObject)", "Element")
                                            End If

                                            Try
                                                Obj = Cls.Objects.Item(objName)
                                            Catch e As Exception
                                                Obj = Cls.Objects.Add(objName, Cls)
                                            Finally
                                                UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                                objSelect = replaceNaming(tp_name, objSelectRead, "(TPNAME)", "")
                                                objSelect = Replace(objSelect, "(ELEMENTRANKMT)", mts.Item(Count).TypeName & "_RANKBH")

                                                objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")
                                                objWhere = Replace(objWhere, "(ELEMENTRANKMT)", mts.Item(Count).TypeName & "_RANKBH")

                                                setObjectParams(Obj, objSelect, objWhere, description, objectType, qualification, aggregation)
                                            End Try
                                            If ParseObject(Obj, Cls) = False Then
                                                Return False
                                            End If
                                        End If
                                    Next Count
                                ElseIf InStrRev(objSelectRead, "(TPRELEASE)") > 0 Then
                                    Cls = getClass(Univ, unvcomputedobject.ClassName)
                                    If Cls Is Nothing Then
                                        Return True
                                    End If

                                    objName = replaceNaming(tp_name, unvcomputedobject.Name, "(TPNAME)", "")

                                    Try
                                        Obj = Cls.Objects.Item(objName)
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add(objName, Cls)
                                    Finally
                                        UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                        objSelect = Replace(objSelectRead, "(TPRELEASE)", tp_name & " " & tp_release)
                                        If Obj.Name = "TP Version" AndAlso Cls.Name = "General" Then
                                            objSelect = "'" & objSelect & "'"
                                            Obj.Tables.Add("DC.DIM_DATE")
                                        End If
                                        objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")

                                        setObjectParams(Obj, objSelect, objWhere, description, objectType, qualification, aggregation)
                                    End Try
                                    If ParseObject(Obj, Cls) = False Then
                                        Return False
                                    End If
                                Else
                                    Cls = getClass(Univ, unvcomputedobject.ClassName)
                                    If (Cls Is Nothing Or unvcomputedobject.Name Is Nothing) Then
                                        Trace.WriteLine("Error adding computed object, class or object name not defined.")
                                        Console.WriteLine("Error adding computed object, class or object name not defined.")
                                        Return True
                                    End If

                                    objName = replaceNaming(tp_name, unvcomputedobject.Name, "(TPNAME)", "")
                                    If (objName Is Nothing OrElse objName = "") Then
                                        Trace.WriteLine("Error adding computed object: " & unvcomputedobject.Name)
                                        Console.WriteLine("Error adding computed object: " & unvcomputedobject.Name)
                                        Return True
                                    Else
                                        Try
                                            Obj = Cls.Objects.Item(objName)
                                        Catch e As Exception
                                            Obj = Cls.Objects.Add(objName, Cls)
                                        Finally
                                            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                            objSelect = replaceNaming(tp_name, objSelectRead, "(TPNAME)", "")

                                            objWhere = replaceNaming(tp_name, objWhereRead, "(TPNAME)", "")

                                            setObjectParams(Obj, objSelect, objWhere, description, objectType, qualification, aggregation)

                                        End Try
                                        If ParseObject(Obj, Cls) = False Then
                                            Return False
                                        End If
                                    End If
                                End If
                            End If
                        End If
                    Catch ex As Exception
                        Trace.WriteLine("Error adding computed object to universe: " & ex.ToString())
                    End Try
                End If
            End If
        End While

        Try
            dbReader.Close()
        Catch ex As Exception
            Trace.WriteLine("ComputedObjects addObjects: " & ex.ToString)
        End Try



        While (dbReader.IsClosed = False)
            dbReader.Close()
            System.Threading.Thread.Sleep(10000)

        End While

        dbCommand.Dispose()
        Return True

    End Function

    Public Function replaceNaming(ByRef techpackName As String, ByRef Expression As String, ByVal Find As String, ByVal Replacement As String) As String
        Dim returnValue As String
        returnValue = ""
        If (techpackName.StartsWith("DC_")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "DC_", Replacement))
        ElseIf (techpackName.StartsWith("PM_")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "PM_", Replacement))
        ElseIf (techpackName.StartsWith("CM_")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "CM_", Replacement))
        ElseIf (techpackName.StartsWith("CUSTOM_")) Then
            returnValue = Replace(Expression, Find, Replace(techpackName, "CUSTOM_", Replacement))
        Else
            ' Return Expression
            returnValue = Expression
        End If
        Return returnValue
    End Function

    Public Function addObject(ByRef Univ As Designer.Universe, ByRef univ_class As String, ByRef univ_object As String, ByRef objType As String, ByRef objSelect As String, ByRef description As String) As Boolean

        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim objName As String
        Dim objWhere As String

        Cls = getClass(Univ, univ_class)

        If Cls Is Nothing Then
            Trace.WriteLine("Class Failure: Class='" & univ_class & "', Object='" & univ_object & _
                "', Description='" & description & "', Type='" & objType & "'.")
            Return True
        End If

        Try
            Obj = Cls.Objects.Item(univ_object)
        Catch e As Exception
            Obj = Cls.Objects.Add(univ_object, Cls)
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
    Public Function removeComputedObject(ByRef Cls As Designer.Class, ByRef cnt_name As String, ByRef cnts As CountersTPIde) As Designer.Object
        Dim Obj As Designer.Object
        Dim x As Integer
        Dim y As Integer
        Dim objectNameInDatabase As String
        Dim objectNameInUniverse As String
        Dim UnvCntAmount As Integer
        Dim MeasCntAmount As Integer

        UnvCntAmount = Cls.Objects.Count()
        MeasCntAmount = cnts.Count()

        If (MeasCntAmount = 0) Then
            For y = UnvCntAmount To 1 Step -1
                objectNameInUniverse = Cls.Objects(y).Name
                If ((objectNameInUniverse <> "data_coverage") And (objectNameInUniverse <> "period_duration")) Then
                    Cls.Objects.Item(y).Delete()
                End If
            Next
        Else

            Dim RemoveItem As Boolean
            For y = UnvCntAmount To 1 Step -1
                RemoveItem = True
                objectNameInUniverse = Cls.Objects(y).Name
                If ((objectNameInUniverse = "data_coverage") Or (objectNameInUniverse = "period_duration")) Then
                    RemoveItem = False
                Else
                    For x = 1 To MeasCntAmount
                        objectNameInDatabase = cnts.Item(x).CounterName.ToString()
                        If (objectNameInDatabase = objectNameInUniverse) Then
                            RemoveItem = False
                            Exit For
                        End If
                    Next x
                End If
                'Universe object is not defined anymore so it must be removed
                If (RemoveItem = True) Then
                    Cls.Objects.Item(y).Delete()
                End If
            Next y

            'For y = 1 To (Cls.Objects.Count() - (Cls.Objects.Count() - cnts.Count()))
            'found = False
            'For x = 1 To cnts.Count()
            'aa = cnts.Item(x).CounterName.ToString()
            'bb = Cls.Objects(y).Name
            'If (cnts.Item(x).UnivObject = Cls.Objects(y).Name) Then
            'If (aa = bb) Then
            'found = True
            'Exit For
            'End If
            'Next
            'If (found = False) Then ' And ((Cls.Objects.Count() - 1) > y)
            'Cls.Objects.Item(y).Delete()
            'End If
            'Next y
        End If

        Return Obj

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
    Sub setObjectParams(ByRef Obj As Designer.Object, ByRef objSelect As String, ByRef objWhere As String, _
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
            Obj.Format.NumberFormat = formatObject(Obj)

            Obj.HasListOfValues = False
            Obj.AllowUserToEditLov = False
            Obj.AutomaticLovRefreshBeforeUse = False
            Obj.ExportLovWithUniverse = False
        Catch ex As Exception
            Trace.WriteLine("Error in BOComputedObjectTPIde.setObjectParams: " & ex.ToString())
        Catch nullException As System.NullReferenceException
            Trace.WriteLine("Null exception in BOComputedObjectTPIde.setObjectParams: " & nullException.ToString)
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
    Sub setReferenceObjectParams(ByRef Obj As Designer.Object, ByRef objSelect As String, ByRef objWhere As String, _
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
            Trace.WriteLine("Error in BOComputedObjectTPIde.setObjectParams: " & ex.ToString())
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
    Public Function ParseObject(ByRef Obj As Designer.Object, ByRef Cls As Designer.Class) As Boolean
        Dim Result As MsgBoxResult
        If ObjectParse = True Then
            Try
                Obj.Parse()
            Catch ex As Exception
                Trace.WriteLine("Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
                Trace.WriteLine("Object Parse Exception: " & ex.ToString)
            End Try
        End If
        Return True
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
    Public Sub setObjectType(ByRef DefinedType As String, ByRef Obj As Designer.Object)

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
    Public Sub setQualificationAndAggregation(ByRef Qualification As String, ByRef DefinedAggregation As String, ByRef Obj As Designer.Object)

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
            Obj.AggregateFunction = Designer.DsObjectAggregate.dsAggregateByNullObject
            Trace.WriteLine("Aggregation must be set for object '" & Obj.Name & "' in class '" & Obj.RootClass.Name & "'. Defaulting to None.")
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
    Public Function formatObject(ByRef Obj As Designer.Object) As String
        If Obj.Type = Designer.DsObjectType.dsNumericObject Then
            'Format = getNumberFormatWithDatascale(Obj, cnt.Datascale)
        End If


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
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt As CountersTPIde.Counter, ByRef cnt_name As String, ByRef Counter As Boolean, ByRef AggrFunc As String) As Designer.Object
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

End Class
