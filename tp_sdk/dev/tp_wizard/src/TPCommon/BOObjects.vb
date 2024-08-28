Option Strict Off


''
'  BOObjects class is a collection of BOObject classes
'
Public Class BOObjects
    Private _objects As System.Collections.ArrayList = New System.Collections.ArrayList
    Public ObjectParse As Boolean

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
    '  Gets BOObject class from BOObjects class based on given index.
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
                If LCase(Trim(Value)) = "x" Then
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
                If LCase(Trim(Value)) = "x" Then
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
    ''
    ' Adds extra object to universe. 
    '
    ' @param tp_name Specifies name of tech pack
    ' @param Univ Specifies reference to universe
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Objects are defined in TP definition's sheet 'Universe objects'.
    Public Function addObjects(ByRef tp_name As String, ByRef tp_release As String, ByRef Univ As Object, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef mts As MeasurementTypes, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef logs As logMessages, ByRef UniverseNameExtension As String) As Boolean

        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim objName As String
        Dim objSelect As String
        Dim objWhere As String
        Dim Count As Integer
        Dim unvobject As BOObject
        Dim addObject As Boolean

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Universe objects$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                If Trim(dbReader.GetValue(1).ToString()) <> "" Then
                    unvobject = New BOObject
                    addObject = False
                    unvobject.ClassName = dbReader.GetValue(0).ToString()
                    unvobject.Name = dbReader.GetValue(1).ToString()

                    If dbReader.FieldCount < 10 Then
                        unvobject.UniverseExtension = ""
                    Else
                        unvobject.UniverseExtension = dbReader.GetValue(9).ToString()
                    End If
                    If dbReader.FieldCount < 11 Then
                        unvobject.ObjectBHRelated = False
                    Else
                        unvobject.ObjectBHRelated = dbReader.GetValue(10).ToString()
                    End If
                    If dbReader.FieldCount < 12 Then
                        unvobject.ElementBHRelated = False
                    Else
                        unvobject.ElementBHRelated = dbReader.GetValue(11).ToString()
                    End If


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

                    If addObject = True Then
                        If unvobject.ObjectBHRelated = ObjectBHSupport OrElse unvobject.ElementBHRelated = ElementBHSupport OrElse (unvobject.ObjectBHRelated = False AndAlso unvobject.ElementBHRelated = False) Then
                            If InStrRev(dbReader.GetValue(6).ToString(), "(DIM_RANKMT)") > 0 Then
                                For Count = 1 To mts.Count
                                    If mts.Item(Count).RankTable = True Then

                                        Cls = getClass(Univ, unvobject.ClassName, logs)
                                        If Cls Is Nothing Then
                                            Return True
                                        End If
                                        objName = Replace(unvobject.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))
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
                                            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                            objSelect = Replace(dbReader.GetValue(6).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                            objSelect = Replace(objSelect, "(DIM_RANKMT)", Replace(mts.Item(Count).MeasurementTypeID, "DC_", "DIM_"))
                                            objWhere = Replace(dbReader.GetValue(7).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                            objWhere = Replace(objWhere, "(DIM_RANKMT)", Replace(mts.Item(Count).MeasurementTypeID, "DC_", "DIM_"))
                                            setObjectParams(Obj, objSelect, objWhere, dbReader.GetValue(2).ToString(), dbReader.GetValue(3).ToString(), dbReader.GetValue(4).ToString(), dbReader.GetValue(5).ToString(), logs)
                                        End Try
                                        If ParseObject(Obj, Cls, logs) = False Then
                                            Return False
                                        End If
                                    End If
                                Next Count                              
                            ElseIf unvobject.ObjectBHRelated = True AndAlso ObjectBHSupport = False OrElse unvobject.ElementBHRelated = True AndAlso ElementBHSupport = False Then
                                'Do nothing
                            ElseIf InStrRev(dbReader.GetValue(6).ToString(), "(ELEMENTRANKMT)") > 0 Then
                                For Count = 1 To mts.Count
                                    If mts.Item(Count).RankTable = True AndAlso mts.Item(Count).ElementBusyHours = True Then

                                        Cls = getClass(Univ, unvobject.ClassName, logs)
                                        If Cls Is Nothing Then
                                            Return True
                                        End If
                                        objName = Replace(unvobject.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                        If mts.Item(Count).ElementBusyHours = True Then
                                            objName = Replace(objName, "(BHObject)", "Element")
                                        End If

                                        Try
                                            Obj = Cls.Objects.Item(objName)
                                        Catch e As Exception
                                            Obj = Cls.Objects.Add(objName, Cls)
                                        Finally
                                            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                            objSelect = Replace(dbReader.GetValue(6).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                            objSelect = Replace(objSelect, "(ELEMENTRANKMT)", mts.Item(Count).MeasurementTypeID & "_RANKBH")
                                            objWhere = Replace(dbReader.GetValue(7).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                            objWhere = Replace(objWhere, "(ELEMENTRANKMT)", mts.Item(Count).MeasurementTypeID & "_RANKBH")
                                            setObjectParams(Obj, objSelect, objWhere, dbReader.GetValue(2).ToString(), dbReader.GetValue(3).ToString(), dbReader.GetValue(4).ToString(), dbReader.GetValue(5).ToString(), logs)
                                        End Try
                                        If ParseObject(Obj, Cls, logs) = False Then
                                            Return False
                                        End If
                                    End If
                                Next Count
                            ElseIf InStrRev(dbReader.GetValue(6).ToString(), "(TPRELEASE)") > 0 Then
                                Cls = getClass(Univ, unvobject.ClassName, logs)
                                If Cls Is Nothing Then
                                    Return True
                                End If
                                objName = Replace(unvobject.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))

                                Try
                                    Obj = Cls.Objects.Item(objName)
                                Catch e As Exception
                                    Obj = Cls.Objects.Add(objName, Cls)
                                Finally
                                    UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    objSelect = Replace(dbReader.GetValue(6).ToString(), "(TPRELEASE)", tp_name & " " & tp_release)
                                    If Obj.Name = "TP Version" AndAlso Cls.Name = "General" Then
                                        objSelect = "'" & objSelect & "'"
                                        Obj.Tables.Add("DC.DIM_DATE")
                                    End If
                                    objWhere = Replace(dbReader.GetValue(7).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                    setObjectParams(Obj, objSelect, objWhere, dbReader.GetValue(2).ToString(), dbReader.GetValue(3).ToString(), dbReader.GetValue(4).ToString(), dbReader.GetValue(5).ToString(), logs)
                                End Try
                                If ParseObject(Obj, Cls, logs) = False Then
                                    Return False
                                End If
                            Else
                                Cls = getClass(Univ, unvobject.ClassName, logs)
                                If Cls Is Nothing Then
                                    Return True
                                End If
                                objName = Replace(unvobject.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                Try
                                    Obj = Cls.Objects.Item(objName)
                                Catch e As Exception
                                    Obj = Cls.Objects.Add(objName, Cls)
                                Finally
                                    UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    objSelect = Replace(dbReader.GetValue(6).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                    objWhere = Replace(dbReader.GetValue(7).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                                    setObjectParams(Obj, objSelect, objWhere, dbReader.GetValue(2).ToString(), dbReader.GetValue(3).ToString(), dbReader.GetValue(4).ToString(), dbReader.GetValue(5).ToString(), logs)
                                End Try
                                If ParseObject(Obj, Cls, logs) = False Then
                                    Return False
                                End If
                            End If
                        End If
                    End If
                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        Return True

    End Function

    Public Function addObject(ByRef Univ As Designer.Universe, ByRef univ_class As String, ByRef univ_object As String, ByRef objType As String, ByRef objSelect As String, ByRef description As String, ByRef logs As logMessages) As Boolean

        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim objName As String
        Dim objWhere As String

        Cls = getClass(Univ, univ_class, logs)

        If Cls Is Nothing Then
            logs.AddLogText("Class Failure: Class='" & univ_class & "', Object='" & univ_object & _
                "', Description='" & description & "', Type='" & objType & "'.")
            Return True
        End If

        Try
            Obj = Cls.Objects.Item(univ_object)
        Catch e As Exception
            Obj = Cls.Objects.Add(univ_object, Cls)
        Finally
            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            objWhere = ""
            If Obj Is Nothing Then
                logs.AddLogText("Object Failure: Class='" & univ_class & "', Object='" & univ_object & _
                "', Description='" & description & "', Type='" & objType & "'.")
            Else
                setReferenceObjectParams(Obj, objSelect, objWhere, description, objType, "dimension", "", logs)
            End If
        End Try
        If ParseObject(Obj, Cls, logs) = False Then
            Return False
        End If
        Return True

    End Function


    Sub setObjectParams(ByRef Obj As Designer.Object, ByRef objSelect As String, ByRef objWhere As String, _
    ByRef description As String, ByRef type As String, ByRef qualification As String, ByRef aggregation As String, ByRef logs As logMessages)
        Try
            Obj.Description = description
            setObjectType(type, Obj, logs)
            setQualificationAndAggregation(qualification, aggregation, Obj, logs)
            Obj.Select = objSelect
            Obj.Format.NumberFormat = formatObject(Obj)
            Obj.Where = objWhere
            Obj.HasListOfValues = True
            Obj.AllowUserToEditLov = True
            Obj.AutomaticLovRefreshBeforeUse = False
            Obj.ExportLovWithUniverse = False
        Catch ex As Exception
            logs.AddLogText("setObjectParams: Obj='" & Obj.Name & "', Select='" & objWhere & _
            "', Where='" & objWhere & "', Description='" & description & _
            "', Type='" & type & "', Qualification='" & qualification & "', Aggregation='" & aggregation & "'.")
            logs.AddLogText("setObjectParams Exception: " & ex.ToString)
        End Try

    End Sub
    Sub setReferenceObjectParams(ByRef Obj As Designer.Object, ByRef objSelect As String, ByRef objWhere As String, _
    ByRef description As String, ByRef type As String, ByRef qualification As String, ByRef aggregation As String, ByRef logs As logMessages)
        Try
            Obj.Description = description
            setObjectType(type, Obj, False, logs)
            setQualificationAndAggregation(qualification, aggregation, Obj, logs)
            Obj.Select = objSelect
            Obj.Format.NumberFormat = formatObject(Obj)
            Obj.Where = objWhere
            Obj.HasListOfValues = True
            Obj.AllowUserToEditLov = True
            Obj.AutomaticLovRefreshBeforeUse = False
            Obj.ExportLovWithUniverse = False
        Catch ex As Exception
            logs.AddLogText("setReferenceObjectParams: Obj='" & Obj.Name & "', Select='" & objWhere & _
            "', Where='" & objWhere & "', Description='" & description & _
            "', Type='" & type & "', Qualification='" & qualification & "', Aggregation='" & aggregation & "'.")
            logs.AddLogText("setReferenceObjectParams Exception: " & ex.ToString)
        End Try


    End Sub
    Public Function ParseCounterObject(ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, ByRef logs As logMessages) As Integer
        Dim Result As MsgBoxResult
        Try
            Obj.Parse()
        Catch ex As Exception
            logs.AddLogText("Counter Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
            logs.AddLogText("Counter Object Parse Exception: " & ex.ToString)
        End Try
        Return 0
    End Function
    Public Function ParseReferenceObject(ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, ByRef logs As logMessages) As Integer
        Dim Result As MsgBoxResult
        Try
            Obj.Parse()
        Catch ex As Exception
            logs.AddLogText("Reference Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
            logs.AddLogText("Reference Object Parse Exception: " & ex.ToString)
        End Try
        Return 0
    End Function
    Public Function ParseObject(ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, ByRef logs As logMessages) As Boolean
        Dim Result As MsgBoxResult
        If ObjectParse = True Then
            Try
                Obj.Parse()
            Catch ex As Exception
                logs.AddLogText("Object Parse failed for '" & Cls.Name & "/" & Obj.Name & "' with Select clause '" & Obj.Select & "'.")
                logs.AddLogText("Object Parse Exception: " & ex.ToString)
            End Try
        End If
        Return True
    End Function
    Function getClass(ByRef Univ As Object, ByRef classname As String, ByRef logs As logMessages) As Designer.Class

        Dim Cls As Designer.Class
        Try
            Cls = Univ.Classes.FindClass(classname)
        Catch e As Exception
            logs.AddLogText("Class '" & classname & "' is not found. Add class to TP Definition.")
            logs.AddLogText("Class Exception: " & e.ToString)
        End Try
        Return Cls
    End Function
    ''
    ' Sets type for universe object. 
    '
    ' @param DefinedType Specifies defined type for object
    ' @param Obj Specifies reference to object
    Public Sub setObjectType(ByRef DefinedType As String, ByRef Obj As Designer.Object, ByRef logs As logMessages)

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
    Public Sub setQualificationAndAggregation(ByRef Qualification As String, ByRef DefinedAggregation As String, ByRef Obj As Designer.Object, ByRef logs As logMessages)

        If LCase(Qualification) = "measure" Then
            Obj.Qualification = Designer.DsObjectQualification.dsMeasureObject
            setAggregateFunction(DefinedAggregation, Obj, logs)
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
    Public Sub setAggregateFunction(ByRef DefinedAggregation As String, ByRef Obj As Designer.Object, ByRef logs As logMessages)

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
            logs.AddLogText("Aggregation must be set for object '" & Obj.Name & "' in class ''.")
        End If

    End Sub
    ''
    ' Sets type for universe object. 
    '
    ' @param DataType Specifies data type of object
    ' @param Obj Specifies reference to object
    ' @param Counter Specifies if object is counter or reference object. If value is True, object is counter object.
    Public Sub setObjectType(ByRef DataType As String, ByRef Obj As Designer.Object, ByRef Counter As Boolean, ByRef logs As logMessages)
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
            logs.AddLogText("setObjectType: Obj='" & Obj.Name & "', Data type='" & DataType & "', Counter='" & Counter & "'.")
            logs.AddLogText("setObjectType Exception: " & ex.ToString)
        End Try

    End Sub

    ''
    ' Sets default formatting for universe object. 
    '
    ' @param Obj Specifies reference to object
    ' @return Formatting mask
    Public Function formatObject(ByRef Obj As Designer.Object) As String

        'If Obj.Type = Designer.DsObjectType.dsDateObject Then
        'Return "yyyy-mm-dd"
        'End If
        If Obj.Type = Designer.DsObjectType.dsNumericObject Then
            Return "0;-0;0"
        End If

    End Function

    ''
    ' Adds an object to specified class. If object already exists, it is selected. 
    '
    ' @param Obj Specifies reference to object's class
    ' @param cnt Specifies reference to counter class
    ' @return Reference to object mask
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt As Counters.Counter, ByRef Counter As Boolean, ByRef AggrFunc As String, ByRef logs As logMessages) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(cnt.UnivObject)
        Catch e As Exception
            Obj = Cls.Objects.Add(cnt.UnivObject, Cls)
        Finally
            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = cnt.Description
            setObjectType(cnt.Datatype, Obj, Counter, logs)
            setAggregateFunction(AggrFunc, Obj, logs)
        End Try
        Return Obj

    End Function

    ''
    ' Adds an object to specified class. If object already exists, it is selected. 
    '
    ' @param Obj Specifies reference to object's class
    ' @param cnt Specifies reference to counter class
    ' @return Reference to object mask
    Public Function addObject(ByRef Cls As Designer.Class, ByRef rd As ReferenceDatas.ReferenceData, ByRef Counter As Boolean, ByRef logs As logMessages) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(rd.UnivObject)
        Catch e As Exception
            Obj = Cls.Objects.Add(rd.UnivObject, Cls)
        Finally
            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = rd.Description
            setObjectType(rd.Datatype, Obj, Counter, logs)
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
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt As Counters.Counter, ByRef cnt_name As String, ByRef Counter As Boolean, ByRef AggrFunc As String, ByRef logs As logMessages) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(cnt_name)
        Catch e As Exception
            Obj = Cls.Objects.Add(cnt_name, Cls)
        Finally
            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
            Obj.Description = cnt.Description
            setObjectType(cnt.Datatype, Obj, Counter, logs)
            setAggregateFunction(AggrFunc, Obj, logs)
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
    Public Function addObject(ByRef Cls As Designer.Class, ByRef cnt_name As String, ByRef objType As Designer.DsObjectType, ByRef objQual As Designer.DsObjectQualification, ByRef objAggr As Designer.DsObjectAggregate, ByRef description As String, ByRef logs As logMessages) As Designer.Object
        Dim Obj As Designer.Object

        Try
            Obj = Cls.Objects.Item(cnt_name)
        Catch e As Exception
            Obj = Cls.Objects.Add(cnt_name, Cls)
        Finally
            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
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
    Public Function keyFormat(ByRef obj As Designer.Object, ByRef cnt_key As CounterKeys.CounterKey, ByRef logs As logMessages)
        Dim bo_objects As New BOObjects


        Try
            obj.Description = cnt_key.Description
            bo_objects.setObjectType(cnt_key.Datatype, obj, False, logs)
            obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
            obj.Format.NumberFormat = bo_objects.formatObject(obj)
            obj.HasListOfValues = True
            obj.AllowUserToEditLov = True
            obj.AutomaticLovRefreshBeforeUse = False
            obj.ExportLovWithUniverse = False
        Catch ex As Exception
            logs.AddLogText("Key Format Error: " & ex.Message)
        End Try

    End Function

    ''
    ' Sets object's key formatting. 
    '
    ' @param obj Specifies reference to object's class
    ' @param CMTechPack Specifies whether technology package is CM package
    ' @return Data formatting string
    Public Function formatCounterObject(ByRef obj As Designer.Object, ByRef CMTechPack As Boolean) As String

        'If obj.Type = Designer.DsObjectType.dsDateObject Then
        'Return "yyyy-mm-dd"
        'End If
        If obj.Type = Designer.DsObjectType.dsNumericObject Then
            If CMTechPack = True Then
                Return "0;-0;0"
            Else
                'Return "0.00;-0.00;0.00"
                Return "0,00;-0,00;0,00"
            End If

        End If

    End Function
End Class
