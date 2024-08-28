Option Strict Off
Public Class TPReports
    Private _tpreports As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _tpreports Is Nothing) Then
                Return _tpreports.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As TPReport
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_tpreports.Item(Index - 1), TPReport)
            End If
            Return Nothing
        End Get
    End Property


    Public Sub AddItem(ByVal ValueIn As TPReport)

        If (Not _tpreports Is Nothing) Then
            _tpreports.Add(ValueIn)
        End If

    End Sub
    Public Class TPReport
        Private m_Category As String
        Private m_ReportName As String
        Private m_Description As String

        Public Property Category()
            Get
                Category = m_Category
            End Get

            Set(ByVal Value)
                m_Category = Value
            End Set

        End Property

        Public Property ReportName()
            Get
                ReportName = m_ReportName
            End Get

            Set(ByVal Value)
                m_ReportName = Value
            End Set

        End Property

        Public Property Description()
            Get
                Description = m_Description
            End Get

            Set(ByVal Value)
                m_Description = Value
            End Set

        End Property

    End Class
End Class
