Option Strict Off

''
'  WebPortalImplementors class is a collection of WebPortalImplementor classes
'
Public NotInheritable Class WebPortalImplementors
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As WebPortalImplementor
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_items.Item(Index - 1), WebPortalImplementor)
            End If
            Return Nothing
        End Get
    End Property

    Public Sub AddItem(ByVal ValueIn As WebPortalImplementor)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  WebPortalImplementor class defines web portal implementor.
    '
    Public NotInheritable Class WebPortalImplementor
        Private m_ClassName As String
        Private m_Priority As Integer
        Private m_ImplementorOptions As WebPortalImplementorOptions
        Private m_ImplementorPrompts As WebPortalPrompts
        Private m_Row As Integer

        ''
        ' Gets and sets value for ClassName parameter. 
        ' KeyType defines implementor java class name.
        '
        ' @param Value Specifies value of ClassName parameter
        ' @return Value of ClassName parameter
        Public Property ClassName() As String
            Get
                ClassName = m_ClassName
            End Get

            Set(ByVal Value As String)
                m_ClassName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Priority parameter. 
        ' PublicKeyName defines implementor priority.
        '
        ' @param Value Specifies value of Priority parameter
        ' @return Value of Priority parameter
        Public Property Priority()
            Get
                Priority = m_Priority
            End Get

            Set(ByVal Value)
                If UCase(Value) = "DEFAULT" Then
                    m_Priority = 30
                ElseIf UCase(Value) = "MEDIUM" Then
                    m_Priority = 20
                ElseIf UCase(Value) = "HIGH" Then
                    m_Priority = 10
                Else
                    m_Priority = 999
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for ImplementorOptions parameter. 
        ' Datatype defines webportal implementor options.
        '
        ' @param Value Specifies value of ImplementorOptions parameter
        ' @return Value of ImplementorOptions parameter
        Public Property ImplementorOptions() As WebPortalImplementorOptions
            Get
                ImplementorOptions = m_ImplementorOptions
            End Get

            Set(ByVal Value As WebPortalImplementorOptions)
                m_ImplementorOptions = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ImplementorPrompts parameter. 
        ' Datasize defines prompts for webportal implementor.
        '
        ' @param Value Specifies value of ImplementorPrompts parameter
        ' @return Value of ImplementorPrompts parameter
        Public Property ImplementorPrompts() As WebPortalPrompts
            Get
                ImplementorPrompts = m_ImplementorPrompts
            End Get

            Set(ByVal Value As WebPortalPrompts)
                m_ImplementorPrompts = Value
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


    Public Sub getImplementors(ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages)

        Dim indexes() As String
        Dim count As Integer
        Dim implementor As WebPortalImplementors.WebPortalImplementor
        Dim implementorOptions As WebPortalImplementorOptions
        Dim implementorPrompts As WebPortalPrompts
        Dim Row As Integer

        Dim SupportedPriorities As String
        SupportedPriorities = "DEFAULT,MEDIUM,HIGH"

        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Webportal implementors$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Row += 1
                implementor = New WebPortalImplementors.WebPortalImplementor
                implementor.ClassName = Trim(dbReader.GetValue(0).ToString())
                implementor.Priority = Trim(dbReader.GetValue(1).ToString())
                implementor.Row = Row
                AddItem(implementor)
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'read through options
        For count = 1 To Me.Count
            implementor = Item(count)
            implementorOptions = New WebPortalImplementorOptions
            implementorPrompts = New WebPortalPrompts
            implementor.ImplementorOptions = implementorOptions.getOptions(implementor.ClassName, conn, dbCommand, dbReader, logs)
            implementor.ImplementorPrompts = implementorPrompts.getPrompts(implementor.ClassName, conn, dbCommand, dbReader, logs)
        Next count

        'test public keys
        Dim testImplementors As WebPortalImplementors
        Dim testImplementor As WebPortalImplementors.WebPortalImplementor
        Dim test_count As Integer
        Dim amount As Integer
        testImplementors = Me
        For count = 1 To Me.Count
            implementor = Item(count)
            amount = 0
            'priority check
            If implementor.Priority = 999 Then
                logs.AddLogText("Priority in Implementor Class '" & implementor.ClassName & "' at Row " & implementor.Row & "  is not one of the supported: " & SupportedPriorities & ".")
            End If
            'duplicate check
            For test_count = 1 To testImplementors.Count
                testImplementor = testImplementors.Item(test_count)
                If implementor.ClassName = testImplementor.ClassName Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Implementor Class '" & implementor.ClassName & "' at Row " & implementor.Row & " has been defined " & amount & " times.")
            End If
        Next count

    End Sub
End Class