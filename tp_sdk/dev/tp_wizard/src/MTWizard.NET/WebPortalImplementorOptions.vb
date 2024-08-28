Option Strict Off

''
'  WebPortalImplementorOptions class is a collection of WebPortalImplementorOption classes
'
Public NotInheritable Class WebPortalImplementorOptions
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As WebPortalImplementorOption
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_items.Item(Index - 1), WebPortalImplementorOption)
            End If
            Return Nothing
        End Get
    End Property

    Public Sub AddItem(ByVal ValueIn As WebPortalImplementorOption)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  WebPortalImplementorOption class defines web portal implementor option.
    '
    Public NotInheritable Class WebPortalImplementorOption
        Private m_ClassName As String
        Private m_OptionName As String
        Private m_OptionValue As String
        Private m_Row As Integer

        Public Property ClassName() As String
            Get
                ClassName = m_ClassName
            End Get

            Set(ByVal Value As String)
                m_ClassName = Value
            End Set

        End Property

        Public Property OptionName() As String
            Get
                OptionName = m_OptionName
            End Get

            Set(ByVal Value As String)
                m_OptionName = Value
            End Set

        End Property


        Public Property OptionValue() As String
            Get
                OptionValue = m_OptionValue
            End Get

            Set(ByVal Value As String)
                m_OptionValue = Value
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

    Public Function getOptions(ByRef ClassName As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages)

        Dim indexes() As String
        Dim count As Integer
        Dim implementorOption As WebPortalImplementorOptions.WebPortalImplementorOption
        Dim Row As Integer

        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Webportal implementor options$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                If Trim(dbReader.GetValue(0).ToString()) = ClassName Then
                    Row += 1
                    implementorOption = New WebPortalImplementorOptions.WebPortalImplementorOption
                    implementorOption.ClassName = Trim(dbReader.GetValue(0).ToString())
                    implementorOption.OptionName = Trim(dbReader.GetValue(1).ToString())
                    implementorOption.OptionValue = Trim(dbReader.GetValue(2).ToString())
                    implementorOption.Row = Row
                    AddItem(implementorOption)
                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test implementor options
        Dim testOptions As WebPortalImplementorOptions
        Dim testOption As WebPortalImplementorOptions.WebPortalImplementorOption
        Dim test_count As Integer
        Dim amount As Integer
        testOptions = Me
        For count = 1 To Me.Count
            implementorOption = Item(count)
            amount = 0
            'duplicate check
            For test_count = 1 To testOptions.Count
                testOption = testOptions.Item(test_count)
                If implementorOption.ClassName = testOption.ClassName AndAlso implementorOption.OptionName = testOption.OptionName Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Options '" & implementorOption.OptionName & "' at Row " & implementorOption.Row & "  in Implementor Class '" & implementorOption.ClassName & "' has been defined " & amount & " times.")
            End If
        Next count

        Return Me

    End Function
End Class