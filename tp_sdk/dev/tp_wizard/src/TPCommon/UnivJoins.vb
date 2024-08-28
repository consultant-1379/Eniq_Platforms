Option Strict Off

''
'  UnivJoins class is a collection of UnivJoin classes
'
Public Class UnivJoins
    Private measurements() As String
    Private measurements_count As Long
    Private basic_measurements() As String
    Private basic_measurements_count As Long
    Private JoinsArray() As String
    Private JoinCount As Integer
    Private _joins As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of UnivJoin classes in UnivJoins class
    '
    ' @param Index Specifies the index in the UnivJoins class
    ' @return Count of UnivJoin classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _joins Is Nothing) Then
                Return _joins.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets UnivJoin class from UnivJoins class based on given index.
    '
    ' @param Index Specifies the index in the UnivJoins class
    ' @return Reference to UnivJoin
    Public ReadOnly Property Item(ByVal Index As Integer) As UnivJoin
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_joins.Item(Index - 1), UnivJoin)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds UnivJoin class to UnivJoins class
    '
    ' @param ValueIn Specifies reference to UnivJoin
    Public Sub AddItem(ByVal ValueIn As UnivJoin)

        If (Not _joins Is Nothing) Then
            _joins.Add(ValueIn)
        End If

    End Sub

    ''
    '  UnivJoin class defines universe's joins.
    '
    Public Class UnivJoin
        Private m_Expression As String
        Private m_Cardinality As Integer
        Private m_Contexts As String
        Private m_ExcludedContexts As String
        Private m_FirstTable As String
        Private m_SecondTable As String

        Public Property FirstTable()
            Get
                FirstTable = m_FirstTable
            End Get

            Set(ByVal Value)
                m_FirstTable = Value
            End Set

        End Property
        Public Property SecondTable()
            Get
                SecondTable = m_SecondTable
            End Get

            Set(ByVal Value)
                m_SecondTable = Value
            End Set

        End Property
        ''
        ' Gets and sets value for Expression parameter. 
        ' Expression defines join's expression.
        '
        ' @param Value Specifies value of Expression parameter
        ' @return Value of Expression parameter
        Public Property Expression()
            Get
                Expression = m_Expression
            End Get

            Set(ByVal Value)
                m_Expression = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Cardinality parameter. 
        ' Cardinality defines join's cardinality.
        '
        ' @param Value Specifies value of Cardinality parameter
        ' @return Value of Cardinality parameter
        Public Property Cardinality()
            Get
                Cardinality = m_Cardinality
            End Get

            Set(ByVal Value)
                If Value = "n_to_1" Then
                    m_Cardinality = Designer.DsCardinality.dsManyToOneCardinality
                End If
                If Value = "1_to_n" Then
                    m_Cardinality = Designer.DsCardinality.dsOneToManyCardinality
                End If
                If Value = "1_to_1" Then
                    m_Cardinality = Designer.DsCardinality.dsOneToOneCardinality
                End If
                If Value = "n_to_n" Then
                    m_Cardinality = Designer.DsCardinality.dsManyToManyCardinality
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for Contexts parameter. 
        ' Contexts defines join's contexts.
        '
        ' @param Value Specifies value of Contexts parameter
        ' @return Value of Contexts parameter
        Public Property Contexts()
            Get
                Contexts = m_Contexts
            End Get

            Set(ByVal Value)
                m_Contexts = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ExcludedContexts parameter. 
        ' ExcludedContexts defines join's excluded contexts.
        '
        ' @param Value Specifies value of ExcludedContexts parameter
        ' @return Value of ExcludedContexts parameter
        Public Property ExcludedContexts()
            Get
                ExcludedContexts = m_ExcludedContexts
            End Get

            Set(ByVal Value)
                m_ExcludedContexts = Value
            End Set

        End Property
    End Class

    ''
    ' Gets joins for measurement types. 
    '
    ' @param tp_name Specifies technology package name
    ' @param mts Specifies reference to MeasurementTypes
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Function getJoins(ByRef tp_name As String, ByRef mts As MeasurementTypes, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages) As Boolean

        Dim JoinExpr As String

        Dim StartT As String
        Dim StartL As String
        Dim StartC As String
        Dim EndT As String
        Dim EndL As String
        Dim EndC As String
        Dim tmp_StartT As String
        Dim tmp_StartL As String
        Dim tmp_StartC As String
        Dim tmp_EndT As String
        Dim tmp_EndL As String
        Dim tmp_EndC As String

        Dim Cardinality As String
        Dim Contexts As String
        Dim ExcludedContexts As String
        Dim count As Integer
        Dim mt As MeasurementTypes.MeasurementType

        Dim bh_mts As MeasurementTypes
        Dim bh_mt As MeasurementTypes.MeasurementType
        Dim bh_count As Integer
        Dim First As Boolean
        Dim bhtables As String

        Dim cnts As CounterKeys
        Dim cnt As CounterKeys.CounterKey
        Dim cnt_count As Integer
        Dim element_column As String

        Dim BusyHourObjects() As String
        Dim bhObjectCount As Integer


        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Universe joins$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                If dbReader.GetValue(2).ToString() <> "" Then
                    tmp_StartT = Trim(dbReader.GetValue(0).ToString())
                    tmp_StartL = Trim(dbReader.GetValue(1).ToString())
                    tmp_StartC = Trim(dbReader.GetValue(2).ToString())
                    tmp_EndT = Trim(dbReader.GetValue(3).ToString())
                    tmp_EndL = Trim(dbReader.GetValue(4).ToString())
                    tmp_EndC = Trim(dbReader.GetValue(5).ToString())
                    Cardinality = Trim(dbReader.GetValue(6).ToString())

                    Contexts = Trim(dbReader.GetValue(7).ToString())
                    ExcludedContexts = Trim(dbReader.GetValue(8).ToString())

                    If InStrRev(dbReader.GetValue(0).ToString(), "(OBJECTBH_MT)") > 0 Then
                        bh_mts = mts
                        For count = 1 To mts.Count
                            mt = mts.Item(count)
                            If mt.RankTable = True AndAlso mt.ObjectBusyHours <> "" Then
                                First = True
                                For bh_count = 1 To bh_mts.Count
                                    bh_mt = bh_mts.Item(bh_count)
                                    If bh_mt.ObjectBusyHours <> "" Then
                                        'BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                                        BusyHourObjects = Split(bh_mt.ObjectBusyHours, ",")
                                        For bhObjectCount = 0 To UBound(BusyHourObjects)
                                            If bh_mt.RankTable = False AndAlso BusyHourObjects(bhObjectCount) = mt.ObjectBusyHours Then
                                                If First = True Then
                                                    bhtables = "DC." & bh_mt.MeasurementTypeID
                                                    First = False
                                                Else
                                                    bhtables &= "," & "DC." & bh_mt.MeasurementTypeID
                                                End If
                                            End If
                                        Next bhObjectCount
                                    End If
                                Next bh_count
                                StartT = Replace(tmp_StartT, "(OBJECTBH_MT)", bhtables)
                                EndT = Replace(tmp_EndT, "(DIM_OBJECTRANKMT)", Replace(mt.MeasurementTypeID, "DC_", "DIM_"))
                                If tmp_StartL <> "RAW/COUNT" Then
                                    StartL = tmp_StartL
                                Else
                                    If mt.CreateCountTable = True Then
                                        StartL = "COUNT"
                                    Else
                                        StartL = "RAW"
                                    End If
                                End If
                                StartC = tmp_StartC
                                EndL = tmp_EndL
                                EndC = tmp_EndC
                                If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, logs) = False Then
                                    Return False
                                End If
                            End If
                        Next count
                    ElseIf InStrRev(dbReader.GetValue(0).ToString(), "(ELEMENTRANKMT)") > 0 Then
                        For count = 1 To mts.Count
                            mt = mts.Item(count)
                            If mt.RankTable = True AndAlso mt.ElementBusyHours = True Then
                                StartT = Replace(tmp_StartT, "(ELEMENTRANKMT)", mt.MeasurementTypeID)
                                EndT = Replace(tmp_EndT, "(DIM_ELEMENTRANKMT)", Replace(mt.MeasurementTypeID, "DC_", "DIM_"))
                                If tmp_StartL <> "RAW/COUNT" Then
                                    StartL = tmp_StartL
                                Else
                                    If mt.CreateCountTable = True Then
                                        StartL = "COUNT"
                                    Else
                                        StartL = "RAW"
                                    End If
                                End If
                                StartL = tmp_StartL
                                StartC = tmp_StartC
                                EndL = tmp_EndL
                                EndC = tmp_EndC
                                If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, logs) = False Then
                                    Return False
                                End If
                            End If
                        Next count
                    ElseIf InStrRev(dbReader.GetValue(0).ToString(), "(ELEMENTBH_MT)") > 0 Then
                        bh_mts = mts
                        For count = 1 To mts.Count
                            mt = mts.Item(count)
                            If mt.RankTable = True AndAlso mt.ElementBusyHours = True Then

                                cnts = mt.CounterKeys
                                For cnt_count = 1 To cnts.Count
                                    cnt = cnts.Item(cnt_count)
                                    If cnt.Element = 1 Then
                                        element_column = cnt.CounterKeyName
                                        Exit For
                                    End If
                                Next cnt_count

                                First = True
                                For bh_count = 1 To bh_mts.Count
                                    bh_mt = bh_mts.Item(bh_count)
                                    If bh_mt.RankTable = False AndAlso bh_mt.ElementBusyHours = mt.ElementBusyHours Then
                                        StartT = Replace(tmp_StartT, "(ELEMENTBH_MT)", "DC." & bh_mt.MeasurementTypeID)
                                        EndT = Replace(tmp_EndT, "(ELEMENTRANKMT)", mt.MeasurementTypeID)
                                        StartC = Replace(tmp_StartC, "(ELEMENTCOLUMN)", element_column)
                                        EndC = Replace(tmp_EndC, "(ELEMENTCOLUMN)", element_column)
                                        If tmp_StartL <> "RAW/COUNT" Then
                                            StartL = tmp_StartL
                                        Else
                                            If bh_mt.CreateCountTable = True Then
                                                StartL = "COUNT"
                                            Else
                                                StartL = "RAW"
                                            End If
                                        End If
                                        EndL = tmp_EndL
                                        If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, logs) = False Then
                                            Return False
                                        End If
                                    End If
                                Next bh_count
                            End If
                        Next count
                    Else
                        StartT = tmp_StartT
                        EndT = tmp_EndT
                        StartL = tmp_StartL
                        StartC = tmp_StartC
                        EndL = tmp_EndL
                        EndC = tmp_EndC
                        If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, logs) = False Then
                            Return False
                        End If
                    End If

                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        Return True

    End Function

    Public Function makeJoins(ByRef tp_name As String, ByRef StartTables As String, ByRef StartLevels As String, ByRef StartColumns As String, ByRef EndTables As String, ByRef EndLevels As String, ByRef EndColumns As String, ByRef Cardinality As String, ByRef Contexts As String, ByRef ExcludedContexts As String, ByRef logs As logMessages) As Boolean

        Dim JoinExpr As String

        CreateJoinExpression(StartTables, StartLevels, StartColumns, EndTables, EndLevels, EndColumns)
        For JoinCount = 0 To UBound(JoinsArray)
            Dim univ_join = New UnivJoins.UnivJoin
            univ_join.Expression = JoinsArray(JoinCount)
            JoinExpr = Replace(univ_join.Expression, "(TPNAME)", Replace(tp_name, "DC_", ""))
            univ_join.Expression = JoinExpr
            If Cardinality <> "n_to_1" AndAlso Cardinality <> "1_to_n" AndAlso Cardinality <> "1_to_1" AndAlso Cardinality <> "n_to_n" Then
                logs.AddLogText("Cardinality '" & Cardinality & "' for '" & univ_join.Expression & "' is not one of the supported: 'n_to_1', '1_to_n', '1_to_1', 'n_to_n'.")
                'Return False
            End If
            univ_join.Cardinality = Cardinality
            univ_join.Contexts = Contexts
            univ_join.ExcludedContexts = ExcludedContexts
            AddItem(univ_join)
        Next JoinCount
        Return True

    End Function
    'add join between *DC_VECTOR and DC_RELEASE
    Public Sub getVectorJoins(ByRef mts As MeasurementTypes)

        Dim VectorTable As String
        Dim JoinExpr As String
        Dim count As Integer
        Dim cnt_count As Integer
        Dim cnts As Counters
        Dim cnt As Counters.Counter
        Dim mt As MeasurementTypes.MeasurementType

        For count = 1 To mts.Count
            mt = mts.Item(count)
            cnts = mts.Item(count).Counters
            For cnt_count = 1 To cnts.Count
                cnt = cnts.Item(cnt_count)
                If cnt.CounterType = "VECTOR" Then
                    VectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                    CreateJoinExpression("DC." & cnt.MeasurementTypeID, "All", "DCVECTOR_INDEX,DC_RELEASE", "DC." & VectorTable, "", cnt.CounterName & "_DCVECTOR,DC_RELEASE")
                    For JoinCount = 0 To UBound(JoinsArray)
                        Dim univ_join = New UnivJoins.UnivJoin
                        univ_join.Expression = JoinsArray(JoinCount)
                        univ_join.Cardinality = "n_to_1"
                        univ_join.Contexts = ""
                        AddItem(univ_join)
                    Next JoinCount
                End If
            Next cnt_count
        Next count

    End Sub

    ''
    ' Builds measurement lists for measurement types. 
    '
    ' @param mts Specifies reference to MeasurementTypes
    Public Sub buildLists(ByRef mts As MeasurementTypes)

        Dim mt As MeasurementTypes.MeasurementType
        Dim mt_count As Integer

        'build measurements for joins
        ReDim Preserve measurements(2000)
        measurements_count = 0
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                If mt.PlainTable = False Then
                    measurements(measurements_count) = "DC." & mt.MeasurementTypeID & "_RAW"
                    measurements_count += 1
                End If
                If mt.PlainTable = True Then
                    measurements(measurements_count) = "DC." & mt.MeasurementTypeID
                    measurements_count += 1
                End If
                If mt.CreateCountTable = True Then
                    measurements(measurements_count) = "DC." & mt.MeasurementTypeID & "_COUNT"
                    measurements_count += 1
                End If
                If mt.DayAggregation = True Then
                    measurements(measurements_count) = "DC." & mt.MeasurementTypeID & "_DAY"
                    measurements_count += 1
                End If
                If mt.ObjectBusyHours <> "" Then
                    measurements(measurements_count) = "DC." & mt.MeasurementTypeID & "_DAYBH"
                    measurements_count += 1
                End If
            End If
        Next mt_count
        ReDim Preserve measurements(measurements_count - 1)

        ReDim Preserve basic_measurements(300)
        basic_measurements_count = 0
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = False Then
                'If mt.PlainTable = True Then
                basic_measurements(basic_measurements_count) = "DC." & mt.MeasurementTypeID
                basic_measurements_count = basic_measurements_count + 1
                'End If
                'If mt.PlainTable = False Then
                'basic_measurements(basic_measurements_count) = "DC." & mt.MeasurementTypeID & "_RAW"
                'basic_measurements_count = basic_measurements_count + 1
                'End If
            End If
        Next mt_count
        ReDim Preserve basic_measurements(basic_measurements_count - 1)
    End Sub

    ''
    ' Builds join expressions
    '
    ' @param StartTables Defines starting tables
    ' @param StartLevels Defines starting table levels
    ' @param StartColumns Defines starting columns
    ' @param EndTables Defines ending tables
    ' @param EndLevels Defines ending table levels
    ' @param EndColumns Defines ending columns
    ' @return Join expression
    Private Function CreateJoinExpression(ByRef StartTables As String, ByRef StartLevels As String, ByRef StartColumns As String, ByRef EndTables As String, ByRef EndLevels As String, ByRef EndColumns As String) As Object

        Dim JoinStartColumns() As String
        Dim JoinEndColumns() As String

        Dim JoinStartTableLevels() As String
        'Dim JoinEndTableLevels() As String

        Dim JoinStartLevels() As String
        Dim JoinStartTables() As String
        Dim col_count As Integer
        Dim meas_count As Integer
        Dim basic_meas_count As Integer
        Dim table_count As Integer
        Dim start_level_count As Integer
        Dim start_table_count As Integer
        Dim JoinExpression As String
        Dim ExpressionStart As String
        Dim ExpressionEnd As String

        JoinStartColumns = Split(StartColumns, ",")
        JoinEndColumns = Split(EndColumns, ",")


        ReDim Preserve JoinStartTableLevels(2000)
        start_table_count = 0

        If LCase(StartTables) = "all" AndAlso LCase(StartLevels) <> "all" Then
            JoinStartLevels = Split(StartLevels, ",")
            For start_level_count = 0 To UBound(JoinStartLevels)
                For basic_meas_count = 0 To UBound(basic_measurements)
                    For meas_count = 0 To UBound(measurements)
                        If measurements(meas_count) = basic_measurements(basic_meas_count) & "_" & JoinStartLevels(start_level_count) Then
                            JoinStartTableLevels(start_table_count) = measurements(meas_count)
                            start_table_count += 1
                        End If
                    Next meas_count
                Next basic_meas_count
            Next start_level_count

        ElseIf LCase(StartTables) = "all" AndAlso LCase(StartLevels) = "all" Then
            For meas_count = 0 To UBound(measurements)
                JoinStartTableLevels(start_table_count) = measurements(meas_count)
                start_table_count += 1
            Next meas_count

        ElseIf LCase(StartTables) <> "all" AndAlso LCase(StartLevels) = "all" Then
            JoinStartTables = Split(StartTables, ",")
            For table_count = 0 To UBound(JoinStartTables)
                For meas_count = 0 To UBound(measurements)
                    If measurements(meas_count) = JoinStartTables(table_count) Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_RAW" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_COUNT" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_DAY" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_DAYBH" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                Next meas_count
            Next table_count

        Else
            JoinStartTables = Split(StartTables, ",")
            For table_count = 0 To UBound(JoinStartTables)
                If StartLevels = "" Then
                    JoinStartTableLevels(start_table_count) = JoinStartTables(table_count)
                    start_table_count += 1
                Else
                    JoinStartLevels = Split(StartLevels, ",")
                    For start_level_count = 0 To UBound(JoinStartLevels)
                        JoinStartTableLevels(start_table_count) = JoinStartTables(table_count) & "_" & JoinStartLevels(start_level_count)
                        start_table_count += 1
                    Next start_level_count
                End If
            Next table_count
        End If

        ReDim Preserve JoinStartTableLevels(start_table_count)


        ReDim Preserve JoinsArray(2000)
        JoinCount = 0

        For start_table_count = 0 To UBound(JoinStartTableLevels)
            'all measurement and specified levels (1. table only)
            If JoinStartTableLevels(start_table_count) <> "" Then
                JoinExpression = ""

                For col_count = 0 To UBound(JoinStartColumns)
                    If JoinStartColumns(col_count) <> "" Then

                        ExpressionStart = JoinStartTableLevels(start_table_count) & "." & JoinStartColumns(col_count)

                        If EndLevels <> "" Then
                            ExpressionEnd = EndTables & "_" & EndLevels + "." & JoinEndColumns(col_count)
                        Else
                            ExpressionEnd = EndTables & "." & JoinEndColumns(col_count)
                        End If


                        'multiple columns
                        If col_count > 0 Then
                            JoinExpression &= " and " & ExpressionStart & "=" & ExpressionEnd
                        Else
                            JoinExpression = ExpressionStart & "=" & ExpressionEnd
                        End If
                    End If
                Next col_count
                'add join
                JoinsArray(JoinCount) = JoinExpression
                JoinCount += 1
            End If
        Next start_table_count

        ReDim Preserve JoinsArray(JoinCount - 1)

    End Function
End Class
