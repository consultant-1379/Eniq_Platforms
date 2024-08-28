Option Strict Off

''
'  ReferenceTypes class is a collection of ReferenceType classes
'
Public NotInheritable Class ReferenceTypes
    Private _referencetypes As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of ReferenceType classes in ReferenceTypes class
    '
    ' @param Index Specifies the index in the ReferenceTypes class
    ' @return Count of ReferenceType classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _referencetypes Is Nothing) Then
                Return _referencetypes.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets ReferenceType class from ReferenceTypes class based on given index.
    '
    ' @param Index Specifies the index in the ReferenceTypes class
    ' @return Reference to ReferenceType
    Public ReadOnly Property Item(ByVal Index As Integer) As ReferenceType
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return CType(_referencetypes.Item(Index - 1), ReferenceType)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds ReferenceType class to ReferenceTypes class
    '
    ' @param ValueIn Specifies reference to ReferenceType
    Public Sub AddItem(ByVal ValueIn As ReferenceType)

        If (Not _referencetypes Is Nothing) Then
            _referencetypes.Add(ValueIn)
        End If

    End Sub

    ''
    '  ReferenceType class defines reference types for technology package.
    '
    Public NotInheritable Class ReferenceType
        Private m_ReferenceTypeID As String
        Private m_Description As String
        Private m_Type As String
        Private m_Update As Integer
        Private m_CurrentRequired As Boolean
        Private m_IncludeInUniverse As Boolean
        Private m_Row As Integer

        ''
        '  Copies values from a specified ReferenceType.
        '
        ' @param Value Specifies reference to ReferenceType
        Public Sub copy(ByVal Value As ReferenceType)

            m_ReferenceTypeID = Value.ReferenceTypeID
            m_Description = Value.Description
            m_Type = Value.Type
            m_Update = Value.Update
            m_CurrentRequired = Value.CurrentRequired
            m_IncludeInUniverse = Value.IncludeInUniverse

        End Sub

        ''
        ' Gets and sets value for ReferenceTypeID parameter. 
        ' ReferenceTypeID defines name of the reference table.
        '
        ' @param Value Specifies value of ReferenceTypeID parameter
        ' @return Value of ReferenceTypeID parameter
        Public Property ReferenceTypeID() As String
            Get
                ReferenceTypeID = m_ReferenceTypeID
            End Get

            Set(ByVal Value As String)
                m_ReferenceTypeID = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Description parameter. 
        ' Description defines description.
        '
        ' @param Value Specifies value of Description parameter
        ' @return Value of Description parameter
        Public Property Description() As String
            Get
                Description = m_Description
            End Get

            Set(ByVal Value As String)
                m_Description = Value
            End Set

        End Property

        ''
        ' Gets and sets value for IncludeInUniverse parameter. 
        ' IncludeInUniverse defines whether table should be included in universe.
        '
        ' @param Value Specifies value of IncludeInUniverse parameter
        ' @return Value of IncludeInUniverse parameter
        Public Property IncludeInUniverse()
            Get
                IncludeInUniverse = m_IncludeInUniverse
            End Get

            Set(ByVal Value)
                If LCase(Value) = "yes" Then
                    m_IncludeInUniverse = False
                Else
                    m_IncludeInUniverse = True
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for Type parameter. 
        ' Type defines reference table type.
        '
        ' @param Value Specifies value of Type parameter
        ' @return Value of Type parameter
        Public Property Type() As String
            Get
                Type = m_Type
            End Get

            Set(ByVal Value As String)
                m_Type = LCase(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Update parameter. 
        ' Update defines reference table update function.
        '
        ' @param Value Specifies value of Update parameter
        ' @return Value of Update parameter
        Public Property Update()
            Get
                Update = m_Update
            End Get

            Set(ByVal Value)
                If LCase(Value) = "dynamic" Then
                    m_Update = 2
                    m_CurrentRequired = True
                ElseIf LCase(Value) = "predefined" Then
                    m_Update = 1
                    m_CurrentRequired = False
                ElseIf LCase(Value) = "static" Then
                    m_Update = 0
                    m_CurrentRequired = False
                Else
                    m_Update = 0
                    m_CurrentRequired = False
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for CurrentRequired parameter. 
        ' CurrentRequired defines whether current table is required for reference table.
        '
        ' @param Value Specifies value of CurrentRequired parameter
        ' @return Value of CurrentRequired parameter
        Public Property CurrentRequired()
            Get
                CurrentRequired = m_CurrentRequired
            End Get

            Set(ByVal Value)
                If LCase(Value) = "yes" Then
                    m_CurrentRequired = True
                Else
                    m_CurrentRequired = False
                End If
            End Set

        End Property

        Public Property Row()
            Get
                Row = m_Row
            End Get

            Set(ByVal Value)
                m_Row = Value
            End Set

        End Property

        ''
        ' Gets text description of update method.
        '
        Public Function getUpdateMethod() As String

            If m_Update = 2 Then
                Return "dynamic"
            ElseIf m_Update = 1 Then
                Return "predefined"
            ElseIf m_Update = 0 Then
                Return "static"
            Else
                Return "static"
            End If
        End Function

    End Class

    ''
    ' Gets topology information defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Function getTopology(ByRef tp_name As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef mts As MeasurementTypes, ByRef logs As logMessages) As Boolean
        Dim count As Integer
        Dim actual_table As String
        Dim rt As ReferenceType
        Dim Row As Integer

        Dim SupportedTypes As String
        Dim SupportedUpdates As String

        SupportedTypes = "table,view"
        SupportedUpdates = "dynamic,predefined,static"
        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [ReferenceTables$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If Trim(dbReader.GetValue(0).ToString()) = "" Then
                Exit While
            Else
                Row += 1
                If InStrRev(dbReader.GetValue(0).ToString(), "(DIM_RANKMT)") > 0 Then
                    For count = 1 To mts.Count
                        If mts.Item(count).RankTable = True Then
                            actual_table = Replace(dbReader.GetValue(0).ToString(), "(DIM_RANKMT)", mts.Item(count).MeasurementTypeID)
                            rt = New ReferenceType
                            rt.ReferenceTypeID = Replace(actual_table, "DC_", "DIM_")
                            rt.Description = dbReader.GetValue(1).ToString()
                            rt.Type = dbReader.GetValue(3).ToString()
                            rt.Update = dbReader.GetValue(2).ToString()
                            rt.Row = Row
                            'update check
                            If InStrRev(SupportedUpdates, LCase(dbReader.GetValue(2).ToString())) = 0 Then
                                logs.AddLogText("Update for Reference Table '" & rt.ReferenceTypeID & "' is not one of the supported: " & SupportedUpdates)
                            End If
                            AddItem(rt)
                        End If
                    Next count
                Else
                    rt = New ReferenceType
                    rt.ReferenceTypeID = Replace(dbReader.GetValue(0).ToString(), "(TPNAME)", Replace(tp_name, "DC_", ""))
                    rt.Description = dbReader.GetValue(1).ToString()
                    rt.Type = dbReader.GetValue(3).ToString()
                    rt.Update = dbReader.GetValue(2).ToString()
                    rt.Row = Row
                    'update check
                    If InStrRev(SupportedUpdates, LCase(dbReader.GetValue(2).ToString())) = 0 Then
                        logs.AddLogText("Update for Reference Table '" & rt.ReferenceTypeID & "' is not one of the supported: " & SupportedUpdates)
                    End If
                    AddItem(rt)
                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test types
        Dim testTypes As ReferenceTypes
        Dim testType As ReferenceTypes.ReferenceType
        Dim test_count As Integer
        Dim amount As Integer
        testTypes = Me
        For count = 1 To Me.Count
            rt = Item(count)
            amount = 0
            'type check
            If InStrRev(SupportedTypes, LCase(rt.Type)) = 0 Then
                logs.AddLogText("Type for Reference Table '" & rt.ReferenceTypeID & "' at Row " & rt.Row & " is not one of the supported: " & SupportedTypes)
            End If
            'duplicate check
            For test_count = 1 To testTypes.Count
                testType = testTypes.Item(test_count)
                If rt.ReferenceTypeID = testType.ReferenceTypeID Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Reference Table '" & rt.ReferenceTypeID & "' at Row " & rt.Row & " has been defined " & amount & " times.")
                Return False
            End If
        Next count

        Return True

    End Function

    ''
    ' Gets vector topology information defined in TP definition. 
    '
    Public Sub getVectorTopology(ByRef mts As MeasurementTypes)
        Dim count As Integer
        Dim cnt_count As Integer
        Dim actual_table As String
        Dim rt As ReferenceType
        Dim cnts As Counters
        Dim cnt As Counters.Counter

        For count = 1 To mts.Count
            If mts.Item(count).RankTable = False Then
                cnts = mts.Item(count).Counters
                For cnt_count = 1 To cnts.Count
                    cnt = cnts.Item(cnt_count)
                    If cnt.CounterType = "VECTOR" Then
                        actual_table = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                        rt = New ReferenceType
                        rt.ReferenceTypeID = actual_table
                        rt.Description = "Vector mapping for " & cnt.CounterName & " in " & cnt.MeasurementTypeID
                        rt.Type = "table"
                        rt.Update = "static"
                        AddItem(rt)
                    End If
                Next cnt_count
            End If
        Next count

    End Sub

End Class

