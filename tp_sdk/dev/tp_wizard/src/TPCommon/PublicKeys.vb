Option Strict Off

''
'  PublicKeys class is a collection of PublicKey classes
'
Public NotInheritable Class PublicKeys
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
    Public Sub getPublicKeys(ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages)

        Dim indexes() As String
        Dim count As Integer
        Dim pub_key As PublicKeys.PublicKey
        Dim Row As Integer

        Row = 1

        Dim SupportedIndexes As String
        SupportedIndexes = "LF,HG,HNG,DTTM,DATE,TIME"

        Dim tputils = New TPUtilities

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Public Keys$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Row += 1
                pub_key = New PublicKeys.PublicKey
                pub_key.KeyType = Trim(dbReader.GetValue(0).ToString())
                pub_key.PublicKeyName = Trim(dbReader.GetValue(1).ToString())
                pub_key.Description = Trim(dbReader.GetValue(2).ToString())
                pub_key.givenDatatype = Trim(dbReader.GetValue(3).ToString())
                Call tputils.getDatatype(Trim(dbReader.GetValue(3).ToString()))
                pub_key.Datatype = tputils.Datatype
                pub_key.Datasize = tputils.Datasize
                pub_key.Datascale = tputils.Datascale
                pub_key.DupConstraint = Trim(dbReader.GetValue(4).ToString())
                pub_key.IQIndex = Trim(dbReader.GetValue(5).ToString())
                pub_key.Nullable = Trim(dbReader.GetValue(6).ToString())
                pub_key.MaxAmount = Trim(dbReader.GetValue(7).ToString())
                pub_key.IncludeInSQLInterface = Trim(dbReader.GetValue(8).ToString())
                pub_key.Row = Row
                AddItem(pub_key)
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test public keys
        Dim testKeys As PublicKeys
        Dim testKey As PublicKeys.PublicKey
        Dim test_count As Integer
        Dim amount As Integer
        testKeys = Me
        For count = 1 To Me.Count
            pub_key = Item(count)
            amount = 0
            'description check
            If InStrRev(pub_key.Description, "'") > 0 OrElse InStrRev(pub_key.Description, ControlChars.Quote) > 0 Then
                logs.AddLogText("Description in Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' contains invalid characters.")
            End If
            'data type check
            If pub_key.Datatype = "NOT FOUND" OrElse pub_key.Datasize = "Err" Then
                logs.AddLogText("Data Type in Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' is not defined correctly.")
            End If
            'IQ index check
            If pub_key.IQIndex <> "" Then
                indexes = Split(pub_key.IQIndex, ",")
                For test_count = 0 To UBound(indexes)
                    If InStrRev(SupportedIndexes, indexes(test_count)) = 0 Then
                        logs.AddLogText("IQ Index for Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' is not one of the supported: " & SupportedIndexes)
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
                logs.AddLogText("Public Key '" & pub_key.PublicKeyName & "' at Row " & pub_key.Row & "  in Level '" & pub_key.KeyType & "' has been defined " & amount & " times.")
            End If
        Next count

    End Sub
End Class