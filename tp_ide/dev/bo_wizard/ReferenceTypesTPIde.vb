Option Strict Off

''
'  ReferenceTypes class is a collection of ReferenceType classes
'
Public NotInheritable Class ReferenceTypesTPIde
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
        Private m_TypeName As String
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
            m_TypeName = Value.TypeName
            m_Update = Value.Update
            m_CurrentRequired = Value.CurrentRequired
            m_IncludeInUniverse = Value.IncludeInUniverse

        End Sub

        Public Property TypeName() As String
            Get
                TypeName = m_TypeName
            End Get

            Set(ByVal Value As String)
                m_TypeName = Value
            End Set

        End Property

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
                m_IncludeInUniverse = Value
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
                If Value = 2 Then
                    m_CurrentRequired = True
                ElseIf Value = 1 Then
                    m_CurrentRequired = False
                ElseIf Value = 0 Then
                    m_CurrentRequired = False
                Else
                    m_CurrentRequired = False
                End If
                m_Update = Value
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
    Public Function getTopology(ByRef tp_name As String, ByRef conn As System.Data.Odbc.OdbcConnection, ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, ByRef mts As MeasurementTypesTPIde, ByRef TechPackTPIde As String) As Boolean
        Dim count As Integer
        Dim actual_table As String
        Dim rt As ReferenceType
        Dim Row As Integer

        Dim SupportedTypes As String
        Dim SupportedUpdates As String

        SupportedTypes = "table,view"
        SupportedUpdates = "dynamic,predefined,static"
        Row = 1

        Dim reftable As String
        reftable = "SELECT TYPEID," & _
        "SUBSTR(DESCRIPTION,1,8000),SUBSTR(DESCRIPTION,8001,8000),SUBSTR(DESCRIPTION,16001,8000),SUBSTR(DESCRIPTION,24001,8000)" & _
        ",TYPENAME,TABLE_TYPE,UPDATE_POLICY FROM ReferenceTable where VERSIONID ='" & TechPackTPIde & "'"

        dbCommand = New System.Data.Odbc.OdbcCommand(reftable, conn)
        'modified for TR HK80515
        Console.WriteLine("Gets topology information for the Reference Types defined in TP definition.")
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
            If dbReader.GetValue(5).ToString() <> "" Then
                Row += 1
                If InStrRev(dbReader.GetValue(5).ToString(), "(DIM_RANKMT)") > 0 Then
                    For count = 1 To mts.Count
                        If mts.Item(count).RankTable = True Then
                            rt = New ReferenceType
                            rt.TypeName = dbReader.GetValue(5).ToString()
                            actual_table = Replace(rt.TypeName, "(DIM_RANKMT)", mts.Item(count).TypeName)
                            ' rt.TypeName = Replace(actual_table, "DC_", "DIM_")
                            If (actual_table.StartsWith("DC_")) Then
                                rt.TypeName = Replace(actual_table, "DC_", "DIM_", , 1)
                               
                            ElseIf (actual_table.StartsWith("PM_")) Then
                                rt.TypeName = Replace(actual_table, "PM_", "DIM_")
                            ElseIf (actual_table.StartsWith("CM_")) Then
                                rt.TypeName = Replace(actual_table, "CM_", "DIM_")
                            ElseIf (actual_table.StartsWith("CUSTOM_")) Then
                                rt.TypeName = Replace(actual_table, "CUSTOM_", "DIM_")
                            End If

                            rt.ReferenceTypeID = TechPackTPIde + ":" + rt.TypeName
                            If dbReader.IsDBNull(1) = False Then
                                rt.Description = Trim(dbReader.GetString(1) + dbReader.GetString(2) + dbReader.GetString(3) + dbReader.GetString(4))
                            Else
                                rt.Description() = ""
                            End If
                            rt.Type = dbReader.GetValue(6).ToString()
                            rt.Update = dbReader.GetValue(7).ToString()
                            rt.Row = Row
                            'update check
                            If InStrRev(SupportedUpdates, rt.getUpdateMethod) = 0 Then
                                Trace.WriteLine("Update for Reference Table '" & rt.TypeName & "' is not one of the supported: " & SupportedUpdates)
                            End If
                            'WORKAROUND to prevent extra DIM_*_BHTYPE
                            rt.IncludeInUniverse = False
                            '...
                            AddItem(rt)

                        End If
                    Next count
                Else
                    rt = New ReferenceType
                    rt.TypeName = dbReader.GetValue(5).ToString()
                    'actual_table = Replace(rt.TypeName, "(TPNAME)", Replace(tp_name, "DC_", ""))

                    'APO 6.2.2009
                    If (tp_name.StartsWith("DC_")) Then
                        actual_table = Replace(rt.TypeName, "(TPNAME)", Replace(tp_name, "DC_", ""))
                    ElseIf (tp_name.StartsWith("PM_")) Then
                        actual_table = Replace(rt.TypeName, "(TPNAME)", Replace(tp_name, "PM_", ""))
                    ElseIf (tp_name.StartsWith("CM_")) Then
                        actual_table = Replace(rt.TypeName, "(TPNAME)", Replace(tp_name, "CM_", ""))
                    ElseIf (tp_name.StartsWith("CUSTOM_")) Then
                        actual_table = Replace(rt.TypeName, "(TPNAME)", Replace(tp_name, "CUSTOM_", ""))
                    Else
                        actual_table = rt.TypeName
                    End If

                    rt.TypeName = actual_table
                    rt.ReferenceTypeID = TechPackTPIde + ":" + rt.TypeName
                    If dbReader.IsDBNull(1) = False Then
                        rt.Description = Trim(dbReader.GetString(1) + dbReader.GetString(2) + dbReader.GetString(3) + dbReader.GetString(4))
                    Else
                        rt.Description() = ""
                    End If
                    rt.Type = dbReader.GetValue(6).ToString()
                    rt.Update = dbReader.GetValue(7).ToString()
                    rt.Row = Row
                    'update check
                    If InStrRev(SupportedUpdates, rt.getUpdateMethod) = 0 Then
                        Trace.WriteLine("Update for Reference Table '" & rt.TypeName & "' is not one of the supported: " & SupportedUpdates)
                    End If
                    'WORKAROUND to prevent CURRENT_DC and PUBLIC_REFTYPE to go to universe
                    If InStrRev(rt.TypeName, "_CURRENT_DC") > 0 OrElse InStrRev(rt.TypeName, "PUBLIC_REFTYPE") > 0 Then
                        rt.IncludeInUniverse = False
                    Else
                        rt.IncludeInUniverse = True
                    End If
                    AddItem(rt)
                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test types
        Dim testTypes As ReferenceTypesTPIde
        Dim testType As ReferenceTypesTPIde.ReferenceType
        Dim test_count As Integer
        Dim amount As Integer
        testTypes = Me
        For count = 1 To Me.Count
            rt = Item(count)
            amount = 0
            'type check
            If InStrRev(SupportedTypes, rt.Type) = 0 Then
                Trace.WriteLine("Type for Reference Table '" & rt.TypeName & "' at Row " & rt.Row & " is not one of the supported: " & SupportedTypes)
            End If
            'duplicate check
            For test_count = 1 To testTypes.Count
                testType = testTypes.Item(test_count)
                If rt.ReferenceTypeID = testType.ReferenceTypeID Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                Trace.WriteLine("Reference Table '" & rt.TypeName & "' at Row " & rt.Row & " has been defined " & amount & " times.")
                Return False
            End If
        Next count

        Return True

    End Function

    ''
    ' Gets vector topology information defined in TP definition. 
    '
    Public Sub getVectorTopology(ByRef mts As MeasurementTypesTPIde, ByVal tpConn As System.Data.Odbc.OdbcConnection)
        Dim count As Integer
        Dim cnt_count As Integer
        Dim actual_table_id As String
        Dim actual_table_name As String
        Dim rt As ReferenceType
        Dim cnts As CountersTPIde
        Dim cnt As CountersTPIde.Counter
        Dim tpUtilities As New TPUtilitiesTPIde

        For count = 1 To mts.Count
            If mts.Item(count).RankTable = False Then
                cnts = mts.Item(count).Counters
                For cnt_count = 1 To cnts.Count
                    cnt = cnts.Item(cnt_count)
                    If cnt.CounterType = "VECTOR" AndAlso mts.Item(count).VectorSupport = True Then
                        'Check for whether vector counter has range or not.
                        If (tpUtilities.isVectorRangePresent(cnt.MeasurementTypeID, cnt.CounterName, tpConn)) Then
                            ' actual_table_id = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                            If (cnt.MeasurementTypeID.StartsWith("DC_")) Then
                                actual_table_id = Replace(cnt.MeasurementTypeID, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                            ElseIf (cnt.MeasurementTypeID.StartsWith("PM_")) Then
                                actual_table_id = Replace(cnt.MeasurementTypeID, "PM_", "DIM_") & "_" & cnt.CounterName
                            ElseIf (cnt.MeasurementTypeID.StartsWith("CM_")) Then
                                actual_table_id = Replace(cnt.MeasurementTypeID, "CM_", "DIM_") & "_" & cnt.CounterName
                            ElseIf (cnt.MeasurementTypeID.StartsWith("CUSTOM_")) Then
                                actual_table_id = Replace(cnt.MeasurementTypeID, "CUSTOM_", "DIM_") & "_" & cnt.CounterName
                            End If

                            ' actual_table_name = Replace(cnt.TypeName, "DC_", "DIM_") & "_" & cnt.CounterName
                            If (cnt.TypeName.StartsWith("DC_")) Then
                                actual_table_name = Replace(cnt.TypeName, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                            ElseIf (cnt.TypeName.StartsWith("PM_")) Then
                                actual_table_name = Replace(cnt.TypeName, "PM_", "DIM_") & "_" & cnt.CounterName
                            ElseIf (cnt.TypeName.StartsWith("CM_")) Then
                                actual_table_name = Replace(cnt.TypeName, "CM_", "DIM_") & "_" & cnt.CounterName
                            ElseIf (cnt.TypeName.StartsWith("CUSTOM_")) Then
                                actual_table_name = Replace(cnt.TypeName, "CUSTOM_", "DIM_") & "_" & cnt.CounterName
                            End If

                            rt = New ReferenceType
                            rt.ReferenceTypeID = actual_table_id
                            rt.TypeName = actual_table_name
                            rt.Description = "Vector mapping for " & cnt.CounterName & " in " & cnt.MeasurementTypeID
                            rt.Type = "table"
                            rt.Update = 0
                            AddItem(rt)
                        Else
                            ' Nothing
                            Trace.WriteLine("No range is defined for the type id: " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding vector to Refrence Type.")
                        End If
                        'Check for whether vector counter has range or not.
                    End If
                Next cnt_count
            End If
        Next count

    End Sub

End Class


