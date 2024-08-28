Option Strict Off

''
'  BOContexts class is a collection of BOJoin classes
'
Public Class UnivIncombatibles
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BOJoin classes in BOContexts class
    '
    ' @param Index Specifies the index in the BOJoin class
    ' @return Count of BOJoin classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOJoin class from BOContexts class based on given index.
    '
    ' @param Index Specifies the index in the BOContexts class
    ' @return Reference to BOJoin
    Public ReadOnly Property Item(ByVal Index As Integer) As UnivIncombatible
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_items.Item(Index - 1), UnivIncombatible)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOJoin class to BOContexts class
    '
    ' @param ValueIn Specifies reference to BOJoin
    Public Sub AddItem(ByVal ValueIn As UnivIncombatible)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOJoin defines class context's joins.
    '
    Public Class UnivIncombatible
        Private m_Table As String
        Private m_UnivClass As String
        Private m_UnivObject As String
        Private m_Type As String

        ''
        ' Gets and sets value for Context parameter. 
        ' Context defines context's name.
        '
        ' @param Value Specifies value of Context parameter
        ' @return Value of Context parameter
        Public Property Table()
            Get
                Table = m_Table
            End Get

            Set(ByVal Value)
                m_Table = Value
            End Set

        End Property
        ''
        ' Gets and sets value for FirstTable parameter. 
        ' FirstTable defines join's first table.
        '
        ' @param Value Specifies value of FirstTable parameter
        ' @return Value of FirstTable parameter
        Public Property UnivClass()
            Get
                UnivClass = m_UnivClass
            End Get

            Set(ByVal Value)
                m_UnivClass = Value
            End Set

        End Property

        ''
        ' Gets and sets value for SecondTable parameter. 
        ' SecondTable defines join's second table.
        '
        ' @param Value Specifies value of SecondTable parameter
        ' @return Value of SecondTable parameter
        Public Property UnivObject()
            Get
                UnivObject = m_UnivObject
            End Get

            Set(ByVal Value)
                m_UnivObject = Value
            End Set

        End Property
        ''
        ' Gets and sets value for JoinLevel parameter. 
        ' JoinLevel defines join's level.
        '
        ' @param Value Specifies value of JoinLevel parameter
        ' @return Value of JoinLevel parameter
        Public Property Type()
            Get
                Type = m_Type
            End Get

            Set(ByVal Value)
                m_Type = Value
            End Set

        End Property

    End Class
   
End Class

