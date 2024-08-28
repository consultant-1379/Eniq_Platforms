Option Strict Off

''
'  MeasurementTypes class is a collection of MeasurementType classes
'
Public NotInheritable Class MeasurementTypes
    Private _measurementtypes As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    ' Gets count of MeasurementType classes in MeasurementTypes class
    '
    ' @param Index Specifies the index in the MeasurementTypes class
    ' @return Count of MeasurementType classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _measurementtypes Is Nothing) Then
                Return _measurementtypes.Count
            End If
            Return 0
        End Get
    End Property

    ''
    ' Gets MeasurementType class from MeasurementTypes class based on given index.
    '
    ' @param Index Specifies the index in the MeasurementTypes class
    ' @return Reference to MeasurementType
    Public ReadOnly Property Item(ByVal Index As Integer) As MeasurementType
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_measurementtypes.Item(Index - 1), MeasurementType)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds MeasurementType class to MeasurementTypes class
    '
    ' @param ValueIn Specifies reference to MeasurementType
    Public Sub AddItem(ByVal ValueIn As MeasurementType)

        If (Not _measurementtypes Is Nothing) Then
            _measurementtypes.Add(ValueIn)
        End If

    End Sub

    ''
    '  MeasurementType class defines measurement types for technology package.
    '

    Public NotInheritable Class MeasurementType
        Private m_MeasurementTypeID As String
        Private m_VendorMeasurement As String
        Private m_Description As String
        Private m_MeasurementTypeClass As String
        Private m_MeasurementTypeClassDescription As String
        Private m_PartitionPlan As String
        Private m_DayAggregation As Boolean
        Private m_ObjectBusyHours As String
        Private m_ElementBusyHours As Boolean
        Private m_RankTable As Boolean
        Private m_CreateCountTable As Boolean
        'Private m_CountTable As Boolean
        Private m_CountSupport As String
        Private m_PlainTable As Boolean
        Private m_Joinable As String
        Private m_ExtendedUniverse As String
        Private m_counters As Counters
        Private m_counterkeys As CounterKeys
        Private m_publickeys As PublicKeys
        Private m_Row As Integer


        Public Property ExtendedUniverse() As String
            Get
                ExtendedUniverse = m_ExtendedUniverse
            End Get

            Set(ByVal Value As String)
                m_ExtendedUniverse = LCase(Value)
            End Set
        End Property

        Public Property CountSupport() As String
            Get
                CountSupport = m_CountSupport
            End Get

            Set(ByVal Value As String)
                m_CountSupport = UCase(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for PartitionPlan parameter. 
        ' Counters defines partitioning plan for measurement type. Values are one of the following values; Extra Small, Small, Medium, Large, Or Extra Large. 
        ' Other or empty values are converted to Medium. 
        '
        ' @param Value Specifies value of PartitionPlan parameter
        ' @return Value of PartitionPlan parameter
        Public Property PartitionPlan() As String
            Get
                PartitionPlan = m_PartitionPlan
            End Get

            Set(ByVal Value As String)
                m_PartitionPlan = LCase(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Counters parameter. 
        ' Counters defines counters for measurement type.
        '
        ' @param Value Specifies value of Counters parameter
        ' @return Value of Counters parameter
        Public Property Counters() As Counters
            Get
                Counters = m_counters
            End Get

            Set(ByVal Value As Counters)
                m_counters = Value
            End Set

        End Property

        ''
        ' Gets and sets value for VendorMeasurement parameter. 
        ' VendorMeasurement vendor name of measurement type.
        '
        ' @param Value Specifies value of VendorMeasurement parameter
        ' @return Value of VendorMeasurement parameter
        Public Property VendorMeasurement() As String
            Get
                VendorMeasurement = m_VendorMeasurement
            End Get

            Set(ByVal Value As String)
                m_VendorMeasurement = Value
            End Set

        End Property

        ''
        ' Gets and sets value for CounterKeys parameter. 
        ' CounterKeys defines keys for measurement type.
        '
        ' @param Value Specifies value of CounterKeys parameter
        ' @return Value of CounterKeys parameter
        Public Property CounterKeys() As CounterKeys
            Get
                CounterKeys = m_counterkeys
            End Get

            Set(ByVal Value As CounterKeys)
                m_counterkeys = Value
            End Set

        End Property

        ''
        ' Gets and sets value for MeasurementTypeID parameter. 
        ' MeasurementTypeID defines name of measurement type.
        '
        ' @param Value Specifies value of MeasurementTypeID parameter
        ' @return Value of MeasurementTypeID parameter
        Public Property MeasurementTypeID() As String
            Get
                MeasurementTypeID = m_MeasurementTypeID
            End Get

            Set(ByVal Value As String)
                m_MeasurementTypeID = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Description parameter. 
        ' Description defines description of measurement type.
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
        ' Gets and sets value for DayAggregation parameter. 
        ' DayAggregation defines whether measurement type is total aggregated for day.
        '
        ' @param Value Specifies value of DayAggregation parameter
        ' @return Value of DayAggregation parameter
        Public Property DayAggregation()
            Get
                DayAggregation = m_DayAggregation
            End Get

            Set(ByVal Value)
                If LCase(Value) = "" Then
                    m_DayAggregation = False
                Else
                    m_DayAggregation = True
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for ObjectBusyHours parameter. 
        ' ObjectBusyHours lists object busy hours aggregated for measurement type.
        '
        ' @param Value Specifies value of ObjectBusyHours parameter
        ' @return Value of ObjectBusyHours parameter
        Public Property ObjectBusyHours() As String
            Get
                ObjectBusyHours = m_ObjectBusyHours
            End Get

            Set(ByVal Value As String)
                m_ObjectBusyHours = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ElementBusyHours parameter. 
        ' ElementBusyHours list element busy hours aggregated for measurement type.
        '
        ' @param Value Specifies value of ElementBusyHours parameter
        ' @return Value of ElementBusyHours parameter
        Public Property ElementBusyHours()
            Get
                ElementBusyHours = m_ElementBusyHours
            End Get

            Set(ByVal Value)
                If LCase(Value) = "" Then
                    m_ElementBusyHours = False
                Else
                    m_ElementBusyHours = True
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for RankTable parameter. 
        ' RankTable defines whether measurement type is rank table.
        '
        ' @param Value Specifies value of RankTable parameter
        ' @return Value of RankTable parameter
        Public Property RankTable()
            Get
                RankTable = m_RankTable
            End Get

            Set(ByVal Value)
                If LCase(Value) = "" Then
                    m_RankTable = False
                Else
                    m_RankTable = True
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for CountTable parameter. 
        ' CountTable defines whether measurement type includes count table.
        '
        ' @param Value Specifies value of CountTable parameter
        ' @return Value of CountTable parameter
        Public Property CreateCountTable()
            Get
                CreateCountTable = m_CreateCountTable
            End Get

            Set(ByVal Value)
                If LCase(Value) = "x" OrElse LCase(Value) = "yes" OrElse InStrRev(LCase(Value), "yes") > 0 Then
                    m_CreateCountTable = True
                Else
                    m_CreateCountTable = False
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for PlainTable parameter. 
        ' PlainTable defines whether measurement type includes only plain table.
        '
        ' @param Value Specifies value of PlainTable parameter
        ' @return Value of PlainTable parameter
        Public Property PlainTable()
            Get
                PlainTable = m_PlainTable
            End Get

            Set(ByVal Value)
                If LCase(Value) = "" Then
                    m_PlainTable = False
                Else
                    m_PlainTable = True
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for Joinable parameter. 
        ' Joinable defines are PREV tables created or not.
        '
        ' @param Value Specifies value of Joinable parameter
        ' @return Value of Joinable parameter
  
        Public Property Joinable() As String

            Get
                Joinable = m_Joinable
            End Get

            Set(ByVal Value As String)
                m_Joinable = Value

            End Set
        End Property

        ''
        ' Gets and sets value for MeasurementTypeClass parameter. 
        ' MeasurementTypeClass defines measurement type class.
        '
        ' @param Value Specifies value of MeasurementTypeClass parameter
        ' @return Value of MeasurementTypeClass parameter
        Public Property MeasurementTypeClass() As String
            Get
                MeasurementTypeClass = m_MeasurementTypeClass
            End Get

            Set(ByVal Value As String)
                m_MeasurementTypeClass = Value
            End Set

        End Property

        ''
        ' Gets and sets value for MeasurementTypeClassDescription parameter. 
        ' MeasurementTypeClassDescription defines measurement type class description.
        '
        ' @param Value Specifies value of MeasurementTypeClassDescription parameter
        ' @return Value of MeasurementTypeClassDescription parameter
        Public Property MeasurementTypeClassDescription() As String
            Get
                MeasurementTypeClassDescription = m_MeasurementTypeClassDescription
            End Get

            Set(ByVal Value As String)
                m_MeasurementTypeClassDescription = Value
            End Set

        End Property

        ''
        ' Gets and sets value for PublicKeys parameter. 
        ' PublicKeys defines reference to public keys of the measurement type.
        '
        ' @param Value Specifies value of PublicKeys parameter
        ' @return Value of PublicKeys parameter
        Public Property PublicKeys() As PublicKeys
            Get
                PublicKeys = m_publickeys
            End Get

            Set(ByVal Value As PublicKeys)
                m_publickeys = Value
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
        ' Prints insert clauses to metadata for measurement type.
        '
        ' @param iFileNum Specifies handle to output file
        ' @param PackRel Specifies technology package name
        Public Sub PrintMeasurementInformation(ByVal iFileNum As Short, ByRef PackRel As String, ByRef TechPackName As String, ByRef TPVersion As String, ByRef TechPackType As String)
            Dim cnt As Integer
            PrintLine(iFileNum, "insert into MeasurementType " & _
            "(typeid, typeclassid, typename, vendorid, foldername, description, versionid, objectid, objectname, joinable) " & _
            "values (" & _
            "'" & PackRel & ":" & MeasurementTypeID & "'," & _
            "'" & PackRel & ":" & MeasurementTypeClass & "'," & _
            "'" & MeasurementTypeID & "'," & _
            "'" & TechPackName & "'," & _
            "'" & MeasurementTypeID & "'," & _
            "'" & Description & "'," & _
            "'" & PackRel & "'," & _
            "'" & PackRel & ":" & MeasurementTypeID & "'," & _
            "'" & MeasurementTypeID & "'," & _
            "'" & Joinable & "');")

            For cnt = 1 To CounterKeys.Count
                PrintLine(iFileNum, "insert into MeasurementKey " & _
                "(typeid, dataname, description, iselement, uniquekey) " & _
                "values (" & _
                "'" & PackRel & ":" & CounterKeys.Item(cnt).MeasurementTypeID & "'," & _
                "'" & CounterKeys.Item(cnt).CounterKeyName & "'," & _
                "'" & CounterKeys.Item(cnt).Description & "'," & _
                CounterKeys.Item(cnt).Element & "," & _
                CounterKeys.Item(cnt).DupConstraint & ");")
            Next cnt
            'Add DCVECTOR_INDEX
            For cnt = 1 To Counters.Count
                If Counters.Item(cnt).CounterType = "VECTOR" Then
                    PrintLine(iFileNum, "insert into MeasurementKey " & _
                    "(typeid, dataname, description, iselement, uniquekey) " & _
                    "values (" & _
                    "'" & PackRel & ":" & MeasurementTypeID & "'," & _
                    "'DCVECTOR_INDEX'," & _
                    "'Vector Index'," & _
                    "0," & _
                    "1);")
                    Exit For
                End If
            Next cnt
            If RankTable = False Then
                For cnt = 1 To Counters.Count
                    Dim CountAggregation As String
                    CountAggregation = ""
                    'incremental to cumulative support
                    If Me.CountSupport = "X" OrElse Me.CountSupport = "YES" OrElse Me.CountSupport = "NO" OrElse Me.CountSupport = "" Then
                        CountAggregation = Counters.Item(cnt).CountAggr
                    Else
                        CountAggregation = Me.CountSupport()
                        CountAggregation = Replace(CountAggregation, ";YES", ";" & Counters.Item(cnt).CountAggr)
                        CountAggregation = Replace(CountAggregation, ";NO", ";GAUGE")
                    End If

                    PrintLine(iFileNum, "insert into MeasurementCounter " & _
                    "(typeid, dataname, description, timeaggregation, groupaggregation, countaggregation) " & _
                    "values (" & _
                    "'" & PackRel & ":" & Counters.Item(cnt).MeasurementTypeID & "'," & _
                    "'" & Counters.Item(cnt).CounterName & "'," & _
                    "'" & Counters.Item(cnt).Description & "'," & _
                    "'" & Counters.Item(cnt).TimeAggr & "'," & _
                    "'" & Counters.Item(cnt).GroupAggr & "'," & _
                    "'" & CountAggregation & "');")
                Next cnt
            End If
            If RankTable = False Then
                If PlainTable = False Then
                    PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType, "RAW")
                End If
                If DayAggregation = True Then
                    PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType, "DAY")
                End If
                If ObjectBusyHours <> "" Then
                    PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType, "DAYBH")
                End If
                If CreateCountTable = True Then
                    PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType, "COUNT")
                End If
                If PlainTable = True Then
                    PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType, "PLAIN")
                End If
            Else
                PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType, "RANKBH")
            End If
        End Sub

        ''
        ' Prints insert clauses to metadata for measurement type's table level. 
        '
        ' @param iFileNum Specifies handle to output file
        ' @param PackRel Specifies technology package name
        ' @param TLevel Specifies table level
        Public Sub PrintMeasurementInformation(ByVal iFileNum As Short, ByRef PackRel As String, ByRef TechPackName As String, ByRef TPVersion As String, ByRef TechPackType As String, ByRef TLevel As String)
            Dim cnt As Integer
            Dim colNumber As Integer
            If TLevel = "PLAIN" Then
                PrintLine(iFileNum, "insert into MeasurementTable " & _
                "(mtableid, typeid, tablelevel, basetablename, partitionplan) " & _
                "values (" & _
                "'" & PackRel & ":" & MeasurementTypeID & ":" & TLevel & "'," & _
                "'" & PackRel & ":" & MeasurementTypeID & "'," & _
                "'" & TLevel & "'," & _
                "'" & MeasurementTypeID & "'," & _
                "'" & PartitionPlan & "_" & LCase(TLevel) & "');")
            Else
                PrintLine(iFileNum, "insert into MeasurementTable " & _
                "(mtableid, typeid, tablelevel, basetablename, partitionplan) " & _
                "values (" & _
                "'" & PackRel & ":" & MeasurementTypeID & ":" & TLevel & "'," & _
                "'" & PackRel & ":" & MeasurementTypeID & "'," & _
                "'" & TLevel & "'," & _
                "'" & MeasurementTypeID & "_" & TLevel & "'," & _
                "'" & PartitionPlan & "_" & LCase(TLevel) & "');")
            End If

            'PrintLine(iFileNum, "insert into TypeActivation " & _
            '"(techpack_name, typename, tablelevel, type) " & _
            '"values (" & _
            '"'" & TechPackName & "'," & _
            '"'" & PackRel & ":" & MeasurementTypeID & "'," & _
            '"'" & TLevel & "'," & _
            '"'" & TechPackType & "');")

            For cnt = 1 To CounterKeys.Count
                PrintLine(iFileNum, "insert into MeasurementColumn " & _
                "(mtableid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes, description, dataid, releaseid, uniquekey, includesql) " & _
                "values (" & _
                "'" & PackRel & ":" & MeasurementTypeID & ":" & TLevel & "'," & _
                CounterKeys.Item(cnt).ColNumber & "," & _
                "'" & CounterKeys.Item(cnt).CounterKeyName & "'," & _
                "'" & CounterKeys.Item(cnt).Datatype & "'," & _
                CounterKeys.Item(cnt).Datasize & "," & _
                CounterKeys.Item(cnt).Datascale & "," & _
                CounterKeys.Item(cnt).MaxAmount & "," & _
                CounterKeys.Item(cnt).Nullable & "," & _
                "'" & CounterKeys.Item(cnt).IQIndex & "'," & _
                "'" & CounterKeys.Item(cnt).Description & "'," & _
                "'" & CounterKeys.Item(cnt).CounterKeyName & "'," & _
                "'" & TPVersion & "'," & _
                CounterKeys.Item(cnt).DupConstraint & "," & _
                CounterKeys.Item(cnt).IncludeInSQLInterface & ");")
                colNumber = CounterKeys.Item(cnt).ColNumber
            Next cnt

            'Add DCVECTOR_INDEX
            For cnt = 1 To Counters.Count
                If Counters.Item(cnt).CounterType = "VECTOR" Then '
                    colNumber += 1
                    PrintLine(iFileNum, "insert into MeasurementColumn " & _
                    "(mtableid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes, description, dataid, releaseid, uniquekey, includesql) " & _
                    "values (" & _
                    "'" & PackRel & ":" & MeasurementTypeID & ":" & TLevel & "'," & _
                    colNumber & "," & _
                    "'DCVECTOR_INDEX'," & _
                    "'integer'," & _
                    "0," & _
                    "0," & _
                    "255," & _
                    "1," & _
                    "'HG'," & _
                    "'Vector Index'," & _
                    "'DCVECTOR_INDEX'," & _
                    "'" & TPVersion & "'," & _
                    "1," & _
                    "1);")
                    Exit For
                End If
            Next cnt
            For cnt = 1 To PublicKeys.Count
                If PublicKeys.Item(cnt).KeyType = TLevel Then
                    PrintLine(iFileNum, "insert into MeasurementColumn " & _
                    "(mtableid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes, description, dataid, releaseid, uniquekey, includesql) " & _
                    "values (" & _
                    "'" & PackRel & ":" & MeasurementTypeID & ":" & PublicKeys.Item(cnt).KeyType & "'," & _
                    PublicKeys.Item(cnt).ColNumber & "," & _
                    "'" & PublicKeys.Item(cnt).PublicKeyName & "'," & _
                    "'" & PublicKeys.Item(cnt).Datatype & "'," & _
                    PublicKeys.Item(cnt).Datasize & "," & _
                    PublicKeys.Item(cnt).Datascale & "," & _
                    PublicKeys.Item(cnt).MaxAmount & "," & _
                    PublicKeys.Item(cnt).Nullable & "," & _
                    "'" & PublicKeys.Item(cnt).IQIndex & "'," & _
                    "'" & PublicKeys.Item(cnt).Description & "'," & _
                    "'" & PublicKeys.Item(cnt).PublicKeyName & "'," & _
                    "'" & TPVersion & "'," & _
                    PublicKeys.Item(cnt).DupConstraint & "," & _
                    PublicKeys.Item(cnt).IncludeInSQLInterface & ");")
                End If
            Next cnt
            If TLevel <> "RANKBH" Then
                For cnt = 1 To Counters.Count
                    PrintLine(iFileNum, "insert into MeasurementColumn " & _
                    "(mtableid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes, description, dataid, releaseid, uniquekey, includesql) " & _
                    "values (" & _
                    "'" & PackRel & ":" & Counters.Item(cnt).MeasurementTypeID & ":" & TLevel & "'," & _
                    Counters.Item(cnt).ColNumber & "," & _
                    "'" & Counters.Item(cnt).CounterName & "'," & _
                    "'" & Counters.Item(cnt).Datatype & "'," & _
                    Counters.Item(cnt).Datasize & "," & _
                    "" & Counters.Item(cnt).Datascale & "," & _
                    Counters.Item(cnt).MaxAmount & "," & _
                    "1," & _
                    "'" & Counters.Item(cnt).IndexValue & "'," & _
                    "'" & Counters.Item(cnt).Description & "'," & _
                    "'" & Counters.Item(cnt).CounterName & "'," & _
                    "'" & TPVersion & "'," & _
                    "0," & _
                    Counters.Item(cnt).IncludeInSQLInterface & ");")
                Next cnt
            End If
        End Sub

    End Class

    ''
    ' Gets measurements. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @param all_cnts Specifies reference to Counters class
    ' @param all_cnt_keys Specifies reference to CounterKeys class
    ' @param pub_keys Specifies reference to PublicKeys class
    ' @param all_bh_types Specifies reference to BHTypes class
    ' @param all_bhobjs Specifies reference to BHObjects class
    ', ByRef all_bh_types As BHTypes, ByRef all_bhobjs As BHObjects
    Public Function getMeasurements(ByRef TechPackName As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef all_cnts As Counters, ByRef all_cnt_keys As CounterKeys, ByRef pub_keys As PublicKeys, ByRef logs As logMessages) As Boolean

        Dim PrevMeas As String
        PrevMeas = ""
        Dim count As Integer
        Dim ColCount As Integer
        Dim Result As MsgBoxResult
        Dim mt As MeasurementTypes.MeasurementType
        Dim Row As Integer
        Dim tempVendorReleases As String
        Dim VendorReleases() As String
        Dim CountSupports() As String
        Dim YesCount As String
        Dim NoCount As String
        Dim TmpCol As Integer

        TmpCol = 0
        Row = 1

        Dim tp_utils = New TPUtilities
        tempVendorReleases = tp_utils.readSingleValue("Coversheet$B7:B8", conn, dbCommand, dbReader)
        VendorReleases = Split(tempVendorReleases, ",")
        tp_utils = Nothing

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [MeasurementTypes$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Row += 1
                mt = New MeasurementTypes.MeasurementType
                mt.MeasurementTypeID = Trim(dbReader.GetValue(0).ToString())
                mt.VendorMeasurement = Trim(dbReader.GetValue(1).ToString())
                mt.Description = Trim(dbReader.GetValue(2).ToString())
                'Original
                'mt.MeasurementTypeClass = Trim(dbReader.GetValue(3).ToString())
                mt.MeasurementTypeClassDescription = Trim(dbReader.GetValue(4).ToString())
                'Updated
                mt.MeasurementTypeClass = TechPackName & "_" & Replace(mt.MeasurementTypeClassDescription, " ", "_")
                mt.PartitionPlan = Trim(dbReader.GetValue(5).ToString())
                mt.DayAggregation = Trim(dbReader.GetValue(6).ToString())
                mt.ObjectBusyHours = Trim(dbReader.GetValue(7).ToString())
                mt.ElementBusyHours = Trim(dbReader.GetValue(8).ToString())
                mt.RankTable = Trim(dbReader.GetValue(9).ToString())
                mt.CreateCountTable = Trim(dbReader.GetValue(10).ToString())
                mt.CountSupport = Trim(dbReader.GetValue(10).ToString())

                'count support check
                If tempVendorReleases <> "" Then
                    CountSupports = Split(mt.CountSupport, ",")
                    If mt.CountSupport <> "X" AndAlso mt.CountSupport <> "YES" AndAlso mt.CountSupport <> "NO" AndAlso mt.CountSupport <> "" Then
                        If UBound(CountSupports) <> UBound(VendorReleases) Then
                            logs.AddLogText("Table '" & mt.MeasurementTypeID & "' count support does not match number of vendor releases.")
                        Else
                            YesCount = ""
                            NoCount = ""
                            For count = 0 To UBound(VendorReleases)
                                If UCase(CountSupports(count)) = "YES" Then
                                    If YesCount = "" Then
                                        YesCount &= VendorReleases(count)
                                    Else
                                        YesCount &= "," & VendorReleases(count)
                                    End If
                                End If
                                If UCase(CountSupports(count)) = "NO" Then
                                    If NoCount = "" Then
                                        NoCount &= VendorReleases(count)
                                    Else
                                        NoCount &= "," & VendorReleases(count)
                                    End If
                                End If
                            Next count
                            mt.CountSupport = YesCount & ";YES/" & NoCount & ";NO"
                        End If
                    End If
                End If

                mt.PlainTable = Trim(dbReader.GetValue(11).ToString())

                If dbReader.FieldCount < 14 Then
                    If dbReader.FieldCount = 13 Then
                        mt.Joinable = ""
                        mt.ExtendedUniverse = ""
                        If TmpCol <> 3 Then
                            TmpCol = 1
                        End If
                    ElseIf dbReader.FieldCount = 12 Then
                        mt.ExtendedUniverse = ""
                        mt.Joinable = ""
                        If TmpCol <> 3 Then
                            TmpCol = 2
                        End If
                    End If
                Else
                    mt.ExtendedUniverse = Trim(dbReader.GetValue(12).ToString())
                    mt.Joinable = Trim(dbReader.GetValue(13).ToString())
                End If

                If TmpCol = 1 Then
                    MsgBox("Check used Tech Pack Definition Template. MeasurementTypes:Universe Extension or Joinable column missing.")
                    TmpCol = 3
                ElseIf TmpCol = 2 Then
                    MsgBox("Check used Tech Pack Definition Template. MeasurementTypes:Universe Extension and Joinable columns missing.")
                    TmpCol = 3
                End If

                mt.Row = Row
                'Add counters
                Dim cnts = New Counters
                For count = 1 To all_cnts.Count
                    If all_cnts.Item(count).MeasurementTypeID = mt.MeasurementTypeID Then
                        cnts.AddItem(all_cnts.Item(count))
                    End If
                Next count
                mt.Counters = cnts
                ColCount = 100
                For count = 1 To mt.Counters.Count
                    ColCount += 1
                    mt.Counters.Item(count).ColNumber = ColCount
                Next count

                'Add counter keys
                Dim cnt_keys = New CounterKeys
                For count = 1 To all_cnt_keys.Count
                    If all_cnt_keys.Item(count).MeasurementTypeID = mt.MeasurementTypeID Then
                        cnt_keys.AddItem(all_cnt_keys.Item(count))
                    End If
                Next count
                mt.CounterKeys = cnt_keys
                ColCount = 0
                For count = 1 To mt.CounterKeys.Count
                    ColCount += 1
                    mt.CounterKeys.Item(count).ColNumber = ColCount
                Next count

                'Add public keys
                Dim mt_pub_keys = New PublicKeys
                Dim pub_key As PublicKeys.PublicKey
                Dim PrevType As String
                PrevType = ""
                ColCount = 50
                For count = 1 To pub_keys.Count
                    pub_key = pub_keys.Item(count)
                    If pub_key.KeyType <> PrevType Then
                        ColCount = 50
                    End If
                    ColCount += 1
                    pub_key.ColNumber = ColCount
                    mt_pub_keys.AddItem(pub_key)
                    PrevType = pub_key.KeyType
                Next count
                mt.PublicKeys = mt_pub_keys
                AddItem(mt)
                End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test measurements
        Dim testMts As MeasurementTypes
        Dim testMt As MeasurementTypes.MeasurementType
        Dim test_count As Integer
        Dim test_count2 As Integer
        Dim amount As Integer
        Dim found As Boolean
        testMts = Me
        For count = 1 To Me.Count
            mt = Item(count)
            amount = 0
            'name check
            If mt.MeasurementTypeID.Length > 27 Then
                logs.AddLogText("Table '" & mt.MeasurementTypeID & "'  at Row " & mt.Row & " exceeds maximum of 27 characters.")
            End If
            'description check
            If InStrRev(mt.Description, "'") > 0 OrElse InStrRev(mt.Description, ControlChars.Quote) > 0 Then
                logs.AddLogText("Table '" & mt.MeasurementTypeID & "' description at Row " & mt.Row & " contains invalid characters.")
            End If
            'partition plan check
            If mt.PartitionPlan <> "extrasmall" AndAlso mt.PartitionPlan <> "small" AndAlso mt.PartitionPlan <> "medium" AndAlso mt.PartitionPlan <> "large" AndAlso mt.PartitionPlan <> "extralarge" Then
                logs.AddLogText("Partition Plan set to default 'medium' in Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                mt.PartitionPlan = "medium"
            End If
            'rank table name check
            If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = True Then
                If mt.MeasurementTypeID <> TechPackName & "_" & UCase(mt.ObjectBusyHours) & "BH" Then
                    logs.AddLogText("Rank table at Row " & mt.Row & " for Object Busy Hours '" & mt.ObjectBusyHours & "' should be named as '" & TechPackName & "_" & UCase(mt.ObjectBusyHours) & "BH'.")
                End If
            End If
            If mt.ElementBusyHours = True AndAlso mt.RankTable = True Then
                If mt.MeasurementTypeID <> TechPackName & "_ELEMBH" Then
                    logs.AddLogText("Rank table at Row " & mt.Row & " for Element Busy Hours should be named as '" & TechPackName & "_ELEMBH'.")
                End If
            End If
            'counters check
            If mt.Counters.Count = 0 And mt.RankTable = False Then
                logs.AddLogText("No Counters have been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
            End If
            'keys check
            If mt.CounterKeys.Count = 0 Then
                logs.AddLogText("No Keys have been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
            End If
            If mt.RankTable = False Then
                'public keys check (RAW)
                found = False
                For test_count = 1 To mt.PublicKeys.Count
                    If mt.PublicKeys.Item(test_count).KeyType = "RAW" Then
                        found = True
                    End If
                Next test_count
                If found = False Then
                    logs.AddLogText("No Public Keys defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                End If
                If mt.CreateCountTable = True Then
                    'public keys check (COUNT)
                    found = False
                    For test_count = 1 To mt.PublicKeys.Count
                        If mt.PublicKeys.Item(test_count).KeyType = "COUNT" Then
                            found = True
                        End If
                    Next test_count
                    If found = False Then
                        logs.AddLogText("No Public Keys defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                End If
                If mt.DayAggregation = True Then
                    'public keys check (DAY)
                    found = False
                    For test_count = 1 To mt.PublicKeys.Count
                        If mt.PublicKeys.Item(test_count).KeyType = "DAY" Then
                            found = True
                        End If
                    Next test_count
                    If found = False Then
                        logs.AddLogText("No Public Keys defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                End If
                If mt.ObjectBusyHours <> "" Then
                    'public keys check (DAYBH)
                    found = False
                    For test_count = 1 To mt.PublicKeys.Count
                        If mt.PublicKeys.Item(test_count).KeyType = "DAYBH" Then
                            found = True
                        End If
                    Next test_count
                    If found = False Then
                        logs.AddLogText("No Public Keys defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                End If
                If mt.PlainTable = True Then
                    'public keys check (PLAIN)
                    found = False
                    For test_count = 1 To mt.PublicKeys.Count
                        If mt.PublicKeys.Item(test_count).KeyType = "PLAIN" Then
                            found = True
                        End If
                    Next test_count
                    If found = False Then
                        logs.AddLogText("No Public Keys defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                End If
            End If
            If mt.RankTable = True Then
                'public keys check (RANKBH)
                found = False
                For test_count = 1 To mt.PublicKeys.Count
                    If mt.PublicKeys.Item(test_count).KeyType = "RANKBH" Then
                        found = True
                    End If
                Next test_count
                If found = False Then
                    logs.AddLogText("No Public Keys defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                End If
            End If

            'same keys and counters check
            For test_count = 1 To mt.CounterKeys.Count
                For test_count2 = 1 To mt.Counters.Count
                    If mt.CounterKeys.Item(test_count).CounterKeyName = mt.Counters.Item(test_count2).CounterName Then
                        logs.AddLogText("Same Key and Counter '" & mt.CounterKeys.Item(test_count).CounterKeyName & "' has been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                Next test_count2
            Next test_count
            'same keys and public keys check
            For test_count = 1 To mt.CounterKeys.Count
                For test_count2 = 1 To mt.PublicKeys.Count
                    If mt.CounterKeys.Item(test_count).CounterKeyName = mt.PublicKeys.Item(test_count2).PublicKeyName Then
                        logs.AddLogText("Same Key and Public Key '" & mt.CounterKeys.Item(test_count).CounterKeyName & "' has been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                Next test_count2
            Next test_count
            'same counters and public keys check
            For test_count = 1 To mt.Counters.Count
                For test_count2 = 1 To mt.PublicKeys.Count
                    If mt.Counters.Item(test_count).CounterName = mt.PublicKeys.Item(test_count2).PublicKeyName Then
                        logs.AddLogText("Same Counter and Public Key '" & mt.Counters.Item(test_count).CounterName & "' has been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                Next test_count2
            Next test_count
            'object busy hour check
            Dim BusyHourObjects() As String
            Dim bh_count As Integer
            If mt.ObjectBusyHours <> "" Then
                BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                For bh_count = 0 To UBound(BusyHourObjects)
                    amount = 0
                    For test_count = 1 To testMts.Count
                        testMt = testMts.Item(test_count)
                        If testMt.RankTable = True AndAlso BusyHourObjects(bh_count) = testMt.ObjectBusyHours Then
                            amount += 1
                        End If
                    Next test_count
                    If amount < 1 Then
                        logs.AddLogText("No Ranking table has been defined for supported Object Busy Hour '" & BusyHourObjects(bh_count) & "' at Row " & mt.Row & ".")
                    End If
                    If amount > 1 Then
                        logs.AddLogText("Ranking table at Row " & mt.Row & " for supported Object Busy Hour '" & BusyHourObjects(bh_count) & "' has been defined " & amount & " times.")
                    End If
                Next bh_count
            End If
            amount = 0
            'element busy hour check
            If mt.ElementBusyHours = True Then
                For test_count = 1 To testMts.Count
                    testMt = testMts.Item(test_count)
                    If testMt.RankTable = True AndAlso mt.ElementBusyHours = testMt.ElementBusyHours Then
                        amount += 1
                    End If
                Next test_count
                If amount < 1 Then
                    logs.AddLogText("No Ranking table has been defined for Element Busy Hours at Row " & mt.Row & ".")
                End If
                If amount > 1 Then
                    logs.AddLogText("Ranking table at Row " & mt.Row & " for Element Busy Hours has been defined " & amount & " times.")
                End If
            End If
            amount = 0
            'duplicate check
            For test_count = 1 To testMts.Count
                testMt = testMts.Item(test_count)
                If mt.MeasurementTypeID = testMt.MeasurementTypeID Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Fact Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & " has been defined " & amount & " times.")
                Return False
            End If
        Next count

        Return True

    End Function

End Class
