Option Strict Off

''
'  PublicKeys class is a collection of PublicKey classes
'
Public NotInheritable Class PublicKeysTPIde
    Private _publickeys As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of PublicKey classes in PublicKeys class
    '
    ' @param Index Specifies the index in the PublicKeys class
    ' @return Count of PublicKey classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _publickeys Is Nothing) Then
                Return _publickeys.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets PublicKey class from PublicKeys class based on given index.
    '
    ' @param Index Specifies the index in the PublicKeys class
    ' @return Reference to PublicKey
    Public ReadOnly Property Item(ByVal Index As Integer) As PublicKey
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_publickeys.Item(Index - 1), PublicKey)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds PublicKey class to PublicKeys class
    '
    ' @param ValueIn Specifies reference to PublicKey
    Public Sub AddItem(ByVal ValueIn As PublicKey)

        If (Not _publickeys Is Nothing) Then
            _publickeys.Add(ValueIn)
        End If

    End Sub

    ''
    '  PublicKey class defines public keys for measurement types.
    '
    Public NotInheritable Class PublicKey
        Private m_KeyType As String
        Private m_PublicKeyName As String
        Private m_Description As String
        Private m_Datatype As String
        Private m_Datasize As String
        Private m_Datascale As String
        Private m_Nullable As String
        Private m_DupConstraint As Integer
        Private m_MaxAmount As Integer
        Private m_IQIndex As String
        Private m_ColNumber As Integer
        Private m_Row As Integer
        Private m_IncludeInSQLInterface As Integer
        Private m_givenDatatype As String

        ''
        ' Gets and sets value for KeyType parameter. 
        ' KeyType defines public key type.
        '
        ' @param Value Specifies value of KeyType parameter
        ' @return Value of KeyType parameter
        Public Property KeyType() As String
            Get
                KeyType = m_KeyType
            End Get

            Set(ByVal Value As String)
                m_KeyType = Value
            End Set

        End Property

        ''
        ' Gets and sets value for PublicKeyName parameter. 
        ' PublicKeyName defines name.
        '
        ' @param Value Specifies value of PublicKeyName parameter
        ' @return Value of PublicKeyName parameter
        Public Property PublicKeyName() As String
            Get
                PublicKeyName = m_PublicKeyName
            End Get

            Set(ByVal Value As String)
                m_PublicKeyName = Value
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
        ' Gets and sets value for Nullable parameter. 
        ' Nullable defines whether null values are allowed.
        '
        ' @param Value Specifies value of Nullable parameter
        ' @return Value of Nullable parameter
        Public Property Nullable()
            Get
                Nullable = m_Nullable
            End Get

            Set(ByVal Value)
                If Value = "" Then
                    m_Nullable = 1
                Else
                    m_Nullable = 1
                End If
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
                If Value = "" Then
                    m_DupConstraint = 0
                Else
                    m_DupConstraint = 1
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
                If Value <> "" Then
                    m_MaxAmount = Value
                Else
                    m_MaxAmount = 255
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for IndexValue parameter. 
        ' IndexValue defines unique index value for public key.
        '
        ' @param Value Specifies value of IndexValue parameter
        ' @return Value of IndexValue parameter
        Public Property IQIndex() As String
            Get
                IQIndex = m_IQIndex
            End Get

            Set(ByVal Value As String)
                m_IQIndex = UCase(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for ColNumber parameter. 
        ' ColNumber defines column order number for public key.
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
    ' Gets public keys defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    Public Sub getPublicKeys(ByRef conn As System.Data.Odbc.OdbcConnection, ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                             ByRef BaseTechPackTPIde As String)

        If (BaseTechPackTPIde = "") OrElse (BaseTechPackTPIde Is Nothing) Then
            Dim message As String
            message = "PublicKeysTPIde.vb, getPublicKeys(): base tech pack name is not defined. Failed to get public keys."
            Trace.WriteLine(message)
            Console.WriteLine(message)
            Return
        End If

        Dim indexes() As String
        Dim count As Integer
        Dim pub_key As PublicKeysTPIde.PublicKey
        Dim Row As Integer

        Row = 1

        Dim SupportedIndexes As String
        SupportedIndexes = "LF,HG,HNG,DTTM,DATE,TIME"

        Dim tputils = New TPUtilitiesTPIde

        Dim tmpmeascol As String
        tmpmeascol = "SELECT t.MTABLEID,t.TABLELEVEL, c.DATANAME, SUBSTR(c.DESCRIPTION,1,8000),SUBSTR(c.DESCRIPTION,8001,8000),SUBSTR(c.DESCRIPTION,16001,8000),SUBSTR(c.DESCRIPTION,24001,8000), c.DATATYPE, C.DATASIZE, c.DATASCALE," & _
        " c.UNIQUEKEY, c.INDEXES, c.NULLABLE, c.UNIQUEVALUE, c.INCLUDESQL" & _
        " FROM MeasurementColumn c, MeasurementTable t WHERE c.MTABLEID=t.MTABLEID and t.MTABLEID LIKE '" & BaseTechPackTPIde & "%'"
        dbCommand = New System.Data.Odbc.OdbcCommand(tmpmeascol, conn)

        'Modification for HK80515
        Console.WriteLine("Getting PublicKeys from the Database")

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
                Exit While
            Else
                Row += 1
                pub_key = New PublicKeysTPIde.PublicKey
                pub_key.KeyType = Trim(dbReader.GetValue(1).ToString())
                pub_key.PublicKeyName = Trim(dbReader.GetValue(2).ToString())
                pub_key.Description = Trim(dbReader.GetString(3) + dbReader.GetString(4) + dbReader.GetString(5) + dbReader.GetString(6))
                pub_key.Datatype = Trim(dbReader.GetValue(7).ToString())
                pub_key.Datasize = Trim(dbReader.GetValue(8).ToString())
                pub_key.Datascale = Trim(dbReader.GetValue(9).ToString())
                pub_key.DupConstraint = Trim(dbReader.GetValue(10).ToString())
                pub_key.IQIndex = Trim(dbReader.GetValue(11).ToString())
                pub_key.Nullable = Trim(dbReader.GetValue(12).ToString())
                pub_key.MaxAmount = Trim(dbReader.GetValue(13).ToString())
                pub_key.IncludeInSQLInterface = Trim(dbReader.GetValue(14).ToString())
                pub_key.Row = Row
                AddItem(pub_key)

            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test public keys
        Dim testKeys As PublicKeysTPIde
        Dim testKey As PublicKeysTPIde.PublicKey
        Dim test_count As Integer
        Dim amount As Integer
        testKeys = Me
        For count = 1 To Me.Count
            pub_key = Item(count)
            amount = 0
            'description check
            If InStrRev(pub_key.Description, "'") > 0 OrElse InStrRev(pub_key.Description, ControlChars.Quote) > 0 Then
                Trace.WriteLine("Description in Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' contains invalid characters.")
            End If
            'data type check
            If pub_key.Datatype = "NOT FOUND" OrElse pub_key.Datasize = "Err" Then
                Trace.WriteLine("Data Type in Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' is not defined correctly.")
            End If
            'IQ index check
            If pub_key.IQIndex <> "" Then
                indexes = Split(pub_key.IQIndex, ",")
                For test_count = 0 To UBound(indexes)
                    If InStrRev(SupportedIndexes, indexes(test_count)) = 0 Then
                        Trace.WriteLine("IQ Index for Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' is not one of the supported: " & SupportedIndexes)
                    End If
                Next
            End If
            'duplicate check
            For test_count = 1 To testKeys.Count
                testKey = testKeys.Item(test_count)
                If pub_key.KeyType = testKey.KeyType AndAlso pub_key.PublicKeyName = testKey.PublicKeyName Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                'Disabled for ENIQ2.0
                'Trace.Writeline("Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' has been defined " & amount & " times.")
            End If
        Next count

    End Sub
End Class