Option Strict Off

''
'  CounterKeys class is a collection of CounterKey classes
'
Public NotInheritable Class CounterKeysTPIde
    Private _counterkeys As System.Collections.ArrayList = New System.Collections.ArrayList
    Dim cnt_key As CounterKey

    ''
    '  Gets count of CounterKey classes in CounterKeys class
    '
    ' @param Index Specifies the index in the CounterKeys class
    ' @return Count of CounterKey classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _counterkeys Is Nothing) Then
                Return _counterkeys.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets CounterKey class from CounterKeys class based on given index.
    '
    ' @param Index Specifies the index in the CounterKeys class
    ' @return Reference to CounterKey
    Public ReadOnly Property Item(ByVal Index As Integer) As CounterKey
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_counterkeys.Item(Index - 1), CounterKey)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds CounterKey class to CounterKeys class
    '
    ' @param ValueIn Specifies reference to CounterKey
    Public Sub AddItem(ByVal ValueIn As CounterKey)

        If (Not _counterkeys Is Nothing) Then
            _counterkeys.Add(ValueIn)
        End If

    End Sub

    ''
    '  CounterKey class defines keys for measurement types.
    '
    Public NotInheritable Class CounterKey
        Private m_MeasurementTypeID As String
        Private m_TypeName As String
        Private m_CounterKeyName As String
        Private m_Description As String
        Private m_Datatype As String
        Private m_Datasize As String
        Private m_Datascale As String
        Private m_UnivObject As String
        Private m_DupConstraint As Integer
        Private m_Element As Integer
        Private m_MaxAmount As Integer
        Private m_IQIndex As String
        Private m_ColNumber As Integer
        Private m_Nullable As Integer
        Private m_IncludeInSQLInterface As Integer
        Private m_Row As Integer
        Private m_givenDatatype As String

        Public Property TypeName() As String
            Get
                TypeName = m_TypeName
            End Get

            Set(ByVal Value As String)
                m_TypeName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for MeasurementTypeID parameter. 
        ' MeasurementTypeID defines measurement type for key.
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
        ' Gets and sets value for CounterKeyName parameter. 
        ' CounterKeyName defines name for key.
        '
        ' @param Value Specifies value of CounterKeyName parameter
        ' @return Value of CounterKeyName parameter
        Public Property CounterKeyName() As String
            Get
                CounterKeyName = m_CounterKeyName
            End Get

            Set(ByVal Value As String)
                m_CounterKeyName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Description parameter. 
        ' Description defines description for key.
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
        ' Datatype defines data type for key.
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
        ' Datasize defines data size for key.
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
        ' Datascale defines data scale for key.
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
        ' Gets and sets value for UnivObject parameter. 
        ' UnivObject defines universe object name for key.
        '
        ' @param Value Specifies value of UnivObject parameter
        ' @return Value of UnivObject parameter
        Public Property UnivObject() As String
            Get
                UnivObject = m_UnivObject
            End Get

            Set(ByVal Value As String)
                m_UnivObject = Value
            End Set

        End Property

        ''
        ' Gets and sets value for DupConstraint parameter. 
        ' DupConstraint defines whether key is duplicate constraint.
        '
        ' @param Value Specifies value of DupConstraint parameter
        ' @return Value of DupConstraint parameter
        Public Property DupConstraint()
            Get
                DupConstraint = m_DupConstraint
            End Get

            Set(ByVal Value)
                m_DupConstraint = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Element parameter. 
        ' Element defines whether key is element key.
        '
        ' @param Value Specifies value of Element parameter
        ' @return Value of Element parameter
        Public Property Element()
            Get
                Element = m_Element
            End Get

            Set(ByVal Value)
                m_Element = Value
            End Set

        End Property

        ''
        ' Gets and sets value for MaxAmount parameter. 
        ' MaxAmount defines estimated maximum amount of different values for key.
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
        ' Gets and sets value for IndexValue parameter. 
        ' IndexValue defines unique index value for key.
        '
        ' @param Value Specifies value of IndexValue parameter
        ' @return Value of IndexValue parameter
        Public Property IQIndex() As String
            Get
                IQIndex = m_IQIndex
            End Get

            Set(ByVal Value As String)
                m_IQIndex = Replace(UCase(Value), " ", "")
            End Set

        End Property

        ''
        ' Gets and sets value for ColNumber parameter. 
        ' ColNumber defines column order number for key.
        '
        ' @param Value Specifies value of ColNumber parameter
        ' @return Value of ColNumber parameter
        Public Property ColNumber() As Integer
            Get
                ColNumber = m_ColNumber
            End Get

            Set(ByVal Value As Integer)
                m_ColNumber = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Nullable parameter. 
        ' Nullable defines whether key allow null values.
        '
        ' @param Value Specifies value of Nullable parameter
        ' @return Value of Nullable parameter
        Public Property Nullable()
            Get
                Nullable = m_Nullable
            End Get

            Set(ByVal Value)
                If LCase(Value) = "1" Then
                    m_Nullable = 1
                Else
                    m_Nullable = 1
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
                m_IncludeInSQLInterface = Value
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
    ' Gets counter keys defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Sub getCounterKeys(ByRef DefaultKeyMaxAmount As String, ByRef conn As System.Data.Odbc.OdbcConnection, _
                              ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                              ByRef TechPackTPIde As String)
        Dim tputils = New TPUtilitiesTPIde
        Dim Row As Integer
        Row = 1

        Dim tmpmeaskey As String
        tmpmeaskey = "SELECT TYPEID,DATANAME," & _
        "SUBSTR(DESCRIPTION,1,8000),SUBSTR(DESCRIPTION,8001,8000),SUBSTR(DESCRIPTION,16001,8000),SUBSTR(DESCRIPTION,24001,8000)" & _
        ",DATATYPE,DATASIZE,DATASCALE,UNIQUEKEY,NULLABLE,INDEXES,UNIVOBJECT,ISELEMENT,INCLUDESQL FROM MeasurementKey WHERE TYPEID LIKE '" & TechPackTPIde & "%'"

        dbCommand = New System.Data.Odbc.OdbcCommand(tmpmeaskey, conn)
        'Modification for HK80515
        Console.WriteLine("Getting counter keys defined in TP definition from the Database")

        Try
            If dbReader.IsClosed = False Then
                dbReader.Close()
            End If
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            Trace.WriteLine("Database Exception: " & ex.ToString)
            Exit Sub
        End Try

        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Trace.WriteLine("CounterKeysTPIde.getCounterKeys(): Warning: No data read for counter keys")
                Exit While
            Else
                Row += 1
                Try
                    cnt_key = New CounterKey
                    cnt_key.MeasurementTypeID = Trim(dbReader.GetValue(0).ToString())
                    cnt_key.CounterKeyName = Trim(dbReader.GetValue(1).ToString())
                    cnt_key.Description = Trim(dbReader.GetString(2) + dbReader.GetString(3) + dbReader.GetString(4) + dbReader.GetString(5))
                    cnt_key.Datatype = Trim(dbReader.GetValue(6).ToString())
                    cnt_key.Datasize = Trim(dbReader.GetValue(7).ToString())
                    cnt_key.Datascale = Trim(dbReader.GetValue(8).ToString())
                    cnt_key.DupConstraint = Trim(dbReader.GetValue(9).ToString())
                    cnt_key.Nullable = Trim(dbReader.GetValue(10).ToString())
                    cnt_key.IQIndex = Trim(dbReader.GetValue(11).ToString())
                    cnt_key.UnivObject = Trim(dbReader.GetValue(12).ToString())
                    cnt_key.Element = Trim(dbReader.GetValue(13).ToString())
                    cnt_key.IncludeInSQLInterface = Trim(dbReader.GetValue(14).ToString())
                    cnt_key.MaxAmount = 255 'DefaultKeyMaxAmount
                    cnt_key.Row = Row
                    AddItem(cnt_key)
                Catch ex As Exception
                    Trace.WriteLine("Error getting counter key data from database: " & ex.ToString())
                End Try
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()
        checkKeys()
    End Sub

    ''
    'Checks the counter keys.
    Public Sub checkKeys()
        'test keys
        Dim testKeys As CounterKeysTPIde
        Dim testKey As CounterKeysTPIde.CounterKey
        Dim test_count As Integer
        Dim amount As Integer
        testKeys = Me
        Dim count As Integer
        Dim indexes() As String

        Dim SupportedIndexes As String
        SupportedIndexes = "LF,HG,HNG,DTTM,DATE,TIME"

        Dim SupportedUniqueIndexes As String
        SupportedUniqueIndexes = "LF,HG"

        For count = 1 To Me.Count
            Try
                cnt_key = Item(count)
                amount = 0
                'description check
                'If InStrRev(cnt_key.Description, "'") > 0 OrElse InStrRev(cnt_key.Description, ControlChars.Quote) > 0 Then
                'Trace.Writeline("Description in Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' contains invalid characters.")
                'End If
                'data type check
                If cnt_key.Datatype = "NOT FOUND" OrElse cnt_key.Datasize = "Err" Then
                    Trace.WriteLine("Data Type in Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' is not defined correctly.")
                End If
                'IQ index check
                If cnt_key.IQIndex <> "" Then
                    indexes = Split(cnt_key.IQIndex, ",")
                    For test_count = 0 To UBound(indexes)
                        If InStrRev(SupportedIndexes, indexes(test_count)) = 0 Then
                            Trace.WriteLine("IQ Index for Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' is not one of the supported: ''," & SupportedIndexes)
                        End If
                    Next
                End If
                'force IQ index to LF or HG for counter that are duplicate constraints
                If cnt_key.DupConstraint = 1 Then
                    If cnt_key.IQIndex <> "" Then
                        indexes = Split(cnt_key.IQIndex, ",")
                        For test_count = 0 To UBound(indexes)
                            If InStrRev(SupportedUniqueIndexes, indexes(test_count)) = 0 Then
                                Trace.WriteLine("IQ Index for Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' is not one of the supported: " & SupportedUniqueIndexes)
                            End If
                        Next
                    Else
                        Trace.WriteLine("IQ Index for Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' must be one of the following: " & SupportedUniqueIndexes)
                    End If
                End If
                'universe object check
                If cnt_key.UnivObject.Length() > 128 Then
                    Trace.WriteLine("Universe Object '" & cnt_key.UnivObject & "' for Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' exceeds maximum of 128 characters")
                End If
                'duplicate check
                For test_count = 1 To testKeys.Count
                    testKey = testKeys.Item(test_count)
                    If cnt_key.MeasurementTypeID = testKey.MeasurementTypeID AndAlso cnt_key.CounterKeyName = testKey.CounterKeyName Then
                        amount += 1
                    End If
                Next test_count
                If amount > 1 Then
                    Trace.WriteLine("Key '" & cnt_key.CounterKeyName & "' at Row " & cnt_key.Row & "  in Fact Table '" & cnt_key.MeasurementTypeID & "' has been defined " & amount & " times.")
                End If
            Catch ex As Exception
                Trace.WriteLine("Error checking counter key: " & ex.ToString())
            End Try
        Next count
    End Sub

End Class