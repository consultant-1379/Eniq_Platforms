Option Strict Off

''
'  BHObjects class is a collection of BHObject classes
'
Public NotInheritable Class BHObjects
    Private _bhobjects As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BHObject classes in BHObjects class
    '
    ' @param Index Specifies the index in the BHObejcts class
    ' @return Count of BHObject classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _bhobjects Is Nothing) Then
                Return _bhobjects.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BHObject class from BHObjects class based on given index.
    '
    ' @param Index Specifies the index in the BHObjects class
    ' @return Reference to BHObject
    Public ReadOnly Property Item(ByVal Index As Integer) As BHObject
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_bhobjects.Item(Index - 1), BHObject)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BHObject class to BHObjects class
    '
    ' @param ValueIn Specifies reference to BHObject
    Public Sub AddItem(ByVal ValueIn As BHObject)

        If (Not _bhobjects Is Nothing) Then
            _bhobjects.Add(ValueIn)
        End If

    End Sub

    ''
    '  BHObject class defines supported BH objects for the technology package
    '
    Public NotInheritable Class BHObject
        Private m_BHObject As String
        Private m_Keys As String
        Private m_KeyValues As String
        Private m_Type As String

        ''
        '  Copies values from a specified BHObject.
        '
        ' @param Value Specifies reference to BHObject
        Public Sub copy(ByVal Value As BHObject)

            m_BHObject = Value.BHObject
            m_Keys = Value.Keys
            m_KeyValues = Value.KeyValues
            m_Type = Value.Type

        End Sub

        ''
        '  Gets and sets value for BHObject parameter
        '
        ' @param Value Specifies value of BHObject parameter
        Public Property BHObject() As String
            Get
                BHObject = m_BHObject
            End Get

            Set(ByVal Value As String)
                m_BHObject = Value
            End Set

        End Property

        ''
        '  Gets and sets value for Keys parameter
        '
        ' @param Value Specifies value of Keys parameter
        Public Property Keys() As String
            Get
                Keys = m_Keys
            End Get

            Set(ByVal Value As String)
                m_Keys = Value
            End Set

        End Property

        ''
        '  Gets and sets value for KeyValues parameter
        '
        ' @param Value Specifies value of KeyValues parameter
        Public Property KeyValues() As String
            Get
                KeyValues = m_KeyValues
            End Get

            Set(ByVal Value As String)
                m_KeyValues = Value
            End Set

        End Property

        ''
        '  Gets and sets value for Type parameter
        '
        ' @param Value Specifies value of Type parameter
        Public Property Type() As String
            Get
                Type = m_Type
            End Get

            Set(ByVal Value As String)
                m_Type = LCase(Value)
            End Set

        End Property

    End Class
End Class


