Option Strict Off

''
'  BHObjects class is a collection of BHType classes
'
Public NotInheritable Class BHTypes
    Private _bhtypes As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BHType classes in BHTypes class
    '
    ' @param Index Specifies the index in the BHTypes class
    ' @return Count of BHType classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _bhtypes Is Nothing) Then
                Return _bhtypes.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BHType class from BHTypes class based on given index.
    '
    ' @param Index Specifies the index in the BHTypes class
    ' @return Reference to BHType
    Public ReadOnly Property Item(ByVal Index As Integer) As BHType
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_bhtypes.Item(Index - 1), BHType)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BHType class to BHTypes class
    '
    ' @param ValueIn Specifies reference to BHType
    Public Sub AddItem(ByVal ValueIn As BHType)

        If (Not _bhtypes Is Nothing) Then
            _bhtypes.Add(ValueIn)
        End If

    End Sub

    ''
    '  BHType class defines supported BH types for the technology package
    '
    Public NotInheritable Class BHType
        Private m_BHType As String
        Private m_Description As String
        Private m_BHSource As String
        Private m_BHCriteria As String
        Private m_BHWhere As String
        Private m_BHObjects As String
        Private m_BHElements As String

        ''
        '  Gets and sets value for BHType parameter. BHType defines the name of the busy hour.
        '
        ' @param Value Specifies value of BHType parameter
        ' @return Value of BHType parameter
        Public Property BHType() As String
            Get
                BHType = m_BHType
            End Get

            Set(ByVal Value As String)
                m_BHType = Value
            End Set

        End Property

        ''
        '  Gets and sets value for Description parameter. Description defines the description of the busy hour.
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

        ''
        '  Gets and sets value for BHSource parameter. BHSource defines the source tables of the busy hour.
        '
        ' @param Value Specifies value of BHSource parameter
        ' @return Value of BHSource parameter
        Public Property BHSource() As String
            Get
                BHSource = m_BHSource
            End Get

            Set(ByVal Value As String)
                m_BHSource = Value
            End Set

        End Property

        ''
        '  Gets and sets value for BHCriteria parameter. BHCriteria defines the criteria formula of the busy hour.
        '
        ' @param Value Specifies value of BHCriteria parameter
        ' @return Value of BHCriteria parameter
        Public Property BHCriteria() As String
            Get
                BHCriteria = m_BHCriteria
            End Get

            Set(ByVal Value As String)
                m_BHCriteria = Value
            End Set

        End Property

        ''
        '  Gets and sets value for BHWhere parameter. BHWhere defines the where clause of the busy hour.
        '
        ' @param Value Specifies value of BHWhere parameter
        ' @return Value of BHWhere parameter
        Public Property BHWhere() As String
            Get
                BHWhere = m_BHWhere
            End Get

            Set(ByVal Value As String)
                m_BHWhere = Value
            End Set

        End Property

        ''
        '  Gets and sets value for BHObjects parameter. BHObjects defines the supported objects for the busy hour.
        '
        ' @param Value Specifies value of BHObjects parameter
        ' @return Value of BHObjects parameter
        Public Property BHObjects() As String
            Get
                BHObjects = m_BHObjects
            End Get

            Set(ByVal Value As String)
                m_BHObjects = Value
            End Set

        End Property

        ''
        '  Gets and sets value for BHElements parameter. BHElements defines the supported elements for the busy hour.
        '
        ' @param Value Specifies value of BHElements parameter
        ' @return Value of BHElements parameter
        Public Property BHElements() As String
            Get
                BHElements = m_BHElements
            End Get

            Set(ByVal Value As String)
                m_BHElements = Value
            End Set

        End Property

    End Class
End Class
