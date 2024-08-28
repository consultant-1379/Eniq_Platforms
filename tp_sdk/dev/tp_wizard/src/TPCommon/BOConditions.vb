Option Strict Off

''
'  BOConditions class is a collection of BOCondition classes
'
Public Class BOConditions
    Private _conditions As System.Collections.ArrayList = New System.Collections.ArrayList
    Public ConditionParse As Boolean

    ''
    '  Gets count of BOCondition classes in BOConditions class
    '
    ' @param Index Specifies the index in the BOConditions class
    ' @return Count of BOCondition classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _conditions Is Nothing) Then
                Return _conditions.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOCondition class from BOConditions class based on given index.
    '
    ' @param Index Specifies the index in the BOConditions class
    ' @return Reference to BOCondition
    Public ReadOnly Property Item(ByVal Index As Integer) As BOCondition
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_conditions.Item(Index - 1), BOCondition)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOCondition class to BOConditions class
    '
    ' @param ValueIn Specifies reference to BOCondition
    Public Sub AddItem(ByVal ValueIn As BOCondition)

        If (Not _conditions Is Nothing) Then
            _conditions.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOCondition class defines conditions for universe.
    '
    Public Class BOCondition
        Private m_ClassName As String
        Private m_OldClassName As String
        Private m_Name As String
        Private m_OldName As String
        Private m_Description As String
        Private m_Path As String
        Private m_Table As String
        Private m_Header As String
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
        ' ClassName defines condition's class name.
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
        ' OldClassName defines condition's previous class name.
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
        ' Level defines condition's class level.
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
        ' Name defines condition's name.
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
        ' OldName defines condition's previous name.
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
        ' Path defines condition's path.
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
        ' Table defines condition's table.
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
        ' Header defines condition's header.
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

    End Class
    ''
    ' Adds extra condition to universe. 
    '
    ' @param tp_name Specifies name of tech pack
    ' @param Univ Specifies reference to universe
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Conditions are defined in TP definition's sheet 'Universe conditions'.

    Public Function addConditions(ByRef tp_name As String, ByRef Univ As Designer.Universe, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef mts As MeasurementTypes, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef logs As logMessages, ByRef UniverseNameExtension As String) As Boolean

        Dim Cond As Designer.PredefinedCondition
        Dim Obj As Designer.Object
        Dim Cls As Designer.Class
        Dim Count As Integer

        Dim condName As String
        Dim objName As String
        Dim condWhere As String
        Dim unvcondition As BOCondition
        Dim addcondition As Boolean

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Universe conditions$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                If dbReader.GetValue(1).ToString() <> "" Then
                    unvcondition = New BOCondition
                    addcondition = False
                    unvcondition.ClassName = dbReader.GetValue(0).ToString()
                    unvcondition.Name = dbReader.GetValue(1).ToString()

                    If unvcondition.ClassName.Length > 35 Then
                        logs.AddLogText("Universe Class '" & unvcondition.ClassName & "' for Condition '" & unvcondition.Name & "' exceeds maximum of 35 characters.")
                    End If
                    If unvcondition.Name.Length > 35 Then
                        logs.AddLogText("Universe Condition '" & unvcondition.Name & "' exceeds maximum of 35 characters.")
                    End If

                    If dbReader.FieldCount < 11 Then
                        unvcondition.UniverseExtension = ""
                    Else
                        unvcondition.UniverseExtension = dbReader.GetValue(10).ToString()
                    End If
                    If dbReader.FieldCount < 12 Then
                        unvcondition.ObjectBHRelated = False
                    Else
                        unvcondition.ObjectBHRelated = dbReader.GetValue(11).ToString()
                    End If
                    If dbReader.FieldCount < 13 Then
                        unvcondition.ElementBHRelated = False
                    Else
                        unvcondition.ElementBHRelated = dbReader.GetValue(12).ToString()
                    End If

                    If unvcondition.UniverseExtension = "all" Then
                        addcondition = True
                    ElseIf unvcondition.UniverseExtension = "" AndAlso UniverseNameExtension = "" Then
                        addcondition = True
                    Else
                        Dim UniverseCountList() As String
                        Dim UnvCount As Integer
                        If InStrRev(unvcondition.UniverseExtension, ",") = 0 Then
                            If unvcondition.UniverseExtension = UniverseNameExtension Then
                                addcondition = True
                            End If
                        Else
                            UniverseCountList = Split(unvcondition.UniverseExtension, ",")
                            For UnvCount = 0 To UBound(UniverseCountList)
                                If UniverseCountList(UnvCount) = UniverseNameExtension Then
                                    addcondition = True
                                    Exit For
                                End If
                            Next
                        End If
                    End If

                    If addcondition = True Then
                        If unvcondition.ObjectBHRelated = ObjectBHSupport OrElse unvcondition.ElementBHRelated = ElementBHSupport OrElse (unvcondition.ObjectBHRelated = False AndAlso unvcondition.ElementBHRelated = False) Then
                            If InStrRev(unvcondition.Name, "(BHObject)") > 0 Then
                                For Count = 1 To mts.Count
                                    If mts.Item(Count).RankTable = True Then
                                        condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                        If mts.Item(Count).ElementBusyHours = True Then
                                            condName = Replace(condName, "(BHObject)", "Element")
                                        End If
                                        If mts.Item(Count).ObjectBusyHours <> "" Then
                                            condName = Replace(condName, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                        End If

                                        Cls = getClass(Univ, unvcondition.ClassName)
                                        If Cls Is Nothing Then
                                            logs.AddLogText("Condition '" & condName & "' generation error: Class '" & unvcondition.ClassName & "' not found.")
                                            'Return False
                                        End If

                                        Try
                                            Cond = Cls.PredefinedConditions(condName)
                                            UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                        Catch e As Exception
                                            Cond = Cls.PredefinedConditions.Add(condName, Cls)
                                            UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                        End Try
                                        If LCase(dbReader.GetValue(4).ToString()) = "x" Then
                                            condWhere = "@Select(" & dbReader.GetValue(5).ToString() & "\" & dbReader.GetValue(6).ToString() & ")"
                                            condWhere &= setSelectMode(dbReader.GetValue(8).ToString())
                                            condWhere &= "@Prompt('" & dbReader.GetValue(7).ToString() & ":','"
                                            Try
                                                If mts.Item(Count).ElementBusyHours = True Then
                                                    objName = Replace(dbReader.GetValue(6).ToString(), "(BHObject)", "Element")
                                                End If
                                                If mts.Item(Count).ObjectBusyHours <> "" Then
                                                    objName = Replace(dbReader.GetValue(6).ToString(), "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                                End If
                                                Obj = Cls.Objects(objName)
                                            Catch e As Exception
                                                logs.AddLogText("Condition '" & condName & "' generation error: Object '" & objName & "' not found in class '" & Cls.Name & "'.")
                                                Return False
                                            End Try
                                            condWhere &= setPromptType(Obj)
                                            condWhere &= "','" & dbReader.GetValue(5).ToString() & "\" & dbReader.GetValue(6).ToString() & "',"
                                            condWhere &= setListMode(dbReader.GetValue(8).ToString())
                                            condWhere &= setTextMode(dbReader.GetValue(9).ToString())
                                        Else
                                            condWhere = dbReader.GetValue(3).ToString()
                                        End If

                                        condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                        If mts.Item(Count).ElementBusyHours = True Then
                                            condWhere = Replace(condWhere, "(BHObject)", "Element")
                                        End If
                                        If mts.Item(Count).ObjectBusyHours <> "" Then
                                            condWhere = Replace(condWhere, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                        End If

                                        Cond.Description = dbReader.GetValue(2).ToString()
                                        Cond.Where = condWhere
                                        If ParseCondition(Cond, Cls, logs) = False Then
                                            Return False
                                        End If

                                    End If
                                Next Count

                            ElseIf unvcondition.ObjectBHRelated = True AndAlso ObjectBHSupport = False OrElse unvcondition.ElementBHRelated = True AndAlso ElementBHSupport = False Then
                                'Do nothing
                            Else
                                condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))

                                Cls = getClass(Univ, unvcondition.ClassName)
                                If Cls Is Nothing Then
                                    logs.AddLogText("Condition '" & condName & "' generation error: Class '" & unvcondition.ClassName & "' not found.")
                                    'Return False
                                Else
                                    Try
                                        Cond = Cls.PredefinedConditions(condName)
                                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                    Catch e As Exception
                                        Cond = Cls.PredefinedConditions.Add(condName, Cls)
                                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                    End Try
                                    If LCase(dbReader.GetValue(4).ToString()) = "x" Then
                                        condWhere = "@Select(" & dbReader.GetValue(5).ToString() & "\" & dbReader.GetValue(6).ToString() & ")"
                                        condWhere &= setSelectMode(dbReader.GetValue(8).ToString())
                                        condWhere &= "@Prompt('" & dbReader.GetValue(7).ToString() & ":','"
                                        Try
                                            Obj = Cls.Objects(dbReader.GetValue(6).ToString())
                                        Catch e As Exception
                                            logs.AddLogText("Condition '" & condName & "' generation error: Object '" & dbReader.GetValue(6).ToString() & "' not found in class '" & Cls.Name & "'.")
                                            Return False
                                        End Try
                                        condWhere &= setPromptType(Obj)
                                        condWhere &= "','" & dbReader.GetValue(5).ToString() & "\" & dbReader.GetValue(6).ToString() & "',"
                                        condWhere &= setListMode(dbReader.GetValue(8).ToString())
                                        condWhere &= setTextMode(dbReader.GetValue(9).ToString())
                                    Else
                                        condWhere = dbReader.GetValue(3).ToString()
                                    End If

                                    condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                    Cond.Description = dbReader.GetValue(2).ToString()
                                    Cond.Where = condWhere
                                    If ParseCondition(Cond, Cls, logs) = False Then
                                        Return False
                                    End If
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
    Public Function ParseCondition(ByRef Cond As Designer.PredefinedCondition, ByRef Cls As Designer.Class, ByRef logs As logMessages) As Boolean
        Dim Result As MsgBoxResult
        If ConditionParse = True Then
            Try
                Cond.Parse()
            Catch ex As Exception
                logs.AddLogText("Condition Parse failed for '" & Cls.Name & "/" & Cond.Name & "' with Where clause '" & Cond.Where & "'.")
                logs.AddLogText("Condition Parse Exception: " & ex.ToString)
            End Try
        End If
        Return True
    End Function

    Public Function addCondition(ByRef Univ As Object, ByRef univ_class As String, ByRef univ_object As String, ByRef description As String, ByRef logs As logMessages) As Boolean

        Dim Cond As Designer.PredefinedCondition
        Dim Obj As Designer.Object
        Dim Cls As Designer.Class

        Dim condName As String
        Dim condWhere As String

        condName = "Select " & univ_object

        Cls = getClass(Univ, univ_class)
        If Cls Is Nothing Then
            logs.AddLogText("Condition '" & condName & "' generation error: Class '" & univ_class & "' not found.")
            Return False
        End If

        Try
            Cond = Cls.PredefinedConditions(condName)
            UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
        Catch e As Exception
            Cond = Cls.PredefinedConditions.Add(condName, Cls)
            UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
        End Try
        condWhere = "@Select(" & univ_class & "\" & univ_object & ")"
        condWhere &= setSelectMode("x")
        condWhere &= "@Prompt('" & univ_object & ":','"
        Try
            Obj = Cls.Objects(univ_object)
        Catch e As Exception
            logs.AddLogText("Condition '" & condName & "' generation error: Object '" & univ_object & "' not found in class '" & univ_class & "'.")
            Return False
        End Try

        condWhere &= setPromptType(Obj)
        condWhere &= "','" & univ_class & "\" & univ_object & "',"
        condWhere &= setListMode("x")
        condWhere &= setTextMode("x")
        Cond.Description = description
        Cond.Where = condWhere
        If ParseCondition(Cond, Cls, logs) = False Then
            Return False
        End If
        Return True

    End Function
    Function getClass(ByRef Univ As Designer.Universe, ByRef classname As String) As Designer.Class

        Dim Cls As Designer.Class
        Try
            Cls = Univ.Classes.FindClass(classname)
        Catch e As Exception
            Return Nothing
        End Try
        Return Cls

    End Function
    Function setTextMode(ByRef Setting As String)
        If LCase(Setting) = "x" Then
            Return "free)"
        Else
            Return "CONSTRAINED)"
        End If
    End Function
    Function setListMode(ByRef Setting As String)
        If LCase(Setting) = "x" Then
            Return "multi,"
        Else
            Return "mono,"
        End If
    End Function
    Function setSelectMode(ByRef Setting As String)
        If LCase(Setting) = "x" Then
            Return " IN "
        Else
            Return " = "
        End If
    End Function
    Function setPromptType(ByRef Obj As Designer.Object)
        If Obj.Type = Designer.DsObjectType.dsDateObject Then
            Return "D"
        End If
        If Obj.Type = Designer.DsObjectType.dsCharacterObject Then
            Return "A"
        End If
        If Obj.Type = Designer.DsObjectType.dsNumericObject Then
            Return "N"
        End If
    End Function
End Class
