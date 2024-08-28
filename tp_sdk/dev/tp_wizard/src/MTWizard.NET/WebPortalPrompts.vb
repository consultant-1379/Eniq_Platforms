Option Strict Off

''
'  WebPortalPrompts class is a collection of WebPortalPrompt classes
'
Public NotInheritable Class WebPortalPrompts
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As WebPortalPrompt
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_items.Item(Index - 1), WebPortalPrompt)
            End If
            Return Nothing
        End Get
    End Property

    Public Sub AddItem(ByVal ValueIn As WebPortalPrompt)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  WebPortalImplementorOption class defines web portal implementor option.
    '
    Public NotInheritable Class WebPortalPrompt
        Private m_ClassName As String
        Private m_PromptText As String
        Private m_Refreshable As String
        Private m_Order As Integer
        Private m_Row As Integer

        Public Property ClassName() As String
            Get
                ClassName = m_ClassName
            End Get

            Set(ByVal Value As String)
                m_ClassName = Value
            End Set

        End Property

        Public Property PromptText() As String
            Get
                PromptText = m_PromptText                
            End Get

            Set(ByVal Value As String)
                If Value.EndsWith(":") Then
                    m_PromptText = Value
                Else
                    m_PromptText = Value & ":"
                End If
            End Set

        End Property


        Public Property Refreshable() As String
            Get
                Refreshable = m_Refreshable
            End Get

            Set(ByVal Value As String)
                m_Refreshable = UCase(Value)
            End Set

        End Property

        Public Property Order()
            Get
                Order = m_Order
            End Get
            Set(ByVal Value)
                If UCase(Value) = "ELEMENT TOPOLOGY" Then
                    m_Order = 10
                ElseIf UCase(Value) = "TOPOLOGY" Then
                    m_Order = 20
                ElseIf UCase(Value) = "TIME" Then
                    m_Order = 30
                ElseIf UCase(Value) = "BUSY HOUR" Then
                    m_Order = 40
                ElseIf UCase(Value) = "OTHER" Then
                    m_Order = 50
                Else
                    m_Order = 999
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

    Public Function getPrompts(ByRef ClassName As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages)

        Dim indexes() As String
        Dim count As Integer
        Dim implementorPrompt As WebPortalPrompts.WebPortalPrompt
        Dim Row As Integer

        Dim SupportedOrders As String
        SupportedOrders = "ELEMENT TOPOLOGY,TOPOLOGY,TIME,BUSY HOUR,OTHER"

        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Webportal prompts$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                If Trim(dbReader.GetValue(1).ToString()) = ClassName Then
                    Row += 1
                    implementorPrompt = New WebPortalPrompts.WebPortalPrompt
                    implementorPrompt.ClassName = Trim(dbReader.GetValue(1).ToString())
                    implementorPrompt.PromptText = Trim(dbReader.GetValue(0).ToString())
                    implementorPrompt.Order = Trim(dbReader.GetValue(2).ToString())
                    implementorPrompt.Refreshable = Trim(dbReader.GetValue(3).ToString())
                    implementorPrompt.Row = Row
                    AddItem(implementorPrompt)
                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        'test implementor prompts
        Dim supportedRefreshable As String
        Dim testPrompts As WebPortalPrompts
        Dim testPrompt As WebPortalPrompts.WebPortalPrompt
        Dim test_count As Integer
        Dim amount As Integer

        supportedRefreshable = "YES,NO"
        testPrompts = Me
        For count = 1 To Me.Count
            implementorPrompt = Item(count)
            amount = 0
            'order check
            If implementorPrompt.Order = 999 Then
                logs.AddLogText("Order in Prompt '" & implementorPrompt.PromptText & "' at Row " & implementorPrompt.Row & "  is not one of the supported: " & SupportedOrders & ".")
            End If
            'refreshable check
            If InStrRev(supportedRefreshable, implementorPrompt.Refreshable) = 0 AndAlso implementorPrompt.Refreshable <> "" Then
                logs.AddLogText("Refreshable setting for Prompt '" & implementorPrompt.PromptText & "' at Row " & implementorPrompt.Row & " is not one of the supported: ''," & supportedRefreshable)
            End If
            'duplicate check
            For test_count = 1 To testPrompts.Count
                testPrompt = testPrompts.Item(test_count)
                If implementorPrompt.ClassName = testPrompt.ClassName AndAlso implementorPrompt.PromptText = testPrompt.PromptText Then
                    amount += 1
                End If
            Next test_count
            If amount > 1 Then
                logs.AddLogText("Prompts '" & implementorPrompt.PromptText & "' at Row " & implementorPrompt.Row & "  in Implementor Class '" & implementorPrompt.ClassName & "' has been defined " & amount & " times.")
            End If
        Next count

        Return Me

    End Function
End Class