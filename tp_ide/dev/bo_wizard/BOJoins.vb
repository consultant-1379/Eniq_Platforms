Option Strict Off

''
'  BOJoins class is a collection of BOJoin classes
'
Public Class BOJoins
    Private _joins As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BOJoin classes in BOJoins class
    '
    ' @param Index Specifies the index in the BOJoins class
    ' @return Count of BOJoin classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _joins Is Nothing) Then
                Return _joins.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOJoin class from BOJoins class based on given index.
    '
    ' @param Index Specifies the index in the BOJoins class
    ' @return Reference to BOJoin
    Public ReadOnly Property Item(ByVal Index As Integer) As BOJoin
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_joins.Item(Index - 1), BOJoin)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOJoin class to BOJoins class
    '
    ' @param ValueIn Specifies reference to BOJoin
    Public Sub AddItem(ByVal ValueIn As BOJoin)

        If (Not _joins Is Nothing) Then
            _joins.Add(ValueIn)
        End If

    End Sub
    ''
    '  BOJoin class defines join information for universe.
    '
    Public Class BOJoin
        Private m_JoinTable As String
        Private m_JoinLevel As String

        ''
        ' Gets and sets value for JoinTable parameter. 
        ' JoinTable defines join table.
        '
        ' @param Value Specifies value of JoinTable parameter
        ' @return Value of JoinTable parameter
        Public Property JoinTable()
            Get
                JoinTable = m_JoinTable
            End Get

            Set(ByVal Value)
                m_JoinTable = Value
            End Set

        End Property

        ''
        ' Gets and sets value for JoinLevel parameter. 
        ' JoinLevel defines join level.
        '
        ' @param Value Specifies value of JoinLevel parameter
        ' @return Value of JoinLevel parameter
        Public Property JoinLevel()
            Get
                JoinLevel = m_JoinLevel
            End Get

            Set(ByVal Value)
                m_JoinLevel = Value
            End Set

        End Property

    End Class
End Class
