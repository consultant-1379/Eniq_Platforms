Option Strict Off

Public NotInheritable Class logMessages
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As Message
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_items.Item(Index - 1), Message)
            End If
            Return Nothing
        End Get
    End Property

    Public Sub AddItem(ByVal ValueIn As Message)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    Public Sub AddLogText(ByVal ValueIn As String)

        If (Not _items Is Nothing) Then
            Dim message As New Message
            message.Text = ValueIn
            _items.Add(message)
        End If

    End Sub

    Public NotInheritable Class Message
        Private m_Text As String

        Public Property Text() As String
            Get
                Text = m_Text
            End Get

            Set(ByVal Value As String)
                m_Text = Value
            End Set

        End Property

    End Class

    Public Sub mergeLogs(ByRef srcLogs As logMessages)
        Dim Count As Integer
        For Count = 1 To srcLogs.Count
            Me.AddLogText(srcLogs.Item(Count).Text)
        Next Count
    End Sub
End Class


