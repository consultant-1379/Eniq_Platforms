Option Strict Off

''
'  ReferenceDatas class is a collection of ReferenceData classes
'
Public NotInheritable Class ReferenceDatas
    Private _referencedatas As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of ReferenceData classes in ReferenceDatas class
    '
    ' @param Index Specifies the index in the ReferenceDatas class
    ' @return Count of ReferenceData classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _referencedatas Is Nothing) Then
                Return _referencedatas.Count
            End If
            Return 0
        End Get
    End Property


    ''
    '  Gets ReferenceData class from ReferenceDatas class based on given index.
    '
    ' @param Index Specifies the index in the ReferenceDatas class
    ' @return Reference to ReferenceData
    Public ReadOnly Property Item(ByVal Index As Integer) As ReferenceData
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return CType(_referencedatas.Item(Index - 1), ReferenceData)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds ReferenceData class to ReferenceDatas class
    '
    ' @param ValueIn Specifies reference to ReferenceData
    Public Sub AddItem(ByVal ValueIn As ReferenceData)

        If (Not _referencedatas Is Nothing) Then
            _referencedatas.Add(ValueIn)
        End If

    End Sub

    ''
    '  ReferenceData class defines data columns for reference types.
    '
    Public NotInheritable Class ReferenceData
        Private m_ReferenceTypeID As String
        Private m_ReferenceDataID As String
        Private m_Description As String
        Private m_Datatype As String
        Private m_Datasize As String
        Private m_Datascale As String
        Private m_Nullable As String
        Private m_MaxAmount As Integer
        Private m_DupConstraint As Integer
        Private m_UnivClass As String
        Private m_UnivObject As String
        Private m_UnivCondition As Boolean
        Private m_IncludeInSQLInterface As Integer
        Private m_IncludeInTopologyUpdate As Integer
        Private m_Row As Integer
        Private m_givenDatatype As String

        ''
        '  Copies values from a specified ReferenceType.
        '
        ' @param Value Specifies reference to ReferenceType
        Public Sub copy(ByVal Value As ReferenceData)

            m_ReferenceTypeID = Value.ReferenceTypeID
            m_ReferenceDataID = Value.ReferenceDataID
            m_Description = Value.Description
            m_Datatype = Value.Datatype
            m_Datasize = Value.Datasize
            m_Datascale = Value.Datascale
            m_Nullable = Value.Nullable
            m_MaxAmount = Value.MaxAmount
            m_DupConstraint = Value.DupConstraint
            m_UnivClass = Value.UnivClass
            m_UnivObject = Value.UnivObject
            m_UnivCondition = Value.UnivCondition
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
        ' Gets and sets value for ReferenceDataID parameter. 
        ' ReferenceDataID defines name of reference column.
        '
        ' @param Value Specifies value of ReferenceDataID parameter
        ' @return Value of ReferenceDataID parameter
        Public Property ReferenceDataID() As String
            Get
                ReferenceDataID = m_ReferenceDataID
            End Get

            Set(ByVal Value As String)
                m_ReferenceDataID = Value
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
        ' Gets and sets value for MaxAmount parameter. 
        ' MaxAmount defines estimated maximum amount of different values.
        '
        ' @param Value Specifies value of MaxAmount parameter
        ' @return Value of MaxAmount parameter
        Public Property MaxAmount()
            Get
                MaxAmount = m_MaxAmount
            End Get

            Set(ByVal Value)
                If Value <> 0 Then
                    m_MaxAmount = Value
                Else
                    m_MaxAmount = 255
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for Nullable parameter. 
        ' Nullable defines whether null values are allowed.
        '
        ' @param Value Specifies value of Nullable parameter
        ' @return Value of Nullable parameter
        Public Property Nullable() As String
            Get
                Nullable = m_Nullable
            End Get

            Set(ByVal Value As String)
                If LCase(Value) = "" Then
                    m_Nullable = 1
                Else
                    m_Nullable = 1
                End If
            End Set

        End Property


        Public Property givenDatatype() As String
            Get
                givenDatatype = m_givenDatatype
            End Get

            Set(ByVal Value As String)
                m_givenDatatype = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Datatype parameter. 
        ' Datatype defines data type.
        '
        ' @param Value Specifies value of Datatype parameter
        ' @return Value of Datatype parameter
        Public Property Datatype() As String
            Get
                Datatype = m_Datatype
            End Get

            Set(ByVal Value As String)
                m_Datatype = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Datasize parameter. 
        ' Datasize defines data size.
        '
        ' @param Value Specifies value of Datasize parameter
        ' @return Value of Datasize parameter
        Public Property Datasize() As String
            Get
                Datasize = m_Datasize
            End Get

            Set(ByVal Value As String)
                m_Datasize = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Datascale parameter. 
        ' Datascale defines data scale.
        '
        ' @param Value Specifies value of Datascale parameter
        ' @return Value of Datascale parameter
        Public Property Datascale() As String
            Get
                Datascale = m_Datascale
            End Get

            Set(ByVal Value As String)
                m_Datascale = Value
            End Set

        End Property

        ''
        ' Gets and sets value for DupConstraint parameter. 
        ' DupConstraint defines whether public key is duplicate constraint.
        '
        ' @param Value Specifies value of DupConstraint parameter
        ' @return Value of DupConstraint parameter
        Public Property DupConstraint()
            Get
                DupConstraint = m_DupConstraint
            End Get

            Set(ByVal Value)
                If LCase(Value) = "" Then
                    m_DupConstraint = 0
                Else
                    m_DupConstraint = 1
                End If
            End Set

        End Property

        Public Property UnivClass() As String
            Get
                UnivClass = m_UnivClass
            End Get

            Set(ByVal Value As String)
                m_UnivClass = Value
            End Set

        End Property

        Public Property UnivObject() As String
            Get
                UnivObject = m_UnivObject
            End Get

            Set(ByVal Value As String)
                m_UnivObject = Value
            End Set

        End Property

        Public Property UnivCondition()
            Get
                UnivCondition = m_UnivCondition
            End Get

            Set(ByVal Value)
                If LCase(Value) = "" Then
                    m_UnivCondition = False
                Else
                    m_UnivCondition = True
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for IncludeInSQLInterface parameter. 
        ' IncludeInSQLInterface defines whether public key is visible in SQL interface.
        '
        ' @param Value Specifies value of IncludeInSQLInterface parameter
        ' @return Value of IncludeInSQLInterface parameter
        Public Property IncludeInSQLInterface()
            Get
                IncludeInSQLInterface = m_IncludeInSQLInterface
            End Get

            Set(ByVal Value)
                If Value = "" Then
                    m_IncludeInSQLInterface = 1
                Else
                    m_IncludeInSQLInterface = 0
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for IncludeInSQLInterface parameter. 
        ' IncludeInSQLInterface defines whether public key is visible in SQL interface.
        '
        ' @param Value Specifies value of IncludeInSQLInterface parameter
        ' @return Value of IncludeInSQLInterface parameter
        Public Property IncludeInTopologyUpdate()
            Get
                IncludeInTopologyUpdate = m_IncludeInTopologyUpdate
            End Get

            Set(ByVal Value)
                If Value = "" Then
                    m_IncludeInTopologyUpdate = 1
                Else
                    m_IncludeInTopologyUpdate = 0
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
    End Class

    ''
    ' Gets topology information defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Sub getTopology(ByRef tp_name As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef mts As MeasurementTypes, ByRef logs As logMessages, ByRef rts As ReferenceTypes)
        Dim tputils = New TPUtilities
        Dim rd As ReferenceData
        Dim count As Integer
        Dim actual_table As String
        Dim Row As Integer

        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [ReferenceColumns$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Row += 1
                If InStrRev(dbReader.GetValue(0).ToString(), "(DIM_RANKMT)") > 0 Then
                    For count = 1 To mts.Count
                        If mts.Item(count).RankTable = True Then
                            actual_table = Replace(Trim(dbReader.GetValue(0).ToString()), "(DIM_RANKMT)", mts.Item(count).MeasurementTypeID)
                            rd = New ReferenceData
                            rd.ReferenceTypeID = Replace(actual_table, "DC_", "DIM_")
                            rd.ReferenceDataID = Trim(dbReader.GetValue(1).ToString())
                            rd.Description = Trim(dbReader.GetValue(2).ToString())
                            rd.givenDatatype = Trim(dbReader.GetValue(3).ToString())
                            tputils.getDatatype(Trim(dbReader.GetValue(3).ToString()))
                            rd.Datatype = tputils.Datatype
                            rd.Datasize = tputils.Datasize
                            rd.Datascale = tputils.Datascale
                            rd.DupConstraint = Trim(dbReader.GetValue(4).ToString())
                            rd.Nullable = Trim(dbReader.GetValue(5).ToString())
                            rd.MaxAmount = 255
                            rd.UnivClass = ""
                            rd.UnivObject = ""
                            rd.UnivCondition = ""
                            rd.IncludeInSQLInterface = Trim(dbReader.GetValue(9).ToString())
                            rd.IncludeInTopologyUpdate = Trim(dbReader.GetValue(10).ToString())
                            rd.Row = Row
                            AddItem(rd)
                        End If
                    Next count
                Else
                    rd = New ReferenceData
                    rd.ReferenceTypeID = Replace(Trim(dbReader.GetValue(0).ToString()), "(TPNAME)", Replace(tp_name, "DC_", ""))
                    rd.ReferenceDataID = Trim(dbReader.GetValue(1).ToString())
                    rd.Description = Trim(dbReader.GetValue(2).ToString())
                    tputils.getDatatype(Trim(dbReader.GetValue(3).ToString()))
                    rd.Datatype = tputils.Datatype
                    rd.Datasize = tputils.Datasize
                    rd.Datascale = tputils.Datascale
                    rd.DupConstraint = Trim(dbReader.GetValue(4).ToString())
                    rd.Nullable = Trim(dbReader.GetValue(5).ToString())
                    rd.MaxAmount = 255
                    rd.UnivClass = Trim(dbReader.GetValue(6).ToString())
                    rd.UnivObject = Trim(dbReader.GetValue(7).ToString())
                    rd.UnivCondition = Trim(dbReader.GetValue(8).ToString())
                    rd.IncludeInSQLInterface = Trim(dbReader.GetValue(9).ToString())
                    rd.IncludeInTopologyUpdate = Trim(dbReader.GetValue(10).ToString())
                    rd.Row = Row
                    AddItem(rd)
                End If



            End If
        End While

        dbReader.Close()
        dbCommand.Dispose()

        If Not rts Is Nothing Then
            Me.getCommonTopology(tp_name, conn, dbCommand, dbReader, rts, logs)
        End If


        'test datas
        Dim testDatas As ReferenceDatas
        Dim testData As ReferenceDatas.ReferenceData
        Dim test_count As Integer
        Dim amount As Integer
        testDatas = Me
        For count = 1 To Me.Count
            rd = Item(count)
            amount = 0
            'data type check
            If rd.Datatype = "NOT FOUND" OrElse rd.Datasize = "Err" Then
                logs.AddLogText("Data Type in Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in Reference Table '" & rd.ReferenceTypeID & "' is not defined correctly.")
            End If
            'universe class check
            If rd.UnivClass.Length > 35 Then
                logs.AddLogText("Universe Class '" & rd.UnivClass & "' for Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in Reference Table '" & rd.ReferenceTypeID & "' exceeds maximum of 35 characters")
            End If
            'universe object check
            If rd.UnivObject.Length > 35 Then
                logs.AddLogText("Universe Object '" & rd.UnivObject & "' for Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in Reference Table '" & rd.ReferenceTypeID & "' exceeds maximum of 35 characters")
            End If
            'duplicate check
            For test_count = 1 To testDatas.Count
                testData = testDatas.Item(test_count)
                If rd.ReferenceTypeID = testData.ReferenceTypeID AndAlso rd.ReferenceDataID = testData.ReferenceDataID Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " for Reference Table '" & rd.ReferenceTypeID & "' has been defined " & amount & " times.")
            End If
        Next count

    End Sub

    ''
    ' Gets topology information defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Sub getCommonTopology(ByRef tp_name As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef rts As ReferenceTypes, ByRef logs As logMessages)
        Dim tputils = New TPUtilities
        Dim rd As ReferenceData
        Dim count As Integer
        Dim actual_table As String
        Dim Row As Integer

        Dim SupportedUpdates As String
        SupportedUpdates = "dynamic,predefined,static"

        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Common Reference Columns$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Row += 1
                For count = 1 To rts.Count
                    If rts.Item(count).Type = "table" Then
                        rd = New ReferenceData
                        If rts.Item(count).getUpdateMethod = LCase(Trim(dbReader.GetValue(0).ToString())) Then
                            rd.ReferenceTypeID = rts.Item(count).ReferenceTypeID
                            rd.ReferenceDataID = Trim(dbReader.GetValue(1).ToString())
                            rd.Description = Trim(dbReader.GetValue(2).ToString())
                            rd.givenDatatype = Trim(dbReader.GetValue(3).ToString())
                            tputils.getDatatype(Trim(dbReader.GetValue(3).ToString()))
                            rd.Datatype = tputils.Datatype
                            rd.Datasize = tputils.Datasize
                            rd.Datascale = tputils.Datascale
                            rd.DupConstraint = Trim(dbReader.GetValue(4).ToString())
                            rd.Nullable = Trim(dbReader.GetValue(5).ToString())
                            rd.MaxAmount = 255
                            rd.UnivClass = Trim(dbReader.GetValue(6).ToString())
                            rd.UnivObject = Trim(dbReader.GetValue(7).ToString())
                            rd.UnivCondition = Trim(dbReader.GetValue(8).ToString())
                            rd.IncludeInSQLInterface = Trim(dbReader.GetValue(9).ToString())
                            rd.IncludeInTopologyUpdate = Trim(dbReader.GetValue(10).ToString())
                            rd.Row = Row
                            AddItem(rd)
                        End If
                    End If
                Next count
            End If
        End While

        dbReader.Close()
        dbCommand.Dispose()

        'test datas
        Dim testDatas As ReferenceDatas
        Dim testData As ReferenceDatas.ReferenceData
        Dim test_count As Integer
        Dim amount As Integer
        testDatas = Me
        For count = 1 To Me.Count
            rd = Item(count)
            amount = 0
            'data type check
            If rd.Datatype = "NOT FOUND" OrElse rd.Datasize = "Err" Then
                logs.AddLogText("Data Type in Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in common columns for update method '" & rd.ReferenceTypeID & "' is not defined correctly.")
            End If
            'universe class check
            If rd.UnivClass.Length > 35 Then
                logs.AddLogText("Universe Class '" & rd.UnivClass & "' for Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in common columns for update method '" & rd.ReferenceTypeID & "' exceeds maximum of 35 characters")
            End If
            'universe object check
            If rd.UnivObject.Length > 28 Then
                logs.AddLogText("Universe Object '" & rd.UnivObject & "' for Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in common columns for update method '" & rd.ReferenceTypeID & "' exceeds maximum of 35 characters")
            End If
            'duplicate check
            For test_count = 1 To testDatas.Count
                testData = testDatas.Item(test_count)
                If rd.ReferenceTypeID = testData.ReferenceTypeID AndAlso rd.ReferenceDataID = testData.ReferenceDataID Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Column '" & rd.ReferenceDataID & "' at Row " & rd.Row & " in common columns for update method '" & rd.ReferenceTypeID & "' has been defined " & amount & " times.")
            End If
        Next count

    End Sub

    ''
    ' Gets vector topology information defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Sub getVectorTopology(ByRef mts As MeasurementTypes, ByRef logs As logMessages)
        Dim tputils = New TPUtilities
        Dim rd As ReferenceData
        Dim count As Integer
        Dim actual_table As String
        Dim Row As Integer
        Dim Vector_Found As Boolean

        Row = 1

        Dim cnt_count As Integer
        Dim cnts As Counters
        Dim cnt As Counters.Counter
        Vector_Found = False

        For count = 1 To mts.Count
            If mts.Item(count).RankTable = False Then
                cnts = mts.Item(count).Counters
                For cnt_count = 1 To cnts.Count
                    cnt = cnts.Item(cnt_count)
                    If cnt.CounterType = "VECTOR" Then
                        Vector_Found = True
                        actual_table = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                        rd = New ReferenceData
                        rd.ReferenceTypeID = actual_table
                        rd.ReferenceDataID = cnt.CounterName & "_DCVECTOR"
                        rd.Description = "Vector " & cnt.CounterName & " index"
                        tputils.getDatatype("integer")
                        rd.givenDatatype = "integer"
                        rd.Datatype = tputils.Datatype
                        rd.Datasize = tputils.Datasize
                        rd.Datascale = tputils.Datascale
                        rd.DupConstraint = 0
                        rd.Nullable = 1
                        rd.MaxAmount = 255
                        rd.UnivClass = ""
                        rd.UnivObject = ""
                        rd.UnivCondition = ""
                        rd.IncludeInSQLInterface = "x"
                        rd.IncludeInTopologyUpdate = "x"
                        rd.Row = cnt.Row
                        AddItem(rd)

                        rd = New ReferenceData
                        rd.ReferenceTypeID = actual_table
                        rd.ReferenceDataID = cnt.CounterName & "_VALUE"
                        rd.Description = "Vector " & cnt.CounterName & " real value"
                        tputils.getDatatype("varchar(50)")
                        rd.givenDatatype = "varchar(50)"
                        rd.Datatype = tputils.Datatype
                        rd.Datasize = tputils.Datasize
                        rd.Datascale = tputils.Datascale
                        rd.DupConstraint = 0
                        rd.Nullable = 1
                        rd.MaxAmount = 255
                        'get universe information from counters
                        rd.UnivClass = cnt.UnivClass
                        rd.UnivObject = "Vector: " & cnt.UnivObject
                        rd.UnivCondition = "x"
                        rd.IncludeInSQLInterface = "x"
                        rd.IncludeInTopologyUpdate = "x"
                        rd.Row = cnt.Row
                        AddItem(rd)

                        rd = New ReferenceData
                        rd.ReferenceTypeID = actual_table
                        rd.ReferenceDataID = "DC_RELEASE"
                        rd.Description = "Release information"
                        tputils.getDatatype("varchar(16)")
                        rd.givenDatatype = "varchar(16)"
                        rd.Datatype = tputils.Datatype
                        rd.Datasize = tputils.Datasize
                        rd.Datascale = tputils.Datascale
                        rd.DupConstraint = 0
                        rd.Nullable = 1
                        rd.MaxAmount = 255
                        rd.UnivClass = ""
                        rd.UnivObject = ""
                        rd.UnivCondition = ""
                        rd.IncludeInSQLInterface = "x"
                        rd.IncludeInTopologyUpdate = "x"
                        rd.Row = cnt.Row
                        AddItem(rd)
                    End If
                Next cnt_count
            End If
        Next count

        If Vector_Found = True Then


            'test datas
            Dim testDatas As ReferenceDatas
            Dim testData As ReferenceDatas.ReferenceData
            Dim test_count As Integer
            Dim amount As Integer
            testDatas = Me
            For count = 1 To Me.Count
                rd = Item(count)
                amount = 0
                'data type check
                If rd.Datatype = "NOT FOUND" OrElse rd.Datasize = "Err" Then
                    logs.AddLogText("Data Type in Column '" & rd.ReferenceDataID & "' at Vector Counter Row " & rd.Row & " in Reference Table '" & rd.ReferenceTypeID & "' is not defined correctly.")
                End If
                'universe class check
                If rd.UnivClass.Length > 35 Then
                    logs.AddLogText("Universe Class '" & rd.UnivClass & "' for Column '" & rd.ReferenceDataID & "' at Vector Counter Row " & rd.Row & " in Reference Table '" & rd.ReferenceTypeID & "' exceeds maximum of 35 characters")
                End If
                'universe object check
                If rd.UnivObject.Length > 35 Then
                    logs.AddLogText("Universe Object '" & rd.UnivObject & "' for Column '" & rd.ReferenceDataID & "' at Vector Counter Row " & rd.Row & " in Reference Table '" & rd.ReferenceTypeID & "' exceeds maximum of 35 characters")
                End If
                'duplicate check
                For test_count = 1 To testDatas.Count
                    testData = testDatas.Item(test_count)
                    If rd.ReferenceTypeID = testData.ReferenceTypeID AndAlso rd.ReferenceDataID = testData.ReferenceDataID Then
                        amount += 1
                    End If
                Next test_count
                If amount > 1 Then
                    logs.AddLogText("Column '" & rd.ReferenceDataID & "' at Vector Counter Row " & rd.Row & " for Reference Table '" & rd.ReferenceTypeID & "' has been defined " & amount & " times.")
                End If
            Next count
        End If

    End Sub

End Class

