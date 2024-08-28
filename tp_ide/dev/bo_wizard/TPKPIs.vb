Option Strict Off
Public Class TPKPIs
    Private _tpkpis As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _tpkpis Is Nothing) Then
                Return _tpkpis.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As TPKPI
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_tpkpis.Item(Index - 1), TPKPI)
            End If
            Return Nothing
        End Get
    End Property


    Public Sub AddItem(ByVal ValueIn As TPKPI)

        If (Not _tpkpis Is Nothing) Then
            _tpkpis.Add(ValueIn)
        End If

    End Sub
    Public Class TPKPI
        Private m_Name As String
        Private m_Description As String

        Public Property Name()
            Get
                Name = m_Name
            End Get

            Set(ByVal Value)
                m_Name = Value
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

