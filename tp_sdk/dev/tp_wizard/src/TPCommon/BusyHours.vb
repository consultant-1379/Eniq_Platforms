Option Strict Off

''
'  BusyHours class is a collection of BusyHour classes
'
Public NotInheritable Class BusyHours
    Private _busyhours As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BusyHour classes in BusyHours class
    '
    ' @param Index Specifies the index in the BusyHours class
    ' @return Count of BusyHour classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _busyhours Is Nothing) Then
                Return _busyhours.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BusyHour class from BusyHours class based on given index.
    '
    ' @param Index Specifies the index in the BusyHours class
    ' @return Reference to BusyHour
    Public ReadOnly Property Item(ByVal Index As Integer) As BusyHour
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_busyhours.Item(Index - 1), BusyHour)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BusyHour class to BusyHours class
    '
    ' @param ValueIn Specifies reference to BusyHour
    Public Sub AddItem(ByVal ValueIn As BusyHour)

        If (Not _busyhours Is Nothing) Then
            _busyhours.Add(ValueIn)
        End If

    End Sub

    ''
    '  BHType class defines supported BH information for the technology package.
    '
    Public NotInheritable Class BusyHour
        Private m_Name As String
        Private m_BHType As BHTypes.BHType
        Private m_BHObject As BHObjects.BHObject

        ''
        '  Gets and sets value for Name parameter. Name defines the name of the busy hour.
        '
        ' @param Value Specifies value of Name parameter
        ' @return Value of Name parameter
        Public Property Name() As String
            Get
                Name = m_Name
            End Get

            Set(ByVal Value As String)
                m_Name = Value
            End Set

        End Property

        ''
        '  Gets and sets value for BHType parameter. BHType defines supported BH types.
        '
        ' @param Value Specifies value of BHType parameter
        ' @return Value of BHType parameter
        Public Property BHType() As BHTypes.BHType
            Get
                BHType = m_BHType
            End Get

            Set(ByVal Value As BHTypes.BHType)
                m_BHType = Value
            End Set

        End Property

        ''
        '  Gets and sets value for BHObject parameter. BHObject defines supported BH objects.
        '
        ' @param Value Specifies value of BHObject parameter
        ' @return Value of BHObject parameter
        Public Property BHObject() As BHObjects.BHObject
            Get
                BHObject = m_BHObject
            End Get

            Set(ByVal Value As BHObjects.BHObject)
                m_BHObject = Value
            End Set

        End Property

    End Class
End Class


