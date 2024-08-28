Option Strict Off

''
'  MeasurementTypes class is a collection of MeasurementType classes
'
Public NotInheritable Class MeasurementTypesTPIde
    Private _measurementtypes As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    ' Gets the ArrayList of MeasurementType objects in MeasurementTypes class
    ' @return The ArrayList of MeasurementType objects
    Public ReadOnly Property MeasurementTypes() As System.Collections.ArrayList
        Get
            If (Not _measurementtypes Is Nothing) Then
                Return _measurementtypes
            End If
        End Get
    End Property

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
        Private m_TypeName As String
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
        Private m_VectorSupport As Boolean
        Private m_Deltacalcsupport As Boolean
        Private m_Joinable As String
        Private m_ExtendedUniverse As String
        Private m_counters As CountersTPIde
        Private m_counterkeys As CounterKeysTPIde
        Private m_publickeys As PublicKeysTPIde
        Private m_Row As Integer


        Public Property TypeName() As String
            Get
                TypeName = m_TypeName
            End Get

            Set(ByVal Value As String)
                m_TypeName = Value  'removed UCase
            End Set

        End Property

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
        Public Property Counters() As CountersTPIde
            Get
                Counters = m_counters
            End Get

            Set(ByVal Value As CountersTPIde)
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
        Public Property CounterKeys() As CounterKeysTPIde
            Get
                CounterKeys = m_counterkeys
            End Get

            Set(ByVal Value As CounterKeysTPIde)
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
                If Value = "0" Then
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
                If Value = "0" Then
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
                If Value = "0" Then
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
                m_CreateCountTable = Value
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
                If Value = "0" Then
                    m_PlainTable = False
                Else
                    m_PlainTable = True
                End If
            End Set

        End Property

        ' VectorSupport
        ''
        ' Gets and sets value for VectorSupport parameter. 
        ' VectorSupport defines whether measurement type has vector support.
        '
        ' @param Value Specifies value of VectorSupport parameter
        ' @return Value of VectorSupport parameter
        Public Property VectorSupport() As Boolean
            Get
                VectorSupport = m_VectorSupport
            End Get

            Set(ByVal Value As Boolean)
                m_VectorSupport = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Deltacalcsupport parameter. 
        ' Deltacalcsupport defines whether measurement type includes calc table.
        '
        ' @param Value Specifies value of Deltacalcsupport parameter
        ' @return Value of Deltacalcsupport parameter
        Public Property Deltacalcsupport()
            Get
                Deltacalcsupport = m_Deltacalcsupport
            End Get

            Set(ByVal Value)
                If Value = "1" Then
                    m_Deltacalcsupport = True
                Else
                    m_Deltacalcsupport = False
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
        Public Property PublicKeys() As PublicKeysTPIde
            Get
                PublicKeys = m_publickeys
            End Get

            Set(ByVal Value As PublicKeysTPIde)
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
    Public Function getMeasurements(ByRef TechPackName As String, ByRef conn As System.Data.Odbc.OdbcConnection, _
                                    ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                                    ByRef all_cnts As CountersTPIde, ByRef all_cnt_keys As CounterKeysTPIde, ByRef pub_keys As PublicKeysTPIde, _
                                    ByRef TechPackTPIde As String) As Boolean

        Dim PrevMeas As String
        PrevMeas = ""
        Dim count As Integer
        Dim ColCount As Integer
        Dim Result As MsgBoxResult
        Dim mt As MeasurementTypesTPIde.MeasurementType
        Dim Row As Integer
        Dim tempVendorReleases As String
        Dim VendorReleases() As String
        Dim CountSupports() As String
        Dim YesCount As String
        Dim NoCount As String
        Dim tempDeltaSupport As String
        Dim TmpCol As Integer


        TmpCol = 0
        Row = 1

        Dim tp_utils = New TPUtilitiesTPIde
        Dim supvenrel As String
        supvenrel = "SELECT * FROM SupportedVendorRelease WHERE VERSIONID ='" & TechPackTPIde & "'"
        tempVendorReleases = tp_utils.readSingleValue(supvenrel, conn, dbCommand, dbReader, TechPackTPIde)
        VendorReleases = Split(tempVendorReleases, ",")


        Dim meastype As String
        meastype = "SELECT t.TYPEID,t.TYPECLASSID,TYPENAME,VENDORID," & _
        "SUBSTR(t.DESCRIPTION,1,8000),SUBSTR(t.DESCRIPTION,8001,8000),SUBSTR(t.DESCRIPTION,16001,8000),SUBSTR(t.DESCRIPTION,24001,8000)" & _
        ",JOINABLE,SIZING,TOTALAGG,ELEMENTBHSUPPORT,RANKINGTABLE,PLAINTABLE,DELTACALCSUPPORT,UNIVERSEEXTENSION," & _
        "SUBSTR(c.DESCRIPTION,1,8000),SUBSTR(c.DESCRIPTION,8001,8000),SUBSTR(c.DESCRIPTION,16001,8000),SUBSTR(c.DESCRIPTION,24001,8000)" & _
        ", t.VECTORSUPPORT FROM MeasurementType t, MeasurementTypeClass c WHERE c.TYPECLASSID=t.TYPECLASSID AND t.VERSIONID='" & TechPackTPIde & "'"

        dbCommand = New System.Data.Odbc.OdbcCommand(meastype, conn)
        'Modified / Added for HK80515 TR
        Console.WriteLine("Gets measurements ")
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
            Try
                If dbReader.GetValue(0).ToString() = "" Then
                    Exit While
                Else
                    Row += 1
                    mt = New MeasurementTypesTPIde.MeasurementType
                    mt.MeasurementTypeID = Trim(dbReader.GetValue(0).ToString())
                    mt.MeasurementTypeClass = Trim(dbReader.GetValue(1).ToString())
                    mt.TypeName = Trim(dbReader.GetValue(2).ToString())
                    mt.VendorMeasurement = Trim(dbReader.GetValue(3).ToString())
                    mt.Description = getDescription(mt, dbReader)
                    mt.Joinable = Trim(dbReader.GetValue(8).ToString())
                    mt.PartitionPlan = Trim(dbReader.GetValue(9).ToString())
                    mt.DayAggregation = Trim(dbReader.GetValue(10).ToString())
                    mt.ElementBusyHours = Trim(dbReader.GetValue(11).ToString())
                    mt.RankTable = Trim(dbReader.GetValue(12).ToString())
                    mt.PlainTable = Trim(dbReader.GetValue(13).ToString())
                    mt.Deltacalcsupport = Trim(dbReader.GetValue(14).ToString())
                    mt.ExtendedUniverse = Trim(dbReader.GetValue(15).ToString())

                    'mt.CountSupport = ""
                    mt.ObjectBusyHours = ""
                    If dbReader.IsDBNull(16) = False Then
                        mt.MeasurementTypeClassDescription = Trim(dbReader.GetString(16) + dbReader.GetString(17) + dbReader.GetString(18) + dbReader.GetString(19))
                    Else
                        mt.MeasurementTypeClassDescription = ""
                    End If

                    ' Check if vector support is enabled for the measurment type:
                    Dim vectorSupportColValue As String = dbReader.GetValue(20).ToString()
                    mt.VectorSupport = getVectorSupport(vectorSupportColValue)

                    'count support check
                    'If tempVendorReleases <> "" Then
                    'CountSupports = Split(mt.CountSupport, ",")
                    'If mt.CountSupport <> "X" AndAlso mt.CountSupport <> "YES" AndAlso mt.CountSupport <> "NO" AndAlso mt.CountSupport <> "" Then
                    'If UBound(CountSupports) <> UBound(VendorReleases) Then
                    'Trace.Writeline("Table '" & mt.TypeName & "' count support does not match number of vendor releases.")
                    'Else
                    'YesCount = ""
                    'NoCount = ""
                    'For count = 0 To UBound(VendorReleases)
                    'If UCase(CountSupports(count)) = "YES" Then
                    'If YesCount = "" Then
                    'YesCount &= VendorReleases(count)
                    'Else
                    'YesCount &= "," & VendorReleases(count)
                    'End If
                    'End If
                    'If UCase(CountSupports(count)) = "NO" Then
                    'If NoCount = "" Then
                    'NoCount &= VendorReleases(count)
                    'Else
                    'NoCount &= "," & VendorReleases(count)
                    'End If
                    'End If
                    'Next count
                    'mt.CountSupport = YesCount & ";YES/" & NoCount & ";NO"
                    'End If
                    'End If
                    'End If

                    mt.Row = Row
                    'Add counters
                    Dim cnts = New CountersTPIde
                    For count = 1 To all_cnts.Count
                        If all_cnts.Item(count).MeasurementTypeID = mt.MeasurementTypeID Then
                            all_cnts.Item(count).TypeName = mt.TypeName
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
                    Dim cnt_keys = New CounterKeysTPIde
                    For count = 1 To all_cnt_keys.Count
                        If all_cnt_keys.Item(count).MeasurementTypeID = mt.MeasurementTypeID Then
                            all_cnt_keys.Item(count).TypeName = mt.TypeName
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
                    Dim mt_pub_keys = New PublicKeysTPIde
                    Dim pub_key As PublicKeysTPIde.PublicKey
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
            Catch ex As Exception
                Trace.WriteLine("Error getting measurement type: " & ex.ToString())
                Trace.WriteLine("Measurement type ID: " & mt.TypeName)
            End Try
        End While
        dbReader.Close()
        dbCommand.Dispose()



        'update delta and busy hour support
        For count = 1 To Me.Count
            mt = Item(count)

            mt.CreateCountTable = False
            If mt.Deltacalcsupport = True Then
                Dim deltaSupport As String
                'deltaSupport = "SELECT VENDORRELEASE || ';' || DELTACALCSUPPORT AS DELTA FROM MeasurementDeltaCalcSupport WHERE VERSIONID='" & TechPackTPIde & "' AND TYPEID='" & mt.MeasurementTypeID & "'"
                deltaSupport = "SELECT b.VENDORRELEASE || ';' || b.DELTACALCSUPPORT AS DELTA FROM SupportedVendorRelease a, MeasurementDeltaCalcSupport b where a.VERSIONID='" & TechPackTPIde & "' and b.VERSIONID=a.VERSIONID and b.VENDORRELEASE=a.VENDORRELEASE and b.TYPEID='" & mt.MeasurementTypeID & "'" & _
                " union all " & _
                "SELECT VENDORRELEASE || ';1'  AS DELTA FROM SupportedVendorRelease where VERSIONID='" & TechPackTPIde & "' and VENDORRELEASE NOT IN (SELECT VENDORRELEASE FROM MeasurementDeltaCalcSupport where VERSIONID='" & TechPackTPIde & "' and DELTACALCSUPPORT=0 and TYPEID='" & mt.MeasurementTypeID & "')"

                tempDeltaSupport = tp_utils.readSingleValue(deltaSupport, conn, dbCommand, dbReader)
                If tempDeltaSupport <> "" Then
                    If InStrRev(tempDeltaSupport, ";1") > 0 Then
                        mt.CreateCountTable = True
                    Else
                        mt.CreateCountTable = False
                    End If
                Else
                    mt.CreateCountTable = True
                End If
            End If

            mt.ObjectBusyHours = tp_utils.readSingleValue("SELECT OBJBHSUPPORT FROM MeasurementObjBHSupport WHERE TYPEID='" & mt.MeasurementTypeID & "'", conn, dbCommand, dbReader)
        Next count

        'test measurements
        Dim testMts As MeasurementTypesTPIde
        Dim testMt As MeasurementTypesTPIde.MeasurementType
        Dim test_count As Integer
        Dim test_count2 As Integer
        Dim amount As Integer
        Dim found As Boolean
        testMts = Me
        For count = 1 To Me.Count
            mt = Item(count)
            amount = 0
            'name check
            If mt.TypeName.Length > 27 Then
                Trace.WriteLine("Table '" & mt.TypeName & "'  at Row " & mt.Row & " exceeds maximum of 27 characters.")
            End If
            'description check
            If InStrRev(mt.Description, "'") > 0 OrElse InStrRev(mt.Description, ControlChars.Quote) > 0 Then
                Trace.WriteLine("Table '" & mt.MeasurementTypeID & "' description at Row " & mt.Row & " contains invalid characters.")
            End If
            'partition plan check
            If mt.PartitionPlan <> "extrasmall" AndAlso mt.PartitionPlan <> "small" AndAlso mt.PartitionPlan <> "medium" AndAlso mt.PartitionPlan <> "large" AndAlso mt.PartitionPlan <> "extralarge" Then
                Trace.WriteLine("Partition Plan set to default 'medium' in Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                mt.PartitionPlan = "medium"
            End If
            'rank table name check
            If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = True Then
                If mt.TypeName <> TechPackName & "_" & UCase(mt.ObjectBusyHours) & "BH" Then
                    Trace.WriteLine("Rank table at Row " & mt.Row & " for Object Busy Hours '" & mt.ObjectBusyHours & "' should be named as '" & TechPackName & "_" & UCase(mt.ObjectBusyHours) & "BH'.")
                End If
            End If
            If mt.ElementBusyHours = True AndAlso mt.RankTable = True Then
                If mt.TypeName <> TechPackName & "_ELEMBH" Then
                    Trace.WriteLine("Rank table at Row " & mt.Row & " for Element Busy Hours should be named as '" & TechPackName & "_ELEMBH'.")
                End If
            End If
            'counters check
            If mt.Counters.Count = 0 And mt.RankTable = False Then
                Trace.WriteLine("No Counters have been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
            End If
            'keys check
            If mt.CounterKeys.Count = 0 Then
                Trace.WriteLine("No Keys have been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
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
                    Trace.WriteLine("No Public Keys defined for Table '" & mt.TypeName & "' at Row " & mt.Row & ".")
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
                        Trace.WriteLine("No Public Keys defined for Table '" & mt.TypeName & "' at Row " & mt.Row & ".")
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
                        Trace.WriteLine("No Public Keys defined for Table '" & mt.TypeName & "' at Row " & mt.Row & ".")
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
                        Trace.WriteLine("No Public Keys defined for Table '" & mt.TypeName & "' at Row " & mt.Row & ".")
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
                        Trace.WriteLine("No Public Keys defined for Table '" & mt.TypeName & "' at Row " & mt.Row & ".")
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
                    Trace.WriteLine("No Public Keys defined for Table '" & mt.TypeName & "' at Row " & mt.Row & ".")
                End If
            End If

            'same keys and counters check
            For test_count = 1 To mt.CounterKeys.Count
                For test_count2 = 1 To mt.Counters.Count
                    If mt.CounterKeys.Item(test_count).CounterKeyName = mt.Counters.Item(test_count2).CounterName Then
                        Trace.WriteLine("Same Key and Counter '" & mt.CounterKeys.Item(test_count).CounterKeyName & "' has been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                Next test_count2
            Next test_count
            'same keys and public keys check
            For test_count = 1 To mt.CounterKeys.Count
                For test_count2 = 1 To mt.PublicKeys.Count
                    If mt.CounterKeys.Item(test_count).CounterKeyName = mt.PublicKeys.Item(test_count2).PublicKeyName Then
                        Trace.WriteLine("Same Key and Public Key '" & mt.CounterKeys.Item(test_count).CounterKeyName & "' has been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
                    End If
                Next test_count2
            Next test_count
            'same counters and public keys check
            For test_count = 1 To mt.Counters.Count
                For test_count2 = 1 To mt.PublicKeys.Count
                    If mt.Counters.Item(test_count).CounterName = mt.PublicKeys.Item(test_count2).PublicKeyName Then
                        Trace.WriteLine("Same Counter and Public Key '" & mt.Counters.Item(test_count).CounterName & "' has been defined for Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & ".")
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
                        Trace.WriteLine("No Ranking table has been defined for supported Object Busy Hour '" & BusyHourObjects(bh_count) & "' at Row " & mt.Row & ".")
                    End If
                    If amount > 1 Then
                        Trace.WriteLine("Ranking table at Row " & mt.Row & " for supported Object Busy Hour '" & BusyHourObjects(bh_count) & "' has been defined " & amount & " times.")
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
                    Trace.WriteLine("No Ranking table has been defined for Element Busy Hours at Row " & mt.Row & ".")
                End If
                If amount > 1 Then
                    Trace.WriteLine("Ranking table at Row " & mt.Row & " for Element Busy Hours has been defined " & amount & " times.")
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
                Trace.WriteLine("Fact Table '" & mt.MeasurementTypeID & "' at Row " & mt.Row & " has been defined " & amount & " times.")
                Return False
            End If
        Next count

        tp_utils = Nothing

        Return True

    End Function

    ''' Gets the vector support value for a measurment type.
    ''' 
    ''' @param      valueRead           The value read from the database.
    ''' @returns    vectorSupport       A boolean value, true if vector support is enabled for the measurement type.
    Public Function getVectorSupport(ByVal valueRead As String) As Boolean
        ' Return value:
        Dim vectorSupport As Boolean = False

        ' Define column index for VectorSupport:
        Dim vectorSupportColumn As Integer = 20

        ' Trim the value read:
        valueRead = Trim(valueRead)
        Try
            If (valueRead Is Nothing) Then
                vectorSupport = False
            ElseIf valueRead = "1" Then
                ' Only set to true if vector support value from database is "1":
                vectorSupport = True
            End If
        Catch ex As Exception
            Trace.WriteLine("Error getting vector support value: " & ex.ToString())
        End Try
        Return vectorSupport
    End Function

    ''
    'Gets the description for the measurement type.    
    '@param     mt          The measurement type object.
    '@param     dbReader    The OdbcDataReader object used to read from the database.
    '@returns   description The dsecription String.
    Private Function getDescription(ByVal mt As MeasurementTypesTPIde.MeasurementType, ByRef dbReader As System.Data.Odbc.OdbcDataReader) As String
        Dim description As String = ""
        Try
            description = Trim(dbReader.GetValue(4).ToString() + dbReader.GetValue(5).ToString() + dbReader.GetValue(6).ToString() + _
                               dbReader.GetValue(7).ToString())
        Catch ex As Exception
            Trace.WriteLine("Error getting measurement type description: " & ex.ToString())
        End Try
        Return description
    End Function

End Class
